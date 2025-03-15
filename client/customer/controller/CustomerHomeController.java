package client.customer.controller;

import server.customer.LogServer;
import client.customer.view.*;
import client.customer.model.*;
import client.registration.controller.LoginController;
import client.registration.view.LoginPanel;


public class CustomerHomeController {
    private final CustomerHomePage view;
    private final CustomerHomeModel model;
    private TimeSlotModel timeSlotModel;

    public CustomerHomeController(CustomerHomePage view, String username, String userFilePath) {
        this.view = view;
        this.model = new CustomerHomeModel(username, userFilePath);
        setupListeners();
    }

    private void setupListeners() {
        view.setHomeButtonListener(this::showHome);
        view.setBookButtonListener(this::openBookingSystem);
        view.setHistoryButtonListener(this::openHistoryView);
        view.setLogoutButtonListener(this::logout);
    }

    private void showHome() {
        view.navigateToHome();
    }

    public void openBookingSystem() {
        BookingView bookingView = new BookingView();
        new BookingController(bookingView, this);
        view.addViewToMainPanel(bookingView, "BOOKING");
        view.navigateTo("BOOKING");
    }

    public void openHistoryView() {
        HistoryView historyView = new HistoryView();
        new HistoryController(historyView, model.getUsername(), model.getUserFilePath());
        view.addViewToMainPanel(historyView, "HISTORY");
        view.navigateTo("HISTORY");
    }

    public void openCalendarView(String machineType) {
        CalendarView calendarView = new CalendarView(machineType);
        new CalendarController(calendarView, this, machineType);
        view.addViewToMainPanel(calendarView, "CALENDAR");
        view.navigateTo("CALENDAR");
    }

    public void openTimeSlotView(String machineType, String date) {
        TimeSlotView timeSlotView = new TimeSlotView(machineType, date);
        if (timeSlotModel == null) {
            timeSlotModel = new TimeSlotModel("schedule.xml", model.getUserFilePath());
            // String path passed
        }
        new TimeSlotController(timeSlotView, this, machineType, date, timeSlotModel);
        view.addViewToMainPanel(timeSlotView, "TIMESLOT");
        view.navigateTo("TIMESLOT");
    }

    public void openTransactionView(String machineType, String date, String timeSlot) {
        if (timeSlotModel == null) {
            timeSlotModel = new TimeSlotModel("schedule.xml", model.getUserFilePath());
            // String path passed
        }
        TransactionView transactionView = new TransactionView(machineType, date, timeSlot);
        new TransactionController(
                transactionView, timeSlotModel, view, machineType, date, timeSlot,
                model.getUserFilePath()
        );
        transactionView.setNavigationComponents(view.getMainPanel(), view.getCardLayout());
        view.addViewToMainPanel(transactionView, "TRANSACTION");
        view.navigateTo("TRANSACTION");
    }

    private void logout() {
        view.closeWindow();
        createLoginScreen();
    }

    protected void createLoginScreen() {
        LoginPanel loginView = new LoginPanel();
        new LoginController(loginView);
        loginView.setVisible(true);
    }

    public CustomerHomeModel getModel() {
        return model;
    }

    public static void main(String[] args) {
        LogServer.start();
        Runnable startupTask = () -> {
            String username = "kael";
            String userFilePath = "userData/" + username + ".xml";
            CustomerHomePage homePage = new CustomerHomePage(username, new String(userFilePath));
            new CustomerHomeController(homePage, username, userFilePath);
            homePage.setVisible(true);
        };
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            startupTask.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(startupTask);
        }
    }
}