package org.ut.biolab.medsavant.db.util.query;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.ComboCondition;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.OrderObject.Dir;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import org.ut.biolab.medsavant.db.model.Annotation;
import org.ut.biolab.medsavant.db.format.AnnotationField;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.ut.biolab.medsavant.db.format.AnnotationFormat;
import org.ut.biolab.medsavant.db.log.DBLogger;
import org.ut.biolab.medsavant.db.table.ReferenceTable;
import org.ut.biolab.medsavant.db.table.VariantMapTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;
import org.ut.biolab.medsavant.db.util.DBSettings;
import org.ut.biolab.medsavant.db.format.AnnotationFormat.AnnotationType;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.AnnotationTableSchema;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.AnnotationformatTableSchema;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.ReferenceTableSchema;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.VarianttablemapTableSchema;
import org.ut.biolab.medsavant.db.model.structure.TableSchema;
import org.ut.biolab.medsavant.db.table.AnnotationFormatTable;
import org.xml.sax.SAXException;

/**
 *
 * @author mfiume
 */
public class AnnotationQueryUtil {

    public static List<Annotation> getAnnotations() throws SQLException {
        
        TableSchema refTable = MedSavantDatabase.ReferenceTableSchema;
        TableSchema annTable = MedSavantDatabase.AnnotationTableSchema;
        
        SelectQuery query = new SelectQuery();
        query.addFromTable(annTable.getTable());
        query.addAllColumns();
        query.addJoin(
                SelectQuery.JoinType.LEFT_OUTER, 
                annTable.getTable(), 
                refTable.getTable(), 
                BinaryCondition.equalTo(
                    annTable.getDbColumn(AnnotationTableSchema.COLUMNNAME_OF_REFERENCE_ID), 
                    refTable.getDbColumn(ReferenceTableSchema.COLUMNNAME_OF_REFERENCE_ID)));
        
        ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString());

        List<Annotation> results = new ArrayList<Annotation>();

        while (rs.next()) {
            results.add(new Annotation(
                    rs.getInt(AnnotationTableSchema.COLUMNNAME_OF_ANNOTATION_ID),
                    rs.getString(AnnotationTableSchema.COLUMNNAME_OF_PROGRAM),
                    rs.getString(AnnotationTableSchema.COLUMNNAME_OF_VERSION),
                    rs.getString(ReferenceTable.FIELDNAME_NAME),
                    rs.getString(AnnotationTableSchema.COLUMNNAME_OF_PATH),
                    AnnotationFormat.intToAnnotationType(rs.getInt(AnnotationTableSchema.COLUMNNAME_OF_TYPE))));
        }

        return results;
    }

    public static Annotation getAnnotation(int annotation_id) throws SQLException {

        TableSchema refTable = MedSavantDatabase.ReferenceTableSchema;
        TableSchema annTable = MedSavantDatabase.AnnotationTableSchema;
        
        SelectQuery query = new SelectQuery();
        query.addFromTable(annTable.getTable());
        query.addAllColumns();
        query.addJoin(
                SelectQuery.JoinType.LEFT_OUTER, 
                annTable.getTable(), 
                refTable.getTable(), 
                BinaryCondition.equalTo(
                    annTable.getDbColumn(AnnotationTableSchema.COLUMNNAME_OF_REFERENCE_ID), 
                    refTable.getDbColumn(ReferenceTableSchema.COLUMNNAME_OF_REFERENCE_ID)));
        query.addCondition(BinaryCondition.equalTo(annTable.getDbColumn(AnnotationTableSchema.COLUMNNAME_OF_ANNOTATION_ID), annotation_id));
        
        ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString());

        rs.next();
        Annotation result = new Annotation(
                    rs.getInt(AnnotationTableSchema.COLUMNNAME_OF_ANNOTATION_ID),
                    rs.getString(AnnotationTableSchema.COLUMNNAME_OF_PROGRAM),
                    rs.getString(AnnotationTableSchema.COLUMNNAME_OF_VERSION),
                    rs.getString(ReferenceTable.FIELDNAME_NAME),
                    rs.getString(AnnotationTableSchema.COLUMNNAME_OF_PATH),
                    AnnotationFormat.intToAnnotationType(rs.getInt(AnnotationTableSchema.COLUMNNAME_OF_TYPE)));

        return result;
    }

    public static int[] getAnnotationIds(int projectId, int referenceId) throws SQLException {
        
        TableSchema table = MedSavantDatabase.VarianttablemapTableSchema;
        SelectQuery query = new SelectQuery();
        query.addFromTable(table.getTable());
        query.addColumns(table.getDbColumn(VarianttablemapTableSchema.COLUMNNAME_OF_ANNOTATION_IDS));
        query.addCondition(ComboCondition.and(
                BinaryCondition.equalTo(table.getDbColumn(VarianttablemapTableSchema.COLUMNNAME_OF_PROJECT_ID), projectId),
                BinaryCondition.equalTo(table.getDbColumn(VarianttablemapTableSchema.COLUMNNAME_OF_REFERENCE_ID), referenceId)));
        
        
        String a = query.toString();
        ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString());
        
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
        
        TableSchema annTable = MedSavantDatabase.AnnotationTableSchema;
        SelectQuery query1 = new SelectQuery();
        query1.addFromTable(annTable.getTable());
        query1.addAllColumns();
        query1.addCondition(BinaryCondition.equalTo(annTable.getDbColumn(AnnotationTableSchema.COLUMNNAME_OF_ANNOTATION_ID), annotationId));
        
        ResultSet rs1 = ConnectionController.connect().createStatement().executeQuery(query1.toString());

        rs1.next();
        
        String program = rs1.getString(AnnotationTableSchema.COLUMNNAME_OF_PROGRAM);
        String version = rs1.getString(AnnotationTableSchema.COLUMNNAME_OF_VERSION);
        int referenceId = rs1.getInt(AnnotationTableSchema.COLUMNNAME_OF_REFERENCE_ID);
        String path = rs1.getString(AnnotationTableSchema.COLUMNNAME_OF_PATH);
        boolean hasRef = rs1.getBoolean(AnnotationTableSchema.COLUMNNAME_OF_HAS_REF);
        boolean hasAlt = rs1.getBoolean(AnnotationTableSchema.COLUMNNAME_OF_HAS_ALT);
        AnnotationType type = AnnotationFormat.intToAnnotationType(rs1.getInt(AnnotationTableSchema.COLUMNNAME_OF_TYPE));
        
        
        TableSchema annFormatTable = MedSavantDatabase.AnnotationformatTableSchema;
        SelectQuery query2 = new SelectQuery();
        query2.addFromTable(annFormatTable.getTable());
        query2.addAllColumns();
        query2.addCondition(BinaryCondition.equalTo(annFormatTable.getDbColumn(AnnotationformatTableSchema.COLUMNNAME_OF_ANNOTATION_ID), annotationId));
        query2.addOrdering(annFormatTable.getDbColumn(AnnotationformatTableSchema.COLUMNNAME_OF_POSITION), Dir.ASCENDING);
        
        ResultSet rs2 = ConnectionController.connect().createStatement().executeQuery(query2.toString());

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
    
       
    public static int addAnnotation(String program, String version, int referenceid, String path, String format, boolean hasRef, boolean hasAlt, int type) throws SQLException {
        
        DBLogger.log("Adding annotation...");
        
        TableSchema table = MedSavantDatabase.AnnotationTableSchema;
        InsertQuery query = new InsertQuery(table.getTable());
        query.addColumn(table.getDbColumn(AnnotationTableSchema.COLUMNNAME_OF_PROGRAM), program);
        query.addColumn(table.getDbColumn(AnnotationTableSchema.COLUMNNAME_OF_VERSION), version);
        query.addColumn(table.getDbColumn(AnnotationTableSchema.COLUMNNAME_OF_REFERENCE_ID), referenceid);
        query.addColumn(table.getDbColumn(AnnotationTableSchema.COLUMNNAME_OF_PATH), path);
        query.addColumn(table.getDbColumn(AnnotationTableSchema.COLUMNNAME_OF_HAS_REF), hasRef);
        query.addColumn(table.getDbColumn(AnnotationTableSchema.COLUMNNAME_OF_HAS_ALT), hasAlt);
        query.addColumn(table.getDbColumn(AnnotationTableSchema.COLUMNNAME_OF_TYPE), type);

        PreparedStatement stmt = (ConnectionController.connect(DBSettings.DBNAME)).prepareStatement(
                query.toString(),
                Statement.RETURN_GENERATED_KEYS);

        stmt.execute();
        ResultSet res = stmt.getGeneratedKeys();
        res.next();

        int annotid = res.getInt(1);

        return annotid;
    }
    
}
