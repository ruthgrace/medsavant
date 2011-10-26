/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.model.structure;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.ut.biolab.medsavant.db.api.MedSavantDatabase;
import org.ut.biolab.medsavant.db.api.MedSavantDatabase.DefaultPatientTableSchema;
import org.ut.biolab.medsavant.db.api.MedSavantDatabase.DefaultVariantTableSchema;
import org.ut.biolab.medsavant.db.util.DBUtil;

/**
 *
 * @author Andrew
 */
public class CustomTables {
    
    private static Map<String, TableSchema> map = new HashMap<String, TableSchema>();
    
    /*public static class CustomvariantTableSchema extends DefaultVariantTableSchema {
        public CustomvariantTableSchema(String tablename) {
            super(MedSavantDatabase.schema, tablename);
            //could add columns here...
        }
    }
    
    public static class CustompatientTableSchema extends DefaultPatientTableSchema {
        public CustompatientTableSchema(String tablename) {
            super(MedSavantDatabase.schema, tablename);
            //could add columns here...
        }
    }
        
    public static TableSchema getPatientTableSchema(String tablename) {
        
        TableSchema table;
        if((table = map.get(tablename)) != null){
            return table;
        }
        
        table = new CustompatientTableSchema(tablename);
        map.put(tablename, table);
        return table;
    }
    
    public static TableSchema getVariantTableSchema(String tablename) {
        
        TableSchema table;
        if((table = map.get(tablename)) != null){
            return table;
        }
        
        table = new CustomvariantTableSchema(tablename);
        map.put(tablename, table);
        return table;
    }*/

    public static TableSchema getCustomTableSchema(String tablename) throws SQLException {
        
        TableSchema table;
        if((table = map.get(tablename)) != null){
            return table;
        }
        
        table = DBUtil.importTableSchema(tablename);
        return table;
    }
    
    public void clearMap(){
        //TODO: do this after a logout event!!
        map.clear();
    }
    
}
