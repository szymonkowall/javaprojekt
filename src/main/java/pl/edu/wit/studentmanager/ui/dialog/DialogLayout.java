package pl.edu.wit.studentmanager.ui.dialog;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Pomocnicze metody układające pola formularzy dialogowych.
 */
final class DialogLayout {

    /** Prywatny konstruktor klasy narzędziowej. */
    private DialogLayout() {
    }

    /**
     * Dodaje etykietę i pole do kolejnego wiersza formularza.
     *
     * @param panel panel z układem GridBagLayout
     * @param row numer wiersza
     * @param label etykieta
     * @param component pole formularza
     */
    static void addRow(JPanel panel, int row, JLabel label, JComponent component) {
        GridBagConstraints left = new GridBagConstraints();
        left.gridx = 0;
        left.gridy = row;
        left.anchor = GridBagConstraints.LINE_END;
        left.insets = new Insets(5, 5, 5, 8);
        panel.add(label, left);

        GridBagConstraints right = new GridBagConstraints();
        right.gridx = 1;
        right.gridy = row;
        right.weightx = 1.0;
        right.fill = GridBagConstraints.HORIZONTAL;
        right.insets = new Insets(5, 5, 5, 5);
        panel.add(component, right);
    }
}
