package pl.edu.wit.studentmanager.ui.panel;

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

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

/**
 * Panel zarządzania przedmiotami i kryteriami.
 */
public final class SubjectPanel extends JPanel implements Translatable {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Serwis przedmiotów. */
    private final SubjectService subjectService;

    /** Funkcja odświeżenia wszystkich paneli. */
    private final Runnable refreshAllAction;

    /** Model tabeli przedmiotów. */
    private final SubjectTableModel subjectModel;

    /** Model tabeli kryteriów. */
    private final CriterionTableModel criterionModel;

    /** Tabela przedmiotów. */
    private final JTable subjectTable;

    /** Tabela kryteriów. */
    private final JTable criterionTable;

    /** Przycisk dodawania przedmiotu. */
    private final JButton addSubjectButton = new JButton();

    /** Przycisk edycji przedmiotu. */
    private final JButton editSubjectButton = new JButton();

    /** Przycisk usuwania przedmiotu. */
    private final JButton deleteSubjectButton = new JButton();

    /** Przycisk dodawania kryterium. */
    private final JButton addCriterionButton = new JButton();

    /** Przycisk edycji kryterium. */
    private final JButton editCriterionButton = new JButton();

    /** Przycisk usuwania kryterium. */
    private final JButton deleteCriterionButton = new JButton();

    /**
     * Tworzy panel przedmiotów.
     *
     * @param languageManager menedżer języka
     * @param subjectService serwis przedmiotów
     * @param refreshAllAction funkcja odświeżająca
     */
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

    /** Buduje interfejs panelu. */
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

    /** Otwiera dialog dodawania przedmiotu. */
    private void addSubject() {
        SubjectDialog dialog = new SubjectDialog(owner(), languageManager, subjectService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    /** Otwiera dialog edycji przedmiotu. */
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

    /** Usuwa przedmiot. */
    private void deleteSubject() {
        Subject subject = selectedSubject(true);
        if (subject == null || !UiDialogs.confirm(
                this, languageManager, "message.confirm.delete.subject")) {
            return;
        }
        subjectService.deleteSubject(subject.getId());
        refreshAllAction.run();
    }

    /** Otwiera dialog dodawania kryterium. */
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

    /** Otwiera dialog edycji kryterium. */
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

    /** Usuwa kryterium. */
    private void deleteCriterion() {
        AssessmentCriterion criterion = selectedCriterion();
        if (criterion == null || !UiDialogs.confirm(
                this, languageManager, "message.confirm.delete.criterion")) {
            return;
        }
        subjectService.deleteCriterion(criterion.getId());
        refreshAllAction.run();
    }

    /**
     * Zwraca wybrany przedmiot.
     *
     * @param showMessage czy pokazać komunikat przy braku wyboru
     * @return przedmiot lub {@code null}
     */
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

    /**
     * Zwraca wybrane kryterium.
     *
     * @return kryterium lub {@code null}
     */
    private AssessmentCriterion selectedCriterion() {
        int viewRow = criterionTable.getSelectedRow();
        if (viewRow < 0) {
            UiDialogs.showInformation(this, languageManager, "message.select.criterion");
            return null;
        }
        return criterionModel.getCriterionAt(criterionTable.convertRowIndexToModel(viewRow));
    }

    /**
     * Zwraca okno nadrzędne panelu.
     *
     * @return okno nadrzędne
     */
    private Window owner() {
        return SwingUtilities.getWindowAncestor(this);
    }

    /** Odświeża obie tabele. */
    public void refreshData() {
        subjectModel.refresh();
        criterionModel.setSubjectId(null);
    }

    /** Aktualizuje teksty. */
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
