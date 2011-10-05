package org.ut.biolab.medsavant.db.util.query;

import java.io.IOException;
import java.sql.SQLException;
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
    private TabixReader reader;

    public Annotation(int id, String program, String version, String reference, String dataPath) {
        this.id = id;
        this.program = program;
        this.version = version;
        this.reference = reference;
        this.dataPath = dataPath;
    }
    
    public AnnotationFormat getAnnotationFormat() throws SQLException, IOException, ParserConfigurationException, SAXException {
        return AnnotationQueryUtil.getAnnotationFormat(id); 
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
