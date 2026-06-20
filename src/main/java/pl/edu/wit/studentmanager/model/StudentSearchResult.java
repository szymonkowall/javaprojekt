package pl.edu.wit.studentmanager.model;

import java.util.Objects;


public final class StudentSearchResult {


    private final String studentName;


    private final String albumNumber;


    private final String groupCode;


    private final String subjectName;


    private final String criterionName;


    private final Double points;


    private final Double maximumPoints;


    public StudentSearchResult(
            String studentName,
            String albumNumber,
            String groupCode,
            String subjectName,
            String criterionName,
            Double points,
            Double maximumPoints) {
        this.studentName = Objects.requireNonNull(studentName);
        this.albumNumber = Objects.requireNonNull(albumNumber);
        this.groupCode = Objects.requireNonNull(groupCode);
        this.subjectName = Objects.requireNonNull(subjectName);
        this.criterionName = Objects.requireNonNull(criterionName);
        this.points = points;
        this.maximumPoints = maximumPoints;
    }


    public String getStudentName() {
        return studentName;
    }


    public String getAlbumNumber() {
        return albumNumber;
    }


    public String getGroupCode() {
        return groupCode;
    }


    public String getSubjectName() {
        return subjectName;
    }


    public String getCriterionName() {
        return criterionName;
    }

 
    public Double getPoints() {
        return points;
    }


    public Double getMaximumPoints() {
        return maximumPoints;
    }
}
