package pl.edu.wit.studentmanager.ui.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.i18n.Translatable;
import pl.edu.wit.studentmanager.service.SearchService;
import pl.edu.wit.studentmanager.ui.table.SearchResultTableModel;


public final class SearchPanel extends JPanel implements Translatable {

    private static final long serialVersionUID = 1L;

    private final LanguageManager languageManager;

    private final SearchService searchService;

    private final SearchResultTableModel tableModel;

    private final JTextField queryField = new JTextField(28);

    private final JLabel queryLabel = new JLabel();

    private final JButton searchButton = new JButton();


    public SearchPanel(LanguageManager languageManager, SearchService searchService) {
        this.languageManager = languageManager;
        this.searchService = searchService;
        tableModel = new SearchResultTableModel(languageManager);
        buildInterface();
        updateTexts();
        refreshData();
    }

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

    private void performSearch() {
        tableModel.setRows(searchService.search(queryField.getText()));
    }

    public void refreshData() {
        performSearch();
    }

    @Override
    public void updateTexts() {
        queryLabel.setText(languageManager.get("label.query"));
        searchButton.setText(languageManager.get("button.search"));
        tableModel.refreshLanguage();
    }
}
