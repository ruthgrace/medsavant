/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db;

import java.sql.SQLException;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;
import org.ut.biolab.medsavant.db.util.jobject.ProjectQueryUtil;
import org.ut.biolab.medsavant.db.util.jobject.UserQueryUtil;

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
        
        UserQueryUtil.addUser("mfiume","", true);
        UserQueryUtil.addUser("nadmin","pass", false);
        int refid = manage.addReference("hg19");
        int projectid = manage.addProject("Autism Genome Project");
        manage.addAnnotation("GATK", "1.0", refid, "/data/blah", "fieldname1:type1|fieldname2:type2");
        manage.addAnnotation("GATK", "2.0", refid, "/data/blah", "fieldname1:type1|fieldname2:type2");
        manage.addAnnotation("SIFT", "1.0", refid, "/data/blah", "fieldname1:type1|fieldname2:type2");
        
        ProjectQueryUtil.createVariantTable(projectid, refid);
        ProjectQueryUtil.createVariantTable(projectid, manage.addReference("hg20"));
        ProjectQueryUtil.createVariantTable(projectid, manage.addReference("hg21"));
        ProjectQueryUtil.createVariantTable(projectid, manage.addReference("hg22"));
        ProjectQueryUtil.createVariantTable(projectid, manage.addReference("hg23"));
        ProjectQueryUtil.createVariantTable(projectid, manage.addReference("hg24"));
        
        manage.setAnnotations(projectid,refid,"1,2");
        
        //manage.removeProject(projectid);
        
        manage.addProject("FORGE");
        
    }
}
