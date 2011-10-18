/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.server.update;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;
import org.ut.biolab.medsavant.db.util.DBUtil;
import org.ut.biolab.medsavant.db.util.query.AnnotationQueryUtil;
import org.ut.biolab.medsavant.db.util.query.ProjectQueryUtil;
import org.ut.biolab.medsavant.db.util.query.VariantQueryUtil;
import org.ut.biolab.medsavant.server.log.ServerLogger;

/**
 *
 * @author Andrew
 */
public class UpdateVariantTable {
        
    public static void performUpdate(int projectId, int referenceId) throws SQLException, Exception{
        
        String tableName = ProjectQueryUtil.getVariantTablename(projectId, referenceId);
        
        //create TDF from existing variants
        String tempFilename = "temp_proj" + projectId + "_ref" + referenceId;
        variantsToFile(tableName, new File(tempFilename));
        
        //drop table and recreate
        dropTable(tableName);
        ProjectQueryUtil.createVariantTable(projectId, referenceId, 0, AnnotationQueryUtil.getAnnotationIds(projectId, referenceId), false, false); //recreate with annotations
        
        //annotate
        String outputFilename = tempFilename + "_annotated";
        int[] annotationIds = AnnotationQueryUtil.getAnnotationIds(projectId, referenceId);
        annotateTDF(tempFilename, outputFilename, annotationIds);
        
        //upload file
        VariantQueryUtil.uploadFileToVariantTable(new File(outputFilename), tableName);
        
        //remove temporary files
        removeTemp(tempFilename);
        removeTemp(outputFilename);
    }
    
    public static void performAddVCF(int projectId, int referenceId, int updateId) throws SQLException, IOException, Exception{
        
        String tableName = ProjectQueryUtil.getVariantTablename(projectId, referenceId);
        
        //create TDF from staging table
        String stagingTableName = DBSettings.createVariantStagingTableName(projectId, referenceId, updateId);
        String tempFilename = "temp_proj" + projectId + "_ref" + referenceId + "_update" + updateId;
        variantsToFile(stagingTableName, new File(tempFilename));
        
        //annotate
        String annotatedFilename = tempFilename + "_annotated";
        int[] annotationIds = AnnotationQueryUtil.getAnnotationIds(projectId, referenceId);
        annotateTDF(tempFilename, annotatedFilename, annotationIds);
        
        //dump current table and append
        String outputFilename = tempFilename + "_output";
        dumpTableToFile(tableName, new File(outputFilename));
        appendToFile(outputFilename, annotatedFilename);
        
        //recreate empty table
        dropTable(tableName);
        ProjectQueryUtil.createVariantTable(projectId, referenceId, 0, AnnotationQueryUtil.getAnnotationIds(projectId, referenceId), false, false);
        
        //upload file
        VariantQueryUtil.uploadFileToVariantTable(new File(outputFilename), tableName);
        
        //remove temporary files
        removeTemp(tempFilename);
        removeTemp(outputFilename);
        removeTemp(annotatedFilename);
        
        //drop staging table
        dropTable(stagingTableName);       
    }
    
    private static void annotateTDF(String tdfFilename, String outputFilename, int[] annotationIds) throws Exception{
        (new Annotate(tdfFilename, outputFilename, annotationIds)).annotate();
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
                + " FIELDS TERMINATED BY ',' ENCLOSED BY '\"'"
                + " LINES TERMINATED BY '\\r\\n'"
                + " FROM " + tableName + ";");
    }
    
    private static void variantsToFile(String tableName, File file) throws SQLException{
        Connection c = (ConnectionController.connect(DBSettings.DBNAME));
        c.createStatement().execute(
                "SELECT `upload_id`, `file_id`, `variant_id`, `dna_id`, `chrom`, `position`, `"
                + "dbsnp_id`, `ref`, `alt`, `qual`, `filter`, `aa`, `ac`, `af`, `an`, `bq`, `cigar`, `db`, `dp`, `"
                + "end`, `h2`, `mq`, `mq0`, `ns`, `sb`, `somatic`, `validated`, `custom_info`"
                + " INTO OUTFILE \"" + file.getAbsolutePath().replaceAll("\\\\", "/") + "\""
                + " FIELDS TERMINATED BY ',' ENCLOSED BY '\"'"
                + " LINES TERMINATED BY '\\r\\n'"
                + " FROM " + tableName);
                //+ " ORDER BY `dna_id`, `chrom`, `position`;"); //TODO: correct ordering?
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
