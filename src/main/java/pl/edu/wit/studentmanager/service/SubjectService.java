package pl.edu.wit.studentmanager.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.model.Subject;


public final class SubjectService {

    private final AppData data;


    public SubjectService(AppData data) {
        this.data = Objects.requireNonNull(data, "Dane nie mogą być puste.");
    }


    public Subject addSubject(String name, String description) {
        String normalizedName = ServiceValidation.requireText(name, "validation.subject.name.required");
        ensureSubjectNameUnique(normalizedName, null);
        Subject subject = new Subject(normalizedName, description);
        data.getSubjects().add(subject);
        return subject;
    }


    public Subject updateSubject(UUID subjectId, String name, String description) {
        Subject subject = findSubjectById(subjectId);
        String normalizedName = ServiceValidation.requireText(name, "validation.subject.name.required");
        ensureSubjectNameUnique(normalizedName, subjectId);
        subject.setName(normalizedName);
        subject.setDescription(description);
        return subject;
    }


    public void deleteSubject(UUID subjectId) {
        Subject subject = findSubjectById(subjectId);
        Set<UUID> criterionIds = data.getCriteria().stream()
                .filter(criterion -> criterion.getSubjectId().equals(subjectId))
                .map(AssessmentCriterion::getId)
                .collect(Collectors.toSet());
        data.getScores().removeIf(score -> criterionIds.contains(score.getCriterionId()));
        data.getCriteria().removeIf(criterion -> criterion.getSubjectId().equals(subjectId));
        data.getSubjects().remove(subject);
    }


    public AssessmentCriterion addCriterion(UUID subjectId, String name, double maximumPoints) {
        findSubjectById(subjectId);
        String normalizedName = ServiceValidation.requireText(name, "validation.criterion.name.required");
        ServiceValidation.requirePositiveFinite(
                maximumPoints, "validation.criterion.maximum.positive");
        ensureCriterionNameUnique(subjectId, normalizedName, null);

        AssessmentCriterion criterion = new AssessmentCriterion(subjectId, normalizedName, maximumPoints);
        data.getCriteria().add(criterion);
        return criterion;
    }


    public AssessmentCriterion updateCriterion(
            UUID criterionId,
            String name,
            double maximumPoints) {
        AssessmentCriterion criterion = findCriterionById(criterionId);
        String normalizedName = ServiceValidation.requireText(name, "validation.criterion.name.required");
        ServiceValidation.requirePositiveFinite(
                maximumPoints, "validation.criterion.maximum.positive");
        ensureCriterionNameUnique(criterion.getSubjectId(), normalizedName, criterionId);

        boolean scoreTooHigh = data.getScores().stream()
                .filter(score -> score.getCriterionId().equals(criterionId))
                .anyMatch(score -> score.getPoints() > maximumPoints);
        if (scoreTooHigh) {
            throw new ValidationException("validation.criterion.maximum.belowExistingScore");
        }

        criterion.setName(normalizedName);
        criterion.setMaximumPoints(maximumPoints);
        return criterion;
    }


    public void deleteCriterion(UUID criterionId) {
        AssessmentCriterion criterion = findCriterionById(criterionId);
        data.getScores().removeIf(score -> score.getCriterionId().equals(criterionId));
        data.getCriteria().remove(criterion);
    }


    public Subject findSubjectById(UUID subjectId) {
        if (subjectId == null) {
            throw new ValidationException("validation.subject.notFound");
        }
        return data.getSubjects().stream()
                .filter(subject -> subject.getId().equals(subjectId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("validation.subject.notFound"));
    }


    public AssessmentCriterion findCriterionById(UUID criterionId) {
        if (criterionId == null) {
            throw new ValidationException("validation.criterion.notFound");
        }
        return data.getCriteria().stream()
                .filter(criterion -> criterion.getId().equals(criterionId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("validation.criterion.notFound"));
    }


    public List<Subject> getAllSubjects() {
        return data.getSubjects().stream()
                .sorted(Comparator.comparing(Subject::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }


    public List<AssessmentCriterion> getCriteriaForSubject(UUID subjectId) {
        return data.getCriteria().stream()
                .filter(criterion -> criterion.getSubjectId().equals(subjectId))
                .sorted(Comparator.comparing(
                        AssessmentCriterion::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }


    private void ensureSubjectNameUnique(String name, UUID ignoredId) {
        boolean duplicate = data.getSubjects().stream()
                .filter(subject -> ignoredId == null || !subject.getId().equals(ignoredId))
                .anyMatch(subject -> subject.getName().equalsIgnoreCase(name));
        if (duplicate) {
            throw new ValidationException("validation.subject.name.duplicate");
        }
    }


    private void ensureCriterionNameUnique(UUID subjectId, String name, UUID ignoredId) {
        boolean duplicate = data.getCriteria().stream()
                .filter(criterion -> criterion.getSubjectId().equals(subjectId))
                .filter(criterion -> ignoredId == null || !criterion.getId().equals(ignoredId))
                .anyMatch(criterion -> criterion.getName().equalsIgnoreCase(name));
        if (duplicate) {
            throw new ValidationException("validation.criterion.name.duplicate");
        }
    }
}
