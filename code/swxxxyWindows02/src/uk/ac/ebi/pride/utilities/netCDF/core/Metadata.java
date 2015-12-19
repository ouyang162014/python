package uk.ac.ebi.pride.utilities.netCDF.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 28/09/15
 */
public class Metadata {


    Map<String, String> attributes = new HashMap<String, String>();

    public Metadata(){}

    public Metadata(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "attributes=" + attributes +
                '}';
    }
}
