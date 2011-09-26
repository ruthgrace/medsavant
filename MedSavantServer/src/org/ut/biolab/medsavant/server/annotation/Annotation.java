package org.ut.biolab.medsavant.server.annotation;

/**
 *
 * @author mfiume
 */
public class Annotation {
    
    private String name;
    private String version;
    private String path;
    private AnnotationFormat format;

    public Annotation(String name, String version, String path, AnnotationFormat format) {
        this.name = name;
        this.version = version;
        this.path = path;
        this.format = format;
    }
    
}
