package client.customer.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class HistoryView extends JPanel {
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JButton searchButton, saveButton, deleteButton;

    public HistoryView() {
        setLayout(new BorderLayout(20, 20));

        JLabel headerLabel = new JLabel("Transactions", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(0, 102, 204));
        add(headerLabel, BorderLayout.NORTH);

        String[] columns = {"MACHINE TYPE", "DATE", "TIME", "COMMENTS"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        historyTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        historyTable.setRowSorter(rowSorter);
        historyTable.setFillsViewportHeight(true);
        add(new JScrollPane(historyTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        searchField = new JTextField();
        searchButton = new JButton("Search");
        saveButton = new JButton("Save Changes");
        deleteButton = new JButton("Delete Selected");

        searchButton.setBackground(new Color(102, 204, 255));
        searchButton.setForeground(Color.WHITE);
        saveButton.setBackground(new Color(34, 177, 76));
        saveButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(204, 0, 0));
        deleteButton.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);

        bottomPanel.add(searchField, BorderLayout.CENTER);
        bottomPanel.add(searchButton, BorderLayout.EAST);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void updateHistory(List<String[]> historyRecords) {
        tableModel.setRowCount(0);
        for (String[] record : historyRecords) {
            tableModel.addRow(record);
        }
    }

    public void setSearchListener(Consumer<String> listener) {
        searchButton.addActionListener(e -> listener.accept(searchField.getText().trim()));
    }

    public void setSaveListener(Runnable listener) {
        saveButton.addActionListener(e -> listener.run());
    }

    public void setDeleteListener(Runnable listener) {
        deleteButton.addActionListener(e -> listener.run());
    }

    public void filterHistory(String query) {
        if (query.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    public int getRowCount() {
        return tableModel.getRowCount();
    }

    public String getValueAt(int row, int column) {
        return (String) tableModel.getValueAt(row, column);
    }

    public int getSelectedRow() {
        return historyTable.getSelectedRow();
    }

    public void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean confirmDeletion() {
        return JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this transaction?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}