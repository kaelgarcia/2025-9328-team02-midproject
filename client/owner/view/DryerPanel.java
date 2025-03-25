package client.owner.view;

import client.owner.controller.DryerController;
import client.owner.model.DryerButtonActions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class DryerPanel extends JPanel {
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private DryerController dryerController;
    private DryerButtonActions dryerButtonActions;

    public DryerPanel() {
        setLayout(new BorderLayout());

        // Initialize actions
        dryerButtonActions = new DryerButtonActions();
        File scheduleFile = new File("schedule.xml");

        // Pass `this` instead of creating a new panel
        dryerController = new DryerController(this, dryerButtonActions, scheduleFile);

        String[] columnNames = {"Customer", "Date", "Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        transactionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);

        JTextField searchField = new JTextField(20);
        searchField.addActionListener(e -> filterTransactions(searchField.getText()));
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        JButton addTimeButton = new JButton("Add Time");
        addTimeButton.addActionListener(e -> showAddTimeDialog());

        JButton deleteTimeButton = new JButton("Delete Time");
        deleteTimeButton.addActionListener(e -> showDeleteTimeDialog());

        JPanel actionPanel = new JPanel();
        actionPanel.add(addTimeButton);
        actionPanel.add(deleteTimeButton);
        add(actionPanel, BorderLayout.SOUTH);

        // Load data initially
        dryerController.loadTransactions();
    }

    public void updateTable(List<String[]> transactions) {
        tableModel.setRowCount(0);
        for (String[] transaction : transactions) {
            tableModel.addRow(transaction);
        }
    }

    private void filterTransactions(String searchQuery) {
        if (dryerController != null) {
            dryerController.filterTransactions(searchQuery);
        } else {
            System.err.println("dryerController is not initialized!");
        }
    }

    private void showAddTimeDialog() {
        String timeInput = JOptionPane.showInputDialog(this, "Enter Time (e.g., 08:30):");
        if (timeInput != null && !timeInput.isEmpty()) {
            System.out.println("Add Time clicked! Sending time: " + timeInput);
            if (dryerController != null) {
                dryerController.addTime(timeInput);
            } else {
                System.err.println("DryerController is NULL!");
            }
        }
    }

    private void showDeleteTimeDialog() {
        String timeInput = JOptionPane.showInputDialog(this, "Enter Time to Delete (e.g., 08:30):");
        if (timeInput != null && !timeInput.isEmpty()) {
            System.out.println("Delete Time clicked! Sending time: " + timeInput);
            if (dryerController != null) {
                dryerController.deleteTime(timeInput);
            } else {
                System.err.println("DryerController is NULL!");
            }
        }
    }
    public void setDryerController(DryerController dryerController) {
        this.dryerController = dryerController;
        this.dryerController.loadTransactions();
    }
}

