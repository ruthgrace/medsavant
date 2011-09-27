/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util;

import java.io.File;
import java.sql.SQLException;
import org.ut.biolab.medsavant.db.util.jobject.LogQueryUtil;
import org.ut.biolab.medsavant.db.util.jobject.LogQueryUtil.Action;
import org.ut.biolab.medsavant.db.util.jobject.ProjectQueryUtil;

/**
 *
 * @author Andrew
 */
public class ImportVariantSet {
    
    public static void performImport(File variantsTdf, int projectId, int referenceId) throws SQLException {

        //add log
        int updateId = LogQueryUtil.addLogEntry(projectId, referenceId, Action.ADD_VARIANTS);
        
        //create the staging table       
        try {
            ProjectQueryUtil.createVariantTable(projectId, referenceId, updateId, null, true, false);
        } catch (SQLException ex) {
            //table already exists?
        }
           
        //add variants to table
        String tableName = DBUtil.getVariantStagingTableName(projectId, referenceId, updateId);
        DBUtil.uploadFileToVariantTable(variantsTdf, tableName); 

        //delete temp file
        variantsTdf.delete();

        //set log as pending
        LogQueryUtil.setLogPending(updateId, true);
        
    }
 
}
