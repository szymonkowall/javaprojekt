package pl.edu.wit.studentmanager.ui.dialog;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.service.SubjectService;
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
import java.util.UUID;

/**
 * Modalny formularz kryterium oceniania.
 */
public final class CriterionDialog extends JDialog {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Serwis przedmiotów. */
    private final SubjectService subjectService;

    /** Identyfikator przedmiotu. */
    private final UUID subjectId;

    /** Edytowane kryterium lub {@code null}. */
    private final AssessmentCriterion criterion;

    /** Pole nazwy. */
    private final JTextField nameField = new JTextField(24);

    /** Pole maksimum punktów. */
    private final JTextField maximumField = new JTextField(12);

    /** Informacja o zapisaniu. */
    private boolean saved;

    /**
     * Tworzy dialog kryterium.
     *
     * @param owner okno nadrzędne
     * @param languageManager menedżer języka
     * @param subjectService serwis przedmiotów
     * @param subjectId identyfikator przedmiotu
     * @param criterion edytowane kryterium lub {@code null}
     */
    public CriterionDialog(
            Window owner,
            LanguageManager languageManager,
            SubjectService subjectService,
            UUID subjectId,
            AssessmentCriterion criterion) {
        super(owner, languageManager.get(criterion == null
                ? "dialog.criterion.add" : "dialog.criterion.edit"), ModalityType.APPLICATION_MODAL);
        this.languageManager = languageManager;
        this.subjectService = subjectService;
        this.subjectId = subjectId;
        this.criterion = criterion;
        buildInterface();
        fillFields();
        pack();
        setResizable(false);
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
        JPanel form = new JPanel(new GridBagLayout());
        DialogLayout.addRow(form, 0, new JLabel(languageManager.get("field.criterionName")), nameField);
        DialogLayout.addRow(form, 1, new JLabel(languageManager.get("field.maximumPoints")), maximumField);

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
        if (criterion != null) {
            nameField.setText(criterion.getName());
            maximumField.setText(Double.toString(criterion.getMaximumPoints()));
        }
    }

    /** Próbuje zapisać dane. */
    private void save() {
        final double maximum;
        try {
            maximum = Double.parseDouble(maximumField.getText().trim().replace(',', '.'));
        } catch (NumberFormatException exception) {
            UiDialogs.showInformation(this, languageManager, "message.invalid.number");
            return;
        }

        try {
            if (criterion == null) {
                subjectService.addCriterion(subjectId, nameField.getText(), maximum);
            } else {
                subjectService.updateCriterion(criterion.getId(), nameField.getText(), maximum);
            }
            saved = true;
            dispose();
        } catch (ValidationException exception) {
            UiDialogs.showValidationError(this, languageManager, exception);
        }
    }
}
