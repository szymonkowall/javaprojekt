package pl.edu.wit.studentmanager.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Reprezentuje liczbę punktów uzyskaną przez studenta w jednym kryterium.
 */
public final class StudentScore {

    /** Unikalny identyfikator wyniku. */
    private final UUID id;

    /** Identyfikator ocenianego studenta. */
    private final UUID studentId;

    /** Identyfikator kryterium. */
    private final UUID criterionId;

    /** Liczba uzyskanych punktów. */
    private double points;

    /**
     * Tworzy wynik z nowym identyfikatorem.
     *
     * @param studentId identyfikator studenta
     * @param criterionId identyfikator kryterium
     * @param points liczba punktów
     */
    public StudentScore(UUID studentId, UUID criterionId, double points) {
        this(UUID.randomUUID(), studentId, criterionId, points);
    }

    /**
     * Tworzy wynik z podanym identyfikatorem.
     *
     * @param id identyfikator wyniku
     * @param studentId identyfikator studenta
     * @param criterionId identyfikator kryterium
     * @param points liczba punktów
     */
    public StudentScore(UUID id, UUID studentId, UUID criterionId, double points) {
        this.id = Objects.requireNonNull(id, "Identyfikator nie może być pusty.");
        this.studentId = Objects.requireNonNull(studentId, "Identyfikator studenta nie może być pusty.");
        this.criterionId = Objects.requireNonNull(criterionId, "Identyfikator kryterium nie może być pusty.");
        setPoints(points);
    }

    /**
     * Tworzy kopię wyniku.
     *
     * @param other kopiowany wynik
     */
    public StudentScore(StudentScore other) {
        this(other.id, other.studentId, other.criterionId, other.points);
    }

    /**
     * Zwraca identyfikator wyniku.
     *
     * @return identyfikator
     */
    public UUID getId() {
        return id;
    }

    /**
     * Zwraca identyfikator studenta.
     *
     * @return identyfikator studenta
     */
    public UUID getStudentId() {
        return studentId;
    }

    /**
     * Zwraca identyfikator kryterium.
     *
     * @return identyfikator kryterium
     */
    public UUID getCriterionId() {
        return criterionId;
    }

    /**
     * Zwraca liczbę punktów.
     *
     * @return punkty
     */
    public double getPoints() {
        return points;
    }

    /**
     * Ustawia liczbę punktów. Pełna walidacja względem maksimum jest wykonywana
     * przez serwis, ponieważ ta klasa nie zna kryterium.
     *
     * @param points nowa liczba punktów
     */
    public void setPoints(double points) {
        if (!Double.isFinite(points) || points < 0.0) {
            throw new IllegalArgumentException("Liczba punktów musi być nieujemną liczbą skończoną.");
        }
        this.points = points;
    }

    /**
     * Porównuje wszystkie pola wyniku.
     *
     * @param object porównywany obiekt
     * @return {@code true}, gdy wyniki są równe
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof StudentScore other)) {
            return false;
        }
        return Double.compare(points, other.points) == 0
                && id.equals(other.id)
                && studentId.equals(other.studentId)
                && criterionId.equals(other.criterionId);
    }

    /**
     * Oblicza kod skrótu wyniku.
     *
     * @return kod skrótu
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, criterionId, points);
    }
}
