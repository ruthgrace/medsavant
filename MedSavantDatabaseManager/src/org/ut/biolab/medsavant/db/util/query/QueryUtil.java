/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util.query;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.ComboCondition;
import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ut.biolab.medsavant.db.exception.NonFatalDatabaseException;
import org.ut.biolab.medsavant.db.model.BEDRecord;
import org.ut.biolab.medsavant.db.model.Range;

/**
 *
 * @author Andrew
 */
public class QueryUtil {
    
    public static List<String> getBAMFilesForDNAIds(List<String> dnaIds) throws SQLException, NonFatalDatabaseException {
        
        
        /*TableSchema t = OMedSavantDatabase.getInstance().getAlignmentTableSchema();
        SelectQuery q = new SelectQuery();
        q.setIsDistinct(true);
        q.addColumns(t.getDBColumn(AlignmentTableSchema.ALIAS_ALIGNMENTPATH));
        q.addFromTable(t.getTable());

        Condition[] conditions = new Condition[dnaIds.size()];
        for(int i = 0; i < dnaIds.size(); i++){
            conditions[i] = new BinaryCondition(BinaryCondition.Op.EQUAL_TO, t.getDBColumn(VariantTableSchema.ALIAS_DNAID), dnaIds.get(i));
        }
        q.addCondition(ComboCondition.or(conditions));
        
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery(q.toString());

        List<String> results = new ArrayList<String>();
        while (rs.next()) {
            results.add(rs.getString(1));
        }*/
        
        System.err.println("NOT IMPLEMENTED YET");
        
        return new ArrayList<String>();      
    }
    
     public static Map<String, List<String>> getSavantBookmarkPositionsForDNAIds(List<String> dnaIds, int limit) throws SQLException, NonFatalDatabaseException {
        
        /*Map<String, List<String>> results = new HashMap<String, List<String>>();
        
        TableSchema t = OMedSavantDatabase.getInstance().getVariantTableSchema();
        SelectQuery q = getCurrentBaseVariantFilterQuery();
        q.addColumns(t.getDBColumn(VariantTableSchema.ALIAS_DNAID), t.getDBColumn(VariantTableSchema.ALIAS_CHROM), t.getDBColumn(VariantTableSchema.ALIAS_POSITION));
        
        Condition[] conditions = new Condition[dnaIds.size()];
        for(int i = 0; i < dnaIds.size(); i++){
            conditions[i] = new BinaryCondition(BinaryCondition.Op.EQUAL_TO, t.getDBColumn(VariantTableSchema.ALIAS_DNAID), dnaIds.get(i));
            results.put(dnaIds.get(i), new ArrayList<String>());
        }
        q.addCondition(ComboCondition.or(conditions));    
        
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery(q.toString() + ((limit == -1) ? "" : (" LIMIT " + limit)));

        while (rs.next()) {
            results.get(rs.getString(1)).add(rs.getString(2) + ":" + (rs.getLong(3)-100) + "-" + (rs.getLong(3)+100));  
        }
        
        return results;*/
         
        System.err.println("NOT IMPLEMENTED YET");
        
        return new HashMap<String, List<String>>(); 
    }
}
