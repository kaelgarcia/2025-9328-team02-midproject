package client.owner.model;

import client.owner.controller.DryerController;
import client.owner.controller.LaundryController;
import client.owner.view.DryerPanel;
import client.owner.view.LaundryPanel;
import client.owner.view.OwnerHomePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

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
        this.laundryButton.addActionListener(e -> {
            if (onLaundryClick != null) onLaundryClick.run();
        });
        this.dryerButton.addActionListener(e -> {
            if (onDryerClick != null) onDryerClick.run();
        });
        this.calendarButton.addActionListener(e -> { // Calendar Button Action
            if (onCalendarClick != null) onCalendarClick.run();
        });
        this.logoutButton.addActionListener(e -> {
            if (onLogoutClick != null) onLogoutClick.run();
        });
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
    public LaundryPanel createLaundryPanel() {
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
