package uk.ac.ebi.pride.tools.isorelax;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

/**
 * Created using IntelliJ IDEA.
 * User: phil
 * Date: 16-Mar-2006
 * Time: 12:05:01
 *
 * @author Phil Jones
 */
public class PrideXmlValidatorIsoRelax extends AbstractXmlValidator {

	private static Schema SCHEMA = null;

	private static final String SCHEMA_NAME = "pride.xsd";

	/**
	 * This method must be implemented to create a suitable Schema object for the
	 * xsd file in question.
	 *
	 * @param reader the XML file being validated as a Stream (Reader)
	 * @return an XMLValidationErrorHandler that can be queried to return all of the
	 *         error in the XML file as plain text or HTML.
	 */
	public static XMLValidationErrorHandler validate(Reader reader) throws IOException, VerifierConfigurationException, SAXException {
        URL schemaLocation = PrideXmlValidatorIsoRelax.class.getClassLoader().getResource(SCHEMA_NAME);
        if (schemaLocation == null) {
            throw new IllegalStateException("Could not find PRIDE XML schema!");
        }
        Schema schema = VERIFIER_FACTORY.compileSchema(PrideXmlValidatorIsoRelax.class.getClassLoader().getResourceAsStream(SCHEMA_NAME));
		return validate(reader, schema);
	}

    public static XMLValidationErrorHandler validate(Reader reader, URL schemaUrl) throws IOException, VerifierConfigurationException, SAXException {
		if (schemaUrl == null) {
            throw new IllegalArgumentException("No valid schema URL provided!" + schemaUrl);
        }
        Schema schema = VERIFIER_FACTORY.compileSchema(schemaUrl.openStream());

		return validate(reader, schema);
	}

    public static XMLValidationErrorHandler validate(Reader reader, File schemaFile) throws IOException, VerifierConfigurationException, SAXException {
		if (schemaFile == null || !schemaFile.exists() || !schemaFile.canRead() ) {
            throw new IllegalArgumentException("No valid schema URL provided!" + schemaFile);
        }
        Schema schema = VERIFIER_FACTORY.compileSchema(new FileInputStream(schemaFile));

		return validate(reader, schema);
	}
}
