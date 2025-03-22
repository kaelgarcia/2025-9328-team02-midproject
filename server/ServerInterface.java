package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import com.google.gson.JsonObject;
import java.util.List;


/**
 * RMI interface for handling server operations with Gson-based JSON communication.
 */
public interface ServerInterface extends Remote {
    List<String[]> loadHistory(String username) throws RemoteException;
    void updateComment(String username, String machineType, String date, String time, String comment) throws RemoteException;
    void deleteBooking(String username, String machineType, String date, String time) throws RemoteException;
    List<String> getTimeSlotsForDate(String machineType, String date) throws RemoteException;
    String getSlotStatus(String machineType, String date, String timeSlot) throws RemoteException;
    boolean updateSlotStatus(String machineType, String date, String timeSlot, String status) throws RemoteException;
    String getTimeSlots() throws RemoteException;

    boolean validateUserCredentials(String username, String password) throws RemoteException;

    boolean checkUserDataExists(String username) throws RemoteException;

    boolean createUserSpecificJSON(String username) throws RemoteException;

    String getUserSpecificJSON(String username) throws RemoteException;

    boolean saveUserToJSON(String username, String password) throws RemoteException;

    void logAction(String message, String action) throws RemoteException;
}
