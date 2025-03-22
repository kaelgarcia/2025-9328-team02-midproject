package client.customer.model;

import server.ServerInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotModel implements ServerInterface {
    private final String machineType;
    private final String date;
    private final ServerInterface server;

    public TimeSlotModel(String machineType, String date, ServerInterface server) {
        this.machineType = machineType;
        this.date = date;
        this.server = server;
    }

    public List<String> getAvailableTimeSlots() {
        try {
            return server.getTimeSlotsForDate(machineType, date);
        } catch (RemoteException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String getSlotStatus(String timeSlot) {
        try {
            return server.getSlotStatus(machineType, date, timeSlot);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getMachineType() {
        return machineType;
    }

    public String getDate() {
        return date;
    }

    @Override
    public List<String[]> loadHistory(String username) throws RemoteException {
        return null;
    }

    @Override
    public void updateComment(String username, String machineType, String date, String time, String comment) throws RemoteException {

    }

    @Override
    public void deleteBooking(String username, String machineType, String date, String time) throws RemoteException {

    }

    @Override
    public List<String> getTimeSlotsForDate(String machineType, String date) throws RemoteException {
        return null;
    }

    @Override
    public String getSlotStatus(String machineType, String date, String timeSlot) throws RemoteException {
        return null;
    }

    @Override
    public boolean updateSlotStatus(String machineType, String date, String timeSlot, String status) throws RemoteException {
        return false;
    }

    @Override
    public String getTimeSlots() throws RemoteException {
        return null;
    }

    @Override
    public boolean validateUserCredentials(String username, String password) throws RemoteException {
        return false;
    }

    @Override
    public boolean checkUserDataExists(String username) throws RemoteException {
        return false;
    }

    @Override
    public boolean createUserSpecificJSON(String username) throws RemoteException {
        return false;
    }

    @Override
    public String getUserSpecificJSON(String username) throws RemoteException {
        return null;
    }

    @Override
    public boolean saveUserToJSON(String username, String password) throws RemoteException {
        return false;
    }

    @Override
    public void logAction(String message, String action) throws RemoteException {

    }
}