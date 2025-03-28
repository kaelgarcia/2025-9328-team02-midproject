package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;

/**
 * OwnerServerImpl class handles the server-side operations for the owner panel.
 * It uses Gson for JSON data parsing and processing.
 */
public class OwnerServerImpl implements Remote {
    private static final int PORT = 12345;
    private static final String FILE_PATH = "data/transactions.json";

    public static void main(String[] args) {
        new OwnerServerImpl().startServer();
    }

    /**
     * Starts the server and listens for client connections.
     */
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles communication with a connected client.
     *
     * @param socket the client socket
     */
    private void handleClient(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String request = in.readLine();
            System.out.println("Received request: " + request);

            if (request.equals("GET_TRANSACTIONS")) {
                String jsonResponse = getTransactionsAsJson();
                out.println(jsonResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads transactions from the JSON file and returns them as a JSON string.
     *
     * @return JSON formatted string representing transactions
     */
    private String getTransactionsAsJson() {
        List<JsonObject> transactions = new ArrayList<>();
        Gson gson = new Gson();

        try (Reader reader = new FileReader(FILE_PATH)) {
            JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
            for (int i = 0; i < jsonArray.size(); i++) {
                transactions.add(jsonArray.get(i).getAsJsonObject());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Gson().toJson(transactions);
    }
}
