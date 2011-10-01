/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.db.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import net.sf.samtools.util.AsciiLineReader;
import net.sf.samtools.util.BlockCompressedOutputStream;
import org.broad.tabix.TabixWriter;
import org.ut.biolab.medsavant.db.util.query.AnnotationFormat;
import org.ut.biolab.medsavant.db.util.query.ReferenceQueryUtil;
import org.broad.tabix.TabixWriter.Conf;
import org.ut.biolab.medsavant.db.util.query.AnnotationQueryUtil;

/**
 *
 * @author Andrew
 */
public class ImportAnnotation {
    
    public static void main(String[] args) throws SQLException{
        
        if(args.length != 5) {
            setError("Usage: ImportAnnotation program version reference_name file_path format_path");
        }
        
        String program = args[0];
        String version = args[1];   
        String referenceName = args[2];
        String filePath = args[3];
        String formatPath = args[4];
        
        //get referenceId
        int referenceId = ReferenceQueryUtil.getReferenceId(referenceName);
        if(referenceId == -1){
            setError("Reference " + referenceName + " does not exist");
        }
        
        //ensure files exist
        File annotationFile = new File(filePath);
        File formatFile = new File(formatPath);
        if(!annotationFile.exists()){
            setError("File " + filePath + " does not exist");
        }
        if(!formatFile.exists()){
            setError("File " + formatPath + " does not exist");
        }  
        
        //retrieve format
        AnnotationFormat af = null;
        try { 
            af = new AnnotationFormat(formatPath);
        } catch (Exception e){
            setError("There was an error parsing format file: " + formatPath, e);
        }
        String[] header = af.getFieldNames().toArray(new String[af.getNumFields()]);
        File tabixFile = new File(filePath + ".tabix");
        try {
            formatTabix(header, annotationFile, tabixFile);
        } catch (Exception ex) {
            setError("There was an error converting " + filePath + " to Tabix", ex);
        }

        //add annotation
        AnnotationQueryUtil.addAnnotation(program, version, referenceId, tabixFile.getAbsolutePath().replaceAll("\\\\", "/"), formatFile.getAbsolutePath().replaceAll("\\\\", "/"));
    }
    
    public static void setError(String errorString){
        System.exit(1);
    }
    
    public static void setError(String errorString, Exception e){
        e.printStackTrace();
        System.exit(1);
    }
    
    private static void formatTabix(String[] header, File infile, File outfile) throws Exception {
        ColumnMapping mapping = new ColumnMapping(header);
        Conf conf = mapping.getTabixConf();
        
        //Assume that the annotation file is already sorted. If not, God help us. 
        
        //Compress the text file 
        AsciiLineReader input = new AsciiLineReader(new FileInputStream(infile));
        PrintWriter output = new PrintWriter(new BlockCompressedOutputStream(outfile));
        String line;
        while ((line = input.readLine()) != null) {
            output.println(line);
        }
        output.close();
        input.close();
        
        //Create index file
        TabixWriter writer = new TabixWriter(outfile, conf);
        writer.createIndex(outfile);
    }
}

class ColumnMapping {

    private int index_genomeId;
    private int index_chrom;
    private int index_position;
    private String[] columnNames;

    public ColumnMapping(String[] columnNames){
        this.columnNames = columnNames;
        for(int i = 0; i < columnNames.length; i++){
            String col = columnNames[i];
            if(col.equals("genome_id")){
                index_genomeId = i;
            } else if (col.equals("chrom")){
                index_chrom = i;
            } else if (col.equals("position")){
                index_position = i;
            }
        }
    }

    public String getColumnName(int index){
        return columnNames[index];
    }

    public int getIndexGenomeId(){
        return index_genomeId;
    }

    public int getIndexChrom(){
        return index_chrom;
    }

    public int getIndexPosition(){
        return index_position;
    }

    public Conf getTabixConf(){
        return new Conf(0, index_chrom+1, index_position+1, index_position+1, '#', 0);
    }

}
