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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DryerButtonActions {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    public boolean addTimeToSchedule(String time, File scheduleFile) {
        if (!isValidTimeFormat(time)) {
            System.out.println("Invalid time format. Use HH:mm (24-hour format).");
            return false;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(scheduleFile);

            NodeList daysList = doc.getElementsByTagName("day");
            for (int i = 0; i < daysList.getLength(); i++) {
                Element day = (Element) daysList.item(i);

                if (isTimeSlotTaken(day, time)) {
                    System.out.println("Time slot " + time + " is already booked.");
                    return false;
                }

                Element newTimeSlot = doc.createElement("time");
                newTimeSlot.setAttribute("slot", time);
                newTimeSlot.setTextContent("VACANT");
                day.appendChild(newTimeSlot);
            }

            saveToFile(doc, scheduleFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean bookTimeSlot(String time, File scheduleFile, String clientName) {
        if (!isValidTimeFormat(time)) {
            System.out.println("Invalid time format.");
            return false;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(scheduleFile);

            NodeList daysList = doc.getElementsByTagName("day");
            for (int i = 0; i < daysList.getLength(); i++) {
                Element day = (Element) daysList.item(i);

                if (isTimeSlotTaken(day, time)) {
                    System.out.println("Time slot " + time + " is already booked.");
                    return false;
                }

                Element newTimeSlot = doc.createElement("time");
                newTimeSlot.setAttribute("slot", time);
                newTimeSlot.setTextContent(clientName);
                day.appendChild(newTimeSlot);
            }

            saveToFile(doc, scheduleFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidTimeFormat(String time) {
        try {
            TIME_FORMAT.setLenient(false);
            TIME_FORMAT.parse(time);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isTimeSlotTaken(Element day, String time) {
        NodeList timeSlots = day.getElementsByTagName("time");
        for (int j = 0; j < timeSlots.getLength(); j++) {
            Element timeSlot = (Element) timeSlots.item(j);
            if (timeSlot.getAttribute("slot").equals(time)) {
                return true;
            }
        }
        return false;
    }

    private void saveToFile(Document doc, File scheduleFile) {
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

    public void deleteTimeFromSchedule(String time, File scheduleFile) {
        if (!isValidTimeFormat(time)) {
            System.out.println("Invalid time format.");
            return;
        }

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
                        day.removeChild(timeSlot);
                        saveToFile(doc, scheduleFile);
                        System.out.println("Time slot " + time + " has been removed.");
                        return;
                    }
                }
            }

            System.out.println("Time slot " + time + " not found.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String[]> filterTransactions(List<String[]> allTransactions, String searchQuery) {
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

        try {
            File file = new File("dryer_transactions.csv");
            if (!file.exists()) {
                System.out.println("Transaction file not found.");
                return transactions;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] transaction = line.split(",");
                transactions.add(transaction);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;
    }
}