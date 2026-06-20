package pl.edu.wit.studentmanager.ui.panel;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.i18n.Translatable;
import pl.edu.wit.studentmanager.service.SearchService;
import pl.edu.wit.studentmanager.ui.table.SearchResultTableModel;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Panel wyszukiwania studentów, przedmiotów, kryteriów i punktów.
 */
public final class SearchPanel extends JPanel implements Translatable {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Serwis wyszukiwania. */
    private final SearchService searchService;

    /** Model wyników. */
    private final SearchResultTableModel tableModel;

    /** Pole wyszukiwania. */
    private final JTextField queryField = new JTextField(28);

    /** Etykieta pola wyszukiwania. */
    private final JLabel queryLabel = new JLabel();

    /** Przycisk wyszukiwania. */
    private final JButton searchButton = new JButton();

    /**
     * Tworzy panel wyszukiwania.
     *
     * @param languageManager menedżer języka
     * @param searchService serwis wyszukiwania
     */
    public SearchPanel(LanguageManager languageManager, SearchService searchService) {
        this.languageManager = languageManager;
        this.searchService = searchService;
        tableModel = new SearchResultTableModel(languageManager);
        buildInterface();
        updateTexts();
        refreshData();
    }

    /** Buduje interfejs. */
    private void buildInterface() {
        setLayout(new BorderLayout(8, 8));
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.add(queryLabel);
        searchBar.add(queryField);
        searchBar.add(searchButton);
        add(searchBar, BorderLayout.NORTH);

        JTable table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchButton.addActionListener(event -> performSearch());
        queryField.addActionListener(event -> performSearch());
    }

    /** Wykonuje wyszukiwanie. */
    private void performSearch() {
        tableModel.setRows(searchService.search(queryField.getText()));
    }

    /** Odświeża bieżący wynik. */
    public void refreshData() {
        performSearch();
    }

    /** Aktualizuje teksty. */
    @Override
    public void updateTexts() {
        queryLabel.setText(languageManager.get("label.query"));
        searchButton.setText(languageManager.get("button.search"));
        tableModel.refreshLanguage();
    }
}
