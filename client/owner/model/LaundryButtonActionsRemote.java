package client.owner.model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LaundryButtonActionsRemote extends Remote {
    void addTimeToSchedule(String time, String scheduleFilePath) throws RemoteException;
    void deleteTimeFromSchedule(String time, String scheduleFilePath) throws RemoteException;
    List<String[]> filterTransactions(List<String[]> allTransactions, String searchQuery) throws RemoteException;
    List<String[]> loadLaundryTransactions() throws RemoteException;

    // New methods for validation
    boolean isTimeSlotAvailable(String time, String scheduleFilePath) throws RemoteException;
    boolean isValidTimeFormat(String time) throws RemoteException;

    boolean isTimeAlreadyBooked(String time, String s);

    boolean bookTimeSlot(String time, String s, String type) throws RemoteException;
}
