package client.owner.model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OwnerTimeSlotModel {
    private List<String> timeSlots;
    private String machineType;
    private int selectedDay;

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
}
