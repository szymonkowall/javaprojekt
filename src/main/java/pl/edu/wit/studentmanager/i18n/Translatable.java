package pl.edu.wit.studentmanager.i18n;

/**
 * Określa komponent, którego teksty można odświeżyć po zmianie języka.
 */
public interface Translatable {

    /**
     * Aktualizuje wszystkie teksty widoczne w komponencie.
     */
    void updateTexts();
}
