package client.owner.model;

import client.utility.ClientServerConnection;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DryerButtonActions {
    public void addTimeToSchedule(String time, File scheduleFile) {
        try {
            System.out.println("Attempting to add time: " + time);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(scheduleFile);

            NodeList daysList = doc.getElementsByTagName("day");
            for (int i = 0; i < daysList.getLength(); i++) {
                Element day = (Element) daysList.item(i);

                // Create new time slot element
                Element newTimeSlot = doc.createElement("time");
                newTimeSlot.setAttribute("slot", time);
                newTimeSlot.setTextContent("VACANT");

                // Append to the day node
                day.appendChild(newTimeSlot);
                System.out.println("Added time to " + day.getAttribute("date"));
            }

            saveToFile(doc, scheduleFile);
            System.out.println("Time added successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTimeFromSchedule(String time, File scheduleFile) {
        try {
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
                        day.removeChild(timeSlot); // Remove the node
                        break;
                    }
                }
            }

            saveToFile(doc, scheduleFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String[]> filterTransactions(List<String[]> allTransactions, String searchQuery) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return allTransactions; // Return all if search query is empty
        }

        List<String[]> filteredTransactions = new ArrayList<>();
        for (String[] transaction : allTransactions) {
            for (String field : transaction) {
                if (field.toLowerCase().contains(searchQuery.toLowerCase())) {
                    filteredTransactions.add(transaction);
                    break;
                }
            }
        }

        return filteredTransactions;
    }

    public List<String[]> loadDryerTransactions() {
        List<String[]> transactions = new ArrayList<>();

        File userDataDir = new File("userData");
        if (userDataDir.exists() && userDataDir.isDirectory()) {
            File[] userFiles = userDataDir.listFiles((dir, name) -> name.endsWith(".xml"));
            if (userFiles != null) {
                for (File file : userFiles) {
                    transactions.addAll(loadDryerTransactionsFromFile(file));
                }
            }
        }

        return transactions;
    }

    private List<String[]> loadDryerTransactionsFromFile(File file) {
        List<String[]> transactions = new ArrayList<>();
        try {
            String username = Paths.get(file.getName()).getFileName().toString().replace(".xml", "");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList bookingsList = doc.getElementsByTagName("booking");
            for (int i = 0; i < bookingsList.getLength(); i++) {
                Element bookingElement = (Element) bookingsList.item(i);
                String machineType = bookingElement.getElementsByTagName("machineType").item(0).getTextContent();

                if ("dryer".equalsIgnoreCase(machineType)) {
                    String date = bookingElement.getElementsByTagName("date").item(0).getTextContent();
                    String time = bookingElement.getElementsByTagName("time").item(0).getTextContent();

                    if (date != null && time != null) {
                        // Add transaction with username, date, and time
                        transactions.add(new String[]{username, date, time});
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactions;
    }

    private void saveToFile(Document doc, File scheduleFile) {
        try (FileOutputStream output = new FileOutputStream(scheduleFile)) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(output));

            ClientServerConnection client = ClientServerConnection.getInstance();
            client.requestToServer("ReceiveFile");
            client.sendXMLFileToServer(String.valueOf(scheduleFile)); // must have method in server
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
