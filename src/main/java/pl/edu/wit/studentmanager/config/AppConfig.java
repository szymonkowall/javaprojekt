package pl.edu.wit.studentmanager.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class AppConfig {

    private static final int DEFAULT_THREAD_POOL_SIZE = 2;

    private static final String DEFAULT_DATA_FILE = "student-data.bin";

    private static final String CONFIG_RESOURCE = "/app.properties";

    private final int threadPoolSize;

    private final String defaultDataFile;


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


    public int getThreadPoolSize() {
        return threadPoolSize;
    }


    public String getDefaultDataFile() {
        return defaultDataFile;
    }


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


    @Override
    public int hashCode() {
        return Objects.hash(threadPoolSize, defaultDataFile);
    }
}
