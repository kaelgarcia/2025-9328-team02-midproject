package client.customer.controller;

import client.customer.model.History;
import client.customer.view.HistoryView;
import server.ServerInterface;

public class HistoryController {
    private final HistoryView view;
    private final History model;

    public HistoryController(HistoryView view, String username, ServerInterface server) {
        this.view = view;
        this.model = new History(username, server);
        loadHistory();

        // Set listeners for view actions
        view.setSearchListener(query -> filterHistory(query));
        view.setSaveListener(this::saveChanges);
        view.setDeleteListener(this::deleteSelectedBooking);
    }

    private void loadHistory() {
        view.updateHistory(model.loadHistory().toArray(new String[0][]));
    }

    private void filterHistory(String query) {
        view.filterHistory(query);
    }

    private void saveChanges() {
        for (int i = 0; i < view.getRowCount(); i++) {
            String machineType = view.getValueAt(i, 0);
            String date = view.getValueAt(i, 1);
            String time = view.getValueAt(i, 2);
            String comment = view.getValueAt(i, 3);
            // Update comment if changed; assumes original comments are tracked elsewhere if needed
            model.updateComment(machineType, date, time, comment);
        }
        loadHistory(); // Refresh the view
        view.showMessage("Changes saved successfully.", "Success");
    }

    private void deleteSelectedBooking() {
        int selectedRow = view.getSelectedRow();
        if (selectedRow >= 0) {
            String date = view.getValueAt(selectedRow, 1);
            if (model.canDelete(date)) {
                if (view.confirmDeletion()) {
                    String machineType = view.getValueAt(selectedRow, 0);
                    String time = view.getValueAt(selectedRow, 2);
                    model.deleteBooking(machineType, date, time);
                    loadHistory(); // Refresh the view
                    view.showMessage("Booking deleted successfully.", "Success");
                }
            } else {
                view.showMessage("Cannot delete booking less than 15 days from today.", "Error");
            }
        } else {
            view.showMessage("Please select a booking to delete.", "Error");
        }
    }

    public void run() {
        loadHistory();
    }

    public History getModel() {
        return model;
    }
}