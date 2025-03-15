package server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server.customer.LogServer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int PORT = 3000;
    private static volatile boolean running = true;

    public static void main(String[] args) {

        new Thread(Server::listenForShutdownCommand).start();

        ServerLogXML.log("localhost", "Open Server");

        try(
                ServerSocket server = new ServerSocket(3000);
                ){

            while(running){
                Socket client = server.accept();
                ServerLogXML.log(client.getInetAddress().getHostAddress(), "Client is Connected");
                new Thread(() -> run(client)).start();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void run(Socket client){

//        System.out.println("Client is Connected");
//        System.out.println("Handling: " + client.getInetAddress().getHostAddress());
        try(
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
                ){

            String clientRequest;

            while((clientRequest = in.readLine()) != null){

                ServerLogXML.log(client.getInetAddress().getHostAddress(), "Requests " + clientRequest);

                switch (clientRequest) {
                    case "GetTimeSlots" -> sendXMLFileToClient(dataOutputStream);
                    case "ValidateUserCredentials" -> validateUserCredentials(in, out);
                    case "CheckUserDataExists" -> checkUserDataExists(in, out);
                    case "CreateUserSpecificXML" -> createUserSpecificXML(in, out);
                    case "GetUserSpecificXML" -> getUserSpecificXML(in, out, dataOutputStream);
                    case "SaveUserToXML" -> saveUserToXML(in, out);
                    case "LogAction" -> logAction(in, out);
                    case "ReceiveFile" -> receiveXMLFile(in, out, client);

                }

                ServerLogXML.log(client.getInetAddress().getHostAddress(), "Finished " + clientRequest);

            }

        } catch (Exception e){

        }

    }
    private static void sendXMLFileToClient(DataOutputStream dataOutputStream) {
        File xmlFile = new File("schedule.xml");

        try (FileInputStream fileInputStream = new FileInputStream(xmlFile)) {
            long fileSize = xmlFile.length();
            dataOutputStream.writeLong(fileSize); // Send the size of the file

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytesRead);
            }

            //System.out.println("File sent successfully!");
        } catch (IOException e) {
            System.err.println("Error sending XML file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Document receiveXMLFile(BufferedReader bufferedReader, PrintWriter printWriter, Socket socket) {

        if (socket == null || socket.isClosed()) {
            System.err.println("Error: Socket is null or closed. Connection not established.");
            return null;
        }

        try {
            //InputStream inputStream = socket.getInputStream();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            FileOutputStream fileOut = new FileOutputStream("schedule.xml");

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
            return builder.parse(new File("schedule.xml"));

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


    private static void validateUserCredentials(BufferedReader in, PrintWriter out) throws ParserConfigurationException, IOException, SAXException {
        // Get the filepath of the user
        File usersXMLFile = new File("users.xml");

        String username = in.readLine();
        String password = in.readLine();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(usersXMLFile);
        doc.getDocumentElement().normalize();

        NodeList userList = doc.getElementsByTagName("user");

        for (int i = 0; i < userList.getLength(); i++) {
            Node userNode = userList.item(i);
            if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                Element userElement = (Element) userNode;
                String storedUsername = userElement.getElementsByTagName("username").item(0).getTextContent();
                String storedPassword = userElement.getElementsByTagName("password").item(0).getTextContent();

                if (storedUsername.equals(username) && storedPassword.equals(password)) {
                    out.println(true);
                    out.flush();
                    return;
                }
            }
        }

        out.println(false);

    }

    private static void checkUserDataExists(BufferedReader in, PrintWriter out) throws IOException, TransformerException, ParserConfigurationException {
        // Get the username
        String username = in.readLine();

        // Create a userFile
        File userFile = new File("userData" + File.separator + username + ".xml");

        if (userFile.exists()) {
            out.println(true);
        } else {
            out.println(false);
        }

    }

    public static void createUserSpecificXML(BufferedReader in, PrintWriter out) throws ParserConfigurationException, IOException, TransformerException {
        String username = in.readLine();

        File userFile = new File("userData" + File.separator + username + ".xml");

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        // Root element
        Element rootElement = doc.createElement("userProfile");
        doc.appendChild(rootElement);

        // Username element
        Element usernameElement = doc.createElement("username");
        usernameElement.appendChild(doc.createTextNode(username));
        rootElement.appendChild(usernameElement);

        // Write content to file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(userFile);
        transformer.transform(source, result);

    }

    public static void getUserSpecificXML(BufferedReader in, PrintWriter out, DataOutputStream dataOutputStream) throws IOException {
        try {
            String username = in.readLine();
            File userDataDir = new File("userData" + File.separator + username + ".xml");

            if (!userDataDir.exists()) {
                System.err.println("File not found: " + userDataDir.getAbsolutePath());
                dataOutputStream.writeLong(0);
                return;
            }

            try (FileInputStream fileIn = new FileInputStream(userDataDir)) {
                long fileSize = userDataDir.length();
                //System.out.println("Sending file size: " + fileSize);
                dataOutputStream.writeLong(fileSize);
                //System.out.println("Sending file size after: " + fileSize);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytesRead);
                }
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        } catch (IOException e) {
        }
    }


    public static void saveUserToXML(BufferedReader in, PrintWriter out) throws IOException {

        String username = in.readLine();
        String password = in.readLine();

        try {
            File xmlFile = new File("users.xml");
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc;
            // Check if file exists and has content
            if (xmlFile.exists() && xmlFile.length() > 0) {
                doc = docBuilder.parse(xmlFile);
                doc.getDocumentElement().normalize();
            } else {
                // Create a new XML document if the file does not exist
                doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("users");
                doc.appendChild(rootElement);
            }

            // Get root element
            Element root = doc.getDocumentElement();

            // Create user element
            Element userElement = doc.createElement("user");

            // Add username
            Element usernameElement = doc.createElement("username");
            usernameElement.appendChild(doc.createTextNode(username));
            userElement.appendChild(usernameElement);

            // Add password (Note: Ideally, password should be hashed before storing)
            Element passwordElement = doc.createElement("password");
            passwordElement.appendChild(doc.createTextNode(password));
            userElement.appendChild(passwordElement);

            // Append new user to root
            root.appendChild(userElement);

            // Write the updated content back to XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

            //System.out.println("User data saved to " + "users.xml");
            LogServer.log(username + " successfully passed, now converted to xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logAction(BufferedReader in, PrintWriter out) throws IOException {
        String message = in.readLine();
        String action = in.readLine();

        ServerLogXML.log(message, action);
    }

    private static void listenForShutdownCommand() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String command = consoleReader.readLine();
                if ("exit".equalsIgnoreCase(command)) {
                    //System.out.println("Shutting down server...");
                    running = false;
                    ServerLogXML.log("Server", "Stopping");
                    System.exit(0);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
