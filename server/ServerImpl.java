package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Server implementation for RMI with Gson-based JSON communication.
 */
public class ServerImpl extends UnicastRemoteObject implements ServerInterface {

    private static final Gson gson = new Gson();

    protected ServerImpl() throws RemoteException {
        super();
    }

    @Override
    public String getTimeSlots() throws RemoteException {
        try (Scanner scanner = new Scanner(new File("timeslots.json"))) {
            StringBuilder jsonContent = new StringBuilder();
            while (scanner.hasNextLine()) {
                jsonContent.append(scanner.nextLine());
            }
            JsonObject response = gson.fromJson(jsonContent.toString(), JsonObject.class);
            return gson.toJson(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    @Override
    public boolean validateUserCredentials(String username, String password) throws RemoteException {
        try (FileReader reader = new FileReader("users.json")) {
            JsonObject users = gson.fromJson(reader, JsonObject.class);
            if (users.has(username) && users.get(username).getAsString().equals(password)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkUserDataExists(String username) throws RemoteException {
        File file = new File("userData/" + username + ".json");
        return file.exists();
    }

    @Override
    public boolean createUserSpecificJSON(String username) throws RemoteException {
        try (FileWriter file = new FileWriter("userData/" + username + ".json")) {
            JsonObject userJson = new JsonObject();
            userJson.addProperty("username", username);
            userJson.add("bookings", new JsonArray());
            file.write(gson.toJson(userJson));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getUserSpecificJSON(String username) throws RemoteException {
        try (FileReader reader = new FileReader("userData/" + username + ".json")) {
            JsonObject userJson = gson.fromJson(reader, JsonObject.class);
            return gson.toJson(userJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    @Override
    public boolean saveUserToJSON(String username, String password) throws RemoteException {
        JsonObject users = new JsonObject();

        try (FileReader reader = new FileReader("users.json")) {
            users = gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        users.addProperty(username, password);

        try (FileWriter file = new FileWriter("users.json")) {
            file.write(gson.toJson(users));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void logAction(String message, String action) throws RemoteException {
        JsonObject logEntry = new JsonObject();
        logEntry.addProperty("timestamp", System.currentTimeMillis());
        logEntry.addProperty("message", message);
        logEntry.addProperty("action", action);

        try (FileWriter file = new FileWriter("server_logs.json", true)) {
            file.write(gson.toJson(logEntry) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

