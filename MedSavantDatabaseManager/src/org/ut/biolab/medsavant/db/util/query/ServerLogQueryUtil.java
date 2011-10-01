package org.ut.biolab.medsavant.db.util.query;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import org.ut.biolab.medsavant.db.log.DBLogger;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;

/**
 *
 * @author mfiume
 */
public class ServerLogQueryUtil {

    public enum LogType { INFO, ERROR, LOGIN, LOGOUT };
    
    public static void addLog(String uname, LogType t, String description) {
        try {
            Connection c = ConnectionController.connect();
            java.sql.Timestamp sqlDate = new java.sql.Timestamp((new Date()).getTime());
            String q = "INSERT INTO " + DBSettings.TABLENAME_SERVELOG + " VALUES (null,'" + uname + "','" + t.toString() + "','" + description + "','" + sqlDate + "')";
            c.createStatement().execute(q);
        } catch (SQLException ex) {
            DBLogger.log(ex.getLocalizedMessage(), Level.SEVERE);
        }
    }
}
