package client.owner.model;

import client.owner.controller.DryerController;
import client.owner.controller.LaundryController;
import client.owner.view.DryerPanel;
import client.owner.view.LaundryPanel;
import client.owner.view.OwnerHomePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.rmi.RemoteException;
import java.util.regex.Pattern;

public class OwnerHomeButtonPanel extends JPanel {
    private JButton laundryButton;
    private JButton dryerButton;
    private JButton calendarButton; // New Calendar Button
    private JButton logoutButton;

    private Runnable onLaundryClick;
    private Runnable onDryerClick;
    private Runnable onCalendarClick; // New Event Handler
    private Runnable onLogoutClick;
    private File scheduleFile;

    private static final Pattern TIME_FORMAT_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");

    public OwnerHomeButtonPanel(OwnerHomePanel ownerPanel) {
        this.setLayout(new FlowLayout());
        this.createButtons();
        this.layoutButtons();
        this.attachListeners();
    }

    private void createButtons() {
        this.laundryButton = new JButton("Laundry");
        this.dryerButton = new JButton("Dryer");
        this.calendarButton = new JButton("Calendar"); // Initialize Calendar Button
        this.logoutButton = new JButton("Logout");
    }

    private void layoutButtons() {
        this.add(laundryButton);
        this.add(dryerButton);
        this.add(calendarButton); // Add to Panel
        this.add(logoutButton);
    }

    private void attachListeners() {
        this.laundryButton.addActionListener(e -> handleBooking("Laundry"));
        this.dryerButton.addActionListener(e -> handleBooking("Dryer"));
        this.calendarButton.addActionListener(e -> { // Calendar Button Action
            if (onCalendarClick != null) onCalendarClick.run();
        });
        this.logoutButton.addActionListener(e -> {
            if (onLogoutClick != null) onLogoutClick.run();
        });
    }

    private void handleBooking(String type) {
        String time = JOptionPane.showInputDialog(this, "Enter time (HH:MM, 24-hour format):");

        if (time == null || time.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Time input is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidTimeFormat(time)) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Use HH:MM (24-hour format).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LaundryButtonActionsRemote laundryService = (LaundryButtonActionsRemote) java.rmi.Naming.lookup("rmi://localhost/LaundryService");

            // Check if time is already booked
            if (laundryService.isTimeAlreadyBooked(time, "schedule.xml")) {
                JOptionPane.showMessageDialog(this, "This time slot is already booked.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Book the time slot
            boolean booked = laundryService.bookTimeSlot(time, "schedule.xml", type);
            if (booked) {
                JOptionPane.showMessageDialog(this, "Time slot booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to book the time slot.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error connecting to laundry service: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidTimeFormat(String time) {
        return TIME_FORMAT_PATTERN.matcher(time).matches();
    }

    public void setOnLaundryClick(Runnable onLaundryClick) {
        this.onLaundryClick = onLaundryClick;
    }

    public void setOnDryerClick(Runnable onDryerClick) {
        this.onDryerClick = onDryerClick;
    }

    public void setOnCalendarClick(Runnable onCalendarClick) { // Setter for Calendar Click
        this.onCalendarClick = onCalendarClick;
    }

    public void setOnLogoutClick(Runnable onLogoutClick) {
        this.onLogoutClick = onLogoutClick;
    }

    public LaundryPanel createLaundryPanel() throws RemoteException {
        LaundryPanel laundryPanel = new LaundryPanel();
        LaundryController laundryController = new LaundryController(laundryPanel, new LaundryButtonActions(), scheduleFile);
        laundryPanel.setLaundryController(laundryController);
        return laundryPanel;
    }

    public DryerPanel createDryerPanel() {
        DryerPanel dryerPanel = new DryerPanel();
        DryerController dryerController = new DryerController(dryerPanel, new DryerButtonActions(), scheduleFile);
        dryerPanel.setDryerController(dryerController);
        return dryerPanel;
    }
}
