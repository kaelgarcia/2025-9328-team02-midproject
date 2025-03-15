package client.registration.view;

import javax.swing.*;
import java.awt.*;

public class SignUpView extends JFrame implements ISignUpView {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel messageLabel;
    private JButton registerButton;

    public SignUpView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Sign Up Form");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(30, 30, 30));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 1, 10, 15));
        inputPanel.setOpaque(false);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);

        inputPanel.add(usernameField);
        inputPanel.add(passwordField);
        inputPanel.add(confirmPasswordField);

        registerButton = new JButton("Sign Up");

        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(titleLabel);
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(registerButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(messageLabel);

        add(mainPanel);
    }

    @Override
    public String getUsername() { return usernameField.getText(); }
    @Override
    public String getPassword() { return new String(passwordField.getPassword()); }
    @Override
    public String getConfirmPassword() { return new String(confirmPasswordField.getPassword()); }

    @Override
    public void showMessage(String message) {
        messageLabel.setText(message);
    }

    @Override
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    @Override
    public void addRegisterListener(RegisterListener listener) {
        registerButton.addActionListener(e -> 
            listener.onRegisterAttempt(getUsername(), getPassword(), getConfirmPassword()));
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
