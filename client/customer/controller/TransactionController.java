package client.customer.controller;

import client.customer.model.TimeSlotModel;
import server.customer.LogServer;
import client.customer.view.TransactionView;
import client.customer.view.CustomerHomePage;
import client.customer.model.BookingConfirmationModel;

public class TransactionController {
    private final TransactionView view;
    private final BookingConfirmationModel model;
    private final String machineType;
    private final String selectedDate;
    private final String selectedTimeSlot;
    private final String username;

    public TransactionController(TransactionView view, TimeSlotModel timeSlotModel,
                                 CustomerHomePage homePage, String machineType,
                                 String selectedDate, String selectedTimeSlot, String userFilePath) {
        this.view = view;
        this.model = new BookingConfirmationModel(timeSlotModel, userFilePath, "data.xml");
        this.machineType = machineType;
        this.selectedDate = selectedDate;
        this.selectedTimeSlot = selectedTimeSlot;
        this.username = extractUsernameFromPath(userFilePath);
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
            LogServer.log(username + " booked " + machineType + " at " + selectedDate + ", " + selectedTimeSlot);
        } else {
            view.showMessage("Failed to confirm booking! Please try again.", false);
        }
    }

    public void cancelBooking() {
        view.navigateToHome();
    }

    private String extractUsernameFromPath(String userFilePath) {
        return userFilePath.substring(userFilePath.lastIndexOf("/") + 1).replace(".xml", "");
    }

    public TransactionView getView() {
        return view;
    }

    public BookingConfirmationModel getModel() {
        return model;
    }
}