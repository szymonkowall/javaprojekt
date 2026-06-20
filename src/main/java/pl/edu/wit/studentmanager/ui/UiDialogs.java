package pl.edu.wit.studentmanager.ui;

import java.awt.Component;

import javax.swing.JOptionPane;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.i18n.LanguageManager;


public final class UiDialogs {


    private UiDialogs() {
    }


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
