/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author mfiume
 */
public class ConnectionController {

    private static Connection lastConnection;

    public static void disconnectAll() {
        if (lastConnection != null) {
            try {
                lastConnection.close();
            } catch (SQLException ex) {
            }
            lastConnection = null;
        }

    }
    
    private static String dbhost;
    private static int port;
    private static String dbname;
    
    public static Connection connectPooled() throws SQLException {
        
        if (!hostSet) {
            throw new SQLException("DB host not set");
        }
        
        if (!dbnameset) {
            throw new SQLException("DB name not set");
        }
        
        if (!portset) {
            throw new SQLException("DB port not set");
        }
        
        return connectInternal(dbhost,port,dbname);
    }
    
    public static Connection connectUnpooled(String dbhost, int port, String dbname) throws SQLException {
        return connectOnce(dbhost, port, dbname);
    }
    
    private static Connection connectInternal(String dbhost, int port, String dbname) throws SQLException {

        if (lastConnection == null || lastConnection.isClosed()) {
            lastConnection = connectOnce(dbhost, port, dbname);
        }
        
        return lastConnection;
    }

    public static void setDbhost(String dbhost) {
        hostSet = true;
        ConnectionController.dbhost = dbhost;
    }

    public static void setDbname(String dbname) {
        dbnameset = true;
        ConnectionController.dbname = dbname;
    }

    public static void setPort(int port) {
        portset = true;
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
    private static boolean hostSet;
    private static boolean portset;
    private static boolean dbnameset;
}
