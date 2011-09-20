package org.ut.biolab.medsavant.db.util.jobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.util.ConnectionController;

/**
 *
 * @author mfiume
 */
public class AnnotationQueryUtil { 
    
    public static List<Annotation> getAnnotations() throws SQLException {
        
        Connection conn = ConnectionController.connect();
        
        String ref = org.ut.biolab.medsavant.db.util.DBSettings.TABLENAME_REFERENCE;
        String ann = org.ut.biolab.medsavant.db.util.DBSettings.TABLENAME_ANNOTATION;
        
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM `" + ann + "` "
                + "LEFT JOIN `" + ref + "` "
                + "ON " + ann + ".`reference_id` = " + ref + ".`reference_id`" 
                );
        
        List<Annotation> results = new ArrayList<Annotation>();
        
        while (rs.next()) {
            results.add(new Annotation(
                    rs.getInt("annotation_id"), 
                    rs.getString("program"), 
                    rs.getString("version"), 
                    rs.getString("name"), 
                    rs.getString("path"),
                    rs.getString("format")));
        }
        
        return results;
    }
}
