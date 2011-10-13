/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.ut.biolab.medsavant.db.model.Chromosome;
import org.ut.biolab.medsavant.db.table.ChromosomeTable;
import org.ut.biolab.medsavant.db.util.ConnectionController;

/**
 *
 * @author Andrew
 */
public class ChromosomeQueryUtil {
    
    public static List<Chromosome> getContigs(int refid) throws SQLException{
        
        Connection c = ConnectionController.connect();
        ResultSet rs = c.createStatement().executeQuery(
                "SELECT *"
                + " FROM " + ChromosomeTable.TABLENAME
                + " WHERE " + ChromosomeTable.FIELDNAME_REFERENCEID + "=" + refid
                + " ORDER BY " + ChromosomeTable.FIELDNAME_CONTIGID);
        
        List<Chromosome> result = new ArrayList<Chromosome>();
        while(rs.next()){
            result.add(new Chromosome(rs.getString(ChromosomeTable.FIELDNAME_CONTIGNAME), null, rs.getLong(ChromosomeTable.FIELDNAME_CENTROMEREPOS), rs.getLong(ChromosomeTable.FIELDNAME_CONTIGLENGTH)));
        }
        return result;       
    }
    
}
