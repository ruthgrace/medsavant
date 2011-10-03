package org.ut.biolab.medsavant.db.util.query;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.ut.biolab.medsavant.db.log.DBLogger;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;
import org.ut.biolab.medsavant.db.util.DBUtil;
import org.xml.sax.SAXException;

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
                + "ON " + ann + ".`reference_id` = " + ref + ".`reference_id`");

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

    public static Annotation getAnnotation(int annotation_id) throws SQLException {

        Connection conn = ConnectionController.connect();

        String ref = DBSettings.TABLENAME_REFERENCE;
        String ann = DBSettings.TABLENAME_ANNOTATION;

        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM `" + ann + "` "
                + "LEFT JOIN `" + ref + "` "
                + "ON " + ann + ".`reference_id` = " + ref + ".`reference_id` "
                + "WHERE annotation_id=" + annotation_id);


        rs.next();
        Annotation result = new Annotation(
                rs.getInt("annotation_id"),
                rs.getString("program"),
                rs.getString("version"),
                rs.getString("name"),
                rs.getString("path"),
                rs.getString("format"));

        return result;
    }

    public static int[] getAnnotationIds(int projectId, int referenceId) throws SQLException {

        Connection conn = ConnectionController.connect();

        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT annotation_ids FROM " + DBSettings.TABLENAME_VARIANTTABLEINFO + 
                " WHERE project_id=" + projectId + " AND reference_id=" + referenceId);

        rs.next();
        String annotationString = rs.getString("annotation_ids");

        if (annotationString == null || annotationString.isEmpty()) {
            return new int[0];
        }

        String[] split = annotationString.split(",");
        int[] result = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            result[i] = Integer.parseInt(split[i]);
        }

        return result;
    }
    
    /*public static AnnotationFormat getAnnotationFormat(int annotationId) throws SQLException, IOException, ParserConfigurationException, SAXException {
        Connection conn = ConnectionController.connect();
        
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT format FROM " + DBSettings.TABLENAME_ANNOTATION + " WHERE annotation_id=" + annotationId);
        
        rs.next();
        return new AnnotationFormat(rs.getString("format"));
    }*/
    
    public static AnnotationFormat getAnnotationFormat(int annotationId) throws SQLException, IOException, ParserConfigurationException, SAXException {
        
        Connection conn = ConnectionController.connect();
        
        ResultSet rs1 = conn.createStatement().executeQuery(
                "SELECT * " + 
                "FROM " + DBSettings.TABLENAME_ANNOTATION + " " + 
                "WHERE annotation_id=" + annotationId + ";");
        rs1.next();
        
        String program = rs1.getString("program");
        String version = rs1.getString("version");
        int referenceId = rs1.getInt("reference_id");
        String path = rs1.getString("path");
        boolean hasRef = rs1.getBoolean("hasRef");
        boolean hasAlt = rs1.getBoolean("hasAlt");
        
        
        ResultSet rs2 = conn.createStatement().executeQuery(
                "SELECT * " + 
                "FROM " + DBUtil.getAnnotationFormatTableName(annotationId) + " " +
                "ORDER BY `position`");
        
        List<AnnotationField> fields = new ArrayList<AnnotationField>();
        while(rs2.next()){
            fields.add(new AnnotationField(
                    rs2.getString("column_name"), 
                    rs2.getString("column_type"), 
                    rs2.getBoolean("filterable"), 
                    rs2.getString("alias"), 
                    rs2.getString("description")));
        }

        return new AnnotationFormat(program, version, referenceId, path, hasRef, hasAlt, fields);
    }
    
       
    public static int addAnnotation(String program, String version, int referenceid, String path, String format) throws SQLException {
        
        DBLogger.log("Adding annotation...");
        
        String q = "INSERT INTO " 
                + DBSettings.TABLENAME_ANNOTATION 
                + " VALUES (null,'" + program + "','" + version + "'," + referenceid + ",'" + path + "','" + format + "')";
        PreparedStatement stmt = (ConnectionController.connect(DBSettings.DBNAME)).prepareStatement(q,
                Statement.RETURN_GENERATED_KEYS);

        stmt.execute();
        ResultSet res = stmt.getGeneratedKeys();
        res.next();

        int annotid = res.getInt(1);

        return annotid;
    }
    
}
