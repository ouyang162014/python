package uk.ac.ebi.pride.tools;

import org.iso_relax.verifier.VerifierConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import uk.ac.ebi.pride.tools.cl.PrideXmlClValidator;

import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * The GenericSchemaValidator  
 *
 *
 * @author Florian Reisinger
 *         Date: 29-Apr-2009
 * @since 0.1
 */
public class GenericSchemaValidator {


    /**
     * This static SchemaFactory is used to create the Schema object
     * used for validation.
     */
    private static final SchemaFactory SCHEMA_FACTORY = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

    /**
     * Read buffer size in byte.
     */
    private int readBufferSize = -1;

    /**
     * The schema to validate against.
     */
    private Schema schema = null;

    private ErrorHandlerIface errorHandler = null;

    ///// ///// ///// ///// ///// ///// ///// ///// ///// /////
    // Constructor
    public GenericSchemaValidator() {}

    ///// ///// ///// ///// ///// ///// ///// ///// ///// /////
    // Getter & Setter

    public void setSchema(URI aSchemaUri) throws SAXException, MalformedURLException {
        schema = SCHEMA_FACTORY.newSchema(aSchemaUri.toURL());
    }

    public Schema getSchema() {
        return schema;
    }

    public ErrorHandlerIface getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(ErrorHandlerIface errorHandler) {
        this.errorHandler = errorHandler;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public void setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
    }

    ///// ///// ///// ///// ///// ///// ///// ///// ///// /////
    // Methods

    /**
     * This method carries out the work of validating the XML file passed in through
     * 'inputStream' against the compiled XML schema 'schema'.  This method is a helper
     * method called by the implementation of this abstract class.
     *
     * @param reader being a java.io.Reader from the complete XML file being validated.
     * @param schema being a compiled schema object built from the appropriate xsd (
     *               performed by the implementing sub-class of this abstract class.)
     * @return an ErrorHandlerIface that can be queried for details of any
     *         parsing errors to retrieve plain text or HTML
     * @throws org.xml.sax.SAXException forwarded from the SAXParser reading the input XML file.
     */
    protected ErrorHandlerIface validate(Reader reader, Schema schema)
            throws SAXException {

        if ( errorHandler == null ) {
             errorHandler = new ValidationErrorHandler();
        }
        Validator validator = schema.newValidator();
        validator.setErrorHandler(errorHandler);
        try {
            validator.validate( new SAXSource( new InputSource(reader) ) );
        } catch (IOException ioe) {
            errorHandler.fatalError(ioe);
        } catch (SAXParseException spe) {
            errorHandler.fatalError(spe);
        }
        return errorHandler;
    }

    /**
     * This method must be implemented to create a suitable Schema object for the
     * xsd file in question.
     *
     * @param reader Reader to the XML file to be validated.
     * @return an ErrorHandlerIface that can be queried to return all of the
     *         error in the XML file as plain text or HTML.
     * @throws SAXException forwarded from the SAXParser reading the input XML file.
     */
    public ErrorHandlerIface validate(Reader reader) throws SAXException {
        if (schema == null) {
            throw new IllegalStateException("No schema found to validate against! A schema " +
                    "has to be set before validation. See 'setSchema(File aSchemaFile)' method!");
        }
        return validate(reader, schema);
    }

    /**
     * Simple and crude estimation for a buffer size when using a
     * BufferedReader to read a file for validation.
     * ToDo: refine a bit!
     *
     * @param file the file to validate an for which to estimate the buffer size.
     * @return an estimation for a buffer size (in byte).
     */
    public static int suggestBufferSize(File file) {
        long fileLength = file.length();
        // assume a fast validation requires as little as possible I/O operations
        // 1000 reads per file seems sensible
        long estimate = fileLength / 1000;
        // also check that the suggested size in not bigger than 10MB
        if (estimate > 10485760 ) {
            estimate = 10485760;
        }
        // or smaller than 1KB
        if (estimate < 1024) {
            estimate = 1024;
        }
        return (int) estimate;
    }

    ///// ///// ///// ///// ///// ///// ///// ///// ///// /////
    // Methods for command line execution

    /**
     * The main method allows the usage of the validator as a command line tool.
     *
     * @param args the provided command line arguments.
     */
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();
        GenericSchemaValidator validator = new GenericSchemaValidator();

        // check if the required number of arguments where supplied
        if(args == null || args.length != 2) {
            printUsage();
            System.exit(1);
        }

        // Check schema file.
        URI schemaUri = null;

        // check if we can use the file location as given
        File schemaFile = new File(args[0]);
        if (schemaFile.exists()) {
            schemaUri = schemaFile.toURI();
        }

        // maybe the schema was provided in URL/URI form
        if (schemaUri == null ) {
            try {
                schemaUri = new URI(args[0]);
            } catch (URISyntaxException e) {
                System.err.println("\nURI is not in a valid syntax! " + args[0] + "\n");
                System.exit(1);
            }
        }

        // a few checks on the URI 
        if (schemaUri.isOpaque()) {
            System.err.println("\nOpaque URIs are not supported! " + args[0] + "\n");
            System.exit(1);
        }
        if (!schemaUri.getPath().endsWith(".xsd")) {
            System.err.println("\nWARNING: The specified URI does not seem to point to " +
                    "a XML schema file (it should have the ending '.xsd')! Trying anyway...");
        }

        boolean inputIsFolder = false;

        // Check input file or folder.
        File inputLocation = new File(args[1]);
        if(!inputLocation.exists()){
            System.err.println("\nUnable to find the input you specified: '" + args[1] + "'!\n");
            System.exit(1);
        }
        if(inputLocation.isDirectory()) {
            inputIsFolder = true;
        } else if (inputLocation.isFile()) {
            inputIsFolder = false;
        } else {
            System.err.println("\nThe input you specified (" + args[1] + ") is not a folder nor a normal file!\n");
            System.exit(1);
        }


        try {
            // Set the schema.
            validator.setSchema(schemaUri);

            // accumulate the file(s) we are supposed to validate
            File[] inputFiles;
            if (inputIsFolder) {
                // the specified input is a directory, so we have to validate all XML files in that directory
                System.out.println("\nRetrieving files from '" + inputLocation.getAbsolutePath() + "'...");
                inputFiles = inputLocation.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        boolean result = false;
                        if(name.toLowerCase().endsWith("mzml") || name.toLowerCase().endsWith("xml")) {
                            result = true;
                        }
                        return result;
                    }
                });
            } else {
                // the specified input is a file, so we only have to validate this file
                inputFiles = new File[1];
                inputFiles[0] = inputLocation;
            }

            System.out.println("Validating " + inputFiles.length + " input file(s)...");
            for (File inputFile : inputFiles) {
                BufferedReader br = null;
                try{
                    // set the suggested buffer size for the BufferedReader
                    validator.setReadBufferSize(suggestBufferSize(inputFile));
                    System.out.println("\n\n  - Validating file '" + inputFile.getAbsolutePath() + "'...");
                    System.out.println("     (using a buffer size (in bytes): " + validator.getReadBufferSize());

                    if (validator.getReadBufferSize() > 0) {
                        // use user defined buffer size
                        br = new BufferedReader(new FileReader(inputFile), validator.getReadBufferSize());
                    } else {
                        // use system default size for the buffer
                        br = new BufferedReader(new FileReader(inputFile));
                    }
                    ErrorHandlerIface xveh = validator.validate(br);
                    if (xveh.noErrors()) {
                        System.out.println("    File is valid!");
                    } else {
                        System.out.println("    * Errors detected: ");
                        for (Object vMsg : xveh.getErrorMessages()) {
                            System.out.println( vMsg.toString() );
                        }
                    }
                } finally {
                    try {
                        if(br != null) {
                            br. close();
                        }
                    } catch(IOException ioe) {
                        // Do nothing.
                    }
                }
            }
            System.out.println("\nAll done!\n");
        } catch(Exception e) {
            e.printStackTrace();
        }
        long stopTime = System.currentTimeMillis();

        System.out.println( "Time for validation (in ms): " + (stopTime - startTime) );
    }

    private static void printUsage() {
        StringBuilder out = new StringBuilder();
        out.append("\n\nUsage: java -jar xmlSchemaValidator-1.0.jar <schema_file_uri> <input>\n");
        out.append("    where <schema_file_uri> is the location of the XML schema, either directly or as URI\n");
        out.append("    and the <input> is either the XML file to validate or a\n");
        out.append("    directory containing multiple XML files to validate.\n");
        System.out.println(out.toString());
    }



    private long validateIsoRelax(Reader xmlFileReader, URL schemaUrl) throws VerifierConfigurationException, IOException, SAXException {
        long startTime = System.currentTimeMillis();

        if (schemaUrl == null || xmlFileReader == null) {
            throw new IllegalArgumentException("Schema or XML file is null!");
        }

        File schemaFile;
        try {
            schemaFile = new File(schemaUrl.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Provided schema URL is no valid URI!" + schemaUrl);
        }

        if (!schemaFile.exists() || !schemaFile.canRead()) {
            throw new IllegalArgumentException("Invalid schema file provided. Could not find: " + schemaFile.getAbsolutePath());
        }


        // actual validation work
        org.iso_relax.verifier.Schema clSchema = PrideXmlClValidator.VERIFIER_FACTORY.compileSchema(schemaFile);
        PrideXmlClValidator validator = new PrideXmlClValidator();
        validator.validate(xmlFileReader, clSchema);

        long stopTime = System.currentTimeMillis();

        return (stopTime - startTime);
    }

}
