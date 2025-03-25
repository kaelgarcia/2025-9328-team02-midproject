package client.owner.view;

import client.owner.model.OwnerCalendarPanel;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class OwnerCalendarView extends JPanel {
    private String machineType;
    private OwnerHomePanel homePanel;
    private Consumer<Integer> onDateSelected; // Callback for date selection

    public OwnerCalendarView(String machineType, OwnerHomePanel homePanel, Consumer<Integer> onDateSelected) {
        this.machineType = machineType;
        this.homePanel = homePanel;
        this.onDateSelected = onDateSelected;
        this.initializeUI();
    }

    public OwnerCalendarView(String type, OwnerHomePanel homePanel) {
    }

    private void initializeUI() {
        this.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Select a Date for " + machineType.toUpperCase(), JLabel.CENTER);
        JPanel calendarPanel = new JPanel(new GridLayout(5, 7, 5, 5));
        JButton backButton = new JButton("Back");

        for (int day = 1; day <= 31; day++) {
            JButton dayButton = new JButton(String.valueOf(day));
            final int selectedDay = day;
            dayButton.addActionListener(e -> handleDateSelection(selectedDay));
            calendarPanel.add(dayButton);
        }

        backButton.addActionListener(e -> homePanel.showPanel(new OwnerCalendarPanel(homePanel)));

        this.add(titleLabel, BorderLayout.NORTH);
        this.add(calendarPanel, BorderLayout.CENTER);
        this.add(backButton, BorderLayout.SOUTH);
    }

    private void handleDateSelection(int day) {
        if (onDateSelected != null) {
            onDateSelected.accept(day);
            JOptionPane.showMessageDialog(this, "You selected: " + day, "Date Selected", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void setOnDateSelected(Consumer<Integer> onDateSelected) {
        this.onDateSelected = onDateSelected;
    }
}


