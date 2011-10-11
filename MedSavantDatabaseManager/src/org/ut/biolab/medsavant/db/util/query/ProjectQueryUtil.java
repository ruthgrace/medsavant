package org.ut.biolab.medsavant.db.util.query;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.ut.biolab.medsavant.db.format.AnnotationFormat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.table.PatientMapTable;
import org.ut.biolab.medsavant.db.table.ProjectTable;
import org.ut.biolab.medsavant.db.table.ReferenceTable;
import org.ut.biolab.medsavant.db.table.VariantMapTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;
import org.ut.biolab.medsavant.db.util.DBUtil;
import org.ut.biolab.medsavant.db.util.query.AnnotationLogQueryUtil.Action;
import org.ut.biolab.medsavant.db.util.query.AnnotationLogQueryUtil.Status;
import org.xml.sax.SAXException;

/**
 *
 * @author mfiume
 */
public class ProjectQueryUtil {
    
    public static List<String> getProjectNames() throws SQLException {
        
        Connection conn = ConnectionController.connect();
        
        ResultSet rs = conn.createStatement().executeQuery("SELECT name FROM " + ProjectTable.TABLENAME);
        
        List<String> results = new ArrayList<String>();
        
        while (rs.next()) {
            results.add(rs.getString(1));
        }
        
        return results;
    }
    
    
    public static boolean containsProject(String projectName) throws SQLException {
        
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT * FROM `" + ProjectTable.TABLENAME + "` WHERE name=\"" + projectName + "\"");
        
        return rs1.next();
    }

    public static int getProjectId(String projectName) throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT project_id FROM `" + ProjectTable.TABLENAME + "` WHERE name=\"" + projectName + "\"");
        
        if (rs1.next()) {
            return rs1.getInt(1);
        } else {
            return -1;
        }
    }

    public static void removeReferenceForProject(int project_id, int ref_id) throws SQLException {
        
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT variant_tablename FROM `" + VariantMapTable.TABLENAME + "` WHERE project_id=" + project_id + " AND reference_id=" + ref_id);
        
        while (rs1.next()) {
            String tableName = rs1.getString(1);
            DBUtil.dropTable(tableName);
        }
        
        c.createStatement().execute("DELETE FROM `" + VariantMapTable.TABLENAME + "` WHERE project_id=" + project_id + " AND reference_id=" + ref_id);
    }

    public static String getProjectName(int projectid) throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT name FROM `" + ProjectTable.TABLENAME + "` WHERE project_id=" + projectid );
        
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
        
        String variantTableInfoName = isStaging ? DBSettings.createVariantStagingTableName(projectid, referenceid, updateid) : DBSettings.createVariantTableName(projectid, referenceid);

        Connection c = (ConnectionController.connect(DBSettings.DBNAME));
   
        String query = 
                "CREATE TABLE `" + variantTableInfoName + "` ("
                + "`upload_id` int(11) NOT NULL,"
                + "`file_id` int(11) NOT NULL,"
                + "`variant_id` int(11) NOT NULL,"
                + "`dna_id` varchar(100) COLLATE latin1_bin NOT NULL,"
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
            String q = "INSERT INTO " + VariantMapTable.TABLENAME + " VALUES (" + projectid + ",'" + referenceid + "','" + variantTableInfoName + "',null)";
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

    public static String getVariantTable(int projectid, int refid) throws SQLException {
        
        Connection c = ConnectionController.connect();
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT variant_tablename FROM `" + VariantMapTable.TABLENAME + "` "
                + "WHERE project_id=" + projectid + " AND reference_id=" + refid);
        rs.next();
        return rs.getString(1);
    }
    
    
    public static int addProject(String name, File patientFormatFile) throws SQLException, ParserConfigurationException, SAXException, IOException {

        String projectQuery = "INSERT INTO " + ProjectTable.TABLENAME + " VALUES (null,'" + name + "')";
        PreparedStatement stmt = (ConnectionController.connect(DBSettings.DBNAME)).prepareStatement(projectQuery,
                Statement.RETURN_GENERATED_KEYS);

        stmt.execute();
        ResultSet res = stmt.getGeneratedKeys();
        res.next();

        int projectid = res.getInt(1);

        PatientQueryUtil.createPatientTable(projectid, patientFormatFile);

        return projectid;
    }

    public static void removeProject(String projectName) throws SQLException {
        
        Connection c = ConnectionController.connect(DBSettings.DBNAME);
        ResultSet rs = c.createStatement().executeQuery("SELECT project_id FROM `" + ProjectTable.TABLENAME + "` WHERE name=\"" + projectName + "\"");
        
        if (rs.next()) {
            removeProject(rs.getInt(1));
        }
    }
      
      
    public static void removeProject(int projectid) throws SQLException {
        
        
        Connection c = ConnectionController.connect(DBSettings.DBNAME);
        
        //remove from project table
        c.createStatement().execute("DELETE FROM `" + ProjectTable.TABLENAME + "` WHERE project_id=" + projectid);    
        
        //remove patient table and patient format table
        ResultSet rs1 = c.createStatement().executeQuery(
            "SELECT patient_tablename, format_tablename FROM " + PatientMapTable.TABLENAME + " WHERE project_id=" + projectid);    
        rs1.next();
        String patientTableName = rs1.getString("patient_tablename");
        String patientFormatTableName = rs1.getString("format_tablename");
        c.createStatement().execute("DROP TABLE IF EXISTS " + patientTableName);
        c.createStatement().execute("DROP TABLE IF EXISTS " + patientFormatTableName);
        
        //remove from patient tablemap
        c.createStatement().execute("DELETE FROM `" + PatientMapTable.TABLENAME + "` WHERE project_id=" + projectid);
        
        //remove variant tables
        ResultSet rs2 = c.createStatement().executeQuery(
            "SELECT variant_tablename FROM " + VariantMapTable.TABLENAME + " WHERE project_id=" + projectid);   
        while(rs2.next()) {
            String variantTableName = rs2.getString(1);
            c.createStatement().execute("DROP TABLE IF EXISTS " + variantTableName);
        }
        
        //remove from variant tablemap
        c.createStatement().execute("DELETE FROM `" + VariantMapTable.TABLENAME + "` WHERE project_id=" + projectid);

        //remove cohort entries
        List<Integer> cohortIds = CohortQueryUtil.getCohortIds(projectid);
        for(Integer cohortId : cohortIds){
            CohortQueryUtil.removeCohort(cohortId);
        }
        
    }
    
    
    public static void setAnnotations(int projectid, int refid, String annotation_ids) throws SQLException {
        //String q = "UPDATE " + DBSettings.TABLENAME_VARIANTTABLEINFO + " SET annotation_ids=\"" + annotation_ids + "\" "
        //        + "WHERE (project_id=" + projectid + " AND reference_id=" + refid + ")";
        
        String q = "UPDATE " + VariantMapTable.TABLENAME + " SET annotation_ids=\"" + annotation_ids + "\" "
                + "WHERE (project_id=" + (projectid)  + " AND reference_id=" + (refid) + ")";
        
        (ConnectionController.connect(DBSettings.DBNAME)).createStatement().execute(q);
        
        AnnotationLogQueryUtil.addAnnotationLogEntry(projectid, refid, Action.UPDATE_TABLE, Status.PENDING);
    }
    
    public static List<ProjectDetails> getProjectDetails(int projectId) throws SQLException {
        
        ResultSet rs = org.ut.biolab.medsavant.db.util.ConnectionController.connect().createStatement().executeQuery(
                        "SELECT * FROM " + VariantMapTable.TABLENAME
                        + " LEFT JOIN " + ReferenceTable.TABLENAME + " ON "
                        + VariantMapTable.TABLENAME + ".reference_id = "
                        + ReferenceTable.TABLENAME + ".reference_id "
                        + "WHERE project_id=" + projectId + ";");
        
        List<ProjectDetails> result = new ArrayList<ProjectDetails>();
        while(rs.next()){
            result.add(new ProjectDetails(rs.getInt("reference_id"), rs.getString("name"), rs.getString("annotation_ids")));
        }
        
        return result;
    }

}
