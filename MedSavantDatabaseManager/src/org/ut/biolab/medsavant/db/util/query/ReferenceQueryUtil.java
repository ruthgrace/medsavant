package org.ut.biolab.medsavant.db.util.query;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ut.biolab.medsavant.db.model.Chromosome;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.AnnotationTableSchema;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.ChromosomeTableSchema;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.ReferenceTableSchema;
import org.ut.biolab.medsavant.db.model.structure.MedSavantDatabase.VarianttablemapTableSchema;
import org.ut.biolab.medsavant.db.model.structure.TableSchema;
import org.ut.biolab.medsavant.db.table.AnnotationTable;
import org.ut.biolab.medsavant.db.table.ChromosomeTable;
import org.ut.biolab.medsavant.db.table.ReferenceTable;
import org.ut.biolab.medsavant.db.table.VariantMapTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;

/**
 *
 * @author mfiume
 */
public class ReferenceQueryUtil { 
    
    public static List<String> getReferenceNames() throws SQLException {
        
        TableSchema table = MedSavantDatabase.ReferenceTableSchema;
        SelectQuery query = new SelectQuery();
        query.addFromTable(table.getTable());
        query.addColumns(table.getDBColumn(ReferenceTableSchema.COLUMNNAME_OF_NAME));
        
        ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString());

        List<String> results = new ArrayList<String>();      
        while (rs.next()) {
            results.add(rs.getString(1));
        }
        
        return results;
    }

    public static int getReferenceId(String refName) throws SQLException {
        
        TableSchema table = MedSavantDatabase.ReferenceTableSchema;
        SelectQuery query = new SelectQuery();
        query.addFromTable(table.getTable());
        query.addColumns(table.getDBColumn(ReferenceTableSchema.COLUMNNAME_OF_REFERENCE_ID));
        query.addCondition(BinaryCondition.equalTo(table.getDBColumn(ReferenceTableSchema.COLUMNNAME_OF_NAME), refName));
        
        ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString());
        
        if (rs.next()) {
            return rs.getInt(1);
        } else {
            return -1;
        }
    }

     public static boolean containsReference(String name) throws SQLException {
         
        TableSchema table = MedSavantDatabase.ReferenceTableSchema;
        SelectQuery query = new SelectQuery();
        query.addFromTable(table.getTable());
        query.addAllColumns();
        query.addCondition(BinaryCondition.equalTo(table.getDBColumn(ReferenceTableSchema.COLUMNNAME_OF_NAME), name));

        ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString());

        return rs.next();
    }
    
     
     public static int addReference(String name, List<Chromosome> contigs) throws SQLException {
         
        TableSchema referenceTable = MedSavantDatabase.ReferenceTableSchema;
        TableSchema chromTable = MedSavantDatabase.ChromosomeTableSchema;
        Connection c = ConnectionController.connect();

        //add reference
        InsertQuery query1 = new InsertQuery(referenceTable.getTable());
        query1.addColumn(referenceTable.getDBColumn(ReferenceTableSchema.COLUMNNAME_OF_NAME), name);

        PreparedStatement stmt = c.prepareStatement(query1.toString(), Statement.RETURN_GENERATED_KEYS);

        stmt.execute();
        ResultSet res = stmt.getGeneratedKeys();
        res.next();

        int refid = res.getInt(1);
        
        //add contigs
        c.setAutoCommit(false);
        for(int i = 0; i < contigs.size(); i++){
            Chromosome chrom = contigs.get(i);
            InsertQuery query = new InsertQuery(chromTable.getTable());
            query.addColumn(chromTable.getDBColumn(ChromosomeTableSchema.COLUMNNAME_OF_REFERENCE_ID), refid);
            query.addColumn(chromTable.getDBColumn(ChromosomeTableSchema.COLUMNNAME_OF_CONTIG_ID), i);
            query.addColumn(chromTable.getDBColumn(ChromosomeTableSchema.COLUMNNAME_OF_CONTIG_NAME), chrom.getName());
            query.addColumn(chromTable.getDBColumn(ChromosomeTableSchema.COLUMNNAME_OF_CONTIG_LENGTH), chrom.getLength());
            query.addColumn(chromTable.getDBColumn(ChromosomeTableSchema.COLUMNNAME_OF_CENTROMERE_POS), chrom.getCentromerepos());
            c.createStatement().executeUpdate(query.toString());
        }
        c.commit();
        c.setAutoCommit(true);
        
        return refid;
    }
     
     public static boolean removeReference(int refid) throws SQLException {
         
         TableSchema annotationTable = MedSavantDatabase.AnnotationTableSchema;
         TableSchema variantMapTable = MedSavantDatabase.VarianttablemapTableSchema;
         TableSchema refTable = MedSavantDatabase.ReferenceTableSchema;
         TableSchema chromTable = MedSavantDatabase.ChromosomeTableSchema;
         
         Connection c = ConnectionController.connect();
         
         SelectQuery q1 = new SelectQuery();
         q1.addFromTable(annotationTable.getTable());
         q1.addAllColumns();
         q1.addCondition(BinaryCondition.equalTo(annotationTable.getDBColumn(AnnotationTableSchema.COLUMNNAME_OF_REFERENCE_ID), refid));
         ResultSet rs = c.createStatement().executeQuery(q1.toString());
         if (rs.next()) { return false; }
         
         SelectQuery q2 = new SelectQuery();
         q2.addFromTable(variantMapTable.getTable());
         q2.addAllColumns();
         q2.addCondition(BinaryCondition.equalTo(variantMapTable.getDBColumn(VarianttablemapTableSchema.COLUMNNAME_OF_REFERENCE_ID), refid));
         rs = c.createStatement().executeQuery(q2.toString());
         if (rs.next()) { return false; }
         
         DeleteQuery q3 = new DeleteQuery(refTable.getTable());
         q3.addCondition(BinaryCondition.equalTo(refTable.getDBColumn(ReferenceTableSchema.COLUMNNAME_OF_REFERENCE_ID), refid));
         c.createStatement().execute(q3.toString());

         DeleteQuery q4 = new DeleteQuery(chromTable.getTable());
         q4.addCondition(BinaryCondition.equalTo(chromTable.getDBColumn(ChromosomeTableSchema.COLUMNNAME_OF_REFERENCE_ID), refid));
         c.createStatement().execute(q4.toString());
         
         return true;
    }  
     
    public static List<String> getReferencesForProject(int projectid) throws SQLException {
        
        TableSchema refTable = MedSavantDatabase.ReferenceTableSchema;
        TableSchema variantMapTable = MedSavantDatabase.VarianttablemapTableSchema;
        
        SelectQuery query = new SelectQuery();
        query.addFromTable(variantMapTable.getTable());
        query.addColumns(refTable.getDBColumn(ReferenceTableSchema.COLUMNNAME_OF_NAME));
        query.addJoin(SelectQuery.JoinType.LEFT_OUTER, 
                refTable.getTable(), 
                variantMapTable.getTable(), 
                BinaryCondition.equalTo(
                        refTable.getDBColumn(ReferenceTableSchema.COLUMNNAME_OF_REFERENCE_ID), 
                        variantMapTable.getDBColumn(VarianttablemapTableSchema.COLUMNNAME_OF_REFERENCE_ID)));
        query.addCondition(BinaryCondition.equalTo(variantMapTable.getDBColumn(VarianttablemapTableSchema.COLUMNNAME_OF_PROJECT_ID), projectid));
        
        ResultSet rs = ConnectionController.connect().createStatement().executeQuery(query.toString());
        
        List<String> references = new ArrayList<String>();
        while (rs.next()) {
            references.add(rs.getString(1));
        }
        
        return references;
    }
    
    
    public static Map<Integer, String> getReferencesWithoutTablesInProject(int projectid) throws SQLException {
        
        Connection c = org.ut.biolab.medsavant.db.util.ConnectionController.connect();
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT *"
                + " FROM " + ReferenceTableSchema.TABLE_NAME
                + " WHERE " + ReferenceTableSchema.COLUMNNAME_OF_REFERENCE_ID + " NOT IN"
                + " (SELECT " + VarianttablemapTableSchema.COLUMNNAME_OF_REFERENCE_ID + " FROM " + VarianttablemapTableSchema.TABLE_NAME
                + " WHERE " + VarianttablemapTableSchema.COLUMNNAME_OF_PROJECT_ID + "=" + projectid + ")");
        
        HashMap<Integer,String> result = new HashMap<Integer,String>();
        
        while (rs.next()) {
            result.put(rs.getInt(1), rs.getString(2));
        }
        
        return result;
        
    }

}
