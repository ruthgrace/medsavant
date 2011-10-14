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

        if (DBUtil.tableExists(DBSettings.DBNAME, PatientMapTable.TABLENAME)) {
            List<String> patientTables = getValuesFromField(PatientMapTable.TABLENAME, "patient_tablename");
            for (String s : patientTables) {
                DBUtil.dropTable(s);
            }
        }
        
        if (DBUtil.tableExists(DBSettings.DBNAME, VariantMapTable.TABLENAME)) {
            List<String> variantTables = getValuesFromField(VariantMapTable.TABLENAME, "variant_tablename");
            for (String s : variantTables) {
                DBUtil.dropTable(s);
            }
        }

        DBUtil.dropTable(ServerLogTable.TABLENAME);
        DBUtil.dropTable(UserTable.TABLENAME);
        DBUtil.dropTable(AnnotationTable.TABLENAME);
        DBUtil.dropTable(ReferenceTable.TABLENAME);
        DBUtil.dropTable(ProjectTable.TABLENAME);
        DBUtil.dropTable(PatientMapTable.TABLENAME);
        DBUtil.dropTable(VariantMapTable.TABLENAME);
        DBUtil.dropTable(RegionSetTable.TABLENAME);
        DBUtil.dropTable(RegionSetMembershipTable.TABLENAME);
        DBUtil.dropTable(CohortTable.TABLENAME);
        DBUtil.dropTable(CohortMembershipTable.TABLENAME);
        DBUtil.dropTable(VariantPendingUpdateTable.TABLENAME);
        DBUtil.dropTable(PatientMapTable.TABLENAME);
        DBUtil.dropTable(ChromosomeTable.TABLENAME);
        DBUtil.dropTable(PatientFormatTable.TABLENAME);
        DBUtil.dropTable(AnnotationFormatTable.TABLENAME);
    }

    private static void createTables() throws SQLException {

        Connection c = (ConnectionController.connect(DBSettings.DBNAME));

        c.createStatement().execute(
                "CREATE TABLE `" + ServerLogTable.TABLENAME + "` ("
                  + "`id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
                  + "`user` varchar(50) COLLATE latin1_bin DEFAULT NULL,"
                  + "`event` varchar(50) COLLATE latin1_bin DEFAULT NULL,"
                  + "`description` blob,"
                  + "`timestamp` datetime NOT NULL,"
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
                + "`region_set_id` int(11) NOT NULL AUTO_INCREMENT,"
                + "`name` varchar(255) CHARACTER SET latin1 NOT NULL,"
                + "PRIMARY KEY (`region_set_id`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute(
                "CREATE TABLE `" + RegionSetMembershipTable.TABLENAME + "` ("
                + "`regionset_id` int(11) NOT NULL,"
                + "`genome_id` int(11) NOT NULL,"
                + "`chrom` varchar(255) COLLATE latin1_bin NOT NULL,"
                + "`start` int(11) NOT NULL,"
                + "`end` int(11) NOT NULL,"
                + "`description` varchar(255) COLLATE latin1_bin NOT NULL"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute(
                "CREATE TABLE `" + CohortTable.TABLENAME + "` ("
                + "`cohort_id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
                + "`project_id` int(11) unsigned NOT NULL,"
                + "`name` varchar(255) CHARACTER SET latin1 NOT NULL,"
                + "PRIMARY KEY (`cohort_id`,`project_id`) USING BTREE"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute(
                "CREATE TABLE `" + CohortMembershipTable.TABLENAME + "` ("
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

        c.createStatement().execute(
                "CREATE TABLE `" + AnnotationTable.TABLENAME + "` ("
                + "`annotation_id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
                + "`program` varchar(100) COLLATE latin1_bin NOT NULL DEFAULT '',"
                + "`version` varchar(100) COLLATE latin1_bin DEFAULT NULL,"
                + "`reference_id` int(11) unsigned NOT NULL,"
                + "`path` varchar(500) COLLATE latin1_bin NOT NULL DEFAULT '',"
                + "`has_ref` tinyint(1) NOT NULL,"
                + "`has_alt` tinyint(1) NOT NULL,"
                + "`type` int(11) unsigned NOT NULL,"
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
                "CREATE TABLE `" + PatientMapTable.TABLENAME + "` ("
                + "`project_id` int(11) unsigned NOT NULL,"
                + "`patient_tablename` varchar(100) COLLATE latin1_bin NOT NULL,"
                + "PRIMARY KEY (`project_id`)"
                + ") ENGINE=MyISAM;");

        c.createStatement().execute(
                "CREATE TABLE `" + VariantMapTable.TABLENAME + "` ("
                + "`project_id` int(11) unsigned NOT NULL,"
                + "`reference_id` int(11) unsigned NOT NULL,"
                + "`variant_tablename` varchar(100) COLLATE latin1_bin NOT NULL,"
                + "`annotation_ids` varchar(500) COLLATE latin1_bin DEFAULT NULL,"
                + "UNIQUE KEY `unique` (`project_id`,`reference_id`,`variant_tablename`)"
                + ") ENGINE=MyISAM;");
        
        c.createStatement().execute(
                "CREATE TABLE  `" + VariantPendingUpdateTable.TABLENAME + "` ("
                + "`update_id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
                + "`project_id` int(11) unsigned NOT NULL,"
                + "`reference_id` int(11) unsigned NOT NULL,"
                + "`action` int(11) unsigned NOT NULL,"
                + "`status` int(5) unsigned NOT NULL DEFAULT '0',"
                + "`timestamp` datetime DEFAULT NULL,"
                + "PRIMARY KEY (`update_id`) USING BTREE"
                + ") ENGINE=MyISAM;");
        
        c.createStatement().execute(
                "CREATE TABLE  `" + ChromosomeTable.TABLENAME + "` ("
                + "`reference_id` int(11) unsigned NOT NULL,"
                + "`contig_id` int(11) unsigned NOT NULL,"
                + "`contig_name` varchar(100) COLLATE latin1_bin NOT NULL,"
                + "`contig_length` int(11) unsigned NOT NULL,"
                + "`centromere_pos` int(11) unsigned NOT NULL,"
                + "PRIMARY KEY (`reference_id`,`contig_id`) USING BTREE"
                +") ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_bin;");
        
        c.createStatement().execute(
                "CREATE TABLE  `" + AnnotationFormatTable.TABLENAME + "` ("
                + "`annotation_id` int(11) unsigned NOT NULL,"
                + "`position` int(11) unsigned NOT NULL,"
                + "`column_name` varchar(200) COLLATE latin1_bin NOT NULL,"
                + "`column_type` varchar(45) COLLATE latin1_bin NOT NULL,"
                + "`filterable` tinyint(1) NOT NULL,"
                + "`alias` varchar(200) COLLATE latin1_bin NOT NULL,"
                + "`description` varchar(500) COLLATE latin1_bin NOT NULL,"
                + "PRIMARY KEY (`annotation_id`,`position`)"
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_bin;");
        
        c.createStatement().execute(
                "CREATE TABLE  `" + PatientFormatTable.TABLENAME + "` ("
                + "`project_id` int(11) unsigned NOT NULL,"
                + "`position` int(11) unsigned NOT NULL,"
                + "`column_name` varchar(200) COLLATE latin1_bin NOT NULL,"
                + "`column_type` varchar(45) COLLATE latin1_bin NOT NULL,"
                + "`filterable` tinyint(1) NOT NULL,"
                + "`alias` varchar(200) COLLATE latin1_bin NOT NULL,"
                + "`description` varchar(500) COLLATE latin1_bin NOT NULL,"
                + "PRIMARY KEY (`project_id`,`position`)"
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_bin;");
        
        c.createStatement().execute(
                "CREATE TABLE  `medsavantkb`.`default_patient` ("
                + "`patient_id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
                + "`family_id` varchar(100) COLLATE latin1_bin DEFAULT NULL,"
                + "`pedigree_id` varchar(100) COLLATE latin1_bin DEFAULT NULL,"
                + "`hospital_id` varchar(100) COLLATE latin1_bin DEFAULT NULL,"
                + "`dna_ids` varchar(1000) COLLATE latin1_bin DEFAULT NULL,"
                + "PRIMARY KEY (`patient_id`)"
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_bin;");
        
        c.createStatement().execute(
                "CREATE TABLE  `medsavantkb`.`default_variant` ("
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
                + "`custom_info` varchar(500) COLLATE latin1_bin DEFAULT NULL"
                + ") ENGINE=BRIGHTHOUSE DEFAULT CHARSET=latin1 COLLATE=latin1_bin;");
    }
    
    private static void addRootUser() throws SQLException {
        Connection c = ConnectionController.connect();
        c.createStatement().executeUpdate(
                "INSERT INTO `" + UserTable.TABLENAME + "` "
                + "(`name`, is_admin) VALUES ('root', 1)");
    }

    public static void main(String[] argv) throws SQLException {
        dropTables();
        createTables();
        addRootUser();
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
