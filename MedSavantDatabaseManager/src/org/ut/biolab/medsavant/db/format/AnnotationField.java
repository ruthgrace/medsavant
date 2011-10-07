/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.format;

/**
 *
 * @author Andrew
 */
public class AnnotationField extends CustomField {
    
    public static enum Category {PATIENT, GENOTYPE, PHENOTYPE, ONTOLOGY, PATHWAYS}
    
    private Category category;
    
    public AnnotationField(String name, String type, boolean filterable, String alias, String description){
        this(name, type, filterable, alias, description, Category.PHENOTYPE);
    }
    
    public AnnotationField(String name, String type, boolean filterable, String alias, String description, Category category){
        super(name, type, filterable, alias, description);
        this.category = category;
    }
  
    public Category getCategory() {
        return category;
    }
    
}
