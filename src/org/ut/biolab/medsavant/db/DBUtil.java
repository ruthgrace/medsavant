/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import fiume.vcf.VCFParser;
import fiume.vcf.VariantRecord;
import fiume.vcf.VariantSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ut.biolab.medsavant.db.table.CohortTableSchema;
import org.ut.biolab.medsavant.db.table.CohortViewTableSchema;
import org.ut.biolab.medsavant.db.table.GeneListMembershipTableSchema;
import org.ut.biolab.medsavant.db.table.GeneListTableSchema;
import org.ut.biolab.medsavant.db.table.ModifiableColumn;
import org.ut.biolab.medsavant.db.table.PatientTableSchema;
import org.ut.biolab.medsavant.db.table.TableSchema;
import org.ut.biolab.medsavant.db.table.TableSchema.ColumnType;
import org.ut.biolab.medsavant.db.table.VariantAnnotationGatkTableSchema;
import org.ut.biolab.medsavant.db.table.VariantAnnotationPolyphenTableSchema;
import org.ut.biolab.medsavant.db.table.VariantAnnotationSiftTableSchema;
import org.ut.biolab.medsavant.db.table.VariantTableSchema;
import org.ut.biolab.medsavant.exception.FatalDatabaseException;
import org.ut.biolab.medsavant.exception.NonFatalDatabaseException;
import org.ut.biolab.medsavant.view.dialog.ComboForm;
import org.ut.biolab.medsavant.view.dialog.ConfirmDialog;

/**
 *
 * @author mfiume, AndrewBrook
 */
public class DBUtil {

    public static List<Vector> parseResultSet(Object[][] columnsTypesIndices, ResultSet r1) throws SQLException {

        int numColumns = columnsTypesIndices.length;

        List<Vector> results = new ArrayList<Vector>();

        while (r1.next()) {

            Vector v = new Vector();

            for (int i = 0; i < numColumns; i++) {

                Integer index = (Integer) columnsTypesIndices[i][0];
                ColumnType type = (ColumnType) columnsTypesIndices[i][2];

                switch (type) {
                    case VARCHAR:
                        v.add(r1.getString(index));
                        break;
                    case BOOLEAN:
                        v.add(r1.getBoolean(index));
                        break;
                    case INTEGER:
                        v.add(r1.getInt(index));
                        break;
                    case FLOAT:
                        v.add(r1.getFloat(index));
                        break;
                    case DECIMAL:
                        v.add(r1.getDouble(index));
                        break;
                    case DATE:
                        v.add(r1.getDate(index));
                        break;
                    default:
                        throw new FatalDatabaseException("Unrecognized column type: " + type);
                }
            }

            results.add(v);
        }

        return results;

    }
    
    public static void addVcfToDb(String filename, int genome_id, int pipeline_id) throws SQLException {
        
        //establish connection
        Connection conn;
        try {
            conn = ConnectionController.connect();
        } catch (Exception ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        PreparedStatement ps = getVcfStatement(conn);
        TableSchema table = MedSavantDatabase.getInstance().getVariantTableSchema();              
        int base_variant_id = QueryUtil.getMaxValueForColumn(conn, table, table.getDBColumn(VariantTableSchema.ALIAS_VARIANTID)) + 1;        
        
        conn.setAutoCommit(false);    
        VariantSet vs = new VariantSet();
        try {
            System.out.println("Parsing variants...");
            vs = VCFParser.parseVariants(new File(filename), ps, base_variant_id, genome_id, pipeline_id, table.getColumns());
            System.out.println("Done parsing variants...");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        conn.commit();
        conn.setAutoCommit(true);   
        
        /*
        List<VariantRecord> records = vs.getRecords();
        try {
            TabixReader reader = new TabixReader("C:\\Users\\Andrew\\Documents\\medsavant\\test\\tabix_testout_100000");

            for(VariantRecord r : records){
                org.broad.tabix.TabixReader.Iterator it = reader.query(1, r.getPosition().intValue(), r.getPosition().intValue()+1);
                String s;
                while((s = it.next()) != null){

            }
            }
        } catch (IOException ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
         * 
         */
        
    }
    
    public static void addVcfToDb1(String filename, int genome_id, int pipeline_id) throws SQLException {
        
        //establish connection
        Connection conn;
        try {
            conn = ConnectionController.connect();
        } catch (Exception ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        
        //perform staging       
        clearStagingTable(conn);
        PreparedStatement ps = getVcfStatement(conn);
        TableSchema table = MedSavantDatabase.getInstance().getVariantTableSchema();              
        int base_variant_id = QueryUtil.getMaxValueForColumn(conn, table, table.getDBColumn(VariantTableSchema.ALIAS_VARIANTID)) + 1;        
        
        conn.setAutoCommit(false);    
        VariantSet vs = new VariantSet();
        try {
            System.out.println("Parsing variants...");
            vs = VCFParser.parseVariants(new File(filename), ps, base_variant_id, genome_id, pipeline_id, table.getColumns());
            System.out.println("Done parsing variants...");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        conn.commit();
        conn.setAutoCommit(true);   
        
        
        List<VariantRecord> records = vs.getRecords();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("C:\\Users\\Andrew\\Documents\\medsavant\\test\\testout")));

            String line;
            int i = 0;
            int matches = 0;
            int recPos = 0;
            VariantRecord r = records.get(recPos);
                    
            long start = System.nanoTime();
            while((line = bufferedReader.readLine()) != null){               
                
                int i1 = line.indexOf("\t");
                int i2 = line.indexOf("\t", i1+1);
                int i3 = line.indexOf("\t", i2+1);
                
                String chr = line.substring(i1+1, i2);
                long pos = Long.parseLong(line.substring(i2+1, i3));
                
                int compare = r.compareTo(chr, pos);
                while(compare < 0){
                    recPos++;
                    if(recPos >= records.size()) break;
                    r = records.get(recPos);
                    compare = r.compareTo(chr, pos);
                }
                if(recPos >= records.size()) break;
                if (compare == 0){
                    //System.out.println("MATCH FOUND: " + chr + " " + pos + "\t\t" + r.getChrom() + " " + r.getPosition());
                    r.setTranscriptStrand("XXX");
                    matches++;
                }

                i++;
                if(i % 10000000 == 0){
                    System.out.println(i + " records passed");
                }
            }
            long end = System.nanoTime();
            System.out.println("total time: " + (end - start));
            System.out.println("total matches: " + matches);

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
        
        if(true){
            return;
        }
        
        
        
        
        //delete existing file
        
        String dateFormat = "yyyyMMddhhmmss";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String dateString = sdf.format(cal.getTime());
        String fileName = "/data/medsavant/stagingFile_" + dateString;
        //(new File(fileName)).delete();
        
        
        System.out.println("A");
        String q1 = 
                "select * " +
                "into outfile '" + filename + "a" + "' " +
                "fields terminated by '\t' " +
                "lines terminated by '\n' " +
                "from variant_staging";
        Statement s1 = conn.createStatement();
        s1.execute(q1);
        
        System.out.println("B");      
        String q2 = 
                "load data infile '" + filename + "a" + "' " +
                "into table variant_staging_ib " +
                "fields terminated by '\t'";
        Statement s2 = conn.createStatement();
        s2.execute(q2);
        
        /*if(true){
            return;
        }*/
        
        
        //join with annotations -> outfile
        
        System.out.println("starting join");
        
        VariantAnnotationSiftTableSchema sift = MedSavantDatabase.getInstance().getVariantSiftTableSchema();
        VariantAnnotationPolyphenTableSchema polyphen = MedSavantDatabase.getInstance().getVariantPolyphenTableSchema();
        VariantAnnotationGatkTableSchema gatk = MedSavantDatabase.getInstance().getVariantGatkTableSchema();
        
        String dumpString = 
                "SELECT " 
                //variant
                + "t0.*, ";
        
        //sift
        for(Object[] column : sift.getJoinedColumnGrid()){
            dumpString += "t1.`" + ((DbColumn)column[1]).getColumnNameSQL() + "`, ";
        }
        
        //polyphen
        for(Object[] column : polyphen.getJoinedColumnGrid()){
            dumpString += "t2.`" + ((DbColumn)column[1]).getColumnNameSQL() + "`, ";
        }
        
        //gatk
        int i = gatk.getJoinedColumnGrid().length;
        for(Object[] column : gatk.getJoinedColumnGrid()){
            dumpString += "t3.`" + ((DbColumn)column[1]).getColumnNameSQL() + "`";
            if(i != 1){
                dumpString += ",";
            }
            dumpString += " ";
            i--;
        }
        
        dumpString += 
                "INTO OUTFILE '" + fileName + "' "
                + "FIELDS TERMINATED BY '\\t' "
                + "FROM ( select * from variant_staging_ib ) as t0 "       
                //sift
                + "LEFT OUTER JOIN ( select * from " + VariantAnnotationSiftTableSchema.TABLE_NAME + " group by chrom) as t1 "
                + "ON "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_GENOMEID + "`=t1.`" + VariantAnnotationSiftTableSchema.DBFIELDNAME_GENOMEID + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_CHROM + "`=t1.`" + VariantAnnotationSiftTableSchema.DBFIELDNAME_CHROM + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_POSITION + "`=t1.`" + VariantAnnotationSiftTableSchema.DBFIELDNAME_POSITION + "` "
                //polyphen
                + "LEFT OUTER JOIN ( select * from " + VariantAnnotationPolyphenTableSchema.TABLE_NAME + " group by chrom) as t1 "
                + "ON "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_GENOMEID + "`=t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_GENOMEID + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_CHROM + "`=t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_CHROM + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_POSITION + "`=t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_POSITION + "` "
                //gatk
                + "LEFT OUTER JOIN ( select * from " + VariantAnnotationGatkTableSchema.TABLE_NAME + " group by chrom) as t1 "
                + "ON "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_GENOMEID + "`=t3.`" + VariantAnnotationGatkTableSchema.DBFIELDNAME_GENOMEID + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_CHROM + "`=t3.`" + VariantAnnotationGatkTableSchema.DBFIELDNAME_CHROM + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_POSITION + "`=t3.`" + VariantAnnotationGatkTableSchema.DBFIELDNAME_POSITION + "`";
                
        
        /*dumpString +=
                "INTO OUTFILE '" + fileName + "' "
                + "FIELDS TERMINATED BY '\\t' "
                + "FROM variant_staging t0 " 
                //sift
                + "LEFT OUTER JOIN " + VariantAnnotationSiftTableSchema.TABLE_NAME + " t1 ON ("
                + "t0.`" + VariantTableSchema.DBFIELDNAME_GENOMEID + "`=t1.`" + VariantAnnotationSiftTableSchema.DBFIELDNAME_GENOMEID + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_CHROM + "`=t1.`" + VariantAnnotationSiftTableSchema.DBFIELDNAME_CHROM + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_POSITION + "`=t1.`" + VariantAnnotationSiftTableSchema.DBFIELDNAME_POSITION + "`) "
                //polyphen
                + "LEFT OUTER JOIN " + VariantAnnotationPolyphenTableSchema.TABLE_NAME + " t2 ON ("
                + "t0.`" + VariantTableSchema.DBFIELDNAME_GENOMEID + "`=t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_GENOMEID + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_CHROM + "`=t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_CHROM + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_POSITION + "`=t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_POSITION + "`) "
                //gatk
                + "LEFT OUTER JOIN " + VariantAnnotationGatkTableSchema.TABLE_NAME + " t3 ON ("
                + "t0.`" + VariantTableSchema.DBFIELDNAME_GENOMEID + "`=t3.`" + VariantAnnotationGatkTableSchema.DBFIELDNAME_GENOMEID + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_CHROM + "`=t3.`" + VariantAnnotationGatkTableSchema.DBFIELDNAME_CHROM + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_POSITION + "`=t3.`" + VariantAnnotationGatkTableSchema.DBFIELDNAME_POSITION + "`)";*/
                
        /*String dumpString = 
                "SELECT " 
                //variant
                + "t0.*, "
                //sift
                + "t1.`" + VariantAnnotationSiftTableSchema.DBFIELDNAME_NAME_SIFT
                + "`, t1.`" + VariantAnnotationSiftTableSchema.DBFIELDNAME_NAME2_SIFT
                + "`, t1.`" + VariantAnnotationSiftTableSchema.DBFIELDNAME_DAMAGEPROBABILITY
                //polyphen
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_CDNACOORD
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_OPOS
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_OAA1
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_OAA2
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_SNPID
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_ACC
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_POS
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_PREDICTION
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_PPH2CLASS
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_PPH2PROB
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_PPH2FPR
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_PPH2TPR
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_PPH2FDR
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_TRANSV
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_CODPOS
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_CPG
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_MINDJNC
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_IDPMAX
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_IDPSNP
                + "`, t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_IDQMIN
                + "` "
                + "INTO OUTFILE '" + fileName + "' "
                + "FIELDS TERMINATED BY '\\t' "
                + "FROM variant_staging t0 " 
                //sift
                + "LEFT OUTER JOIN " + VariantAnnotationSiftTableSchema.TABLE_NAME + " t1 ON ("
                + "t0.`" + VariantTableSchema.DBFIELDNAME_GENOMEID + "`=t1.`" + VariantAnnotationSiftTableSchema.DBFIELDNAME_GENOMEID + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_CHROM + "`=t1.`" + VariantAnnotationSiftTableSchema.DBFIELDNAME_CHROM + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_POSITION + "`=t1.`" + VariantAnnotationSiftTableSchema.DBFIELDNAME_POSITION + "`)"
                //polyphen
                + "LEFT OUTER JOIN " + VariantAnnotationPolyphenTableSchema.TABLE_NAME + " t2 ON ("
                + "t0.`" + VariantTableSchema.DBFIELDNAME_GENOMEID + "`=t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_GENOMEID + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_CHROM + "`=t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_CHROM + "` AND "
                + "t0.`" + VariantTableSchema.DBFIELDNAME_POSITION + "`=t2.`" + VariantAnnotationPolyphenTableSchema.DBFIELDNAME_POSITION + "`)";*/
        Statement s = conn.createStatement();
        s.execute(dumpString);
        
        
        //load outfile to variants
        
        System.out.println("starting load");
        String loadString = 
                "LOAD DATA INFILE '" + fileName + "' " +
                "INTO TABLE " + "variant_combined_ib" + " " + 
                "FIELDS TERMINATED BY '\\t'";
        s = conn.createStatement();
        s.execute(loadString);
        
        
        
    } 
    
    public static PreparedStatement getVcfStatement(Connection conn) throws SQLException{
        
        TableSchema table = MedSavantDatabase.getInstance().getVariantTableSchema();        
        InsertQuery is = new InsertQuery(MedSavantDatabase.getInstance().getVariantTableSchema().getTable());

        List<DbColumn> columns = table.getColumns();
        for(DbColumn col : columns){
            is.addPreparedColumns(col);   
        }

        String insertString = is.toString().replaceAll(VariantTableSchema.TABLE_NAME, "variant_staging"); //TODO: safe?
        PreparedStatement ps = conn.prepareStatement(insertString);
        return ps;
    }
    
    public static void clearStagingTable(Connection conn) throws SQLException {
        
        TableSchema table = MedSavantDatabase.getInstance().getVariantTableSchema();        
        DeleteQuery d = new DeleteQuery(table.getTable());
        d.addCondition(BinaryCondition.equalTo(1, 1));
        
        Statement s = conn.createStatement();
        s.executeUpdate(d.toString().replaceAll(VariantTableSchema.TABLE_NAME, "variant_staging")); 
        
        //create new variant_staging_ib table
        Statement s1 = conn.createStatement();
        s1.execute("drop table variant_staging_ib");
        
        Statement s2 = conn.createStatement();
        s2.execute(
            "CREATE TABLE  `medsavantdb`.`variant_staging_ib` (" +
              "`variant_id` int(11) NOT NULL," +
              "`genome_id` int(11) NOT NULL," +
              "`pipeline_id` varchar(10) COLLATE latin1_bin NOT NULL," +
              "`dna_id` varchar(10) COLLATE latin1_bin NOT NULL," +
              "`chrom` varchar(5) COLLATE latin1_bin NOT NULL DEFAULT ''," +
              "`position` int(11) NOT NULL," +
              "`dbsnp_id` varchar(45) COLLATE latin1_bin DEFAULT NULL," +
              "`ref` varchar(30) COLLATE latin1_bin DEFAULT NULL," +
              "`alt` varchar(30) COLLATE latin1_bin DEFAULT NULL," +
              "`qual` float DEFAULT NULL," +
              "`filter` varchar(500) COLLATE latin1_bin DEFAULT NULL," +
              "`aa` varchar(500) COLLATE latin1_bin DEFAULT NULL," +
              "`ac` varchar(500) COLLATE latin1_bin DEFAULT NULL," +
              "`af` varchar(500) COLLATE latin1_bin DEFAULT NULL," +
              "`an` int(11) DEFAULT NULL," +
              "`bq` float DEFAULT NULL," +
              "`cigar` varchar(500) COLLATE latin1_bin DEFAULT NULL," +
              "`db` tinyint(1) DEFAULT NULL," +
              "`dp` int(11) DEFAULT NULL," +
              "`end` varchar(500) COLLATE latin1_bin DEFAULT NULL," +
              "`h2` tinyint(1) DEFAULT NULL," +
              "`mq` varchar(500) COLLATE latin1_bin DEFAULT NULL," +
              "`mq0` varchar(500) COLLATE latin1_bin DEFAULT NULL," +
              "`ns` int(11) DEFAULT NULL," +
              "`sb` varchar(500) COLLATE latin1_bin DEFAULT NULL," +
              "`somatic` tinyint(1) DEFAULT NULL," +
              "`validated` tinyint(1) DEFAULT NULL," +
              "`custom_info` varchar(500) COLLATE latin1_bin DEFAULT NULL" +
            ") ENGINE=BRIGHTHOUSE DEFAULT CHARSET=latin1 COLLATE=latin1_bin;");
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    /*
     * Given path to vcf file, add to database.
     * Return true iff success.
     */
   /* public static void addVcfToDb(String filename, int genome_id, int pipeline_id) throws SQLException {

        //establish connection
        Connection conn;
        try {
            conn = ConnectionController.connect();
        } catch (Exception ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        //create current variant table
       // String tableName = createVariantTable(conn);
        
        TableSchema table = MedSavantDatabase.getInstance().getVariantTableSchema(); 
        int variant_id = QueryUtil.getMaxValueForColumn(conn, table, table.getDBColumn(VariantTableSchema.ALIAS_VARIANTID)) + 1; 
        
        //get variants from file
        //VariantSet variants = new VariantSet();
        //String fileName = "C:/Users/Andrew/Documents/medsavant/variant_tdf"; //TODO: how do we do this when db is not local???
        //File tempFile = new File(fileName);
        try {
            System.out.println("Parsing variants...");
            VCFParser.parseVariants(new File(filename));
            System.out.println("Done parsing variants...");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //add to db
        //addVariantsToDb(conn, fileName);
        //addVariantsToDb(variants, genome_id, pipeline_id, conn);
        //FilterController.fireFiltersChangedEvent();
    }*/
    
   /* private static void addVariantsToDb(Connection conn, String fileName){
        
        String query = 
                "LOAD DATA INFILE '" + fileName + "' " + 
                "INTO TABLE " + VariantTableSchema.TABLE_NAME + " " + 
                "FIELDS TERMINATED BY '\\t'";
        
        try {
            Statement s = conn.createStatement();
            s.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }*/
    
   /* private static String createVariantTable(Connection conn){
        
        String baseName = VariantTableSchema.TABLE_NAME;
        String tableName = baseName + "_";

        String query = 
                "CREATE TABLE " + tableName + " AS " +
                "(SELECT * FROM " + baseName + " WHERE 1=2)";
        
        try {
            Statement s = conn.createStatement();
            //System.out.println(query);
            s.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        TableSchema vcf = MedSavantDatabase.getInstance().getVCFUploadTableSchema();
        InsertQuery iq = new InsertQuery(vcf.getTable());
        iq.addColumn(vcf.getDBColumn(VCFUploadTableSchema.ALIAS_TABLENAME), tableName);
        
        try {
            Statement s = conn.createStatement();
            //System.out.println(iq.toString());
            s.execute(iq.toString());
        } catch (SQLException ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return tableName;
    }*/

    /*private static void addVariantsToDb(VariantSet variants, int genome_id, int pipeline_id, Connection conn) throws SQLException {

        
        
        TableSchema table = MedSavantDatabase.getInstance().getVariantTableSchema();        
        InsertQuery is = new InsertQuery(MedSavantDatabase.getInstance().getVariantTableSchema().getTable());

        List<DbColumn> columns = table.getColumns();
        for(DbColumn col : columns){
            is.addPreparedColumns(col);   
        }

        String insertString = is.toString().replace(VariantTableSchema.TABLE_NAME, "variant_staging");
        PreparedStatement ps = conn.prepareStatement(insertString);
        
        conn.setAutoCommit(false);
        
        int numRecords = 0;
        
        System.out.println("Preparing " + variants.getRecords().size() + " records ...");
        
        
        int variant_id = QueryUtil.getMaxValueForColumn(conn, table, table.getDBColumn(VariantTableSchema.ALIAS_VARIANTID)) + 1; 
        
        //add records
        for (VariantRecord record : variants.getRecords()) {

            numRecords++;
            if (numRecords % 10000 == 0) {
                System.out.println("Prepared " + numRecords + " records");
            }
            
            for(int i = 0; i < columns.size(); i++){
                DbColumn col = columns.get(i);
                switch(VariantTableSchema.FIELD_NAMES.valueOf(col.getColumnNameSQL().toUpperCase())){
                    case VARIANT_ID:
                        ps.setInt(i+1, variant_id);
                        break;
                    case GENOME_ID:
                        ps.setInt(i+1, genome_id);
                        break;
                    case PIPELINE_ID:
                        ps.setInt(i+1, pipeline_id);
                        break;
                    case DNA_ID:
                        ps.setString(i+1, record.getDnaID());
                        break;
                    case CHROM:
                        ps.setString(i+1, record.getChrom());
                        break;
                    case POSITION:
                        ps.setLong(i+1, record.getPos());
                        break;
                    case DBSNP_ID:
                        ps.setString(i+1, record.getDbSNPID());
                        break;
                    case REF:
                        ps.setString(i+1, record.getRef());
                        break;
                    case ALT:
                        ps.setString(i+1, record.getAlt());
                        break;
                    case QUAL:
                        ps.setFloat(i+1, record.getQual());
                        break;
                    case FILTER:
                        ps.setString(i+1, record.getFilter());
                        break;
                    case AA:
                        ps.setString(i+1, record.getAA());
                        break;
                    case AC:
                        ps.setString(i+1, record.getAC());
                        break;
                    case AF:
                        ps.setString(i+1, record.getAF());
                        break;
                    case AN:
                        ps.setInt(i+1, record.getAN());
                        break;
                    case BQ:
                        ps.setFloat(i+1, record.getBQ());
                        break;
                    case CIGAR:
                        ps.setString(i+1, record.getCigar());
                        break;
                    case DB:
                        ps.setBoolean(i+1, record.getDB());
                        break;
                    case DP:
                        ps.setInt(i+1, record.getDP());
                        break;
                    case END:
                        ps.setLong(i+1, record.getEnd());
                        break;
                    case H2:
                        ps.setBoolean(i+1, record.getH2());
                        break;
                    case MQ:
                        ps.setFloat(i+1, record.getMQ());
                        break;
                    case MQ0:
                        ps.setInt(i+1, record.getMQ0());
                        break;
                    case NS:
                        ps.setInt(i+1, record.getNS());
                        break;
                    case SB:
                        ps.setFloat(i+1, record.getSB());
                        break;
                    case SOMATIC:
                        ps.setBoolean(i+1, record.getSomatic());
                        break;
                    case VALIDATED:
                        ps.setBoolean(i+1, record.getValidated());
                        break;
                    case CUSTOM_INFO:
                        ps.setString(i+1, record.getCustomInfo());
                        break;
                    case VARIANT_ANNOTATION_SIFT_ID:
                        ps.setInt(i+1, 0);
                        break;
                    default:
                        break;
                }              
            }
            variant_id++;
            ps.executeUpdate();
            
        }
        
        conn.commit();
        conn.setAutoCommit(true);
        
    }*/

    public static void addIndividualsToCohort(String[] patient_ids) {

        HashMap<String, Integer> cohortMap = new HashMap<String, Integer>();

        Connection conn;
        try {
            conn = ConnectionController.connect();
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM cohort");
            while (rs.next()) {
                cohortMap.put(rs.getString(2), rs.getInt(1));
            }

        } catch (Exception ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
            return; //TODO
        }

        Object[] options = cohortMap.keySet().toArray();
        ComboForm form = new ComboForm(options, "Select Cohort", "Select which cohort to add to:");
        String selected = (String) form.getSelectedValue();
        if (selected == null) {
            return;
        }
        int cohort_id = cohortMap.get(selected);

        try {
            String sql = "INSERT INTO cohort_membership ("
                    + "cohort_id,"
                    + "hospital_id) "
                    + "VALUES (?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            conn.setAutoCommit(false);

            for (String patient_id : patient_ids) {
                pstmt.setInt(1, cohort_id);
                pstmt.setString(2, patient_id);

                pstmt.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void removeIndividualsFromCohort(String cohort_name, String[] patient_ids) {
        try {
            Connection conn = ConnectionController.connect();

            String sql1 = "SELECT cohort_id FROM cohort WHERE name=\"" + cohort_name + "\"";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql1);
            int cohort_id = -1;
            if (rs.next()) {
                cohort_id = rs.getInt(1);
            } else {
                return;
            }

            String sql2 = "DELETE FROM cohort_membership "
                    + "WHERE cohort_id=? AND hospital_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql2);
            conn.setAutoCommit(false);

            for (String patient_id : patient_ids) {
                pstmt.setInt(1, cohort_id);
                pstmt.setString(2, patient_id);

                pstmt.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);

        } catch (Exception ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void deleteIndividuals(String[] patient_ids) {

        String message = "Do you really want to delete these individuals?";
        if (patient_ids.length == 1) {
            message = "Do you really want to delete " + patient_ids[0] + "?";
        }

        ConfirmDialog cd = new ConfirmDialog("Confirm delete", message);
        boolean confirmed = cd.isConfirmed();
        cd.dispose();
        if (!confirmed) {
            return;
        }


        try {
            Connection conn = ConnectionController.connect();
            
            String sql1 = "DELETE FROM " + PatientTableSchema.TABLE_NAME
                    + " WHERE " + PatientTableSchema.DBFIELDNAME_PATIENTID + "=?";
            PreparedStatement pstmt1 = conn.prepareStatement(sql1);

            String sql2 = "DELETE FROM " + CohortViewTableSchema.TABLE_NAME
                    + " WHERE " + CohortViewTableSchema.DBFIELDNAME_HOSPITALID + "=?";
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);

            conn.setAutoCommit(false);

            for (String patient_id : patient_ids) {
                pstmt1.setString(1, patient_id);
                pstmt1.executeUpdate();

                pstmt2.setString(1, patient_id);
                pstmt2.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);

        } catch (Exception ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void addPatient(List<ModifiableColumn> cols, List<String> values){
        //TODO: make sure row doesn't already exist.
        TableSchema t = MedSavantDatabase.getInstance().getPatientTableSchema();
        InsertQuery is = new InsertQuery(t.getTable());
        
        for(int i = 0; i < cols.size(); i++){
            ModifiableColumn c = cols.get(i);
            String s = values.get(i);          
            if(s == null || s.equals("")){
                continue;
            }
            switch(c.getType()){
                case BOOLEAN:
                    is.addColumn(t.getDBColumn(c.getShortName()), Boolean.getBoolean(s));
                    break;
                case DATE:
                    is.addColumn(t.getDBColumn(c.getShortName()), Date.valueOf(s));
                    break;
                case DECIMAL:
                    is.addColumn(t.getDBColumn(c.getShortName()), Double.parseDouble(s));
                    break;
                case FLOAT:
                    is.addColumn(t.getDBColumn(c.getShortName()), Float.parseFloat(s));
                    break;
                case INTEGER:
                    is.addColumn(t.getDBColumn(c.getShortName()), Integer.parseInt(s));
                    break;
                case VARCHAR:
                    is.addColumn(t.getDBColumn(c.getShortName()), s);
                    break;                
            }
        }
        
        try {
            Statement s = ConnectionController.connect().createStatement();
            s.executeUpdate(is.toString());
        } catch (NonFatalDatabaseException ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    public static void addCohort(String cohort_name) {
        //TODO: make sure name doesn't already exist.
        TableSchema t = MedSavantDatabase.getInstance().getCohortTableSchema();
        InsertQuery is = new InsertQuery(t.getTable());
        is.addColumn(t.getDBColumn(CohortTableSchema.ALIAS_COHORTNAME), cohort_name);        
        try {
            Statement s = ConnectionController.connect().createStatement();
            s.executeUpdate(is.toString());
        } catch (NonFatalDatabaseException ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void deleteCohorts(String[] cohort_names) {

        String message = "Do you really want to delete these cohorts?";
        if (cohort_names.length == 1) {
            message = "Do you really want to delete " + cohort_names[0] + "?";
        }

        ConfirmDialog cd = new ConfirmDialog("Confirm delete", message);
        boolean confirmed = cd.isConfirmed();
        cd.dispose();
        if (!confirmed) {
            return;
        }


        try {
            Connection conn = ConnectionController.connect();

            String sql1 = "DELETE FROM cohort "
                    + "WHERE name=?";
            PreparedStatement pstmt1 = conn.prepareStatement(sql1);

            conn.setAutoCommit(false);

            for (String cohort_name : cohort_names) {
                pstmt1.setString(1, cohort_name);
                pstmt1.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);

        } catch (Exception ex) {
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void addGeneListToDatabase(String geneListName, Iterator<String[]> i) throws NonFatalDatabaseException, SQLException {

        Connection conn = ConnectionController.connect();

        // create gene list
        TableSchema geneListTable = MedSavantDatabase.getInstance().getGeneListTableSchema();
        InsertQuery q0 = new InsertQuery(geneListTable.getTable());

        q0.addColumn(geneListTable.getDBColumn(GeneListTableSchema.ALIAS_NAME), geneListName);

        Statement s0 = conn.createStatement();

        System.out.println("Inserting: " + q0.toString());

        s0.executeUpdate(q0.toString());

        System.out.println("Done executing statement");

        SelectQuery q1 = new SelectQuery();

        q1.addFromTable(geneListTable.getTable());
        q1.addAllColumns();
        q1.addCondition(new BinaryCondition(BinaryCondition.Op.EQUAL_TO,
                geneListTable.getDBColumn(GeneListTableSchema.ALIAS_NAME),
                geneListName));

        Statement s1 = conn.createStatement();

        //System.out.println("Querying for: " + q1.toString());

        ResultSet r1 = s1.executeQuery(q1.toString());
        r1.next();

        int genelistid = r1.getInt(GeneListTableSchema.DBFIELDNAME_ID);

        //System.out.println("Gene list id = " + genelistid);

        conn.setAutoCommit(false);

        InsertQuery q2;
        Statement s2;

        TableSchema glmembership = MedSavantDatabase.getInstance().getGeneListMembershipTableSchema();

        while (i.hasNext()) {

                    System.out.println("Sending region member");

            String[] line = i.next();

            q2 = new InsertQuery(glmembership.getTable());
            q2.addColumn(glmembership.getDBColumn(GeneListMembershipTableSchema.ALIAS_REGIONSETID), genelistid);
            //TODO: dont hard code! Get from the user!!
            q2.addColumn(glmembership.getDBColumn(GeneListMembershipTableSchema.ALIAS_GENOMEID), 1);
            q2.addColumn(glmembership.getDBColumn(GeneListMembershipTableSchema.ALIAS_CHROM), line[0]);
            q2.addColumn(glmembership.getDBColumn(GeneListMembershipTableSchema.ALIAS_START), line[1]);
            q2.addColumn(glmembership.getDBColumn(GeneListMembershipTableSchema.ALIAS_END), line[2]);
            q2.addColumn(glmembership.getDBColumn(GeneListMembershipTableSchema.ALIAS_DESCRIPTION), line[3]);

            s2 = conn.createStatement();

            //System.out.println("Inserting: " + q2.toString());
            s2.executeUpdate(q2.toString());
        }

        conn.commit();
        conn.setAutoCommit(true);

        /**
         * TODO: all this!
         */
        //SelectQuery q = new SelectQuery();
            /*
        q.addFromTable(t.getTable());
        q.addCustomColumns(FunctionCall.min().addColumnParams(col));
        q.addCustomColumns(FunctionCall.max().addColumnParams(col));
        
        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery(q.toString());
        
        
        
        
        
        
        
        
        String sql = "INSERT INTO cohort_membership ("
        + "cohort_id,"
        + "hospital_id) "
        + "VALUES (?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        conn.setAutoCommit(false);
        
        for(String patient_id : patient_ids){       
        pstmt.setInt(1, cohort_id);
        pstmt.setString(2, patient_id);
        
        pstmt.executeUpdate();
        }
        
        conn.commit();
        conn.setAutoCommit(true);
        
        } catch (SQLException ex) {
        Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // put genes into gene list
         * 
         */
    }
    
    public static void executeUpdate(String sql) throws SQLException, NonFatalDatabaseException {
        Connection conn = ConnectionController.connect();
        Statement s = conn.createStatement();
        s.executeUpdate(sql);
    }
}
