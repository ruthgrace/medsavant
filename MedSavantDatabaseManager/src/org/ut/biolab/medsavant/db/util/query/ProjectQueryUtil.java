package org.ut.biolab.medsavant.db.util.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;
import org.ut.biolab.medsavant.db.util.DBUtil;
import org.ut.biolab.medsavant.db.util.query.LogQueryUtil.Action;
import org.ut.biolab.medsavant.db.util.query.LogQueryUtil.Status;

/**
 *
 * @author mfiume
 */
public class ProjectQueryUtil {
    
    private static final String PATIENT_TABLE_PREFIX = "z_patient";
    
    public static List<String> getProjectNames() throws SQLException {
        
        Connection conn = ConnectionController.connect();
        
        ResultSet rs = conn.createStatement().executeQuery("SELECT name FROM " + org.ut.biolab.medsavant.db.util.DBSettings.TABLENAME_PROJECT);
        
        List<String> results = new ArrayList<String>();
        
        while (rs.next()) {
            results.add(rs.getString(1));
        }
        
        return results;
    }
    
    
    public static boolean containsProject(String projectName) throws SQLException {
        
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT * FROM `" + DBSettings.TABLENAME_PROJECT + "` WHERE name=\"" + projectName + "\"");
        
        return rs1.next();
    }

    public static int getProjectId(String projectName) throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT project_id FROM `" + DBSettings.TABLENAME_PROJECT + "` WHERE name=\"" + projectName + "\"");
        
        if (rs1.next()) {
            return rs1.getInt(1);
        } else {
            return -1;
        }
    }

    public static void removeReferenceForProject(int project_id, int ref_id) throws SQLException {
        
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT variant_tablename FROM `" + DBSettings.TABLENAME_VARIANTTABLEINFO + "` WHERE project_id=" + project_id + " AND reference_id=" + ref_id);
        
        while (rs1.next()) {
            String tableName = rs1.getString(1);
            DBUtil.dropTable(tableName);
        }
        
        c.createStatement().execute("DELETE FROM `" + DBSettings.TABLENAME_VARIANTTABLEINFO + "` WHERE project_id=" + project_id + " AND reference_id=" + ref_id);
    }

    public static String getProjectName(int projectid) throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT name FROM `" + DBSettings.TABLENAME_PROJECT + "` WHERE project_id=" + projectid );
        
        if (rs1.next()) {
            return rs1.getString(1);
        } else {
            return null;
        }
    }
    
    public static String createVariantTable(int projectid, int referenceid) throws SQLException {
        return createVariantTable(projectid, referenceid, 0, null, false, true);
    }

    public static String createVariantTable(int projectid, int referenceid, int updateid, int[] annotationIds, boolean isStaging, boolean addToTableMap) throws SQLException {
        
        String variantTableInfoName = isStaging ? DBUtil.getVariantStagingTableName(projectid, referenceid, updateid) : DBUtil.getVariantTableName(projectid, referenceid);

        Connection c = (ConnectionController.connect(DBSettings.DBNAME));
   
        String query = 
                "CREATE TABLE `" + variantTableInfoName + "` ("
                + "`upload_id` int(11) NOT NULL,"
                + "`file_id` int(11) NOT NULL,"
                + "`variant_id` int(11) NOT NULL,"
                + "`dna_id` varchar(10) COLLATE latin1_bin NOT NULL,"
                + "`chrom` varchar(5) COLLATE latin1_bin NOT NULL DEFAULT '',"
                + "`position` int(11) NOT NULL,"
                + "`dbsnp_id` varchar(45) COLLATE latin1_bin DEFAULT NULL,"
                + "`ref` varchar(30) COLLATE latin1_bin DEFAULT NULL,"
                + "`alt` varchar(30) COLLATE latin1_bin DEFAULT NULL,"
                + "`qual` float(10,0) DEFAULT NULL,"
                + "`filter` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`aa` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`ac` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`af` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`an` int(11) DEFAULT NULL,"
                + "`bq` float DEFAULT NULL,"
                + "`cigar` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`db` int(1) DEFAULT NULL,"
                + "`dp` int(11) DEFAULT NULL,"
                + "`end` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`h2` int(1) DEFAULT NULL,"
                + "`mq` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`mq0` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`ns` int(11) DEFAULT NULL,"
                + "`sb` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`somatic` int(1) DEFAULT NULL,"
                + "`validated` int(1) DEFAULT NULL,"
                + "`custom_info` varchar(500) COLLATE latin1_bin DEFAULT NULL,";
        
        //add each annotation
        if(annotationIds != null){
            for(int annotationId : annotationIds){
                query += getAnnotationSchema(annotationId);
            }
        }
        
        query = query.substring(0, query.length()-1); //remove last comma
        query += ") ENGINE=BRIGHTHOUSE;";

        c.createStatement().execute(query);

        if(!isStaging && addToTableMap){
            String q = "INSERT INTO " + DBSettings.TABLENAME_VARIANTTABLEINFO + " VALUES (" + projectid + ",'" + referenceid + "','" + variantTableInfoName + "',null)";
            c.createStatement().execute(q);
        }

        return variantTableInfoName;
    }
    
    private static String getAnnotationSchema(int annotationId){
        
        AnnotationFormat format = null;
        try {
            format = AnnotationQueryUtil.getAnnotationFormat(annotationId);
        } catch (Exception e){
            e.printStackTrace();
        }
        
        return format.generateSchema();
    }

    public static int getNumberOfRecordsInVariantTable(int projectid, int refid) throws SQLException {
        String variantTableName = ProjectQueryUtil.getVariantTable(projectid,refid);
        return DBUtil.getNumRecordsInTable(variantTableName);
    }

    private static String getVariantTable(int projectid, int refid) throws SQLException {
        
        Connection c = ConnectionController.connect();
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT variant_tablename FROM `" + DBSettings.TABLENAME_VARIANTTABLEINFO + "` "
                + "WHERE project_id=" + projectid + " AND reference_id=" + refid);
        rs.next();
        return rs.getString(1);
    }
    
    
    public static int addProject(String name) throws SQLException {

        System.out.println("Adding project...");
        
        String projectQuery = "INSERT INTO " + DBSettings.TABLENAME_PROJECT + " VALUES (null,'" + name + "')";
        PreparedStatement stmt = (ConnectionController.connect(DBSettings.DBNAME)).prepareStatement(projectQuery,
                Statement.RETURN_GENERATED_KEYS);

        stmt.execute();
        ResultSet res = stmt.getGeneratedKeys();
        res.next();
        //System.out.println("Key: " + res.getInt(1));

        int projectid = res.getInt(1);

        String patientTableName = createPatientTable(projectid);

        String patientQuery = "INSERT INTO " + DBSettings.TABLENAME_PATIENTTABLEINFO + " VALUES (" + projectid + ",'" + patientTableName + "')";
        (ConnectionController.connect(DBSettings.DBNAME)).createStatement().execute(patientQuery);

        return projectid;
    }

    
    private static String createPatientTable(int projectid) throws SQLException {

        String patientTableName = PATIENT_TABLE_PREFIX + "_proj" + projectid;

        Connection c = (ConnectionController.connect(DBSettings.DBNAME));

        c.createStatement().execute(
                "CREATE TABLE `" + patientTableName + "` ("
                + "`patient_id` int(11) unsigned NOT NULL,"
                + "`first_name` varchar(50) COLLATE latin1_bin DEFAULT NULL,"
                + "`last_name` varchar(50) COLLATE latin1_bin DEFAULT NULL,"
                + "PRIMARY KEY (`patient_id`)"
                + ") ENGINE=MyISAM;");

        return patientTableName;
    }
    
     public static void setAnnotations(int projectid, int refid, String annotation_ids) throws SQLException {
        //String q = "UPDATE " + DBSettings.TABLENAME_VARIANTTABLEINFO + " SET annotation_ids=\"" + annotation_ids + "\" "
        //        + "WHERE (project_id=" + projectid + " AND reference_id=" + refid + ")";
        
        System.out.println("Setting annotation...");
        
        String q = "UPDATE " + DBSettings.TABLENAME_VARIANTTABLEINFO + " SET annotation_ids=\"" + annotation_ids + "\" "
                + "WHERE (project_id=" + (projectid)  + " AND reference_id=" + (refid) + ")";
        
        (ConnectionController.connect(DBSettings.DBNAME)).createStatement().execute(q);
        
        LogQueryUtil.addLogEntry(projectid, refid, Action.UPDATE_TABLE);
    }


      public static void removeProject(String projectName) throws SQLException {
        
        Connection c = ConnectionController.connect(DBSettings.DBNAME);
        ResultSet rs = c.createStatement().executeQuery("SELECT project_id FROM `" + DBSettings.TABLENAME_PROJECT + "` WHERE name=\"" + projectName + "\"");
        
        if (rs.next()) {
            removeProject(rs.getInt(1));
        }
    }
      
      
    public static void removeProject(int projectid) throws SQLException {
        
        System.out.println("Removing project...");
        
        Connection c = ConnectionController.connect(DBSettings.DBNAME);
        
        c.createStatement().execute("DELETE FROM `" + DBSettings.TABLENAME_PROJECT + "` WHERE project_id=" + projectid);
        
        ResultSet rs1 = c.createStatement().executeQuery(
        "SELECT patient_tablename FROM " + DBSettings.TABLENAME_PATIENTTABLEINFO + " WHERE project_id=" + projectid);
    
        rs1.next();
        String patientTableName = rs1.getString(1);
        
        c.createStatement().execute("DROP TABLE IF EXISTS " + patientTableName);
        
        c.createStatement().execute("DELETE FROM `" + DBSettings.TABLENAME_PATIENTTABLEINFO + "` WHERE project_id=" + projectid);
        
        ResultSet rs2 = c.createStatement().executeQuery(
        "SELECT variant_tablename FROM " + DBSettings.TABLENAME_VARIANTTABLEINFO + " WHERE project_id=" + projectid);
    
        while(rs2.next()) {
            String variantTableName = rs2.getString(1);
            c.createStatement().execute("DROP TABLE IF EXISTS " + variantTableName);
        }
        
        c.createStatement().execute("DELETE FROM `" + DBSettings.TABLENAME_VARIANTTABLEINFO + "` WHERE project_id=" + projectid);
    }
    
    /*
    public void setAnnotations(int projectid, int refid, String annotation_ids) throws SQLException {
        //String q = "UPDATE " + DBSettings.TABLENAME_VARIANTTABLEINFO + " SET annotation_ids=\"" + annotation_ids + "\" "
        //        + "WHERE (project_id=" + projectid + " AND reference_id=" + refid + ")";
        
        System.out.println("Setting annotation...");
        
        String q = "UPDATE " + DBSettings.TABLENAME_VARIANTTABLEINFO + " SET annotation_ids=\"" + annotation_ids + "\" "
                + "WHERE (project_id=" + (projectid)  + " AND reference_id=" + (refid) + ")";
        
        (ConnectionController.connect(DBSettings.DBNAME)).createStatement().execute(q);
        
        LogQueryUtil.addLogEntry(projectid, refid, Action.UPDATE_TABLE, Status.PENDING);
    }
     * 
     */
    
}
