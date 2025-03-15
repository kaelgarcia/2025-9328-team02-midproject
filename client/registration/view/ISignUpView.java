package client.registration.view;

public interface ISignUpView {
    String getUsername();
    String getPassword();
    String getConfirmPassword();
    void showMessage(String message);
    void clearFields();
    void setVisible(boolean visible);
    void dispose();
    
    void addRegisterListener(RegisterListener listener);

    interface RegisterListener {
        void onRegisterAttempt(String username, String password, String confirmPassword);
    }
}
