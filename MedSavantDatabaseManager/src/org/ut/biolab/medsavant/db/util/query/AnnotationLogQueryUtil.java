/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util.query;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import org.ut.biolab.medsavant.db.table.VariantPendingUpdateTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBUtil;

/**
 *
 * @author Andrew
 */
public class AnnotationLogQueryUtil {

   
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
    
    public static Status intToStatus(int status){
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
    
    public static int addAnnotationLogEntry(int projectId, int referenceId, Action action) throws SQLException{    
        return addAnnotationLogEntry(projectId,referenceId,action,Status.PREPROCESS);
    }
    
    public static int addAnnotationLogEntry(int projectId, int referenceId, Action action, Status status) throws SQLException {
        Timestamp sqlDate = DBUtil.getCurrentTimestamp();
        String query = 
                "INSERT INTO " + VariantPendingUpdateTable.TABLENAME + " (" + 
                VariantPendingUpdateTable.FIELDNAME_PROJECTID + "," + 
                VariantPendingUpdateTable.FIELDNAME_REFERENCEID + "," + 
                VariantPendingUpdateTable.FIELDNAME_ACTION + "," + 
                VariantPendingUpdateTable.FIELDNAME_STATUS + "," + 
                VariantPendingUpdateTable.FIELDNAME_TIMESTAMP + 
                ") VALUES (" + 
                projectId + "," + 
                referenceId + "," + 
                actionToInt(action) + "," + 
                statusToInt(status) + ",\"" + 
                sqlDate + "\");";
        PreparedStatement stmt = (ConnectionController.connect()).prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.execute();
        
        ResultSet rs = stmt.getGeneratedKeys();
        rs.next();
        return rs.getInt(1);
    }
    

    public static ResultSet getPendingUpdates() throws SQLException, IOException{
        Connection conn = ConnectionController.connect();
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT *"
                + " FROM " + VariantPendingUpdateTable.TABLENAME
                + " WHERE " + VariantPendingUpdateTable.FIELDNAME_STATUS + "=" + statusToInt(Status.PENDING)
                + " ORDER BY " + VariantPendingUpdateTable.FIELDNAME_ACTION + ", " + VariantPendingUpdateTable.FIELDNAME_UPDATEID);//always do updates before adds
        
        return rs;
    }
    
    public static void setAnnotationLogStatus(int updateId, Status status) throws SQLException {
        Connection conn = ConnectionController.connect();
        conn.createStatement().executeUpdate(
                "UPDATE " + VariantPendingUpdateTable.TABLENAME + 
                " SET " + VariantPendingUpdateTable.FIELDNAME_STATUS + "=" + statusToInt(status) + 
                " WHERE " + VariantPendingUpdateTable.FIELDNAME_UPDATEID + "=" + updateId);
    }
    
    public static void setAnnotationLogStatus(int updateId, Status status, Timestamp sqlDate) throws SQLException {
        Connection conn = ConnectionController.connect();
        conn.createStatement().executeUpdate(
                "UPDATE " + VariantPendingUpdateTable.TABLENAME + 
                " SET " + VariantPendingUpdateTable.FIELDNAME_STATUS + "=" + statusToInt(status) + ", `" + VariantPendingUpdateTable.FIELDNAME_TIMESTAMP + "`=\"" + sqlDate + "\"" +  
                " WHERE " + VariantPendingUpdateTable.FIELDNAME_UPDATEID + "=" + updateId);
    }
    
}
