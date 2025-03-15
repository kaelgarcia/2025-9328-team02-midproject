package client.owner.model;

import client.owner.view.OwnerHomePanel;
import client.owner.view.LaundryPanel;
import client.owner.view.DryerPanel;
import client.owner.controller.LaundryController;
import client.owner.controller.DryerController;


import javax.swing.*;
import java.awt.*;
import java.io.File;

public class OwnerHomeButtonPanel extends JPanel {
    private JButton laundryButton;
    private JButton dryerButton;
    private JButton logoutButton;
    private OwnerHomePanel ownerHomePanel;
    private final File scheduleFile;

    private Runnable onLaundryClick;
    private Runnable onDryerClick;
    private Runnable onLogoutClick;

    public OwnerHomeButtonPanel(OwnerHomePanel ownerHomePanel) {
        this.ownerHomePanel = ownerHomePanel;
        this.scheduleFile = new File("schedule.xml");
        this.setLayout(new FlowLayout());
        this.createButtons();
        this.layoutButtons();
        this.attachListeners();
    }

    private void createButtons() {
        this.laundryButton = new JButton("Laundry");
        this.dryerButton = new JButton("Dryer");
        this.logoutButton = new JButton("Logout");
    }

    private void layoutButtons() {
        this.add(laundryButton);
        this.add(dryerButton);
        this.add(logoutButton);
    }

    private void attachListeners() {
        this.laundryButton.addActionListener(e -> {
            if (onLaundryClick != null) onLaundryClick.run();
        });
        this.dryerButton.addActionListener(e -> {
            if (onDryerClick != null) onDryerClick.run();
        });
        this.logoutButton.addActionListener(e -> {
            if (onLogoutClick != null) onLogoutClick.run();
        });
    }

    public void setOnLaundryClick(Runnable onClick) {
        this.onLaundryClick = onClick;
    }

    public void setOnDryerClick(Runnable onClick) {
        this.onDryerClick = onClick;
    }

    public void setOnLogoutClick(Runnable onClick) {
        this.onLogoutClick = onClick;
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
