package org.ut.biolab.medsavant.db.util.query;

import com.healthmarketscience.sqlbuilder.InsertQuery;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import org.ut.biolab.medsavant.db.log.DBLogger;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.ServerlogTableSchema;
import org.ut.biolab.medsavant.db.model.structure.TableSchema;
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
            Timestamp sqlDate = new java.sql.Timestamp((new Date()).getTime());
            
            TableSchema table = MedSavantDatabase.ServerlogTableSchema;
            InsertQuery query = new InsertQuery(table.getTable());
            query.addColumn(table.getDBColumn(ServerlogTableSchema.COLUMNNAME_OF_USER), uname);
            query.addColumn(table.getDBColumn(ServerlogTableSchema.COLUMNNAME_OF_EVENT), t.toString());
            query.addColumn(table.getDBColumn(ServerlogTableSchema.COLUMNNAME_OF_DESCRIPTION), description);
            query.addColumn(table.getDBColumn(ServerlogTableSchema.COLUMNNAME_OF_TIMESTAMP), sqlDate);
            ConnectionController.connect().createStatement().execute(query.toString());
            
        } catch (SQLException ex) {
            DBLogger.log(ex.getLocalizedMessage(), Level.SEVERE);
        }
    }
}
