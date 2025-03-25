package client.owner.controller;

import client.owner.model.DryerButtonActions;
import client.owner.view.DryerPanel;

import java.io.File;
import java.util.List;

public class DryerController {
    private DryerPanel dryerPanel;
    private DryerButtonActions dryerButtonActions;

    // Schedule file used for add/delete operations
    private File scheduleFile;
    private List<String[]> allTransactions;

    public DryerController(DryerPanel dryerPanel, DryerButtonActions dryerButtonActions, File scheduleFile) {
        this.dryerPanel = dryerPanel;
        this.dryerButtonActions = dryerButtonActions;
        this.scheduleFile = scheduleFile;
    }

    public void addTime(String time) {
        if (dryerButtonActions.addTimeToSchedule(time, scheduleFile)) {
            loadTransactions();
        }
    }

    public void deleteTime(String time) {
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
        List<String[]> transactions = dryerButtonActions.loadDryerTransactions();
        this.allTransactions = transactions;
        dryerPanel.updateTable(transactions);
    }
}
