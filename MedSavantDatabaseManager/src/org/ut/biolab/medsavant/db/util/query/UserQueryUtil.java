package org.ut.biolab.medsavant.db.util.query;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.UserTableSchema;
import org.ut.biolab.medsavant.db.model.structure.TableSchema;
import org.ut.biolab.medsavant.db.table.UserTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;

/**
 *
 * @author mfiume
 */
public class UserQueryUtil {
    
    public static List<String> getUserNames() throws SQLException {
        
        TableSchema table = MedSavantDatabase.UserTableSchema;
        SelectQuery query = new SelectQuery();
        query.addFromTable(table.getTable());
        query.addColumns(table.getDBColumn(UserTableSchema.COLUMNNAME_OF_NAME));
        
        ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString());
        
        List<String> results = new ArrayList<String>();       
        while (rs.next()) {
            results.add(rs.getString(1));
        }
        
        return results;
    }

    public static boolean userExists(String username) throws SQLException {
        
        TableSchema table = MedSavantDatabase.UserTableSchema;
        SelectQuery query = new SelectQuery();
        query.addFromTable(table.getTable());
        query.addAllColumns();
        query.addCondition(BinaryCondition.equalTo(table.getDBColumn(UserTableSchema.COLUMNNAME_OF_NAME), username));
        
        ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString());
        
        return rs.next();
    }
    
     
    public static int addUser(String name, String pass, boolean isAdmin) throws SQLException {

        (ConnectionController.connect()).createStatement().execute(
                "CREATE USER '"+ name +"'@'localhost' IDENTIFIED BY '"+ pass +"';");
                
        (ConnectionController.connect()).createStatement().execute(
                "GRANT ALL ON "+ DBSettings.DBNAME +".* TO '"+  name +"'@'localhost';");
        

        TableSchema table = MedSavantDatabase.UserTableSchema;
        InsertQuery query = new InsertQuery(table.getTable());
        query.addColumn(table.getDBColumn(UserTableSchema.COLUMNNAME_OF_NAME), name);
        query.addColumn(table.getDBColumn(UserTableSchema.COLUMNNAME_OF_IS_ADMIN), isAdmin);
        
        PreparedStatement stmt = (ConnectionController.connect()).prepareStatement(query.toString(),
                Statement.RETURN_GENERATED_KEYS);

        stmt.execute();
        ResultSet res = stmt.getGeneratedKeys();
        res.next();

        int id = res.getInt(1);
        
        return id;
    }

    public static boolean isUserAdmin(String username) throws SQLException {
        if (userExists(username)) {
            
            TableSchema table = MedSavantDatabase.UserTableSchema;
            SelectQuery query = new SelectQuery();
            query.addFromTable(table.getTable());
            query.addColumns(table.getDBColumn(UserTableSchema.COLUMNNAME_OF_IS_ADMIN));
            query.addCondition(BinaryCondition.equalTo(table.getDBColumn(UserTableSchema.COLUMNNAME_OF_NAME), username));
            
            ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString());
            
            rs.next();
            return rs.getBoolean(1);
            
        } else {
            return false;
        }
    }
    
        
    public static void removeUser(String name) throws SQLException {
        (ConnectionController.connect()).createStatement().execute(
                "DROP USER '"+name+"'@'localhost';");
        
        TableSchema table = MedSavantDatabase.UserTableSchema;
        DeleteQuery query = new DeleteQuery(table.getTable());
        query.addCondition(BinaryCondition.equalTo(table.getDBColumn(UserTableSchema.COLUMNNAME_OF_NAME), name));
        ConnectionController.connect().createStatement().execute(query.toString());
    }

}
