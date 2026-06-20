package pl.edu.wit.studentmanager.ui.table;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.service.AssignmentService;
import pl.edu.wit.studentmanager.service.StudentService;


public final class StudentTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private final StudentService studentService;

    private final AssignmentService assignmentService;

    private final LanguageManager languageManager;

    private List<Student> rows = List.of();


    public StudentTableModel(
            StudentService studentService,
            AssignmentService assignmentService,
            LanguageManager languageManager) {
        this.studentService = studentService;
        this.assignmentService = assignmentService;
        this.languageManager = languageManager;
        refresh();
    }


    public void refresh() {
        rows = studentService.getAllStudents();
        fireTableDataChanged();
    }


    public void refreshLanguage() {
        fireTableStructureChanged();
    }


    public Student getStudentAt(int rowIndex) {
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
            case 0 -> languageManager.get("column.firstName");
            case 1 -> languageManager.get("column.lastName");
            case 2 -> languageManager.get("column.albumNumber");
            case 3 -> languageManager.get("column.group");
            default -> "";
        };
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student student = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> student.getFirstName();
            case 1 -> student.getLastName();
            case 2 -> student.getAlbumNumber();
            case 3 -> assignmentService.findGroupForStudent(student.getId())
                    .map(group -> group.getCode())
                    .orElse("");
            default -> "";
        };
    }


    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
