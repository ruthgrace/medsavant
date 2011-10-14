/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.ut.biolab.medsavant.db.exception.NonFatalDatabaseException;
import org.ut.biolab.medsavant.db.model.GenomicRegion;
import org.ut.biolab.medsavant.db.model.Range;
import org.ut.biolab.medsavant.db.model.RegionSet;
import org.ut.biolab.medsavant.db.table.RegionSetMembershipTable;
import org.ut.biolab.medsavant.db.table.RegionSetTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;

/**
 *
 * @author Andrew
 */
public class RegionQueryUtil {
    
    public static void addRegionList(String geneListName, Iterator<String[]> i) throws NonFatalDatabaseException, SQLException {

        Connection conn = ConnectionController.connect();
        
        //add region set
        String q = 
                "INSERT INTO " + RegionSetTable.TABLENAME 
                + " (" + RegionSetTable.FIELDNAME_NAME + ") VALUES ('" + geneListName + "')";
        PreparedStatement stmt = conn.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);
        
        stmt.execute();
        ResultSet rs = stmt.getGeneratedKeys();
        rs.next();
        
        int regionSetId = rs.getInt(1);
        
        //add regions
        conn.setAutoCommit(false);
        while(i.hasNext()){
            String[] line = i.next();
            conn.createStatement().executeUpdate(
                    "INSERT INTO " + RegionSetMembershipTable.TABLENAME + " (" 
                    + RegionSetMembershipTable.FIELDNAME_GENOMEID + ","
                    + RegionSetMembershipTable.FIELDNAME_REGIONSETID + ","
                    + RegionSetMembershipTable.FIELDNAME_CHROM + "," 
                    + RegionSetMembershipTable.FIELDNAME_START + "," 
                    + RegionSetMembershipTable.FIELDNAME_END + "," 
                    + RegionSetMembershipTable.FIELDNAME_DESCRIPTION
                    + ") VALUES ("
                    + 1 + ","
                    + regionSetId + ","
                    + "'" + line[0] + "',"
                    + line[1] + ","
                    + line[2] + ","
                    + "'" + line[3] + "')");
        }
        conn.commit();
        conn.setAutoCommit(false);
    }
    
    public static void removeRegionList(int regionSetId) throws SQLException {
        Connection c = ConnectionController.connect();
        
        //remove members
        c.createStatement().execute(
                "DELETE FROM " + RegionSetMembershipTable.TABLENAME
                + " WHERE " + RegionSetMembershipTable.FIELDNAME_REGIONSETID + "=" + regionSetId);
        
        //remove from region table
        c.createStatement().execute(
                "DELETE FROM " + RegionSetTable.TABLENAME
                + " WHERE " + RegionSetTable.FIELDNAME_ID + "=" + regionSetId);
        
    }
    
    public static List<RegionSet> getRegionSets() throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT *"
                + " FROM " + RegionSetTable.TABLENAME);
        List<RegionSet> result = new ArrayList<RegionSet>();
        while(rs.next()){
            result.add(new RegionSet(rs.getInt(RegionSetTable.FIELDNAME_ID), rs.getString(RegionSetTable.FIELDNAME_NAME)));
        }
        return result;
    }
    
    public static int getNumberRegions(int regionSetId) throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT COUNT(*)"
                + " FROM " + RegionSetMembershipTable.TABLENAME
                + " WHERE " + RegionSetMembershipTable.FIELDNAME_REGIONSETID + "=" + regionSetId);
        rs.next();
        return rs.getInt(1);
    }

    public static List<String> getRegionNamesInRegionSet(int regionSetId, int limit) throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT " + RegionSetMembershipTable.FIELDNAME_DESCRIPTION
                + " FROM " + RegionSetMembershipTable.TABLENAME
                + " WHERE " + RegionSetMembershipTable.FIELDNAME_REGIONSETID + "=" + regionSetId
                + " LIMIT " + limit);
        List<String> result = new ArrayList<String>();
        while(rs.next()){
            result.add(rs.getString(1));
        }
        return result;
    }

    public static List<GenomicRegion> getRegionsInRegionSet(int regionSetId) throws SQLException {
        Connection c = ConnectionController.connect();
        
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT *"
                + " FROM " + RegionSetMembershipTable.TABLENAME
                + " WHERE " + RegionSetMembershipTable.FIELDNAME_REGIONSETID + "=" + regionSetId);
        
        List<GenomicRegion> result = new ArrayList<GenomicRegion>();
        while(rs.next()){
            result.add(new GenomicRegion(
                    rs.getString(RegionSetMembershipTable.FIELDNAME_CHROM), 
                    new Range(rs.getDouble(RegionSetMembershipTable.FIELDNAME_START), rs.getDouble(RegionSetMembershipTable.FIELDNAME_END))));
        }
        return result;
    }
    
}
