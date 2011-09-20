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

/**
 *
 * @author mfiume
 */
public class Manage {

    //private final DbTable projectsTable;
    private static final String PATIENT_TABLE_PREFIX = "z_patient";
    private static final String VARIANT_TABLEINFO_PREFIX = "z_variant";

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

    public static int addReference(String name) throws SQLException {

        System.out.println("Adding reference...");
        
        String q = "INSERT INTO " + DBSettings.TABLENAME_REFERENCE + " VALUES (null,'" + name + "')";
        PreparedStatement stmt = (ConnectionController.connect(DBSettings.DBNAME)).prepareStatement(q,
                Statement.RETURN_GENERATED_KEYS);

        stmt.execute();
        ResultSet res = stmt.getGeneratedKeys();
        res.next();

        int refid = res.getInt(1);
        
        return refid;
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

    public String createVariantTable(int projectid, int referenceid) throws SQLException {
        
        
        String variantTableInfoName = VARIANT_TABLEINFO_PREFIX + "_proj" + projectid + "_ref" + referenceid;

        Connection c = (ConnectionController.connect(DBSettings.DBNAME));

        c.createStatement().execute(
                "CREATE TABLE `" + variantTableInfoName + "` ("
                + "`variant_id` int(11) NOT NULL,"
                + "`reference_id` int(11) NOT NULL,"
                + "`pipeline_id` varchar(10) COLLATE latin1_bin NOT NULL,"
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
                + "`custom_info` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "`variant_annotation_sift_id` int(11) DEFAULT NULL"
                + ") ENGINE=BRIGHTHOUSE;");

        String q = "INSERT INTO " + DBSettings.TABLENAME_VARIANTTABLEINFO + " VALUES (" + projectid + ",'" + referenceid + "','" + variantTableInfoName + "',null)";
        c.createStatement().execute(q);

        return variantTableInfoName;

    }

    public void setAnnotations(int projectid, int refid, String annotation_ids) throws SQLException {
        //String q = "UPDATE " + DBSettings.TABLENAME_VARIANTTABLEINFO + " SET annotation_ids=\"" + annotation_ids + "\" "
        //        + "WHERE (project_id=" + projectid + " AND reference_id=" + refid + ")";
        
        System.out.println("Setting annotation...");
        
        String q = "UPDATE " + DBSettings.TABLENAME_VARIANTTABLEINFO + " SET annotation_ids=\"" + annotation_ids + "\" "
                + "WHERE (project_id=" + (projectid)  + " AND reference_id=" + (refid) + ")";
        
        (ConnectionController.connect(DBSettings.DBNAME)).createStatement().execute(q);
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
