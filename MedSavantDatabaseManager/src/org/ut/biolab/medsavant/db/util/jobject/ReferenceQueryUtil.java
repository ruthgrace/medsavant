package org.ut.biolab.medsavant.db.util.jobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.util.ConnectionController;

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
    
    public static int getReferenceId(String referenceName) throws SQLException {
        
        Connection conn = ConnectionController.connect();
        
        ResultSet rs = conn.createStatement().executeQuery("SELECT reference_id FROM " + org.ut.biolab.medsavant.db.util.DBSettings.TABLENAME_REFERENCE + " WHERE `name`=\"" + referenceName + "\"");
        
        if(rs.next()){
            return rs.getInt(1);
        } else {
            return -1;
        }
    }
}
