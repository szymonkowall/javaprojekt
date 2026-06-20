package pl.edu.wit.studentmanager.ui.table;

import java.util.List;
import java.util.UUID;

import javax.swing.table.AbstractTableModel;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.AssessmentCriterion;
import pl.edu.wit.studentmanager.service.SubjectService;


public final class CriterionTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private final SubjectService subjectService;

    private final LanguageManager languageManager;

    private UUID subjectId;

    private List<AssessmentCriterion> rows = List.of();


    public CriterionTableModel(SubjectService subjectService, LanguageManager languageManager) {
        this.subjectService = subjectService;
        this.languageManager = languageManager;
    }

    public void setSubjectId(UUID subjectId) {
        this.subjectId = subjectId;
        refresh();
    }

    public void refresh() {
        rows = subjectId == null ? List.of() : subjectService.getCriteriaForSubject(subjectId);
        fireTableDataChanged();
    }

    public void refreshLanguage() {
        fireTableStructureChanged();
    }


    public AssessmentCriterion getCriterionAt(int rowIndex) {
        return rows.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }


    @Override
    public String getColumnName(int column) {
        return column == 0
                ? languageManager.get("column.criterion")
                : languageManager.get("column.maximumPoints");
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AssessmentCriterion criterion = rows.get(rowIndex);
        return columnIndex == 0 ? criterion.getName() : criterion.getMaximumPoints();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
