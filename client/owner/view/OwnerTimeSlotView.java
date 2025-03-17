package client.owner.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OwnerTimeSlotView extends JPanel {
    private JPanel timeSlotPanel;
    private JButton backButton;

    public OwnerTimeSlotView(String machineType, int selectedDay, OwnerHomePanel homePanel) {
        this.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Time Slots for " + machineType.toUpperCase() + " - March " + selectedDay, JLabel.CENTER);
        timeSlotPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        backButton = new JButton("Back");

        backButton.addActionListener(e -> homePanel.showPanel(new OwnerCalendarView(machineType, homePanel)));

        this.add(titleLabel, BorderLayout.NORTH);
        this.add(timeSlotPanel, BorderLayout.CENTER);
        this.add(backButton, BorderLayout.SOUTH);
    }

    public void displayTimeSlots(List<String> timeSlots) {
        timeSlotPanel.removeAll();
        for (String slot : timeSlots) {
            JButton slotButton = new JButton(slot);
            slotButton.setEnabled(false); // Disable for owner view
            timeSlotPanel.add(slotButton);
        }
        timeSlotPanel.revalidate();
        timeSlotPanel.repaint();
    }
}
