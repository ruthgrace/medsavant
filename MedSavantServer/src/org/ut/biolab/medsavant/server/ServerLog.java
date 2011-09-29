package org.ut.biolab.medsavant.server;

import java.util.Date;
import org.ut.biolab.medsavant.server.mail.Mail;

/**
 *
 * @author mfiume
 */
public class ServerLog {
    
    private static String logPath = "server.log";
    private static String emailaddress;

    public static void log(String string) {
        log(string,LogType.INFO);
    }
 
    public enum LogType { INFO, ERROR, WARNING };
    
    public static void setMailRecipient(String eaddress) {
        emailaddress = eaddress;
    }

    public static void logByEmail(String subject, String message) {
        if (emailaddress != null)
            Mail.sendEmail(emailaddress, subject, message);
    }
    
    public static void log(String msg, LogType type) {
        System.out.println(type.toString() + " [ " + now() + " ] " + msg);
    }
    
    private static String now() {
        return (new Date()).toString();
    }
    
}
