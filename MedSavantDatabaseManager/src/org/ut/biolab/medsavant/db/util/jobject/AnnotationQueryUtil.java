package org.ut.biolab.medsavant.db.util.jobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;

/**
 *
 * @author mfiume
 */
public class AnnotationQueryUtil { 
    
    public static List<Annotation> getAnnotations() throws SQLException {
        
        Connection conn = ConnectionController.connect();
        
        String ref = DBSettings.TABLENAME_REFERENCE;
        String ann = DBSettings.TABLENAME_ANNOTATION;
        
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
    
    public static int[] getAnnotationIds(int projectId, int referenceId) throws SQLException{
        
        Connection conn = ConnectionController.connect();
        
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT annotation_ids FROM " + DBSettings.TABLENAME_VARIANTTABLEINFO);
        
        rs.next();
        String annotationString = rs.getString("annotation_ids");
        
        if(annotationString == null || annotationString.isEmpty()){
            return new int[0];
        }
        
        String[] split = annotationString.split(",");
        int[] result = new int[split.length];
        for(int i = 0; i < split.length; i++){
            result[i] = Integer.parseInt(split[i]);
        }
        
        return result;
    }
}
