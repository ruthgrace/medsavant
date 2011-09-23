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
import org.ut.biolab.medsavant.db.util.UpdateVariantTable;

/**
 *
 * @author Andrew
 */
public class LogQueryUtil {
    
    public enum Action {ADD_VARIANTS, UPDATE_TABLE};
    
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
    
    private static Action intToAction(int action){
        switch(action){
            case 0:
                return Action.UPDATE_TABLE;
            case 1:
                return Action.ADD_VARIANTS;
            default:
                return null;
        }
    }
    
    public static void addLogEntry(int projectId, int referenceId, Action action) throws SQLException{    
        //if entry does not exist, add entry
        if(!entryExists(projectId, referenceId, action)){
            Connection conn = ConnectionController.connect();
            conn.createStatement().executeUpdate(
                "INSERT INTO " + DBSettings.TABLENAME_VARIANTPENDINGUPDATE
                + " (project_id, reference_id, action) VALUES"
                + " (" + projectId + "," + referenceId + "," + actionToInt(action) + ");");
        }
    }
    
    public static boolean entryExists(int projectId, int referenceId, Action action) throws SQLException{
        Connection conn = ConnectionController.connect();
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM " + DBSettings.TABLENAME_VARIANTPENDINGUPDATE
                + " WHERE project_id=" + projectId + " AND reference_id=" + referenceId + " AND action=" + actionToInt(action));
        return rs.next();
    }
    
    public static void checkAndUpdate() throws SQLException, IOException{
        Connection conn = ConnectionController.connect();
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM " + DBSettings.TABLENAME_VARIANTPENDINGUPDATE
                + " ORDER BY action"); //always do updates before adds
        
        while(rs.next()){
            int projectId = rs.getInt("project_id");
            int referenceId = rs.getInt("reference_id");
            Action action = intToAction(rs.getInt("action"));           
            switch(action){
                case ADD_VARIANTS:
                    UpdateVariantTable.performAddVCF(projectId, referenceId);
                    break;
                case UPDATE_TABLE:
                    UpdateVariantTable.performUpdate(projectId, referenceId);
                    break;
            }
        }
           
        clearLog();
    }
    
    private static void clearLog() throws SQLException{
        Connection conn = ConnectionController.connect();
        conn.createStatement().execute(
                "DELETE FROM " + DBSettings.TABLENAME_VARIANTPENDINGUPDATE
                + " WHERE 1=1");       
    }
    
}