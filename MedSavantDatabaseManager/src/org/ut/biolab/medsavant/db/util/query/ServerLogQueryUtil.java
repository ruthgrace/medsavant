package org.ut.biolab.medsavant.db.util.query;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import org.ut.biolab.medsavant.db.log.DBLogger;
import org.ut.biolab.medsavant.db.table.ServerLogTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;

/**
 *
 * @author mfiume
 */
public class ServerLogQueryUtil {

    public static final String SEVERVER_UNAME = "server";
    public enum LogType { INFO, ERROR, LOGIN, LOGOUT };
    
    public static void addServerLog(LogType t, String description) {
        addLog(SEVERVER_UNAME, t, description);
    }
    
    public static void addLog(String uname, LogType t, String description) {
        try {
            Connection c = ConnectionController.connect();
            java.sql.Timestamp sqlDate = new java.sql.Timestamp((new Date()).getTime());
            String q = 
                    "INSERT INTO " + ServerLogTable.TABLENAME 
                    + " VALUES (null,'" + uname + "','" + t.toString() + "','" + description + "','" + sqlDate + "')";
            c.createStatement().execute(q);
        } catch (SQLException ex) {
            DBLogger.log(ex.getLocalizedMessage(), Level.SEVERE);
        }
    }
}
