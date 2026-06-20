package pl.edu.wit.studentmanager.service;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.Student;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Realizuje operacje biznesowe dotyczące studentów.
 */
public final class StudentService {

    /** Wspólny kontener danych aplikacji. */
    private final AppData data;

    /**
     * Tworzy serwis korzystający z podanego kontenera danych.
     *
     * @param data dane aplikacji
     */
    public StudentService(AppData data) {
        this.data = Objects.requireNonNull(data, "Dane nie mogą być puste.");
    }

    /**
     * Dodaje nowego studenta.
     *
     * @param firstName imię
     * @param lastName nazwisko
     * @param albumNumber numer albumu
     * @return utworzony student
     * @throws ValidationException gdy dane są niepoprawne lub numer albumu się powtarza
     */
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

    /**
     * Aktualizuje dane istniejącego studenta.
     *
     * @param studentId identyfikator studenta
     * @param firstName nowe imię
     * @param lastName nowe nazwisko
     * @param albumNumber nowy numer albumu
     * @return zmodyfikowany student
     */
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

    /**
     * Usuwa studenta oraz jego przypisania i punkty.
     *
     * @param studentId identyfikator studenta
     */
    public void deleteStudent(UUID studentId) {
        Student student = findById(studentId);
        data.getAssignments().removeIf(assignment -> assignment.getStudentId().equals(studentId));
        data.getScores().removeIf(score -> score.getStudentId().equals(studentId));
        data.getStudents().remove(student);
    }

    /**
     * Wyszukuje studenta po identyfikatorze.
     *
     * @param studentId identyfikator
     * @return znaleziony student
     * @throws ValidationException gdy student nie istnieje
     */
    public Student findById(UUID studentId) {
        if (studentId == null) {
            throw new ValidationException("validation.student.notFound");
        }
        return data.getStudents().stream()
                .filter(student -> student.getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("validation.student.notFound"));
    }

    /**
     * Zwraca studentów uporządkowanych według nazwiska, imienia i numeru albumu.
     *
     * @return niemodyfikowalna lista studentów
     */
    public List<Student> getAllStudents() {
        return data.getStudents().stream()
                .sorted(Comparator.comparing(Student::getLastName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Student::getFirstName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Student::getAlbumNumber, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /**
     * Sprawdza unikalność numeru albumu.
     *
     * @param albumNumber sprawdzany numer
     * @param ignoredStudentId identyfikator edytowanego studenta lub {@code null}
     */
    private void ensureAlbumUnique(String albumNumber, UUID ignoredStudentId) {
        boolean duplicate = data.getStudents().stream()
                .filter(student -> ignoredStudentId == null || !student.getId().equals(ignoredStudentId))
                .anyMatch(student -> student.getAlbumNumber().equalsIgnoreCase(albumNumber));
        if (duplicate) {
            throw new ValidationException("validation.student.album.duplicate");
        }
    }
}
