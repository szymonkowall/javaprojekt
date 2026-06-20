package pl.edu.wit.studentmanager.model;

import java.util.Objects;
import java.util.UUID;


public final class StudentGroup {


    private final UUID id;


    private String code;


    private String specialization;


    private String description;


    public StudentGroup(String code, String specialization, String description) {
        this(UUID.randomUUID(), code, specialization, description);
    }


    public StudentGroup(UUID id, String code, String specialization, String description) {
        this.id = Objects.requireNonNull(id, "Identyfikator nie może być pusty.");
        setCode(code);
        setSpecialization(specialization);
        setDescription(description);
    }


    public StudentGroup(StudentGroup other) {
        this(other.id, other.code, other.specialization, other.description);
    }


    public UUID getId() {
        return id;
    }


    public String getCode() {
        return code;
    }

 
    public void setCode(String code) {
        this.code = requireText(code, "Kod grupy nie może być pusty.");
    }


    public String getSpecialization() {
        return specialization;
    }


    public void setSpecialization(String specialization) {
        this.specialization = requireText(specialization, "Specjalizacja nie może być pusta.");
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }


    @Override
    public String toString() {
        return code;
    }


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


    @Override
    public int hashCode() {
        return Objects.hash(id, code, specialization, description);
    }


    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
