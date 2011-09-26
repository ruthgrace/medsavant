/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ut.biolab.medsavant.db.util.jobject.LogQueryUtil;

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
                LogQueryUtil.checkAndUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(PeriodicUpdate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PeriodicUpdate.class.getName()).log(Level.SEVERE, null, ex);
            }
            Thread.sleep(period);
        }
        
    }
    
}
