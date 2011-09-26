package org.ut.biolab.medsavant.server.annotation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mfiume
 */
public class AnnotationFormat {

    public static class AnnotationField {

        private final String name;
        private final String type;

        public AnnotationField(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
        
        
    }
    
    private boolean hasRef;
    private boolean hasAlt;
    private List<AnnotationField> fields;

    public AnnotationFormat() {
        fields = new ArrayList<AnnotationField>();
    }
    
    public void addField(AnnotationField af) {
        fields.add(af);
    }

    public List<AnnotationField> getFields() {
        return fields;
    }

    public boolean isHasAlt() {
        return hasAlt;
    }

    public void setHasAlt(boolean hasAlt) {
        this.hasAlt = hasAlt;
    }

    public boolean isHasRef() {
        return hasRef;
    }

    public void setHasRef(boolean hasRef) {
        this.hasRef = hasRef;
    }
    
    
    
}
