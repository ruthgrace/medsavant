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
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;

/**
 *
 * @author mfiume
 */
public class ReferenceQueryUtil { 
    
    public static List<String> getReferenceNames() throws SQLException {
        
        Connection conn = ConnectionController.connect();
        
        ResultSet rs = conn.createStatement().executeQuery("SELECT name FROM " + org.ut.biolab.medsavant.db.util.DBSettings.TABLENAME_REFERENCE);
        
        List<String> results = new ArrayList<String>();
        
        while (rs.next()) {
            results.add(rs.getString(1));
        }
        
        return results;
    }

    public static int getReferenceId(String refName) throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT reference_id FROM `" + DBSettings.TABLENAME_REFERENCE + "` WHERE name=\"" + refName + "\"");
        
        if (rs1.next()) {
            return rs1.getInt(1);
        } else {
            return -1;
        }
    }

     public static boolean containsReference(String name) throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT * FROM `" + DBSettings.TABLENAME_REFERENCE + "` WHERE name=\"" + name + "\"");
        
        return rs1.next();
    }
    
     
     public static int addReference(String name) throws SQLException {

        String q = "INSERT INTO " + DBSettings.TABLENAME_REFERENCE + " VALUES (null,'" + name + "')";
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
         
         ResultSet rs = c.createStatement().executeQuery("SELECT * FROM " + DBSettings.TABLENAME_ANNOTATION + " WHERE reference_id=" + refid);
         if (rs.next()) { return false; }
         rs = c.createStatement().executeQuery("SELECT * FROM " + DBSettings.TABLENAME_VARIANTTABLEINFO + " WHERE reference_id=" + refid);
         if (rs.next()) { return false; }
         
         c.createStatement().execute("DELETE FROM `" + DBSettings.TABLENAME_REFERENCE + "` WHERE reference_id=" + refid);
         
         return true;
    }  
     
    public static List<String> getReferencesForProject(int projectid) throws SQLException {
        
        ResultSet rs = org.ut.biolab.medsavant.db.util.ConnectionController.connect().createStatement().executeQuery(
                        "SELECT reference.name FROM " + org.ut.biolab.medsavant.db.util.DBSettings.TABLENAME_VARIANTTABLEINFO
                        + " LEFT JOIN " + org.ut.biolab.medsavant.db.util.DBSettings.TABLENAME_REFERENCE + " ON "
                        + org.ut.biolab.medsavant.db.util.DBSettings.TABLENAME_VARIANTTABLEINFO + ".reference_id = "
                        + org.ut.biolab.medsavant.db.util.DBSettings.TABLENAME_REFERENCE + ".reference_id "
                        + "WHERE project_id=" + projectid + ";");
        
        List<String> references = new ArrayList<String>();
        while (rs.next()) {
            references.add(rs.getString(1));
        }
        
        return references;
    }
    
    
    public static Map<Integer, String> getReferencesWithoutTablesInProject(int projectid) throws SQLException {
        
        Connection c = org.ut.biolab.medsavant.db.util.ConnectionController.connect();
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT * FROM " + DBSettings.TABLENAME_REFERENCE
                + " WHERE reference_id NOT IN "
                + "(SELECT reference_id FROM " + DBSettings.TABLENAME_VARIANTTABLEINFO
                + " WHERE project_id=" + projectid + ")");
        
        HashMap<Integer,String> result = new HashMap<Integer,String>();
        
        while (rs.next()) {
            result.put(rs.getInt(1), rs.getString(2));
        }
        
        return result;
        
    }

}
