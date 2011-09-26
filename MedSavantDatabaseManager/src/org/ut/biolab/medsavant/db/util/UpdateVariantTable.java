/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.ut.biolab.medsavant.db.util.jobject.AnnotationQueryUtil;
import org.ut.biolab.medsavant.db.util.jobject.ProjectQueryUtil;

/**
 *
 * @author Andrew
 */
public class UpdateVariantTable {
        
    public static void performUpdate(int projectId, int referenceId) throws SQLException{
        
        String tableName = ProjectQueryUtil.VARIANT_TABLEINFO_PREFIX + "_proj" + projectId + "_ref" + referenceId; 
        
        //create TDF from existing variants
        String tempFilename = "temp_proj" + projectId + "_" + referenceId;
        variantsToFile(tableName, new File(tempFilename));
        
        //annotate
        String outputFilename = tempFilename + "_annotated";
        int[] annotationIds = AnnotationQueryUtil.getAnnotationIds(projectId, referenceId);
        annotateTDF(tempFilename, outputFilename, annotationIds);
        
        //upload file
        uploadFile(new File(outputFilename), tableName);
        
        //remove temporary files
        removeTemp(tempFilename);
        removeTemp(outputFilename);
    }
    
    public static void performAddVCF(int projectId, int referenceId) throws SQLException, IOException{
        
        String tableName = ProjectQueryUtil.VARIANT_TABLEINFO_PREFIX + "_proj" + projectId + "_ref" + referenceId;       
        
        //create TDF from staging table
        String stagingTableName = ProjectQueryUtil.VARIANT_TABLEINFO_STAGING_PREFIX + "_proj" + projectId + "_ref" + referenceId;
        String tempFilename = "temp_proj" + projectId + "_ref" + referenceId;
        variantsToFile(stagingTableName, new File(tempFilename));
        
        //annotate
        String annotatedFilename = tempFilename + "_annotated";
        int[] annotationIds = AnnotationQueryUtil.getAnnotationIds(projectId, referenceId);
        annotateTDF(tempFilename, annotatedFilename, annotationIds);
        
        //dump current table and append
        String outputFilename = tempFilename + "_output";
        dumpTableToFile(tableName, new File(outputFilename));
        appendToFile(outputFilename, tempFilename); //TODO: tempFilename -> annotatedFilename
        
        //recreate empty table
        dropTable(tableName);
        ProjectQueryUtil.createVariantTable(projectId, referenceId, false, false);
        
        //upload file
        uploadFile(new File(outputFilename), tableName);
        
        //remove temporary files
        removeTemp(tempFilename);
        removeTemp(outputFilename);
        removeTemp(annotatedFilename);
        
        //drop staging table
        dropTable(stagingTableName);       
    }
    
    private static void annotateTDF(String tdfFilename, String outputFilename, int[] annotationIds){
        
        //TODO: MARC
        
    }
    
    private static void appendToFile(String baseFilename, String appendingFilename) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(baseFilename, true));
        BufferedReader reader = new BufferedReader(new FileReader(appendingFilename));
        String line; 
        while((line = reader.readLine()) != null){
            writer.write(line);
            writer.write("\r\n");
        }
        writer.close();
        reader.close();
    }
    
    private static void dumpTableToFile(String tableName, File file) throws SQLException{
        Connection c = (ConnectionController.connect(DBSettings.DBNAME));
        c.createStatement().execute(
                "SELECT *"
                + " INTO OUTFILE \"" + file.getAbsolutePath().replaceAll("\\\\", "/") + "\""
                + " FIELDS TERMINATED BY '\\t' LINES TERMINATED BY '\\r\\n'"
                + " FROM " + tableName + ";");
    }
    
    private static void variantsToFile(String tableName, File file) throws SQLException{
        Connection c = (ConnectionController.connect(DBSettings.DBNAME));
        c.createStatement().execute(
                "SELECT `variant_id`, `reference_id`, `pipeline_id`, `dna_id`, `chrom`, `position`, `"
                + "dbsnp_id`, `ref`, `alt`, `qual`, `filter`, `aa`, `ac`, `af`, `an`, `bq`, `cigar`, `db`, `dp`, `"
                + "end`, `h2`, `mq`, `mq0`, `ns`, `sb`, `somatic`, `validated`, `custom_info`, `variant_annotation_sift_id`"
                + " INTO OUTFILE \"" + file.getAbsolutePath().replaceAll("\\\\", "/") + "\""
                + " FIELDS TERMINATED BY '\\t' LINES TERMINATED BY '\\r\\n'"
                + " FROM " + tableName
                + " ORDER BY `dna_id`, `chrom`, `position`;"); //TODO: correct ordering?
    }
    
    public static void uploadFile(File file, String tableName) throws SQLException{
        Connection c = (ConnectionController.connect(DBSettings.DBNAME));
        c.createStatement().execute(
                "LOAD DATA LOCAL INFILE '" + file.getAbsolutePath().replaceAll("\\\\", "/") + "' "
                + "INTO TABLE " + tableName + " "
                + "FIELDS TERMINATED BY '\\t' "
                + "LINES TERMINATED BY '\\r\\n';");
    }
    
    private static void removeTemp(String filename){
        removeTemp(new File(filename));
    }
    
    private static void removeTemp(File temp){
        if(!temp.delete()){
            temp.deleteOnExit();
        }
    }
    
    private static void dropTable(String tableName) throws SQLException{
        Connection c = (ConnectionController.connect(DBSettings.DBNAME));
        c.createStatement().execute(
                "DROP TABLE IF EXISTS " + tableName + ";");
    }
        
}
