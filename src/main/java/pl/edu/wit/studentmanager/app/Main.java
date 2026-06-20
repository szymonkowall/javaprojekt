package pl.edu.wit.studentmanager.app;

import pl.edu.wit.studentmanager.config.AppConfig;
import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.persistence.BinaryDataRepository;
import pl.edu.wit.studentmanager.persistence.PersistenceService;
import pl.edu.wit.studentmanager.service.AssignmentService;
import pl.edu.wit.studentmanager.service.GroupService;
import pl.edu.wit.studentmanager.service.ScoreService;
import pl.edu.wit.studentmanager.service.SearchService;
import pl.edu.wit.studentmanager.service.StudentService;
import pl.edu.wit.studentmanager.service.SubjectService;
import pl.edu.wit.studentmanager.ui.MainFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Główny punkt wejścia aplikacji.
 */
public final class Main {

    /**
     * Prywatny konstruktor blokujący tworzenie instancji klasy startowej.
     */
    private Main() {
    }

    /**
     * Uruchamia aplikację na wątku zdarzeń Swing.
     *
     * @param args argumenty wiersza poleceń; obecnie nie są używane
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::startApplication);
    }

    /** Tworzy wszystkie zależności i pokazuje główne okno. */
    private static void startApplication() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Domyślny wygląd Swing jest poprawnym rozwiązaniem awaryjnym.
        }

        try {
            AppConfig config = AppConfig.load();
            AppData data = new AppData();
            LanguageManager languageManager = new LanguageManager();

            StudentService studentService = new StudentService(data);
            GroupService groupService = new GroupService(data);
            AssignmentService assignmentService = new AssignmentService(data);
            SubjectService subjectService = new SubjectService(data);
            ScoreService scoreService = new ScoreService(data);
            SearchService searchService = new SearchService(data);
            PersistenceService persistenceService = new PersistenceService(
                    new BinaryDataRepository(), config.getThreadPoolSize());

            MainFrame frame = new MainFrame(
                    config,
                    data,
                    persistenceService,
                    languageManager,
                    studentService,
                    groupService,
                    assignmentService,
                    subjectService,
                    scoreService,
                    searchService);
            frame.setVisible(true);
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(
                    null,
                    exception.getMessage(),
                    "Błąd uruchamiania",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
