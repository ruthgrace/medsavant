package org.ut.biolab.medsavant.db.util.query;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.broad.tabix.TabixReader;
import org.xml.sax.SAXException;

/**
 *
 * @author mfiume
 */
public class Annotation {
        
        final private int id;
        final private String program;
        final private String version;
        final private String reference;
        final private String dataPath;
        final private String formatPath;
    private TabixReader reader;

    public Annotation(int id, String program, String version, String reference, String dataPath, String formatPath) {
        this.id = id;
        this.program = program;
        this.version = version;
        this.reference = reference;
        this.dataPath = dataPath;
        this.formatPath = formatPath;
    }

    public String getFormatPath() {
        return formatPath;
    }
    
    public AnnotationFormat getAnnotationFormat() throws IOException, SAXException, ParserConfigurationException {
        return new AnnotationFormat(this.formatPath);
    }

    public int getId() {
        return id;
    }

    public String getDataPath() {
        return dataPath;
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
    
    public TabixReader getReader() throws IOException {
        if (reader == null) {
            reader = new TabixReader(this.getDataPath());
        }
        return reader;
    }
}
