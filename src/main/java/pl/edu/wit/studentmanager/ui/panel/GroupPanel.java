package pl.edu.wit.studentmanager.ui.panel;

import pl.edu.wit.studentmanager.i18n.LanguageManager;
import pl.edu.wit.studentmanager.i18n.Translatable;
import pl.edu.wit.studentmanager.model.StudentGroup;
import pl.edu.wit.studentmanager.service.AssignmentService;
import pl.edu.wit.studentmanager.service.GroupService;
import pl.edu.wit.studentmanager.ui.UiDialogs;
import pl.edu.wit.studentmanager.ui.dialog.GroupDialog;
import pl.edu.wit.studentmanager.ui.table.GroupTableModel;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;

/**
 * Panel ewidencji grup studenckich.
 */
public final class GroupPanel extends JPanel implements Translatable {

    /** Numer wersji klasy. */
    private static final long serialVersionUID = 1L;

    /** Menedżer języka. */
    private final LanguageManager languageManager;

    /** Serwis grup. */
    private final GroupService groupService;

    /** Funkcja odświeżenia całej aplikacji. */
    private final Runnable refreshAllAction;

    /** Model tabeli. */
    private final GroupTableModel tableModel;

    /** Tabela grup. */
    private final JTable table;

    /** Przycisk dodawania. */
    private final JButton addButton = new JButton();

    /** Przycisk edycji. */
    private final JButton editButton = new JButton();

    /** Przycisk usuwania. */
    private final JButton deleteButton = new JButton();

    /**
     * Tworzy panel grup.
     *
     * @param languageManager menedżer języka
     * @param groupService serwis grup
     * @param assignmentService serwis przypisań
     * @param refreshAllAction funkcja odświeżenia
     */
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

    /** Buduje interfejs panelu. */
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

    /** Otwiera dialog dodawania. */
    private void addGroup() {
        GroupDialog dialog = new GroupDialog(owner(), languageManager, groupService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshAllAction.run();
        }
    }

    /** Otwiera dialog edycji. */
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

    /** Usuwa wybraną grupę. */
    private void deleteGroup() {
        StudentGroup group = selectedGroup();
        if (group == null || !UiDialogs.confirm(
                this, languageManager, "message.confirm.delete.group")) {
            return;
        }
        groupService.deleteGroup(group.getId());
        refreshAllAction.run();
    }

    /**
     * Zwraca wybraną grupę.
     *
     * @return grupa lub {@code null}
     */
    private StudentGroup selectedGroup() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            UiDialogs.showInformation(this, languageManager, "message.select.group");
            return null;
        }
        return tableModel.getGroupAt(table.convertRowIndexToModel(viewRow));
    }

    /**
     * Zwraca okno nadrzędne panelu.
     *
     * @return okno nadrzędne
     */
    private Window owner() {
        return SwingUtilities.getWindowAncestor(this);
    }

    /** Odświeża dane tabeli. */
    public void refreshData() {
        tableModel.refresh();
    }

    /** Aktualizuje teksty panelu. */
    @Override
    public void updateTexts() {
        addButton.setText(languageManager.get("button.add"));
        editButton.setText(languageManager.get("button.edit"));
        deleteButton.setText(languageManager.get("button.delete"));
        tableModel.refreshLanguage();
    }
}
