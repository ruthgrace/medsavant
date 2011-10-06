package org.ut.biolab.medsavant.db.util.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.table.UserTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;

/**
 *
 * @author mfiume
 */
public class UserQueryUtil {
    
    public static List<String> getUserNames() throws SQLException {
        
        Connection conn = ConnectionController.connect();
        
        ResultSet rs = conn.createStatement().executeQuery("SELECT name FROM " + UserTable.TABLENAME);
        
        List<String> results = new ArrayList<String>();
        
        while (rs.next()) {
            results.add(rs.getString(1));
        }
        
        return results;
    }

    public static boolean userExists(String username) throws SQLException {
        
        Connection c = ConnectionController.connect();
        
        ResultSet rs1 = c.createStatement().executeQuery("SELECT * FROM `" + UserTable.TABLENAME + "` WHERE name=\"" + username + "\"");
        
        return rs1.next();
    }
    
     
    public static int addUser(String name, String pass, boolean isAdmin) throws SQLException {

        (ConnectionController.connect()).createStatement().execute(
                "CREATE USER '"+ name +"'@'localhost' IDENTIFIED BY '"+ pass +"';");
                
        (ConnectionController.connect()).createStatement().execute(
                "GRANT ALL ON "+ DBSettings.DBNAME +".* TO '"+  name +"'@'localhost';");
        

        
        String q = "INSERT INTO " + UserTable.TABLENAME + " VALUES (null,'" + name + "'," + isAdmin + ")";
        PreparedStatement stmt = (ConnectionController.connect()).prepareStatement(q,
                Statement.RETURN_GENERATED_KEYS);

        stmt.execute();
        ResultSet res = stmt.getGeneratedKeys();
        res.next();

        int id = res.getInt(1);
        
        return id;
    }

    public static boolean isUserAdmin(String username) throws SQLException {
        if (userExists(username)) {
            
            ResultSet rs = ConnectionController.connect().createStatement().executeQuery("SELECT is_admin FROM " + UserTable.TABLENAME + " WHERE name=\"" + username + "\"");
            rs.next();
            return rs.getBoolean(1);
            
        } else {
            return false;
        }
    }
    
        
    public static void removeUser(String name) throws SQLException {
        (ConnectionController.connect()).createStatement().execute(
                "DROP USER '"+name+"'@'localhost';");
        Connection c = ConnectionController.connect(DBSettings.DBNAME);
        
        c.createStatement().execute("DELETE FROM `" + UserTable.TABLENAME + "` WHERE name='" + name +"'");
    }

}
