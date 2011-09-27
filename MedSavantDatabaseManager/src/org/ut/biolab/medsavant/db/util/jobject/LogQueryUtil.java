/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util.jobject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;

/**
 *
 * @author Andrew
 */
public class LogQueryUtil {
    
    public static enum Action {ADD_VARIANTS, UPDATE_TABLE};
    
    private static int actionToInt(Action action){
        switch(action){
            case UPDATE_TABLE:
                return 0;
            case ADD_VARIANTS:
                return 1;
            default:
                return -1;
        }
    }
    
    public static Action intToAction(int action){
        switch(action){
            case 0:
                return Action.UPDATE_TABLE;
            case 1:
                return Action.ADD_VARIANTS;
            default:
                return null;
        }
    }
    
    public static int addLogEntry(int projectId, int referenceId, Action action) throws SQLException{    
        Connection conn = ConnectionController.connect();
        conn.createStatement().executeUpdate(
            "INSERT INTO " + DBSettings.TABLENAME_VARIANTPENDINGUPDATE
            + " (project_id, reference_id, action) VALUES"
            + " (" + projectId + "," + referenceId + "," + actionToInt(action) + ");");
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT update_id FROM " + DBSettings.TABLENAME_VARIANTPENDINGUPDATE +
                " WHERE project_id=" + projectId + " AND reference_id=" + referenceId + 
                " ORDER BY update_id DESC"); 
        rs.next();
        return rs.getInt("update_id"); 
    }

    public static ResultSet getPendingUpdates() throws SQLException, IOException{
        Connection conn = ConnectionController.connect();
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM " + DBSettings.TABLENAME_VARIANTPENDINGUPDATE
                + " WHERE pending=1"
                + " ORDER BY action, update_id");//always do updates before adds
        
        return rs;
    }
        
    public static void setLogPending(int updateId, boolean pending) throws SQLException {
        Connection conn = ConnectionController.connect();
        conn.createStatement().executeUpdate(
                "UPDATE " + DBSettings.TABLENAME_VARIANTPENDINGUPDATE + 
                " SET pending=" + (pending ? "1" : "0") + 
                " WHERE update_id=" + updateId);
    }
    
}
