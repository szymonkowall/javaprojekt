package pl.edu.wit.studentmanager.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Reprezentuje studenta zarejestrowanego w aplikacji.
 */
public final class Student {

    /** Unikalny identyfikator studenta. */
    private final UUID id;

    /** Imię studenta. */
    private String firstName;

    /** Nazwisko studenta. */
    private String lastName;

    /** Unikalny numer albumu studenta. */
    private String albumNumber;

    /**
     * Tworzy studenta z nowym identyfikatorem.
     *
     * @param firstName imię
     * @param lastName nazwisko
     * @param albumNumber numer albumu
     */
    public Student(String firstName, String lastName, String albumNumber) {
        this(UUID.randomUUID(), firstName, lastName, albumNumber);
    }

    /**
     * Tworzy studenta z podanym identyfikatorem, na przykład podczas odczytu pliku.
     *
     * @param id identyfikator
     * @param firstName imię
     * @param lastName nazwisko
     * @param albumNumber numer albumu
     */
    public Student(UUID id, String firstName, String lastName, String albumNumber) {
        this.id = Objects.requireNonNull(id, "Identyfikator nie może być pusty.");
        setFirstName(firstName);
        setLastName(lastName);
        setAlbumNumber(albumNumber);
    }

    /**
     * Tworzy kopię studenta.
     *
     * @param other kopiowany student
     */
    public Student(Student other) {
        this(other.id, other.firstName, other.lastName, other.albumNumber);
    }

    /**
     * Zwraca identyfikator studenta.
     *
     * @return identyfikator
     */
    public UUID getId() {
        return id;
    }

    /**
     * Zwraca imię studenta.
     *
     * @return imię
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Ustawia imię studenta.
     *
     * @param firstName nowe imię
     */
    public void setFirstName(String firstName) {
        this.firstName = requireText(firstName, "Imię nie może być puste.");
    }

    /**
     * Zwraca nazwisko studenta.
     *
     * @return nazwisko
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Ustawia nazwisko studenta.
     *
     * @param lastName nowe nazwisko
     */
    public void setLastName(String lastName) {
        this.lastName = requireText(lastName, "Nazwisko nie może być puste.");
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
     * Ustawia numer albumu.
     *
     * @param albumNumber nowy numer albumu
     */
    public void setAlbumNumber(String albumNumber) {
        this.albumNumber = requireText(albumNumber, "Numer albumu nie może być pusty.");
    }

    /**
     * Zwraca tekstową nazwę studenta używaną w polach wyboru.
     *
     * @return imię, nazwisko i numer albumu
     */
    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + albumNumber + ")";
    }

    /**
     * Porównuje wszystkie pola studenta.
     *
     * @param object porównywany obiekt
     * @return {@code true}, gdy obiekty są równe
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Student other)) {
            return false;
        }
        return id.equals(other.id)
                && firstName.equals(other.firstName)
                && lastName.equals(other.lastName)
                && albumNumber.equals(other.albumNumber);
    }

    /**
     * Oblicza kod skrótu studenta.
     *
     * @return kod skrótu
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, albumNumber);
    }

    /**
     * Sprawdza i normalizuje obowiązkową wartość tekstową.
     *
     * @param value wartość
     * @param message komunikat błędu
     * @return tekst bez skrajnych białych znaków
     */
    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
