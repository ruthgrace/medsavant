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

        //create the staging table       
        try {
            ProjectQueryUtil.createVariantTable(projectId, referenceId, true, false);
        } catch (SQLException ex) {
            //table already exists?
        }
           
        //add variants to table
        String tableName = ProjectQueryUtil.VARIANT_TABLEINFO_STAGING_PREFIX + "_proj" + projectId + "_ref" + referenceId;
        UpdateVariantTable.uploadFile(variantsTdf, tableName); 

        //delete temp file
        variantsTdf.delete();

        //add log
        LogQueryUtil.addLogEntry(projectId, referenceId, Action.ADD_VARIANTS);
        
    }
 
}
