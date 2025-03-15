package client.registration.model;

import client.utility.ClientServerConnection;

import java.io.File;
import java.io.IOException;

public class LoginUser {
    private String username;
    private String password;

    public LoginUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public static boolean validateUserCredentials(String username, String password) {
        try {
            ClientServerConnection client = ClientServerConnection.getInstance();
            client.requestToServer("ValidateUserCredentials");
            client.requestToServer(username);
            client.requestToServer(password);

            String receivedMessage = client.receivedMessage();

            if(receivedMessage != null){
                return Boolean.parseBoolean(receivedMessage);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isUserDataExistInServer(String username) throws IOException {
        ClientServerConnection client = ClientServerConnection.getInstance();
        client.requestToServer("CheckUserDataExists");
        client.requestToServer(username);

        String response = client.receivedMessage();
        return Boolean.parseBoolean(response);
    }

    public static void requestCreateUserSpecificXMLToServer(String username){
        ClientServerConnection client = ClientServerConnection.getInstance();
        client.requestToServer("CreateUserSpecificXML");
        client.requestToServer(username);
    }

    public static File requestUserSpecificXMLToServer(String username){
        ClientServerConnection client = ClientServerConnection.getInstance();
        client.requestToServer("GetUserSpecificXML");
        client.requestToServer(username);

        File pathFile = new File("localFilesUser" + File.separator + username + ".xml");
        client.receiveXMLFile(String.valueOf(pathFile));
        return pathFile;
    }
}