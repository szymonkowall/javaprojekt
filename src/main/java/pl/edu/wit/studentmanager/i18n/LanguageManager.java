package pl.edu.wit.studentmanager.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

public final class LanguageManager {

    private static final String BUNDLE_BASE_NAME = "i18n.messages";
    private Locale currentLocale;
    private ResourceBundle bundle;
    
    private final List<Runnable> listeners = new ArrayList<>();

    public LanguageManager() {
        this(Locale.forLanguageTag("pl"));
    }

    public LanguageManager(Locale initialLocale) {
        currentLocale = Objects.requireNonNull(initialLocale, "Lokalizacja nie może być pusta.");
        bundle = loadBundle(currentLocale);
    }

    public String get(String key) {
        Objects.requireNonNull(key, "Klucz nie może być pusty.");
        try {
            return bundle.getString(key);
        } catch (MissingResourceException exception) {
            return "!" + key + "!";
        }
    }

    public void setLocale(Locale locale) {
        Objects.requireNonNull(locale, "Lokalizacja nie może być pusta.");
        if (locale.equals(currentLocale)) {
            return;
        }
        currentLocale = locale;
        bundle = loadBundle(locale);

        List<Runnable> listenersCopy = new ArrayList<>(listeners);
        for (Runnable listener : listenersCopy) {
            listener.run();
        }
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public void addLanguageChangeListener(Runnable listener) {
        listeners.add(Objects.requireNonNull(listener, "Słuchacz nie może być pusty."));
    }

    public void removeLanguageChangeListener(Runnable listener) {
        listeners.remove(listener);
    }

    private static ResourceBundle loadBundle(Locale locale) {
        return ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
    }
}