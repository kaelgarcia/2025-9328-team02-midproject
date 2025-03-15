package client.customer.controller;

import client.customer.view.HistoryView;
import client.customer.model.History;

public class HistoryController {
    private final HistoryView view;
    private final History model;

    public HistoryController(HistoryView view, String username, String userFilePath) {
        this.view = view;
        this.model = new History(userFilePath);
        loadHistory();
    }

    private void loadHistory() {
        view.updateHistory(model.loadHistory());
    }

    public void run() {
        loadHistory();
    }

    public History getModel() {
        return model;
    }
}