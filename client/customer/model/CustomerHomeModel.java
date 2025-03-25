package client.customer.model;

import server.ServerInterface;

public class CustomerHomeModel {
    private final String username;
    private final ServerInterface server;

    public CustomerHomeModel(String username, ServerInterface server) {
        this.username = username;
        this.server = server;
    }

    public String getUsername() {
        return username;
    }


    public ServerInterface getServer() {
        return server;
    }
}