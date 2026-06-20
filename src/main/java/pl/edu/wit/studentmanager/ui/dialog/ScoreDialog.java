package pl.edu.wit.studentmanager.ui.dialog;

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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.util.Comparator;

/**
 * Dialog dodawania lub edycji punktów studenta.
 */
public final class ScoreDialog extends JDialog {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Dane aplikacji. */
    private final AppData data;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Serwis punktów. */
    private final ScoreService scoreService;

    /** Serwis przedmiotów. */
    private final SubjectService subjectService;

    /** Edytowany wynik lub {@code null}. */
    private final StudentScore score;

    /** Lista studentów. */
    private final JComboBox<Student> studentCombo = new JComboBox<>();

    /** Lista przedmiotów. */
    private final JComboBox<Subject> subjectCombo = new JComboBox<>();

    /** Lista kryteriów. */
    private final JComboBox<AssessmentCriterion> criterionCombo = new JComboBox<>();

    /** Pole punktów. */
    private final JTextField pointsField = new JTextField(12);

    /** Informacja o zapisaniu. */
    private boolean saved;

    /**
     * Tworzy dialog punktów.
     *
     * @param owner okno nadrzędne
     * @param data dane aplikacji
     * @param languageManager menedżer języka
     * @param scoreService serwis punktów
     * @param subjectService serwis przedmiotów
     * @param score edytowany wynik lub {@code null}
     */
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

    /**
     * Zwraca informację o zapisaniu formularza.
     *
     * @return {@code true}, gdy zapis się udał
     */
    public boolean isSaved() {
        return saved;
    }

    /** Wypełnia listy studentów i przedmiotów. */
    private void fillCombos() {
        data.getStudents().stream()
                .sorted(Comparator.comparing(Student::getLastName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Student::getFirstName, String.CASE_INSENSITIVE_ORDER))
                .forEach(studentCombo::addItem);
        subjectService.getAllSubjects().forEach(subjectCombo::addItem);
        subjectCombo.addActionListener(event -> refreshCriteria());
        refreshCriteria();
    }

    /** Buduje interfejs dialogu. */
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

    /** Odświeża kryteria po zmianie przedmiotu. */
    private void refreshCriteria() {
        criterionCombo.removeAllItems();
        Subject subject = (Subject) subjectCombo.getSelectedItem();
        if (subject != null) {
            subjectService.getCriteriaForSubject(subject.getId()).forEach(criterionCombo::addItem);
        }
    }

    /** Ustawia wartości podczas edycji istniejącego wyniku. */
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

    /** Próbuje zapisać wynik. */
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
