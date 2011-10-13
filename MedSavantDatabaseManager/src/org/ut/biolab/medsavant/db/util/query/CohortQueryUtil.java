/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util.query;

import org.ut.biolab.medsavant.db.model.Cohort;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.table.CohortMembershipTable;
import org.ut.biolab.medsavant.db.table.CohortTable;
import org.ut.biolab.medsavant.db.table.PatientMapTable;
import org.ut.biolab.medsavant.db.table.PatientTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;

/**
 *
 * @author Andrew
 */
public class CohortQueryUtil {
    
    public static List<Integer> getIndividualsInCohort(int cohortId) throws SQLException {
               
        Connection c = ConnectionController.connect();
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT " + CohortMembershipTable.FIELDNAME_PATIENTID + 
                " FROM " + CohortMembershipTable.TABLENAME + 
                " WHERE " + CohortMembershipTable.FIELDNAME_COHORTID + "=" + cohortId);
        
        List<Integer> result = new ArrayList<Integer>();
        while(rs.next()){
            result.add(rs.getInt(1));
        }
        return result;
    }
    
    public static List<String> getDNAIdsInCohort(int cohortId) throws SQLException {

        Connection c = ConnectionController.connect();
        
        //get patient tablename
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT " + PatientMapTable.FIELDNAME_PATIENTTABLENAME
                + " FROM " + PatientMapTable.TABLENAME + " t0, " + CohortTable.TABLENAME + " t1"
                + " WHERE t1." + CohortTable.FIELDNAME_ID + "=" + cohortId 
                + " AND t1." + CohortTable.FIELDNAME_PROJECTID + "=t0." + PatientMapTable.FIELDNAME_PROJECTID);
        rs.next();
        String patientTablename = rs.getString(1);
        
        //get dna id lists
        rs = c.createStatement().executeQuery(
                "SELECT " + PatientTable.FIELDNAME_DNAIDS
                + " FROM " + CohortMembershipTable.TABLENAME + " t0, " + patientTablename + " t1"
                + " WHERE t0." + CohortMembershipTable.FIELDNAME_COHORTID + "=" + cohortId 
                + " AND t0." + CohortMembershipTable.FIELDNAME_PATIENTID + "=t1." + PatientTable.FIELDNAME_ID);
        List<String> result = new ArrayList<String>();
        while(rs.next()){          
            String[] dnaIds = rs.getString(1).split(",");
            for(String id : dnaIds){
                if(!result.contains(id)){
                    result.add(id);
                }
            }
        }
        return result;
    }
    
    public static void addPatientsToCohort(int[] patientIds, int cohortId) throws SQLException {
        
        Connection c = ConnectionController.connect();
        c.setAutoCommit(false);
        
        for(int id : patientIds){
            try {
                c.createStatement().executeUpdate(
                        "INSERT INTO " + CohortMembershipTable.TABLENAME
                        + " (" + CohortMembershipTable.FIELDNAME_COHORTID + ", " + CohortMembershipTable.FIELDNAME_PATIENTID + ")"
                        + " VALUES (" + cohortId + "," + id + ")");
            } catch (MySQLIntegrityConstraintViolationException e){
                //duplicate entry, ignore
            }
        }
 
        c.commit();
        c.setAutoCommit(true);
    }
    
    public static void removePatientsFromCohort(int[] patientIds, int cohortId) throws SQLException {
        
        Connection c = ConnectionController.connect();
        c.setAutoCommit(false);
        
        for(int id : patientIds){
            c.createStatement().executeUpdate(
                    "DELETE FROM " + CohortMembershipTable.TABLENAME
                    + " WHERE " + CohortMembershipTable.FIELDNAME_COHORTID + "=" + cohortId 
                    + " AND " + CohortMembershipTable.FIELDNAME_PATIENTID + "=" + id);
        }
 
        c.commit();
        c.setAutoCommit(true);
    }
    
    public static List<Cohort> getCohorts(int projectId) throws SQLException {
        
        Connection c = ConnectionController.connect();
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT * FROM " + CohortTable.TABLENAME + 
                " WHERE " + CohortTable.FIELDNAME_PROJECTID + "=" + projectId);
        
        List<Cohort> result = new ArrayList<Cohort>();
        while(rs.next()){
            result.add(new Cohort(rs.getInt(CohortTable.FIELDNAME_ID), rs.getString(CohortTable.FIELDNAME_NAME)));
        }
        return result;
    }

    public static void addCohort(int projectId, String name) throws SQLException {
        
        Connection c = ConnectionController.connect();
        c.createStatement().executeUpdate(
                "INSERT INTO " + CohortTable.TABLENAME + 
                " (" + CohortTable.FIELDNAME_PROJECTID + ", `" + CohortTable.FIELDNAME_NAME + "`)"
                + " VALUES (" + projectId + ",'" + name + "')");
    }
    
    public static void removeCohort(int cohortId) throws SQLException {
        
        Connection c = ConnectionController.connect();
        
        //remove all entries from membership
        c.createStatement().executeUpdate(
                "DELETE FROM " + CohortMembershipTable.TABLENAME + 
                " WHERE " + CohortMembershipTable.FIELDNAME_COHORTID + "=" + cohortId);
        
        //remove from cohorts
        c.createStatement().executeUpdate(
                "DELETE FROM " + CohortTable.TABLENAME + 
                " WHERE " + CohortTable.FIELDNAME_ID + "=" + cohortId);
    }
    
    public static void removeCohorts(Cohort[] cohorts) throws SQLException {
        for(Cohort c : cohorts){
            removeCohort(c.getId());
        }
    }
    
    public static List<Integer> getCohortIds(int projectId) throws SQLException {
        
        Connection c = ConnectionController.connect();
        
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT " + CohortTable.FIELDNAME_ID + 
                " FROM " + CohortTable.TABLENAME + 
                " WHERE " + CohortTable.FIELDNAME_PROJECTID + "=" + projectId);
        List<Integer> result = new ArrayList<Integer>();
        while(rs.next()){
            result.add(rs.getInt(1));
        }
        return result;
    }
    
    public static void removePatientReferences(int projectId, int patientId) throws SQLException {
        
        List<Integer> cohortIds = getCohortIds(projectId);
        
        Connection c = ConnectionController.connect();
        for(Integer cohortId : cohortIds){
            c.createStatement().executeUpdate(
                    "DELETE FROM " + CohortMembershipTable.TABLENAME +
                    " WHERE " + CohortMembershipTable.FIELDNAME_COHORTID + "=" + cohortId + 
                    " AND " + CohortMembershipTable.FIELDNAME_PATIENTID + "=" + patientId);
        }
    }

}
