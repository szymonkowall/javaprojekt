package pl.edu.wit.studentmanager.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.nio.file.Path;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import pl.edu.wit.studentmanager.config.AppConfig;
import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.i18n.Translatable;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.persistence.PersistenceService;
import pl.edu.wit.studentmanager.service.AssignmentService;
import pl.edu.wit.studentmanager.service.GroupService;
import pl.edu.wit.studentmanager.service.ScoreService;
import pl.edu.wit.studentmanager.service.SearchService;
import pl.edu.wit.studentmanager.service.StudentService;
import pl.edu.wit.studentmanager.service.SubjectService;
import pl.edu.wit.studentmanager.ui.panel.GroupPanel;
import pl.edu.wit.studentmanager.ui.panel.ScorePanel;
import pl.edu.wit.studentmanager.ui.panel.SearchPanel;
import pl.edu.wit.studentmanager.ui.panel.StudentPanel;
import pl.edu.wit.studentmanager.ui.panel.SubjectPanel;

/**
 * Główne okno aplikacji, które łączy wszystkie panele funkcjonalne.
 */
public final class MainFrame extends JFrame implements Translatable {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Konfiguracja aplikacji. */
    private final AppConfig config;

    /** Wspólny kontener danych. */
    private final AppData data;

    /** Serwis operacji plikowych. */
    private final PersistenceService persistenceService;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Panel studentów. */
    private final StudentPanel studentPanel;

    /** Panel grup. */
    private final GroupPanel groupPanel;

    /** Panel przedmiotów. */
    private final SubjectPanel subjectPanel;

    /** Panel punktów. */
    private final ScorePanel scorePanel;

    /** Panel wyszukiwania. */
    private final SearchPanel searchPanel;

    /** Zakładki głównego okna. */
    private final JTabbedPane tabs = new JTabbedPane();

    /** Przycisk zapisu. */
    private final JButton saveButton = new JButton();

    /** Przycisk odczytu. */
    private final JButton loadButton = new JButton();

    /** Etykieta języka. */
    private final JLabel languageLabel = new JLabel();

    /** Lista wyboru języka. */
    private final JComboBox<String> languageCombo = new JComboBox<>();

    /** Etykieta stanu operacji plikowej. */
    private final JLabel statusLabel = new JLabel(" ");

    /** Znacznik blokujący obsługę zdarzeń podczas przebudowy listy języków. */
    private boolean updatingLanguageCombo;

    /**
     * Tworzy główne okno aplikacji.
     */
    public MainFrame(
            AppConfig config,
            AppData data,
            PersistenceService persistenceService,
            LanguageManager languageManager,
            StudentService studentService,
            GroupService groupService,
            AssignmentService assignmentService,
            SubjectService subjectService,
            ScoreService scoreService,
            SearchService searchService) {
        this.config = config;
        this.data = data;
        this.persistenceService = persistenceService;
        this.languageManager = languageManager;

        studentPanel = new StudentPanel(
                languageManager, studentService, groupService,
                assignmentService, this::refreshAll);
        groupPanel = new GroupPanel(
                languageManager, groupService, assignmentService, this::refreshAll);
        subjectPanel = new SubjectPanel(
                languageManager, subjectService, this::refreshAll);
        scorePanel = new ScorePanel(
                data, languageManager, scoreService, subjectService, this::refreshAll);
        searchPanel = new SearchPanel(languageManager, searchService);

        buildInterface();
        registerEvents();
        updateTexts();
        setSize(1100, 700);
        setLocationRelativeTo(null);
    }

    /** Buduje układ głównego okna. */
    private void buildInterface() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(saveButton);
        toolbar.add(loadButton);
        toolbar.add(languageLabel);
        toolbar.add(languageCombo);
        toolbar.add(statusLabel);
        add(toolbar, BorderLayout.NORTH);

        tabs.addTab("", studentPanel);
        tabs.addTab("", groupPanel);
        tabs.addTab("", subjectPanel);
        tabs.addTab("", scorePanel);
        tabs.addTab("", searchPanel);
        add(tabs, BorderLayout.CENTER);
    }

    /** Rejestruje zdarzenia przycisków i języka. */
    private void registerEvents() {
        saveButton.addActionListener(event -> chooseAndSave());
        loadButton.addActionListener(event -> chooseAndLoad());
        languageCombo.addActionListener(event -> {
            if (updatingLanguageCombo) {
                return;
            }
            languageManager.setLocale(languageCombo.getSelectedIndex() == 1
                    ? Locale.ENGLISH : Locale.forLanguageTag("pl"));
        });
        languageManager.addLanguageChangeListener(() -> {
            if (SwingUtilities.isEventDispatchThread()) {
                updateTexts();
            } else {
                SwingUtilities.invokeLater(this::updateTexts);
            }
        });
    }

    /** Otwiera wybór pliku i rozpoczyna zapis. */
    private void chooseAndSave() {
        JFileChooser chooser = createFileChooser();
        chooser.setSelectedFile(Path.of(config.getDefaultDataFile()).toFile());
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path path = ensureBinExtension(chooser.getSelectedFile().toPath());
        setFileOperationInProgress(true);
        
        persistenceService.saveAsync(path, data,
            () -> SwingUtilities.invokeLater(() -> {
                setFileOperationInProgress(false);
                UiDialogs.showInformation(this, languageManager, "message.save.success");
            }),
            (error) -> SwingUtilities.invokeLater(() -> {
                setFileOperationInProgress(false);
                UiDialogs.showError(this, languageManager, "message.save.error", error.getMessage());
            })
        );
    }

    /** Otwiera wybór pliku i rozpoczyna odczyt. */
    private void chooseAndLoad() {
        JFileChooser chooser = createFileChooser();
        chooser.setSelectedFile(Path.of(config.getDefaultDataFile()).toFile());
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        setFileOperationInProgress(true);
        
        persistenceService.loadAsync(chooser.getSelectedFile().toPath(),
            (loaded) -> SwingUtilities.invokeLater(() -> {
                setFileOperationInProgress(false);
                data.replaceWith(loaded);
                refreshAll();
                UiDialogs.showInformation(this, languageManager, "message.load.success");
            }),
            (error) -> SwingUtilities.invokeLater(() -> {
                setFileOperationInProgress(false);
                UiDialogs.showError(this, languageManager, "message.load.error", error.getMessage());
            })
        );
    }

    /** Tworzy skonfigurowany wybór pliku binarnego. */
    private JFileChooser createFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(
                languageManager.get("message.file.filter"), "bin"));
        return chooser;
    }

    /** Dodaje rozszerzenie .bin, gdy użytkownik go nie podał. */
    private static Path ensureBinExtension(Path path) {
        String fileName = path.getFileName().toString();
        if (fileName.toLowerCase(Locale.ROOT).endsWith(".bin")) {
            return path;
        }
        return path.resolveSibling(fileName + ".bin");
    }

    /** Włącza lub wyłącza stan operacji plikowej. */
    private void setFileOperationInProgress(boolean inProgress) {
        saveButton.setEnabled(!inProgress);
        loadButton.setEnabled(!inProgress);
        statusLabel.setText(inProgress
                ? languageManager.get("message.operation.inProgress") : " ");
    }

/** Odświeża dane wszystkich paneli. */
    public void refreshAll() {
        studentPanel.refreshData();
        groupPanel.refreshData();
        subjectPanel.refreshData();
        scorePanel.refreshData();
        searchPanel.refreshData(); // Zmień z refreshPanel() z powrotem na refreshData()
    }

    /** Aktualizuje wszystkie teksty głównego okna. */
    @Override
    public void updateTexts() {
        setTitle(languageManager.get("app.title"));
        saveButton.setText(languageManager.get("button.save.file")); // ZMIENIONO: używa teraz dedykowanego klucza pliku
        loadButton.setText(languageManager.get("button.load"));
        languageLabel.setText(languageManager.get("label.language"));

        tabs.setTitleAt(0, languageManager.get("tab.students"));
        tabs.setTitleAt(1, languageManager.get("tab.groups"));
        tabs.setTitleAt(2, languageManager.get("tab.subjects"));
        tabs.setTitleAt(3, languageManager.get("tab.scores"));
        tabs.setTitleAt(4, languageManager.get("tab.search"));

        updatingLanguageCombo = true;
        int selectedIndex = languageManager.getCurrentLocale().getLanguage().equals("en") ? 1 : 0;
        languageCombo.removeAllItems();
        languageCombo.addItem(languageManager.get("language.polish"));
        languageCombo.addItem(languageManager.get("language.english"));
        languageCombo.setSelectedIndex(selectedIndex);
        updatingLanguageCombo = false;

        studentPanel.updateTexts();
        groupPanel.updateTexts();
        subjectPanel.updateTexts();
        scorePanel.updateTexts();
        searchPanel.updateTexts();
    }
}