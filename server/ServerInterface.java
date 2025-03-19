package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import com.google.gson.JsonObject;

/**
 * RMI interface for handling server operations with Gson-based JSON communication.
 */
public interface ServerInterface extends Remote {

    String getTimeSlots() throws RemoteException;

    boolean validateUserCredentials(String username, String password) throws RemoteException;

    boolean checkUserDataExists(String username) throws RemoteException;

    boolean createUserSpecificJSON(String username) throws RemoteException;

    String getUserSpecificJSON(String username) throws RemoteException;

    boolean saveUserToJSON(String username, String password) throws RemoteException;

    void logAction(String message, String action) throws RemoteException;
}
