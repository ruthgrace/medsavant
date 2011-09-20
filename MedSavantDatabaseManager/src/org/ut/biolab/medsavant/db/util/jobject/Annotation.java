package org.ut.biolab.medsavant.db.util.jobject;

/**
 *
 * @author mfiume
 */
public class Annotation {
        
        final private int id;
        final private String program;
        final private String version;
        final private String reference;
        final private String path;
        final private String format;

    public Annotation(int id, String program, String version, String reference, String path, String format) {
        this.id = id;
        this.program = program;
        this.version = version;
        this.reference = reference;
        this.path = path;
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getProgram() {
        return program;
    }

    public String getReference() {
        return reference;
    }

    public String getVersion() {
        return version;
    }
}
