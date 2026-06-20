package pl.edu.wit.studentmanager.ui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.service.StudentService;
import pl.edu.wit.studentmanager.ui.UiDialogs;


public final class StudentDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final LanguageManager languageManager;

    private final StudentService studentService;

    private final Student student;

    private final JTextField firstNameField = new JTextField(24);

    private final JTextField lastNameField = new JTextField(24);

    private final JTextField albumField = new JTextField(24);

    private boolean saved;


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


    public boolean isSaved() {
        return saved;
    }

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

    private void fillFields() {
        if (student != null) {
            firstNameField.setText(student.getFirstName());
            lastNameField.setText(student.getLastName());
            albumField.setText(student.getAlbumNumber());
        }
    }

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
