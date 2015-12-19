package uk.ac.ebi.pride.tools;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian Reisinger
 *         Date: 29-Apr-2009
 * @since $version
 */
public class ValidationErrorHandler implements ErrorHandlerIface {

    List<String> errorMessages = null;

    public ValidationErrorHandler() {
        errorMessages = new ArrayList<String>();
    }

    public boolean noErrors() {
        return errorMessages.size() == 0;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }



    public void warning(SAXParseException exception) throws SAXException {
        StringBuilder sb = new StringBuilder();
        sb.append("Warning: Validation of the XMl has detected the following condition on line ")
                .append(exception.getLineNumber())
                .append("\n  Warning message: ")
                .append(exception.getMessage());
        errorMessages.add( sb.toString() );
    }

    public void error(SAXParseException exception) throws SAXException {
        StringBuilder sb = new StringBuilder();
        sb.append("Non-fatal XML Parsing error detected on line ")
                .append(exception.getLineNumber())
                .append("\n  Error message: ")
                .append(exception.getMessage());
        errorMessages.add(sb.toString());
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        StringBuilder sb = new StringBuilder();
        sb.append("FATAL XML Parsing error detected on line ")
                .append(exception.getLineNumber())
                .append("\n  Fatal Error message: ")
                .append(exception.getMessage());
        errorMessages.add(sb.toString());
    }

    public void fatalError(IOException exception) {
        StringBuilder sb = new StringBuilder();
        sb.append("FATAL XML Validation error. ");
        if (exception instanceof FileNotFoundException) {
            String exceptionMsg = exception.getMessage();
            // we are only interested in the file name that is referenced not the full path
            // (since the full path contains the local part not necessary only the path specified in the XML)
            String sub = exceptionMsg.substring( exceptionMsg.lastIndexOf(File.separatorChar) + 1 );
            sb.append("A needed or referenced File was not found!\n")
                    .append("  ").append(sub)
                    .append("\n  Hint: Please make sure your file does not reference local schema or DTD files.");
        } else {
            sb.append("An I/O error prevented the file from being validated!\n");
        }
        errorMessages.add(sb.toString());
    }

}
