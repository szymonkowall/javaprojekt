package pl.edu.wit.studentmanager.ui.table;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.Subject;
import pl.edu.wit.studentmanager.service.SubjectService;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Model tabeli przedmiotów.
 */
public final class SubjectTableModel extends AbstractTableModel {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Serwis przedmiotów. */
    private final SubjectService subjectService;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Aktualne wiersze. */
    private List<Subject> rows = List.of();

    /**
     * Tworzy model tabeli przedmiotów.
     *
     * @param subjectService serwis przedmiotów
     * @param languageManager menedżer języka
     */
    public SubjectTableModel(SubjectService subjectService, LanguageManager languageManager) {
        this.subjectService = subjectService;
        this.languageManager = languageManager;
        refresh();
    }

    /** Odświeża dane. */
    public void refresh() {
        rows = subjectService.getAllSubjects();
        fireTableDataChanged();
    }

    /** Odświeża nagłówki. */
    public void refreshLanguage() {
        fireTableStructureChanged();
    }

    /**
     * Zwraca przedmiot z wiersza.
     *
     * @param rowIndex indeks wiersza
     * @return przedmiot
     */
    public Subject getSubjectAt(int rowIndex) {
        return rows.get(rowIndex);
    }

    /** @return liczba wierszy */
    @Override
    public int getRowCount() {
        return rows.size();
    }

    /** @return liczba kolumn */
    @Override
    public int getColumnCount() {
        return 2;
    }

    /**
     * Zwraca nazwę kolumny.
     *
     * @param column indeks kolumny
     * @return nazwa kolumny
     */
    @Override
    public String getColumnName(int column) {
        return column == 0
                ? languageManager.get("column.subject")
                : languageManager.get("column.description");
    }

    /**
     * Zwraca wartość komórki.
     *
     * @param rowIndex indeks wiersza
     * @param columnIndex indeks kolumny
     * @return wartość
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Subject subject = rows.get(rowIndex);
        return columnIndex == 0 ? subject.getName() : subject.getDescription();
    }

    /** @return zawsze {@code false} */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
