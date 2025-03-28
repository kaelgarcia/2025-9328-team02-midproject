package server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface OwnerInterface extends Remote {

    void addTimeToSchedule(String time, String scheduleFilePath) throws RemoteException;
    void deleteTimeFromSchedule(String time, String scheduleFilePath) throws RemoteException;
    List<String[]> filterTransactions(List<String[]> allTransactions, String searchQuery) throws RemoteException;
    List<String[]> loadLaundryTransactions() throws RemoteException;

    // New methods for validation
    boolean isTimeSlotAvailable(String time, String scheduleFilePath) throws RemoteException;
    boolean isValidTimeFormat(String time) throws RemoteException;
    boolean isTimeAlreadyBooked(String time, String s);
    boolean bookTimeSlot(String time, String s, String type) throws RemoteException;

    // DryerButtonActions
    boolean addTimeToSchedule(String time, File scheduleFile) throws RemoteException;
    boolean bookTimeSlot(String time, File scheduleFile, String clientName) throws RemoteException;
    // boolean isValidTimeFormat(String time) throws RemoteException;
    boolean isTimeSlotTaken(Element day, String time) throws RemoteException;
    void saveToFile(Document doc, File scheduleFile) throws RemoteException;
    void deleteTimeFromSchedule(String time, File scheduleFilePath) throws RemoteException;
    // List<String[]> filterTransactions(List<String[]> allTransactions, String searchQuery) throws RemoteException;
    List<String[]> loadDryerTransactions() throws RemoteException;

    //OwnerTimeSlotModel
    void loadTimeSlotsFromXML() throws RemoteException;
    List<String> getTimeSlots() throws RemoteException;
    boolean isTimeSlotAvailable(String time) throws RemoteException;
    boolean bookTimeSlot(String time) throws RemoteException;
    // boolean isValidTimeFormat(String time) throws RemoteException;
    void saveBookingToXML(String time) throws RemoteException;

}
