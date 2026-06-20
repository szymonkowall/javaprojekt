package pl.edu.wit.studentmanager.service;

import pl.edu.wit.studentmanager.exception.ValidationException;

/**
 * Udostępnia wspólne metody walidacyjne używane przez serwisy.
 */
final class ServiceValidation {

    /**
     * Prywatny konstruktor blokujący tworzenie instancji klasy narzędziowej.
     */
    private ServiceValidation() {
    }

    /**
     * Sprawdza obowiązkową wartość tekstową i usuwa skrajne białe znaki.
     *
     * @param value sprawdzana wartość
     * @param messageKey klucz komunikatu błędu
     * @return znormalizowany tekst
     */
    static String requireText(String value, String messageKey) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(messageKey);
        }
        return value.trim();
    }

    /**
     * Sprawdza, czy liczba jest dodatnia i skończona.
     *
     * @param value sprawdzana liczba
     * @param messageKey klucz komunikatu błędu
     */
    static void requirePositiveFinite(double value, String messageKey) {
        if (!Double.isFinite(value) || value <= 0.0) {
            throw new ValidationException(messageKey);
        }
    }

    /**
     * Sprawdza, czy liczba jest nieujemna i skończona.
     *
     * @param value sprawdzana liczba
     * @param messageKey klucz komunikatu błędu
     */
    static void requireNonNegativeFinite(double value, String messageKey) {
        if (!Double.isFinite(value) || value < 0.0) {
            throw new ValidationException(messageKey);
        }
    }
}
