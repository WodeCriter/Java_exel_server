package engine.util.file_man.load.imp;

import engine.util.file_man.STLConverter.imp.STLConverter;
import engine.spreadsheet.api.Sheet;
import engine.util.jaxb.classes.STLSheet;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class xmlFileLoader {

    private final static String JAXB_PROJECT_XML_CLASSES = "exel.engine.util.jaxb.classes";
    /**
     * Load an XML file into a Java object based on JAXB generated classes.
     * @param filePath The path to the XML file.
     * @return The loaded object or null if an error occurs.
     */
    public static Sheet loadSpreadsheet(String filePath) {
        File file = new File(filePath);
        try
        {
            return loadSpreadsheet(new FileInputStream(file));
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static Sheet loadSpreadsheet(InputStream fileContent){
        try {
            // Create a JAXB context passing in the class of the generated JAXB classes
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_PROJECT_XML_CLASSES);

            // Create an Unmarshaller from the JAXB Context
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // Unmarshal the XML content to Java object
            STLSheet sheetFromFile = (STLSheet) unmarshaller.unmarshal(fileContent);
            return STLConverter.fromSTLSheet(sheetFromFile);
        } catch (JAXBException e) {
            return null; // or handle the error as appropriate
        }
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        xmlFileLoader loader = new xmlFileLoader();
        Sheet spreadsheet = loader.loadSpreadsheet("path/to/your/spreadsheet.xml");

        if (spreadsheet == null) {
            throw new RuntimeException("Failed to load the spreadsheet.");
        }
    }
}
