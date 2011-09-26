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
import org.ut.biolab.medsavant.db.util.jobject.LogQueryUtil;
import org.ut.biolab.medsavant.db.util.jobject.LogQueryUtil.Action;

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
                ResultSet rs = LogQueryUtil.getPendingUpdates();
                
                
                boolean foundUpdate = false;
                while(rs.next()){
                    foundUpdate = true;
                    int projectId = rs.getInt("project_id");
                    int referenceId = rs.getInt("reference_id");
                    Action action = LogQueryUtil.intToAction(rs.getInt("action"));           
                    switch(action){
                        case ADD_VARIANTS:
                            UpdateVariantTable.performAddVCF(projectId, referenceId);
                            break;
                        case UPDATE_TABLE:
                            UpdateVariantTable.performUpdate(projectId, referenceId);
                            break;
                    }
                }

                //System.out.println(".");
                if(foundUpdate){
                    //System.out.println("\nX");
                    LogQueryUtil.clearLog();
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
