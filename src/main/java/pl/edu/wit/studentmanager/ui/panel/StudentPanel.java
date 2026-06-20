package pl.edu.wit.studentmanager.ui.panel;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.i18n.Translatable;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.service.AssignmentService;
import pl.edu.wit.studentmanager.service.GroupService;
import pl.edu.wit.studentmanager.service.StudentService;
import pl.edu.wit.studentmanager.ui.UiDialogs;
import pl.edu.wit.studentmanager.ui.dialog.AssignmentDialog;
import pl.edu.wit.studentmanager.ui.dialog.StudentDialog;
import pl.edu.wit.studentmanager.ui.table.StudentTableModel;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import javax.swing.SwingUtilities;

/**
 * Panel ewidencji studentów i przypisań do grup.
 */
public final class StudentPanel extends JPanel implements Translatable {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Serwis studentów. */
    private final StudentService studentService;

    /** Serwis grup. */
    private final GroupService groupService;

    /** Serwis przypisań. */
    private final AssignmentService assignmentService;

    /** Funkcja odświeżająca wszystkie panele. */
    private final Runnable refreshAllAction;

    /** Model tabeli studentów. */
    private final StudentTableModel tableModel;

    /** Tabela studentów. */
    private final JTable table;

    /** Przycisk dodawania. */
    private final JButton addButton = new JButton();

    /** Przycisk edycji. */
    private final JButton editButton = new JButton();

    /** Przycisk usuwania. */
    private final JButton deleteButton = new JButton();

    /** Przycisk przypisania. */
    private final JButton assignButton = new JButton();

    /** Przycisk usunięcia przypisania. */
    private final JButton unassignButton = new JButton();

    /**
     * Tworzy panel studentów.
     *
     * @param languageManager menedżer języka
     * @param studentService serwis studentów
     * @param groupService serwis grup
     * @param assignmentService serwis przypisań
     * @param refreshAllAction funkcja odświeżająca całą aplikację
     */
    public StudentPanel(
            LanguageManager languageManager,
            StudentService studentService,
            GroupService groupService,
            AssignmentService assignmentService,
            Runnable refreshAllAction) {
        this.languageManager = languageManager;
        this.studentService = studentService;
        this.groupService = groupService;
        this.assignmentService = assignmentService;
        this.refreshAllAction = refreshAllAction;
        tableModel = new StudentTableModel(studentService, assignmentService, languageManager);
        table = new JTable(tableModel);
        buildInterface();
        updateTexts();
    }

    /** Buduje interfejs panelu. */
    private void buildInterface() {
        setLayout(new BorderLayout(8, 8));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(deleteButton);
        buttons.add(assignButton);
        buttons.add(unassignButton);
        add(buttons, BorderLayout.SOUTH);

        addButton.addActionListener(event -> addStudent());
        editButton.addActionListener(event -> editStudent());
        deleteButton.addActionListener(event -> deleteStudent());
        assignButton.addActionListener(event -> assignStudent());
        unassignButton.addActionListener(event -> unassignStudent());
    }

    /** Otwiera formularz dodawania studenta. */
    private void addStudent() {
        StudentDialog dialog = new StudentDialog(
                owner(), languageManager, studentService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    /** Otwiera formularz edycji studenta. */
    private void editStudent() {
        Student student = selectedStudent();
        if (student == null) {
            return;
        }
        StudentDialog dialog = new StudentDialog(
                owner(), languageManager, studentService, student);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    /** Usuwa wybranego studenta po potwierdzeniu. */
    private void deleteStudent() {
        Student student = selectedStudent();
        if (student == null || !UiDialogs.confirm(
                this, languageManager, "message.confirm.delete.student")) {
            return;
        }
        studentService.deleteStudent(student.getId());
        refreshAllAction.run();
    }

    /** Przypisuje wybranego studenta do grupy. */
    private void assignStudent() {
        Student student = selectedStudent();
        if (student == null) {
            return;
        }
        if (groupService.getAllGroups().isEmpty()) {
            UiDialogs.showInformation(this, languageManager, "message.no.groups");
            return;
        }
        AssignmentDialog dialog = new AssignmentDialog(
                owner(), languageManager, assignmentService, groupService, student);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    /** Usuwa przypisanie wybranego studenta. */
    private void unassignStudent() {
        Student student = selectedStudent();
        if (student == null || !UiDialogs.confirm(
                this, languageManager, "message.confirm.unassign")) {
            return;
        }
        try {
            assignmentService.unassignStudent(student.getId());
            refreshAllAction.run();
        } catch (ValidationException exception) {
            UiDialogs.showValidationError(this, languageManager, exception);
        }
    }

    /**
     * Zwraca wybranego studenta albo pokazuje komunikat.
     *
     * @return student lub {@code null}
     */
    private Student selectedStudent() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            UiDialogs.showInformation(this, languageManager, "message.select.student");
            return null;
        }
        return tableModel.getStudentAt(table.convertRowIndexToModel(viewRow));
    }

    /**
     * Zwraca okno nadrzędne panelu.
     *
     * @return okno
     */
    private Window owner() {
        return SwingUtilities.getWindowAncestor(this);
    }

    /** Odświeża dane tabeli. */
    public void refreshData() {
        tableModel.refresh();
    }

    /** Aktualizuje teksty panelu. */
    @Override
    public void updateTexts() {
        addButton.setText(languageManager.get("button.add"));
        editButton.setText(languageManager.get("button.edit"));
        deleteButton.setText(languageManager.get("button.delete"));
        assignButton.setText(languageManager.get("button.assign"));
        unassignButton.setText(languageManager.get("button.unassign"));
        tableModel.refreshLanguage();
    }
}
