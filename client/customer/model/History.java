package client.customer.model;

import server.customer.LogServer;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class History {
    private File userFile;

    public History(String userFile) {
        this.userFile = new File(userFile);
    } // ALL WILL BE CONVERTED AS RMI AND JSON

    public List<String[]> loadHistory() {
        List<String[]> historyRecords = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(userFile);
            doc.getDocumentElement().normalize();

            NodeList bookingNodes = doc.getElementsByTagName("booking");
            for (int i = 0; i < bookingNodes.getLength(); i++) {
                Element bookingElement = (Element) bookingNodes.item(i);
                String machineType = bookingElement.getElementsByTagName("machineType").item(0).getTextContent();
                String date = bookingElement.getElementsByTagName("date").item(0).getTextContent();
                String time = bookingElement.getElementsByTagName("time").item(0).getTextContent();
                NodeList commentNodes = bookingElement.getElementsByTagName("comment");
                String comment = commentNodes.getLength() > 0 ? commentNodes.item(0).getTextContent() : "";
                historyRecords.add(new String[]{machineType, date, time, comment});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return historyRecords;
    }

    public void updateComment(String machineType, String date, String time, String comment) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(userFile);
            doc.getDocumentElement().normalize();

            NodeList bookingNodes = doc.getElementsByTagName("booking");
            for (int i = 0; i < bookingNodes.getLength(); i++) {
                Element bookingElement = (Element) bookingNodes.item(i);
                String existingMachine = bookingElement.getElementsByTagName("machineType").item(0).getTextContent();
                String existingDate = bookingElement.getElementsByTagName("date").item(0).getTextContent();
                String existingTime = bookingElement.getElementsByTagName("time").item(0).getTextContent();

                if (existingMachine.equals(machineType) && existingDate.equals(date) && existingTime.equals(time)) {
                    NodeList commentNodes = bookingElement.getElementsByTagName("comment");
                    if (commentNodes.getLength() > 0) {
                        commentNodes.item(0).setTextContent(comment);
                    } else {
                        Element commentElement = doc.createElement("comment");
                        commentElement.setTextContent(comment);
                        bookingElement.appendChild(commentElement);
                    }
                    break;
                }
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(userFile));

            String username = userFile.getName().replace(".xml", "");
            LogServer.log(username + " commented on " + machineType + " at " + date + ", " + time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean canDelete(String bookingDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy");
            Calendar today = Calendar.getInstance();
            int currentYear = today.get(Calendar.YEAR);

            Date bookedDate = dateFormat.parse(bookingDate + " " + currentYear);
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            Calendar booking = Calendar.getInstance();
            booking.setTime(bookedDate);

            long diffDays = (booking.getTimeInMillis() - today.getTimeInMillis()) / (24 * 60 * 60 * 1000);
            return diffDays >= 15;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteBooking(String machineType, String date, String time) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(userFile);
            doc.getDocumentElement().normalize();

            NodeList bookingNodes = doc.getElementsByTagName("booking");
            Node bookingToDelete = null;
            for (int i = 0; i < bookingNodes.getLength(); i++) {
                Element bookingElement = (Element) bookingNodes.item(i);
                String existingMachine = bookingElement.getElementsByTagName("machineType").item(0).getTextContent();
                String existingDate = bookingElement.getElementsByTagName("date").item(0).getTextContent();
                String existingTime = bookingElement.getElementsByTagName("time").item(0).getTextContent();

                if (existingMachine.equals(machineType) && existingDate.equals(date) && existingTime.equals(time)) {
                    bookingToDelete = bookingElement;
                    break;
                }
            }

            if (bookingToDelete != null) {
                bookingToDelete.getParentNode().removeChild(bookingToDelete);
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(new DOMSource(doc), new StreamResult(userFile));
                markSlotAsVacant(machineType, date, time);

                String username = userFile.getName().replace(".xml", "");
                LogServer.log(username + " cancelled " + machineType + " at " + date + ", " + time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void markSlotAsVacant(String machineType, String date, String time) {
        try {
            File scheduleFile = new File("schedule.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(scheduleFile);
            doc.getDocumentElement().normalize();

            NodeList machineNodes = doc.getElementsByTagName(machineType.toLowerCase());
            for (int i = 0; i < machineNodes.getLength(); i++) {
                Element machineElement = (Element) machineNodes.item(i);
                NodeList dayNodes = machineElement.getElementsByTagName("day");
                for (int j = 0; j < dayNodes.getLength(); j++) {
                    Element dayElement = (Element) dayNodes.item(j);
                    if (dayElement.getAttribute("date").equals(date)) {
                        NodeList timeSlots = dayElement.getElementsByTagName("time");
                        for (int k = 0; k < timeSlots.getLength(); k++) {
                            Element timeElement = (Element) timeSlots.item(k);
                            if (timeElement.getAttribute("slot").equals(time)) {
                                timeElement.setTextContent("VACANT");
                                break;
                            }
                        }
                        break;
                    }
                }
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(scheduleFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}