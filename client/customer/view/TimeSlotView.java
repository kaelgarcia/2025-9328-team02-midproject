package client.customer.view;

import client.customer.model.TimeSlotModel; // this must not be here
import javax.swing.*;
import java.awt.*;

public class TimeSlotView extends JPanel {
    private String machineType;
    private String selectedDate;
    private TimeSlotModel timeSlotModel;
    private JPanel slotPanel;

    public TimeSlotView(String machineType, String selectedDate) {
        this.machineType = machineType.toLowerCase();
        this.selectedDate = selectedDate;


        setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Select a Time Slot for " + selectedDate, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        slotPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        add(slotPanel, BorderLayout.CENTER);
    }

    public void loadTimeSlots(final SlotSelectionListener listener) {
        slotPanel.removeAll();
        String[] allSlots = timeSlotModel.getTimeSlotsForDate(machineType, selectedDate)
                .toArray(new String[0]);

        for (int i = 0; i < allSlots.length; i++) {
            final String slot = allSlots[i];
            JButton slotButton = new JButton(slot);
            String slotStatus = timeSlotModel.getSlotStatus(machineType, selectedDate, slot);

            if ("VACANT".equals(slotStatus)) {
                slotButton.setBackground(Color.GREEN);
                slotButton.setEnabled(true);
                if (listener != null) {
                    slotButton.addActionListener(e -> listener.onSlotSelected(slot));
                }
            } else {
                slotButton.setBackground(Color.RED);
                slotButton.setEnabled(false);
            }

            slotPanel.add(slotButton);
        }

        slotPanel.revalidate();
        slotPanel.repaint();
    }

    public void refreshUI() {
        loadTimeSlots(null);
    }

    // Custom interface to replace java.util.function.Consumer
    public interface SlotSelectionListener {
        void onSlotSelected(String slot);
    }
}