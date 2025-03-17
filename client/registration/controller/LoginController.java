package client.registration.controller;

import client.registration.model.LoginUser;
import client.registration.view.ILoginView;
import client.registration.view.RegistrationUIManager;

public class LoginController {
    private ILoginView view;

    public LoginController(ILoginView view) {
        this.view = view;
        this.view.addLoginListener((username, password) -> {
            String usernameInput = view.getUsername();
            String passwordInput = view.getPassword();

            if (usernameInput.equals("exclusive_owner") && passwordInput.equals("OWNER")) {
                view.showMessage("Welcome, Owner!");
                RegistrationUIManager.openOwnerHomePanel(view);
            } else if (LoginUser.validateUserCredentials(usernameInput, passwordInput)) {
                view.showMessage("Login successful!");

                try {
                    if (LoginUser.isUserDataExistInServer(usernameInput)) {
                        LoginUser.requestUserSpecificXMLToServer(usernameInput);
                    } else {
                        LoginUser.requestCreateUserSpecificXMLToServer(usernameInput);
                        LoginUser.requestUserSpecificXMLToServer(usernameInput);
                    }
                } catch (java.io.IOException ex) {
                    ex.printStackTrace();
                }

                RegistrationUIManager.openCustomerHomePage(view, usernameInput);
            } else {
                view.showMessage("Invalid username or password!");
            }
        });
        this.view.addRegisterListener(() -> openSignUpPanel());
    }

    private void openSignUpPanel() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            view.setVisible(false);
            client.registration.view.ISignUpView signUpView = new client.registration.view.SignUpView();
            new SignUpController(signUpView);
            signUpView.setVisible(true);
        });
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            client.registration.view.ILoginView view = new client.registration.view.LoginPanel();
            new LoginController(view);
            view.setVisible(true);
        });
    }
}
