package org.ut.biolab.medsavant.db.util.query;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.OrderObject.Dir;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.ProjectTableSchema;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.ReferenceTableSchema;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.ServerlogTableSchema;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.VariantpendingupdateTableSchema;
import org.ut.biolab.medsavant.db.model.structure.TableSchema;
import org.ut.biolab.medsavant.db.util.ConnectionController;

/**
 *
 * @author mfiume
 */
public class LogQueryUtil {

    public static ResultSet getClientLog() throws SQLException {
        
        TableSchema table = MedSavantDatabase.ServerlogTableSchema;
        SelectQuery query = new SelectQuery();
        query.addFromTable(table.getTable());
        query.addAllColumns();
        query.addCondition(BinaryCondition.notEqualTo(table.getDbColumn(ServerlogTableSchema.COLUMNNAME_OF_USER), "server"));
        query.addOrdering(table.getDbColumn(ServerlogTableSchema.COLUMNNAME_OF_TIMESTAMP), Dir.DESCENDING);
        
        return ConnectionController.connect().createStatement().executeQuery(query.toString());
    }

    public static ResultSet getServerLog() throws SQLException {
        
        TableSchema table = MedSavantDatabase.ServerlogTableSchema;
        SelectQuery query = new SelectQuery();
        query.addFromTable(table.getTable());
        query.addAllColumns();
        query.addCondition(BinaryCondition.equalTo(table.getDbColumn(ServerlogTableSchema.COLUMNNAME_OF_USER), "server"));
        query.addOrdering(table.getDbColumn(ServerlogTableSchema.COLUMNNAME_OF_TIMESTAMP), Dir.DESCENDING);
        
        return ConnectionController.connect().createStatement().executeQuery(query.toString());
    }

    public static ResultSet getAnnotationLog() throws SQLException {
        
        TableSchema projectTable = MedSavantDatabase.ProjectTableSchema;
        TableSchema referenceTable = MedSavantDatabase.ReferenceTableSchema;
        TableSchema updateTable = MedSavantDatabase.VariantpendingupdateTableSchema;
        
        SelectQuery query = new SelectQuery();
        query.addFromTable(updateTable.getTable());
        query.addColumns(
                projectTable.getDbColumn(ProjectTableSchema.COLUMNNAME_OF_NAME),
                referenceTable.getDbColumn(ReferenceTableSchema.COLUMNNAME_OF_NAME),
                updateTable.getDbColumn(VariantpendingupdateTableSchema.COLUMNNAME_OF_ACTION),
                updateTable.getDbColumn(VariantpendingupdateTableSchema.COLUMNNAME_OF_STATUS),
                updateTable.getDbColumn(VariantpendingupdateTableSchema.COLUMNNAME_OF_TIMESTAMP),
                updateTable.getDbColumn(VariantpendingupdateTableSchema.COLUMNNAME_OF_UPDATE_ID));
        query.addJoin(
                SelectQuery.JoinType.LEFT_OUTER, 
                updateTable.getTable(), 
                projectTable.getTable(), 
                BinaryCondition.equalTo(
                        updateTable.getDbColumn(VariantpendingupdateTableSchema.COLUMNNAME_OF_PROJECT_ID), 
                        projectTable.getDbColumn(ProjectTableSchema.COLUMNNAME_OF_PROJECT_ID)));
        query.addJoin(
                SelectQuery.JoinType.LEFT_OUTER, 
                updateTable.getTable(), 
                referenceTable.getTable(), 
                BinaryCondition.equalTo(
                        updateTable.getDbColumn(VariantpendingupdateTableSchema.COLUMNNAME_OF_REFERENCE_ID), 
                        referenceTable.getDbColumn(ReferenceTableSchema.COLUMNNAME_OF_REFERENCE_ID)));
        
        return ConnectionController.connect().createStatement().executeQuery(query.toString());
    }
}
