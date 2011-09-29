/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util.jobject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;

/**
 *
 * @author Andrew
 */
public class LogQueryUtil {
    
    public static enum Action {ADD_VARIANTS, UPDATE_TABLE};
    public static enum Status {PREPROCESS, PENDING, INPROGRESS, ERROR, COMPLETE}; 
    
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
    
    private static int statusToInt(Status status){
        switch(status){
            case PREPROCESS:
                return 0;
            case PENDING:
                return 1;
            case INPROGRESS:
                return 2;
            case ERROR:
                return 3;
            case COMPLETE:
                return 4;
            default:
                return -1;
        }
    }
    
    private static Status intToStatus(int status){
        switch(status){
            case 0:
                return Status.PREPROCESS;
            case 1:
                return Status.PENDING;
            case 2:
                return Status.INPROGRESS;
            case 3:
                return Status.ERROR;
            case 4:
                return Status.COMPLETE;
            default:
                return null;
        }
    }
    
    public static int addLogEntry(int projectId, int referenceId, Action action) throws SQLException{    
        String query = 
                "INSERT INTO " + DBSettings.TABLENAME_VARIANTPENDINGUPDATE + 
                " (project_id, reference_id, action, status) VALUES" + 
                " (" + projectId + "," + referenceId + "," + actionToInt(action) + "," + statusToInt(Status.PREPROCESS) + ");";
        PreparedStatement stmt = (ConnectionController.connect()).prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.execute();
        
        ResultSet rs = stmt.getGeneratedKeys();
        rs.next();
        return rs.getInt(1);
    }

    public static ResultSet getPendingUpdates() throws SQLException, IOException{
        Connection conn = ConnectionController.connect();
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM " + DBSettings.TABLENAME_VARIANTPENDINGUPDATE
                + " WHERE status=" + statusToInt(Status.PENDING)
                + " ORDER BY action, update_id");//always do updates before adds
        
        return rs;
    }
    
    public static void setLogStatus(int updateId, Status status) throws SQLException {
        Connection conn = ConnectionController.connect();
        conn.createStatement().executeUpdate(
                "UPDATE " + DBSettings.TABLENAME_VARIANTPENDINGUPDATE + 
                " SET status=" + statusToInt(status) + 
                " WHERE update_id=" + updateId);
    }
    
}
