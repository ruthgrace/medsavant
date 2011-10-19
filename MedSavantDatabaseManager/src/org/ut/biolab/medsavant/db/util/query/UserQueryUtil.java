/*
 *    Copyright 2011 University of Toronto
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.ut.biolab.medsavant.db.util.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;

import org.ut.biolab.medsavant.db.api.MedSavantDatabase;
import org.ut.biolab.medsavant.db.api.MedSavantDatabase.UserTableSchema;
import org.ut.biolab.medsavant.db.model.structure.TableSchema;
import org.ut.biolab.medsavant.db.util.ConnectionController;

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
                "GRANT ALL ON "+ ConnectionController.getDbname() +".* TO '"+  name +"'@'localhost';");
        

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
