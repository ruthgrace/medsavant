package org.ut.biolab.medsavant.db.util.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ut.biolab.medsavant.db.table.AnnotationTable;
import org.ut.biolab.medsavant.db.table.ReferenceTable;
import org.ut.biolab.medsavant.db.table.VariantMapTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;

/**
 *
 * @author mfiume
 */
public class ReferenceQueryUtil { 
    
    public static List<String> getReferenceNames() throws SQLException {
        
        Connection conn = ConnectionController.connect();
        
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT " + ReferenceTable.FIELDNAME_NAME
                + " FROM " + ReferenceTable.TABLENAME);
        
        List<String> results = new ArrayList<String>();
        
        while (rs.next()) {
            results.add(rs.getString(1));
        }
        
        return results;
    }

    public static int getReferenceId(String refName) throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery(
                "SELECT " + ReferenceTable.FIELDNAME_ID 
                + " FROM `" + ReferenceTable.TABLENAME + "`" 
                + " WHERE " + ReferenceTable.FIELDNAME_NAME + "=\"" + refName + "\"");
        
        if (rs1.next()) {
            return rs1.getInt(1);
        } else {
            return -1;
        }
    }

     public static boolean containsReference(String name) throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery(
                "SELECT *"
                + " FROM `" + ReferenceTable.TABLENAME + "`"
                + " WHERE " + ReferenceTable.FIELDNAME_NAME + "=\"" + name + "\"");
        
        return rs1.next();
    }
    
     
     public static int addReference(String name) throws SQLException {

        String q = "INSERT INTO " + ReferenceTable.TABLENAME + " VALUES (null,'" + name + "')";
        PreparedStatement stmt = (ConnectionController.connect(DBSettings.DBNAME)).prepareStatement(q,
                Statement.RETURN_GENERATED_KEYS);

        stmt.execute();
        ResultSet res = stmt.getGeneratedKeys();
        res.next();

        int refid = res.getInt(1);
        
        return refid;
    }
     
     public static boolean removeReference(int refid) throws SQLException {
         
         Connection c = ConnectionController.connect();
         
         ResultSet rs = c.createStatement().executeQuery(
                 "SELECT *"
                 + " FROM " + AnnotationTable.TABLENAME 
                 + " WHERE " + AnnotationTable.FIELDNAME_REFERENCEID + "=" + refid);
         if (rs.next()) { return false; }
         rs = c.createStatement().executeQuery(
                 "SELECT *"
                 + " FROM " + VariantMapTable.TABLENAME 
                 + " WHERE " + VariantMapTable.FIELDNAME_REFERENCEID + "=" + refid);
         if (rs.next()) { return false; }
         
         c.createStatement().execute("DELETE FROM `" + ReferenceTable.TABLENAME + "` WHERE reference_id=" + refid);
         
         return true;
    }  
     
    public static List<String> getReferencesForProject(int projectid) throws SQLException {
        
        ResultSet rs = org.ut.biolab.medsavant.db.util.ConnectionController.connect().createStatement().executeQuery(
                "SELECT " + ReferenceTable.TABLENAME + "." + ReferenceTable.FIELDNAME_NAME 
                + " FROM " + VariantMapTable.TABLENAME
                + " LEFT JOIN " + ReferenceTable.TABLENAME + " ON "
                + VariantMapTable.TABLENAME + "." + VariantMapTable.FIELDNAME_REFERENCEID + "=" + ReferenceTable.TABLENAME + "." + ReferenceTable.FIELDNAME_ID
                + " WHERE " + VariantMapTable.FIELDNAME_PROJECTID + "=" + projectid + ";");
        
        List<String> references = new ArrayList<String>();
        while (rs.next()) {
            references.add(rs.getString(1));
        }
        
        return references;
    }
    
    
    public static Map<Integer, String> getReferencesWithoutTablesInProject(int projectid) throws SQLException {
        
        Connection c = org.ut.biolab.medsavant.db.util.ConnectionController.connect();
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT *"
                + " FROM " + ReferenceTable.TABLENAME
                + " WHERE " + ReferenceTable.FIELDNAME_ID + " NOT IN"
                + " (SELECT " + VariantMapTable.FIELDNAME_REFERENCEID + " FROM " + VariantMapTable.TABLENAME
                + " WHERE " + VariantMapTable.FIELDNAME_PROJECTID + "=" + projectid + ")");
        
        HashMap<Integer,String> result = new HashMap<Integer,String>();
        
        while (rs.next()) {
            result.put(rs.getInt(1), rs.getString(2));
        }
        
        return result;
        
    }

}
