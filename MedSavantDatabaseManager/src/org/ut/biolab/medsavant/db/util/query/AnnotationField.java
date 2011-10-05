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
    
    public static enum FieldType {VARCHAR, FLOAT, INT, BOOLEAN, DECIMAL}
    
    private String columnName;
    private String columnType;
    private boolean filterable;
    private String alias;
    private String description;
    private FieldType fieldType;

    public AnnotationField(String name, String type, boolean filterable, String alias, String description){
        this.columnName = name;
        this.columnType = type;
        this.filterable = filterable;
        this.alias = alias;
        this.description = description;
        setFieldType(columnType);
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
    
    private void setFieldType(String type){
        String typeLower = type.toLowerCase();
        if (typeLower.contains("float")){
            fieldType = FieldType.FLOAT;
        } else if (typeLower.contains("decimal")){
            fieldType = FieldType.DECIMAL;
        } else if (typeLower.contains("int")){
            if(typeLower.contains("(1)")){
                fieldType = FieldType.BOOLEAN;
            } else {
                fieldType = FieldType.INT;
            }
        } else if (typeLower.contains("boolean")){
            fieldType = FieldType.BOOLEAN;
        } else {
            fieldType = FieldType.VARCHAR;
        }
    }
    
    public FieldType getFieldType(){
        return fieldType;
    }
    
    
}
