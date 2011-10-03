/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util.query;

import java.util.ArrayList;
import java.util.List;

/*import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;*/

/**
 *
 * @author Andrew
 */
public class AnnotationFormat {
    
    private boolean hasRef;
    private boolean hasAlt;
    private String program;
    private String version;
    private int referenceId;
    private String path;
    private List<AnnotationField> fields = new ArrayList<AnnotationField>();
    
    public AnnotationFormat(String program, String version, int referenceId, String path, boolean hasRef, boolean hasAlt, List<AnnotationField> fields){
        this.program = program;
        this.version = version;
        this.referenceId = referenceId;
        this.path = path;
        this.hasRef = hasRef;
        this.hasAlt = hasAlt;
        this.fields = fields;
    }
    
    public String generateSchema(){
        String result = "";
        
        //add custom columns
        for(int i = 0; i < fields.size(); i++){
            AnnotationField field = fields.get(i);
            String columnName = field.getColumnName();
            String columnType = field.getColumnType();
            result += "`" + columnName + "` " + columnType + " DEFAULT NULL,";
        }

        return result;
    }
    
    public int getNumNonDefaultFields(){
        return fields.size();
    }
    
    public boolean hasRef(){
        return hasRef;
    }
    
    public boolean hasAlt(){
        return hasAlt;
    }
    
    
    
    /*private Document doc;
    private boolean hasRef = false;
    private boolean hasAlt = false;
    private String version;
    private String program;
    private String prefix;
    
    private List<String> columnNames = new ArrayList<String>();
    private List<String> columnTypes = new ArrayList<String>();
    
    public static String[] DEFAULT_COLUMNS = new String[]{"genome_id", "chrom", "position", "ref", "alt"};
    public static String[] DEFAULT_TYPES = new String[]{"INT(11)", "VARCHAR(5)", "INT(11)", "VARCHAR(30)", "VARCHAR(30)"};
    
    public AnnotationFormat(String path) throws SAXException, ParserConfigurationException, IOException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(new File(path));
        doc.getDocumentElement().normalize();
        
        hasRef = doc.getDocumentElement().getAttribute("hasref").equals("true");
        hasAlt = doc.getDocumentElement().getAttribute("hasalt").equals("true");
        version = doc.getDocumentElement().getAttribute("version"); 
        program = doc.getDocumentElement().getAttribute("program"); 
        
        prefix = program + "_" + version.replaceAll("\\.", "_") + "_"; 
        
        //copy defaults
        for(int i = 0; i < 5; i++){
            if((i==3 && !hasRef) || (i==4 && !hasAlt)) continue;
            columnNames.add(DEFAULT_COLUMNS[i]);
            columnTypes.add(DEFAULT_TYPES[i]); 
        }     
        
        //get custom columns
        NodeList fields = doc.getElementsByTagName("field");
        for(int i = 0; i < fields.getLength(); i++){
            Element field = (Element)(fields.item(i));
            columnNames.add(prefix + field.getAttribute("name"));
            columnTypes.add(field.getAttribute("type")); 
        }
    }
    
    public List<String> getFieldNames(){
        return columnNames;
    }
    
    public int getNumFields(){
        return columnNames.size();
    }
    
    public int getNumNonDefaultFields(){
        return columnNames.size() - (DEFAULT_COLUMNS.length - (hasRef ? 0 : 1) - (hasAlt ? 0 : 1));
    }
    
    public String getFieldType(String fieldName){
        int pos = columnNames.indexOf(fieldName);
        if(pos == -1) return null;
        return columnTypes.get(pos);
    }
    
    public String getVersion(){
        return version;
    }
    
    public String generateSchema(){
        
        String result = "";
        
        //add custom columns
        for(int i = DEFAULT_COLUMNS.length; i < columnNames.size(); i++){
            String columnName = columnNames.get(i);
            String columnType = columnTypes.get(i);
            result += "`" + columnName + "` " + columnType + " DEFAULT NULL,";
        }

        return result;
    }

    public boolean hasAlt() {
        return hasAlt;
    }

    public boolean hasRef() {
        return hasRef;
    }    */

}
