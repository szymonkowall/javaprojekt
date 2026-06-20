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


class ScoreServiceTest {

    private AppData data;

    private ScoreService service;

    private Student student;

    private AssessmentCriterion criterion;

    @BeforeEach
    void setUp() {
        data = new AppData();
        service = new ScoreService(data);
        student = new Student("Jan", "Nowak", "1");
        Subject subject = new Subject("Java", "");
        criterion = new AssessmentCriterion(subject.getId(), "Kolokwium", 20);
        data.getStudents().add(student);
        data.getSubjects().add(subject);
        data.getCriteria().add(criterion);
    }

    @Test
    void shouldAcceptBoundaryValues() {
        StudentScore zero = service.addScore(student.getId(), criterion.getId(), 0);
        assertEquals(0, zero.getPoints());
        service.deleteScore(zero.getId());
        StudentScore maximum = service.addScore(student.getId(), criterion.getId(), 20);
        assertEquals(20, maximum.getPoints());
    }

    @Test
    void shouldRejectNegativePoints() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.addScore(student.getId(), criterion.getId(), -0.01));
        assertEquals("validation.score.nonNegative", exception.getMessage());
    }

    @Test
    void shouldRejectPointsAboveMaximum() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.addScore(student.getId(), criterion.getId(), 20.01));
        assertEquals("validation.score.aboveMaximum", exception.getMessage());
    }

    @Test
    void shouldRejectNonFinitePoints() {
        assertThrows(ValidationException.class,
                () -> service.addScore(student.getId(), criterion.getId(), Double.NaN));
        assertThrows(ValidationException.class,
                () -> service.addScore(student.getId(), criterion.getId(), Double.POSITIVE_INFINITY));
    }

    @Test
    void shouldRejectDuplicateScore() {
        service.addScore(student.getId(), criterion.getId(), 10);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.addScore(student.getId(), criterion.getId(), 11));
        assertEquals("validation.score.duplicate", exception.getMessage());
    }

    @Test
    void shouldUpdateAndDeleteScore() {
        StudentScore score = service.addScore(student.getId(), criterion.getId(), 10);
        service.updateScore(score.getId(), 15);
        assertEquals(15, score.getPoints());
        service.deleteScore(score.getId());
        assertTrue(data.getScores().isEmpty());
    }
}
