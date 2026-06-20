package pl.edu.wit.studentmanager.persistence;

import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.model.GroupAssignment;
import pl.edu.wit.studentmanager.model.Student;
import pl.edu.wit.studentmanager.model.StudentGroup;
import pl.edu.wit.studentmanager.model.StudentScore;
import pl.edu.wit.studentmanager.model.Subject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

/**
 * Zapisuje i odczytuje dane w kontrolowanym formacie binarnym przy użyciu
 * {@link DataOutputStream} i {@link DataInputStream}.
 */
public final class BinaryDataRepository implements DataRepository {

    /**
     * Tworzy repozytorium binarne.
     */
    public BinaryDataRepository() {
        // Repozytorium nie wymaga dodatkowej konfiguracji.
    }

    /** Sygnatura identyfikująca pliki aplikacji. */
    private static final int MAGIC = 0x53544D47;

    /** Aktualna wersja formatu pliku. */
    private static final int VERSION = 1;

    /** Maksymalna akceptowana liczba elementów jednej kolekcji. */
    private static final int MAX_COLLECTION_SIZE = 1_000_000;

    /** Maksymalna długość pojedynczego tekstu w bajtach. */
    private static final int MAX_STRING_BYTES = 10_000_000;

    /**
     * Zapisuje pełną zawartość aplikacji do pliku tymczasowego, a następnie
     * zastępuje plik docelowy. Ogranicza to ryzyko pozostawienia częściowego pliku.
     *
     * @param path docelowa ścieżka
     * @param data dane do zapisania
     * @throws IOException gdy zapis się nie powiedzie
     */
    @Override
    public void save(Path path, AppData data) throws IOException {
        Objects.requireNonNull(path, "Ścieżka nie może być pusta.");
        Objects.requireNonNull(data, "Dane nie mogą być puste.");

        Path absolutePath = path.toAbsolutePath();
        Path parent = absolutePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path temporaryPath = Files.createTempFile(parent, "student-manager-", ".tmp");

        boolean completed = false;
        try (DataOutputStream output = new DataOutputStream(
                new BufferedOutputStream(Files.newOutputStream(temporaryPath)))) {
            output.writeInt(MAGIC);
            output.writeInt(VERSION);
            writeStudents(output, data);
            writeGroups(output, data);
            writeAssignments(output, data);
            writeSubjects(output, data);
            writeCriteria(output, data);
            writeScores(output, data);
            output.flush();
            completed = true;
        } finally {
            if (!completed) {
                Files.deleteIfExists(temporaryPath);
            }
        }

        try {
            Files.move(temporaryPath, absolutePath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryPath, absolutePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Odczytuje pełną zawartość aplikacji z pliku.
     *
     * @param path ścieżka pliku
     * @return odczytane dane
     * @throws IOException gdy format pliku jest błędny
     */
    @Override
    public AppData load(Path path) throws IOException {
        Objects.requireNonNull(path, "Ścieżka nie może być pusta.");
        try (DataInputStream input = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(path)))) {
            int magic = input.readInt();
            int version = input.readInt();
            if (magic != MAGIC) {
                throw new IOException("Nieprawidłowy format pliku danych.");
            }
            if (version != VERSION) {
                throw new IOException("Nieobsługiwana wersja pliku danych: " + version);
            }

            AppData data = new AppData();
            readStudents(input, data);
            readGroups(input, data);
            readAssignments(input, data);
            readSubjects(input, data);
            readCriteria(input, data);
            readScores(input, data);
            return data;
        } catch (EOFException exception) {
            throw new IOException("Plik danych jest niekompletny.", exception);
        } catch (IllegalArgumentException exception) {
            throw new IOException("Plik zawiera niepoprawne dane.", exception);
        }
    }

    /**
     * Zapisuje listę studentów.
     *
     * @param output strumień wyjściowy
     * @param data dane aplikacji
     * @throws IOException gdy zapis się nie powiedzie
     */
    private static void writeStudents(DataOutputStream output, AppData data) throws IOException {
        output.writeInt(data.getStudents().size());
        for (Student student : data.getStudents()) {
            writeUuid(output, student.getId());
            writeString(output, student.getFirstName());
            writeString(output, student.getLastName());
            writeString(output, student.getAlbumNumber());
        }
    }

    /**
     * Odczytuje listę studentów.
     *
     * @param input strumień wejściowy
     * @param data docelowe dane aplikacji
     * @throws IOException gdy odczyt się nie powiedzie
     */
    private static void readStudents(DataInputStream input, AppData data) throws IOException {
        int count = readCount(input, "studentów");
        for (int index = 0; index < count; index++) {
            data.getStudents().add(new Student(
                    readUuid(input), readString(input), readString(input), readString(input)));
        }
    }

    /**
     * Zapisuje listę grup.
     *
     * @param output strumień wyjściowy
     * @param data dane aplikacji
     * @throws IOException gdy zapis się nie powiedzie
     */
    private static void writeGroups(DataOutputStream output, AppData data) throws IOException {
        output.writeInt(data.getGroups().size());
        for (StudentGroup group : data.getGroups()) {
            writeUuid(output, group.getId());
            writeString(output, group.getCode());
            writeString(output, group.getSpecialization());
            writeString(output, group.getDescription());
        }
    }

    /**
     * Odczytuje listę grup.
     *
     * @param input strumień wejściowy
     * @param data docelowe dane aplikacji
     * @throws IOException gdy odczyt się nie powiedzie
     */
    private static void readGroups(DataInputStream input, AppData data) throws IOException {
        int count = readCount(input, "grup");
        for (int index = 0; index < count; index++) {
            data.getGroups().add(new StudentGroup(
                    readUuid(input), readString(input), readString(input), readString(input)));
        }
    }

    /**
     * Zapisuje listę przypisań studentów do grup.
     *
     * @param output strumień wyjściowy
     * @param data dane aplikacji
     * @throws IOException gdy zapis się nie powiedzie
     */
    private static void writeAssignments(DataOutputStream output, AppData data) throws IOException {
        output.writeInt(data.getAssignments().size());
        for (GroupAssignment assignment : data.getAssignments()) {
            writeUuid(output, assignment.getId());
            writeUuid(output, assignment.getStudentId());
            writeUuid(output, assignment.getGroupId());
        }
    }

    /**
     * Odczytuje listę przypisań studentów do grup.
     *
     * @param input strumień wejściowy
     * @param data docelowe dane aplikacji
     * @throws IOException gdy odczyt się nie powiedzie
     */
    private static void readAssignments(DataInputStream input, AppData data) throws IOException {
        int count = readCount(input, "przypisań");
        for (int index = 0; index < count; index++) {
            data.getAssignments().add(new GroupAssignment(
                    readUuid(input), readUuid(input), readUuid(input)));
        }
    }

    /**
     * Zapisuje listę przedmiotów.
     *
     * @param output strumień wyjściowy
     * @param data dane aplikacji
     * @throws IOException gdy zapis się nie powiedzie
     */
    private static void writeSubjects(DataOutputStream output, AppData data) throws IOException {
        output.writeInt(data.getSubjects().size());
        for (Subject subject : data.getSubjects()) {
            writeUuid(output, subject.getId());
            writeString(output, subject.getName());
            writeString(output, subject.getDescription());
        }
    }

    /**
     * Odczytuje listę przedmiotów.
     *
     * @param input strumień wejściowy
     * @param data docelowe dane aplikacji
     * @throws IOException gdy odczyt się nie powiedzie
     */
    private static void readSubjects(DataInputStream input, AppData data) throws IOException {
        int count = readCount(input, "przedmiotów");
        for (int index = 0; index < count; index++) {
            data.getSubjects().add(new Subject(
                    readUuid(input), readString(input), readString(input)));
        }
    }

    /**
     * Zapisuje listę kryteriów.
     *
     * @param output strumień wyjściowy
     * @param data dane aplikacji
     * @throws IOException gdy zapis się nie powiedzie
     */
    private static void writeCriteria(DataOutputStream output, AppData data) throws IOException {
        output.writeInt(data.getCriteria().size());
        for (AssessmentCriterion criterion : data.getCriteria()) {
            writeUuid(output, criterion.getId());
            writeUuid(output, criterion.getSubjectId());
            writeString(output, criterion.getName());
            output.writeDouble(criterion.getMaximumPoints());
        }
    }

    /**
     * Odczytuje listę kryteriów.
     *
     * @param input strumień wejściowy
     * @param data docelowe dane aplikacji
     * @throws IOException gdy odczyt się nie powiedzie
     */
    private static void readCriteria(DataInputStream input, AppData data) throws IOException {
        int count = readCount(input, "kryteriów");
        for (int index = 0; index < count; index++) {
            data.getCriteria().add(new AssessmentCriterion(
                    readUuid(input), readUuid(input), readString(input), input.readDouble()));
        }
    }

    /**
     * Zapisuje listę wyników.
     *
     * @param output strumień wyjściowy
     * @param data dane aplikacji
     * @throws IOException gdy zapis się nie powiedzie
     */
    private static void writeScores(DataOutputStream output, AppData data) throws IOException {
        output.writeInt(data.getScores().size());
        for (StudentScore score : data.getScores()) {
            writeUuid(output, score.getId());
            writeUuid(output, score.getStudentId());
            writeUuid(output, score.getCriterionId());
            output.writeDouble(score.getPoints());
        }
    }

    /**
     * Odczytuje listę wyników.
     *
     * @param input strumień wejściowy
     * @param data docelowe dane aplikacji
     * @throws IOException gdy odczyt się nie powiedzie
     */
    private static void readScores(DataInputStream input, AppData data) throws IOException {
        int count = readCount(input, "wyników");
        for (int index = 0; index < count; index++) {
            data.getScores().add(new StudentScore(
                    readUuid(input), readUuid(input), readUuid(input), input.readDouble()));
        }
    }

    /**
     * Zapisuje identyfikator UUID jako dwa typy {@code long}.
     *
     * @param output strumień
     * @param uuid identyfikator
     * @throws IOException błąd zapisu
     */
    private static void writeUuid(DataOutputStream output, UUID uuid) throws IOException {
        output.writeLong(uuid.getMostSignificantBits());
        output.writeLong(uuid.getLeastSignificantBits());
    }

    /**
     * Odczytuje identyfikator UUID.
     *
     * @param input strumień
     * @return identyfikator
     * @throws IOException błąd odczytu
     */
    private static UUID readUuid(DataInputStream input) throws IOException {
        return new UUID(input.readLong(), input.readLong());
    }

    /**
     * Zapisuje tekst w UTF-8 poprzedzony długością w bajtach.
     *
     * @param output strumień
     * @param value tekst
     * @throws IOException błąd zapisu
     */
    private static void writeString(DataOutputStream output, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > MAX_STRING_BYTES) {
            throw new IOException("Tekst jest zbyt długi do zapisania.");
        }
        output.writeInt(bytes.length);
        output.write(bytes);
    }

    /**
     * Odczytuje tekst zapisany metodą {@link #writeString(DataOutputStream, String)}.
     *
     * @param input strumień
     * @return odczytany tekst
     * @throws IOException błąd odczytu lub niepoprawna długość
     */
    private static String readString(DataInputStream input) throws IOException {
        int length = input.readInt();
        if (length < 0 || length > MAX_STRING_BYTES) {
            throw new IOException("Nieprawidłowa długość tekstu: " + length);
        }
        byte[] bytes = input.readNBytes(length);
        if (bytes.length != length) {
            throw new EOFException("Plik zakończył się podczas odczytu tekstu.");
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Odczytuje i sprawdza liczbę elementów kolekcji.
     *
     * @param input strumień
     * @param collectionName nazwa kolekcji do komunikatu
     * @return liczba elementów
     * @throws IOException gdy liczba jest niepoprawna
     */
    private static int readCount(DataInputStream input, String collectionName) throws IOException {
        int count = input.readInt();
        if (count < 0 || count > MAX_COLLECTION_SIZE) {
            throw new IOException("Nieprawidłowa liczba " + collectionName + ": " + count);
        }
        return count;
    }
}
