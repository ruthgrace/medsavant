/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.admin;

import java.io.File;
import java.sql.SQLException;
import org.ut.biolab.medsavant.db.Manage;
import org.ut.biolab.medsavant.db.util.jobject.ReferenceQueryUtil;

/**
 *
 * @author Andrew
 */
public class ImportAnnotation {
    
    public static void main(String[] args) throws SQLException{
        
        if(args.length != 5) {
            setError("Usage: ImportAnnotation program version reference_name file_path format_path");
        };
        
        String program = args[0];
        String version = args[1];   
        String referenceName = args[2];
        String filePath = args[3];
        String formatPath = args[4];
        
        //get referenceId
        int referenceId = ReferenceQueryUtil.getReferenceId(referenceName);
        if(referenceId == -1){
            setError("Reference " + referenceName + " does not exist");
        }
        
        //ensure files exist
        File annotationFile = new File(filePath);
        File formatFile = new File(formatPath);
        if(!annotationFile.exists()){
            setError("File " + filePath + " does not exist");
        }
        if(!formatFile.exists()){
            setError("File " + formatPath + " does not exist");
        }  
        
        //add annotation
        Manage.addAnnotation(program, version, referenceId, annotationFile.getAbsolutePath(), formatFile.getAbsolutePath());
    }
    
    public static void setError(String errorString){
        System.err.println(errorString);
        System.exit(1);
    }

}
