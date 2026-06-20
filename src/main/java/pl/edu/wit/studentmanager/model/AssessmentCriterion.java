package pl.edu.wit.studentmanager.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Reprezentuje kryterium oceny przypisane do przedmiotu.
 */
public final class AssessmentCriterion {

    /** Unikalny identyfikator kryterium. */
    private final UUID id;

    /** Identyfikator przedmiotu nadrzędnego. */
    private final UUID subjectId;

    /** Nazwa kryterium, na przykład Kolokwium 1. */
    private String name;

    /** Maksymalna liczba punktów możliwych do uzyskania. */
    private double maximumPoints;

    /**
     * Tworzy kryterium z nowym identyfikatorem.
     *
     * @param subjectId identyfikator przedmiotu
     * @param name nazwa kryterium
     * @param maximumPoints maksymalna liczba punktów
     */
    public AssessmentCriterion(UUID subjectId, String name, double maximumPoints) {
        this(UUID.randomUUID(), subjectId, name, maximumPoints);
    }

    /**
     * Tworzy kryterium z podanym identyfikatorem.
     *
     * @param id identyfikator kryterium
     * @param subjectId identyfikator przedmiotu
     * @param name nazwa kryterium
     * @param maximumPoints maksymalna liczba punktów
     */
    public AssessmentCriterion(UUID id, UUID subjectId, String name, double maximumPoints) {
        this.id = Objects.requireNonNull(id, "Identyfikator nie może być pusty.");
        this.subjectId = Objects.requireNonNull(subjectId, "Identyfikator przedmiotu nie może być pusty.");
        setName(name);
        setMaximumPoints(maximumPoints);
    }

    /**
     * Tworzy kopię kryterium.
     *
     * @param other kopiowane kryterium
     */
    public AssessmentCriterion(AssessmentCriterion other) {
        this(other.id, other.subjectId, other.name, other.maximumPoints);
    }

    /**
     * Zwraca identyfikator kryterium.
     *
     * @return identyfikator kryterium
     */
    public UUID getId() {
        return id;
    }

    /**
     * Zwraca identyfikator przedmiotu.
     *
     * @return identyfikator przedmiotu
     */
    public UUID getSubjectId() {
        return subjectId;
    }

    /**
     * Zwraca nazwę kryterium.
     *
     * @return nazwa
     */
    public String getName() {
        return name;
    }

    /**
     * Ustawia nazwę kryterium.
     *
     * @param name nowa nazwa
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nazwa kryterium nie może być pusta.");
        }
        this.name = name.trim();
    }

    /**
     * Zwraca maksymalną liczbę punktów.
     *
     * @return maksimum punktów
     */
    public double getMaximumPoints() {
        return maximumPoints;
    }

    /**
     * Ustawia maksymalną liczbę punktów.
     *
     * @param maximumPoints nowe maksimum
     */
    public void setMaximumPoints(double maximumPoints) {
        if (!Double.isFinite(maximumPoints) || maximumPoints <= 0.0) {
            throw new IllegalArgumentException("Maksymalna liczba punktów musi być dodatnią liczbą skończoną.");
        }
        this.maximumPoints = maximumPoints;
    }

    /**
     * Zwraca nazwę kryterium używaną w polach wyboru.
     *
     * @return nazwa kryterium
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Porównuje wszystkie pola kryterium.
     *
     * @param object porównywany obiekt
     * @return {@code true}, gdy kryteria są równe
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AssessmentCriterion other)) {
            return false;
        }
        return Double.compare(maximumPoints, other.maximumPoints) == 0
                && id.equals(other.id)
                && subjectId.equals(other.subjectId)
                && name.equals(other.name);
    }

    /**
     * Oblicza kod skrótu kryterium.
     *
     * @return kod skrótu
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, subjectId, name, maximumPoints);
    }
}
