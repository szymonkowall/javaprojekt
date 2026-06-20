package pl.edu.wit.studentmanager.ui.table;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.StudentGroup;
import pl.edu.wit.studentmanager.service.AssignmentService;
import pl.edu.wit.studentmanager.service.GroupService;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Model tabeli grup studenckich.
 */
public final class GroupTableModel extends AbstractTableModel {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Serwis grup. */
    private final GroupService groupService;

    /** Serwis przypisań. */
    private final AssignmentService assignmentService;

    /** Menedżer tłumaczeń. */
    private final LanguageManager languageManager;

    /** Aktualne wiersze. */
    private List<StudentGroup> rows = List.of();

    /**
     * Tworzy model tabeli grup.
     *
     * @param groupService serwis grup
     * @param assignmentService serwis przypisań
     * @param languageManager menedżer języka
     */
    public GroupTableModel(
            GroupService groupService,
            AssignmentService assignmentService,
            LanguageManager languageManager) {
        this.groupService = groupService;
        this.assignmentService = assignmentService;
        this.languageManager = languageManager;
        refresh();
    }

    /** Odświeża dane. */
    public void refresh() {
        rows = groupService.getAllGroups();
        fireTableDataChanged();
    }

    /** Odświeża nagłówki. */
    public void refreshLanguage() {
        fireTableStructureChanged();
    }

    /**
     * Zwraca grupę z wiersza.
     *
     * @param rowIndex indeks wiersza
     * @return grupa
     */
    public StudentGroup getGroupAt(int rowIndex) {
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
        return 4;
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
            case 0 -> languageManager.get("column.groupCode");
            case 1 -> languageManager.get("column.specialization");
            case 2 -> languageManager.get("column.description");
            case 3 -> languageManager.get("column.studentCount");
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
        StudentGroup group = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> group.getCode();
            case 1 -> group.getSpecialization();
            case 2 -> group.getDescription();
            case 3 -> assignmentService.countStudentsInGroup(group.getId());
            default -> "";
        };
    }

    /** @return zawsze {@code false} */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
