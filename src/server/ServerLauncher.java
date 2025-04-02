package server;

import rmi.RemoteInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerLauncher {
    private static final int PORT = 1099;
    private static Registry registry;
    private static ServerImpl server;
    private static final AtomicBoolean running = new AtomicBoolean(true);
    private static final AtomicBoolean serverPaused = new AtomicBoolean(false);
    private static ExecutorService commandExecutor;
    
    public static void main(String[] args) {
        try {
            System.out.println("Laundry Registration Server Launcher");
            System.out.println("--------------------------------------");
            System.out.println("Server is not running. Use the control panel to start it.");
            
            // Show the control panel without starting the server
            showControlPanel();
            
            System.out.println("Server launcher exiting...");
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void showControlPanel() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        System.out.println("\nLaundry Registration Server Control Panel");
        
        while (running) {
            System.out.println("\n1. Start Server");
            System.out.println("2. Pause Server");
            System.out.println("3. Resume Server");
            System.out.println("4. Stop Server");
            System.out.println("5. Exit");
            System.out.print("\nSelect an option: ");
            
            try {
                int option = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                switch (option) {
                    case 1:
                        startServer();
                        break;
                    case 2:
                        pauseServer();
                        break;
                    case 3:
                        resumeServer();
                        break;
                    case 4:
                        stopServer();
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine(); // consume invalid input
            }
        }
        
        // Close scanner
        scanner.close();
    }
    
    private static void startServer() {
        try {
            if (server != null) {
                System.out.println("Server is already running!");
                return;
            }
            
            // Create and export the server object
            server = new ServerImpl();
            
            // Create or get the registry
            try {
                registry = LocateRegistry.createRegistry(PORT);
                System.out.println("Created new RMI registry on port " + PORT);
            } catch (Exception e) {
                System.out.println("Using existing RMI registry on port " + PORT);
                registry = LocateRegistry.getRegistry(PORT);
            }
            
            // Bind the server object to the registry
            registry.rebind("RegistrationService", server);
            System.out.println("Bound RegistrationService to registry");
            
            registry.rebind("RegistrationServer", server);   // Alternative name for client compatibility
            System.out.println("Bound RegistrationServer to registry");
            
            registry.rebind("BookingServer", server);        // Alternative name for client compatibility
            System.out.println("Bound BookingServer to registry");
            
            registry.rebind("Server", server);               // Simple name for client compatibility
            System.out.println("Bound Server to registry");
            
            System.out.println("Server started successfully on port " + PORT);
            
        } catch (Exception e) {
            System.err.println("Server startup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void stopServer() {
        try {
            if (server == null) {
                System.out.println("Server is not running!");
                return;
            }
            
            // Unbind the server from the registry
            try {
                registry.unbind("RegistrationService");
                registry.unbind("RegistrationServer");  // Also unbind alternative names
                registry.unbind("BookingServer");
                registry.unbind("Server");
            } catch (Exception e) {
                System.err.println("Error unbinding service: " + e.getMessage());
            }
            
            UnicastRemoteObject.unexportObject(server, true);
            server = null;
            
            System.out.println("Server stopped successfully");
            
        } catch (Exception e) {
            System.err.println("Server shutdown failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void pauseServer() {
        if (server == null) {
            System.out.println("Server is not running!");
            return;
        }
        
        server.setServerPaused(true);
        serverPaused.set(true);
        System.out.println("Server paused - all operations will be rejected until resumed");
    }
    
    private static void resumeServer() {
        if (server == null) {
            System.out.println("Server is not running!");
            return;
        }
        
        server.setServerPaused(false);
        serverPaused.set(false);
        System.out.println("Server resumed - normal operations restored");
    }
} 