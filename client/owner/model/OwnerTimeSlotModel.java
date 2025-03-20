package client.owner.model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class OwnerTimeSlotModel {
    private List<String> timeSlots;
    private String machineType;
    private int selectedDay;

    private static final Pattern TIME_FORMAT_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");

    public OwnerTimeSlotModel(String machineType, int selectedDay) {  // implement remote access using RMI + JSON
        this.machineType = machineType;
        this.selectedDay = selectedDay;
        this.timeSlots = new ArrayList<>();
        loadTimeSlotsFromXML();
    }

    private void loadTimeSlotsFromXML() {
        try {
            File xmlFile = new File("schedule.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList machines = doc.getElementsByTagName(machineType);
            for (int i = 0; i < machines.getLength(); i++) {
                Node machineNode = machines.item(i);
                if (machineNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element machineElement = (Element) machineNode;
                    NodeList days = machineElement.getElementsByTagName("day");

                    for (int j = 0; j < days.getLength(); j++) {
                        Element dayElement = (Element) days.item(j);
                        if (dayElement.getAttribute("date").equals("March " + selectedDay)) {
                            NodeList timeNodes = dayElement.getElementsByTagName("time");
                            for (int k = 0; k < timeNodes.getLength(); k++) {
                                Element timeElement = (Element) timeNodes.item(k);
                                String slot = timeElement.getAttribute("slot");
                                String status = timeElement.getTextContent().trim();
                                timeSlots.add(slot + " (" + status + ")");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getTimeSlots() {
        return timeSlots;
    }

    public boolean isTimeSlotAvailable(String time) {
        if (!isValidTimeFormat(time)) {
            System.out.println("Invalid time format. Use HH:MM (24-hour format).");
            return false;
        }

        for (String slot : timeSlots) {
            if (slot.startsWith(time) && slot.contains("Booked")) {
                return false;  // Time slot is already booked
            }
        }
        return true;
    }

    public boolean bookTimeSlot(String time) {
        if (!isValidTimeFormat(time)) {
            System.out.println("Invalid time format. Use HH:MM (24-hour format).");
            return false;
        }

        if (!isTimeSlotAvailable(time)) {
            System.out.println("This time slot is already booked.");
            return false;
        }

        // Add the booked slot
        timeSlots.add(time + " (Booked)");
        saveBookingToXML(time);
        System.out.println("Time slot booked successfully!");
        return true;
    }

    private boolean isValidTimeFormat(String time) {
        return TIME_FORMAT_PATTERN.matcher(time).matches();
    }

    private void saveBookingToXML(String time) {
        try {
            File xmlFile = new File("schedule.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList machines = doc.getElementsByTagName(machineType);
            for (int i = 0; i < machines.getLength(); i++) {
                Node machineNode = machines.item(i);
                if (machineNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element machineElement = (Element) machineNode;
                    NodeList days = machineElement.getElementsByTagName("day");

                    for (int j = 0; j < days.getLength(); j++) {
                        Element dayElement = (Element) days.item(j);
                        if (dayElement.getAttribute("date").equals("March " + selectedDay)) {
                            Element newTimeElement = doc.createElement("time");
                            newTimeElement.setAttribute("slot", time);
                            newTimeElement.setTextContent("Booked");
                            dayElement.appendChild(newTimeElement);
                        }
                    }
                }
            }

            // Save changes to XML file
            FileWriter writer = new FileWriter("schedule.xml");
            writer.write(doc.toString());
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
