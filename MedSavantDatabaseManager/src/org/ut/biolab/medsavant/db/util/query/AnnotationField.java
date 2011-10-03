/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util.query;

/**
 *
 * @author Andrew
 */
public class AnnotationField {
    
    private String columnName;
    private String columnType;
    private boolean filterable;
    private String alias;
    private String description;

    public AnnotationField(String name, String type, boolean filterable, String alias, String description){
        this.columnName = name;
        this.columnType = type;
        this.filterable = filterable;
        this.alias = alias;
        this.description = description;
    }

    public String getAlias() {
        return alias;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFilterable() {
        return filterable;
    }
    
    
}
