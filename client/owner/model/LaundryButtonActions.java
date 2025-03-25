package client.owner.model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.regex.Pattern;
import client.utility.ClientServerConnection;

public class LaundryButtonActions extends UnicastRemoteObject implements LaundryButtonActionsRemote {

    private static final Pattern TIME_FORMAT_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");

    protected LaundryButtonActions() throws RemoteException {
        super();
    }

    public boolean isValidTimeFormat(String time) {
        return TIME_FORMAT_PATTERN.matcher(time).matches();
    }

    @Override
    public boolean isTimeAlreadyBooked(String time, String s) {
        return false;
    }

    private boolean isTimeAlreadyBooked(String time, Document doc) {
        NodeList timeSlots = doc.getElementsByTagName("time");
        for (int i = 0; i < timeSlots.getLength(); i++) {
            Element timeSlot = (Element) timeSlots.item(i);
            if (timeSlot.getAttribute("slot").equals(time) && !timeSlot.getTextContent().equals("VACANT")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addTimeToSchedule(String time, String scheduleFilePath) throws RemoteException {
        if (!isValidTimeFormat(time)) {
            throw new RemoteException("Invalid time format. Use HH:MM (24-hour format).");
        }

        try {
            File scheduleFile = new File(scheduleFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(scheduleFile);

            if (isTimeAlreadyBooked(time, doc)) {
                throw new RemoteException("Time slot already booked.");
            }

            NodeList daysList = doc.getElementsByTagName("day");
            for (int i = 0; i < daysList.getLength(); i++) {
                Element day = (Element) daysList.item(i);
                Element newTimeSlot = doc.createElement("time");
                newTimeSlot.setAttribute("slot", time);
                newTimeSlot.setTextContent("VACANT");
                day.appendChild(newTimeSlot);
            }

            saveToFile(doc, scheduleFile);
        } catch (Exception e) {
            throw new RemoteException("Error updating schedule: " + e.getMessage());
        }
    }

    @Override
    public void deleteTimeFromSchedule(String time, String scheduleFilePath) throws RemoteException {
        try {
            File scheduleFile = new File(scheduleFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(scheduleFile);

            NodeList daysList = doc.getElementsByTagName("day");
            for (int i = 0; i < daysList.getLength(); i++) {
                Element day = (Element) daysList.item(i);
                NodeList timeSlots = day.getElementsByTagName("time");

                for (int j = 0; j < timeSlots.getLength(); j++) {
                    Element timeSlot = (Element) timeSlots.item(j);
                    if (timeSlot.getAttribute("slot").equals(time)) {
                        day.removeChild(timeSlot);
                        saveToFile(doc, scheduleFile);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            throw new RemoteException("Error deleting time slot: " + e.getMessage());
        }
    }

    @Override
    public List<String[]> filterTransactions(List<String[]> allTransactions, String searchQuery) throws RemoteException {
        return List.of();
    }

    @Override
    public List<String[]> loadLaundryTransactions() throws RemoteException {
        return List.of();
    }

    @Override
    public boolean isTimeSlotAvailable(String time, String scheduleFilePath) throws RemoteException {
        return false;
    }

    @Override
    public boolean bookTimeSlot(String time, String scheduleFilePath, String clientName) throws RemoteException {
        if (!isValidTimeFormat(time)) {
            throw new RemoteException("Invalid time format. Use HH:MM (24-hour format).");
        }

        try {
            File scheduleFile = new File(scheduleFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(scheduleFile);

            if (isTimeAlreadyBooked(time, doc)) {
                throw new RemoteException("Time slot already booked.");
            }

            NodeList daysList = doc.getElementsByTagName("day");
            for (int i = 0; i < daysList.getLength(); i++) {
                Element day = (Element) daysList.item(i);
                NodeList timeSlots = day.getElementsByTagName("time");

                for (int j = 0; j < timeSlots.getLength(); j++) {
                    Element timeSlot = (Element) timeSlots.item(j);
                    if (timeSlot.getAttribute("slot").equals(time) && timeSlot.getTextContent().equals("VACANT")) {
                        timeSlot.setTextContent(clientName);
                        saveToFile(doc, scheduleFile);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new RemoteException("Error booking time slot: " + e.getMessage());
        }
        return false;
    }

    private void saveToFile(Document doc, File scheduleFile) throws RemoteException {
        try (FileOutputStream output = new FileOutputStream(scheduleFile)) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(output));

            ClientServerConnection client = ClientServerConnection.getInstance();
            client.requestToServer("ReceiveFile");
            client.sendXMLFileToServer(scheduleFile.getAbsolutePath());
        } catch (Exception e) {
            throw new RemoteException("Error saving schedule: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            LaundryButtonActionsRemote obj = new LaundryButtonActions();
            Naming.rebind("LaundryService", obj);
            System.out.println("Laundry Service is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
