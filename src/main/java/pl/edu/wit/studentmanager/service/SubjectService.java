package pl.edu.wit.studentmanager.service;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.model.Subject;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Zarządza przedmiotami i ich kryteriami oceniania.
 */
public final class SubjectService {

    /** Wspólny kontener danych aplikacji. */
    private final AppData data;

    /**
     * Tworzy serwis przedmiotów.
     *
     * @param data dane aplikacji
     */
    public SubjectService(AppData data) {
        this.data = Objects.requireNonNull(data, "Dane nie mogą być puste.");
    }

    /**
     * Dodaje przedmiot.
     *
     * @param name nazwa przedmiotu
     * @param description opis
     * @return utworzony przedmiot
     */
    public Subject addSubject(String name, String description) {
        String normalizedName = ServiceValidation.requireText(name, "validation.subject.name.required");
        ensureSubjectNameUnique(normalizedName, null);
        Subject subject = new Subject(normalizedName, description);
        data.getSubjects().add(subject);
        return subject;
    }

    /**
     * Aktualizuje przedmiot.
     *
     * @param subjectId identyfikator przedmiotu
     * @param name nowa nazwa
     * @param description nowy opis
     * @return zmodyfikowany przedmiot
     */
    public Subject updateSubject(UUID subjectId, String name, String description) {
        Subject subject = findSubjectById(subjectId);
        String normalizedName = ServiceValidation.requireText(name, "validation.subject.name.required");
        ensureSubjectNameUnique(normalizedName, subjectId);
        subject.setName(normalizedName);
        subject.setDescription(description);
        return subject;
    }

    /**
     * Usuwa przedmiot, jego kryteria i wszystkie powiązane punkty.
     *
     * @param subjectId identyfikator przedmiotu
     */
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

    /**
     * Dodaje kryterium do przedmiotu.
     *
     * @param subjectId identyfikator przedmiotu
     * @param name nazwa kryterium
     * @param maximumPoints maksimum punktów
     * @return utworzone kryterium
     */
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

    /**
     * Aktualizuje kryterium.
     *
     * @param criterionId identyfikator kryterium
     * @param name nowa nazwa
     * @param maximumPoints nowe maksimum punktów
     * @return zmodyfikowane kryterium
     */
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

    /**
     * Usuwa kryterium i powiązane punkty.
     *
     * @param criterionId identyfikator kryterium
     */
    public void deleteCriterion(UUID criterionId) {
        AssessmentCriterion criterion = findCriterionById(criterionId);
        data.getScores().removeIf(score -> score.getCriterionId().equals(criterionId));
        data.getCriteria().remove(criterion);
    }

    /**
     * Wyszukuje przedmiot po identyfikatorze.
     *
     * @param subjectId identyfikator przedmiotu
     * @return znaleziony przedmiot
     */
    public Subject findSubjectById(UUID subjectId) {
        if (subjectId == null) {
            throw new ValidationException("validation.subject.notFound");
        }
        return data.getSubjects().stream()
                .filter(subject -> subject.getId().equals(subjectId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("validation.subject.notFound"));
    }

    /**
     * Wyszukuje kryterium po identyfikatorze.
     *
     * @param criterionId identyfikator kryterium
     * @return znalezione kryterium
     */
    public AssessmentCriterion findCriterionById(UUID criterionId) {
        if (criterionId == null) {
            throw new ValidationException("validation.criterion.notFound");
        }
        return data.getCriteria().stream()
                .filter(criterion -> criterion.getId().equals(criterionId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("validation.criterion.notFound"));
    }

    /**
     * Zwraca uporządkowaną listę przedmiotów.
     *
     * @return lista przedmiotów
     */
    public List<Subject> getAllSubjects() {
        return data.getSubjects().stream()
                .sorted(Comparator.comparing(Subject::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /**
     * Zwraca kryteria wybranego przedmiotu.
     *
     * @param subjectId identyfikator przedmiotu
     * @return lista kryteriów
     */
    public List<AssessmentCriterion> getCriteriaForSubject(UUID subjectId) {
        return data.getCriteria().stream()
                .filter(criterion -> criterion.getSubjectId().equals(subjectId))
                .sorted(Comparator.comparing(
                        AssessmentCriterion::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /**
     * Sprawdza unikalność nazwy przedmiotu.
     *
     * @param name nazwa
     * @param ignoredId identyfikator edytowanego przedmiotu
     */
    private void ensureSubjectNameUnique(String name, UUID ignoredId) {
        boolean duplicate = data.getSubjects().stream()
                .filter(subject -> ignoredId == null || !subject.getId().equals(ignoredId))
                .anyMatch(subject -> subject.getName().equalsIgnoreCase(name));
        if (duplicate) {
            throw new ValidationException("validation.subject.name.duplicate");
        }
    }

    /**
     * Sprawdza unikalność nazwy kryterium w obrębie przedmiotu.
     *
     * @param subjectId identyfikator przedmiotu
     * @param name nazwa kryterium
     * @param ignoredId identyfikator edytowanego kryterium
     */
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
