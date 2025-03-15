package client.customer.model;

public class CustomerHomeModel {
    private final String username;
    private final String userFilePath; // Changed from File to String

    public CustomerHomeModel(String username, String userFilePath) {
        this.username = username;
        this.userFilePath = userFilePath;
    }

    public String getUsername() {
        return username;
    }

    public String getUserFilePath() {
        return userFilePath;
    }
}