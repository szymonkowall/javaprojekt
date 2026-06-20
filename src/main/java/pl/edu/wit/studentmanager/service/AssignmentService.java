package pl.edu.wit.studentmanager.service;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.GroupAssignment;
import pl.edu.wit.studentmanager.model.StudentGroup;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Zarządza przypisaniem studenta do grupy.
 */
public final class AssignmentService {

    /** Wspólny kontener danych aplikacji. */
    private final AppData data;

    /**
     * Tworzy serwis przypisań.
     *
     * @param data dane aplikacji
     */
    public AssignmentService(AppData data) {
        this.data = Objects.requireNonNull(data, "Dane nie mogą być puste.");
    }

    /**
     * Przypisuje studenta do grupy.
     *
     * @param studentId identyfikator studenta
     * @param groupId identyfikator grupy
     * @return utworzone przypisanie
     */
    public GroupAssignment assignStudent(UUID studentId, UUID groupId) {
        ensureStudentExists(studentId);
        ensureGroupExists(groupId);
        if (findAssignmentByStudent(studentId).isPresent()) {
            throw new ValidationException("validation.assignment.studentAlreadyAssigned");
        }
        GroupAssignment assignment = new GroupAssignment(studentId, groupId);
        data.getAssignments().add(assignment);
        return assignment;
    }

    /**
     * Usuwa przypisanie studenta do grupy.
     *
     * @param studentId identyfikator studenta
     */
    public void unassignStudent(UUID studentId) {
        GroupAssignment assignment = findAssignmentByStudent(studentId)
                .orElseThrow(() -> new ValidationException("validation.assignment.notFound"));
        data.getAssignments().remove(assignment);
    }

    /**
     * Zwraca przypisanie danego studenta.
     *
     * @param studentId identyfikator studenta
     * @return opcjonalne przypisanie
     */
    public Optional<GroupAssignment> findAssignmentByStudent(UUID studentId) {
        if (studentId == null) {
            return Optional.empty();
        }
        return data.getAssignments().stream()
                .filter(assignment -> assignment.getStudentId().equals(studentId))
                .findFirst();
    }

    /**
     * Zwraca grupę przypisaną do studenta.
     *
     * @param studentId identyfikator studenta
     * @return opcjonalna grupa
     */
    public Optional<StudentGroup> findGroupForStudent(UUID studentId) {
        return findAssignmentByStudent(studentId)
                .flatMap(assignment -> data.getGroups().stream()
                        .filter(group -> group.getId().equals(assignment.getGroupId()))
                        .findFirst());
    }

    /**
     * Liczy studentów przypisanych do grupy.
     *
     * @param groupId identyfikator grupy
     * @return liczba przypisań
     */
    public long countStudentsInGroup(UUID groupId) {
        return data.getAssignments().stream()
                .filter(assignment -> assignment.getGroupId().equals(groupId))
                .count();
    }

    /**
     * Sprawdza istnienie studenta.
     *
     * @param studentId identyfikator studenta
     */
    private void ensureStudentExists(UUID studentId) {
        boolean exists = studentId != null && data.getStudents().stream()
                .anyMatch(student -> student.getId().equals(studentId));
        if (!exists) {
            throw new ValidationException("validation.student.notFound");
        }
    }

    /**
     * Sprawdza istnienie grupy.
     *
     * @param groupId identyfikator grupy
     */
    private void ensureGroupExists(UUID groupId) {
        boolean exists = groupId != null && data.getGroups().stream()
                .anyMatch(group -> group.getId().equals(groupId));
        if (!exists) {
            throw new ValidationException("validation.group.notFound");
        }
    }
}
