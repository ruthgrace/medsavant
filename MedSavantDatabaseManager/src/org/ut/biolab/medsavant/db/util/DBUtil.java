package org.ut.biolab.medsavant.db.util;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author mfiume
 */
public class DBUtil {

    public static String getColumnType(String s) {
        int pos = s.indexOf("(");
        if (pos == -1) { return s; }
        else { return s.substring(0,pos); }
    }
    
    public static int getColumnLength(String s) {
        
        int fpos = s.indexOf("(");
        int rpos = s.indexOf(")");
        int cpos = s.indexOf(",");
        if(cpos != -1 && cpos < rpos){
            rpos = cpos;
        }
        
        if (fpos == -1) { return -1; }
        else { 
            return Integer.parseInt(s.substring(fpos+1,rpos)); 
        }
    }
    
    public static DbTable importTable(String tablename) throws SQLException {
        return importTable(DBSettings.DBNAME, tablename);
    }
    
    public static DbTable importTable(String dbname, String tablename) throws SQLException {
        
        Connection c;
        try {
            c = ConnectionController.connect(dbname);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        
        DbSpec spec = new DbSpec();
        DbSchema schema = spec.addDefaultSchema();
        
        DbTable table = schema.addTable(tablename);
        
        Statement s = c.createStatement ();
        ResultSet rs = s.executeQuery("DESCRIBE " + tablename);

        ResultSetMetaData rsMetaData = rs.getMetaData();
        int numberOfColumns = rsMetaData.getColumnCount();

        while (rs.next()) {
            table.addColumn(rs.getString(1), getColumnType(rs.getString(2)), getColumnLength(rs.getString(2)));
        }

        return table;
    }
    
    public static void dropTable(String tablename) throws SQLException {
        Connection c = (ConnectionController.connect(DBSettings.DBNAME));

        c.createStatement().execute(
                "DROP TABLE IF EXISTS " + tablename + ";");
    }

    public static boolean tableExists(String dbname, String tablename) throws SQLException {
        Statement s = ConnectionController.connect(dbname).createStatement();
        
        ResultSet rs = s.executeQuery("SHOW TABLES");
        
        while(rs.next()) {
            if (rs.getString(1).equals(tablename)) {
                return true;
            }
        }
        
        return false;
    }

    public static int getNumRecordsInTable(String tablename) {
        try {
            Connection c = ConnectionController.connect(DBSettings.DBNAME);
            ResultSet rs =  c.createStatement().executeQuery("SELECT COUNT(*) FROM `" + tablename + "`");
            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public static Timestamp getCurrentTimestamp(){
        return new Timestamp((new Date()).getTime());
    }
}
