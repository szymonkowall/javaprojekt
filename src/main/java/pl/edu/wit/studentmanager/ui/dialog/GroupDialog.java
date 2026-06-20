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
import pl.edu.wit.studentmanager.model.StudentGroup;
import pl.edu.wit.studentmanager.service.GroupService;
import pl.edu.wit.studentmanager.ui.UiDialogs;


public final class GroupDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final LanguageManager languageManager;

    private final GroupService groupService;

    private final StudentGroup group;

    private final JTextField codeField = new JTextField(24);

    private final JTextField specializationField = new JTextField(24);

    private final JTextArea descriptionArea = new JTextArea(4, 24);

    private boolean saved;


    public GroupDialog(
            Window owner,
            LanguageManager languageManager,
            GroupService groupService,
            StudentGroup group) {
        super(owner, languageManager.get(group == null
                ? "dialog.group.add" : "dialog.group.edit"), ModalityType.APPLICATION_MODAL);
        this.languageManager = languageManager;
        this.groupService = groupService;
        this.group = group;
        buildInterface();
        fillFields();
        pack();
        setLocationRelativeTo(owner);
    }


    public boolean isSaved() {
        return saved;
    }

    private void buildInterface() {
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JPanel form = new JPanel(new GridBagLayout());
        DialogLayout.addRow(form, 0, new JLabel(languageManager.get("field.groupCode")), codeField);
        DialogLayout.addRow(form, 1, new JLabel(languageManager.get("field.specialization")), specializationField);
        DialogLayout.addRow(form, 2, new JLabel(languageManager.get("field.description")),
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
        if (group != null) {
            codeField.setText(group.getCode());
            specializationField.setText(group.getSpecialization());
            descriptionArea.setText(group.getDescription());
        }
    }

    private void save() {
        try {
            if (group == null) {
                groupService.addGroup(
                        codeField.getText(), specializationField.getText(), descriptionArea.getText());
            } else {
                groupService.updateGroup(
                        group.getId(), codeField.getText(),
                        specializationField.getText(), descriptionArea.getText());
            }
            saved = true;
            dispose();
        } catch (ValidationException exception) {
            UiDialogs.showValidationError(this, languageManager, exception);
        }
    }
}
