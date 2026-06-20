package pl.edu.wit.studentmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.model.StudentGroup;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testy przypisywania studentów do grup.
 */
class AssignmentServiceTest {

    /** Dane testowe. */
    private AppData data;

    /** Testowany serwis. */
    private AssignmentService service;

    /** Student testowy. */
    private Student student;

    /** Grupa testowa. */
    private StudentGroup group;

    /** Przygotowuje obiekty testowe. */
    @BeforeEach
    void setUp() {
        data = new AppData();
        service = new AssignmentService(data);
        student = new Student("Jan", "Nowak", "1");
        group = new StudentGroup("G1", "Java", "");
        data.getStudents().add(student);
        data.getGroups().add(group);
    }

    /** Sprawdza przypisanie studenta. */
    @Test
    void shouldAssignStudent() {
        service.assignStudent(student.getId(), group.getId());
        assertTrue(service.findAssignmentByStudent(student.getId()).isPresent());
        assertEquals(group, service.findGroupForStudent(student.getId()).orElseThrow());
    }

    /** Sprawdza zakaz podwójnego przypisania. */
    @Test
    void shouldRejectSecondAssignment() {
        service.assignStudent(student.getId(), group.getId());
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.assignStudent(student.getId(), group.getId()));
        assertEquals("validation.assignment.studentAlreadyAssigned", exception.getMessage());
    }

    /** Sprawdza odrzucenie nieistniejącego studenta. */
    @Test
    void shouldRejectUnknownStudent() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.assignStudent(UUID.randomUUID(), group.getId()));
        assertEquals("validation.student.notFound", exception.getMessage());
    }

    /** Sprawdza usunięcie przypisania. */
    @Test
    void shouldUnassignStudent() {
        service.assignStudent(student.getId(), group.getId());
        service.unassignStudent(student.getId());
        assertFalse(service.findAssignmentByStudent(student.getId()).isPresent());
    }
}
