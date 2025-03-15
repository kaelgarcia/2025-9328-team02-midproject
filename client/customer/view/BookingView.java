package client.customer.view;

import javax.swing.*;
import java.awt.*;

public class BookingView extends JPanel {
    private JButton laundryButton;
    private JButton dryerButton;

    public BookingView() {
        setLayout(new GridBagLayout());
        setBackground(Color.LIGHT_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        JLabel label = new JLabel("Select Machine Type:");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 0;
        add(label, gbc);

        laundryButton = new JButton("Laundry");
        dryerButton = new JButton("Dryer");

        gbc.gridy = 1;
        add(laundryButton, gbc);
        gbc.gridy = 2;
        add(dryerButton, gbc);
    }

    public JButton getLaundryButton() {
        return laundryButton;
    }

    public JButton getDryerButton() {
        return dryerButton;
    }
}