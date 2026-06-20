package pl.edu.wit.studentmanager.persistence;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.model.GroupAssignment;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.model.StudentGroup;
import pl.edu.wit.studentmanager.model.StudentScore;
import pl.edu.wit.studentmanager.model.Subject;


class BinaryDataRepositoryTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldSaveAndLoadCompleteData() throws IOException {
        AppData original = createCompleteData();
        Path file = temporaryDirectory.resolve("dane.bin");
        BinaryDataRepository repository = new BinaryDataRepository();

        repository.save(file, original);
        AppData loaded = repository.load(file);

        assertEquals(original, loaded);
    }

    @Test
    void shouldRejectInvalidMagicNumber() throws IOException {
        Path file = temporaryDirectory.resolve("invalid.bin");
        try (DataOutputStream output = new DataOutputStream(Files.newOutputStream(file))) {
            output.writeInt(123);
            output.writeInt(1);
        }
        BinaryDataRepository repository = new BinaryDataRepository();
        assertThrows(IOException.class, () -> repository.load(file));
    }

    @Test
    void shouldRejectUnsupportedVersion() throws IOException {
        Path file = temporaryDirectory.resolve("version.bin");
        try (DataOutputStream output = new DataOutputStream(Files.newOutputStream(file))) {
            output.writeInt(0x53544D47);
            output.writeInt(999);
        }
        BinaryDataRepository repository = new BinaryDataRepository();
        assertThrows(IOException.class, () -> repository.load(file));
    }


    private static AppData createCompleteData() {
        AppData data = new AppData();
        Student student = new Student("Łukasz", "Żółć", "000123");
        StudentGroup group = new StudentGroup("GR-1", "Inżynieria oprogramowania", "Opis zażółć");
        Subject subject = new Subject("Język Java", "Programowanie obiektowe");
        AssessmentCriterion criterion = new AssessmentCriterion(subject.getId(), "Kolokwium 1", 20);
        data.getStudents().add(student);
        data.getGroups().add(group);
        data.getAssignments().add(new GroupAssignment(student.getId(), group.getId()));
        data.getSubjects().add(subject);
        data.getCriteria().add(criterion);
        data.getScores().add(new StudentScore(student.getId(), criterion.getId(), 17.5));
        return data;
    }
}
