package client.owner.controller;


import client.owner.model.LaundryButtonActions;
import client.owner.view.LaundryPanel;
import java.io.File;
import java.rmi.RemoteException;
import java.util.List;
import javax.swing.JOptionPane;

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
        String timeInput = JOptionPane.showInputDialog(laundryPanel, "Enter Time (e.g., 8:30-9:30AM):");
        if (timeInput != null && !timeInput.isEmpty()) {
            try {
                laundryButtonActions.addTimeToSchedule(timeInput, scheduleFile.getAbsolutePath());
                loadTransactions();
            } catch (RemoteException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(laundryPanel, "Error communicating with the server.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteTime() {
        String timeInput = JOptionPane.showInputDialog(laundryPanel, "Enter Time to Delete (e.g., 8:30-9:30AM):");
        if (timeInput != null && !timeInput.isEmpty()) {
            try {
                laundryButtonActions.deleteTimeFromSchedule(timeInput, scheduleFile.getAbsolutePath());
                loadTransactions();
            } catch (RemoteException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(laundryPanel, "Error communicating with the server.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filterTransactions(String searchQuery) {
        try {
            List<String[]> filteredTransactions = laundryButtonActions.filterTransactions(allTransactions, searchQuery);
            laundryPanel.getTableModel().setRowCount(0);
            for (String[] transaction : filteredTransactions) {
                laundryPanel.getTableModel().addRow(transaction);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(laundryPanel, "Error retrieving filtered transactions from server.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTransactions() {
        try {
            allTransactions = laundryButtonActions.loadLaundryTransactions();
            laundryPanel.getTableModel().setRowCount(0);
            for (String[] transaction : allTransactions) {
                laundryPanel.getTableModel().addRow(transaction);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(laundryPanel, "Error loading transactions from server.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
