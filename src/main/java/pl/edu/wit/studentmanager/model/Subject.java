package pl.edu.wit.studentmanager.model;

import java.util.Objects;
import java.util.UUID;


public final class Subject {


    private final UUID id;


    private String name;


    private String description;


    public Subject(String name, String description) {
        this(UUID.randomUUID(), name, description);
    }


    public Subject(UUID id, String name, String description) {
        this.id = Objects.requireNonNull(id, "Identyfikator nie może być pusty.");
        setName(name);
        setDescription(description);
    }


    public Subject(Subject other) {
        this(other.id, other.name, other.description);
    }


    public UUID getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nazwa przedmiotu nie może być pusta.");
        }
        this.name = name.trim();
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
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
        if (!(object instanceof Subject other)) {
            return false;
        }
        return id.equals(other.id)
                && name.equals(other.name)
                && description.equals(other.description);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}
