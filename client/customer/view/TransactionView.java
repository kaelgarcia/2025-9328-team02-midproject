package client.customer.view;

import javax.swing.*;
import java.awt.*;

public class TransactionView extends JPanel {
    private String machineType;
    private String selectedDate;
    private String timeSlot;
    private JButton confirmButton;
    private JButton cancelButton;

    // Store listeners as functional interfaces
    private Runnable confirmButtonListener;
    private Runnable cancelButtonListener;

    // Add reference to layout components (without exposing them directly)
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public TransactionView(String machineType, String selectedDate, String timeSlot) {
        this.machineType = machineType;
        this.selectedDate = selectedDate;
        this.timeSlot = timeSlot;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Confirm Booking for " + selectedDate + " " + timeSlot, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        confirmButton = new JButton("Confirm");
        cancelButton = new JButton("Cancel");
        buttonsPanel.add(confirmButton);
        buttonsPanel.add(cancelButton);
        add(buttonsPanel, BorderLayout.CENTER);

        // Setup internal button listeners that delegate to the external ones
        confirmButton.addActionListener(e -> {
            if (confirmButtonListener != null) {
                confirmButtonListener.run();
            }
        });

        cancelButton.addActionListener(e -> {
            if (cancelButtonListener != null) {
                cancelButtonListener.run();
            }
        });
    }

    // Methods to set layout components that will be needed for navigation
    public void setNavigationComponents(JPanel mainPanel, CardLayout cardLayout) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
    }

    // Navigation method that handles the layout internally
    public void navigateToHome() {
        if (mainPanel != null && cardLayout != null) {
            cardLayout.show(mainPanel, "HOME");
        }
    }

    // Event listener setters (instead of exposing buttons)
    public void setConfirmButtonListener(Runnable listener) {
        this.confirmButtonListener = listener;
    }

    public void setCancelButtonListener(Runnable listener) {
        this.cancelButtonListener = listener;
    }

    // Original getters preserved for backward compatibility
    public JButton getConfirmButton() {
        return confirmButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    // Updated message display to accept a boolean for success/failure instead of Color
    public void showMessage(String message, boolean isSuccess) {
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setForeground(isSuccess ? new Color(0, 150, 0) : Color.RED);

        // Remove any existing message first
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel && comp != confirmButton && comp != cancelButton) {
                if (BorderLayout.SOUTH.equals(((BorderLayout)getLayout()).getConstraints(comp))) {
                    remove(comp);
                }
            }
        }

        add(messageLabel, BorderLayout.SOUTH);
        revalidate();
    }

    // Getters for the stored values
    public String getMachineType() {
        return machineType;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }
}