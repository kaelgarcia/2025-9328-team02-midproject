package client.owner.view;

import client.owner.model.OwnerCalendarPanel;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class OwnerCalendarView extends JPanel {
    private String machineType;
    private OwnerHomePanel homePanel;
    private Consumer<Integer> onDateSelected; // Callback for date selection

    public OwnerCalendarView(String machineType, OwnerHomePanel homePanel) {
        this.machineType = machineType;
        this.homePanel = homePanel;
        this.initializeUI();
    }

    private void initializeUI() {
        this.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Select a Date for " + machineType.toUpperCase(), JLabel.CENTER);
        JPanel calendarPanel = new JPanel(new GridLayout(5, 7, 5, 5));
        JButton backButton = new JButton("Back");

        for (int day = 1; day <= 31; day++) {
            JButton dayButton = new JButton(String.valueOf(day));
            final int finalDay = day;
            dayButton.addActionListener(e -> {
                if (onDateSelected != null) {
                    onDateSelected.accept(finalDay);
                }
            });
            calendarPanel.add(dayButton);
        }

        backButton.addActionListener(e -> homePanel.showPanel(new OwnerCalendarPanel(homePanel)));

        this.add(titleLabel, BorderLayout.NORTH);
        this.add(calendarPanel, BorderLayout.CENTER);
        this.add(backButton, BorderLayout.SOUTH);
    }

    public void setOnDateSelected(Consumer<Integer> onDateSelected) {
        this.onDateSelected = onDateSelected;
    }
}
