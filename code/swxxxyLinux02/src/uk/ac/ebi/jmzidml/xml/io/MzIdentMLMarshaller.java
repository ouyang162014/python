package uk.ac.ebi.jmzidml.xml.io;


import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import org.apache.log4j.Logger;

import uk.ac.ebi.jmzidml.model.MzIdentMLObject;
import uk.ac.ebi.jmzidml.model.mzidml.MzIdentML;
import uk.ac.ebi.jmzidml.model.utils.ModelConstants;
import uk.ac.ebi.jmzidml.xml.Constants;
import uk.ac.ebi.jmzidml.xml.jaxb.marshaller.MarshallerFactory;
import uk.ac.ebi.jmzidml.xml.util.EscapingXMLStreamWriter;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Florian Reisinger
 *         Date: 20-Oct-2010
 * @since 0.2
 */
public class MzIdentMLMarshaller {

    private static final Logger logger = Logger.getLogger(MzIdentMLMarshaller.class);

//    jaxb.fragment - value must be a java.lang.Boolean
//        This property determines whether or not document level events will be generated by the Marshaller.
//        If the property is not specified, the default is false. This property has different implications
//        depending on which marshal api you are using - when this property is set to true:
//
//            * marshal(Object,ContentHandler) - the Marshaller won't invoke ContentHandler.startDocument() and ContentHandler.endDocument().
//            * marshal(Object,Node) - the property has no effect on this API.
//            * marshal(Object,OutputStream) - the Marshaller won't generate an xml declaration.
//            * marshal(Object,Writer) - the Marshaller won't generate an xml declaration.
//            * marshal(Object,Result) - depends on the kind of Result object, see semantics for Node, ContentHandler, and Stream APIs
//            * marshal(Object,XMLEventWriter) - the Marshaller will not generate XMLStreamConstants.START_DOCUMENT and XMLStreamConstants.END_DOCUMENT events.
//            * marshal(Object,XMLStreamWriter) - the Marshaller will not generate XMLStreamConstants.START_DOCUMENT and XMLStreamConstants.END_DOCUMENT events.
//

    @Deprecated
    public <T extends MzIdentMLObject> String marshall(T object) {
        return this.marshal(object);
    }
    public <T extends MzIdentMLObject> String marshal(T object) {
        StringWriter sw = new StringWriter();
        this.marshal(object, sw);
        return sw.toString();
    }

    @Deprecated
    public <T extends MzIdentMLObject> void marshall(T object, OutputStream os) {
        this.marshal(object, os);
    }
    public <T extends MzIdentMLObject> void marshal(T object, OutputStream os) {
        this.marshal(object, os, "UTF-8");
    }
    public <T extends MzIdentMLObject> void marshal(T object, OutputStream os, String encoding) {
        try {
            this.marshal(object, new OutputStreamWriter(os, encoding), encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not set character encoding!");
        }
    }

    @Deprecated
    public <T extends MzIdentMLObject> void marshall(T object, Writer out) {
        this.marshal(object, out);
    }

    /**
     * Method to write an MzIdentML object using a writer.
     * Note: this assumes UTF-8 character encoding to be used for the writer.
     *       If the writer set to another encoding, it may result in an exception
     *       complaining about incompatible encodings.
     * To use other encodings @see this#marshal(uk.ac.ebi.jmzidml.model.MzIdentMLObject, java.io.Writer, String)
     *
     * @param object the MzIdentML object to marshal.
     * @param out the writer to marshal the object to.
     */
    @SuppressWarnings("unchecked")
    public <T extends MzIdentMLObject> void marshal(T object, Writer out) {
        this.marshal(object, out, "UTF-8");
    }

    public <T extends MzIdentMLObject> void marshal(T object, Writer out, String encoding) {
        if (object == null) {
            throw new IllegalArgumentException("Cannot marshall a NULL object");
        }

        try {
            Marshaller marshaller = MarshallerFactory.getInstance().initializeMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // Set JAXB_FRAGMENT_PROPERTY to true for all objects that do not have
            // a @XmlRootElement annotation
            if (!(object instanceof MzIdentML)) {
                marshaller.setProperty(Constants.JAXB_FRAGMENT_PROPERTY, true);
                if (logger.isDebugEnabled()) logger.debug("Object '" + object.getClass().getName() +
                                                          "' will be treated as root element.");
            } else {
                if (logger.isDebugEnabled()) logger.debug("Object '" + object.getClass().getName() +
                                                          "' will be treated as fragment.");
            }

            QName aQName = ModelConstants.getQNameForClass(object.getClass());
            JAXBElement jaxbElement = new JAXBElement(aQName, object.getClass(), object);

            // before marshalling out, wrap in a Custom XMLStreamWriter
            // to fix a JAXB bug: http://java.net/jira/browse/JAXB-614
            // also wrapping in IndentingXMLStreamWriter to generate formatted XML
            System.setProperty("javax.xml.stream.XMLOutputFactory", "com.sun.xml.internal.stream.XMLOutputFactoryImpl");

//            XMLOutputFactory factory = XMLOutputFactory.newFactory("com.sun.xml.internal.stream.XMLOutputFactoryImpl", null);
            XMLOutputFactory factory = XMLOutputFactory.newFactory();
            XMLStreamWriter xmlStreamWriter = factory.createXMLStreamWriter(out);

            //Working implementation
            //XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(out);

            //XMLStreamWriter xmlStreamWriter = XMLOutputFactory2.newFactory().createXMLStreamWriter(out);



            // Note: the EscapingXMLStreamWriter should default to "UTF-8" as character encoding, but
            //       does not on all platforms. Therefore the encoding is hard coded for the default case
            //       see EscapingXMLStreamWriter.writeStartDocument()
            IndentingXMLStreamWriter writer = new IndentingXMLStreamWriter(new EscapingXMLStreamWriter(xmlStreamWriter, encoding));

            marshaller.marshal( jaxbElement, writer );

        } catch (JAXBException e) {
            logger.error("MzMLMarshaller.marshall", e);
            throw new IllegalStateException("Error while marshalling object:" + object.toString());
        } catch (XMLStreamException e) {
            logger.error("MzMLMarshaller.marshall", e);
            throw new IllegalStateException("Error while marshalling object:" + object.toString());
        }

    }

    ///// ///// ///// ///// ///// ///// ///// ///// ///// /////
    // helper methods to 'stitch' together large XML elements
    // ToDo: perhaps add support for other elements as well (SpectrumIdentificationResult, ProteinAmbiguityGroup)?

    public String createXmlHeader() {
        String encoding = System.getProperty("file.encoding");
        return "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>";
    }

    public String createMzIdentMLStartTag(String id) {
        StringBuffer sb = new StringBuffer();

        // tag opening plus id attribute
        sb.append("<MzIdentML id=\"").append(id).append("\"");
        // further attributes
        sb.append(" version=\"").append(ModelConstants.MZIDML_VERSION).append("\"");
        sb.append(" xmlns=\"").append(ModelConstants.MZIDML_NS).append("\"");
        sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        sb.append(" xsi:schemaLocation=\"").append(ModelConstants.MZIDML_NS).append(" ").append(ModelConstants.MZIDML_SCHEMA).append("\"");
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sb.append(" creationDate=\"").append( dfm.format(new Date()) ).append("\"");
        // finally close the tag
        sb.append(" >");

        return sb.toString();
    }

    public String createMzIdentMLClosingTag() {
        return "</MzIdentML>";
    }

    public String createDataCollectionStartTag() {
        return "<DataCollection>";
    }

    public String createDataCollectionClosingTag() {
        return "</DataCollection>";
    }

    public String createAnalysisDataStartTag() {
        return "<AnalysisData>";
    }

    public String createAnalysisDataClosingTag() {
        return "</AnalysisData>";
    }

    public String createProteinDetectionListStartTag(String id, String name) {
        if (id == null) {
            throw new IllegalArgumentException("The 'id' attribute must not be null!");
        }
        // allowed attributes: 'id', 'name'

        StringBuffer sb = new StringBuffer();

        // tag opening plus id attribute
        sb.append("<ProteinDetectionList id=\"").append(id).append("\"");
        // the 'name' attribute, if provided
        if (name != null) {
            sb.append(" name=\"").append(name).append("\"");
        }
        // finally close the tag
        sb.append(" >");

        return sb.toString();
    }

    public String createProteinDetectionListClosingTag() {
        return "</ProteinDetectionList>";
    }

    public String createSpectrumIdentificationListStartTag(String id, String name, Long numSeqSearched) {
        if (id == null) {
            throw new IllegalArgumentException("The 'id' attribute must not be null!");
        }
        // allowed attributes: 'id', 'name', 'numSequencesSearched'

        StringBuffer sb = new StringBuffer();

        // tag opening plus id attribute
        sb.append("<SpectrumIdentificationList id=\"").append(id).append("\"");
        // the attributes, if provided
        if (name != null) {
            sb.append(" name=\"").append(name).append("\"");
        }
        if (numSeqSearched != null) {
            sb.append(" numSequencesSearched=\"").append(numSeqSearched).append("\"");
        }
        // finally close the tag
        sb.append(" >");

        return sb.toString();
    }

    public String createSpectrumIdentificationListClosingTag() {
        return "</SpectrumIdentificationList>";
    }

}
