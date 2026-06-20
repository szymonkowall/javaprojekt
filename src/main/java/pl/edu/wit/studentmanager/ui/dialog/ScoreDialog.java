package pl.edu.wit.studentmanager.ui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.model.StudentScore;
import pl.edu.wit.studentmanager.model.Subject;
import pl.edu.wit.studentmanager.service.ScoreService;
import pl.edu.wit.studentmanager.service.SubjectService;
import pl.edu.wit.studentmanager.ui.UiDialogs;


public final class ScoreDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final AppData data;

    private final LanguageManager languageManager;

    private final ScoreService scoreService;

    private final SubjectService subjectService;

    private final StudentScore score;

    private final JComboBox<Student> studentCombo = new JComboBox<>();

    private final JComboBox<Subject> subjectCombo = new JComboBox<>();

    private final JComboBox<AssessmentCriterion> criterionCombo = new JComboBox<>();

    private final JTextField pointsField = new JTextField(12);

    private boolean saved;


    public ScoreDialog(
            Window owner,
            AppData data,
            LanguageManager languageManager,
            ScoreService scoreService,
            SubjectService subjectService,
            StudentScore score) {
        super(owner, languageManager.get(score == null
                ? "dialog.score.add" : "dialog.score.edit"), ModalityType.APPLICATION_MODAL);
        this.data = data;
        this.languageManager = languageManager;
        this.scoreService = scoreService;
        this.subjectService = subjectService;
        this.score = score;
        fillCombos();
        buildInterface();
        fillExistingScore();
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }


    public boolean isSaved() {
        return saved;
    }

    private void fillCombos() {
        data.getStudents().stream()
                .sorted(Comparator.comparing(Student::getLastName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Student::getFirstName, String.CASE_INSENSITIVE_ORDER))
                .forEach(studentCombo::addItem);
        subjectService.getAllSubjects().forEach(subjectCombo::addItem);
        subjectCombo.addActionListener(event -> refreshCriteria());
        refreshCriteria();
    }

    private void buildInterface() {
        JPanel form = new JPanel(new GridBagLayout());
        DialogLayout.addRow(form, 0, new JLabel(languageManager.get("label.student")), studentCombo);
        DialogLayout.addRow(form, 1, new JLabel(languageManager.get("label.subject")), subjectCombo);
        DialogLayout.addRow(form, 2, new JLabel(languageManager.get("label.criterion")), criterionCombo);
        DialogLayout.addRow(form, 3, new JLabel(languageManager.get("field.points")), pointsField);

        JButton saveButton = new JButton(languageManager.get("button.save"));
        JButton cancelButton = new JButton(languageManager.get("button.cancel"));
        saveButton.addActionListener(event -> save());
        cancelButton.addActionListener(event -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(saveButton);
        buttons.add(cancelButton);

        setLayout(new BorderLayout(8, 8));
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(saveButton);
    }

    private void refreshCriteria() {
        criterionCombo.removeAllItems();
        Subject subject = (Subject) subjectCombo.getSelectedItem();
        if (subject != null) {
            subjectService.getCriteriaForSubject(subject.getId()).forEach(criterionCombo::addItem);
        }
    }

    private void fillExistingScore() {
        if (score == null) {
            return;
        }
        Student student = data.getStudents().stream()
                .filter(item -> item.getId().equals(score.getStudentId()))
                .findFirst().orElse(null);
        AssessmentCriterion criterion = data.getCriteria().stream()
                .filter(item -> item.getId().equals(score.getCriterionId()))
                .findFirst().orElse(null);
        Subject subject = criterion == null ? null : data.getSubjects().stream()
                .filter(item -> item.getId().equals(criterion.getSubjectId()))
                .findFirst().orElse(null);

        studentCombo.setSelectedItem(student);
        subjectCombo.setSelectedItem(subject);
        refreshCriteria();
        criterionCombo.setSelectedItem(criterion);
        pointsField.setText(Double.toString(score.getPoints()));
        studentCombo.setEnabled(false);
        subjectCombo.setEnabled(false);
        criterionCombo.setEnabled(false);
    }

    private void save() {
        final double points;
        try {
            points = Double.parseDouble(pointsField.getText().trim().replace(',', '.'));
        } catch (NumberFormatException exception) {
            UiDialogs.showInformation(this, languageManager, "message.invalid.number");
            return;
        }

        try {
            if (score == null) {
                Student student = (Student) studentCombo.getSelectedItem();
                AssessmentCriterion criterion = (AssessmentCriterion) criterionCombo.getSelectedItem();
                if (student == null) {
                    UiDialogs.showInformation(this, languageManager, "message.no.students");
                    return;
                }
                if (criterion == null) {
                    UiDialogs.showInformation(this, languageManager, "message.no.criteria");
                    return;
                }
                scoreService.addScore(student.getId(), criterion.getId(), points);
            } else {
                scoreService.updateScore(score.getId(), points);
            }
            saved = true;
            dispose();
        } catch (ValidationException exception) {
            UiDialogs.showValidationError(this, languageManager, exception);
        }
    }
}
