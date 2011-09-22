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

/**
 *
 * @author Andrew
 */
public class UpdateVariantTable {
        
    public static void performUpdate(int projectId, int referenceId) throws SQLException{
        
        String tableName = "z_variant_proj" + projectId + "_ref" + referenceId;
        
        //create TDF from existing variants
        String tempFilename = "temp_" + projectId + "_" + referenceId;
        variantsToFile(tableName, tempFilename);
        
        //annotate
        String outputFilename = tempFilename + "_annotated";
        int[] annotationIds = AnnotationQueryUtil.getAnnotationIds(projectId, referenceId);
        annotateTDF(tempFilename, outputFilename, annotationIds);
        
        //upload file
        uploadFile(outputFilename, tableName);
        
        //remove temporary files
        removeTemp(tempFilename);
        removeTemp(outputFilename);
    }
    
    public static void performAddVCF(int projectId, int referenceId) throws SQLException, IOException{
        
        String tableName = "z_variant_proj" + projectId + "_ref" + referenceId;       
        
        //create TDF from staging table
        String stagingTableName = "z_variant_staging_proj" + projectId + "_ref" + referenceId;
        String tempFilename = "temp_" + projectId + "_ref" + referenceId;
        variantsToFile(stagingTableName, tempFilename);
        
        //annotate
        String annotatedFilename = tempFilename + "_annotated";
        int[] annotationIds = AnnotationQueryUtil.getAnnotationIds(projectId, referenceId);
        annotateTDF(tempFilename, annotatedFilename, annotationIds);
        
        //dump current table and append
        String outputFilename = tempFilename + "_output";
        dumpTableToFile(tableName, outputFilename);
        appendToFile(outputFilename, annotatedFilename);
        
        //upload file
        uploadFile(outputFilename, tableName);
        
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
            writer.newLine();
        }
        writer.close();
        reader.close();
    }
    
    private static void dumpTableToFile(String tableName, String filename) throws SQLException{
        Connection c = (ConnectionController.connect(DBSettings.DBNAME));
        c.createStatement().execute(
                "SELECT *"
                + " INTO OUTFILE " + filename
                + " FIELDS TERMINATED BY '\\t' LINES TERMINATED BY '\\n'"
                + " FROM " + tableName + ";");
    }
    
    private static void variantsToFile(String tableName, String filename) throws SQLException{
        Connection c = (ConnectionController.connect(DBSettings.DBNAME));
        c.createStatement().execute(
                "SELECT variant_id, reference_id, pipeline_id, dna_id, chrom, position, "
                + "dbsnp_id, ref, alt, qual, filter, aa, ac, af, an, bq, cigar, db, dp, "
                + "end, h2, mq, mq0, ns, sb, somatic, validated, custom_info, variant_annotation_sift_id"
                + " INTO OUTFILE " + filename
                + " FIELDS TERMINATED BY '\\t' LINES TERMINATED BY '\\n'"
                + " FROM " + tableName
                + " ORDER BY dna_id, chrom, position;"); //TODO: correct ordering?
    }
    
    private static void uploadFile(String filename, String tableName) throws SQLException{
        Connection c = (ConnectionController.connect(DBSettings.DBNAME));
        c.createStatement().execute(
                "LOAD DATA LOCAL INFILE '" + filename.replaceAll("\\\\", "/") + "' "
                + "INTO TABLE " + tableName + " "
                + "FIELDS TERMINATED BY '\\t' "
                + "LINES TERMINATED BY '\\n';");
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
