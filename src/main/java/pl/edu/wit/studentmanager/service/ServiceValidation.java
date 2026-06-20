package pl.edu.wit.studentmanager.service;

import pl.edu.wit.studentmanager.exception.ValidationException;


final class ServiceValidation {

    private ServiceValidation() {
    }


    static String requireText(String value, String messageKey) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(messageKey);
        }
        return value.trim();
    }


    static void requirePositiveFinite(double value, String messageKey) {
        if (!Double.isFinite(value) || value <= 0.0) {
            throw new ValidationException(messageKey);
        }
    }


    static void requireNonNegativeFinite(double value, String messageKey) {
        if (!Double.isFinite(value) || value < 0.0) {
            throw new ValidationException(messageKey);
        }
    }
}
