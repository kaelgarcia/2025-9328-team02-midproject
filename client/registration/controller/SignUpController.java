package client.registration.controller;

import client.registration.model.User;
import client.registration.view.ISignUpView;
import client.registration.rmi.RegistrationServer;

import java.rmi.RemoteException;

public class SignUpController {
    private ISignUpView view;
    private RegistrationServer server;

    public SignUpController(ISignUpView view, RegistrationServer server) {
        this.view = view;
        this.server = server;
    }

    public void signUp(String username, String password, String email) {
        User user = new User(username, password, email);
        try {
            boolean success = server.signUp(user.getUsername(), user.getPassword(), user.getEmail());
            if (success) {
                view.showMessage("Sign up successful");
                // Navigate to the login screen
            } else {
                view.showError("Sign up failed. Username may already exist.");
            }
        } catch (RemoteException e) {
            view.showError("Error connecting to server: " + e.getMessage());
        }
    }
}
