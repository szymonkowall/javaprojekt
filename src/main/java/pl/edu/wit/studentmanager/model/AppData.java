package pl.edu.wit.studentmanager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Przechowuje całą bieżącą zawartość aplikacji w pamięci.
 */
public final class AppData {

    /** Lista studentów. */
    private final List<Student> students = new ArrayList<>();

    /** Lista grup. */
    private final List<StudentGroup> groups = new ArrayList<>();

    /** Lista przypisań studentów do grup. */
    private final List<GroupAssignment> assignments = new ArrayList<>();

    /** Lista przedmiotów. */
    private final List<Subject> subjects = new ArrayList<>();

    /** Lista kryteriów oceniania. */
    private final List<AssessmentCriterion> criteria = new ArrayList<>();

    /** Lista punktów studentów. */
    private final List<StudentScore> scores = new ArrayList<>();

    /**
     * Tworzy pusty kontener danych.
     */
    public AppData() {
        // Pusty konstruktor jest potrzebny podczas uruchamiania i odczytu pliku.
    }

    /**
     * Tworzy głęboką kopię danych.
     *
     * @param other kopiowane dane
     */
    public AppData(AppData other) {
        replaceWith(other);
    }

    /**
     * Zwraca modyfikowalną listę studentów używaną przez serwisy.
     *
     * @return lista studentów
     */
    public List<Student> getStudents() {
        return students;
    }

    /**
     * Zwraca modyfikowalną listę grup używaną przez serwisy.
     *
     * @return lista grup
     */
    public List<StudentGroup> getGroups() {
        return groups;
    }

    /**
     * Zwraca modyfikowalną listę przypisań.
     *
     * @return lista przypisań
     */
    public List<GroupAssignment> getAssignments() {
        return assignments;
    }

    /**
     * Zwraca modyfikowalną listę przedmiotów.
     *
     * @return lista przedmiotów
     */
    public List<Subject> getSubjects() {
        return subjects;
    }

    /**
     * Zwraca modyfikowalną listę kryteriów.
     *
     * @return lista kryteriów
     */
    public List<AssessmentCriterion> getCriteria() {
        return criteria;
    }

    /**
     * Zwraca modyfikowalną listę wyników.
     *
     * @return lista wyników
     */
    public List<StudentScore> getScores() {
        return scores;
    }

    /**
     * Zastępuje bieżące dane głęboką kopią przekazanego kontenera.
     *
     * @param other źródło nowych danych
     */
    public void replaceWith(AppData other) {
        Objects.requireNonNull(other, "Dane nie mogą być puste.");
        students.clear();
        groups.clear();
        assignments.clear();
        subjects.clear();
        criteria.clear();
        scores.clear();

        other.students.stream().map(Student::new).forEach(students::add);
        other.groups.stream().map(StudentGroup::new).forEach(groups::add);
        other.assignments.stream().map(GroupAssignment::new).forEach(assignments::add);
        other.subjects.stream().map(Subject::new).forEach(subjects::add);
        other.criteria.stream().map(AssessmentCriterion::new).forEach(criteria::add);
        other.scores.stream().map(StudentScore::new).forEach(scores::add);
    }

    /**
     * Tworzy głęboką kopię danych przeznaczoną między innymi do zapisu w tle.
     *
     * @return niezależna kopia danych
     */
    public AppData deepCopy() {
        return new AppData(this);
    }

    /**
     * Porównuje wszystkie kolekcje danych.
     *
     * @param object porównywany obiekt
     * @return {@code true}, gdy dane są równe
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AppData other)) {
            return false;
        }
        return students.equals(other.students)
                && groups.equals(other.groups)
                && assignments.equals(other.assignments)
                && subjects.equals(other.subjects)
                && criteria.equals(other.criteria)
                && scores.equals(other.scores);
    }

    /**
     * Oblicza kod skrótu całego kontenera.
     *
     * @return kod skrótu
     */
    @Override
    public int hashCode() {
        return Objects.hash(students, groups, assignments, subjects, criteria, scores);
    }
}
