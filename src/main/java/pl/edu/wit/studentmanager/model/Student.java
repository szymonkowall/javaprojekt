package pl.edu.wit.studentmanager.model;

import java.util.Objects;
import java.util.UUID;


public final class Student {


    private final UUID id;

    private String firstName;

    private String lastName;


    private String albumNumber;


    public Student(String firstName, String lastName, String albumNumber) {
        this(UUID.randomUUID(), firstName, lastName, albumNumber);
    }


    public Student(UUID id, String firstName, String lastName, String albumNumber) {
        this.id = Objects.requireNonNull(id, "Identyfikator nie może być pusty.");
        setFirstName(firstName);
        setLastName(lastName);
        setAlbumNumber(albumNumber);
    }


    public Student(Student other) {
        this(other.id, other.firstName, other.lastName, other.albumNumber);
    }


    public UUID getId() {
        return id;
    }


    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = requireText(firstName, "Imię nie może być puste.");
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = requireText(lastName, "Nazwisko nie może być puste.");
    }


    public String getAlbumNumber() {
        return albumNumber;
    }


    public void setAlbumNumber(String albumNumber) {
        this.albumNumber = requireText(albumNumber, "Numer albumu nie może być pusty.");
    }


    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + albumNumber + ")";
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Student other)) {
            return false;
        }
        return id.equals(other.id)
                && firstName.equals(other.firstName)
                && lastName.equals(other.lastName)
                && albumNumber.equals(other.albumNumber);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, albumNumber);
    }


    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
