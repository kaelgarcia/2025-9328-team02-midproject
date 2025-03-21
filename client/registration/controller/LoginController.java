package client.registration.controller;

import client.registration.model.LoginUser;
import client.registration.view.ILoginView;
import client.registration.rmi.RegistrationServer;

import java.rmi.RemoteException;

public class LoginController {
    private ILoginView view;
    private RegistrationServer server;

    public LoginController(ILoginView view, RegistrationServer server) {
        this.view = view;
        this.server = server;
    }

    public void login(String username, String password) {
        LoginUser user = new LoginUser(username, password);
        try {
            boolean success = server.login(user.getUsername(), user.getPassword());
            if (success) {
                view.showMessage("Login successful");
                // Navigate to the next screen
            } else {
                view.showError("Invalid credentials");
            }
        } catch (RemoteException e) {
            view.showError("Error connecting to server: " + e.getMessage());
        }
    }
}
