package pl.edu.wit.studentmanager.ui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.Subject;
import pl.edu.wit.studentmanager.service.SubjectService;
import pl.edu.wit.studentmanager.ui.UiDialogs;


public final class SubjectDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final LanguageManager languageManager;

    private final SubjectService subjectService;

    private final Subject subject;

    private final JTextField nameField = new JTextField(24);

    private final JTextArea descriptionArea = new JTextArea(4, 24);

    private boolean saved;


    public SubjectDialog(
            Window owner,
            LanguageManager languageManager,
            SubjectService subjectService,
            Subject subject) {
        super(owner, languageManager.get(subject == null
                ? "dialog.subject.add" : "dialog.subject.edit"), ModalityType.APPLICATION_MODAL);
        this.languageManager = languageManager;
        this.subjectService = subjectService;
        this.subject = subject;
        buildInterface();
        fillFields();
        pack();
        setLocationRelativeTo(owner);
    }


    public boolean isSaved() {
        return saved;
    }

    /** Buduje interfejs dialogu. */
    private void buildInterface() {
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JPanel form = new JPanel(new GridBagLayout());
        DialogLayout.addRow(form, 0, new JLabel(languageManager.get("field.subjectName")), nameField);
        DialogLayout.addRow(form, 1, new JLabel(languageManager.get("field.description")),
                new JScrollPane(descriptionArea));

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
        if (subject != null) {
            nameField.setText(subject.getName());
            descriptionArea.setText(subject.getDescription());
        }
    }

    private void save() {
        try {
            if (subject == null) {
                subjectService.addSubject(nameField.getText(), descriptionArea.getText());
            } else {
                subjectService.updateSubject(subject.getId(), nameField.getText(), descriptionArea.getText());
            }
            saved = true;
            dispose();
        } catch (ValidationException exception) {
            UiDialogs.showValidationError(this, languageManager, exception);
        }
    }
}
