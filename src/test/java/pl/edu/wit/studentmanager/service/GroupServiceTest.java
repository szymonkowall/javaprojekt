package pl.edu.wit.studentmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.GroupAssignment;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.model.StudentGroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testy operacji na grupach.
 */
class GroupServiceTest {

    /** Dane testowe. */
    private AppData data;

    /** Testowany serwis. */
    private GroupService service;

    /** Przygotowuje test. */
    @BeforeEach
    void setUp() {
        data = new AppData();
        service = new GroupService(data);
    }

    /** Sprawdza dodawanie grupy. */
    @Test
    void shouldAddGroup() {
        StudentGroup group = service.addGroup("WIT-1", "Inżynieria", "Opis");
        assertEquals("WIT-1", group.getCode());
        assertEquals(1, data.getGroups().size());
    }

    /** Sprawdza unikalność kodu grupy. */
    @Test
    void shouldRejectDuplicateGroupCode() {
        service.addGroup("WIT-1", "A", "");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.addGroup("wit-1", "B", ""));
        assertEquals("validation.group.code.duplicate", exception.getMessage());
    }

    /** Sprawdza aktualizację grupy. */
    @Test
    void shouldUpdateGroup() {
        StudentGroup group = service.addGroup("G1", "A", "X");
        service.updateGroup(group.getId(), "G2", "B", "Y");
        assertEquals("G2", group.getCode());
        assertEquals("B", group.getSpecialization());
        assertEquals("Y", group.getDescription());
    }

    /** Sprawdza usunięcie przypisań do usuwanej grupy. */
    @Test
    void shouldDeleteGroupAssignments() {
        StudentGroup group = service.addGroup("G1", "A", "");
        Student student = new Student("Jan", "Nowak", "1");
        data.getStudents().add(student);
        data.getAssignments().add(new GroupAssignment(student.getId(), group.getId()));

        service.deleteGroup(group.getId());

        assertTrue(data.getGroups().isEmpty());
        assertTrue(data.getAssignments().isEmpty());
    }
}
