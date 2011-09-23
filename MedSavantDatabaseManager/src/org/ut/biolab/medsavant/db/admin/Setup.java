package org.ut.biolab.medsavant.db.admin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.Manage;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;
import org.ut.biolab.medsavant.db.util.DBUtil;

/**
 *
 * @author mfiume
 */
public class Setup {

    private static void dropTables() throws SQLException {


        if (DBUtil.tableExists(DBSettings.DBNAME, DBSettings.TABLENAME_USER)) {
            List<String> userNames = getValuesFromField(DBSettings.TABLENAME_USER, "name");
            for (String s : userNames) {
                Manage.removeUser(s);
            }
        }

        if (DBUtil.tableExists(DBSettings.DBNAME, DBSettings.TABLENAME_PATIENTTABLEINFO)) {
            List<String> patientTables = getValuesFromField(DBSettings.TABLENAME_PATIENTTABLEINFO, "patient_tablename");
            for (String s : patientTables) {
                DBUtil.dropTable(s);
            }
        }
        if (DBUtil.tableExists(DBSettings.DBNAME, DBSettings.TABLENAME_VARIANTTABLEINFO)) {
            List<String> variantTables = getValuesFromField(DBSettings.TABLENAME_VARIANTTABLEINFO, "variant_tablename");
            for (String s : variantTables) {
                DBUtil.dropTable(s);
            }
        }

        DBUtil.dropTable(DBSettings.TABLENAME_USER);
        DBUtil.dropTable(DBSettings.TABLENAME_ANNOTATION);
        DBUtil.dropTable(DBSettings.TABLENAME_REFERENCE);
        DBUtil.dropTable(DBSettings.TABLENAME_PROJECT);
        DBUtil.dropTable(DBSettings.TABLENAME_PATIENTTABLEINFO);
        DBUtil.dropTable(DBSettings.TABLENAME_VARIANTTABLEINFO);
        DBUtil.dropTable(DBSettings.TABLENAME_REGIONSET);
        DBUtil.dropTable(DBSettings.TABLENAME_REGIONSETMEMBERSHIP);
        DBUtil.dropTable(DBSettings.TABLENAME_COHORT);
        DBUtil.dropTable(DBSettings.TABLENAME_COHORTMEMBERSHIP);
        DBUtil.dropTable(DBSettings.TABLENAME_VARIANTPENDINGUPDATE);
    }

    private static void createTables() throws SQLException {

        Connection c = (ConnectionController.connect(DBSettings.DBNAME));

        c.createStatement().execute(
                "CREATE TABLE `" + DBSettings.TABLENAME_USER + "` ("
                + "`id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
                + "`name` varchar(50) COLLATE latin1_bin NOT NULL DEFAULT '',"
                + "`is_admin` tinyint(1) NOT NULL DEFAULT '0',"
                + "PRIMARY KEY (`id`),"
                + "UNIQUE KEY `name` (`name`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute(
                "CREATE TABLE `" + DBSettings.TABLENAME_REGIONSET + "` ("
                + "`regionset_id` int(11) NOT NULL AUTO_INCREMENT,"
                + "`name` varchar(255) CHARACTER SET latin1 NOT NULL,"
                + "PRIMARY KEY (`regionset_id`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute("CREATE TABLE `" + DBSettings.TABLENAME_REGIONSETMEMBERSHIP + "` ("
                + "`regionset_id` int(11) NOT NULL,"
                + "`genome_id` int(11) NOT NULL,"
                + "`chrom` varchar(255) COLLATE latin1_bin NOT NULL,"
                + "`start` int(11) NOT NULL,"
                + "`end` int(11) NOT NULL,"
                + "`description` varchar(255) COLLATE latin1_bin NOT NULL"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute("CREATE TABLE `" + DBSettings.TABLENAME_COHORT + "` ("
                + "`cohort_id` int(11) NOT NULL AUTO_INCREMENT,"
                + "`name` varchar(255) CHARACTER SET latin1 NOT NULL,"
                + "PRIMARY KEY (`cohort_id`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute("CREATE TABLE `" + DBSettings.TABLENAME_COHORTMEMBERSHIP + "` ("
                + "`cohort_id` int(11) NOT NULL,"
                + "`hospital_id` varchar(255) CHARACTER SET latin1 NOT NULL"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute(
                "CREATE TABLE `" + DBSettings.TABLENAME_REFERENCE + "` ("
                + "`reference_id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
                + "`name` varchar(50) COLLATE latin1_bin NOT NULL,"
                + "PRIMARY KEY (`reference_id`), "
                + "UNIQUE KEY `name` (`name`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute("CREATE TABLE `" + DBSettings.TABLENAME_ANNOTATION + "` ("
                + "`annotation_id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
                + "`program` varchar(100) COLLATE latin1_bin NOT NULL DEFAULT '',"
                + "`version` varchar(100) COLLATE latin1_bin DEFAULT NULL,"
                + "`reference_id` int(11) unsigned NOT NULL,"
                + "`path` varchar(500) COLLATE latin1_bin NOT NULL DEFAULT '',"
                + "`format` varchar(10000) COLLATE latin1_bin NOT NULL DEFAULT '',"
                + "PRIMARY KEY (`annotation_id`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute(
                "CREATE TABLE `" + DBSettings.TABLENAME_PROJECT + "` "
                + "(`project_id` int(11) unsigned NOT NULL AUTO_INCREMENT, "
                + "`name` varchar(50) NOT NULL, "
                + "PRIMARY KEY (`project_id`), "
                + "UNIQUE KEY `name` (`name`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute(
                "CREATE TABLE `" + DBSettings.TABLENAME_PATIENTTABLEINFO + "` ("
                + "`project_id` int(11) unsigned NOT NULL,"
                + "`patient_tablename` varchar(100) COLLATE latin1_bin NOT NULL,"
                + "UNIQUE KEY `patient_tablename` (`patient_tablename`,`project_id`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute(
                "CREATE TABLE `" + DBSettings.TABLENAME_VARIANTTABLEINFO + "` ("
                + "`project_id` int(11) unsigned NOT NULL,"
                + "`reference_id` int(11) unsigned NOT NULL,"
                + "`variant_tablename` varchar(100) COLLATE latin1_bin NOT NULL,"
                + "`annotation_ids` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "UNIQUE KEY `unique` (`project_id`,`reference_id`,`variant_tablename`)"
                + ") ENGINE=MyISAM;");
        
        c.createStatement().execute(
                "CREATE TABLE  `" + DBSettings.TABLENAME_VARIANTPENDINGUPDATE + "` ("
                + "`project_id` int(11) unsigned NOT NULL,"
                + "`reference_id` int(11) unsigned NOT NULL,"
                + "`action` int(11) unsigned NOT NULL,"
                + "PRIMARY KEY (`project_id`,`reference_id`, `action`) USING BTREE"
                + ") ENGINE=MyISAM;");

    }

    public static void main(String[] argv) throws SQLException {
        System.out.println("Dropping tables...");
        dropTables();
        System.out.println("Creating tables...");
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
