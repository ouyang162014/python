package uk.ac.ebi.pride.tools.isorelax;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Edited using IntelliJ IDEA
 * Date: 16-Sep-2005
 * Time: 15:26:12
 *
 * @author Phil Jones
 */
public class XMLValidationErrorHandler implements ErrorHandler {

	public XMLValidationErrorHandler() {
		super();
	}

	private StringBuffer errorMessageBuffer = null;

	public boolean noErrors() {
		return errorMessageBuffer == null;
	}

	public String getErrorsFormattedAsPlainText() {
		if (noErrors()) {
			return null;
		}
		return errorMessageBuffer.toString();
	}

	public String getErrorsFormattedAsHTML() {
		if (noErrors()) {
			return null;
		}
		return (errorMessageBuffer.toString().replaceAll("<", "&lt;")).replaceAll(">", "&gt;").replaceAll("\\n", "<br/>");
	}


	private void initialiseErrorMessageBuffer() {
		if (errorMessageBuffer == null) {
			errorMessageBuffer = new StringBuffer("Unfortunately, your XML document does not conform to the XML schema for the following reasons:\n");
		}
	}

	/**
	 * Receive notification of a recoverable error.
	 * <p/>
	 * <p>This corresponds to the definition of "error" in section 1.2
	 * of the W3C XML 1.0 Recommendation.  For example, a validating
	 * parser would use this callback to report the violation of a
	 * validity constraint.  The default behaviour is to take no
	 * action.</p>
	 * <p/>
	 * <p>The SAX parser must continue to provide normal parsing events
	 * after invoking this method: it should still be possible for the
	 * application to process the document through to the end.  If the
	 * application cannot do so, then the parser should report a fatal
	 * error even if the XML 1.0 recommendation does not require it to
	 * do so.</p>
	 * <p/>
	 * <p>Filters may use this method to report other, non-XML errors
	 * as well.</p>
	 *
	 * @param exception The error information encapsulated in a
	 *                  SAX parse exception.
	 * @throws org.xml.sax.SAXException Any SAX exception, possibly
	 *                                  wrapping another exception.
	 * @see org.xml.sax.SAXParseException
	 */
	public void error(SAXParseException exception) throws SAXException {
		initialiseErrorMessageBuffer();
		errorMessageBuffer.append("\n\nNon-fatal XML Parsing error detected on line ")
				.append(exception.getLineNumber())
				.append("\nError message: ")
				.append(exception.getMessage());
	}

	/**
	 * Receive notification of a non-recoverable error.
	 * <p/>
	 * <p>This corresponds to the definition of "fatal error" in
	 * section 1.2 of the W3C XML 1.0 Recommendation.  For example, a
	 * parser would use this callback to report the violation of a
	 * well-formedness constraint.</p>
	 * <p/>
	 * <p>The application must assume that the document is unusable
	 * after the parser has invoked this method, and should continue
	 * (if at all) only for the sake of collecting addition error
	 * messages: in fact, SAX parsers are free to stop reporting any
	 * other events once this method has been invoked.</p>
	 *
	 * @param exception The error information encapsulated in a
	 *                  SAX parse exception.
	 * @throws org.xml.sax.SAXException Any SAX exception, possibly
	 *                                  wrapping another exception.
	 * @see org.xml.sax.SAXParseException
	 */
	public void fatalError(SAXParseException exception) throws SAXException {
		initialiseErrorMessageBuffer();
		errorMessageBuffer.append("\n\nFATAL XML Parsing error detected on line ")
				.append(exception.getLineNumber())
				.append("\nFatal Error message: ")
				.append(exception.getMessage());
	}

    /**
     * This is a extension method to the #fatalError(SAXParseException) method to
     * allow the handling of IOExceptions.
     * Example: An FileNotFoundException is thrown by the validate method if the file 
     *          to be validated contains a local reference to a DTD (which can not be
     *          found on the server).
     *
     * @param exception
     */
    public void fatalError(IOException exception) {
        initialiseErrorMessageBuffer();
        errorMessageBuffer.append("\n\nFATAL XML Validation error. ");
        if (exception instanceof FileNotFoundException) {
            errorMessageBuffer.append("A needed or referenced File was not found!\n")
                    .append(exception.getMessage())
                    .append("\nHint: Please make sure your file does not reference local schema or DTD files.");
        } else {
            errorMessageBuffer.append("An I/O error prevented the file from being validated!\n");
        }
    }

	/**
	 * Receive notification of a warning.
	 * <p/>
	 * <p>SAX parsers will use this method to report conditions that
	 * are not errors or fatal errors as defined by the XML 1.0
	 * recommendation.  The default behaviour is to take no action.</p>
	 * <p/>
	 * <p>The SAX parser must continue to provide normal parsing events
	 * after invoking this method: it should still be possible for the
	 * application to process the document through to the end.</p>
	 * <p/>
	 * <p>Filters may use this method to report other, non-XML warnings
	 * as well.</p>
	 *
	 * @param exception The warning information encapsulated in a
	 *                  SAX parse exception.
	 * @throws org.xml.sax.SAXException Any SAX exception, possibly
	 *                                  wrapping another exception.
	 * @see org.xml.sax.SAXParseException
	 */
	public void warning(SAXParseException exception) throws SAXException {
		initialiseErrorMessageBuffer();
		errorMessageBuffer.append("\n\nWarning: Validation of the XMl has detected the following condition on line ")
				.append(exception.getLineNumber())
				.append("\nWarning message: ")
				.append(exception.getMessage());
	}
}
