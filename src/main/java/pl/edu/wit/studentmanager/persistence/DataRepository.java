package pl.edu.wit.studentmanager.persistence;

import pl.edu.wit.studentmanager.model.AppData;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Definiuje operacje trwałego zapisu i odczytu wszystkich danych aplikacji.
 */
public interface DataRepository {

    /**
     * Zapisuje dane do pliku.
     *
     * @param path docelowa ścieżka pliku
     * @param data dane do zapisania
     * @throws IOException gdy zapis nie powiedzie się
     */
    void save(Path path, AppData data) throws IOException;

    /**
     * Odczytuje dane z pliku.
     *
     * @param path ścieżka pliku
     * @return odczytane dane
     * @throws IOException gdy plik jest uszkodzony lub nie można go odczytać
     */
    AppData load(Path path) throws IOException;
}
