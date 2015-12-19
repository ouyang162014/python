package uk.ac.ebi.pride.tools.validator;

import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This samples shows how JAXP 1.3 Schema Validation Framework can be used 
 * to validate SAX stream.
 *
 * @author  Neeraj Bajaj, Sun Microsystems,inc.
 *
 */
public class ValidateSAXStream {
           
    private Schema compiledSchema;
    
    public ValidateSAXStream(String schema) throws SAXException{
        this.compiledSchema = Validate.compileSchema(schema);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length != 2){
            printUsage();
        }
        try{
            ValidateSAXStream vss = new ValidateSAXStream(args[0]);
            vss.validateSAXStream(vss.getSAXReader(), args[1]);        
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void validateSAXStream(XMLReader reader, String xml) throws SAXException, IOException{
        ValidatorHandler vh = getValidatorHandler();
        //key is to set "ValidatorHandler" as ContentHandler for the 
        //SAXDriver so that SAX event can be validated by this "ValidatorHandler"
        reader.setContentHandler(vh);
        reader.parse(xml);
    }
    
    public ValidatorHandler getValidatorHandler()throws SAXException, IOException{
        //Schema acts as a factory to create Validator or ValidatorHandler
        //create ValidatorHandler which is capable of validating SAX events
        ValidatorHandler vh = compiledSchema.newValidatorHandler();
        //set Error Hanlder for reporting errors
        vh.setErrorHandler(new MyErrorHandler());
        //pass the output of ValidatorHandler to registered ContentHandler
        vh.setContentHandler(new ContentHandlerImpl());        
        return vh;
    }

    public Validator getValidator(){
        Validator validator = compiledSchema.newValidator();
        validator.setErrorHandler(new MyErrorHandler());
        return validator;
    }
    
    protected XMLReader getSAXReader() throws ParserConfigurationException, SAXException{
        //create SAXReader                        
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        return spf.newSAXParser().getXMLReader();
    }
    
    static void printUsage(){
        System.out.println("java ValidateSAXStream <schema file> <XML document>");
    }
}
