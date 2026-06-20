package pl.edu.wit.studentmanager.model;

import java.util.Objects;
import java.util.UUID;


public final class StudentScore {


    private final UUID id;


    private final UUID studentId;


    private final UUID criterionId;

  
    private double points;


    public StudentScore(UUID studentId, UUID criterionId, double points) {
        this(UUID.randomUUID(), studentId, criterionId, points);
    }


    public StudentScore(UUID id, UUID studentId, UUID criterionId, double points) {
        this.id = Objects.requireNonNull(id, "Identyfikator nie może być pusty.");
        this.studentId = Objects.requireNonNull(studentId, "Identyfikator studenta nie może być pusty.");
        this.criterionId = Objects.requireNonNull(criterionId, "Identyfikator kryterium nie może być pusty.");
        setPoints(points);
    }


    public StudentScore(StudentScore other) {
        this(other.id, other.studentId, other.criterionId, other.points);
    }


    public UUID getId() {
        return id;
    }


    public UUID getStudentId() {
        return studentId;
    }


    public UUID getCriterionId() {
        return criterionId;
    }


    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        if (!Double.isFinite(points) || points < 0.0) {
            throw new IllegalArgumentException("Liczba punktów musi być nieujemną liczbą skończoną.");
        }
        this.points = points;
    }


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

 
    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, criterionId, points);
    }
}
