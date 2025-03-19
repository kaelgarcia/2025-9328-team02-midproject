package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Launches the RMI server.
 */
public class ServerLauncher {
    public static void main(String[] args) {
        try {
            ServerImpl server = new ServerImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("Server", server);
            System.out.println("Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

