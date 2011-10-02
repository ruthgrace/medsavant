package org.ut.biolab.medsavant.db.util.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;

/**
 *
 * @author mfiume
 */
public class LogQueryUtil {

    public static ResultSet getClientLog() throws SQLException {
        return ConnectionController.connect().createStatement().executeQuery("SELECT * FROM `" + DBSettings.TABLENAME_SERVELOG + "` WHERE user <> 'server' ORDER BY timestamp DESC");
    }

    public static ResultSet getServerLog() throws SQLException {
        return ConnectionController.connect().createStatement().executeQuery("SELECT * FROM `" + DBSettings.TABLENAME_SERVELOG + "` WHERE user='server' ORDER BY timestamp DESC");
    }

    public static ResultSet getAnnotationLog() throws SQLException {
        String s = 
                "SELECT "
                + "project.name,"
                + "reference.name,"
                + "action,"
                + "status "
                + "FROM `" + DBSettings.TABLENAME_VARIANTPENDINGUPDATE + "` "
                + "LEFT JOIN `" + DBSettings.TABLENAME_PROJECT + "` ON `" + DBSettings.TABLENAME_VARIANTPENDINGUPDATE + "`.project_id=`" + DBSettings.TABLENAME_PROJECT + "`.project_id "
                + "LEFT JOIN `" + DBSettings.TABLENAME_REFERENCE + "` ON `" + DBSettings.TABLENAME_VARIANTPENDINGUPDATE + "`.reference_id=`" + DBSettings.TABLENAME_REFERENCE + "`.reference_id;";
        return ConnectionController.connect().createStatement().executeQuery(s);
    }
}
