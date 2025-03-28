package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerLauncher {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            CustomerServerImpl customerServer = new CustomerServerImpl();
            registry.rebind("CustomerServer", customerServer);
            OwnerServerImpl ownerServer = new OwnerServerImpl();
            registry.rebind("OwnerServer", ownerServer);
            RegistrationServerImpl registrationServer = new RegistrationServerImpl();
            registry.rebind("RegistrationServer", registrationServer);
            System.out.println("Servers are running...");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}