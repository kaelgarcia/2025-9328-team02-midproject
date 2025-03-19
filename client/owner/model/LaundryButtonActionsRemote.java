package client.owner.model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LaundryButtonActionsRemote extends Remote {
    void addTimeToSchedule(String time, String scheduleFilePath) throws RemoteException;
    void deleteTimeFromSchedule(String time, String scheduleFilePath) throws RemoteException;
    List<String[]> filterTransactions(List<String[]> allTransactions, String searchQuery) throws RemoteException;
    List<String[]> loadLaundryTransactions() throws RemoteException;
}
