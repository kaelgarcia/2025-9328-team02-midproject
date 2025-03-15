package server.customer;

import java.io.*;
import java.net.*;
import java.util.*;

public class LogServer {
    private static final int PORT = 3000;
    private static List<PrintWriter> clients = new ArrayList<>();

    public static void start() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Log server is running on port " + PORT);

                // Accept incoming connections
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());

                    // Create a PrintWriter for the client
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    clients.add(writer);

                    // Handle client disconnection
                    new Thread(() -> {
                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            while (reader.readLine() != null) {
                                // Keep the connection alive
                            }
                        } catch (IOException e) {
                            System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                        } finally {
                            clients.remove(writer);
                        }
                    }).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Method to broadcast logs to all connected clients
    public static void log(String message) {
        for (PrintWriter writer : clients) {
            writer.println(message);
        }
    }
}