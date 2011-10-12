package org.ut.biolab.medsavant.db.util.query;

import org.ut.biolab.medsavant.db.util.ProjectDetails;
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
import org.ut.biolab.medsavant.db.table.VariantTable;
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
        
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT " + ProjectTable.FIELDNAME_NAME 
                + " FROM " + ProjectTable.TABLENAME);
        
        List<String> results = new ArrayList<String>();
        
        while (rs.next()) {
            results.add(rs.getString(1));
        }
        
        return results;
    }
    
    
    public static boolean containsProject(String projectName) throws SQLException {
        
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery(
                "SELECT *"
                + " FROM `" + ProjectTable.TABLENAME + "`"
                + " WHERE " + ProjectTable.FIELDNAME_NAME + "=\"" + projectName + "\"");
        
        return rs1.next();
    }

    public static int getProjectId(String projectName) throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery(
                "SELECT " + ProjectTable.FIELDNAME_ID
                + " FROM `" + ProjectTable.TABLENAME + "`"
                + " WHERE " + ProjectTable.FIELDNAME_NAME + "=\"" + projectName + "\"");
        
        if (rs1.next()) {
            return rs1.getInt(1);
        } else {
            return -1;
        }
    }

    public static void removeReferenceForProject(int project_id, int ref_id) throws SQLException {
        
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery(
                "SELECT " + VariantMapTable.FIELDNAME_VARIANTTABLENAME
                + " FROM `" + VariantMapTable.TABLENAME + "`"
                + " WHERE " + VariantMapTable.FIELDNAME_PROJECTID + "=" + project_id 
                + " AND " + VariantMapTable.FIELDNAME_REFERENCEID + "=" + ref_id);
        
        while (rs1.next()) {
            String tableName = rs1.getString(1);
            DBUtil.dropTable(tableName);
        }
        
        c.createStatement().execute(
                "DELETE FROM `" + VariantMapTable.TABLENAME + "`"
                + " WHERE " + VariantMapTable.FIELDNAME_PROJECTID + "=" + project_id 
                + " AND " + VariantMapTable.FIELDNAME_REFERENCEID + "=" + ref_id);
    }

    public static String getProjectName(int projectid) throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery(
                "SELECT " + ProjectTable.FIELDNAME_NAME
                + " FROM `" + ProjectTable.TABLENAME + "`"
                + " WHERE " + ProjectTable.FIELDNAME_ID + "=" + projectid );
        
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
                + "`" + VariantTable.FIELDNAME_UPLOADID + "` int(11) NOT NULL,"
                + "`" + VariantTable.FIELDNAME_FILEID + "` int(11) NOT NULL,"
                + "`" + VariantTable.FIELDNAME_VARIANTID + "` int(11) NOT NULL,"
                + "`" + VariantTable.FIELDNAME_DNAID + "` varchar(100) COLLATE latin1_bin NOT NULL,"
                + "`" + VariantTable.FIELDNAME_CHROM + "` varchar(5) COLLATE latin1_bin NOT NULL DEFAULT '',"
                + "`" + VariantTable.FIELDNAME_POSITION + "` int(11) NOT NULL,"
                + "`" + VariantTable.FIELDNAME_DBSNPID + "` varchar(45) COLLATE latin1_bin DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_REF + "` varchar(30) COLLATE latin1_bin DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_ALT + "` varchar(30) COLLATE latin1_bin DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_QUAL + "` float(10,0) DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_FILTER + "` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_AA + "` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_AC + "` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_AF + "` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_AN + "` int(11) DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_BQ + "` float DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_CIGAR + "` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_DB + "` int(1) DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_DP + "` int(11) DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_END + "` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_H2 + "` int(1) DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_MQ + "` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_MQ0 + "` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_NS + "` int(11) DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_SB + "` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_SOMATIC + "` int(1) DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_VALIDATED + "` int(1) DEFAULT NULL,"
                + "`" + VariantTable.FIELDNAME_CUSTOMINFO + "` varchar(500) COLLATE latin1_bin DEFAULT NULL,";
        
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
                "SELECT variant_tablename"
                + " FROM `" + VariantMapTable.TABLENAME + "`"
                + " WHERE " + VariantMapTable.FIELDNAME_PROJECTID + "=" + projectid 
                + " AND " + VariantMapTable.FIELDNAME_REFERENCEID + "=" + refid);
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
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT project_id"
                + " FROM `" + ProjectTable.TABLENAME + "`"
                + " WHERE " + ProjectTable.FIELDNAME_NAME + "=\"" + projectName + "\"");
        
        if (rs.next()) {
            removeProject(rs.getInt(1));
        }
    }
      
      
    public static void removeProject(int projectid) throws SQLException {
        
        
        Connection c = ConnectionController.connect(DBSettings.DBNAME);
        
        //remove from project table
        c.createStatement().execute(
                "DELETE FROM `" + ProjectTable.TABLENAME + "`"
                + " WHERE " + ProjectTable.FIELDNAME_ID + "=" + projectid);    
        
        //remove patient table and patient format table
        ResultSet rs1 = c.createStatement().executeQuery(
                "SELECT " + PatientMapTable.FIELDNAME_PATIENTTABLENAME + ", " + PatientMapTable.FIELDNAME_FORMATTABLENAME 
                + " FROM " + PatientMapTable.TABLENAME 
                + " WHERE " + PatientMapTable.FIELDNAME_PROJECTID + "=" + projectid);    
        rs1.next();
        String patientTableName = rs1.getString(PatientMapTable.FIELDNAME_PATIENTTABLENAME);
        String patientFormatTableName = rs1.getString(PatientMapTable.FIELDNAME_FORMATTABLENAME);
        c.createStatement().execute("DROP TABLE IF EXISTS " + patientTableName);
        c.createStatement().execute("DROP TABLE IF EXISTS " + patientFormatTableName);
        
        //remove from patient tablemap
        c.createStatement().execute(
                "DELETE FROM `" + PatientMapTable.TABLENAME + "`"
                + " WHERE " + PatientMapTable.FIELDNAME_PROJECTID + "=" + projectid);
        
        //remove variant tables
        ResultSet rs2 = c.createStatement().executeQuery(
                "SELECT " + VariantMapTable.FIELDNAME_VARIANTTABLENAME
                + " FROM " + VariantMapTable.TABLENAME 
                + " WHERE " + VariantMapTable.FIELDNAME_PROJECTID + "=" + projectid);   
        while(rs2.next()) {
            String variantTableName = rs2.getString(1);
            c.createStatement().execute("DROP TABLE IF EXISTS " + variantTableName);
        }
        
        //remove from variant tablemap
        c.createStatement().execute(
                "DELETE FROM `" + VariantMapTable.TABLENAME + "`"
                + " WHERE " + VariantMapTable.FIELDNAME_PROJECTID + "=" + projectid);

        //remove cohort entries
        List<Integer> cohortIds = CohortQueryUtil.getCohortIds(projectid);
        for(Integer cohortId : cohortIds){
            CohortQueryUtil.removeCohort(cohortId);
        }
        
    }
    
    
    public static void setAnnotations(int projectid, int refid, String annotation_ids) throws SQLException {
        
        (ConnectionController.connect()).createStatement().execute(
                "UPDATE " + VariantMapTable.TABLENAME 
                + " SET " + VariantMapTable.FIELDNAME_ANNOTATIONIDS + "=\"" + annotation_ids + "\""
                + " WHERE " + VariantMapTable.FIELDNAME_PROJECTID + "=" + projectid  
                + " AND " + VariantMapTable.FIELDNAME_REFERENCEID + "=" + refid);
        
        AnnotationLogQueryUtil.addAnnotationLogEntry(projectid, refid, Action.UPDATE_TABLE, Status.PENDING);
    }
    
    public static List<ProjectDetails> getProjectDetails(int projectId) throws SQLException {
        
        ResultSet rs = org.ut.biolab.medsavant.db.util.ConnectionController.connect().createStatement().executeQuery(
                "SELECT *"
                + " FROM " + VariantMapTable.TABLENAME
                + " LEFT JOIN " + ReferenceTable.TABLENAME + " ON "
                + VariantMapTable.TABLENAME + "." + VariantMapTable.FIELDNAME_REFERENCEID + " = "
                + ReferenceTable.TABLENAME + "." + ReferenceTable.FIELDNAME_ID
                + " WHERE project_id=" + projectId + ";");
        
        List<ProjectDetails> result = new ArrayList<ProjectDetails>();
        while(rs.next()){
            result.add(new ProjectDetails(
                    rs.getInt(VariantMapTable.FIELDNAME_REFERENCEID), 
                    rs.getString(ReferenceTable.FIELDNAME_NAME), 
                    rs.getString(VariantMapTable.FIELDNAME_ANNOTATIONIDS)));
        }
        
        return result;
    }

}
