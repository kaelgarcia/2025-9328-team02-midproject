package server;

import com.google.gson.*;
import server.ServerInterface;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class CustomerServerImpl extends UnicastRemoteObject implements ServerInterface {
    private static final Gson gson = new Gson();
    private final String userDataPath = "userData/";
    private final String scheduleFilePath = "schedule.json";

    public CustomerServerImpl() throws RemoteException {
        super();
    }

    @Override
    public List<String[]> loadHistory(String username) throws RemoteException {
        try {
            File userFile = new File(userDataPath + username + ".json");
            if (!userFile.exists()) {
                return new ArrayList<>();
            }
            JsonObject userJson = new Gson().fromJson(new FileReader(userFile), JsonObject.class);
            JsonArray bookings = userJson.getAsJsonArray("bookings");
            List<String[]> historyRecords = new ArrayList<>();
            for (JsonElement bookingElem : bookings) {
                JsonObject booking = bookingElem.getAsJsonObject();
                String machineType = booking.get("machineType").getAsString();
                String date = booking.get("date").getAsString();
                String time = booking.get("time").getAsString();
                String comment = booking.has("comment") ? booking.get("comment").getAsString() : "";
                historyRecords.add(new String[]{machineType, date, time, comment});
            }
            return historyRecords;
        } catch (Exception e) {
            throw new RemoteException("Error loading history for " + username, e);
        }
    }

    @Override
    public void updateComment(String username, String machineType, String date, String time, String comment) throws RemoteException {
        try {
            File userFile = new File(userDataPath + username + ".json");
            JsonObject userJson = new Gson().fromJson(new FileReader(userFile), JsonObject.class);
            JsonArray bookings = userJson.getAsJsonArray("bookings");
            for (JsonElement bookingElem : bookings) {
                JsonObject booking = bookingElem.getAsJsonObject();
                if (booking.get("machineType").getAsString().equals(machineType) &&
                        booking.get("date").getAsString().equals(date) &&
                        booking.get("time").getAsString().equals(time)) {
                    booking.addProperty("comment", comment);
                    break;
                }
            }
            try (FileWriter file = new FileWriter(userFile)) {
                new Gson().toJson(userJson, file);
            }
        } catch (Exception e) {
            throw new RemoteException("Error updating comment for " + username, e);
        }
    }

    @Override
    public void deleteBooking(String username, String machineType, String date, String time) throws RemoteException {
        try {
            // Remove from user's bookings
            File userFile = new File(userDataPath + username + ".json");
            JsonObject userJson = new Gson().fromJson(new FileReader(userFile), JsonObject.class);
            JsonArray bookings = userJson.getAsJsonArray("bookings");
            for (int i = 0; i < bookings.size(); i++) {
                JsonObject booking = bookings.get(i).getAsJsonObject();
                if (booking.get("machineType").getAsString().equals(machineType) &&
                        booking.get("date").getAsString().equals(date) &&
                        booking.get("time").getAsString().equals(time)) {
                    bookings.remove(i);
                    break;
                }
            }
            try (FileWriter file = new FileWriter(userFile)) {
                new Gson().toJson(userJson, file);
            }

            // Mark slot as vacant in schedule
            File scheduleFile = new File(scheduleFilePath);
            JsonObject scheduleJson = new Gson().fromJson(new FileReader(scheduleFile), JsonObject.class);
            JsonObject machines = scheduleJson.getAsJsonObject("machines");
            if (machines.has(machineType)) {
                JsonObject machine = machines.getAsJsonObject(machineType);
                JsonObject dates = machine.getAsJsonObject("dates");
                if (dates.has(date)) {
                    JsonObject day = dates.getAsJsonObject(date);
                    JsonObject timeSlots = day.getAsJsonObject("timeSlots");
                    if (timeSlots.has(time)) {
                        timeSlots.addProperty(time, "VACANT");
                    }
                }
            }
            try (FileWriter file = new FileWriter(scheduleFile)) {
                new Gson().toJson(scheduleJson, file);
            }
        } catch (Exception e) {
            throw new RemoteException("Error deleting booking for " + username, e);
        }
    }

    @Override
    public List<String> getTimeSlotsForDate(String machineType, String date) throws RemoteException {
        try {
            File scheduleFile = new File(scheduleFilePath);
            JsonObject scheduleJson = new Gson().fromJson(new FileReader(scheduleFile), JsonObject.class);
            JsonObject machines = scheduleJson.getAsJsonObject("machines");
            if (!machines.has(machineType)) {
                return new ArrayList<>();
            }
            JsonObject machine = machines.getAsJsonObject(machineType);
            JsonObject dates = machine.getAsJsonObject("dates");
            if (!dates.has(date)) {
                return new ArrayList<>();
            }
            JsonObject day = dates.getAsJsonObject(date);
            JsonObject timeSlots = day.getAsJsonObject("timeSlots");
            List<String> availableSlots = new ArrayList<>();
            for (String timeSlot : timeSlots.keySet()) {
                if ("VACANT".equalsIgnoreCase(timeSlots.get(timeSlot).getAsString())) {
                    availableSlots.add(timeSlot);
                }
            }
            return availableSlots;
        } catch (Exception e) {
            throw new RemoteException("Error getting time slots for " + machineType + " on " + date, e);
        }
    }

    @Override
    public String getSlotStatus(String machineType, String date, String timeSlot) throws RemoteException {
        try {
            File scheduleFile = new File(scheduleFilePath);
            JsonObject scheduleJson = new Gson().fromJson(new FileReader(scheduleFile), JsonObject.class);
            JsonObject machines = scheduleJson.getAsJsonObject("machines");
            if (machines.has(machineType)) {
                JsonObject machine = machines.getAsJsonObject(machineType);
                JsonObject dates = machine.getAsJsonObject("dates");
                if (dates.has(date)) {
                    JsonObject day = dates.getAsJsonObject(date);
                    JsonObject timeSlots = day.getAsJsonObject("timeSlots");
                    if (timeSlots.has(timeSlot)) {
                        return timeSlots.get(timeSlot).getAsString();
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new RemoteException("Error getting slot status", e);
        }
    }

    @Override
    public boolean updateSlotStatus(String machineType, String date, String timeSlot, String status) throws RemoteException {
        try {
            File scheduleFile = new File(scheduleFilePath);
            JsonObject scheduleJson = new Gson().fromJson(new FileReader(scheduleFile), JsonObject.class);
            JsonObject machines = scheduleJson.getAsJsonObject("machines");
            if (machines.has(machineType)) {
                JsonObject machine = machines.getAsJsonObject(machineType);
                JsonObject dates = machine.getAsJsonObject("dates");
                if (dates.has(date)) {
                    JsonObject day = dates.getAsJsonObject(date);
                    JsonObject timeSlots = day.getAsJsonObject("timeSlots");
                    if (timeSlots.has(timeSlot)) {
                        timeSlots.addProperty(timeSlot, status);
                        try (FileWriter file = new FileWriter(scheduleFile)) {
                            new Gson().toJson(scheduleJson, file);
                        }
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            throw new RemoteException("Error updating slot status", e);
        }
    }

    // Placeholder implementations for registration-related methods
    @Override
    public String getTimeSlots() throws RemoteException {
        return null; // Not implemented for this scope
    }

    @Override
    public boolean validateUserCredentials(String username, String password) throws RemoteException {
        return false; // Placeholder
    }

    @Override
    public boolean checkUserDataExists(String username) throws RemoteException {
        return false; // Placeholder
    }

    @Override
    public boolean createUserSpecificJSON(String username) throws RemoteException {
        return false; // Placeholder
    }

    @Override
    public String getUserSpecificJSON(String username) throws RemoteException {
        return null; // Placeholder
    }

    @Override
    public boolean saveUserToJSON(String username, String password) throws RemoteException {
        return false; // Placeholder
    }

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