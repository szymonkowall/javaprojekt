package pl.edu.wit.studentmanager.ui.table;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.StudentGroup;
import pl.edu.wit.studentmanager.service.AssignmentService;
import pl.edu.wit.studentmanager.service.GroupService;


public final class GroupTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private final GroupService groupService;

    private final AssignmentService assignmentService;

    private final LanguageManager languageManager;

    private List<StudentGroup> rows = List.of();


    public GroupTableModel(
            GroupService groupService,
            AssignmentService assignmentService,
            LanguageManager languageManager) {
        this.groupService = groupService;
        this.assignmentService = assignmentService;
        this.languageManager = languageManager;
        refresh();
    }

    public void refresh() {
        rows = groupService.getAllGroups();
        fireTableDataChanged();
    }

    public void refreshLanguage() {
        fireTableStructureChanged();
    }


    public StudentGroup getGroupAt(int rowIndex) {
        return rows.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }


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

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
