package pl.edu.wit.studentmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.model.GroupAssignment;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.model.StudentScore;
import pl.edu.wit.studentmanager.model.Subject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testy operacji wykonywanych na studentach.
 */
class StudentServiceTest {

    /** Dane używane w testach. */
    private AppData data;

    /** Testowany serwis. */
    private StudentService service;

    /** Przygotowuje pusty zestaw danych przed każdym testem. */
    @BeforeEach
    void setUp() {
        data = new AppData();
        service = new StudentService(data);
    }

    /** Sprawdza dodanie poprawnego studenta. */
    @Test
    void shouldAddStudent() {
        Student student = service.addStudent(" Jan ", " Kowalski ", " 12345 ");
        assertEquals("Jan", student.getFirstName());
        assertEquals("Kowalski", student.getLastName());
        assertEquals("12345", student.getAlbumNumber());
        assertEquals(1, data.getStudents().size());
    }

    /** Sprawdza odrzucenie pustego imienia. */
    @Test
    void shouldRejectBlankFirstName() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.addStudent(" ", "Kowalski", "12345"));
        assertEquals("validation.student.firstName.required", exception.getMessage());
    }

    /** Sprawdza unikalność numeru albumu bez rozróżniania wielkości liter. */
    @Test
    void shouldRejectDuplicateAlbumNumber() {
        service.addStudent("Jan", "Kowalski", "AbC123");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.addStudent("Anna", "Nowak", "abc123"));
        assertEquals("validation.student.album.duplicate", exception.getMessage());
    }

    /** Sprawdza aktualizację studenta. */
    @Test
    void shouldUpdateStudent() {
        Student student = service.addStudent("Jan", "Kowalski", "123");
        service.updateStudent(student.getId(), "Adam", "Nowak", "999");
        assertEquals("Adam", student.getFirstName());
        assertEquals("Nowak", student.getLastName());
        assertEquals("999", student.getAlbumNumber());
    }

    /** Sprawdza kaskadowe usunięcie przypisań i punktów studenta. */
    @Test
    void shouldDeleteStudentWithAssignmentsAndScores() {
        Student student = service.addStudent("Jan", "Kowalski", "123");
        var groupId = java.util.UUID.randomUUID();
        var subject = new Subject("Java", "");
        var criterion = new AssessmentCriterion(subject.getId(), "Test", 20);
        data.getAssignments().add(new GroupAssignment(student.getId(), groupId));
        data.getSubjects().add(subject);
        data.getCriteria().add(criterion);
        data.getScores().add(new StudentScore(student.getId(), criterion.getId(), 10));

        service.deleteStudent(student.getId());

        assertTrue(data.getStudents().isEmpty());
        assertTrue(data.getAssignments().isEmpty());
        assertTrue(data.getScores().isEmpty());
    }
}
