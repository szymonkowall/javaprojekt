package pl.edu.wit.studentmanager.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.model.StudentGroup;
import pl.edu.wit.studentmanager.model.StudentScore;
import pl.edu.wit.studentmanager.model.StudentSearchResult;
import pl.edu.wit.studentmanager.model.Subject;


public final class SearchService {

    private final AppData data;


    public SearchService(AppData data) {
        this.data = Objects.requireNonNull(data, "Dane nie mogą być puste.");
    }


    public List<StudentSearchResult> search(String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<StudentSearchResult> results = new ArrayList<>();

        for (Student student : data.getStudents()) {
            String groupCode = findGroup(student).map(StudentGroup::getCode).orElse("");
            List<StudentScore> studentScores = data.getScores().stream()
                    .filter(score -> score.getStudentId().equals(student.getId()))
                    .toList();

            if (studentScores.isEmpty()) {
                StudentSearchResult result = new StudentSearchResult(
                        student.getFirstName() + " " + student.getLastName(),
                        student.getAlbumNumber(),
                        groupCode,
                        "",
                        "",
                        null,
                        null);
                if (matches(result, normalizedQuery)) {
                    results.add(result);
                }
                continue;
            }

            for (StudentScore score : studentScores) {
                AssessmentCriterion criterion = findCriterion(score).orElse(null);
                Subject subject = criterion == null ? null : findSubject(criterion).orElse(null);
                StudentSearchResult result = new StudentSearchResult(
                        student.getFirstName() + " " + student.getLastName(),
                        student.getAlbumNumber(),
                        groupCode,
                        subject == null ? "" : subject.getName(),
                        criterion == null ? "" : criterion.getName(),
                        score.getPoints(),
                        criterion == null ? null : criterion.getMaximumPoints());
                if (matches(result, normalizedQuery)) {
                    results.add(result);
                }
            }
        }

        return results.stream()
                .sorted(Comparator.comparing(StudentSearchResult::getStudentName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(StudentSearchResult::getAlbumNumber, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(StudentSearchResult::getSubjectName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(StudentSearchResult::getCriterionName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }


    private Optional<StudentGroup> findGroup(Student student) {
        return data.getAssignments().stream()
                .filter(assignment -> assignment.getStudentId().equals(student.getId()))
                .findFirst()
                .flatMap(assignment -> data.getGroups().stream()
                        .filter(group -> group.getId().equals(assignment.getGroupId()))
                        .findFirst());
    }


    private Optional<AssessmentCriterion> findCriterion(StudentScore score) {
        return data.getCriteria().stream()
                .filter(criterion -> criterion.getId().equals(score.getCriterionId()))
                .findFirst();
    }


    private Optional<Subject> findSubject(AssessmentCriterion criterion) {
        return data.getSubjects().stream()
                .filter(subject -> subject.getId().equals(criterion.getSubjectId()))
                .findFirst();
    }


    private static boolean matches(StudentSearchResult result, String query) {
        if (query.isEmpty()) {
            return true;
        }
        return contains(result.getStudentName(), query)
                || contains(result.getAlbumNumber(), query)
                || contains(result.getGroupCode(), query)
                || contains(result.getSubjectName(), query)
                || contains(result.getCriterionName(), query);
    }


    private static boolean contains(String value, String query) {
        return value.toLowerCase(Locale.ROOT).contains(query);
    }
}
