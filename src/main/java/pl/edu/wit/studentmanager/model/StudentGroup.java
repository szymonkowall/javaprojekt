package pl.edu.wit.studentmanager.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Reprezentuje grupę studencką.
 */
public final class StudentGroup {

    /** Unikalny identyfikator grupy. */
    private final UUID id;

    /** Unikalny kod grupy. */
    private String code;

    /** Nazwa specjalizacji. */
    private String specialization;

    /** Dodatkowy opis grupy. */
    private String description;

    /**
     * Tworzy grupę z nowym identyfikatorem.
     *
     * @param code kod grupy
     * @param specialization specjalizacja
     * @param description opis
     */
    public StudentGroup(String code, String specialization, String description) {
        this(UUID.randomUUID(), code, specialization, description);
    }

    /**
     * Tworzy grupę z podanym identyfikatorem.
     *
     * @param id identyfikator
     * @param code kod grupy
     * @param specialization specjalizacja
     * @param description opis
     */
    public StudentGroup(UUID id, String code, String specialization, String description) {
        this.id = Objects.requireNonNull(id, "Identyfikator nie może być pusty.");
        setCode(code);
        setSpecialization(specialization);
        setDescription(description);
    }

    /**
     * Tworzy kopię grupy.
     *
     * @param other kopiowana grupa
     */
    public StudentGroup(StudentGroup other) {
        this(other.id, other.code, other.specialization, other.description);
    }

    /**
     * Zwraca identyfikator grupy.
     *
     * @return identyfikator
     */
    public UUID getId() {
        return id;
    }

    /**
     * Zwraca kod grupy.
     *
     * @return kod grupy
     */
    public String getCode() {
        return code;
    }

    /**
     * Ustawia kod grupy.
     *
     * @param code nowy kod
     */
    public void setCode(String code) {
        this.code = requireText(code, "Kod grupy nie może być pusty.");
    }

    /**
     * Zwraca specjalizację grupy.
     *
     * @return specjalizacja
     */
    public String getSpecialization() {
        return specialization;
    }

    /**
     * Ustawia specjalizację grupy.
     *
     * @param specialization nowa specjalizacja
     */
    public void setSpecialization(String specialization) {
        this.specialization = requireText(specialization, "Specjalizacja nie może być pusta.");
    }

    /**
     * Zwraca opis grupy.
     *
     * @return opis, który może być pusty
     */
    public String getDescription() {
        return description;
    }

    /**
     * Ustawia opis grupy.
     *
     * @param description nowy opis; wartość {@code null} jest zamieniana na pusty tekst
     */
    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    /**
     * Zwraca kod grupy używany w polach wyboru.
     *
     * @return kod grupy
     */
    @Override
    public String toString() {
        return code;
    }

    /**
     * Porównuje wszystkie pola grupy.
     *
     * @param object porównywany obiekt
     * @return {@code true}, gdy obiekty są równe
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof StudentGroup other)) {
            return false;
        }
        return id.equals(other.id)
                && code.equals(other.code)
                && specialization.equals(other.specialization)
                && description.equals(other.description);
    }

    /**
     * Oblicza kod skrótu grupy.
     *
     * @return kod skrótu
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, code, specialization, description);
    }

    /**
     * Sprawdza i normalizuje obowiązkowy tekst.
     *
     * @param value wartość
     * @param message komunikat błędu
     * @return tekst bez skrajnych białych znaków
     */
    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
