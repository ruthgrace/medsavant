/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.model;

import java.io.Serializable;

/**
 *
 * @author Andrew
 */
public class SimpleVariantFile implements Serializable {
    
    private int id;
    private String name;
    
    public SimpleVariantFile(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String toString() {
        return name;
    }
    
}