package uk.ac.ebi.pride.tools.validator;

import javax.xml.validation.TypeInfoProvider;
import org.w3c.dom.TypeInfo;

/**
 * A ContentHandler implementation which shows how <code>TypeInfoProvider</code>
 * can be used to retrieve type information of elements/attributes.
 *
 * @author  Neeraj Bajaj, Sun Microsystems.
 */
public class ContentHandlerImpl implements org.xml.sax.ContentHandler
{
    TypeInfoProvider tip ;
    
    public ContentHandlerImpl(){
        
    }
    /** Creates a new instance of ContentHandlerImpl */
    public ContentHandlerImpl(TypeInfoProvider tip) {
        this.tip = tip;
    }

    public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
        System.out.println("characters: " + new String(ch,start,length));
    }

    public void endDocument() throws org.xml.sax.SAXException {
    }

    public void endElement(String uri, String localName, String qName) throws org.xml.sax.SAXException {
        System.out.println("endElement: " + qName);
    }

    public void endPrefixMapping(String prefix) throws org.xml.sax.SAXException {
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws org.xml.sax.SAXException {
    }

    public void processingInstruction(String target, String data) throws org.xml.sax.SAXException {
    }

    public void setDocumentLocator(org.xml.sax.Locator locator) {
    }

    public void skippedEntity(String name) throws org.xml.sax.SAXException {
    }

    public void startDocument() throws org.xml.sax.SAXException {
    }

    public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
        System.out.println("startElement: " + qName);
        if(tip != null){
            TypeInfo ti = tip.getElementTypeInfo();
            System.out.println("type name = " + ti.getTypeName());
            System.out.println("type namespace = " + ti.getTypeNamespace());        
            System.out.println("");
        }
    }

    public void startPrefixMapping(String prefix, String uri) throws org.xml.sax.SAXException {
    }
    
}
