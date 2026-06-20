package pl.edu.wit.studentmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.edu.wit.studentmanager.exception.ValidationException;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.model.StudentScore;
import pl.edu.wit.studentmanager.model.Subject;


class SubjectServiceTest {

    private AppData data;

    private SubjectService service;

    @BeforeEach
    void setUp() {
        data = new AppData();
        service = new SubjectService(data);
    }

    @Test
    void shouldAddSubjectAndCriterion() {
        Subject subject = service.addSubject("Język Java", "Opis");
        AssessmentCriterion criterion = service.addCriterion(subject.getId(), "Kolokwium", 20);
        assertEquals(subject.getId(), criterion.getSubjectId());
        assertEquals(1, data.getCriteria().size());
    }

    @Test
    void shouldRejectNonPositiveMaximum() {
        Subject subject = service.addSubject("Java", "");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.addCriterion(subject.getId(), "Test", 0));
        assertEquals("validation.criterion.maximum.positive", exception.getMessage());
    }

    @Test
    void shouldRejectMaximumBelowExistingScore() {
        Subject subject = service.addSubject("Java", "");
        AssessmentCriterion criterion = service.addCriterion(subject.getId(), "Test", 20);
        Student student = new Student("Jan", "Nowak", "1");
        data.getStudents().add(student);
        data.getScores().add(new StudentScore(student.getId(), criterion.getId(), 15));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.updateCriterion(criterion.getId(), "Test", 10));
        assertEquals("validation.criterion.maximum.belowExistingScore", exception.getMessage());
    }

    @Test
    void shouldDeleteSubjectWithCriteriaAndScores() {
        Subject subject = service.addSubject("Java", "");
        AssessmentCriterion criterion = service.addCriterion(subject.getId(), "Test", 20);
        Student student = new Student("Jan", "Nowak", "1");
        data.getStudents().add(student);
        data.getScores().add(new StudentScore(student.getId(), criterion.getId(), 10));

        service.deleteSubject(subject.getId());

        assertTrue(data.getSubjects().isEmpty());
        assertTrue(data.getCriteria().isEmpty());
        assertTrue(data.getScores().isEmpty());
    }
}
