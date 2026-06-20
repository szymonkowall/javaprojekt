package pl.edu.wit.studentmanager.model;

import java.util.Objects;

/**
 * Reprezentuje jeden wiersz wyniku wyszukiwania danych studenta i jego punktów.
 */
public final class StudentSearchResult {

    /** Imię i nazwisko studenta. */
    private final String studentName;

    /** Numer albumu studenta. */
    private final String albumNumber;

    /** Kod grupy lub pusty tekst. */
    private final String groupCode;

    /** Nazwa przedmiotu lub pusty tekst. */
    private final String subjectName;

    /** Nazwa kryterium lub pusty tekst. */
    private final String criterionName;

    /** Zdobyte punkty; wartość {@code null} oznacza brak wyniku. */
    private final Double points;

    /** Maksymalna punktacja; wartość {@code null} oznacza brak kryterium. */
    private final Double maximumPoints;

    /**
     * Tworzy wynik wyszukiwania.
     *
     * @param studentName imię i nazwisko
     * @param albumNumber numer albumu
     * @param groupCode kod grupy
     * @param subjectName nazwa przedmiotu
     * @param criterionName nazwa kryterium
     * @param points punkty
     * @param maximumPoints maksimum punktów
     */
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

    /**
     * Zwraca imię i nazwisko studenta.
     *
     * @return imię i nazwisko studenta
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * Zwraca numer albumu.
     *
     * @return numer albumu
     */
    public String getAlbumNumber() {
        return albumNumber;
    }

    /**
     * Zwraca kod grupy.
     *
     * @return kod grupy
     */
    public String getGroupCode() {
        return groupCode;
    }

    /**
     * Zwraca nazwę przedmiotu.
     *
     * @return nazwa przedmiotu
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Zwraca nazwę kryterium.
     *
     * @return nazwa kryterium
     */
    public String getCriterionName() {
        return criterionName;
    }

    /**
     * Zwraca zdobyte punkty.
     *
     * @return zdobyte punkty albo {@code null}
     */
    public Double getPoints() {
        return points;
    }

    /**
     * Zwraca maksymalną liczbę punktów.
     *
     * @return maksimum punktów albo {@code null}
     */
    public Double getMaximumPoints() {
        return maximumPoints;
    }
}
