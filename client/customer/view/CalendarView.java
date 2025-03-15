package client.customer.view;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class CalendarView extends JPanel {
    private String machineType;

    public CalendarView(String machineType) {
        this.machineType = machineType;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Select a Date for " + machineType, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JPanel calendarPanel = new JPanel(new GridLayout(5, 7, 5, 5));
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : daysOfWeek) {
            calendarPanel.add(new JLabel(day, SwingConstants.CENTER));
        }

        int firstDayOfWeek = 5;
        int totalDays = 31;
        int dayCounter = 1;

        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarPanel.add(new JLabel(""));
        }

        for (int i = firstDayOfWeek; i < firstDayOfWeek + totalDays; i++) {
            int date = dayCounter++;
            JButton dayButton = new JButton("March " + date);
            calendarPanel.add(dayButton);
        }

        add(calendarPanel, BorderLayout.CENTER);
    }

    public void setButtonListeners(Consumer<String> listener) {
        for (Component comp : ((JPanel) getComponent(1)).getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.addActionListener(e -> listener.accept(button.getText()));
            }
        }
    }
}