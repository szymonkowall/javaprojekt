package pl.edu.wit.studentmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.model.GroupAssignment;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.model.StudentGroup;
import pl.edu.wit.studentmanager.model.StudentScore;
import pl.edu.wit.studentmanager.model.Subject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testy wyszukiwania danych.
 */
class SearchServiceTest {

    /** Testowany serwis. */
    private SearchService service;

    /** Przygotowuje kompletny rekord studenta. */
    @BeforeEach
    void setUp() {
        AppData data = new AppData();
        Student student = new Student("Łukasz", "Żółć", "ABC123");
        StudentGroup group = new StudentGroup("JAVA-1", "Programowanie", "");
        Subject subject = new Subject("Język Java", "");
        AssessmentCriterion criterion = new AssessmentCriterion(subject.getId(), "Kolokwium", 20);
        data.getStudents().add(student);
        data.getGroups().add(group);
        data.getAssignments().add(new GroupAssignment(student.getId(), group.getId()));
        data.getSubjects().add(subject);
        data.getCriteria().add(criterion);
        data.getScores().add(new StudentScore(student.getId(), criterion.getId(), 18));
        service = new SearchService(data);
    }

    /** Sprawdza wyszukiwanie po numerze albumu bez rozróżniania wielkości liter. */
    @Test
    void shouldSearchByAlbumIgnoringCase() {
        var results = service.search("abc123");
        assertEquals(1, results.size());
        assertEquals(Double.valueOf(18.0), results.get(0).getPoints());
    }

    /** Sprawdza wyszukiwanie po przedmiocie i grupie. */
    @Test
    void shouldSearchBySubjectAndGroup() {
        assertEquals(1, service.search("język java").size());
        assertEquals(1, service.search("java-1").size());
    }

    /** Sprawdza pusty wynik dla nieistniejącej frazy. */
    @Test
    void shouldReturnEmptyListForUnknownText() {
        assertTrue(service.search("nieistniejące").isEmpty());
    }
}
