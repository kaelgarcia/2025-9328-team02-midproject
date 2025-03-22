package client.customer.controller;

import client.customer.view.TimeSlotView;
import client.customer.model.TimeSlotModel;

import java.util.List;

public class TimeSlotController {
    private final TimeSlotView view;
    private final CustomerHomeController homeController;
    private final TimeSlotModel model;

    public TimeSlotController(TimeSlotView view, CustomerHomeController homeController, String machineType, String selectedDate) {
        this.view = view;
        this.homeController = homeController;
        this.model = new TimeSlotModel(machineType, selectedDate);
        loadTimeSlots();
    }

    private void loadTimeSlots() {
        List<String> availableSlots = model.getAvailableTimeSlots();
        view.displayTimeSlots(availableSlots, slot -> homeController.openTransactionView(model.getMachineType(), model.getDate(), slot));
    }

    public void refreshUI() {
        loadTimeSlots();
        view.refreshUI();
    }

    public TimeSlotModel getModel() {
        return model;
    }
}
