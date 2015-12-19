package uk.ac.ebi.pride.tools.validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.iso_relax.verifier.VerifierConfigurationException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.ebi.pride.tools.cl.PrideXmlClValidator;
import uk.ac.ebi.pride.tools.isorelax.PrideXmlValidatorIsoRelax;

/**
 * JAXP 1.3 Validation APIs allow compilation of schemas (W3C XML Scheam/RelaxNG) into immutable 
 * memory representation called <code>Schema</code> which can then be used to validate instance 
 * document. There is no need to parse, validate the schema, build memory representation again 
 * and again.
 *
 *         Besides the many advantage offered by this approach, it is much faster compared to
 * JAXP 1.2 way (setting <code>schemaSource</code> & <code>schemaLanguage</code>)of validating
 * instance document against Schema.
 *
 * This program compares the performance of two approach. Note that the gain
 * in performance will depend on the type/size of schema.
 *
 * @author  Neeraj Bajaj, Sun Microsystems
 */
public class ComparePerformance {
    
    
    private static final String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";    
    ErrorHandler eh = new MyErrorHandler();
    DefaultHandler dh = new DefaultHandler();
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        long totalStart = System.currentTimeMillis();
        try{
            if(args.length != 3){
                usage();
            }
            ComparePerformance cp = new ComparePerformance();
            
            String schema, xml ;
            schema = args[0];
            xml = args[1];
            
            //print the passed information
            System.out.println("Schema File = " + args[0]);
            System.out.println("XML Document= " + args[1]);

            //JAXP 1.2 initialization code
            SAXParserFactory spFactory = SAXParserFactory.newInstance();
            spFactory.setNamespaceAware(true);
            spFactory.setValidating(true);            
            SAXParser sp = spFactory.newSAXParser();            
            
            //JAXP 1.3 initialization code
            
            //get compiled Schema (this step needs to be performed only once)
            //a) this schema can be set on factory
            //b) can be used to create validator
            long startSchemaCompile = System.currentTimeMillis();
            Schema cschema = Validate.compileSchema(schema);
            System.out.println("Time to compile schema: " + (System.currentTimeMillis() - startSchemaCompile));

            long startSaxParserInit = System.currentTimeMillis();
            SAXParserFactory spf = SAXParserFactory.newInstance();            
            //set the compiled schema on factory
            spf.setSchema(cschema);
            SAXParser parser = spf.newSAXParser();
            System.out.println("Time to initialise SAX parser: " + (System.currentTimeMillis() - startSaxParserInit));
            
            //Get a Validator which can be used to validate instance 
            //document against this grammar.
            Validator validator = cschema.newValidator();

//            //Warm up
//            long a = cp.usingJAXP1_2(sp, xml, schema);
//            System.out.println("Using JAXP 1.2 schemaLanguage/Source properties, Time taken [ms]: " + a);
//
//            //warum up
//            long b = cp.usingJAXP1_3setSchema(parser, xml);
//            System.out.println("Using JAXP 1.3 <Factory>.setSchema() approach, Time taken [ms]: " + b);
//
//            //warum up
//            long c = cp.usingJAXP1_3Validator(validator, xml);
//            System.out.println("Using JAXP 1.3 Validator.validate() approach, Time taken [ms]: " + c);
//
//            long d = useIsoRelaxValidator(schema, xml);
//            System.out.println("Using isoRelax Validator approach, Time taken for 1 iteration is " + d + " ms ");

            long e = useClValidator(schema, xml);
            System.out.println("Using cl Validator approach, Time taken for 1 iteration is " + e + " ms ");

        }catch(Exception ex){
            ex.printStackTrace();
        }

        System.out.println("Total time [ms]: " + (System.currentTimeMillis() - totalStart));

    }


    long usingJAXP1_3Validator(Validator validator, String xml)throws SAXException, IOException{
        long start = System.currentTimeMillis();
        //reset the validator() to re-use the same instance
        validator.reset();
        //Remember to set the Error Handler again as reset() restores
        //validator to factory settings
        validator.setErrorHandler(eh);
        //Validate this instance document against the Instance document supplied
        validator.validate(new StreamSource(xml));
        return (System.currentTimeMillis() - start);
    }

    long usingJAXP1_3setSchema(SAXParser sp, String xml) throws SAXException, IOException{
        long start = System.currentTimeMillis();
        //reset the parser instance to re-use the same instance
        sp.reset();
        sp.parse(new File(xml), dh);
        return System.currentTimeMillis() - start;
    }

    long usingJAXP1_2(SAXParser sp, String xml, String schema) throws SAXException, IOException{
        long start = System.currentTimeMillis();
        //reset the parser instance to re-use the same instance
        sp.reset();
        sp.setProperty(SCHEMA_LANGUAGE, XMLConstants.W3C_XML_SCHEMA_NS_URI);
        sp.setProperty(SCHEMA_SOURCE, schema);
        sp.parse(new File(xml), dh);
        return System.currentTimeMillis() - start;
    }

    static void usage(){
        System.out.println("java -cp . ValidationFramework <SchemaFile> <XML Document>");
    }

    private static long useIsoRelaxValidator(String schemaFileName, String xmlFileName) throws VerifierConfigurationException, IOException, SAXException {
        long startTime = System.currentTimeMillis();

        // process schema file
        File schemaFile = new File(schemaFileName);
        if (!schemaFile.exists() || !schemaFile.canRead()) {
            throw new IllegalArgumentException("Invalid schema file provided. Could not find: " + schemaFileName);
        }

        // process input xml file
        File xmlFile = new File(xmlFileName);
        if (!xmlFile.exists() || !xmlFile.canRead()) {
            throw new IllegalArgumentException("Invalid schema file provided. Could not find: " + xmlFile);
        }

        BufferedReader reader = new BufferedReader(new FileReader(xmlFile));
        PrideXmlValidatorIsoRelax.validate(reader, schemaFile);

        long stopTime = System.currentTimeMillis();

        return (stopTime - startTime);
    }

    private static long useClValidator(String schemaFileName, String xmlFileName) throws VerifierConfigurationException, IOException, SAXException {
        long startTime = System.currentTimeMillis();

        // process schema file
        File schemaFile = new File(schemaFileName);
        if (!schemaFile.exists() || !schemaFile.canRead()) {
            throw new IllegalArgumentException("Invalid schema file provided. Could not find: " + schemaFileName);
        }

        // process input xml file
        File xmlFile = new File(xmlFileName);
        if (!xmlFile.exists() || !xmlFile.canRead()) {
            throw new IllegalArgumentException("Invalid schema file provided. Could not find: " + xmlFile);
        }
        org.iso_relax.verifier.Schema clSchema = PrideXmlClValidator.VERIFIER_FACTORY.compileSchema(schemaFile);

        BufferedReader reader = new BufferedReader(new FileReader(xmlFile));
        PrideXmlClValidator validator = new PrideXmlClValidator();
        validator.validate(reader, clSchema);

        long stopTime = System.currentTimeMillis();

        return (stopTime - startTime);
    }

}
