/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.format;

import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.format.AnnotationField.Category;

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
        fields.add(new AnnotationField("dna_id", "VARCHAR(10)", true, "DNA ID", "", Category.PATIENT));
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
    
}
