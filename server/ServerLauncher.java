package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerLauncher {
    public static void main(String[] args) {
        try {
            RegistrationServerImpl registrationServer = new RegistrationServerImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("RegistrationServer", registrationServer);
            System.out.println("Registration Server is running...");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}