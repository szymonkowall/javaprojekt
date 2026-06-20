package pl.edu.wit.studentmanager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public final class AppData {


    private final List<Student> students = new ArrayList<>();


    private final List<StudentGroup> groups = new ArrayList<>();


    private final List<GroupAssignment> assignments = new ArrayList<>();


    private final List<Subject> subjects = new ArrayList<>();


    private final List<AssessmentCriterion> criteria = new ArrayList<>();


    private final List<StudentScore> scores = new ArrayList<>();


    public AppData() {

    }


    public AppData(AppData other) {
        replaceWith(other);
    }


    public List<Student> getStudents() {
        return students;
    }


    public List<StudentGroup> getGroups() {
        return groups;
    }


    public List<GroupAssignment> getAssignments() {
        return assignments;
    }


    public List<Subject> getSubjects() {
        return subjects;
    }


    public List<AssessmentCriterion> getCriteria() {
        return criteria;
    }


    public List<StudentScore> getScores() {
        return scores;
    }

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


    public AppData deepCopy() {
        return new AppData(this);
    }


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


    @Override
    public int hashCode() {
        return Objects.hash(students, groups, assignments, subjects, criteria, scores);
    }
}
