package client.customer.controller;

import client.customer.model.TimeSlotModel;
import client.customer.view.TimeSlotView;
import server.ServerInterface;

public class TimeSlotController {
    private final TimeSlotView view;
    private final CustomerHomeController homeController;
    private final TimeSlotModel model;
    private static final String[] ALL_TIME_SLOTS = {"9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"};

    public TimeSlotController(TimeSlotView view, CustomerHomeController homeController, String machineType, String selectedDate, ServerInterface server) {
        this.view = view;
        this.homeController = homeController;
        this.model = new TimeSlotModel(machineType, selectedDate, server);
        loadTimeSlots();
    }

    private void loadTimeSlots() {
        String[] statuses = new String[ALL_TIME_SLOTS.length];
        for (int i = 0; i < ALL_TIME_SLOTS.length; i++) {
            statuses[i] = model.getSlotStatus(ALL_TIME_SLOTS[i]);
        }
        view.setTimeSlots(ALL_TIME_SLOTS, statuses, slot -> homeController.openTransactionView(model.getMachineType(), model.getDate(), slot));
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