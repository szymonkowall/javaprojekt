package pl.edu.wit.studentmanager.ui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.model.StudentGroup;
import pl.edu.wit.studentmanager.service.AssignmentService;
import pl.edu.wit.studentmanager.service.GroupService;
import pl.edu.wit.studentmanager.ui.UiDialogs;


public final class AssignmentDialog extends JDialog {


    private static final long serialVersionUID = 1L;


    private final LanguageManager languageManager;


    private final AssignmentService assignmentService;


    private final Student student;

    private final JComboBox<StudentGroup> groupCombo = new JComboBox<>();


    private boolean saved;


    public AssignmentDialog(
            Window owner,
            LanguageManager languageManager,
            AssignmentService assignmentService,
            GroupService groupService,
            Student student) {
        super(owner, languageManager.get("dialog.assignment"), ModalityType.APPLICATION_MODAL);
        this.languageManager = languageManager;
        this.assignmentService = assignmentService;
        this.student = student;
        groupService.getAllGroups().forEach(groupCombo::addItem);
        buildInterface();
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public boolean isSaved() {
        return saved;
    }


    private void buildInterface() {
        JPanel form = new JPanel(new GridBagLayout());
        JTextFieldLabel studentLabel = new JTextFieldLabel(student.toString());
        DialogLayout.addRow(form, 0, new JLabel(languageManager.get("label.student")), studentLabel);
        DialogLayout.addRow(form, 1, new JLabel(languageManager.get("label.group")), groupCombo);

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


    private void save() {
        StudentGroup group = (StudentGroup) groupCombo.getSelectedItem();
        if (group == null) {
            UiDialogs.showInformation(this, languageManager, "message.no.groups");
            return;
        }
        try {
            assignmentService.assignStudent(student.getId(), group.getId());
            saved = true;
            dispose();
        } catch (ValidationException exception) {
            UiDialogs.showValidationError(this, languageManager, exception);
        }
    }

    private static final class JTextFieldLabel extends javax.swing.JTextField {


        private static final long serialVersionUID = 1L;


        private JTextFieldLabel(String text) {
            super(text, 24);
            setEditable(false);
        }
    }
}
