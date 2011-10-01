/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.server.update;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ut.biolab.medsavant.db.util.query.AnnotationLogQueryUtil;
import org.ut.biolab.medsavant.db.util.query.AnnotationLogQueryUtil.Action;
import org.ut.biolab.medsavant.server.log.ServerLogger;

/**
 *
 * @author Andrew
 */
public class PeriodicUpdate {

    private static int DEFAULT_PERIOD = 60000; //milliseconds (600000 = 10 mins)
    
    public static void main(String[] args) throws InterruptedException{
        
        int period = DEFAULT_PERIOD;
        if(args.length == 1){
            period = Integer.parseInt(args[0]);
        }
        
        while(true){
            try {
                ResultSet rs = AnnotationLogQueryUtil.getPendingUpdates();

                while(rs.next()){

                    int projectId = rs.getInt("project_id");
                    int referenceId = rs.getInt("reference_id");
                    int updateId = rs.getInt("update_id");
                    Action action = AnnotationLogQueryUtil.intToAction(rs.getInt("action")); 
                    
                    AnnotationLogQueryUtil.setAnnotationLogStatus(updateId, AnnotationLogQueryUtil.Status.INPROGRESS);
                    
                    try {
                        switch(action){
                            case ADD_VARIANTS:
                                UpdateVariantTable.performAddVCF(projectId, referenceId, updateId);
                                break;
                            case UPDATE_TABLE:
                                UpdateVariantTable.performUpdate(projectId, referenceId);
                                break;
                        }
                        AnnotationLogQueryUtil.setAnnotationLogStatus(updateId, AnnotationLogQueryUtil.Status.COMPLETE);                      
                    } catch (Exception e){
                        ServerLogger.logByEmail("Uh oh...", "There was a problem making update " + updateId + ". Here's the error message:\n\n" + e.getLocalizedMessage());
                        e.printStackTrace();
                        AnnotationLogQueryUtil.setAnnotationLogStatus(updateId, AnnotationLogQueryUtil.Status.ERROR);
                    }
   
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(PeriodicUpdate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PeriodicUpdate.class.getName()).log(Level.SEVERE, null, ex);
            }
            Thread.sleep(period);
        }
        
    }
    
}
