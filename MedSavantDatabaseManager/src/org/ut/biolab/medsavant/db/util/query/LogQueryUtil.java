package org.ut.biolab.medsavant.db.util.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.ut.biolab.medsavant.db.table.ProjectTable;
import org.ut.biolab.medsavant.db.table.ReferenceTable;
import org.ut.biolab.medsavant.db.table.ServerLogTable;
import org.ut.biolab.medsavant.db.table.VariantPendingUpdateTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;

/**
 *
 * @author mfiume
 */
public class LogQueryUtil {

    public static ResultSet getClientLog() throws SQLException {
        return ConnectionController.connect().createStatement().executeQuery(
                "SELECT *"
                + " FROM `" + ServerLogTable.TABLENAME + "`"
                + " WHERE " + ServerLogTable.FIELDNAME_USER + " <> 'server'"
                + " ORDER BY " + ServerLogTable.FIELDNAME_TIMESTAMP + " DESC");
    }

    public static ResultSet getServerLog() throws SQLException {
        return ConnectionController.connect().createStatement().executeQuery(
                "SELECT *"
                + " FROM `" + ServerLogTable.TABLENAME + "`"
                + " WHERE " + ServerLogTable.FIELDNAME_USER + "='server'"
                + " ORDER BY " + ServerLogTable.FIELDNAME_TIMESTAMP + " DESC");
    }

    public static ResultSet getAnnotationLog() throws SQLException {
        String s = 
                "SELECT "
                + ProjectTable.TABLENAME + "." + ProjectTable.FIELDNAME_NAME + ","
                + ReferenceTable.TABLENAME + "." + ReferenceTable.FIELDNAME_NAME + ","              
                + VariantPendingUpdateTable.FIELDNAME_ACTION + ","
                + VariantPendingUpdateTable.FIELDNAME_STATUS + ","
                + VariantPendingUpdateTable.FIELDNAME_TIMESTAMP + ","
                + VariantPendingUpdateTable.FIELDNAME_UPDATEID + " "
                + "FROM `" + VariantPendingUpdateTable.TABLENAME + "` "
                + "LEFT JOIN `" + ProjectTable.TABLENAME + "` ON `" + VariantPendingUpdateTable.TABLENAME + "`.project_id=`" + ProjectTable.TABLENAME + "`.project_id "
                + "LEFT JOIN `" + ReferenceTable.TABLENAME + "` ON `" + VariantPendingUpdateTable.TABLENAME + "`.reference_id=`" + ReferenceTable.TABLENAME + "`.reference_id;";
        return ConnectionController.connect().createStatement().executeQuery(s);
    }
}
