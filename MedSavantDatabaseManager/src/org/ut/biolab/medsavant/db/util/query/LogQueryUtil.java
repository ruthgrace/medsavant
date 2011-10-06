package org.ut.biolab.medsavant.db.util.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.ut.biolab.medsavant.db.table.ProjectTable;
import org.ut.biolab.medsavant.db.table.ReferenceTable;
import org.ut.biolab.medsavant.db.table.ServerLogTable;
import org.ut.biolab.medsavant.db.table.VariantPendingUpdateTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;

/**
 *
 * @author mfiume
 */
public class LogQueryUtil {

    public static ResultSet getClientLog() throws SQLException {
        return ConnectionController.connect().createStatement().executeQuery("SELECT * FROM `" + ServerLogTable.TABLENAME + "` WHERE user <> 'server' ORDER BY timestamp DESC");
    }

    public static ResultSet getServerLog() throws SQLException {
        return ConnectionController.connect().createStatement().executeQuery("SELECT * FROM `" + ServerLogTable.TABLENAME + "` WHERE user='server' ORDER BY timestamp DESC");
    }

    public static ResultSet getAnnotationLog() throws SQLException {
        String s = 
                "SELECT "
                + "project.name,"
                + "reference.name,"              
                + "action,"
                + "status,"
                + "timestamp,"
                + "update_id "
                + "FROM `" + VariantPendingUpdateTable.TABLENAME + "` "
                + "LEFT JOIN `" + ProjectTable.TABLENAME + "` ON `" + VariantPendingUpdateTable.TABLENAME + "`.project_id=`" + ProjectTable.TABLENAME + "`.project_id "
                + "LEFT JOIN `" + ReferenceTable.TABLENAME + "` ON `" + VariantPendingUpdateTable.TABLENAME + "`.reference_id=`" + ReferenceTable.TABLENAME + "`.reference_id;";
        return ConnectionController.connect().createStatement().executeQuery(s);
    }
}
