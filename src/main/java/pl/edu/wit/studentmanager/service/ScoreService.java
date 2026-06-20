package pl.edu.wit.studentmanager.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.model.StudentScore;


public final class ScoreService {


    private final AppData data;


    public ScoreService(AppData data) {
        this.data = Objects.requireNonNull(data, "Dane nie mogą być puste.");
    }


    public StudentScore addScore(UUID studentId, UUID criterionId, double points) {
        ensureStudentExists(studentId);
        AssessmentCriterion criterion = findCriterion(criterionId);
        validatePoints(points, criterion);
        ensureScoreUnique(studentId, criterionId, null);

        StudentScore score = new StudentScore(studentId, criterionId, points);
        data.getScores().add(score);
        return score;
    }


    public StudentScore updateScore(UUID scoreId, double points) {
        StudentScore score = findById(scoreId);
        AssessmentCriterion criterion = findCriterion(score.getCriterionId());
        validatePoints(points, criterion);
        score.setPoints(points);
        return score;
    }


    public void deleteScore(UUID scoreId) {
        data.getScores().remove(findById(scoreId));
    }


    public StudentScore findById(UUID scoreId) {
        if (scoreId == null) {
            throw new ValidationException("validation.score.notFound");
        }
        return data.getScores().stream()
                .filter(score -> score.getId().equals(scoreId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("validation.score.notFound"));
    }


    public List<StudentScore> getAllScores() {
        return data.getScores().stream()
                .sorted(Comparator.comparing(score -> score.getId().toString()))
                .toList();
    }


    public List<StudentScore> getScoresForStudent(UUID studentId) {
        return data.getScores().stream()
                .filter(score -> score.getStudentId().equals(studentId))
                .toList();
    }


    private static void validatePoints(double points, AssessmentCriterion criterion) {
        ServiceValidation.requireNonNegativeFinite(points, "validation.score.nonNegative");
        if (points > criterion.getMaximumPoints()) {
            throw new ValidationException("validation.score.aboveMaximum");
        }
    }


    private void ensureStudentExists(UUID studentId) {
        boolean exists = studentId != null && data.getStudents().stream()
                .anyMatch(student -> student.getId().equals(studentId));
        if (!exists) {
            throw new ValidationException("validation.student.notFound");
        }
    }


    private AssessmentCriterion findCriterion(UUID criterionId) {
        if (criterionId == null) {
            throw new ValidationException("validation.criterion.notFound");
        }
        return data.getCriteria().stream()
                .filter(criterion -> criterion.getId().equals(criterionId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("validation.criterion.notFound"));
    }


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
