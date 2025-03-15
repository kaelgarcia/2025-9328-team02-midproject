package client.registration.model;

public class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static String validateRegistration(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return "All fields are required!";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match!";
        }

        return null; // No errors
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}