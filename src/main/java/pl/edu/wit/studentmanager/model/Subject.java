package pl.edu.wit.studentmanager.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Reprezentuje przedmiot akademicki.
 */
public final class Subject {

    /** Unikalny identyfikator przedmiotu. */
    private final UUID id;

    /** Nazwa przedmiotu. */
    private String name;

    /** Opis przedmiotu. */
    private String description;

    /**
     * Tworzy przedmiot z nowym identyfikatorem.
     *
     * @param name nazwa przedmiotu
     * @param description opis
     */
    public Subject(String name, String description) {
        this(UUID.randomUUID(), name, description);
    }

    /**
     * Tworzy przedmiot z podanym identyfikatorem.
     *
     * @param id identyfikator
     * @param name nazwa przedmiotu
     * @param description opis
     */
    public Subject(UUID id, String name, String description) {
        this.id = Objects.requireNonNull(id, "Identyfikator nie może być pusty.");
        setName(name);
        setDescription(description);
    }

    /**
     * Tworzy kopię przedmiotu.
     *
     * @param other kopiowany przedmiot
     */
    public Subject(Subject other) {
        this(other.id, other.name, other.description);
    }

    /**
     * Zwraca identyfikator przedmiotu.
     *
     * @return identyfikator
     */
    public UUID getId() {
        return id;
    }

    /**
     * Zwraca nazwę przedmiotu.
     *
     * @return nazwa
     */
    public String getName() {
        return name;
    }

    /**
     * Ustawia nazwę przedmiotu.
     *
     * @param name nowa nazwa
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nazwa przedmiotu nie może być pusta.");
        }
        this.name = name.trim();
    }

    /**
     * Zwraca opis przedmiotu.
     *
     * @return opis
     */
    public String getDescription() {
        return description;
    }

    /**
     * Ustawia opis przedmiotu.
     *
     * @param description nowy opis
     */
    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    /**
     * Zwraca nazwę przedmiotu używaną w polach wyboru.
     *
     * @return nazwa przedmiotu
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Porównuje wszystkie pola przedmiotu.
     *
     * @param object porównywany obiekt
     * @return {@code true}, gdy przedmioty są równe
     */
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

    /**
     * Oblicza kod skrótu przedmiotu.
     *
     * @return kod skrótu
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}
