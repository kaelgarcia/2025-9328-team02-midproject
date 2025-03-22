package client.customer.controller;

import client.customer.model.CustomerHomeModel;
import client.customer.model.TimeSlotModel;
import client.customer.view.*;
import client.registration.controller.LoginController;
import client.registration.view.LoginPanel;
import server.ServerInterface;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CustomerHomeController {
    private final CustomerHomePage view;
    private final CustomerHomeModel model;

    public CustomerHomeController(CustomerHomePage view, String username, ServerInterface server) {
        this.view = view;
        this.model = new CustomerHomeModel(username, server);
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

    public void openCalendarView(String machineType) {
        CalendarView calendarView = new CalendarView(machineType);
        new CalendarController(calendarView, this, machineType);
        view.addViewToMainPanel(calendarView, "CALENDAR");
        view.navigateTo("CALENDAR");
    }

    public void openHistoryView() {
        HistoryView historyView = new HistoryView();
        new HistoryController(historyView, model.getUsername(), model.getServer());
        view.addViewToMainPanel(historyView, "HISTORY");
        view.navigateTo("HISTORY");
    }

    public void openTimeSlotView(String machineType, String date) {
        TimeSlotView timeSlotView = new TimeSlotView(machineType, date);
        TimeSlotModel timeSlotModel = new TimeSlotModel(machineType, date, model.getServer());
        new TimeSlotController(timeSlotView, this, machineType, date, timeSlotModel);
        view.addViewToMainPanel(timeSlotView, "TIMESLOT");
        view.navigateTo("TIMESLOT");
    }

    public void openTransactionView(String machineType, String date, String timeSlot) {
        TransactionView transactionView = new TransactionView(machineType, date, timeSlot);
        TimeSlotModel timeSlotModel = new TimeSlotModel(machineType, date, model.getServer());
        new TransactionController(
                transactionView, timeSlotModel, view, machineType, date, timeSlot, model.getServer()
        );
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
        try {
            String username = "kael";
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ServerInterface server = (ServerInterface) registry.lookup("ServerInterface");
            CustomerHomePage homePage = new CustomerHomePage(username);
            new CustomerHomeController(homePage, username, server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}