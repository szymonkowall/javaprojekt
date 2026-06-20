package pl.edu.wit.studentmanager.ui.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.i18n.Translatable;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.model.Subject;
import pl.edu.wit.studentmanager.service.SubjectService;
import pl.edu.wit.studentmanager.ui.UiDialogs;
import pl.edu.wit.studentmanager.ui.dialog.CriterionDialog;
import pl.edu.wit.studentmanager.ui.dialog.SubjectDialog;
import pl.edu.wit.studentmanager.ui.table.CriterionTableModel;
import pl.edu.wit.studentmanager.ui.table.SubjectTableModel;


public final class SubjectPanel extends JPanel implements Translatable {

    private static final long serialVersionUID = 1L;

    private final LanguageManager languageManager;

    private final SubjectService subjectService;

    private final Runnable refreshAllAction;

    private final SubjectTableModel subjectModel;

    private final CriterionTableModel criterionModel;

    private final JTable subjectTable;

    private final JTable criterionTable;

    private final JButton addSubjectButton = new JButton();

    private final JButton editSubjectButton = new JButton();

    private final JButton deleteSubjectButton = new JButton();

    private final JButton addCriterionButton = new JButton();

    private final JButton editCriterionButton = new JButton();

    private final JButton deleteCriterionButton = new JButton();


    public SubjectPanel(
            LanguageManager languageManager,
            SubjectService subjectService,
            Runnable refreshAllAction) {
        this.languageManager = languageManager;
        this.subjectService = subjectService;
        this.refreshAllAction = refreshAllAction;
        subjectModel = new SubjectTableModel(subjectService, languageManager);
        criterionModel = new CriterionTableModel(subjectService, languageManager);
        subjectTable = new JTable(subjectModel);
        criterionTable = new JTable(criterionModel);
        buildInterface();
        updateTexts();
    }

    private void buildInterface() {
        setLayout(new BorderLayout(8, 8));
        subjectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        criterionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subjectTable.setAutoCreateRowSorter(true);
        criterionTable.setAutoCreateRowSorter(true);
        subjectTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                Subject subject = selectedSubject(false);
                criterionModel.setSubjectId(subject == null ? null : subject.getId());
            }
        });

        JPanel subjectButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        subjectButtons.add(addSubjectButton);
        subjectButtons.add(editSubjectButton);
        subjectButtons.add(deleteSubjectButton);
        JPanel subjectSide = new JPanel(new BorderLayout());
        subjectSide.add(new JScrollPane(subjectTable), BorderLayout.CENTER);
        subjectSide.add(subjectButtons, BorderLayout.SOUTH);

        JPanel criterionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        criterionButtons.add(addCriterionButton);
        criterionButtons.add(editCriterionButton);
        criterionButtons.add(deleteCriterionButton);
        JPanel criterionSide = new JPanel(new BorderLayout());
        criterionSide.add(new JScrollPane(criterionTable), BorderLayout.CENTER);
        criterionSide.add(criterionButtons, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, subjectSide, criterionSide);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        addSubjectButton.addActionListener(event -> addSubject());
        editSubjectButton.addActionListener(event -> editSubject());
        deleteSubjectButton.addActionListener(event -> deleteSubject());
        addCriterionButton.addActionListener(event -> addCriterion());
        editCriterionButton.addActionListener(event -> editCriterion());
        deleteCriterionButton.addActionListener(event -> deleteCriterion());
    }

    private void addSubject() {
        SubjectDialog dialog = new SubjectDialog(owner(), languageManager, subjectService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    private void editSubject() {
        Subject subject = selectedSubject(true);
        if (subject == null) {
            return;
        }
        SubjectDialog dialog = new SubjectDialog(owner(), languageManager, subjectService, subject);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    private void deleteSubject() {
        Subject subject = selectedSubject(true);
        if (subject == null || !UiDialogs.confirm(
                this, languageManager, "message.confirm.delete.subject")) {
            return;
        }
        subjectService.deleteSubject(subject.getId());
        refreshAllAction.run();
    }

    private void addCriterion() {
        Subject subject = selectedSubject(true);
        if (subject == null) {
            return;
        }
        CriterionDialog dialog = new CriterionDialog(
                owner(), languageManager, subjectService, subject.getId(), null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    private void editCriterion() {
        AssessmentCriterion criterion = selectedCriterion();
        if (criterion == null) {
            return;
        }
        CriterionDialog dialog = new CriterionDialog(
                owner(), languageManager, subjectService,
                criterion.getSubjectId(), criterion);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    private void deleteCriterion() {
        AssessmentCriterion criterion = selectedCriterion();
        if (criterion == null || !UiDialogs.confirm(
                this, languageManager, "message.confirm.delete.criterion")) {
            return;
        }
        subjectService.deleteCriterion(criterion.getId());
        refreshAllAction.run();
    }

    private Subject selectedSubject(boolean showMessage) {
        int viewRow = subjectTable.getSelectedRow();
        if (viewRow < 0) {
            if (showMessage) {
                UiDialogs.showInformation(this, languageManager, "message.select.subject");
            }
            return null;
        }
        return subjectModel.getSubjectAt(subjectTable.convertRowIndexToModel(viewRow));
    }



    private AssessmentCriterion selectedCriterion() {
        int viewRow = criterionTable.getSelectedRow();
        if (viewRow < 0) {
            UiDialogs.showInformation(this, languageManager, "message.select.criterion");
            return null;
        }
        return criterionModel.getCriterionAt(criterionTable.convertRowIndexToModel(viewRow));
    }


    private Window owner() {
        return SwingUtilities.getWindowAncestor(this);
    }

    public void refreshData() {
        subjectModel.refresh();
        criterionModel.setSubjectId(null);
    }

    @Override
    public void updateTexts() {
        addSubjectButton.setText(languageManager.get("button.addSubject"));
        editSubjectButton.setText(languageManager.get("button.editSubject"));
        deleteSubjectButton.setText(languageManager.get("button.deleteSubject"));
        addCriterionButton.setText(languageManager.get("button.addCriterion"));
        editCriterionButton.setText(languageManager.get("button.editCriterion"));
        deleteCriterionButton.setText(languageManager.get("button.deleteCriterion"));
        subjectModel.refreshLanguage();
        criterionModel.refreshLanguage();
    }
}
