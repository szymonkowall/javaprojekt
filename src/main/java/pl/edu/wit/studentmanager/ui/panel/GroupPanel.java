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
import pl.edu.wit.studentmanager.model.StudentGroup;
import pl.edu.wit.studentmanager.service.AssignmentService;
import pl.edu.wit.studentmanager.service.GroupService;
import pl.edu.wit.studentmanager.ui.UiDialogs;
import pl.edu.wit.studentmanager.ui.dialog.GroupDialog;
import pl.edu.wit.studentmanager.ui.table.GroupTableModel;


public final class GroupPanel extends JPanel implements Translatable {

    private static final long serialVersionUID = 1L;

    private final LanguageManager languageManager;

    private final GroupService groupService;

    private final Runnable refreshAllAction;

    private final GroupTableModel tableModel;

    private final JTable table;

    private final JButton addButton = new JButton();

    private final JButton editButton = new JButton();

    private final JButton deleteButton = new JButton();


    public GroupPanel(
            LanguageManager languageManager,
            GroupService groupService,
            AssignmentService assignmentService,
            Runnable refreshAllAction) {
        this.languageManager = languageManager;
        this.groupService = groupService;
        this.refreshAllAction = refreshAllAction;
        tableModel = new GroupTableModel(groupService, assignmentService, languageManager);
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

        addButton.addActionListener(event -> addGroup());
        editButton.addActionListener(event -> editGroup());
        deleteButton.addActionListener(event -> deleteGroup());
    }

    private void addGroup() {
        GroupDialog dialog = new GroupDialog(owner(), languageManager, groupService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    private void editGroup() {
        StudentGroup group = selectedGroup();
        if (group == null) {
            return;
        }
        GroupDialog dialog = new GroupDialog(owner(), languageManager, groupService, group);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    private void deleteGroup() {
        StudentGroup group = selectedGroup();
        if (group == null || !UiDialogs.confirm(
                this, languageManager, "message.confirm.delete.group")) {
            return;
        }
        groupService.deleteGroup(group.getId());
        refreshAllAction.run();
    }


    private StudentGroup selectedGroup() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            UiDialogs.showInformation(this, languageManager, "message.select.group");
            return null;
        }
        return tableModel.getGroupAt(table.convertRowIndexToModel(viewRow));
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
