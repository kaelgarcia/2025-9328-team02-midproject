package client.registration.controller;

import client.registration.model.User;
import client.registration.view.ISignUpView;


public class SignUpController {
    private ISignUpView view;

    public SignUpController(ISignUpView view) {
        this.view = view;
        this.view.addRegisterListener((username, password, confirmPassword) -> {
            String error = User.validateRegistration(username, password, confirmPassword);

            if (error != null) {
                view.showMessage(error);
            } else {
                User newUser = new User(username, password);
                saveUserToXML(newUser);
                view.showMessage("Registration successful!");
                view.clearFields();

                javax.swing.SwingUtilities.invokeLater(() -> {
                    view.dispose();
                    client.registration.view.LoginPanel loginPanel = new client.registration.view.LoginPanel();
                    new LoginController(loginPanel);
                    loginPanel.setVisible(true);
                });
            }
        });
    }

    private void saveUserToXML(User user) {
        // Save user to XML
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            client.registration.view.SignUpView view = new client.registration.view.SignUpView();
            new SignUpController(view);
            view.setVisible(true);
        });
    }
}
