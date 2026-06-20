package pl.edu.wit.studentmanager.exception;

/**
 * Sygnalizuje naruszenie reguły walidacyjnej lub biznesowej aplikacji.
 */
public class ValidationException extends RuntimeException {

    /** Numer wersji klasy używany przez mechanizm serializacji wyjątków. */
    private static final long serialVersionUID = 1L;

    /**
     * Tworzy wyjątek z komunikatem przeznaczonym do pokazania użytkownikowi.
     *
     * @param message opis błędu
     */
    public ValidationException(String message) {
        super(message);
    }
}
