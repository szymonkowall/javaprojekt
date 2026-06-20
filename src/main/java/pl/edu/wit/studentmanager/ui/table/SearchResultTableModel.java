package pl.edu.wit.studentmanager.ui.table;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.StudentSearchResult;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Model tabeli wyników wyszukiwania.
 */
public final class SearchResultTableModel extends AbstractTableModel {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Aktualne wyniki. */
    private List<StudentSearchResult> rows = List.of();

    /**
     * Tworzy model wyników wyszukiwania.
     *
     * @param languageManager menedżer języka
     */
    public SearchResultTableModel(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    /**
     * Ustawia nowe wyniki.
     *
     * @param rows nowe wiersze
     */
    public void setRows(List<StudentSearchResult> rows) {
        this.rows = List.copyOf(rows);
        fireTableDataChanged();
    }

    /** Odświeża nagłówki. */
    public void refreshLanguage() {
        fireTableStructureChanged();
    }

    /** @return liczba wierszy */
    @Override
    public int getRowCount() {
        return rows.size();
    }

    /** @return liczba kolumn */
    @Override
    public int getColumnCount() {
        return 7;
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
            case 0 -> languageManager.get("column.search.student");
            case 1 -> languageManager.get("column.search.album");
            case 2 -> languageManager.get("column.search.group");
            case 3 -> languageManager.get("column.search.subject");
            case 4 -> languageManager.get("column.search.criterion");
            case 5 -> languageManager.get("column.search.points");
            case 6 -> languageManager.get("column.search.maximum");
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
        StudentSearchResult result = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> result.getStudentName();
            case 1 -> result.getAlbumNumber();
            case 2 -> result.getGroupCode();
            case 3 -> result.getSubjectName();
            case 4 -> result.getCriterionName();
            case 5 -> result.getPoints() == null ? "" : result.getPoints();
            case 6 -> result.getMaximumPoints() == null ? "" : result.getMaximumPoints();
            default -> "";
        };
    }

    /** @return zawsze {@code false} */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
