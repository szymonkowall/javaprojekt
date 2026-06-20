package pl.edu.wit.studentmanager.persistence;

import java.nio.file.Path;
import java.util.function.Consumer;

import pl.edu.wit.studentmanager.model.AppData;


public final class PersistenceService {

    private final DataRepository repository;

    public PersistenceService(DataRepository repository, int threadPoolSize) {

        if (threadPoolSize < 1) {
            throw new IllegalArgumentException("Liczba wątków musi być większa od zera.");
        }
        this.repository = repository;
    }


    public void saveAsync(Path path, AppData data, Runnable onSuccess, Consumer<Exception> onError) {
        AppData snapshot = data.deepCopy();
        new Thread(() -> {
            try {
                repository.save(path, snapshot);
                if (onSuccess != null) onSuccess.run();
            } catch (Exception exception) {
                if (onError != null) onError.accept(exception);
            }
        }, "WatekZapisu").start();
    }

    public void loadAsync(Path path, Consumer<AppData> onSuccess, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                AppData loaded = repository.load(path);
                if (onSuccess != null) onSuccess.accept(loaded);
            } catch (Exception exception) {
                if (onError != null) onError.accept(exception);
            }
        }, "WatekOdczytu").start();
    }
}