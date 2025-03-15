package client.owner.controller;

import client.owner.model.LaundryButtonActions;
import client.owner.view.LaundryPanel;
import java.io.File;
import java.util.List;

public class LaundryController {
    private LaundryPanel laundryPanel;
    private LaundryButtonActions laundryButtonActions;
    private File scheduleFile;
    private List<String[]> allTransactions;

    public LaundryController(LaundryPanel laundryPanel, LaundryButtonActions laundryButtonActions, File scheduleFile) {
        this.laundryPanel = laundryPanel;
        this.laundryButtonActions = laundryButtonActions;
        this.scheduleFile = scheduleFile;
        bindEvents();
        loadTransactions();
    }

    private void bindEvents() {
        laundryPanel.getSearchField().addActionListener(e -> filterTransactions(laundryPanel.getSearchField().getText()));
        laundryPanel.getAddTimeButton().addActionListener(e -> addTime());
        laundryPanel.getDeleteTimeButton().addActionListener(e -> deleteTime());
    }

    private void addTime() {
        String timeInput = javax.swing.JOptionPane.showInputDialog(laundryPanel, "Enter Time (e.g., 8:30-9:30AM):");
        if (timeInput != null && !timeInput.isEmpty()) {
            laundryButtonActions.addTimeToSchedule(timeInput, scheduleFile);
            loadTransactions();
        }
    }

    private void deleteTime() {
        String timeInput = javax.swing.JOptionPane.showInputDialog(laundryPanel, "Enter Time to Delete (e.g., 8:30-9:30AM):");
        if (timeInput != null && !timeInput.isEmpty()) {
            laundryButtonActions.deleteTimeFromSchedule(timeInput, scheduleFile);
            loadTransactions();
        }
    }

    private void filterTransactions(String searchQuery) {
        List<String[]> filteredTransactions = laundryButtonActions.filterTransactions(allTransactions, searchQuery);
        laundryPanel.getTableModel().setRowCount(0);
        for (String[] transaction : filteredTransactions) {
            laundryPanel.getTableModel().addRow(transaction);
        }
    }

    private void loadTransactions() {
        allTransactions = laundryButtonActions.loadLaundryTransactions();
        laundryPanel.getTableModel().setRowCount(0);
        for (String[] transaction : allTransactions) {
            laundryPanel.getTableModel().addRow(transaction);
        }
    }
}
