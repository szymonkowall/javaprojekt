package pl.edu.wit.studentmanager.i18n;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Zarządza aktualnym językiem aplikacji i informuje komponenty o jego zmianie.
 */
public final class LanguageManager {

    /** Bazowa nazwa plików z tłumaczeniami. */
    private static final String BUNDLE_BASE_NAME = "i18n.messages";

    /** Aktualnie wybrana lokalizacja. */
    private Locale currentLocale;

    /** Aktualnie używany zestaw tłumaczeń. */
    private ResourceBundle bundle;

    /** Lista funkcji wywoływanych po zmianie języka. */
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    /**
     * Tworzy menedżer z językiem polskim jako domyślnym.
     */
    public LanguageManager() {
        this(Locale.forLanguageTag("pl"));
    }

    /**
     * Tworzy menedżer z podanym językiem początkowym.
     *
     * @param initialLocale początkowa lokalizacja
     */
    public LanguageManager(Locale initialLocale) {
        currentLocale = Objects.requireNonNull(initialLocale, "Lokalizacja nie może być pusta.");
        bundle = loadBundle(currentLocale);
    }

    /**
     * Zwraca tekst przypisany do podanego klucza.
     *
     * @param key klucz tłumaczenia
     * @return przetłumaczony tekst albo klucz otoczony znakami wykrzyknika
     */
    public String get(String key) {
        Objects.requireNonNull(key, "Klucz nie może być pusty.");
        try {
            return bundle.getString(key);
        } catch (MissingResourceException exception) {
            return "!" + key + "!";
        }
    }

    /**
     * Ustawia nowy język i powiadamia słuchaczy.
     *
     * @param locale nowa lokalizacja
     */
    public void setLocale(Locale locale) {
        Objects.requireNonNull(locale, "Lokalizacja nie może być pusta.");
        if (locale.equals(currentLocale)) {
            return;
        }
        currentLocale = locale;
        bundle = loadBundle(locale);
        listeners.forEach(Runnable::run);
    }

    /**
     * Zwraca aktualną lokalizację.
     *
     * @return aktualna lokalizacja
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Rejestruje funkcję wykonywaną po zmianie języka.
     *
     * @param listener funkcja odświeżająca teksty
     */
    public void addLanguageChangeListener(Runnable listener) {
        listeners.add(Objects.requireNonNull(listener, "Słuchacz nie może być pusty."));
    }

    /**
     * Usuwa wcześniej zarejestrowanego słuchacza.
     *
     * @param listener usuwany słuchacz
     */
    public void removeLanguageChangeListener(Runnable listener) {
        listeners.remove(listener);
    }

    /**
     * Ładuje zestaw zasobów dla podanej lokalizacji.
     *
     * @param locale lokalizacja
     * @return zestaw tłumaczeń
     */
    private static ResourceBundle loadBundle(Locale locale) {
        return ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
    }
}
