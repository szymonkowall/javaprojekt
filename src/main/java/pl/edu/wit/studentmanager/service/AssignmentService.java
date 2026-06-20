package pl.edu.wit.studentmanager.service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.GroupAssignment;
import pl.edu.wit.studentmanager.model.StudentGroup;


public final class AssignmentService {


    private final AppData data;


    public AssignmentService(AppData data) {
        this.data = Objects.requireNonNull(data, "Dane nie mogą być puste.");
    }


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


    public void unassignStudent(UUID studentId) {
        GroupAssignment assignment = findAssignmentByStudent(studentId)
                .orElseThrow(() -> new ValidationException("validation.assignment.notFound"));
        data.getAssignments().remove(assignment);
    }


    public Optional<GroupAssignment> findAssignmentByStudent(UUID studentId) {
        if (studentId == null) {
            return Optional.empty();
        }
        return data.getAssignments().stream()
                .filter(assignment -> assignment.getStudentId().equals(studentId))
                .findFirst();
    }


    public Optional<StudentGroup> findGroupForStudent(UUID studentId) {
        return findAssignmentByStudent(studentId)
                .flatMap(assignment -> data.getGroups().stream()
                        .filter(group -> group.getId().equals(assignment.getGroupId()))
                        .findFirst());
    }


    public long countStudentsInGroup(UUID groupId) {
        return data.getAssignments().stream()
                .filter(assignment -> assignment.getGroupId().equals(groupId))
                .count();
    }


    private void ensureStudentExists(UUID studentId) {
        boolean exists = studentId != null && data.getStudents().stream()
                .anyMatch(student -> student.getId().equals(studentId));
        if (!exists) {
            throw new ValidationException("validation.student.notFound");
        }
    }


    private void ensureGroupExists(UUID groupId) {
        boolean exists = groupId != null && data.getGroups().stream()
                .anyMatch(group -> group.getId().equals(groupId));
        if (!exists) {
            throw new ValidationException("validation.group.notFound");
        }
    }
}
