package pl.edu.wit.studentmanager.ui.table;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.service.AssignmentService;
import pl.edu.wit.studentmanager.service.StudentService;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Model tabeli studentów.
 */
public final class StudentTableModel extends AbstractTableModel {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Serwis studentów. */
    private final StudentService studentService;

    /** Serwis przypisań do grup. */
    private final AssignmentService assignmentService;

    /** Menedżer tłumaczeń. */
    private final LanguageManager languageManager;

    /** Aktualne wiersze tabeli. */
    private List<Student> rows = List.of();

    /**
     * Tworzy model tabeli.
     *
     * @param studentService serwis studentów
     * @param assignmentService serwis przypisań
     * @param languageManager menedżer języka
     */
    public StudentTableModel(
            StudentService studentService,
            AssignmentService assignmentService,
            LanguageManager languageManager) {
        this.studentService = studentService;
        this.assignmentService = assignmentService;
        this.languageManager = languageManager;
        refresh();
    }

    /**
     * Odświeża dane tabeli.
     */
    public void refresh() {
        rows = studentService.getAllStudents();
        fireTableDataChanged();
    }

    /**
     * Odświeża nagłówki po zmianie języka.
     */
    public void refreshLanguage() {
        fireTableStructureChanged();
    }

    /**
     * Zwraca studenta z wybranego wiersza.
     *
     * @param rowIndex indeks wiersza
     * @return student
     */
    public Student getStudentAt(int rowIndex) {
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
     * @return przetłumaczona nazwa
     */
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

    /**
     * Zwraca wartość komórki.
     *
     * @param rowIndex indeks wiersza
     * @param columnIndex indeks kolumny
     * @return wartość komórki
     */
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

    /**
     * Wyłącza bezpośrednią edycję komórek.
     *
     * @param rowIndex indeks wiersza
     * @param columnIndex indeks kolumny
     * @return zawsze {@code false}
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
