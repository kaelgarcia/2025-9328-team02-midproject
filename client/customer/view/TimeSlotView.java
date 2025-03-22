package client.customer.view;

import javax.swing.*;
import java.awt.*;

public class TimeSlotView extends JPanel {
    private final String machineType;
    private final String selectedDate;
    private final JPanel slotPanel;
    private String[] displayedSlots;
    private String[] displayedStatuses;
    private SlotSelectionListener displayedListener;

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

    public void setTimeSlots(String[] slots, String[] statuses, SlotSelectionListener listener) {
        this.displayedSlots = slots;
        this.displayedStatuses = statuses;
        this.displayedListener = listener;
        updateUI();
    }

    public void updateUI() {
        slotPanel.removeAll();
        if (displayedSlots != null && displayedStatuses != null && displayedSlots.length == displayedStatuses.length) {
            for (int i = 0; i < displayedSlots.length; i++) {
                final String slot = displayedSlots[i];
                String status = displayedStatuses[i];
                JButton slotButton = new JButton(slot);
                if ("VACANT".equals(status)) {
                    slotButton.setBackground(Color.GREEN);
                    slotButton.setEnabled(true);
                    if (displayedListener != null) {
                        slotButton.addActionListener(e -> displayedListener.onSlotSelected(slot));
                    }
                } else {
                    slotButton.setBackground(Color.RED);
                    slotButton.setEnabled(false);
                }
                slotPanel.add(slotButton);
            }
        }
        slotPanel.revalidate();
        slotPanel.repaint();
    }

    public void refreshUI() {
        updateUI();
    }

    // Custom interface to replace java.util.function.Consumer
    public interface SlotSelectionListener {
        void onSlotSelected(String slot);
    }
}