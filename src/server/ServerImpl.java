package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rmi.ClientCallback;
import rmi.RemoteInterface;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerImpl extends UnicastRemoteObject implements RemoteInterface {
    private static final String DATA_DIR = "data/";
    private static final String LOG_FILE = "server_log.json";
    private final Gson gson;
    private final Map<String, List<Map<String, Object>>> dataStore;
    private final List<Map<String, Object>> activityLogs;
    private final List<ClientCallback> clients;
    private volatile boolean isPaused = false;
    
    public ServerImpl() throws RemoteException {
        super();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.dataStore = new ConcurrentHashMap<>();
        this.activityLogs = new CopyOnWriteArrayList<>();
        this.clients = new CopyOnWriteArrayList<>();
        initializeDataStore();
    }
    
    private void initializeDataStore() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        // Load existing data files
        File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                String type = file.getName().replace(".json", "");
                try (Reader reader = new FileReader(file)) {
                    List<Map<String, Object>> data = gson.fromJson(reader, List.class);
                    dataStore.put(type, new ArrayList<>(data));
                } catch (IOException e) {
                    System.err.println("Error loading data file: " + file.getName());
                }
            }
        }
    }
    
    private void saveData(String type) {
        try (Writer writer = new FileWriter(DATA_DIR + type + ".json")) {
            gson.toJson(dataStore.get(type), writer);
        } catch (IOException e) {
            System.err.println("Error saving data for type: " + type);
        }
    }
    
    /**
     * Check if the server is paused before performing operations
     * @throws RemoteException if the server is paused
     */
    private void checkServerPaused() throws RemoteException {
        if (isPaused) {
            System.out.println("Operation attempted while server is paused");
            throw new RemoteException("Server is currently paused. Please try again later.");
        }
    }
    
    @Override
    public String createRecord(String type, Map<String, Object> data) throws RemoteException {
        checkServerPaused();
        
        // For time slot creation, check for overlap
        if ("timeSlot".equals(type)) {
            String date = (String) data.get("date");
            String timeSlot = (String) data.get("timeSlot");
            String machineType = (String) data.get("machineType");
            
            if (!isTimeSlotAvailable(date, timeSlot, machineType)) {
                throw new RemoteException("Cannot create time slot: Overlapping with existing time slot");
            }
        }
        
        String id = UUID.randomUUID().toString();
        data.put("id", id);
        
        dataStore.computeIfAbsent(type, k -> new ArrayList<>()).add(data);
        saveData(type);
        logActivity("system", "CREATE", "Created new " + type + " record with ID: " + id);
        
        // Notify clients
        notifyClientsOfCreate(type, data);
        
        return id;
    }
    
    @Override
    public Map<String, Object> readRecord(String type, String id) throws RemoteException {
        checkServerPaused();
        
        List<Map<String, Object>> records = dataStore.get(type);
        if (records != null) {
            return records.stream()
                    .filter(record -> id.equals(record.get("id")))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
    
    @Override
    public boolean updateRecord(String type, String id, Map<String, Object> data) throws RemoteException {
        checkServerPaused();
        
        System.out.println("Updating " + type + " record with ID: " + id);
        System.out.println("Update data: " + data);
        
        try {
            // Find the record to update
            List<Map<String, Object>> records = dataStore.get(type);
            if (records == null) {
                System.out.println("Record type not found: " + type);
                return false;
            }
            
            for (int i = 0; i < records.size(); i++) {
                Map<String, Object> record = records.get(i);
                String recordId = (String) record.get("id");
                
                if (recordId != null && recordId.equals(id)) {
                    // Create an updated record by starting with the original record
                    Map<String, Object> updatedRecord = new HashMap<>(record);
                    
                    // Apply updates from the data parameter
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        updatedRecord.put(entry.getKey(), entry.getValue());
                    }
                    
                    // Preserve the original ID
                    updatedRecord.put("id", id);
                    
                    // Replace the old record with the updated one
                    records.set(i, updatedRecord);
                    
                    // Save changes
                    saveData(type);
                    
                    // Log the update
                    logActivity("system", "UPDATE", "Updated " + type + " record with ID: " + id);
                    
                    // Notify callbacks about the update
                    notifyClientsOfUpdate(type, updatedRecord);
                    
                    System.out.println("Record updated successfully");
                    return true;
                }
            }
            
            System.out.println("Record not found for update");
            return false;
        } catch (Exception e) {
            System.err.println("Error updating record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteRecord(String type, String id) throws RemoteException {
        checkServerPaused();
        
        List<Map<String, Object>> records = dataStore.get(type);
        if (records != null) {
            boolean removed = records.removeIf(record -> id.equals(record.get("id")));
            if (removed) {
                saveData(type);
                logActivity("system", "DELETE", "Deleted " + type + " record with ID: " + id);
                
                // Notify clients
                notifyClientsOfDelete(type, id);
                
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<Map<String, Object>> searchRecords(String type, Map<String, Object> criteria) throws RemoteException {
        checkServerPaused();
        
        List<Map<String, Object>> records = dataStore.get(type);
        if (records == null) return new ArrayList<>();
        
        return records.stream()
                .filter(record -> criteria.entrySet().stream()
                        .allMatch(entry -> record.containsKey(entry.getKey()) &&
                                record.get(entry.getKey()).equals(entry.getValue())))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    @Override
    public boolean isServerAlive() throws RemoteException {
        // This can be called even when server is paused
        return true;
    }
    
    @Override
    public boolean isServerPaused() throws RemoteException {
        // This can be called even when server is paused
        return isPaused;
    }
    
    /**
     * Sets the server pause state
     * @param paused New pause state
     */
    public void setServerPaused(boolean paused) {
        this.isPaused = paused;
        String action = paused ? "PAUSE" : "RESUME";
        String message = paused ? "Server operations paused" : "Server operations resumed";
        
        try {
            logActivity("system", action, message);
        } catch (RemoteException e) {
            System.err.println("Error logging server pause state change: " + e.getMessage());
        }
        
        System.out.println(message);
        
        // Notify clients about the server pause state change
        notifyClientsOfPauseStateChange(paused);
    }
    
    /**
     * Resumes the server if it was paused
     * @return true if the server was resumed, false if it was not paused
     */
    public boolean resumeServer() {
        if (isPaused) {
            setServerPaused(false);
            return true;
        }
        return false;
    }
    
    /**
     * Notify clients about the server pause state change
     */
    private void notifyClientsOfPauseStateChange(boolean paused) {
        String statusType = paused ? "server_paused" : "server_resumed";
        for (ClientCallback client : clients) {
            try {
                client.notifyGeneric(statusType, null);
            } catch (RemoteException e) {
                // Remove failed clients
                clients.remove(client);
                System.err.println("Failed to notify client of server pause state change, removing from callbacks");
            }
        }
    }
    
    @Override
    public List<String> getSystemLogs() throws RemoteException {
        checkServerPaused();
        
        return activityLogs.stream()
                .map(log -> String.format("[%s] %s - %s: %s",
                        log.get("timestamp"),
                        log.get("userId"),
                        log.get("action"),
                        log.get("details")))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    @Override
    public boolean clearSystemLogs() throws RemoteException {
        checkServerPaused();
        
        activityLogs.clear();
        System.out.println("System logs cleared");
        
        // Record the clearing action itself
        Map<String, Object> log = new HashMap<>();
        log.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        log.put("userId", "system");
        log.put("action", "CLEAR_LOGS");
        log.put("details", "All system logs were cleared");
        activityLogs.add(log);
        
        // Save empty logs to file
        try (Writer writer = new FileWriter(DATA_DIR + LOG_FILE)) {
            gson.toJson(activityLogs, writer);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving cleared log file: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public void logActivity(String userId, String action, String details) throws RemoteException {
        // Logging should work even when server is paused
        Map<String, Object> log = new HashMap<>();
        log.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        log.put("userId", userId);
        log.put("action", action);
        log.put("details", details);
        
        // Add more detailed contextual information based on action type
        if ("BOOK".equals(action)) {
            log.put("actionCategory", "RESERVATION");
        } else if ("CANCEL".equals(action)) {
            log.put("actionCategory", "RESERVATION");
        } else if ("LOGIN".equals(action) || "LOGOUT".equals(action)) {
            log.put("actionCategory", "AUTHENTICATION");
        } else if ("CREATE".equals(action) || "UPDATE".equals(action) || "DELETE".equals(action)) {
            log.put("actionCategory", "DATA_MANAGEMENT");
        } else if ("PAUSE".equals(action) || "RESUME".equals(action)) {
            log.put("actionCategory", "SERVER_CONTROL");
        }
        
        // Add IP address if available (would need to be passed from client)
        // log.put("ipAddress", ipAddress);
        
        activityLogs.add(log);
        
        // Also print to console with enhanced formatting
        System.out.println(String.format("[%s] %s - %s: %s",
            log.get("timestamp"), userId, action, details));
        
        // Save logs to file
        try (Writer writer = new FileWriter(DATA_DIR + LOG_FILE)) {
            gson.toJson(activityLogs, writer);
        } catch (IOException e) {
            System.err.println("Error saving log file");
        }
        
        // Notify clients about the log update
        for (ClientCallback client : clients) {
            try {
                client.notifyGeneric("systemLog", null);
            } catch (RemoteException e) {
                // Remove failed clients
                clients.remove(client);
            }
        }
    }
    
    @Override
    public void registerCallback(ClientCallback callback) throws RemoteException {
        // Allow registration even when paused
        if (!clients.contains(callback)) {
            clients.add(callback);
            System.out.println("Client registered for callbacks");
            logActivity("system", "REGISTER_CALLBACK", "New client registered for callbacks");
        }
    }
    
    @Override
    public void unregisterCallback(ClientCallback callback) throws RemoteException {
        // Allow unregistration even when paused
        clients.remove(callback);
        System.out.println("Client unregistered from callbacks");
        logActivity("system", "UNREGISTER_CALLBACK", "Client unregistered from callbacks");
    }
    
    private void notifyClientsOfCreate(String type, Map<String, Object> data) {
        for (ClientCallback client : clients) {
            try {
                client.notifyCreated(type, data);
            } catch (RemoteException e) {
                // Remove failed clients
                clients.remove(client);
                System.err.println("Failed to notify client of create, removing from callbacks");
            }
        }
    }
    
    private void notifyClientsOfUpdate(String type, Map<String, Object> data) {
        for (ClientCallback client : clients) {
            try {
                client.notifyUpdated(type, data);
            } catch (RemoteException e) {
                // Remove failed clients
                clients.remove(client);
                System.err.println("Failed to notify client of update, removing from callbacks");
            }
        }
    }
    
    private void notifyClientsOfDelete(String type, String id) {
        for (ClientCallback client : clients) {
            try {
                client.notifyDeleted(type, id);
            } catch (RemoteException e) {
                // Remove failed clients
                clients.remove(client);
                System.err.println("Failed to notify client of delete, removing from callbacks");
            }
        }
    }
    
    @Override
    public boolean isTimeSlotAvailable(String date, String timeSlot, String machineType) throws RemoteException {
        return isTimeSlotAvailable(date, timeSlot, machineType, null);
    }
    
    /**
     * Checks if a time slot is available (no overlap with existing time slots)
     * @param date The date for the time slot
     * @param timeSlot The time slot string (e.g., "10:30-11:30 AM")
     * @param machineType The machine type
     * @param excludeId Optional ID to exclude from the check (for updates)
     * @return true if the time slot is available
     */
    private boolean isTimeSlotAvailable(String date, String timeSlot, String machineType, String excludeId) throws RemoteException {
        if (date == null || timeSlot == null || machineType == null) {
            return false;
        }
        
        // Parse the new time slot
        LocalTime newStart = null;
        LocalTime newEnd = null;
        
        try {
            // Parse time slot like "10:30-11:30 AM"
            String[] parts = timeSlot.split("-");
            if (parts.length != 2) {
                return false;
            }
            
            String startTime = parts[0].trim();
            String endTime = parts[1].trim();
            
            // Handle AM/PM
            boolean isStartAM = !startTime.toUpperCase().contains("PM");
            boolean isEndAM = !endTime.toUpperCase().contains("PM");
            
            // Remove AM/PM
            startTime = startTime.replaceAll("(?i)AM|PM", "").trim();
            endTime = endTime.replaceAll("(?i)AM|PM", "").trim();
            
            // Parse hours and minutes
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");
            
            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = startParts.length > 1 ? Integer.parseInt(startParts[1]) : 0;
            
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = endParts.length > 1 ? Integer.parseInt(endParts[1]) : 0;
            
            // Adjust for PM
            if (!isStartAM && startHour < 12) startHour += 12;
            if (!isEndAM && endHour < 12) endHour += 12;
            
            // Create LocalTime objects
            newStart = LocalTime.of(startHour, startMinute);
            newEnd = LocalTime.of(endHour, endMinute);
        } catch (Exception e) {
            System.err.println("Error parsing time slot: " + e.getMessage());
            return false;
        }
        
        // Get all time slots for the same date and machine type
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("date", date);
        criteria.put("machineType", machineType);
        
        List<Map<String, Object>> existingSlots = searchRecords("timeSlot", criteria);
        
        // Check for overlaps
        for (Map<String, Object> slot : existingSlots) {
            String id = (String) slot.get("id");
            
            // Skip the slot we're updating
            if (excludeId != null && excludeId.equals(id)) {
                continue;
            }
            
            String existingTimeSlot = (String) slot.get("timeSlot");
            
            // Parse the existing time slot
            try {
                String[] parts = existingTimeSlot.split("-");
                if (parts.length != 2) {
                    continue;
                }
                
                String startTime = parts[0].trim();
                String endTime = parts[1].trim();
                
                boolean isStartAM = !startTime.toUpperCase().contains("PM");
                boolean isEndAM = !endTime.toUpperCase().contains("PM");
                
                startTime = startTime.replaceAll("(?i)AM|PM", "").trim();
                endTime = endTime.replaceAll("(?i)AM|PM", "").trim();
                
                String[] startParts = startTime.split(":");
                String[] endParts = endTime.split(":");
                
                int startHour = Integer.parseInt(startParts[0]);
                int startMinute = startParts.length > 1 ? Integer.parseInt(startParts[1]) : 0;
                
                int endHour = Integer.parseInt(endParts[0]);
                int endMinute = endParts.length > 1 ? Integer.parseInt(endParts[1]) : 0;
                
                if (!isStartAM && startHour < 12) startHour += 12;
                if (!isEndAM && endHour < 12) endHour += 12;
                
                LocalTime existingStart = LocalTime.of(startHour, startMinute);
                LocalTime existingEnd = LocalTime.of(endHour, endMinute);
                
                // Check for overlap: if new start is before existing end and new end is after existing start
                if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                    System.out.println("Time slot overlap detected: " + timeSlot + " overlaps with " + existingTimeSlot);
                    return false;
                }
            } catch (Exception e) {
                System.err.println("Error parsing existing time slot: " + e.getMessage());
            }
        }
        
        return true;
    }
} 