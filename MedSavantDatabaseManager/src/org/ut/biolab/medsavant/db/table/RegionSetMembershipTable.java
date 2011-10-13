/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.table;

/**
 *
 * @author Andrew
 */
public class RegionSetMembershipTable {
    
    public static String TABLENAME = "region_set_membership";
    
    public static String FIELDNAME_REGIONSETID = "region_set_id";
    public static String FIELDNAME_GENOMEID = "genome_id";
    public static String FIELDNAME_CHROM = "chrom";
    public static String FIELDNAME_START = "start";
    public static String FIELDNAME_END = "end";
    public static String FIELDNAME_DESCRIPTION = "description";
}
