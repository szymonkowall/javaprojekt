package pl.edu.wit.studentmanager.ui.table;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.StudentSearchResult;


public final class SearchResultTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private final LanguageManager languageManager;

    private List<StudentSearchResult> rows = List.of();


    public SearchResultTableModel(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }


    public void setRows(List<StudentSearchResult> rows) {
        this.rows = List.copyOf(rows);
        fireTableDataChanged();
    }

    public void refreshLanguage() {
        fireTableStructureChanged();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }


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

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
