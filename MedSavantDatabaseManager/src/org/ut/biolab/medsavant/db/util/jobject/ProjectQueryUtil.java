package org.ut.biolab.medsavant.db.util.jobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;
import org.ut.biolab.medsavant.db.util.DBUtil;

/**
 *
 * @author mfiume
 */
public class ProjectQueryUtil {
    
    public static List<String> getProjectNames() throws SQLException {
        
        Connection conn = ConnectionController.connect();
        
        ResultSet rs = conn.createStatement().executeQuery("SELECT name FROM " + org.ut.biolab.medsavant.db.util.DBSettings.TABLENAME_PROJECT);
        
        List<String> results = new ArrayList<String>();
        
        while (rs.next()) {
            results.add(rs.getString(1));
        }
        
        return results;
    }
    
    
    public static boolean containsProject(String projectName) throws SQLException {
        
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT * FROM `" + DBSettings.TABLENAME_PROJECT + "` WHERE name=\"" + projectName + "\"");
        
        return rs1.next();
    }

    public static int getProjectId(String projectName) throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT project_id FROM `" + DBSettings.TABLENAME_PROJECT + "` WHERE name=\"" + projectName + "\"");
        
        if (rs1.next()) {
            return rs1.getInt(1);
        } else {
            return -1;
        }
    }

    public static void removeReferenceForProject(int project_id, int ref_id) throws SQLException {
        
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT variant_tablename FROM `" + DBSettings.TABLENAME_VARIANTTABLEINFO + "` WHERE project_id=" + project_id + " AND reference_id=" + ref_id);
        
        while (rs1.next()) {
            String tableName = rs1.getString(1);
            DBUtil.dropTable(tableName);
        }
        
        c.createStatement().execute("DELETE FROM `" + DBSettings.TABLENAME_VARIANTTABLEINFO + "` WHERE project_id=" + project_id + " AND reference_id=" + ref_id);
    }
}
