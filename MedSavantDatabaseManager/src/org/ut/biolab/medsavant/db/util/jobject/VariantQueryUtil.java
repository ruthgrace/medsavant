/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util.jobject;

//import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.Condition;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBUtil;

/**
 *
 * @author Andrew
 */
public class VariantQueryUtil {
    
    public static Vector getVariants(int projectId, int referenceId, int limit) throws SQLException {       
        return getVariants(projectId, referenceId, new ArrayList(), limit);
    }
    
    public static Vector getVariants(int projectId, int referenceId, List conditions, int limit) throws SQLException {            
        
        String query = 
                "SELECT *" + 
                " FROM " + DBUtil.getVariantTableName(projectId, referenceId) + " t0 ";  
        if(!conditions.isEmpty()){
            query += "WHERE ";
        }
        query += conditionsToString(conditions);
        query += " LIMIT " + limit;
        
        Connection conn = ConnectionController.connect();
        ResultSet rs = conn.createStatement().executeQuery(query);
        
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int numberColumns = rsMetaData.getColumnCount();
        
        Vector result = new Vector();
        while(rs.next()){
            Vector v = new Vector();
            for(int i = 1; i <= numberColumns; i++){
                v.add(rs.getObject(i));
            }
            result.add(v);
        }
        
        return result;
    }
    
    private static String conditionsToString(List conditions){
        String s = "";
        for(int i = 0; i < conditions.size(); i++){
            s += conditions.get(i).toString();
            if(i != conditions.size()-1){
                s += " AND ";
            }
        }
        return s;
    }
    
    public static double[] getExtremeValuesForColumn(String tablename, String columnname) throws SQLException {      
        Connection conn = ConnectionController.connect();        
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT MIN(" + columnname + "),MAX(" + columnname + ")" + 
                " FROM " + tablename);
        
        double[] result = new double[2];
        rs.next();
        result[0] = rs.getDouble(1);
        result[1] = rs.getDouble(2);
        
        return result;
    }
    
    public static List<String> getDistinctValuesForColumn(String tablename, String columnname) throws SQLException {
        Connection conn = ConnectionController.connect();
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT DISTINCT " + columnname + 
                " FROM " + tablename);
        
        List<String> result = new ArrayList<String>();
        while(rs.next()){
            String val = rs.getString(1);
            if(val == null){
                result.add("");
            } else {
                result.add(val);
            }
        }
        
        return result;
    }
    
    public static int getNumFilteredVariants(int projectId, int referenceId) throws SQLException {
        return getNumFilteredVariants(projectId, referenceId, new ArrayList());
    }
    
    public static int getNumFilteredVariants(int projectId, int referenceId, List conditions) throws SQLException {
        
        String query = 
                "SELECT COUNT(*)" + 
                " FROM " + DBUtil.getVariantTableName(projectId, referenceId) + " t0 ";  
        if(!conditions.isEmpty()){
            query += "WHERE ";
        }
        query += conditionsToString(conditions);
        
        Connection conn = ConnectionController.connect();
        ResultSet rs = conn.createStatement().executeQuery(query);
        
        rs.next();
        return rs.getInt(1);
    }
    
    public static int getFilteredFrequencyValuesForColumnInRange(int projectId, int referenceId, List conditions, String column, double min, double max) throws SQLException {
        
        String query = 
                "SELECT COUNT(*)" + 
                " FROM " + DBUtil.getVariantTableName(projectId, referenceId) + " t0" +
                " WHERE `" + column + "`>=" + min + " AND `" + column + "`<" + max;
        if(!conditions.isEmpty()){
            query += " AND ";
        }
        query += conditionsToString(conditions);
        
        Connection conn = ConnectionController.connect();
        ResultSet rs = conn.createStatement().executeQuery(query);
        
        rs.next();
        return rs.getInt(1);        
    }
    
    public static Map<String, Integer> getFilteredFrequencyValuesForColumn(int projectId, int referenceId, List conditions, String column) throws SQLException {
        
        String query = 
                "SELECT `" + column + "`, COUNT(*)" + 
                " FROM " + DBUtil.getVariantTableName(projectId, referenceId) + " t0";
        if(!conditions.isEmpty()){
            query += "WHERE ";
        }
        query += conditionsToString(conditions);
        query += " GROUP BY `" + column + "`";
                
        Connection conn = ConnectionController.connect();
        ResultSet rs = conn.createStatement().executeQuery(query);
        
        Map<String, Integer> map = new HashMap<String, Integer>();
        
        while (rs.next()) {
            map.put(rs.getString(1), rs.getInt(2));
        }

        return map;     
    }
    
    
}
