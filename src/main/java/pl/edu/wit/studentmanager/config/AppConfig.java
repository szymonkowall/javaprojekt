package pl.edu.wit.studentmanager.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Przechowuje parametry konfiguracyjne aplikacji odczytane z pliku
 * {@code app.properties}.
 */
public final class AppConfig {

    /** Domyślna liczba wątków w puli operacji plikowych. */
    private static final int DEFAULT_THREAD_POOL_SIZE = 2;

    /** Domyślna nazwa pliku danych. */
    private static final String DEFAULT_DATA_FILE = "student-data.bin";

    /** Nazwa zasobu z konfiguracją. */
    private static final String CONFIG_RESOURCE = "/app.properties";

    /** Liczba wątków używanych przez operacje wykonywane w tle. */
    private final int threadPoolSize;

    /** Domyślna nazwa pliku, w którym aplikacja zapisuje dane. */
    private final String defaultDataFile;

    /**
     * Tworzy konfigurację aplikacji.
     *
     * @param threadPoolSize liczba wątków, co najmniej jeden
     * @param defaultDataFile domyślna nazwa pliku danych
     */
    public AppConfig(int threadPoolSize, String defaultDataFile) {
        if (threadPoolSize < 1) {
            throw new IllegalArgumentException("Liczba wątków musi być większa od zera.");
        }
        if (defaultDataFile == null || defaultDataFile.isBlank()) {
            throw new IllegalArgumentException("Nazwa pliku danych nie może być pusta.");
        }
        this.threadPoolSize = threadPoolSize;
        this.defaultDataFile = defaultDataFile.trim();
    }

    /**
     * Wczytuje konfigurację z zasobu {@code app.properties}.
     * Brak zasobu powoduje zastosowanie wartości domyślnych.
     *
     * @return wczytana konfiguracja
     * @throws IllegalStateException gdy plik istnieje, lecz nie można go odczytać
     *                               albo zawiera niepoprawne wartości
     */
    public static AppConfig load() {
        Properties properties = new Properties();
        try (InputStream input = AppConfig.class.getResourceAsStream(CONFIG_RESOURCE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Nie można odczytać konfiguracji aplikacji.", exception);
        }

        String threadCountText = properties.getProperty(
                "thread.pool.size", String.valueOf(DEFAULT_THREAD_POOL_SIZE));
        String defaultFile = properties.getProperty("data.default.file", DEFAULT_DATA_FILE);

        try {
            return new AppConfig(Integer.parseInt(threadCountText.trim()), defaultFile);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Parametr thread.pool.size musi być liczbą całkowitą.", exception);
        }
    }

    /**
     * Zwraca liczbę wątków puli operacji plikowych.
     *
     * @return liczba wątków
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    /**
     * Zwraca domyślną nazwę pliku danych.
     *
     * @return nazwa pliku
     */
    public String getDefaultDataFile() {
        return defaultDataFile;
    }

    /**
     * Porównuje konfiguracje na podstawie ich wartości.
     *
     * @param object porównywany obiekt
     * @return {@code true}, gdy konfiguracje są równe
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AppConfig other)) {
            return false;
        }
        return threadPoolSize == other.threadPoolSize
                && defaultDataFile.equals(other.defaultDataFile);
    }

    /**
     * Oblicza kod skrótu konfiguracji.
     *
     * @return kod skrótu
     */
    @Override
    public int hashCode() {
        return Objects.hash(threadPoolSize, defaultDataFile);
    }
}
