package pl.edu.wit.studentmanager.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Reprezentuje przypisanie jednego studenta do jednej grupy.
 */
public final class GroupAssignment {

    /** Unikalny identyfikator przypisania. */
    private final UUID id;

    /** Identyfikator przypisanego studenta. */
    private final UUID studentId;

    /** Identyfikator grupy. */
    private final UUID groupId;

    /**
     * Tworzy przypisanie z nowym identyfikatorem.
     *
     * @param studentId identyfikator studenta
     * @param groupId identyfikator grupy
     */
    public GroupAssignment(UUID studentId, UUID groupId) {
        this(UUID.randomUUID(), studentId, groupId);
    }

    /**
     * Tworzy przypisanie z podanym identyfikatorem.
     *
     * @param id identyfikator przypisania
     * @param studentId identyfikator studenta
     * @param groupId identyfikator grupy
     */
    public GroupAssignment(UUID id, UUID studentId, UUID groupId) {
        this.id = Objects.requireNonNull(id, "Identyfikator nie może być pusty.");
        this.studentId = Objects.requireNonNull(studentId, "Identyfikator studenta nie może być pusty.");
        this.groupId = Objects.requireNonNull(groupId, "Identyfikator grupy nie może być pusty.");
    }

    /**
     * Tworzy kopię przypisania.
     *
     * @param other kopiowane przypisanie
     */
    public GroupAssignment(GroupAssignment other) {
        this(other.id, other.studentId, other.groupId);
    }

    /**
     * Zwraca identyfikator przypisania.
     *
     * @return identyfikator przypisania
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
     * Zwraca identyfikator grupy.
     *
     * @return identyfikator grupy
     */
    public UUID getGroupId() {
        return groupId;
    }

    /**
     * Porównuje wszystkie pola przypisania.
     *
     * @param object porównywany obiekt
     * @return {@code true}, gdy przypisania są równe
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof GroupAssignment other)) {
            return false;
        }
        return id.equals(other.id)
                && studentId.equals(other.studentId)
                && groupId.equals(other.groupId);
    }

    /**
     * Oblicza kod skrótu przypisania.
     *
     * @return kod skrótu
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, groupId);
    }
}
