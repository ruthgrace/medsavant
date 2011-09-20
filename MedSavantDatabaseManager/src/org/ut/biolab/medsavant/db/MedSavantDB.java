/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db;

import java.sql.SQLException;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;

/**
 *
 * @author mfiume
 */
public class MedSavantDB {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        
        Manage manage = new Manage();
        
        int userid = manage.addUser("mfiume","", true);
        manage.addUser("nadmin","pass", false);
        int refid = manage.addReference("hg19");
        int projectid = manage.addProject("Autism Genome Project");
        manage.addAnnotation("GATK", "1.0", refid, "/data/blah", "fieldname1:type1|fieldname2:type2");
        manage.addAnnotation("GATK", "2.0", refid, "/data/blah", "fieldname1:type1|fieldname2:type2");
        manage.addAnnotation("SIFT", "1.0", refid, "/data/blah", "fieldname1:type1|fieldname2:type2");
        
        manage.createVariantTable(projectid, refid);
        manage.createVariantTable(projectid, manage.addReference("hg20"));
        manage.createVariantTable(projectid, manage.addReference("hg21"));
        manage.createVariantTable(projectid, manage.addReference("hg22"));
        manage.createVariantTable(projectid, manage.addReference("hg23"));
        manage.createVariantTable(projectid, manage.addReference("hg24"));
        manage.setAnnotations(projectid,refid,"1,2");
        
        //manage.removeProject(projectid);
        
        manage.addProject("FORGE");
        
    }
}
