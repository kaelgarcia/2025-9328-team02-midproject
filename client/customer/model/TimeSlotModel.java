package client.customer.model;

import client.utility.ClientServerConnection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotModel { // ALL WILL BE CONVERTED AS RMI AND JSON
    private final String machineType;
    private final String date;
    private String scheduleFilePath;

    public TimeSlotModel(String machineType, String date) {
        this.machineType = machineType;
        this.date = date;
    }

    public List<String> getTimeSlotsForDate(String machineType, String date) {
        List<String> availableSlots = new ArrayList<>();
        try {
            ClientServerConnection client = ClientServerConnection.getInstance();
            client.connect();
            client.requestToServer("GetTimeSlots");
            Document document = client.receiveXMLFile("localFilesUser" + File.separator + "Sched.xml");

            if (document == null) return availableSlots;

            document.getDocumentElement().normalize();
            NodeList machineNodes = document.getElementsByTagName(machineType.toLowerCase());
            for (int i = 0; i < machineNodes.getLength(); i++) {
                Element machineElement = (Element) machineNodes.item(i);
                NodeList dayNodes = machineElement.getElementsByTagName("day");
                for (int j = 0; j < dayNodes.getLength(); j++) {
                    Element dayElement = (Element) dayNodes.item(j);
                    if (dayElement.getAttribute("date").equals(date)) {
                        NodeList slotNodes = dayElement.getElementsByTagName("time");
                        for (int k = 0; k < slotNodes.getLength(); k++) {
                            Element slotElement = (Element) slotNodes.item(k);
                            if ("VACANT".equalsIgnoreCase(slotElement.getTextContent().trim())) {
                                availableSlots.add(slotElement.getAttribute("slot"));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return availableSlots;
    }

    public boolean updateSlotStatus(String machineType, String date, String timeSlot, String status) {
        try {
            machineType = machineType.toLowerCase();
            File inputFile = new File(scheduleFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputFile);
            document.getDocumentElement().normalize();

            NodeList machineNodes = document.getElementsByTagName(machineType);
            for (int i = 0; i < machineNodes.getLength(); i++) {
                Element machineElement = (Element) machineNodes.item(i);
                NodeList dayNodes = machineElement.getElementsByTagName("day");
                for (int j = 0; j < dayNodes.getLength(); j++) {
                    Element dayElement = (Element) dayNodes.item(j);
                    if (dayElement.getAttribute("date").equals(date)) {
                        NodeList slotNodes = dayElement.getElementsByTagName("time");
                        for (int k = 0; k < slotNodes.getLength(); k++) {
                            Element slotElement = (Element) slotNodes.item(k);
                            if (slotElement.getAttribute("slot").equals(timeSlot)) {
                                slotElement.setTextContent(status);
                                saveXML(document);
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveXML(Document document) throws TransformerException, FileNotFoundException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(new FileOutputStream(scheduleFilePath)));
    }
    public String getSlotStatus(String machineType, String date, String timeSlot) {
        try {
            File inputFile = new File(scheduleFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputFile);
            document.getDocumentElement().normalize();

            NodeList machineNodes = document.getElementsByTagName(machineType);
            for (int i = 0; i < machineNodes.getLength(); i++) {
                Element machineElement = (Element) machineNodes.item(i);
                NodeList dayNodes = machineElement.getElementsByTagName("day");
                for (int j = 0; j < dayNodes.getLength(); j++) {
                    Element dayElement = (Element) dayNodes.item(j);
                    if (dayElement.getAttribute("date").equals(date)) {
                        NodeList slotNodes = dayElement.getElementsByTagName("time");
                        for (int k = 0; k < slotNodes.getLength(); k++) {
                            Element slotElement = (Element) slotNodes.item(k);
                            if (slotElement.getAttribute("slot").equals(timeSlot)) {
                                return slotElement.getTextContent();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAvailableTimeSlots() {
        return getTimeSlotsForDate(machineType, date);
    }

    public String getSlotStatus(String timeSlot) {
        return getSlotStatus(machineType, date, timeSlot);
    }

    public String getMachineType() {
        return machineType;
    }

    public String getDate() {
        return date;
    }
}