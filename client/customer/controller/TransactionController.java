package client.customer.controller;

import client.customer.model.BookingConfirmationModel;
import client.customer.model.TimeSlotModel;
import client.customer.view.CustomerHomePage;
import client.customer.view.TransactionView;
import server.ServerInterface;

public class TransactionController {
    private final TransactionView view;
    private final BookingConfirmationModel model;
    private final String machineType;
    private final String selectedDate;
    private final String selectedTimeSlot;
    private String username;

    public TransactionController(TransactionView view, TimeSlotModel timeSlotModel,
                                 CustomerHomePage homePage, String machineType,
                                 String selectedDate, String selectedTimeSlot,
                                 ServerInterface server) {
        this.view = view;
        this.model = new BookingConfirmationModel(timeSlotModel, server, "data.xml");
        this.machineType = machineType;
        this.selectedDate = selectedDate;
        this.selectedTimeSlot = selectedTimeSlot;
        this.username = username;
        setupListeners();
    }

    private void setupListeners() {
        view.setConfirmButtonListener(this::confirmBooking);
        view.setCancelButtonListener(this::cancelBooking);
    }

    public void confirmBooking() {
        System.out.println("Attempting to confirm booking for machine: " + machineType +
                ", Date: " + selectedDate + ", Time Slot: " + selectedTimeSlot);
        boolean success = model.confirmBooking(machineType, selectedDate, selectedTimeSlot, username);
        if (success) {
            view.showMessage("Booking Confirmed!", true);
            view.navigateToHome();
        } else {
            view.showMessage("Failed to confirm booking! Please try again.", false);
        }
    }

    public void cancelBooking() {
        view.navigateToHome();
    }

    public TransactionView getView() {
        return view;
    }

    public BookingConfirmationModel getModel() {
        return model;
    }
}