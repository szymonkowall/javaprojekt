package pl.edu.wit.studentmanager.ui.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.i18n.Translatable;
import pl.edu.wit.studentmanager.model.AppData;
import pl.edu.wit.studentmanager.model.StudentScore;
import pl.edu.wit.studentmanager.service.ScoreService;
import pl.edu.wit.studentmanager.service.SubjectService;
import pl.edu.wit.studentmanager.ui.UiDialogs;
import pl.edu.wit.studentmanager.ui.dialog.ScoreDialog;
import pl.edu.wit.studentmanager.ui.table.ScoreTableModel;


public final class ScorePanel extends JPanel implements Translatable {

    private static final long serialVersionUID = 1L;

    private final AppData data;

    private final LanguageManager languageManager;

    private final ScoreService scoreService;

    private final SubjectService subjectService;

    private final Runnable refreshAllAction;

    private final ScoreTableModel tableModel;

    private final JTable table;

    private final JButton addButton = new JButton();

    private final JButton editButton = new JButton();

    private final JButton deleteButton = new JButton();


    public ScorePanel(
            AppData data,
            LanguageManager languageManager,
            ScoreService scoreService,
            SubjectService subjectService,
            Runnable refreshAllAction) {
        this.data = data;
        this.languageManager = languageManager;
        this.scoreService = scoreService;
        this.subjectService = subjectService;
        this.refreshAllAction = refreshAllAction;
        tableModel = new ScoreTableModel(data, scoreService, languageManager);
        table = new JTable(tableModel);
        buildInterface();
        updateTexts();
    }

    private void buildInterface() {
        setLayout(new BorderLayout(8, 8));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(deleteButton);
        add(buttons, BorderLayout.SOUTH);

        addButton.addActionListener(event -> addScore());
        editButton.addActionListener(event -> editScore());
        deleteButton.addActionListener(event -> deleteScore());
    }

    private void addScore() {
        if (data.getStudents().isEmpty()) {
            UiDialogs.showInformation(this, languageManager, "message.no.students");
            return;
        }
        if (data.getSubjects().isEmpty()) {
            UiDialogs.showInformation(this, languageManager, "message.no.subjects");
            return;
        }
        ScoreDialog dialog = new ScoreDialog(
                owner(), data, languageManager, scoreService, subjectService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    private void editScore() {
        StudentScore score = selectedScore();
        if (score == null) {
            return;
        }
        ScoreDialog dialog = new ScoreDialog(
                owner(), data, languageManager, scoreService, subjectService, score);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    private void deleteScore() {
        StudentScore score = selectedScore();
        if (score == null || !UiDialogs.confirm(
                this, languageManager, "message.confirm.delete.score")) {
            return;
        }
        scoreService.deleteScore(score.getId());
        refreshAllAction.run();
    }


    private StudentScore selectedScore() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            UiDialogs.showInformation(this, languageManager, "message.select.score");
            return null;
        }
        return tableModel.getScoreAt(table.convertRowIndexToModel(viewRow));
    }


    private Window owner() {
        return SwingUtilities.getWindowAncestor(this);
    }

    public void refreshData() {
        tableModel.refresh();
    }

    @Override
    public void updateTexts() {
        addButton.setText(languageManager.get("button.add"));
        editButton.setText(languageManager.get("button.edit"));
        deleteButton.setText(languageManager.get("button.delete"));
        tableModel.refreshLanguage();
    }
}
