package org.ut.biolab.medsavant.db.util;

import org.ut.biolab.medsavant.db.util.ConnectionController;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;

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
                
        if (fpos == -1) { return -1; }
        else { 
            return Integer.parseInt(s.substring(fpos+1,rpos)); 
        }
    }
    
    
    public static DbTable importTable(String dbname, String tablename) throws SQLException {
        
        Connection c;
        try {
            c = ConnectionController.connect(dbname);
        } catch (Exception ex) {
            System.err.println("error: could not connection to db");
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
        
        for (DbColumn col : table.getColumns()) {
            System.out.println(col);
            System.out.println("\t" + col.getTypeNameSQL());
            System.out.println("\t" + col.getTypeLength());
        }
        
        return table;
    }
    
    public static void dropTable(String tablename) throws SQLException {
        Connection c = (ConnectionController.connect(DBSettings.DBNAME));

        c.createStatement().execute(
                "DROP TABLE IF EXISTS " + tablename + ";");
    }

    /*
    private static void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int numberOfColumns = rsMetaData.getColumnCount();

        System.out.println("Number of columns: " + numberOfColumns);
        
        //System.out.println(rsMetaData);
        for (int i = 1; i <= numberOfColumns; i++) {
            //System.out.println(i);
            System.out.print("=" + rsMetaData.getColumnName(i) + "=\t");
        }
        System.out.println();
        for (int i = 1; i <= numberOfColumns; i++) {
            System.out.print("[" + rsMetaData.getColumnTypeName(i) + "]\t");
        }
        System.out.println();
        
        while (rs.next()) {
            for (int i = 1; i <= numberOfColumns; i++) {
                System.out.format("%s\t", rs.getString(i));
            }
            System.out.println();
        }
        
    }
     * 
     */

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


    public static boolean containsReference(String referenceName) throws SQLException {
        Connection c = ConnectionController.connect(DBSettings.DBNAME);
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT * FROM `" + DBSettings.TABLENAME_REFERENCE + "` WHERE name=\"" + referenceName + "\"");
        
        return rs1.next();
    }
    
}
