package uk.ac.ebi.pride.tools.isorelax;

import com.sun.msv.verifier.jarv.TheFactoryImpl;
import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.Reader;

/**
 * Created using IntelliJ IDEA.
 * User: phil
 * Date: 16-Mar-2006
 * Time: 11:40:56
 *
 * @author Phil Jones
 */
public abstract class AbstractXmlValidator {

	/**
	 * This static object is used by the concrete implementation to create the Schema object
	 * used for validation.
	 */
	protected static final VerifierFactory VERIFIER_FACTORY = new TheFactoryImpl();

	/**
	 * This method carries out the work of validating the XML file passed in through
	 * 'inputStream' against the compiled XML schema 'schema'.  This method is a helper
	 * method called by the implementation of this abstract class.
	 *
	 * @param reader being a java.io.Reader from the complete XML file being validated.
	 * @param schema being a compiled schema object built from the appropriate xsd (
	 *               performed by the implementing sub-class of this abstract class.)
	 * @return an XMLValidationErrorHandler that can be queried for details of any
	 *         parsing errors to retrieve plain text or HTML
	 * @throws VerifierConfigurationException
	 * @throws org.xml.sax.SAXException
	 */
	protected static XMLValidationErrorHandler validate(Reader reader, Schema schema)
			throws VerifierConfigurationException, SAXException {

		final XMLValidationErrorHandler xmlValidationErrorHandler = new XMLValidationErrorHandler();
		Verifier schemaVerifier = schema.newVerifier();
		schemaVerifier.setErrorHandler(xmlValidationErrorHandler);
        try {
            schemaVerifier.verify(new InputSource(reader));
        } catch (SAXParseException e) {
            //this will catch well-formedness exceptions that might be thrown
            //by the SAX parser
            // ToDo: these exceptions should be handed to the error handler first and since that does not throw any exceptions, there should not be any reason to have this catch block ...
            xmlValidationErrorHandler.error(e);
        } catch (IOException e) {
            xmlValidationErrorHandler.fatalError(e);
        }
        return xmlValidationErrorHandler;
	}


}
