package org.ut.biolab.medsavant.db.util.query;

import org.ut.biolab.medsavant.db.util.Annotation;
import org.ut.biolab.medsavant.db.format.AnnotationField;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.ut.biolab.medsavant.db.format.AnnotationFormat;
import org.ut.biolab.medsavant.db.log.DBLogger;
import org.ut.biolab.medsavant.db.table.AnnotationTable;
import org.ut.biolab.medsavant.db.table.AnnotationMapTable;
import org.ut.biolab.medsavant.db.table.ReferenceTable;
import org.ut.biolab.medsavant.db.table.VariantMapTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;
import org.ut.biolab.medsavant.db.format.AnnotationFormat.AnnotationType;
import org.ut.biolab.medsavant.db.table.AnnotationFormatTable;
import org.xml.sax.SAXException;

/**
 *
 * @author mfiume
 */
public class AnnotationQueryUtil {

    public static List<Annotation> getAnnotations() throws SQLException {

        Connection conn = ConnectionController.connect();
     
        String ref = ReferenceTable.TABLENAME;
        String ann = AnnotationTable.TABLENAME;

        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * "
                + "FROM `" + ann + "` "
                + "LEFT JOIN `" + ref + "` "
                + "ON " + ann + ".`" + AnnotationTable.FIELDNAME_REFERENCEID + "` = " + ref + ".`" + ReferenceTable.FIELDNAME_ID + "`");

        List<Annotation> results = new ArrayList<Annotation>();

        while (rs.next()) {
            results.add(new Annotation(
                    rs.getInt(AnnotationTable.FIELDNAME_ID),
                    rs.getString(AnnotationTable.FIELDNAME_PROGRAM),
                    rs.getString(AnnotationTable.FIELDNAME_VERSION),
                    rs.getString(ReferenceTable.FIELDNAME_NAME),
                    rs.getString(AnnotationTable.FIELDNAME_PATH)));
        }

        return results;
    }

    public static Annotation getAnnotation(int annotation_id) throws SQLException {

        Connection conn = ConnectionController.connect();

        String ref = ReferenceTable.TABLENAME;
        String ann = AnnotationTable.TABLENAME;

        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * "
                + "FROM `" + ann + "` "
                + "LEFT JOIN `" + ref + "` "
                + "ON " + ann + ".`" + AnnotationTable.FIELDNAME_REFERENCEID + "` = " + ref + ".`" + ReferenceTable.FIELDNAME_ID + "` "
                + "WHERE " + AnnotationTable.FIELDNAME_ID + "=" + annotation_id);


        rs.next();
        Annotation result = new Annotation(
                    rs.getInt(AnnotationTable.FIELDNAME_ID),
                    rs.getString(AnnotationTable.FIELDNAME_PROGRAM),
                    rs.getString(AnnotationTable.FIELDNAME_VERSION),
                    rs.getString(ReferenceTable.FIELDNAME_NAME),
                    rs.getString(AnnotationTable.FIELDNAME_PATH));

        return result;
    }

    public static int[] getAnnotationIds(int projectId, int referenceId) throws SQLException {

        Connection conn = ConnectionController.connect();

        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT " + VariantMapTable.FIELDNAME_ANNOTATIONIDS + 
                " FROM " + VariantMapTable.TABLENAME + 
                " WHERE " + VariantMapTable.FIELDNAME_PROJECTID + "=" + projectId + 
                " AND " + VariantMapTable.FIELDNAME_REFERENCEID + "=" + referenceId);

        rs.next();
        String annotationString = rs.getString(VariantMapTable.FIELDNAME_ANNOTATIONIDS);

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

    public static AnnotationFormat getAnnotationFormat(int annotationId) throws SQLException, IOException, ParserConfigurationException, SAXException {
        
        Connection conn = ConnectionController.connect();
        
        ResultSet rs1 = conn.createStatement().executeQuery(
                "SELECT * " + 
                "FROM " + AnnotationTable.TABLENAME + " " + 
                "WHERE " + AnnotationTable.FIELDNAME_ID + "=" + annotationId + ";");
        rs1.next();
        
        String program = rs1.getString(AnnotationTable.FIELDNAME_PROGRAM);
        String version = rs1.getString(AnnotationTable.FIELDNAME_VERSION);
        int referenceId = rs1.getInt(AnnotationTable.FIELDNAME_REFERENCEID);
        String path = rs1.getString(AnnotationTable.FIELDNAME_PATH);
        boolean hasRef = rs1.getBoolean(AnnotationTable.FIELDNAME_HASREF);
        boolean hasAlt = rs1.getBoolean(AnnotationTable.FIELDNAME_HASALT);
        AnnotationType type = AnnotationFormat.intToAnnotationType(rs1.getInt(AnnotationTable.FIELDNAME_TYPE));
        
        
        ResultSet rs2 = conn.createStatement().executeQuery(
                "SELECT * " + 
                "FROM " + getAnnotationFormatTableName(annotationId) + " " +
                "ORDER BY `" + AnnotationFormatTable.FIELDNAME_POSITION + "`");
        
        List<AnnotationField> fields = new ArrayList<AnnotationField>();
        while(rs2.next()){
            fields.add(new AnnotationField(
                    rs2.getString(AnnotationFormatTable.FIELDNAME_COLUMNNAME), 
                    rs2.getString(AnnotationFormatTable.FIELDNAME_COLUMNTYPE), 
                    rs2.getBoolean(AnnotationFormatTable.FIELDNAME_FILTERABLE), 
                    rs2.getString(AnnotationFormatTable.FIELDNAME_ALIAS), 
                    rs2.getString(AnnotationFormatTable.FIELDNAME_DESCRIPTION)));
        }

        return new AnnotationFormat(program, version, referenceId, path, hasRef, hasAlt, type, fields);
    }
    
       
    public static int addAnnotation(String program, String version, int referenceid, String path, String format) throws SQLException {
        
        DBLogger.log("Adding annotation...");
        
        String q = "INSERT INTO " + AnnotationTable.TABLENAME 
                + " VALUES (null,'" + program + "','" + version + "'," + referenceid + ",'" + path + "','" + format + "')";
        PreparedStatement stmt = (ConnectionController.connect(DBSettings.DBNAME)).prepareStatement(q,
                Statement.RETURN_GENERATED_KEYS);

        stmt.execute();
        ResultSet res = stmt.getGeneratedKeys();
        res.next();

        int annotid = res.getInt(1);

        return annotid;
    }
    
    public static String getAnnotationFormatTableName(int annotationId) throws SQLException {
        Connection c = ConnectionController.connect();
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT " + AnnotationMapTable.FIELDNAME_TABLENAME 
                + " FROM `" + AnnotationMapTable.TABLENAME + "`"
                + " WHERE " + AnnotationMapTable.FIELDNAME_ID + "=" + annotationId);
        rs.next();
        return rs.getString(1);
    }
    
}
