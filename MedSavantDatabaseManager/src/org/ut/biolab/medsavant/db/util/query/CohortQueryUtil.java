/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util.query;

import org.ut.biolab.medsavant.db.util.Cohort;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.table.CohortMembershipTable;
import org.ut.biolab.medsavant.db.table.CohortTable;
import org.ut.biolab.medsavant.db.table.PatientInfoTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;

/**
 *
 * @author Andrew
 */
public class CohortQueryUtil {
    
    public static List<Integer> getIndividualsInCohort(int cohortId) throws SQLException {
        
        Connection c = ConnectionController.connect();
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT patient_id" +
                " FROM " + CohortMembershipTable.TABLENAME + 
                " WHERE cohort_id=" + cohortId);
        
        List<Integer> result = new ArrayList<Integer>();
        while(rs.next()){
            result.add(rs.getInt(1));
        }
        return result;
    }
    
    public static List<String> getDNAIdsInCohort(int cohortId) throws SQLException {

        Connection c = ConnectionController.connect();
        
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT patient_tablename"
                + " FROM " + PatientInfoTable.TABLENAME + " t0, " + CohortTable.TABLENAME + " t1"
                + " WHERE t1.cohort_id=" + cohortId + " AND t1.project_id=t0.project_id");
        rs.next();
        String patientTablename = rs.getString(1);
        
        rs = c.createStatement().executeQuery(
                "SELECT active_dna_id"
                + " FROM " + CohortMembershipTable.TABLENAME + " t0, " + patientTablename + " t1"
                + " WHERE t0.cohort_id=" + cohortId + " AND t0.patient_id=t1.patient_id");
        List<String> result = new ArrayList<String>();
        while(rs.next()){
            result.add(rs.getString(1));
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
                        + " (cohort_id, patient_id) VALUES (" + cohortId + "," + id + ")");
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
                    + " WHERE cohort_id=" + cohortId + " AND patient_id=" + id);
        }
 
        c.commit();
        c.setAutoCommit(true);
    }
    
    public static List<Cohort> getCohorts(int projectId) throws SQLException {
        
        Connection c = ConnectionController.connect();
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT * FROM " + CohortTable.TABLENAME + 
                " WHERE project_id=" + projectId);
        
        List<Cohort> result = new ArrayList<Cohort>();
        while(rs.next()){
            result.add(new Cohort(rs.getInt("cohort_id"), rs.getString("name")));
        }
        return result;
    }

    public static void addCohort(int projectId, String name) throws SQLException {
        
        Connection c = ConnectionController.connect();
        c.createStatement().executeUpdate(
                "INSERT INTO " + CohortTable.TABLENAME + 
                " (project_id, `name`) VALUES (" + projectId + ",'" + name + "')");
    }
    
    public static void removeCohort(int cohortId) throws SQLException {
        
        Connection c = ConnectionController.connect();
        
        //remove all entries from membership
        c.createStatement().executeUpdate(
                "DELETE FROM " + CohortMembershipTable.TABLENAME + 
                " WHERE cohort_id=" + cohortId);
        
        //remove from cohorts
        c.createStatement().executeUpdate(
                "DELETE FROM " + CohortTable.TABLENAME + 
                " WHERE cohort_id=" + cohortId);
    }
    
    public static void removeCohorts(Cohort[] cohorts) throws SQLException {
        for(Cohort c : cohorts){
            removeCohort(c.getId());
        }
    }
    
    public static List<Integer> getCohortIds(int projectId) throws SQLException {
        
        Connection c = ConnectionController.connect();
        
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT cohort_id FROM " + CohortTable.TABLENAME + 
                " WHERE project_id=" + projectId);
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
                    " WHERE cohort_id=" + cohortId + " AND patient_id=" + patientId);
        }
    }

}
