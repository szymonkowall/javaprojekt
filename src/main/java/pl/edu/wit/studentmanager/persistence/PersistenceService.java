package pl.edu.wit.studentmanager.persistence;

import pl.edu.wit.studentmanager.model.AppData;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Wykonuje operacje plikowe poza wątkiem zdarzeń Swing.
 */
public final class PersistenceService implements AutoCloseable {

    /** Repozytorium realizujące właściwy zapis i odczyt. */
    private final DataRepository repository;

    /** Pula wątków przeznaczona do operacji plikowych. */
    private final ExecutorService executorService;

    /**
     * Tworzy serwis operacji plikowych.
     *
     * @param repository repozytorium danych
     * @param threadPoolSize liczba wątków, co najmniej jeden
     */
    public PersistenceService(DataRepository repository, int threadPoolSize) {
        if (threadPoolSize < 1) {
            throw new IllegalArgumentException("Liczba wątków musi być większa od zera.");
        }
        this.repository = Objects.requireNonNull(repository, "Repozytorium nie może być puste.");
        this.executorService = Executors.newFixedThreadPool(threadPoolSize, runnable -> {
            Thread thread = new Thread(runnable, "student-manager-file-worker");
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * Zapisuje w tle głęboką kopię przekazanych danych.
     *
     * @param path docelowa ścieżka
     * @param data bieżące dane
     * @return obiekt reprezentujący wynik operacji
     */
    public CompletableFuture<Void> saveAsync(Path path, AppData data) {
        Objects.requireNonNull(path, "Ścieżka nie może być pusta.");
        AppData snapshot = Objects.requireNonNull(data, "Dane nie mogą być puste.").deepCopy();
        return CompletableFuture.runAsync(() -> {
            try {
                repository.save(path, snapshot);
            } catch (IOException exception) {
                throw new CompletionException(exception);
            }
        }, executorService);
    }

    /**
     * Odczytuje dane w tle.
     *
     * @param path ścieżka źródłowa
     * @return obiekt reprezentujący przyszły wynik odczytu
     */
    public CompletableFuture<AppData> loadAsync(Path path) {
        Objects.requireNonNull(path, "Ścieżka nie może być pusta.");
        return CompletableFuture.supplyAsync(() -> {
            try {
                return repository.load(path);
            } catch (IOException exception) {
                throw new CompletionException(exception);
            }
        }, executorService);
    }

    /**
     * Zamyka pulę wątków i oczekuje krótko na zakończenie bieżących operacji.
     */
    @Override
    public void close() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException exception) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
