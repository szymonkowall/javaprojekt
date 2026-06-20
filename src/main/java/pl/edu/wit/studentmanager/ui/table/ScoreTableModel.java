package pl.edu.wit.studentmanager.ui.table;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.model.StudentScore;
import pl.edu.wit.studentmanager.model.Subject;
import pl.edu.wit.studentmanager.service.ScoreService;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Model tabeli punktów studentów.
 */
public final class ScoreTableModel extends AbstractTableModel {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Wspólne dane aplikacji. */
    private final AppData data;

    /** Serwis punktów. */
    private final ScoreService scoreService;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Aktualne wiersze. */
    private List<StudentScore> rows = List.of();

    /**
     * Tworzy model tabeli wyników.
     *
     * @param data dane aplikacji
     * @param scoreService serwis wyników
     * @param languageManager menedżer języka
     */
    public ScoreTableModel(
            AppData data,
            ScoreService scoreService,
            LanguageManager languageManager) {
        this.data = data;
        this.scoreService = scoreService;
        this.languageManager = languageManager;
        refresh();
    }

    /** Odświeża dane. */
    public void refresh() {
        rows = scoreService.getAllScores();
        fireTableDataChanged();
    }

    /** Odświeża nagłówki. */
    public void refreshLanguage() {
        fireTableStructureChanged();
    }

    /**
     * Zwraca wynik z wiersza.
     *
     * @param rowIndex indeks wiersza
     * @return wynik
     */
    public StudentScore getScoreAt(int rowIndex) {
        return rows.get(rowIndex);
    }

    /** @return liczba wierszy */
    @Override
    public int getRowCount() {
        return rows.size();
    }

    /** @return liczba kolumn */
    @Override
    public int getColumnCount() {
        return 5;
    }

    /**
     * Zwraca nazwę kolumny.
     *
     * @param column indeks kolumny
     * @return nazwa
     */
    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> languageManager.get("column.student");
            case 1 -> languageManager.get("column.subject");
            case 2 -> languageManager.get("column.criterion");
            case 3 -> languageManager.get("column.points");
            case 4 -> languageManager.get("column.maximumPoints");
            default -> "";
        };
    }

    /**
     * Zwraca wartość komórki.
     *
     * @param rowIndex indeks wiersza
     * @param columnIndex indeks kolumny
     * @return wartość
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        StudentScore score = rows.get(rowIndex);
        Student student = data.getStudents().stream()
                .filter(item -> item.getId().equals(score.getStudentId()))
                .findFirst().orElse(null);
        AssessmentCriterion criterion = data.getCriteria().stream()
                .filter(item -> item.getId().equals(score.getCriterionId()))
                .findFirst().orElse(null);
        Subject subject = criterion == null ? null : data.getSubjects().stream()
                .filter(item -> item.getId().equals(criterion.getSubjectId()))
                .findFirst().orElse(null);

        return switch (columnIndex) {
            case 0 -> student == null ? "" : student.toString();
            case 1 -> subject == null ? "" : subject.getName();
            case 2 -> criterion == null ? "" : criterion.getName();
            case 3 -> score.getPoints();
            case 4 -> criterion == null ? "" : criterion.getMaximumPoints();
            default -> "";
        };
    }

    /** @return zawsze {@code false} */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
