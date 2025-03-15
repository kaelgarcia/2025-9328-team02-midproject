package client.customer.view;

import javax.swing.*;
import java.awt.*;

public class CustomerHomePage extends JFrame {
    private JButton homeButton;
    private JButton bookButton;
    private JButton historyButton;
    private JButton logoutButton;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JLabel welcomeLabel;

    // Add listener fields to store callback functions
    private Runnable homeButtonListener;
    private Runnable bookButtonListener;
    private Runnable historyButtonListener;
    private Runnable logoutButtonListener;

    public CustomerHomePage(String username, String userFile) {
        setTitle("Customer Home");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        homeButton = new JButton("Home");
        bookButton = new JButton("Book");
        historyButton = new JButton("Transactions");
        logoutButton = new JButton("Logout");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(homeButton);
        buttonPanel.add(bookButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(logoutButton);

        JPanel homePanel = new JPanel(new BorderLayout());
        welcomeLabel = new JLabel("WELCOME! " + username, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        homePanel.add(welcomeLabel, BorderLayout.CENTER);

        mainPanel.add(homePanel, "HOME");

        add(buttonPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Setup internal button listeners that delegate to the external ones
        homeButton.addActionListener(e -> {
            if (homeButtonListener != null) {
                homeButtonListener.run();
            }
        });

        bookButton.addActionListener(e -> {
            if (bookButtonListener != null) {
                bookButtonListener.run();
            }
        });

        historyButton.addActionListener(e -> {
            if (historyButtonListener != null) {
                historyButtonListener.run();
            }
        });

        logoutButton.addActionListener(e -> {
            if (logoutButtonListener != null) {
                logoutButtonListener.run();
            }
        });
    }

    // Navigation methods
    public void navigateToHome() {
        cardLayout.show(mainPanel, "HOME");
    }

    public void navigateTo(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }

    // Method to add a view to the main panel
    public void addViewToMainPanel(JComponent component, String cardName) {
        mainPanel.add(component, cardName);
        mainPanel.revalidate();
    }

    // Method to close the window
    public void closeWindow() {
        setVisible(false);
        dispose();
    }

    // Getters for the UI components
    public JPanel getMainPanel() {
        return mainPanel;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public JButton getHomeButton() {
        return homeButton;
    }

    public JButton getBookButton() {
        return bookButton;
    }

    public JButton getHistoryButton() {
        return historyButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    // Listener setters
    public void setHomeButtonListener(Runnable listener) {
        this.homeButtonListener = listener;
    }

    public void setBookButtonListener(Runnable listener) {
        this.bookButtonListener = listener;
    }

    public void setHistoryButtonListener(Runnable listener) {
        this.historyButtonListener = listener;
    }

    public void setLogoutButtonListener(Runnable listener) {
        this.logoutButtonListener = listener;
    }

    public void updateWelcomeMessage(String username) {
        welcomeLabel.setText("WELCOME! " + username);
    }
}