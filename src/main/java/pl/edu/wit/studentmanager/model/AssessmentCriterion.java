package pl.edu.wit.studentmanager.model;

import java.util.Objects;
import java.util.UUID;


public final class AssessmentCriterion {


    private final UUID id;


    private final UUID subjectId;

    private String name;

 
    private double maximumPoints;


    public AssessmentCriterion(UUID subjectId, String name, double maximumPoints) {
        this(UUID.randomUUID(), subjectId, name, maximumPoints);
    }


    public AssessmentCriterion(UUID id, UUID subjectId, String name, double maximumPoints) {
        this.id = Objects.requireNonNull(id, "Identyfikator nie może być pusty.");
        this.subjectId = Objects.requireNonNull(subjectId, "Identyfikator przedmiotu nie może być pusty.");
        setName(name);
        setMaximumPoints(maximumPoints);
    }


    public AssessmentCriterion(AssessmentCriterion other) {
        this(other.id, other.subjectId, other.name, other.maximumPoints);
    }


    public UUID getId() {
        return id;
    }


    public UUID getSubjectId() {
        return subjectId;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nazwa kryterium nie może być pusta.");
        }
        this.name = name.trim();
    }


    public double getMaximumPoints() {
        return maximumPoints;
    }


    public void setMaximumPoints(double maximumPoints) {
        if (!Double.isFinite(maximumPoints) || maximumPoints <= 0.0) {
            throw new IllegalArgumentException("Maksymalna liczba punktów musi być dodatnią liczbą skończoną.");
        }
        this.maximumPoints = maximumPoints;
    }


    @Override
    public String toString() {
        return name;
    }


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


    @Override
    public int hashCode() {
        return Objects.hash(id, subjectId, name, maximumPoints);
    }
}
