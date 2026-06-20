package pl.edu.wit.studentmanager.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.StudentGroup;


public final class GroupService {

   
    private final AppData data;


    public GroupService(AppData data) {
        this.data = Objects.requireNonNull(data, "Dane nie mogą być puste.");
    }


    public StudentGroup addGroup(String code, String specialization, String description) {
        String normalizedCode = ServiceValidation.requireText(code, "validation.group.code.required");
        String normalizedSpecialization = ServiceValidation.requireText(
                specialization, "validation.group.specialization.required");
        ensureCodeUnique(normalizedCode, null);

        StudentGroup group = new StudentGroup(normalizedCode, normalizedSpecialization, description);
        data.getGroups().add(group);
        return group;
    }


    public StudentGroup updateGroup(
            UUID groupId,
            String code,
            String specialization,
            String description) {
        StudentGroup group = findById(groupId);
        String normalizedCode = ServiceValidation.requireText(code, "validation.group.code.required");
        String normalizedSpecialization = ServiceValidation.requireText(
                specialization, "validation.group.specialization.required");
        ensureCodeUnique(normalizedCode, groupId);

        group.setCode(normalizedCode);
        group.setSpecialization(normalizedSpecialization);
        group.setDescription(description);
        return group;
    }


    public void deleteGroup(UUID groupId) {
        StudentGroup group = findById(groupId);
        data.getAssignments().removeIf(assignment -> assignment.getGroupId().equals(groupId));
        data.getGroups().remove(group);
    }


    public StudentGroup findById(UUID groupId) {
        if (groupId == null) {
            throw new ValidationException("validation.group.notFound");
        }
        return data.getGroups().stream()
                .filter(group -> group.getId().equals(groupId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("validation.group.notFound"));
    }


    public List<StudentGroup> getAllGroups() {
        return data.getGroups().stream()
                .sorted(Comparator.comparing(StudentGroup::getCode, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }


    private void ensureCodeUnique(String code, UUID ignoredGroupId) {
        boolean duplicate = data.getGroups().stream()
                .filter(group -> ignoredGroupId == null || !group.getId().equals(ignoredGroupId))
                .anyMatch(group -> group.getCode().equalsIgnoreCase(code));
        if (duplicate) {
            throw new ValidationException("validation.group.code.duplicate");
        }
    }
}
