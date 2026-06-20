package pl.edu.wit.studentmanager.ui;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.i18n.LanguageManager;

import javax.swing.JOptionPane;
import java.awt.Component;

/**
 * Udostępnia wspólne, przetłumaczone okna komunikatów Swing.
 */
public final class UiDialogs {

    /**
     * Prywatny konstruktor blokujący tworzenie instancji klasy narzędziowej.
     */
    private UiDialogs() {
    }

    /**
     * Pokazuje błąd walidacji, tłumacząc klucz zapisany w wyjątku.
     *
     * @param parent komponent nadrzędny
     * @param languageManager menedżer języka
     * @param exception wyjątek walidacji
     */
    public static void showValidationError(
            Component parent,
            LanguageManager languageManager,
            ValidationException exception) {
        JOptionPane.showMessageDialog(
                parent,
                languageManager.get(exception.getMessage()),
                languageManager.get("dialog.error"),
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Pokazuje błąd techniczny.
     *
     * @param parent komponent nadrzędny
     * @param languageManager menedżer języka
     * @param messageKey klucz komunikatu głównego
     * @param details szczegóły błędu
     */
    public static void showError(
            Component parent,
            LanguageManager languageManager,
            String messageKey,
            String details) {
        String message = languageManager.get(messageKey);
        if (details != null && !details.isBlank()) {
            message += System.lineSeparator() + details;
        }
        JOptionPane.showMessageDialog(
                parent,
                message,
                languageManager.get("dialog.error"),
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Pokazuje komunikat informacyjny.
     *
     * @param parent komponent nadrzędny
     * @param languageManager menedżer języka
     * @param messageKey klucz komunikatu
     */
    public static void showInformation(
            Component parent,
            LanguageManager languageManager,
            String messageKey) {
        JOptionPane.showMessageDialog(
                parent,
                languageManager.get(messageKey),
                languageManager.get("dialog.information"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Pyta użytkownika o potwierdzenie operacji.
     *
     * @param parent komponent nadrzędny
     * @param languageManager menedżer języka
     * @param messageKey klucz pytania
     * @return {@code true}, gdy użytkownik wybrał odpowiedź twierdzącą
     */
    public static boolean confirm(
            Component parent,
            LanguageManager languageManager,
            String messageKey) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                languageManager.get(messageKey),
                languageManager.get("dialog.confirmation"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}
