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
    
    private static String dbhost = "localhost";
    private static int port = 5029;
    private static String dbname = "medsavantkb";
    
    public static Connection connect() throws SQLException {
        return connect(dbhost,port,dbname);
    }

    public static Connection connect(String dbhost, int port, String dbname) throws SQLException {

        if (connection == null) {
            connection = connectOnce(dbhost, port, dbname);
        }

        return connection;
    }

    public static void setDbhost(String dbhost) {
        ConnectionController.dbhost = dbhost;
    }

    public static void setDbname(String dbname) {
        ConnectionController.dbname = dbname;
    }

    public static void setPort(int port) {
        ConnectionController.port = port;
    }

    public static String getDbname() {
        return dbname;
    }

    
    
    
    private static String dbdriver = "com.mysql.jdbc.Driver";
    private static String dburl = "jdbc:mysql://";
    private static String user = "root";
    private static String pw = "";

    private static Connection connectOnce(String dbhost, int port, String dbname) throws SQLException {
        Connection c;
        try {
            Class.forName(dbdriver).newInstance();
        } catch (Exception ex) {
        }
        c = DriverManager.getConnection(dburl + dbhost + ":" + port + "/" + dbname, user, pw);
        return c;
    }
}
