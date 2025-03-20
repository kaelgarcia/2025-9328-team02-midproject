package client.owner.model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.*;
import client.utility.ClientServerConnection;

public class LaundryButtonActions extends UnicastRemoteObject implements LaundryButtonActionsRemote {

    protected LaundryButtonActions() throws RemoteException {
        super();
    }

    private boolean isValidTimeFormat(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setLenient(false);
        try {
            sdf.parse(time);
            return true;
        } catch (Exception e) {
            return false;
        }
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

    public void addTimeToSchedule(String time, String scheduleFilePath) throws RemoteException {
        if (!isValidTimeFormat(time)) {
            System.out.println("Invalid time format. Please use HH:mm format.");
            return;
        }

        try {
            File scheduleFile = new File(scheduleFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(scheduleFile);

            if (isTimeAlreadyBooked(time, doc)) {
                System.out.println("Time slot already booked.");
                return;
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
            e.printStackTrace();
        }
    }

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
                        break;
                    }
                }
            }

            saveToFile(doc, scheduleFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean bookTimeSlot(String time, String scheduleFilePath, String clientName) throws RemoteException {
        if (!isValidTimeFormat(time)) {
            System.out.println("Invalid time format. Please use HH:mm format.");
            return false;
        }

        try {
            File scheduleFile = new File(scheduleFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(scheduleFile);

            if (isTimeAlreadyBooked(time, doc)) {
                System.out.println("Time slot already booked.");
                return false;
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
            e.printStackTrace();
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
            e.printStackTrace();
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
