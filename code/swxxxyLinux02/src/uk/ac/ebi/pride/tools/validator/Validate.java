package uk.ac.ebi.pride.tools.validator;

import java.io.File;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * This sample shows how new Validator APIs can be used to compile a standalone schema. This
 * feature is useful for those applications which are developing schema and wants to check
 * the validity of it as per the rules of schema language. 
 *            Once an application has <code>Schema</code> object, it can be used to create
 * <code>Validator</code> which can be used to validate an instance document against the
 * schema or set of schemas this <code>Schema</code> object represents.
 *
 * @author  Neeraj Bajaj, Sun Microsystems.
 */
public class Validate {
         
    private static final boolean DEBUG = System.getProperty("debug") != null ? true : false;
    
    /** Parser the given schema and return in-memory representation of that
     *  schema. Compiling the schema is very simple, just pass the path of schema 
     *  to <code>newSchema()</code> function and it will parse schema, check the 
     *  validity of schema document as per the schema language, compute in-memory 
     *  representation and return it as <code>Schema</code> object. Note that If 
     *  schema imports/includes other schemas, those schemas will be parsed too.    
     *
     * @param String path to schema file
     * @return Schema in-memory representation of schema.
     */
    public static Schema compileSchema(String schema) throws SAXException{
        //Get the SchemaFactory instance which understands W3C XML Schema language
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        if(DEBUG){
            System.out.println("schema factory instance obtained is " + sf);
        }
        //
        return sf.newSchema(new File(schema));
        
    }//compileSchema
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            if(args.length != 2){
                printUsage();
            }            
            //parse schema first, see compileSchema function to see how 
            //Schema object is obtained.
            Schema schema = compileSchema(args[0]);
            //this "Schema" object is used to create "Validator" which 
            //can be used to validate instance document against the schema
            //or set of schemas "Schema" object represents.
            Validator validator = schema.newValidator();
            //set ErrorHandle on this validator
            validator.setErrorHandler(new MyErrorHandler());
            //Validate this instance document against the instance document supplied
            validator.validate(new StreamSource(args[1]));
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println("GET CAUSE:");
            ex.getCause().fillInStackTrace();
        }                
    }
    
    static void printUsage(){
        System.out.println("java Validate <schema file> <XML document>");
    }
    
}
