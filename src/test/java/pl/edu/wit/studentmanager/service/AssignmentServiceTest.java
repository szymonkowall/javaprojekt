package pl.edu.wit.studentmanager.service;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.model.StudentGroup;


class AssignmentServiceTest {

    private AppData data;

    private AssignmentService service;

    private Student student;

    private StudentGroup group;

    @BeforeEach
    void setUp() {
        data = new AppData();
        service = new AssignmentService(data);
        student = new Student("Jan", "Nowak", "1");
        group = new StudentGroup("G1", "Java", "");
        data.getStudents().add(student);
        data.getGroups().add(group);
    }

    @Test
    void shouldAssignStudent() {
        service.assignStudent(student.getId(), group.getId());
        assertTrue(service.findAssignmentByStudent(student.getId()).isPresent());
        assertEquals(group, service.findGroupForStudent(student.getId()).orElseThrow());
    }

    @Test
    void shouldRejectSecondAssignment() {
        service.assignStudent(student.getId(), group.getId());
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.assignStudent(student.getId(), group.getId()));
        assertEquals("validation.assignment.studentAlreadyAssigned", exception.getMessage());
    }

    @Test
    void shouldRejectUnknownStudent() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.assignStudent(UUID.randomUUID(), group.getId()));
        assertEquals("validation.student.notFound", exception.getMessage());
    }

    @Test
    void shouldUnassignStudent() {
        service.assignStudent(student.getId(), group.getId());
        service.unassignStudent(student.getId());
        assertFalse(service.findAssignmentByStudent(student.getId()).isPresent());
    }
}
