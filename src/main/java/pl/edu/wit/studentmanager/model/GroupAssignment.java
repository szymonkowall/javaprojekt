package pl.edu.wit.studentmanager.model;

import java.util.Objects;
import java.util.UUID;


public final class GroupAssignment {


    private final UUID id;


    private final UUID studentId;


    private final UUID groupId;


    public GroupAssignment(UUID studentId, UUID groupId) {
        this(UUID.randomUUID(), studentId, groupId);
    }


    public GroupAssignment(UUID id, UUID studentId, UUID groupId) {
        this.id = Objects.requireNonNull(id, "Identyfikator nie może być pusty.");
        this.studentId = Objects.requireNonNull(studentId, "Identyfikator studenta nie może być pusty.");
        this.groupId = Objects.requireNonNull(groupId, "Identyfikator grupy nie może być pusty.");
    }


    public GroupAssignment(GroupAssignment other) {
        this(other.id, other.studentId, other.groupId);
    }


    public UUID getId() {
        return id;
    }

    public UUID getStudentId() {
        return studentId;
    }


    public UUID getGroupId() {
        return groupId;
    }



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


    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, groupId);
    }
}
