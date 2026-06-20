package pl.edu.wit.studentmanager.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.model.GroupAssignment;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.model.StudentGroup;
import pl.edu.wit.studentmanager.model.StudentScore;
import pl.edu.wit.studentmanager.model.Subject;


public final class BinaryDataRepository implements DataRepository {

    public BinaryDataRepository() {
    }

    @Override
    public void save(Path path, AppData data) throws IOException {
        if (path == null || data == null) {
            throw new IllegalArgumentException("Ścieżka ani dane nie mogą być puste.");
        }

        // Proste sprawdzenie i utworzenie folderu nadrzędnego
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }


        try (DataOutputStream output = new DataOutputStream(
                new BufferedOutputStream(Files.newOutputStream(path)))) {
            
            writeStudents(output, data);
            writeGroups(output, data);
            writeAssignments(output, data);
            writeSubjects(output, data);
            writeCriteria(output, data);
            writeScores(output, data);
        }
    }

    @Override
    public AppData load(Path path) throws IOException {
        if (path == null || !Files.exists(path)) {
            throw new IOException("Wskazany plik nie istnieje.");
        }

        AppData data = new AppData();

        try (DataInputStream input = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(path)))) {
            
            readStudents(input, data);
            readGroups(input, data);
            readAssignments(input, data);
            readSubjects(input, data);
            readCriteria(input, data);
            readScores(input, data);
            
            return data;
        } catch (EOFException exception) {
            throw new IOException("Nieoczekiwany koniec pliku.", exception);
        }
    }

    private void writeStudents(DataOutputStream output, AppData data) throws IOException {
        output.writeInt(data.getStudents().size());
        for (Student student : data.getStudents()) {
            output.writeUTF(student.getId().toString()); // Używamy wbudowanego writeUTF zamiast ręcznych tablic bajtów
            output.writeUTF(student.getFirstName());
            output.writeUTF(student.getLastName());
            output.writeUTF(student.getAlbumNumber());
        }
    }

    private void readStudents(DataInputStream input, AppData data) throws IOException {
        int count = input.readInt();
        for (int i = 0; i < count; i++) {
            UUID id = UUID.fromString(input.readUTF());
            String firstName = input.readUTF();
            String lastName = input.readUTF();
            String albumNumber = input.readUTF();
            data.getStudents().add(new Student(id, firstName, lastName, albumNumber));
        }
    }

    private void writeGroups(DataOutputStream output, AppData data) throws IOException {
        output.writeInt(data.getGroups().size());
        for (StudentGroup group : data.getGroups()) {
            output.writeUTF(group.getId().toString());
            output.writeUTF(group.getCode());
            output.writeUTF(group.getSpecialization());
            output.writeUTF(group.getDescription());
        }
    }

    private void readGroups(DataInputStream input, AppData data) throws IOException {
        int count = input.readInt();
        for (int i = 0; i < count; i++) {
            data.getGroups().add(new StudentGroup(
                    UUID.fromString(input.readUTF()), 
                    input.readUTF(), 
                    input.readUTF(), 
                    input.readUTF()));
        }
    }

    private void writeAssignments(DataOutputStream output, AppData data) throws IOException {
        output.writeInt(data.getAssignments().size());
        for (GroupAssignment assignment : data.getAssignments()) {
            output.writeUTF(assignment.getId().toString());
            output.writeUTF(assignment.getStudentId().toString());
            output.writeUTF(assignment.getGroupId().toString());
        }
    }

    private void readAssignments(DataInputStream input, AppData data) throws IOException {
        int count = input.readInt();
        for (int i = 0; i < count; i++) {
            data.getAssignments().add(new GroupAssignment(
                    UUID.fromString(input.readUTF()), 
                    UUID.fromString(input.readUTF()), 
                    UUID.fromString(input.readUTF())));
        }
    }

    private void writeSubjects(DataOutputStream output, AppData data) throws IOException {
        output.writeInt(data.getSubjects().size());
        for (Subject subject : data.getSubjects()) {
            output.writeUTF(subject.getId().toString());
            output.writeUTF(subject.getName());
            output.writeUTF(subject.getDescription());
        }
    }

    private void readSubjects(DataInputStream input, AppData data) throws IOException {
        int count = input.readInt();
        for (int i = 0; i < count; i++) {
            data.getSubjects().add(new Subject(
                    UUID.fromString(input.readUTF()), 
                    input.readUTF(), 
                    input.readUTF()));
        }
    }

    private void writeCriteria(DataOutputStream output, AppData data) throws IOException {
        output.writeInt(data.getCriteria().size());
        for (AssessmentCriterion criterion : data.getCriteria()) {
            output.writeUTF(criterion.getId().toString());
            output.writeUTF(criterion.getSubjectId().toString());
            output.writeUTF(criterion.getName());
            output.writeDouble(criterion.getMaximumPoints());
        }
    }

    private void readCriteria(DataInputStream input, AppData data) throws IOException {
        int count = input.readInt();
        for (int i = 0; i < count; i++) {
            data.getCriteria().add(new AssessmentCriterion(
                    UUID.fromString(input.readUTF()), 
                    UUID.fromString(input.readUTF()), 
                    input.readUTF(), 
                    input.readDouble()));
        }
    }

    private void writeScores(DataOutputStream output, AppData data) throws IOException {
        output.writeInt(data.getScores().size());
        for (StudentScore score : data.getScores()) {
            output.writeUTF(score.getId().toString());
            output.writeUTF(score.getStudentId().toString());
            output.writeUTF(score.getCriterionId().toString());
            output.writeDouble(score.getPoints());
        }
    }

    private void readScores(DataInputStream input, AppData data) throws IOException {
        int count = input.readInt();
        for (int i = 0; i < count; i++) {
            data.getScores().add(new StudentScore(
                    UUID.fromString(input.readUTF()), 
                    UUID.fromString(input.readUTF()), 
                    UUID.fromString(input.readUTF()), 
                    input.readDouble()));
        }
    }
}