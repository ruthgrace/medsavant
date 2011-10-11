package org.ut.biolab.medsavant.db.admin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.table.*;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;
import org.ut.biolab.medsavant.db.util.DBUtil;
import org.ut.biolab.medsavant.db.util.query.UserQueryUtil;

/**
 *
 * @author mfiume
 */
public class Setup {

    private static void dropTables() throws SQLException {


        if (DBUtil.tableExists(DBSettings.DBNAME, UserTable.TABLENAME)) {
            List<String> userNames = getValuesFromField(UserTable.TABLENAME, "name");
            for (String s : userNames) {
                UserQueryUtil.removeUser(s);
            }
        }

        if (DBUtil.tableExists(DBSettings.DBNAME, PatientInfoTable.TABLENAME)) {
            List<String> patientTables = getValuesFromField(PatientInfoTable.TABLENAME, "patient_tablename");
            for (String s : patientTables) {
                DBUtil.dropTable(s);
            }
        }
        if (DBUtil.tableExists(DBSettings.DBNAME, VariantInfoTable.TABLENAME)) {
            List<String> variantTables = getValuesFromField(VariantInfoTable.TABLENAME, "variant_tablename");
            for (String s : variantTables) {
                DBUtil.dropTable(s);
            }
        }

        DBUtil.dropTable(ServerLogTable.TABLENAME);
        DBUtil.dropTable(UserTable.TABLENAME);
        DBUtil.dropTable(AnnotationTable.TABLENAME);
        DBUtil.dropTable(ReferenceTable.TABLENAME);
        DBUtil.dropTable(ProjectTable.TABLENAME);
        DBUtil.dropTable(PatientInfoTable.TABLENAME);
        DBUtil.dropTable(VariantInfoTable.TABLENAME);
        DBUtil.dropTable(RegionSetTable.TABLENAME);
        DBUtil.dropTable(RegionSetMembershipTable.TABLENAME);
        DBUtil.dropTable(CohortTable.TABLENAME);
        DBUtil.dropTable(CohortMembershipTable.TABLENAME);
        DBUtil.dropTable(VariantPendingUpdateTable.TABLENAME);
    }

    private static void createTables() throws SQLException {

        Connection c = (ConnectionController.connect(DBSettings.DBNAME));

        c.createStatement().execute(
                "CREATE TABLE `" + ServerLogTable.TABLENAME + "` ("
                  + "`id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
                  + "`user` varchar(50) COLLATE latin1_bin DEFAULT NULL,"
                  + "`event` varchar(50) COLLATE latin1_bin DEFAULT NULL,"
                  + "`description` blob,"
                  + "PRIMARY KEY (`id`)"
                + ") ENGINE=MyISAM;"
                );
        
        c.createStatement().execute(
                "CREATE TABLE `" + UserTable.TABLENAME + "` ("
                + "`id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
                + "`name` varchar(50) COLLATE latin1_bin NOT NULL DEFAULT '',"
                + "`is_admin` tinyint(1) NOT NULL DEFAULT '0',"
                + "PRIMARY KEY (`id`),"
                + "UNIQUE KEY `name` (`name`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute(
                "CREATE TABLE `" + RegionSetTable.TABLENAME + "` ("
                + "`regionset_id` int(11) NOT NULL AUTO_INCREMENT,"
                + "`name` varchar(255) CHARACTER SET latin1 NOT NULL,"
                + "PRIMARY KEY (`regionset_id`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute("CREATE TABLE `" + RegionSetMembershipTable.TABLENAME + "` ("
                + "`regionset_id` int(11) NOT NULL,"
                + "`genome_id` int(11) NOT NULL,"
                + "`chrom` varchar(255) COLLATE latin1_bin NOT NULL,"
                + "`start` int(11) NOT NULL,"
                + "`end` int(11) NOT NULL,"
                + "`description` varchar(255) COLLATE latin1_bin NOT NULL"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute("CREATE TABLE `" + CohortTable.TABLENAME + "` ("
                + "`cohort_id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
                + "`project_id` int(11) unsigned NOT NULL"
                + "`name` varchar(255) CHARACTER SET latin1 NOT NULL,"
                + "PRIMARY KEY (`cohort_id`,`project_id`) USING BTREE"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute("CREATE TABLE `" + CohortMembershipTable.TABLENAME + "` ("
                + "`cohort_id` int(11) unsigned NOT NULL,"
                + "`patient_id` int(11) unsigned NOT NULL,"
                + "PRIMARY KEY (`patient_id`,`cohort_id`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute(
                "CREATE TABLE `" + ReferenceTable.TABLENAME + "` ("
                + "`reference_id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
                + "`name` varchar(50) COLLATE latin1_bin NOT NULL,"
                + "PRIMARY KEY (`reference_id`), "
                + "UNIQUE KEY `name` (`name`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute("CREATE TABLE `" + AnnotationTable.TABLENAME + "` ("
                + "`annotation_id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
                + "`program` varchar(100) COLLATE latin1_bin NOT NULL DEFAULT '',"
                + "`version` varchar(100) COLLATE latin1_bin DEFAULT NULL,"
                + "`reference_id` int(11) unsigned NOT NULL,"
                + "`path` varchar(500) COLLATE latin1_bin NOT NULL DEFAULT '',"
                + "`has_ref` tinyint(1) NOT NULL,"
                + "`has_alt` tinyint(1) NOT NULL,"
                + "PRIMARY KEY (`annotation_id`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute(
                "CREATE TABLE `" + ProjectTable.TABLENAME + "` "
                + "(`project_id` int(11) unsigned NOT NULL AUTO_INCREMENT, "
                + "`name` varchar(50) NOT NULL, "
                + "PRIMARY KEY (`project_id`), "
                + "UNIQUE KEY `name` (`name`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute(
                "CREATE TABLE `" + PatientInfoTable.TABLENAME + "` ("
                + "`project_id` int(11) unsigned NOT NULL,"
                + "`patient_tablename` varchar(100) COLLATE latin1_bin NOT NULL,"
                + "`format_tablename` varchar(100) COLLATE latin1_bin NOT NULL,"
                + "UNIQUE KEY `patient_tablename` (`patient_tablename`,`project_id`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute(
                "CREATE TABLE `" + VariantInfoTable.TABLENAME + "` ("
                + "`project_id` int(11) unsigned NOT NULL,"
                + "`reference_id` int(11) unsigned NOT NULL,"
                + "`variant_tablename` varchar(100) COLLATE latin1_bin NOT NULL,"
                + "`annotation_ids` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "UNIQUE KEY `unique` (`project_id`,`reference_id`,`variant_tablename`)"
                + ") ENGINE=MyISAM;");
        
        c.createStatement().execute(
                "CREATE TABLE  `" + VariantPendingUpdateTable.TABLENAME + "` ("
                + "`project_id` int(11) unsigned NOT NULL,"
                + "`reference_id` int(11) unsigned NOT NULL,"
                + "`action` int(11) unsigned NOT NULL,"
                + "`status` int(5) unsigned NOT NULL DEFAULT '0',"
                + "`timestamp` datetime DEFAULT NULL,"
                + "PRIMARY KEY (`project_id`,`reference_id`, `action`) USING BTREE"
                + ") ENGINE=MyISAM;");
        
        c.createStatement().execute(
                "CREATE TABLE  `" + AnnotationMapTable.TABLENAME + "` ("
                + "`annotation_id` int(10) unsigned NOT NULL,"
                + "`format_tablename` varchar(45) COLLATE latin1_bin NOT NULL,"
                + "PRIMARY KEY (`annotation_id`) USING BTREE"
                + ") ENGINE=MyISAM;");
        
    }

    public static void main(String[] argv) throws SQLException {
        dropTables();
        createTables();
    }

    private static List<String> getValuesFromField(String tablename, String fieldname) throws SQLException {
        String q = "SELECT `" + fieldname + "` FROM `" + tablename + "`";
        Statement stmt = (ConnectionController.connect(DBSettings.DBNAME)).createStatement();
        ResultSet rs = stmt.executeQuery(q);
        List<String> results = new ArrayList<String>();
        while (rs.next()) {
            results.add(rs.getString(1));
        }
        return results;
    }
}
