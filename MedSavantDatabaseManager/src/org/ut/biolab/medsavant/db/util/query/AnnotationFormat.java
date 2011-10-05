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
    
    public static enum AnnotationType {POSITION, INTERVAL};
    
    public static AnnotationType intToAnnotationType(int type){
        switch(type){
            case 0:
                return AnnotationType.POSITION;
            case 1:
                return AnnotationType.INTERVAL;
            default:
                return null;
        }
    }
    
    public static int annotationTypeToInt(AnnotationType type){
        switch(type){
            case POSITION:
                return 0;
            case INTERVAL:
                return 1;
            default:
                return -1;
        }
    }
    
    public static int annotationTypeToInt(String type){
        return annotationTypeToInt(AnnotationType.valueOf(type.toUpperCase()));
    }
    
    private boolean hasRef;
    private boolean hasAlt;
    private String program;
    private String version;
    private int referenceId;
    private String path;
    private AnnotationType type;
    private List<AnnotationField> fields = new ArrayList<AnnotationField>();
    
    public AnnotationFormat(String program, String version, int referenceId, String path, boolean hasRef, boolean hasAlt, AnnotationType type, List<AnnotationField> fields){
        this.program = program;
        this.version = version;
        this.referenceId = referenceId;
        this.path = path;
        this.hasRef = hasRef;
        this.hasAlt = hasAlt;
        this.fields = fields;
        this.type = type;
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
    
    public List<AnnotationField> getAnnotationFields(){
        return fields;
    }
    
    public static AnnotationFormat getDefaultAnnotationFormat(){
        List<AnnotationField> fields = new ArrayList<AnnotationField>();
        fields.add(new AnnotationField("upload_id", "INT(11)", false, "Upload ID", ""));
        fields.add(new AnnotationField("file_id", "INT(11)", false, "File ID", ""));
        fields.add(new AnnotationField("variant_id", "INT(11)", false, "Variant ID", ""));
        fields.add(new AnnotationField("dna_id", "VARCHAR(10)", true, "DNA ID", ""));
        fields.add(new AnnotationField("chrom", "VARCHAR(5)", true, "Chromosome", ""));
        fields.add(new AnnotationField("position", "INT(11)", true, "Position", ""));
        fields.add(new AnnotationField("dbsnp_id", "(VARCHAR(45)", false, "dbSNP ID", ""));
        fields.add(new AnnotationField("ref", "VARCHAR(30)", true, "Reference", ""));
        fields.add(new AnnotationField("alt", "VARCHAR(30)", true, "Alternate", ""));
        fields.add(new AnnotationField("qual", "FLOAT(10,0)", true, "Quality", ""));
        fields.add(new AnnotationField("filter", "VARCHAR(500)", false, "Filter", ""));
        fields.add(new AnnotationField("aa", "VARCHAR(500)", false, "AA", ""));
        fields.add(new AnnotationField("ac", "VARCHAR(500)", false, "AC", ""));
        fields.add(new AnnotationField("af", "VARCHAR(500)", false, "AF", ""));
        fields.add(new AnnotationField("an", "INT(11)", false, "AN", ""));
        fields.add(new AnnotationField("bq", "FLOAT", false, "BQ", ""));
        fields.add(new AnnotationField("cigar", "VARCHAR(500)", false, "Cigar", ""));
        fields.add(new AnnotationField("db", "INT(1)", false, "DB", ""));
        fields.add(new AnnotationField("dp", "INT(11)", false, "DP", ""));
        fields.add(new AnnotationField("end", "VARCHAR(500)", false, "End", ""));
        fields.add(new AnnotationField("h2", "INT(1)", false, "H2", ""));
        fields.add(new AnnotationField("mq", "VARCHAR(500)", false, "MQ", ""));
        fields.add(new AnnotationField("mq0", "VARCHAR(500)", false, "MQ0", ""));
        fields.add(new AnnotationField("ns", "INT(11)", false, "NS", ""));
        fields.add(new AnnotationField("sb", "VARCHAR(500)", false, "SB", ""));
        fields.add(new AnnotationField("somatic", "INT(1)", false, "Somatic", ""));
        fields.add(new AnnotationField("validated", "INT(1)", false, "Validated", ""));
        fields.add(new AnnotationField("custom_info", "VARCHAR(500)", false, "Custom Info", ""));
        
        return new AnnotationFormat("default", "default", 0, "", true, true, AnnotationType.POSITION, fields);
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
