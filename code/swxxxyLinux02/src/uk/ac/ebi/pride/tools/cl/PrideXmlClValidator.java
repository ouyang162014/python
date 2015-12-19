package uk.ac.ebi.pride.tools.cl;

import com.sun.msv.verifier.jarv.TheFactoryImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class PrideXmlClValidator {

    public static final VerifierFactory VERIFIER_FACTORY = new TheFactoryImpl();

    private static Schema SCHEMA = null;
    private static final String SCHEMA_NAME = "pride.xsd";

    public void setSchema(URL schemaUrl) throws IOException, VerifierConfigurationException, SAXException {
        SCHEMA = VERIFIER_FACTORY.compileSchema(schemaUrl.openStream());
    }

    public XMLValidationErrorHandler validate(Reader reader, Schema schema) throws IOException, VerifierConfigurationException, SAXException {

        XMLValidationErrorHandler xmlValidationErrorHandler = new XMLValidationErrorHandler();

        Verifier schemaVerifier = schema.newVerifier();
        schemaVerifier.setErrorHandler(xmlValidationErrorHandler);
        try {
            schemaVerifier.verify(new InputSource(reader));
        } catch (SAXParseException e) {
            xmlValidationErrorHandler.error(e);
        }
        return xmlValidationErrorHandler;
    }

    public XMLValidationErrorHandler validate(Reader reader) throws IOException, VerifierConfigurationException, SAXException {

        if (SCHEMA == null) {
            SCHEMA = VERIFIER_FACTORY.compileSchema(PrideXmlClValidator.class.getClassLoader().getResourceAsStream(SCHEMA_NAME));
        }
        return validate(reader, SCHEMA);
    }

    public static void main(String[] args) {
        if ((args == null) || (args.length != 2)) {
            printUsage();
            System.exit(1);
        }

        File schemaFile = new File(args[0]);
        if (!schemaFile.exists() || !schemaFile.canRead()) {
            System.out.println("Provided schema file is not valid! " + schemaFile.getAbsolutePath());
            System.exit(1);
        }
        URL schemaUrl = null;
        try {
            schemaUrl = schemaFile.toURI().toURL();
        } catch (MalformedURLException e) {
            System.out.println("Provided schema location not valid! " + schemaFile.getAbsolutePath());
            e.printStackTrace();
            System.exit(1);
        }
        if (schemaUrl == null) {
            System.out.println("Provided schema file is not valid! " + schemaFile.getAbsolutePath());
            System.exit(1);
        }

        File inputFolder = new File(args[1]);
        if (!inputFolder.exists()) {
            System.err.println("\nUnable to find the input folder you specified: '" + args[1] + "'!\n");
            System.exit(1);
        }
        if (!inputFolder.isDirectory()) {
            System.err.println("\nThe input folder you specified ('" + args[1] + "') was a file, not a folder!\n");
            System.exit(1);
        }

        PrideXmlClValidator validator = new PrideXmlClValidator();
        try {
            validator.setSchema(schemaUrl);
        } catch (Exception e) {
            System.out.println("Provided schema file is not a valid Schema! " + schemaFile.getAbsolutePath());
            e.printStackTrace();
            System.exit(1);
        }

        BufferedReader br = null;
        try {
            System.out.println("\nRetrieving files from '" + inputFolder.getAbsolutePath() + "'...");
            File[] inputFiles = inputFolder.listFiles();
            System.out.println("Found " + inputFiles.length + " input files.\n");
            System.out.println("Validating files...");

            for (File inputFile : inputFiles) {
                System.out.println("  - Validating file '" + inputFile.getAbsolutePath() + "'...");
                br = new BufferedReader(new FileReader(inputFile));
                XMLValidationErrorHandler xveh = validator.validate(br);
                if (xveh.noErrors()) {
                    System.out.println("    File is valid!");
                } else {
                    System.err.println("    * Errors detected: ");
                    System.err.println(xveh.getErrorsFormattedAsPlainText());
                }
                br.close();
            }
            System.out.println("\nAll done!\n");
        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private static void printUsage() {
        StringBuilder out = new StringBuilder();
        out.append("Usage: java ").append(PrideXmlClValidator.class.getName());
        out.append(" <schema file>");
        out.append(" <inputfolder> ");
        System.out.println(out.toString());
    }

}