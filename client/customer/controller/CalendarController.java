package client.customer.controller;

import client.customer.view.CalendarView;
import client.customer.model.CalendarModel;

public class CalendarController {
    private final CalendarView view;
    private final CustomerHomeController homeController;
    private final String machineType;
    private final CalendarModel model;

    public CalendarController(CalendarView view, CustomerHomeController homeController, String machineType) {
        this.view = view;
        this.homeController = homeController;
        this.machineType = machineType;
        this.model = new CalendarModel();
        setupListeners();
    }

    private void setupListeners() {
        view.setButtonListeners(date -> homeController.openTimeSlotView(machineType, date));
    }

    public CalendarModel getModel() {
        return model;
    }
}