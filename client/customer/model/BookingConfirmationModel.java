package client.customer.model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.sql.Time;

public class BookingConfirmationModel {
    private final TimeSlotModel timeSlotModel;
    private final String userFilePath; // Changed from File
    private final String dataFilePath; // Changed from File

    public BookingConfirmationModel(TimeSlotModel timeSlotModel, String userFilePath, String dataFilePath) {
        this.timeSlotModel = timeSlotModel;
        this.userFilePath = userFilePath;
        this.dataFilePath = dataFilePath;
    }

    public boolean confirmBooking(String machineType, String date, String timeSlot, String username) {
        boolean success = timeSlotModel.updateSlotStatus(machineType, date, timeSlot, "OCCUPIED");
        if (success) {
            saveBookingToUserFile(machineType, date, timeSlot);
            saveBookingToDataXML(username, machineType, date, timeSlot);
        }
        return success;
    }

    private void saveBookingToUserFile(String machineType, String date, String timeSlot) {
        try {
            // Temporary: File operations remain; should be moved to a utility
            File userFile = new File(userFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = userFile.exists() ? builder.parse(userFile) : builder.newDocument();
            if (!userFile.exists()) {
                Element rootElement = doc.createElement("user");
                doc.appendChild(rootElement);
            }

            Element root = doc.getDocumentElement();
            Node bookingsNode = getOrCreateElement(doc, root, "bookings");

            Element bookingElement = doc.createElement("booking");
            bookingElement.appendChild(createElement(doc, "machineType", machineType));
            bookingElement.appendChild(createElement(doc, "date", date));
            bookingElement.appendChild(createElement(doc, "time", timeSlot));
            bookingsNode.appendChild(bookingElement);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(userFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveBookingToDataXML(String username, String machineType, String date, String timeSlot) {
        try {
            // Temporary: File operations remain; should be moved to a utility
            File dataFile = new File(dataFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = dataFile.exists() && dataFile.length() > 0 ? builder.parse(dataFile) : builder.newDocument();
            if (!dataFile.exists() || dataFile.length() == 0) {
                Element rootElement = doc.createElement("bookings");
                doc.appendChild(rootElement);
            }

            Element root = doc.getDocumentElement();
            Element bookingElement = doc.createElement("booking");
            bookingElement.appendChild(createElement(doc, "username", username));
            bookingElement.appendChild(createElement(doc, "machineType", machineType));
            bookingElement.appendChild(createElement(doc, "date", date));
            bookingElement.appendChild(createElement(doc, "time", timeSlot));
            root.appendChild(bookingElement);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(dataFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Element createElement(Document doc, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.setTextContent(textContent);
        return element;
    }

    private Node getOrCreateElement(Document doc, Node parent, String tagName) {
        NodeList list = ((Element) parent).getElementsByTagName(tagName);
        return list.getLength() > 0 ? list.item(0) : parent.appendChild(doc.createElement(tagName));
    }
}