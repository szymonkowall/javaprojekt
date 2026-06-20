package pl.edu.wit.studentmanager.ui.dialog;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.model.StudentGroup;
import pl.edu.wit.studentmanager.service.AssignmentService;
import pl.edu.wit.studentmanager.service.GroupService;
import pl.edu.wit.studentmanager.ui.UiDialogs;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Window;

/**
 * Dialog przypisania wybranego studenta do grupy.
 */
public final class AssignmentDialog extends JDialog {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Serwis przypisań. */
    private final AssignmentService assignmentService;

    /** Wybrany student. */
    private final Student student;

    /** Lista grup. */
    private final JComboBox<StudentGroup> groupCombo = new JComboBox<>();

    /** Informacja o zapisaniu. */
    private boolean saved;

    /**
     * Tworzy dialog przypisania.
     *
     * @param owner okno nadrzędne
     * @param languageManager menedżer języka
     * @param assignmentService serwis przypisań
     * @param groupService serwis grup
     * @param student przypisywany student
     */
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

    /**
     * Zwraca informację o utworzeniu przypisania.
     *
     * @return {@code true}, gdy przypisanie utworzono
     */
    public boolean isSaved() {
        return saved;
    }

    /** Buduje interfejs dialogu. */
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

    /** Tworzy przypisanie. */
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

    /**
     * Niedycytowalne pole tekstowe używane jako komponent formularza.
     */
    private static final class JTextFieldLabel extends javax.swing.JTextField {

        /** Numer wersji klasy. */
        private static final long serialVersionUID = 1L;

        /**
         * Tworzy pole z tekstem.
         *
         * @param text tekst
         */
        private JTextFieldLabel(String text) {
            super(text, 24);
            setEditable(false);
        }
    }
}
