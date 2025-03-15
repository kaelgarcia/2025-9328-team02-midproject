package client.utility;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientServerConnection extends Thread{

    private final String SERVERADDRESS = "localhost";
    private final int SERVERPORT = 3000;
    public static Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private FileOutputStream fileOutputStream;
    private boolean running = true;
    private static ClientServerConnection instance;
    private boolean isConnected = false;

    public ClientServerConnection(){

    }

    public static ClientServerConnection getInstance() {
        if(instance == null){
            instance = new ClientServerConnection();
        }

        return instance;
    }

    public void connect(){
        if(!isConnected){
            try{
                socket = new Socket(SERVERADDRESS, SERVERPORT);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printWriter = new PrintWriter(socket.getOutputStream(), true);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                isConnected = true;
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void requestToServer(String request){
        if(!isConnected){
            System.out.println("Client is not connected to the server");
        }

        System.out.println("Client to Server Request: " + request);

        printWriter.println(request);
    }

    public String receivedMessage() throws IOException {
        if(!isConnected){
            System.out.println("Client is not connected to the server");
        }
        return bufferedReader.readLine();
    }

    public static Document receiveXMLFile(String pathToSaveFile) {
        System.out.println("ClientServerConnection: " + pathToSaveFile);

        if (socket == null || socket.isClosed()) {
            System.err.println("Error: Socket is null or closed. Connection not established.");
            return null;
        }

        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            FileOutputStream fileOut = new FileOutputStream(pathToSaveFile);

            long fileSize = in.readLong();
            System.out.println("CLIENTSERVERCONNECTION: File Size " + fileSize);

            if (fileSize == 0) {
                System.err.println("CLIENTSERVERCONNECTION: Server sent file size 0. File not found or error occurred.");
                return null;
            }

            byte[] buffer = new byte[4096];
            long totalBytesRead = 0;
            int bytesRead;
            while (totalBytesRead < fileSize) {
                long remainingBytes = fileSize - totalBytesRead;
                bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, remainingBytes));
                if (bytesRead == -1) {
                    break; // End of stream reached unexpectedly
                }
                fileOut.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }

            fileOut.close();

            if (totalBytesRead != fileSize) {
                System.err.println("CLIENTSERVERCONNECTION: File size mismatch. Expected: " + fileSize + ", Received: " + totalBytesRead);
                return null;
            }

            System.out.println("CLIENTSERVERCONNECTION: Read Successfully");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new File(pathToSaveFile));

        } catch (IOException e) {
            System.err.println("Error receiving or parsing XML file: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("General exception in receiving or parsing: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
