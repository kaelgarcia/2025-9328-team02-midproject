package client.registration.model;

import com.google.gson.Gson;

public class LoginUser {
    private String username;
    private String password;

    public LoginUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static LoginUser fromJson(String json) {
        return new Gson().fromJson(json, LoginUser.class);
    }
}