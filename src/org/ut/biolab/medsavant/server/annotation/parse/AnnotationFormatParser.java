package org.ut.biolab.medsavant.server.annotation.parse;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.ut.biolab.medsavant.server.annotation.AnnotationFormat;
import org.ut.biolab.medsavant.server.annotation.AnnotationFormat.AnnotationField;

/**
 *
 * @author mfiume
 */
public class AnnotationFormatParser {
    
    private static final String CURRENT_VERSION = "1.0";
    
    public static AnnotationFormat parseAnnotationFormat(File inFile) throws Exception {
        
        SAXBuilder parser = new SAXBuilder();
        Document doc = parser.build(inFile);
        
        Element root = doc.getRootElement();
        String version = root.getAttributeValue("version");
        
        if (version.equals(CURRENT_VERSION)) {
            return parseAnnotationFormatFromRoot_v1_0(root);
        } else {
            throw new Exception("error: no parser for version " + version);
        }
        
    }
    
    public static String EL_ROOT = "root";
    public static String EL_FIELD = "field";
    public static String ATT_VERSION = "version";
    public static String ATT_HASREF = "hasref";
    public static String ATT_HASALT = "hasalt";
    public static String ATT_NAME = "name";
    public static String ATT_TYPE = "type";
    
    public static String writeAnnotationFormat(AnnotationFormat af) throws Exception {
        
        Element root = new Element(EL_ROOT);
        root.setAttribute(ATT_VERSION, CURRENT_VERSION);
        root.setAttribute(ATT_HASREF, af.isHasRef() + "");
        root.setAttribute(ATT_HASALT, af.isHasAlt() + "");
        
        for (AnnotationField f : af.getFields()) {
            Element field  = new Element(EL_FIELD);
            field.setAttribute(ATT_NAME, f.getName());
            field.setAttribute(ATT_TYPE, f.getType());
            root.addContent(field);
        }
        
        Document d = new Document(root);
        
        XMLOutputter outputter = new XMLOutputter();
        StringWriter sw = new StringWriter();
    try {
      outputter.output(d, sw);     
      return sw.toString();
    }
    catch (IOException e) {
        e.printStackTrace();
        return null;
    }
        
    }

    private static AnnotationFormat parseAnnotationFormatFromRoot_v1_0(Element root) {
        
        AnnotationFormat af = new AnnotationFormat();

        boolean hasRef = root.getAttributeValue(ATT_HASREF).equals("true");
        boolean hasAlt = root.getAttributeValue(ATT_HASALT).equals("true");
        
        af.setHasAlt(hasAlt);
        af.setHasRef(hasRef);
        
        List<Element> fields = root.getChildren(EL_FIELD);
        
        for (Element field : fields) {
            af.addField(new AnnotationField(field.getAttributeValue(ATT_NAME),field.getAttributeValue(ATT_TYPE)));
        }
        
        return af;
    }
    
    public static void main(String[] args) {
        try {
            System.out.println(writeAnnotationFormat(parseAnnotationFormat(new File("/Users/mfiume/Desktop/delme.xml"))));
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(AnnotationFormatParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
