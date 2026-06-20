package pl.edu.wit.studentmanager.ui.table;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.service.SubjectService;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.UUID;

/**
 * Model tabeli kryteriów wybranego przedmiotu.
 */
public final class CriterionTableModel extends AbstractTableModel {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Serwis przedmiotów i kryteriów. */
    private final SubjectService subjectService;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Identyfikator aktualnie wybranego przedmiotu. */
    private UUID subjectId;

    /** Aktualne wiersze. */
    private List<AssessmentCriterion> rows = List.of();

    /**
     * Tworzy model tabeli kryteriów.
     *
     * @param subjectService serwis przedmiotów
     * @param languageManager menedżer języka
     */
    public CriterionTableModel(SubjectService subjectService, LanguageManager languageManager) {
        this.subjectService = subjectService;
        this.languageManager = languageManager;
    }

    /**
     * Ustawia przedmiot, którego kryteria mają być pokazane.
     *
     * @param subjectId identyfikator przedmiotu lub {@code null}
     */
    public void setSubjectId(UUID subjectId) {
        this.subjectId = subjectId;
        refresh();
    }

    /** Odświeża dane. */
    public void refresh() {
        rows = subjectId == null ? List.of() : subjectService.getCriteriaForSubject(subjectId);
        fireTableDataChanged();
    }

    /** Odświeża nagłówki. */
    public void refreshLanguage() {
        fireTableStructureChanged();
    }

    /**
     * Zwraca kryterium z wiersza.
     *
     * @param rowIndex indeks wiersza
     * @return kryterium
     */
    public AssessmentCriterion getCriterionAt(int rowIndex) {
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
     * @return nazwa
     */
    @Override
    public String getColumnName(int column) {
        return column == 0
                ? languageManager.get("column.criterion")
                : languageManager.get("column.maximumPoints");
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
        AssessmentCriterion criterion = rows.get(rowIndex);
        return columnIndex == 0 ? criterion.getName() : criterion.getMaximumPoints();
    }

    /** @return zawsze {@code false} */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
