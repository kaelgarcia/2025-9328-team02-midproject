package client.customer.controller;

import client.customer.view.TimeSlotView;
import client.customer.model.TimeSlotModel;

public class TimeSlotController {
    private final TimeSlotView view;
    private final CustomerHomeController homeController;
    private final TimeSlotModel model;

    public TimeSlotController(TimeSlotView view, CustomerHomeController homeController, String machineType, String selectedDate, TimeSlotModel timeSlotModel) {
        this.view = view;
        this.homeController = homeController;
        this.model = new TimeSlotModel(machineType, selectedDate);
        loadTimeSlots();
    }

    private void loadTimeSlots() {
        view.loadTimeSlots(slot -> homeController.openTransactionView(model.getMachineType(), model.getDate(), slot));
    }

    public void refreshUI() {
        view.refreshUI();
    }

    public TimeSlotModel getModel() {
        return model;
    }

    private String getMachineType() {
        return model.getMachineType();
    }

    private String getDate() {
        return model.getDate();
    }
}