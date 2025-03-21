package client.registration.view;

import client.registration.controller.LoginController;
import client.registration.controller.SignUpController;
import client.registration.rmi.RegistrationServer;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistrationUIManager {
    private JFrame mainFrame;
    private LoginPanel loginPanel;
    private SignUpPanel signUpPanel;
    private RegistrationServer server;

    public RegistrationUIManager() {
        mainFrame = new JFrame("Registration System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 300);

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            server = (RegistrationServer) registry.lookup("RegistrationServer");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error connecting to server: " + e.getMessage());
            System.exit(1);
        }

        loginPanel = new LoginPanel(new LoginController(loginPanel, server));
        signUpPanel = new SignUpPanel(new SignUpController(signUpPanel, server));

        showLoginPanel();
    }

    public void showLoginPanel() {
        mainFrame.getContentPane().removeAll();
        loginPanel = new LoginPanel(new LoginController(loginPanel, server));
        loginPanel.setSignUpButtonListener(e -> showSignUpPanel());
        mainFrame.getContentPane().add(loginPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void showSignUpPanel() {
        mainFrame.getContentPane().removeAll();
        signUpPanel = new SignUpPanel(new SignUpController(signUpPanel, server));
        signUpPanel.setBackToLoginButtonListener(e -> showLoginPanel());
        mainFrame.getContentPane().add(signUpPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void show() {
        mainFrame.setVisible(true);
    }
}
