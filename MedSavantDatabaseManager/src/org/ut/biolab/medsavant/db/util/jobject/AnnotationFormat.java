/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.util.jobject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Andrew
 */
public class AnnotationFormat {
    
    private Document doc;
    private boolean hasRef = false;
    private boolean hasAlt = false;
    
    private List<String> columnNames = new ArrayList<String>();
    private List<String> columnTypes = new ArrayList<String>();
    
    public static String[] DEFAULT_COLUMNS = new String[]{"genome_id", "chrom", "position", "ref", "alt"};
    public static String[] DEFAULT_TYPES = new String[]{"INT(11)", "VARCHAR(5)", "INT(11)", "VARCHAR(30)", "VARCHAR(30)"};
    
    public AnnotationFormat(String path) throws SAXException, ParserConfigurationException, IOException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(new File(path));
        doc.getDocumentElement().normalize();
        
        hasRef = doc.getDocumentElement().getAttribute("hasref").equals("true");
        hasAlt = doc.getDocumentElement().getAttribute("hasalt").equals("true");
        
        //copy defaults
        for(int i = 0; i < 5; i++){
            if((i==3 && !hasRef) || (i==4 && !hasAlt)) continue;
            columnNames.add(DEFAULT_COLUMNS[i]);
            columnTypes.add(DEFAULT_TYPES[i]); 
        }     
        
        //get custom columns
        NodeList fields = doc.getElementsByTagName("field");
        for(int i = 0; i < fields.getLength(); i++){
            Element field = (Element)(fields.item(i));
            columnNames.add(field.getAttribute("name"));
            columnTypes.add(field.getAttribute("field")); 
        }
    }
    
    public List<String> getFieldNames(){
        return columnNames;
    }
    
    public int getNumFields(){
        return columnNames.size();
    }
    
    public String getFieldType(String fieldName){
        int pos = columnNames.indexOf(fieldName);
        if(pos == -1) return null;
        return columnTypes.get(pos);
    }

}
