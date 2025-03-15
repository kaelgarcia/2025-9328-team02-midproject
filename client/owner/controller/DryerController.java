package client.owner.controller;

import client.owner.model.DryerButtonActions;
import client.owner.model.LaundryButtonActions;
import client.owner.view.DryerPanel;
import client.owner.view.LaundryPanel;

import java.io.File;
import java.util.List;

public class DryerController {
    private DryerPanel dryerPanel;
    private DryerButtonActions dryerButtonActions;
    private File scheduleFile;  // Schedule file to be used for add/delete operations
    private List<String[]> allTransactions;

    public DryerController(DryerPanel dryerPanel, DryerButtonActions dryerButtonActions, File scheduleFile) {
        this.dryerPanel = dryerPanel;
        this.dryerButtonActions = dryerButtonActions;
        this.scheduleFile = scheduleFile;
    }
    public void addTime(String time) {
        System.out.println("addTime() called with time: " + time);  // Debugging
        dryerButtonActions.addTimeToSchedule(time, scheduleFile);
        loadTransactions();
    }

    public void deleteTime(String time) {
        System.out.println("deleteTime() called with time: " + time);  // Debugging
        dryerButtonActions.deleteTimeFromSchedule(time, scheduleFile);
        loadTransactions();
    }

    public void filterTransactions(String searchQuery) {
        List<String[]> filteredTransactions = dryerButtonActions.filterTransactions(allTransactions, searchQuery);
        dryerPanel.updateTable(filteredTransactions);
    }

    /**
     * Loads the transactions from the model and updates the view.
     */
    public void loadTransactions() {
        System.out.println("Loading transactions...");
        List<String[]> transactions = dryerButtonActions.loadDryerTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions found!");
        } else {
            System.out.println("Transactions loaded: " + transactions.size());
        }
        this.allTransactions = transactions;
        dryerPanel.updateTable(transactions);
    }

}
