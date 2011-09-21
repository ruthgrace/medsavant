package org.ut.biolab.medsavant.db.util.jobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    
}
