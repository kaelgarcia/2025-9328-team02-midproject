package client.owner.model;

import client.owner.view.OwnerCalendarView;
import client.owner.view.OwnerHomePanel;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

public class OwnerCalendarPanel extends JPanel {
    private JButton laundryButton;
    private JButton dryerButton;
    private JButton backButton;
    private OwnerHomePanel homePanel;
    private static final Pattern TIME_FORMAT_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");

    public OwnerCalendarPanel(OwnerHomePanel homePanel) {
        this.homePanel = homePanel;
        this.initializeUI();
    }

    private void initializeUI() {
        this.setLayout(new GridLayout(3, 1, 10, 10));

        laundryButton = new JButton("Laundry");
        dryerButton = new JButton("Dryer");
        backButton = new JButton("Back");

        laundryButton.addActionListener(e -> handleBooking("laundry"));
        dryerButton.addActionListener(e -> handleBooking("dryer"));
        backButton.addActionListener(e -> homePanel.showPanel(new OwnerHomeButtonPanel(homePanel)));

        this.add(laundryButton);
        this.add(dryerButton);
        this.add(backButton);
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

        LaundryButtonActionsRemote laundryService;
        try {
            laundryService = (LaundryButtonActionsRemote) java.rmi.Naming.lookup("rmi://localhost/LaundryService");

            // Check if time already exists in the schedule
            if (laundryService.isTimeAlreadyBooked(time, "schedule.xml")) {
                JOptionPane.showMessageDialog(this, "This time slot is already booked.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Book the time slot
            boolean booked = laundryService.bookTimeSlot(time, "schedule.xml", "Owner");
            if (booked) {
                JOptionPane.showMessageDialog(this, "Time slot booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                homePanel.showPanel(new OwnerCalendarView(type, homePanel));
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
}
