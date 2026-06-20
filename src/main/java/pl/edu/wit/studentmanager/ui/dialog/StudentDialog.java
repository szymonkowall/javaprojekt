package pl.edu.wit.studentmanager.ui.dialog;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.service.StudentService;
import pl.edu.wit.studentmanager.ui.UiDialogs;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Window;

/**
 * Modalny formularz dodawania lub edycji studenta.
 */
public final class StudentDialog extends JDialog {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Serwis studentów. */
    private final StudentService studentService;

    /** Edytowany student lub {@code null}. */
    private final Student student;

    /** Pole imienia. */
    private final JTextField firstNameField = new JTextField(24);

    /** Pole nazwiska. */
    private final JTextField lastNameField = new JTextField(24);

    /** Pole numeru albumu. */
    private final JTextField albumField = new JTextField(24);

    /** Informacja, czy dane zapisano. */
    private boolean saved;

    /**
     * Tworzy dialog studenta.
     *
     * @param owner okno nadrzędne
     * @param languageManager menedżer języka
     * @param studentService serwis studentów
     * @param student edytowany student lub {@code null} przy dodawaniu
     */
    public StudentDialog(
            Window owner,
            LanguageManager languageManager,
            StudentService studentService,
            Student student) {
        super(owner, languageManager.get(student == null
                ? "dialog.student.add" : "dialog.student.edit"), ModalityType.APPLICATION_MODAL);
        this.languageManager = languageManager;
        this.studentService = studentService;
        this.student = student;
        buildInterface();
        fillFields();
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    /**
     * Zwraca informację, czy operacja została zatwierdzona.
     *
     * @return {@code true}, gdy zapis się udał
     */
    public boolean isSaved() {
        return saved;
    }

    /** Buduje interfejs dialogu. */
    private void buildInterface() {
        JPanel form = new JPanel(new GridBagLayout());
        DialogLayout.addRow(form, 0, new JLabel(languageManager.get("field.firstName")), firstNameField);
        DialogLayout.addRow(form, 1, new JLabel(languageManager.get("field.lastName")), lastNameField);
        DialogLayout.addRow(form, 2, new JLabel(languageManager.get("field.albumNumber")), albumField);

        JButton saveButton = new JButton(languageManager.get("button.save"));
        JButton cancelButton = new JButton(languageManager.get("button.cancel"));
        saveButton.addActionListener(event -> save());
        cancelButton.addActionListener(event -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(saveButton);
        buttons.add(cancelButton);

        setLayout(new BorderLayout(8, 8));
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(saveButton);
    }

    /** Wypełnia pola danymi edytowanego studenta. */
    private void fillFields() {
        if (student != null) {
            firstNameField.setText(student.getFirstName());
            lastNameField.setText(student.getLastName());
            albumField.setText(student.getAlbumNumber());
        }
    }

    /** Próbuje zapisać dane formularza. */
    private void save() {
        try {
            if (student == null) {
                studentService.addStudent(
                        firstNameField.getText(), lastNameField.getText(), albumField.getText());
            } else {
                studentService.updateStudent(
                        student.getId(), firstNameField.getText(),
                        lastNameField.getText(), albumField.getText());
            }
            saved = true;
            dispose();
        } catch (ValidationException exception) {
            UiDialogs.showValidationError(this, languageManager, exception);
        }
    }
}
