/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.server;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import org.ut.biolab.medsavant.db.util.query.ServerLogQueryUtil;
import org.ut.biolab.medsavant.db.util.query.ServerLogQueryUtil.LogType;
import org.ut.biolab.medsavant.server.worker.AnnotationWorker;
import org.ut.biolab.medsavant.server.worker.PhoneHomeWorker;
import org.ut.biolab.medsavant.server.worker.WorkerChecker;

/**
 *
 * @author mfiume
 */
public class MedSavantServerUtility {

    private static final Object lock = new Object();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        ServerLogQueryUtil.addServerLog(LogType.INFO, "Server booted");
        
        PhoneHomeWorker phoneHomeSwingWorker = new PhoneHomeWorker();
        phoneHomeSwingWorker.execute();
        
        AnnotationWorker annotationSwingWorker = new AnnotationWorker();
        annotationSwingWorker.execute();
        
        List<SwingWorker> workers = new ArrayList<SwingWorker>();
        workers.add(phoneHomeSwingWorker);
        workers.add(annotationSwingWorker);
        
        WorkerChecker workerChecker = new WorkerChecker(workers);
        workerChecker.execute();
        
        synchronized (lock) {
            try { lock.wait() ; }
            catch (Exception e)  {}
        }
        // TODO code application logic here
    }
}
