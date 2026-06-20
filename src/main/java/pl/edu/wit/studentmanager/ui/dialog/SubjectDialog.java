package pl.edu.wit.studentmanager.ui.dialog;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.Subject;
import pl.edu.wit.studentmanager.service.SubjectService;
import pl.edu.wit.studentmanager.ui.UiDialogs;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Window;

/**
 * Modalny formularz dodawania lub edycji przedmiotu.
 */
public final class SubjectDialog extends JDialog {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Serwis przedmiotów. */
    private final SubjectService subjectService;

    /** Edytowany przedmiot lub {@code null}. */
    private final Subject subject;

    /** Pole nazwy. */
    private final JTextField nameField = new JTextField(24);

    /** Pole opisu. */
    private final JTextArea descriptionArea = new JTextArea(4, 24);

    /** Informacja o zapisaniu. */
    private boolean saved;

    /**
     * Tworzy dialog przedmiotu.
     *
     * @param owner okno nadrzędne
     * @param languageManager menedżer języka
     * @param subjectService serwis przedmiotów
     * @param subject edytowany przedmiot lub {@code null}
     */
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

    /**
     * Zwraca informację o zapisaniu formularza.
     *
     * @return {@code true}, gdy zapis się udał
     */
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

    /** Wypełnia formularz. */
    private void fillFields() {
        if (subject != null) {
            nameField.setText(subject.getName());
            descriptionArea.setText(subject.getDescription());
        }
    }

    /** Próbuje zapisać dane. */
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
