package server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerLogXML {
    private static final String FILE_PATH = "server/logs.xml";

    public static void log(String message, String action) {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setIgnoringElementContentWhitespace(true); // Normalize whitespace
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;

            if (file.length() > 0) {
                doc = dBuilder.parse(file);
                doc.getDocumentElement().normalize();
            } else {
                doc = dBuilder.newDocument();
                Element rootElement = doc.createElement("Logs");
                doc.appendChild(rootElement);
            }

            Element logEntry = doc.createElement("Log");
            doc.getDocumentElement().appendChild(logEntry);

            Element timeElement = doc.createElement("Time");
            timeElement.appendChild(doc.createTextNode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).trim()));
            logEntry.appendChild(timeElement);

            Element messageElement = doc.createElement("Message");
            messageElement.appendChild(doc.createTextNode(message.trim()));
            logEntry.appendChild(messageElement);

            Element actionElement = doc.createElement("Action");
            actionElement.appendChild(doc.createTextNode(action.trim()));
            logEntry.appendChild(actionElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " | " + message + " | " + action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
