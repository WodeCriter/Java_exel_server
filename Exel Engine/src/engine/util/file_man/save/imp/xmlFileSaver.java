package engine.util.file_man.save.imp;

import engine.util.file_man.STLConverter.imp.STLConverter;
import engine.spreadsheet.api.Sheet;
import engine.util.jaxb.classes.STLSheet;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import java.io.File;

public class xmlFileSaver {

    private final static String JAXB_PROJECT_XML_CLASSES = "exel.engine.util.jaxb.classes";

    /**
     * Save a Sheet object to an XML file using JAXB.
     * @param sheet The Sheet object to save.
     * @param filePath The path where the XML file will be saved.
     * @return true if the file was saved successfully, false otherwise.
     */
    public static boolean saveSpreadsheet(Sheet sheet, String filePath) {
        try {
            // Create a JAXB context passing in the class of the generated JAXB classes
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_PROJECT_XML_CLASSES);

            // Create a Marshaller from the JAXB Context
            Marshaller marshaller = jaxbContext.createMarshaller();

            // Set property to format the XML output
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // Convert the Sheet object back to an STLSheet object if necessary
            STLSheet stlSheet = STLConverter.toSTLSheet(sheet);

            // Marshal the STLSheet object to a file
            File file = new File(filePath);
            marshaller.marshal(stlSheet, file);

            return true;  // Return true to indicate success
        } catch (JAXBException e) {
            e.printStackTrace();
            return false;  // Return false to indicate failure
        }
    }
}