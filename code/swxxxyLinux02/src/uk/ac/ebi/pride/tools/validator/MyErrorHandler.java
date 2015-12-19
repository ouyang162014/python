package uk.ac.ebi.pride.tools.validator;

/**
 * This ErrorHandler prints any Warning , Error or Fatal Error.
 *
 * @author  Neeraj Bajaj, Sun Microsystems.
 */
public class MyErrorHandler implements org.xml.sax.ErrorHandler{
    
    /** Creates a new instance of MyErrorHandler */
    public MyErrorHandler() {
    }
    

    public void error(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
        System.out.println("ERROR: " + sAXParseException.toString());
    }

    public void fatalError(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
        System.out.println("FATAL ERROR: " + sAXParseException.toString());
    }

    public void warning(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
        System.out.println("WARNING: " + sAXParseException.toString());
    }
    
}
