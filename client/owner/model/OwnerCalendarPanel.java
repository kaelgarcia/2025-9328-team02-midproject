package client.owner.model;

import client.owner.view.OwnerCalendarView;
import client.owner.view.OwnerHomePanel;

import javax.swing.*;
import java.awt.*;

public class OwnerCalendarPanel extends JPanel {
    private JButton laundryButton;
    private JButton dryerButton;
    private JButton backButton;
    private OwnerHomePanel homePanel;

    public OwnerCalendarPanel(OwnerHomePanel homePanel) {
        this.homePanel = homePanel;
        this.initializeUI();
    }

    private void initializeUI() {
        this.setLayout(new GridLayout(3, 1, 10, 10));

        laundryButton = new JButton("Laundry");
        dryerButton = new JButton("Dryer");
        backButton = new JButton("Back");

        laundryButton.addActionListener(e -> homePanel.showPanel(new OwnerCalendarView("laundry", homePanel)));
        dryerButton.addActionListener(e -> homePanel.showPanel(new OwnerCalendarView("dryer", homePanel)));
        backButton.addActionListener(e -> homePanel.showPanel(new OwnerHomeButtonPanel(homePanel)));

        this.add(laundryButton);
        this.add(dryerButton);
        this.add(backButton);
    }
}
