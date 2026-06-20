package pl.edu.wit.studentmanager.ui.panel;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.i18n.Translatable;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.StudentScore;
import pl.edu.wit.studentmanager.service.ScoreService;
import pl.edu.wit.studentmanager.service.SubjectService;
import pl.edu.wit.studentmanager.ui.UiDialogs;
import pl.edu.wit.studentmanager.ui.dialog.ScoreDialog;
import pl.edu.wit.studentmanager.ui.table.ScoreTableModel;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;

/**
 * Panel wprowadzania i edycji punktów.
 */
public final class ScorePanel extends JPanel implements Translatable {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Dane aplikacji. */
    private final AppData data;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Serwis wyników. */
    private final ScoreService scoreService;

    /** Serwis przedmiotów. */
    private final SubjectService subjectService;

    /** Funkcja odświeżająca. */
    private final Runnable refreshAllAction;

    /** Model tabeli. */
    private final ScoreTableModel tableModel;

    /** Tabela wyników. */
    private final JTable table;

    /** Przycisk dodawania wyniku. */
    private final JButton addButton = new JButton();

    /** Przycisk edycji wyniku. */
    private final JButton editButton = new JButton();

    /** Przycisk usuwania wyniku. */
    private final JButton deleteButton = new JButton();

    /**
     * Tworzy panel wyników.
     *
     * @param data dane aplikacji
     * @param languageManager menedżer języka
     * @param scoreService serwis wyników
     * @param subjectService serwis przedmiotów
     * @param refreshAllAction funkcja odświeżająca
     */
    public ScorePanel(
            AppData data,
            LanguageManager languageManager,
            ScoreService scoreService,
            SubjectService subjectService,
            Runnable refreshAllAction) {
        this.data = data;
        this.languageManager = languageManager;
        this.scoreService = scoreService;
        this.subjectService = subjectService;
        this.refreshAllAction = refreshAllAction;
        tableModel = new ScoreTableModel(data, scoreService, languageManager);
        table = new JTable(tableModel);
        buildInterface();
        updateTexts();
    }

    /** Buduje interfejs. */
    private void buildInterface() {
        setLayout(new BorderLayout(8, 8));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(deleteButton);
        add(buttons, BorderLayout.SOUTH);

        addButton.addActionListener(event -> addScore());
        editButton.addActionListener(event -> editScore());
        deleteButton.addActionListener(event -> deleteScore());
    }

    /** Otwiera dialog dodawania wyniku. */
    private void addScore() {
        if (data.getStudents().isEmpty()) {
            UiDialogs.showInformation(this, languageManager, "message.no.students");
            return;
        }
        if (data.getSubjects().isEmpty()) {
            UiDialogs.showInformation(this, languageManager, "message.no.subjects");
            return;
        }
        ScoreDialog dialog = new ScoreDialog(
                owner(), data, languageManager, scoreService, subjectService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    /** Otwiera dialog edycji wyniku. */
    private void editScore() {
        StudentScore score = selectedScore();
        if (score == null) {
            return;
        }
        ScoreDialog dialog = new ScoreDialog(
                owner(), data, languageManager, scoreService, subjectService, score);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    /** Usuwa wynik. */
    private void deleteScore() {
        StudentScore score = selectedScore();
        if (score == null || !UiDialogs.confirm(
                this, languageManager, "message.confirm.delete.score")) {
            return;
        }
        scoreService.deleteScore(score.getId());
        refreshAllAction.run();
    }

    /**
     * Zwraca wybrany wynik.
     *
     * @return wynik lub {@code null}
     */
    private StudentScore selectedScore() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            UiDialogs.showInformation(this, languageManager, "message.select.score");
            return null;
        }
        return tableModel.getScoreAt(table.convertRowIndexToModel(viewRow));
    }

    /**
     * Zwraca okno nadrzędne panelu.
     *
     * @return okno nadrzędne
     */
    private Window owner() {
        return SwingUtilities.getWindowAncestor(this);
    }

    /** Odświeża tabelę. */
    public void refreshData() {
        tableModel.refresh();
    }

    /** Aktualizuje teksty. */
    @Override
    public void updateTexts() {
        addButton.setText(languageManager.get("button.add"));
        editButton.setText(languageManager.get("button.edit"));
        deleteButton.setText(languageManager.get("button.delete"));
        tableModel.refreshLanguage();
    }
}
