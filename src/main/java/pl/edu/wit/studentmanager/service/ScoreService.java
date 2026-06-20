package pl.edu.wit.studentmanager.service;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.model.StudentScore;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Zarządza punktami zdobytymi przez studentów.
 */
public final class ScoreService {

    /** Wspólny kontener danych aplikacji. */
    private final AppData data;

    /**
     * Tworzy serwis punktów.
     *
     * @param data dane aplikacji
     */
    public ScoreService(AppData data) {
        this.data = Objects.requireNonNull(data, "Dane nie mogą być puste.");
    }

    /**
     * Dodaje punkty studenta dla kryterium.
     *
     * @param studentId identyfikator studenta
     * @param criterionId identyfikator kryterium
     * @param points liczba punktów
     * @return utworzony wynik
     */
    public StudentScore addScore(UUID studentId, UUID criterionId, double points) {
        ensureStudentExists(studentId);
        AssessmentCriterion criterion = findCriterion(criterionId);
        validatePoints(points, criterion);
        ensureScoreUnique(studentId, criterionId, null);

        StudentScore score = new StudentScore(studentId, criterionId, points);
        data.getScores().add(score);
        return score;
    }

    /**
     * Aktualizuje istniejący wynik. Student i kryterium pozostają niezmienne.
     *
     * @param scoreId identyfikator wyniku
     * @param points nowa liczba punktów
     * @return zmodyfikowany wynik
     */
    public StudentScore updateScore(UUID scoreId, double points) {
        StudentScore score = findById(scoreId);
        AssessmentCriterion criterion = findCriterion(score.getCriterionId());
        validatePoints(points, criterion);
        score.setPoints(points);
        return score;
    }

    /**
     * Usuwa wynik.
     *
     * @param scoreId identyfikator wyniku
     */
    public void deleteScore(UUID scoreId) {
        data.getScores().remove(findById(scoreId));
    }

    /**
     * Wyszukuje wynik po identyfikatorze.
     *
     * @param scoreId identyfikator wyniku
     * @return znaleziony wynik
     */
    public StudentScore findById(UUID scoreId) {
        if (scoreId == null) {
            throw new ValidationException("validation.score.notFound");
        }
        return data.getScores().stream()
                .filter(score -> score.getId().equals(scoreId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("validation.score.notFound"));
    }

    /**
     * Zwraca wszystkie wyniki w stabilnej kolejności identyfikatorów.
     *
     * @return lista wyników
     */
    public List<StudentScore> getAllScores() {
        return data.getScores().stream()
                .sorted(Comparator.comparing(score -> score.getId().toString()))
                .toList();
    }

    /**
     * Zwraca wyniki konkretnego studenta.
     *
     * @param studentId identyfikator studenta
     * @return lista wyników
     */
    public List<StudentScore> getScoresForStudent(UUID studentId) {
        return data.getScores().stream()
                .filter(score -> score.getStudentId().equals(studentId))
                .toList();
    }

    /**
     * Sprawdza punkty względem reguł i maksimum kryterium.
     *
     * @param points punkty
     * @param criterion kryterium
     */
    private static void validatePoints(double points, AssessmentCriterion criterion) {
        ServiceValidation.requireNonNegativeFinite(points, "validation.score.nonNegative");
        if (points > criterion.getMaximumPoints()) {
            throw new ValidationException("validation.score.aboveMaximum");
        }
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
     * Wyszukuje kryterium.
     *
     * @param criterionId identyfikator kryterium
     * @return kryterium
     */
    private AssessmentCriterion findCriterion(UUID criterionId) {
        if (criterionId == null) {
            throw new ValidationException("validation.criterion.notFound");
        }
        return data.getCriteria().stream()
                .filter(criterion -> criterion.getId().equals(criterionId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("validation.criterion.notFound"));
    }

    /**
     * Sprawdza, czy student nie ma już wyniku dla tego samego kryterium.
     *
     * @param studentId identyfikator studenta
     * @param criterionId identyfikator kryterium
     * @param ignoredScoreId identyfikator ignorowanego wyniku
     */
    private void ensureScoreUnique(UUID studentId, UUID criterionId, UUID ignoredScoreId) {
        boolean duplicate = data.getScores().stream()
                .filter(score -> ignoredScoreId == null || !score.getId().equals(ignoredScoreId))
                .anyMatch(score -> score.getStudentId().equals(studentId)
                        && score.getCriterionId().equals(criterionId));
        if (duplicate) {
            throw new ValidationException("validation.score.duplicate");
        }
    }
}
