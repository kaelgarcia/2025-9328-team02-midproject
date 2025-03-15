package client.owner.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import client.owner.controller.LaundryController;

import java.awt.*;

public class LaundryPanel extends JPanel {
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private LaundryController laundryController;
    private JTextField searchField;
    private JButton addTimeButton;
    private JButton deleteTimeButton;

    public LaundryPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"Customer", "Date", "Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        transactionTable = new JTable(tableModel);
        add(new JScrollPane(transactionTable), BorderLayout.CENTER);

        searchField = new JTextField(20);
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        addTimeButton = new JButton("Add Time");
        deleteTimeButton = new JButton("Delete Time");
        JPanel actionPanel = new JPanel();
        actionPanel.add(addTimeButton);
        actionPanel.add(deleteTimeButton);
        add(actionPanel, BorderLayout.SOUTH);
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JTable getTransactionTable() {
        return transactionTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getAddTimeButton() {
        return addTimeButton;
    }

    public JButton getDeleteTimeButton() {
        return deleteTimeButton;
    }

    public void setLaundryController(LaundryController laundryController) {
        this.laundryController = laundryController;
    }
}
