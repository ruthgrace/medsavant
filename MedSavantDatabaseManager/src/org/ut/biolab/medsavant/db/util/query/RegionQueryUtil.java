/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util.query;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.FunctionCall;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.ut.biolab.medsavant.db.exception.NonFatalDatabaseException;
import org.ut.biolab.medsavant.db.model.BEDRecord;
import org.ut.biolab.medsavant.db.model.GenomicRegion;
import org.ut.biolab.medsavant.db.model.Range;
import org.ut.biolab.medsavant.db.model.RegionSet;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.RegionsetTableSchema;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.RegionsetmembershipTableSchema;
import org.ut.biolab.medsavant.db.model.structure.TableSchema;
import org.ut.biolab.medsavant.db.util.ConnectionController;

/**
 *
 * @author Andrew
 */
public class RegionQueryUtil {
    
    public static void addRegionList(String geneListName, int genomeId, Iterator<String[]> i) throws NonFatalDatabaseException, SQLException {
        
        Connection conn = ConnectionController.connect();       
        TableSchema regionSetTable = MedSavantDatabase.RegionsetTableSchema;     
        TableSchema regionMemberTable = MedSavantDatabase.RegionsetmembershipTableSchema;
        
        //add region set
        InsertQuery query1 = new InsertQuery(regionSetTable.getTable());
        query1.addColumn(regionSetTable.getDBColumn(RegionsetTableSchema.COLUMNNAME_OF_NAME), geneListName);
        
        PreparedStatement stmt = conn.prepareStatement(query1.toString(), Statement.RETURN_GENERATED_KEYS);       
        stmt.execute();
        ResultSet rs = stmt.getGeneratedKeys();
        rs.next();
        
        int regionSetId = rs.getInt(1);
        
        //add regions
        conn.setAutoCommit(false);
        while(i.hasNext()){
            String[] line = i.next();
            InsertQuery query = new InsertQuery(regionMemberTable.getTable());
            query.addColumn(regionMemberTable.getDBColumn(RegionsetmembershipTableSchema.COLUMNNAME_OF_GENOME_ID), genomeId);
            query.addColumn(regionMemberTable.getDBColumn(RegionsetmembershipTableSchema.COLUMNNAME_OF_REGION_SET_ID), regionSetId);
            query.addColumn(regionMemberTable.getDBColumn(RegionsetmembershipTableSchema.COLUMNNAME_OF_CHROM), line[0]);
            query.addColumn(regionMemberTable.getDBColumn(RegionsetmembershipTableSchema.COLUMNNAME_OF_START), line[1]);
            query.addColumn(regionMemberTable.getDBColumn(RegionsetmembershipTableSchema.COLUMNNAME_OF_END), line[2]);
            query.addColumn(regionMemberTable.getDBColumn(RegionsetmembershipTableSchema.COLUMNNAME_OF_DESCRIPTION), line[3]);
            
            conn.createStatement().executeUpdate(query.toString());
        }
        conn.commit();
        conn.setAutoCommit(false);
    }
    
    public static void removeRegionList(int regionSetId) throws SQLException {
        
        TableSchema regionMemberTable = MedSavantDatabase.RegionsetmembershipTableSchema;
        TableSchema regionSetTable = MedSavantDatabase.RegionsetTableSchema;

        Connection c = ConnectionController.connect();
        
        //remove members
        DeleteQuery q1 = new DeleteQuery(regionMemberTable.getTable());
        q1.addCondition(BinaryCondition.equalTo(regionMemberTable.getDBColumn(RegionsetmembershipTableSchema.COLUMNNAME_OF_REGION_SET_ID), regionSetId));
        c.createStatement().execute(q1.toString());
        
        //remove from region regionSetTable
        DeleteQuery q2 = new DeleteQuery(regionSetTable.getTable());
        q2.addCondition(BinaryCondition.equalTo(regionSetTable.getDBColumn(RegionsetTableSchema.COLUMNNAME_OF_REGION_SET_ID), regionSetId));
        c.createStatement().execute(q2.toString());
    }
    
    public static List<RegionSet> getRegionSets() throws SQLException {
        
        TableSchema table = MedSavantDatabase.RegionsetTableSchema;
        
        SelectQuery query = new SelectQuery();
        query.addFromTable(table.getTable());
        query.addAllColumns();
        
        ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString());
        
        List<RegionSet> result = new ArrayList<RegionSet>();
        while(rs.next()){
            result.add(new RegionSet(rs.getInt(RegionsetTableSchema.COLUMNNAME_OF_REGION_SET_ID), rs.getString(RegionsetTableSchema.COLUMNNAME_OF_NAME)));
        }
        return result;
    }
    
    public static int getNumberRegions(int regionSetId) throws SQLException {
        
        TableSchema table = MedSavantDatabase.RegionsetmembershipTableSchema;
        
        SelectQuery query = new SelectQuery();
        query.addFromTable(table.getTable());
        query.addCustomColumns(FunctionCall.countAll());
        query.addCondition(BinaryCondition.equalTo(table.getDBColumn(RegionsetmembershipTableSchema.COLUMNNAME_OF_REGION_SET_ID), regionSetId));

        ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString());
        
        rs.next();
        return rs.getInt(1);
    }

    public static List<String> getRegionNamesInRegionSet(int regionSetId, int limit) throws SQLException {
        
        TableSchema table = MedSavantDatabase.RegionsetmembershipTableSchema;
        
        SelectQuery query = new SelectQuery();
        query.addFromTable(table.getTable());
        query.addAllColumns();
        query.addCondition(BinaryCondition.equalTo(table.getDBColumn(RegionsetmembershipTableSchema.COLUMNNAME_OF_REGION_SET_ID), regionSetId));
        
        ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString() + " LIMIT " + limit);

        List<String> result = new ArrayList<String>();
        while(rs.next()){
            result.add(rs.getString(RegionsetmembershipTableSchema.COLUMNNAME_OF_DESCRIPTION));
        }
        return result;
    }

    public static List<GenomicRegion> getRegionsInRegionSet(int regionSetId) throws SQLException {
        
        TableSchema table = MedSavantDatabase.RegionsetmembershipTableSchema;
        
        SelectQuery query = new SelectQuery();
        query.addFromTable(table.getTable());
        query.addAllColumns();
        query.addCondition(BinaryCondition.equalTo(table.getDBColumn(RegionsetmembershipTableSchema.COLUMNNAME_OF_REGION_SET_ID), regionSetId));
        
        ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString());
        
        List<GenomicRegion> result = new ArrayList<GenomicRegion>();
        while(rs.next()){
            result.add(new GenomicRegion(
                    rs.getString(RegionsetmembershipTableSchema.COLUMNNAME_OF_CHROM), 
                    new Range(rs.getDouble(RegionsetmembershipTableSchema.COLUMNNAME_OF_START), rs.getDouble(RegionsetmembershipTableSchema.COLUMNNAME_OF_END))));
        }
        return result;
    }
    
    public static List<BEDRecord> getBedRegionsInRegionSet(int regionSetId, int limit) throws NonFatalDatabaseException, SQLException {

        TableSchema table = MedSavantDatabase.RegionsetmembershipTableSchema;
        
        SelectQuery query = new SelectQuery();
        query.addFromTable(table.getTable());
        query.addAllColumns();
        query.addCondition(BinaryCondition.equalTo(table.getDBColumn(RegionsetmembershipTableSchema.COLUMNNAME_OF_REGION_SET_ID), regionSetId));
        
        ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString() + " LIMIT " + limit);
        
        List<BEDRecord> result = new ArrayList<BEDRecord>();
        while(rs.next()){
            result.add(new BEDRecord(
                    rs.getString(RegionsetmembershipTableSchema.COLUMNNAME_OF_CHROM), 
                    rs.getInt(RegionsetmembershipTableSchema.COLUMNNAME_OF_START), 
                    rs.getInt(RegionsetmembershipTableSchema.COLUMNNAME_OF_END), 
                    rs.getString(RegionsetmembershipTableSchema.COLUMNNAME_OF_DESCRIPTION)));
        }
        return result;           
    }
    
    
}
