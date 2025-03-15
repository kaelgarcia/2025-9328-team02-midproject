package client.registration.view;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JFrame implements ILoginView {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private JButton loginButton;
    private JButton registerButton;

    public LoginPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Login Panel");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(30, 30, 30));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 1, 10, 15));
        inputPanel.setOpaque(false);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        inputPanel.add(usernameField);
        inputPanel.add(passwordField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        loginButton = new JButton("Login");
        registerButton = new JButton("Sign Up");

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(titleLabel);
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(messageLabel);

        add(mainPanel);
    }

    @Override
    public String getUsername() {
        return usernameField.getText();
    }

    @Override
    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    @Override
    public void showMessage(String message) {
        messageLabel.setText(message);
    }

    @Override
    public void addLoginListener(LoginListener listener) {
        loginButton.addActionListener(e -> listener.onLoginAttempt(getUsername(), getPassword()));
    }

    @Override
    public void addRegisterListener(RegisterListener listener) {
        registerButton.addActionListener(e -> listener.onRegisterRequest());
    }
}
