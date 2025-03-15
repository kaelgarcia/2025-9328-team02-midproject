package client.registration.view;

public interface ILoginView {
    String getUsername();
    String getPassword();
    void showMessage(String message);
    void setVisible(boolean visible);
    void addLoginListener(LoginListener listener);
    void addRegisterListener(RegisterListener listener);

    interface LoginListener {
        void onLoginAttempt(String username, String password);
    }

    interface RegisterListener {
        void onRegisterRequest();
    }
}
