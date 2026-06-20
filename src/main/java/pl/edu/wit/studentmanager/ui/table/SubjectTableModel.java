package pl.edu.wit.studentmanager.ui.table;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.model.Subject;
import pl.edu.wit.studentmanager.service.SubjectService;


public final class SubjectTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private final SubjectService subjectService;

    private final LanguageManager languageManager;

    private List<Subject> rows = List.of();


    public SubjectTableModel(SubjectService subjectService, LanguageManager languageManager) {
        this.subjectService = subjectService;
        this.languageManager = languageManager;
        refresh();
    }

    public void refresh() {
        rows = subjectService.getAllSubjects();
        fireTableDataChanged();
    }

    public void refreshLanguage() {
        fireTableStructureChanged();
    }


    public Subject getSubjectAt(int rowIndex) {
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
                ? languageManager.get("column.subject")
                : languageManager.get("column.description");
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Subject subject = rows.get(rowIndex);
        return columnIndex == 0 ? subject.getName() : subject.getDescription();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
