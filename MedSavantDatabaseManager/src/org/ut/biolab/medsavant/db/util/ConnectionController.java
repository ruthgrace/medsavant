/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mfiume
 */
public class ConnectionController {

    private static Connection connection;

    public static void disconnectAll() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
            }
        }

    }
    
    public static Connection connect() throws SQLException {
        return connect(DBSettings.DBNAME);
    }

    public static Connection connect(String dbname) throws SQLException {

        if (connection == null) {
            connection = connectOnce(dbname);
        }

        return connection;
    }

    
    private static String dbdriver = "com.mysql.jdbc.Driver";
    private static String dburl = "jdbc:mysql://localhost:5029/";
    private static String user = "root";
    private static String pw = "";

    private static Connection connectOnce(String dbname) throws SQLException {
        Connection c;
        try {
            Class.forName(dbdriver).newInstance();
        } catch (Exception ex) {
        }
        c = DriverManager.getConnection(dburl + dbname, user, pw);
        return c;
    }
}
