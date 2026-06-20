package pl.edu.wit.studentmanager.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.Student;


public final class StudentService {


    private final AppData data;


    public StudentService(AppData data) {
        this.data = Objects.requireNonNull(data, "Dane nie mogą być puste.");
    }


    public Student addStudent(String firstName, String lastName, String albumNumber) {
        String normalizedFirstName = ServiceValidation.requireText(
                firstName, "validation.student.firstName.required");
        String normalizedLastName = ServiceValidation.requireText(
                lastName, "validation.student.lastName.required");
        String normalizedAlbum = ServiceValidation.requireText(
                albumNumber, "validation.student.album.required");
        ensureAlbumUnique(normalizedAlbum, null);

        Student student = new Student(normalizedFirstName, normalizedLastName, normalizedAlbum);
        data.getStudents().add(student);
        return student;
    }


    public Student updateStudent(
            UUID studentId,
            String firstName,
            String lastName,
            String albumNumber) {
        Student student = findById(studentId);
        String normalizedFirstName = ServiceValidation.requireText(
                firstName, "validation.student.firstName.required");
        String normalizedLastName = ServiceValidation.requireText(
                lastName, "validation.student.lastName.required");
        String normalizedAlbum = ServiceValidation.requireText(
                albumNumber, "validation.student.album.required");
        ensureAlbumUnique(normalizedAlbum, studentId);

        student.setFirstName(normalizedFirstName);
        student.setLastName(normalizedLastName);
        student.setAlbumNumber(normalizedAlbum);
        return student;
    }


    public void deleteStudent(UUID studentId) {
        Student student = findById(studentId);
        data.getAssignments().removeIf(assignment -> assignment.getStudentId().equals(studentId));
        data.getScores().removeIf(score -> score.getStudentId().equals(studentId));
        data.getStudents().remove(student);
    }


    public Student findById(UUID studentId) {
        if (studentId == null) {
            throw new ValidationException("validation.student.notFound");
        }
        return data.getStudents().stream()
                .filter(student -> student.getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("validation.student.notFound"));
    }


    public List<Student> getAllStudents() {
        return data.getStudents().stream()
                .sorted(Comparator.comparing(Student::getLastName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Student::getFirstName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Student::getAlbumNumber, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }


    private void ensureAlbumUnique(String albumNumber, UUID ignoredStudentId) {
        boolean duplicate = data.getStudents().stream()
                .filter(student -> ignoredStudentId == null || !student.getId().equals(ignoredStudentId))
                .anyMatch(student -> student.getAlbumNumber().equalsIgnoreCase(albumNumber));
        if (duplicate) {
            throw new ValidationException("validation.student.album.duplicate");
        }
    }
}
