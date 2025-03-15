package client.customer.controller;

import client.customer.view.BookingView;
import client.customer.model.MachineTypeModel;

public class BookingController {
    private final BookingView view;
    private final CustomerHomeController homeController;
    private final MachineTypeModel model;

    public BookingController(BookingView view, CustomerHomeController homeController) {
        this.view = view;
        this.homeController = homeController;
        this.model = new MachineTypeModel();
        setupListeners();
    }

    private void setupListeners() {
        view.getLaundryButton().addActionListener(e -> {
            System.out.println("Laundry selected!");
            homeController.openCalendarView("Laundry");
        });

        view.getDryerButton().addActionListener(e -> {
            System.out.println("Dryer selected!");
            homeController.openCalendarView("Dryer");
        });
    }

    public MachineTypeModel getModel() {
        return model;
    }
}