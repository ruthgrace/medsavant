package org.ut.biolab.medsavant.db;

import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;
import org.ut.biolab.medsavant.db.util.DBUtil;
import org.ut.biolab.medsavant.db.util.jobject.LogQueryUtil;
import org.ut.biolab.medsavant.db.util.jobject.LogQueryUtil.Action;

/**
 *
 * @author mfiume
 */
public class Manage {

    //private final DbTable projectsTable;
    private static final String PATIENT_TABLE_PREFIX = "z_patient";
    
    public Manage() throws SQLException {
        //projectsTable = DBUtil.importTable(DBSettings.DBNAME, DBSettings.TABLENAME_PROJECT);
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

    
    
    public static void removeUser(String name) throws SQLException {
        (ConnectionController.connect()).createStatement().execute(
                "DROP USER '"+name+"'@'localhost';");
        Connection c = ConnectionController.connect(DBSettings.DBNAME);
        
        c.createStatement().execute("DELETE FROM `" + DBSettings.TABLENAME_USER + "` WHERE name='" + name +"'");
    }
   
    public static int addAnnotation(String program, String version, int referenceid, String path, String format) throws SQLException {
        
        System.out.println("Adding annotation...");
        
        String q = "INSERT INTO " 
                + DBSettings.TABLENAME_ANNOTATION 
                + " VALUES (null,'" + program + "','" + version + "'," + referenceid + ",'" + path + "','" + format + "')";
        PreparedStatement stmt = (ConnectionController.connect(DBSettings.DBNAME)).prepareStatement(q,
                Statement.RETURN_GENERATED_KEYS);

        stmt.execute();
        ResultSet res = stmt.getGeneratedKeys();
        res.next();

        int annotid = res.getInt(1);

        return annotid;
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

    public void setAnnotations(int projectid, int refid, String annotation_ids) throws SQLException {
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
}
