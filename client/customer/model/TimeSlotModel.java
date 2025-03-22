package client.customer.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import server.ServerInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotModel {
    private final String machineType;
    private final String date;
    private ServerInterface server;
    private List<String> timeSlots;

    public TimeSlotModel(String machineType, String date) {
        this.machineType = machineType;
        this.date = date;
        this.timeSlots = new ArrayList<>();

        try {

            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            server = (ServerInterface) registry.lookup("Server");


            loadTimeSlotsFromServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTimeSlotsFromServer() {
        try {
            String jsonResponse = server.getTimeSlots();


            this.timeSlots = parseAndFilterTimeSlots(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> parseAndFilterTimeSlots(String jsonString) {
        List<String> filteredSlots = new ArrayList<>();

        try {
            JsonArray jsonArray = JsonParser.parseString(jsonString).getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject slotObject = jsonArray.get(i).getAsJsonObject();
                String machine = slotObject.get("machineType").getAsString();
                String slotDate = slotObject.get("date").getAsString();
                String timeSlot = slotObject.get("slot").getAsString();
                String status = slotObject.get("status").getAsString();


                if (machine.equals(this.machineType) && slotDate.equals(this.date) && status.equalsIgnoreCase("VACANT")) {
                    filteredSlots.add(timeSlot);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filteredSlots;
    }

    public List<String> getAvailableTimeSlots() {
        return timeSlots;
    }

    public boolean bookTimeSlot(String timeSlot) {
        if (!timeSlots.contains(timeSlot)) {
            System.out.println("Time slot not available.");
            return false;
        }

        timeSlots.remove(timeSlot); // Simulate booking
        System.out.println("Slot booked successfully! (Not saved on server)");
        return true;
    }

    public String getMachineType() {
        return machineType;
    }

    public String getDate() {
        return date;
    }
}
