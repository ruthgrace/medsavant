/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.samtools.util.AsciiLineReader;
import net.sf.samtools.util.BlockCompressedOutputStream;
import org.broad.tabix.TabixWriter;
import org.broad.tabix.TabixWriter.Conf;
import org.ut.biolab.medsavant.db.table.AnnotationTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;
import org.ut.biolab.medsavant.db.util.DBUtil;
import org.ut.biolab.medsavant.db.format.AnnotationField;
import org.ut.biolab.medsavant.db.format.AnnotationFormat;
import org.ut.biolab.medsavant.db.format.AnnotationFormat.AnnotationType;
import org.ut.biolab.medsavant.db.table.AnnotationFormatTable;
import org.ut.biolab.medsavant.db.table.AnnotationMapTable;
import org.ut.biolab.medsavant.db.util.query.ReferenceQueryUtil;
import org.ut.biolab.medsavant.server.log.ServerLogger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Andrew
 */
public class AddAnnotation {
    
    private static Document doc;
    private static boolean hasRef;
    private static boolean hasAlt;
    private static String version;
    private static String program;
    private static String prefix;
    private static String referenceName;
    private static int referenceId;
    private static int annotationType;
    
    private static String path;
    private static String tabixPath;
    
    private static List<AnnotationField> annotationFields = new ArrayList<AnnotationField>();

    
    public static void main(String[] args){
        
        if(args.length != 2){
            System.err.println("Usage: AddAnnotation.jar annotationFilePath formatFilePath");
            return;
        }
        
        path = args[0];
        tabixPath = path + ".tabix";
        try {
            ServerLogger.log(AddAnnotation.class, "parsing format");
            parseFormat(args[1]);
            ServerLogger.log(AddAnnotation.class, "formatting tabix");
            formatTabix(getFieldNames(), new File(path), new File(tabixPath));
            ServerLogger.log(AddAnnotation.class, "generating tables");
            generateTable();
            ServerLogger.log(AddAnnotation.class, "done adding annotation");
        } catch (Exception e){
            ServerLogger.log(AddAnnotation.class, "error adding annotation");
            e.printStackTrace();
        }       
    }
    
    private static String[] getFieldNames(){
        String[] result = new String[
                1 + //chrom
                (AnnotationFormat.intToAnnotationType(annotationType) == AnnotationType.INTERVAL ? 2 : 1) + // position or start/end
                (hasRef ? 1 : 0) + //ref
                (hasAlt ? 1 : 0) + //alt
                annotationFields.size()]; //numfields
        int pos = 0;
        result[pos++] = "chrom";
        if(AnnotationFormat.intToAnnotationType(annotationType) == AnnotationType.POSITION){
            result[pos++] = "position";
        } else {
            result[pos++] = "start";
            result[pos++] = "end";
        }
        if(hasRef) result[pos++] = "ref";
        if(hasAlt) result[pos++] = "alt";
        for(int i = 0; i < annotationFields.size(); i++){
            result[pos++] = annotationFields.get(i).getColumnName();
        }
        return result;
    }
    
    private static void generateTable() throws SQLException{
        
        //get reference id
        referenceId = ReferenceQueryUtil.getReferenceId(referenceName); 
        
        //insert into annotations table and get annotation_id
        String query = 
                "INSERT INTO " + AnnotationTable.TABLENAME + " ("
                + AnnotationTable.FIELDNAME_PROGRAM + ", "
                + AnnotationTable.FIELDNAME_VERSION + ", "
                + AnnotationTable.FIELDNAME_REFERENCEID + ", "
                + AnnotationTable.FIELDNAME_PATH + ", "
                + AnnotationTable.FIELDNAME_HASREF + ", "
                + AnnotationTable.FIELDNAME_HASALT + ", " 
                + AnnotationTable.FIELDNAME_TYPE
                + ") VALUES (" 
                + "\"" + program 
                + "\",\"" + version 
                + "\"," + referenceId 
                + ",\"" + (new File(tabixPath)).getAbsolutePath().replaceAll("\\\\", "/")
                + "\"," + (hasRef ? "1" : "0")
                + "," + (hasAlt ? "1" : "0") 
                + "," + annotationType + ");";
        PreparedStatement stmt = (ConnectionController.connect()).prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.execute();
        
        ResultSet rs = stmt.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        
        //create format table
        String tableName = DBSettings.createAnnotationFormatTableName(id);
        query = 
                "CREATE TABLE " + tableName + " (" +
                "`" + AnnotationFormatTable.FIELDNAME_POSITION + "` INT(11) unsigned NOT NULL AUTO_INCREMENT," + 
                "`" + AnnotationFormatTable.FIELDNAME_COLUMNNAME + "` VARCHAR(200) NOT NULL," +
                "`" + AnnotationFormatTable.FIELDNAME_COLUMNTYPE + "` VARCHAR(45) NOT NULL," + 
                "`" + AnnotationFormatTable.FIELDNAME_FILTERABLE + "` BOOLEAN NOT NULL," +
                "`" + AnnotationFormatTable.FIELDNAME_ALIAS + "` VARCHAR(200) NOT NULL," +
                "`" + AnnotationFormatTable.FIELDNAME_DESCRIPTION + "` VARCHAR(500) NOT NULL," +
                "PRIMARY KEY (`" + AnnotationFormatTable.FIELDNAME_POSITION + "`)" +
                ") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;";
        ConnectionController.connect().createStatement().execute(query);
        
        //populate
        Connection conn = ConnectionController.connect();
        conn.setAutoCommit(false);
        for(int i = 0; i < annotationFields.size(); i++){
            AnnotationField a = annotationFields.get(i);
            conn.createStatement().executeUpdate(
                    "INSERT INTO " + tableName + " ("
                    + AnnotationFormatTable.FIELDNAME_COLUMNNAME + ", " 
                    + AnnotationFormatTable.FIELDNAME_COLUMNTYPE + ", " 
                    + AnnotationFormatTable.FIELDNAME_FILTERABLE + ", " 
                    + AnnotationFormatTable.FIELDNAME_ALIAS + ", " 
                    + AnnotationFormatTable.FIELDNAME_DESCRIPTION 
                    + ") VALUES (" 
                    + "\"" + a.getColumnName() 
                    + "\",\"" + a.getColumnType() 
                    + "\"," + (a.isFilterable() ? "1" : "0") 
                    + ",\"" + a.getAlias() 
                    + "\",\"" + a.getDescription() + "\");");
        }
        conn.commit();
        conn.setAutoCommit(true);
        
        //add entry to annotation_tablemap
        conn.createStatement().executeUpdate(
                "INSERT INTO " + AnnotationMapTable.TABLENAME
                + " VALUES (" + id + "," + tableName + ")");
        
    }
    
    private static void parseFormat(String path) throws SAXException, ParserConfigurationException, IOException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(new File(path));
        doc.getDocumentElement().normalize();
        
        hasRef = doc.getDocumentElement().getAttribute("hasref").equals("true");
        hasAlt = doc.getDocumentElement().getAttribute("hasalt").equals("true");
        version = doc.getDocumentElement().getAttribute("version"); 
        program = doc.getDocumentElement().getAttribute("program"); 
        referenceName = doc.getDocumentElement().getAttribute("reference");
        annotationType = AnnotationFormat.annotationTypeToInt(doc.getDocumentElement().getAttribute("type"));
        
        prefix = program + "_" + version.replaceAll("\\.", "_") + "_"; 
        
        
        //get custom columns
        NodeList fields = doc.getElementsByTagName("field");
        for(int i = 0; i < fields.getLength(); i++){
            Element field = (Element)(fields.item(i));       
            annotationFields.add(new AnnotationField(
                    prefix + field.getAttribute("name"),
                    field.getAttribute("type"),
                    field.getAttribute("filterable").equals("true"),
                    field.getAttribute("alias"),
                    field.getAttribute("description")
                    ));
        }
    }
    
    private static void formatTabix(String[] header, File infile, File outfile) throws Exception {
        ColumnMapping mapping = new ColumnMapping(header);
        Conf conf = mapping.getTabixConf();
        
        //Assume that the annotation file is already sorted. If not, God help us. 
        
        //Compress the text file 
        AsciiLineReader input = new AsciiLineReader(new FileInputStream(infile));
        PrintWriter output = new PrintWriter(new BlockCompressedOutputStream(outfile));
        String line;
        while ((line = input.readLine()) != null) {
            output.println(line);
        }
        output.close();
        input.close();
        
        //Create index file
        TabixWriter writer = new TabixWriter(outfile, conf);
        writer.createIndex(outfile);
    }
      
}

class ColumnMapping {

    private int index_chrom;
    //private int index_position;
    private int index_start;
    private int index_end;    
    private String[] columnNames;

    public ColumnMapping(String[] columnNames){
        this.columnNames = columnNames;
        for(int i = 0; i < columnNames.length; i++){
            String col = columnNames[i];
            if (col.equals("chrom")){
                index_chrom = i;
            } else if (col.equals("position")){
                //index_position = i;
                index_start = i;
                index_end = i;
            } else if (col.equals("start")){
                index_start = i;
            } else if (col.equals("end")){
                index_end = i;
            }
        }
    }

    /*public String getColumnName(int index){
        return columnNames[index];
    }

    public int getIndexChrom(){
        return index_chrom;
    }

    public int getIndexPosition(){
        return index_position;
    }*/

    public Conf getTabixConf(){
        return new Conf(0, index_chrom+1, index_start+1, index_end+1, '#', 0);
    }

}
