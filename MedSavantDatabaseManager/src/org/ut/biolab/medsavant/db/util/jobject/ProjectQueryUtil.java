package org.ut.biolab.medsavant.db.util.jobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;
import org.ut.biolab.medsavant.db.util.DBUtil;

/**
 *
 * @author mfiume
 */
public class ProjectQueryUtil {
    
    public static final String VARIANT_TABLEINFO_PREFIX = "z_variant";
    public static final String VARIANT_TABLEINFO_STAGING_PREFIX = "z_variant_staging";
    
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
        return createVariantTable(projectid, referenceid, false, true);
    }
    
    public static String createVariantTable(int projectid, int referenceid, boolean isStaging, boolean addToTableMap) throws SQLException {
        
        String variantTableInfoName = (isStaging ? VARIANT_TABLEINFO_STAGING_PREFIX : VARIANT_TABLEINFO_PREFIX) + "_proj" + projectid + "_ref" + referenceid;

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

        if(!isStaging && addToTableMap){
            String q = "INSERT INTO " + DBSettings.TABLENAME_VARIANTTABLEINFO + " VALUES (" + projectid + ",'" + referenceid + "','" + variantTableInfoName + "',null)";
            c.createStatement().execute(q);
        }

        return variantTableInfoName;
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

    
}
