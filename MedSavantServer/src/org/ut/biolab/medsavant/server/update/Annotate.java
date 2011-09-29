package org.ut.biolab.medsavant.server.update;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import org.broad.tabix.TabixReader;
import org.ut.biolab.medsavant.db.util.jobject.Annotation;
import org.ut.biolab.medsavant.db.util.jobject.AnnotationQueryUtil;
import org.ut.biolab.medsavant.server.ServerLog;
import org.ut.biolab.medsavant.server.ServerLog.LogType;
import org.ut.biolab.medsavant.server.mail.Mail;

/**
 *
 * @author mfiume
 */
public class Annotate {

    private static final int ANNOT_INDEX_OF_CHR = 0;
    private static final int ANNOT_INDEX_OF_POS = 1;
    private static final int ANNOT_INDEX_OF_REF = 2;
    private static final int ANNOT_INDEX_OF_ALT = 3;
    private static final int VARIANT_INDEX_OF_CHR = 4;
    private static final int VARIANT_INDEX_OF_POS = 5;
    private static final int VARIANT_INDEX_OF_REF = 7;
    private static final int VARIANT_INDEX_OF_ALT = 8;
    private static File variantFile = new File("/Users/mfiume/Desktop/variantDump");
    
    private static final int JUMP_DISTANCE = 100000;

    private static VariantRecord annotateForChromosome(String chrom, VariantRecord currentVariant, CSVReader recordReader, TabixReader annotationReader, CSVWriter writer, boolean annotationHasRef, boolean annotationHasAlt, int numFieldsInOutputFile) throws EOFException, IOException {

        String[] outLine = new String[numFieldsInOutputFile];

        AnnotationRecord currentAnnotation = new AnnotationRecord(annotationHasRef, annotationHasAlt);

        // CASE 1: this chrom cannot be annotated
        if (!isChrAnnotatable(chrom, annotationReader)) {
            return skipToNextChr(currentVariant, recordReader, writer, outLine);
        }

        // CASE 2: this chrom can be annotated
        String lastChr = currentVariant.chrom;
        int lastPosition = -1;

        TabixReader.Iterator it = annotationReader.query(currentVariant.chrom);

        while (lastChr.equals(currentVariant.chrom)) {

            if (lastPosition == currentVariant.position && lastChr.equals(currentVariant.chrom)) {
                ServerLog.log("Parser does not support multiple lines per position (" + lastChr + " " + lastPosition + ")",LogType.WARNING);
                numWarnings++;
            }

            boolean annotationHitEnd = false;

            // skip 
            //log(currentVariant.position + " " + lastPosition);
            if (currentVariant.position > lastPosition + JUMP_DISTANCE) {
                //log("Skipping chunk in annotations to " + currentVariant.position + " in " + currentVariant.chrom);
                it = annotationReader.query(annotationReader.chr2tid(currentVariant.chrom), currentVariant.position, Integer.MAX_VALUE);
                
                // happens when there are no more annotations for this chrom
                if (it == null) {
                    return skipToNextChr(currentVariant, recordReader, writer, outLine);
                }
            }

            // update annotation pointer
            while (currentAnnotation.position < currentVariant.position) {
                
                String nextannot = it.next();

                // happens when there are no more annotations for this chrom
                if (nextannot == null) {
                    ServerLog.log("No more annotations for this chromosome");
                    annotationHitEnd = true;
                    break;
                }

                currentAnnotation.setFromLine(nextannot.split("\t"));
            }

            // CASE 2A: We hit the end of the annotations for this chromsome.
            // We can't annotate the variants in the rest of the chromosome
            if (annotationHitEnd) {
                return skipToNextChr(currentVariant, recordReader, writer, outLine);
            }

            // CASE 2B: We found the first annotation position >= current variant position

            // CASE 2B(i): There is no annotation at this position
            if (currentAnnotation.position > currentVariant.position) {
                numLinesWritten++;
                writer.writeNext(copyArray(currentVariant.line, outLine));

                // CASE 2B(ii): There is an annotation at this position
            } else {

                // has both
                if (annotationHasRef && annotationHasAlt) {

                    boolean annotationHitEnd1 = false;
                    boolean foundMatch = false;

                    // look for a matching ref / alt pair
                    while (true) {

                        // found a match
                        if (currentVariant.ref.equals(currentAnnotation.ref)
                                && currentVariant.alt.equals(currentAnnotation.alt)) {
                            foundMatch = true;
                            break;
                            // no match exists
                        } else if (currentAnnotation.position != currentVariant.position) {
                            foundMatch = false;
                            break;
                            // not sure, keep looking
                        } else {
                            String nextannot = it.next();

                            // happens when there are no more annotations for this chrom
                            if (nextannot == null) {
                                ServerLog.log("Annotation hit end; skipping chrom");
                                annotationHitEnd1 = true;
                                break;
                            }

                            currentAnnotation.setFromLine(nextannot.split("\t"));
                        }
                    }

                    // We hit the end of the annotations for this chromsome.
                    // We can't annotate the variants in the rest of the chromosome
                    if (annotationHitEnd1) {
                        return skipToNextChr(currentVariant, recordReader, writer, outLine);
                    }

                    numLinesWritten++;
                    if (foundMatch) {
                        numMatches++;
                        //log("Matched " + currentVariant + " with " + currentAnnotation);
                        // write current line with current annotation
                        writer.writeNext(copyArraysExcludingEntries(currentVariant.line, currentAnnotation.line, outLine, 4));
                    } else {
                        // write current line without annotation
                        writer.writeNext(copyArray(currentVariant.line, outLine));
                    }

                    // has neither
                } else if (!annotationHasRef && !annotationHasAlt) {
                    throw new UnsupportedOperationException("We don't support annotations which don't have ref yet");
                    // has only ref
                } else if (annotationHasRef) {
                    throw new UnsupportedOperationException("We don't support annotations which only have ref yet");
                    // has only alt
                } else if (annotationHasAlt) {
                    throw new UnsupportedOperationException("We don't support annotations which only have alt yet");
                }
            }

            lastChr = currentVariant.chrom;
            lastPosition = currentVariant.position;
                        
            String[] nextLine = recordReader.readNext();
            // throw an exception if we hit the end of the variant file
            if (nextLine == null) {
                throw new EOFException("e1");
            }

            numLinesRead++;

            currentVariant.setFromLine(nextLine);
        }

        return currentVariant;
    }

    private static String[] copyArraysExcludingEntries(String[] line0, String[] line1, String[] outLine, int numFieldsFromLine1ToExclude) {
        System.arraycopy(line0, 0, outLine, 0, line0.length);
        System.arraycopy(line1, numFieldsFromLine1ToExclude, outLine, line0.length, line1.length - numFieldsFromLine1ToExclude);
        return outLine;
    }

    
    private String tdfFilename;
    private String outputFilename;
    private int[] annotationIds;
    //private ChromosomalPosition currentPosition;

    public Annotate(String tdfFilename, String outputFilename, int[] annotIds) {
        this.tdfFilename = tdfFilename;
        this.outputFilename = outputFilename;
        this.annotationIds = annotIds;
    }
    
    public void annotate() throws Exception {

        ServerLog.logByEmail("Annotation started", "Annotation of " + this.tdfFilename + " was started. " + annotationIds.length + " annotation(s) will be performed.\n\nYou will be notified again upon completion.\n\nThanks,\nMedSavant Server Utility");
        
        // if no annotations to perform, copy input to output
        if (annotationIds.length == 0) {
            copyFile(tdfFilename, outputFilename);
            return;
        }

        // otherwise, perform annotations

        // get annotation objects for all ids (from db)
        Annotation[] annotations = new Annotation[annotationIds.length];
        for (int i = 0; i < annotationIds.length; i++) {
            annotations[i] = AnnotationQueryUtil.getAnnotation(annotationIds[i]);
        }

        File[] tmpFiles = new File[annotationIds.length];

        // perform each annotation in turn
        File inFile = new File(tdfFilename);
        File outFile = null;
        for (int i = 0; i < annotations.length; i++) {
            outFile = new File(outputFilename + "_part" + i);
            tmpFiles[i] = outFile;
            annotate(inFile, annotations[i], outFile);
            inFile = outFile;
        }

        // copy the output file to the appropriate destination
        copyFile(outFile.getAbsolutePath(), outputFilename);

        // remove tmp files
        for (File f : tmpFiles) {
            f.delete();
        }
        
        ServerLog.logByEmail("Annotation complete", "Annotation of " + this.tdfFilename + " completed. " + annotationIds.length + " annotations were performed.\nThanks.");
    }

    /*
    private static VariantRecord parsePosFromLine(String[] recordLine) {
    VariantRecord p = new VariantRecord();
    p.chrom = recordLine[VARIANT_INDEX_OF_CHR];
    p.position = Integer.parseInt(recordLine[VARIANT_INDEX_OF_POS]);
    p.ref = recordLine[VARIANT_INDEX_OF_REF];
    p.alt = recordLine[VARIANT_INDEX_OF_ALT];
    return p;
    }
     * 
     */
    private static VariantRecord skipToNextChr(VariantRecord currentPos, CSVReader recordReader, CSVWriter writer, String[] outLine) throws EOFException, IOException {

        //writer.writeNext(new String[]{"### SKIPPING ###"});

        // skip to next chr
        String currentChr = currentPos.chrom;
        String nextLineChr = currentPos.chrom;

        ServerLog.log("Flushing remaining variants in " + currentPos.chrom);

        String[] recordLine = null;

        String[] lastLine = null;
        
        writer.writeNext(copyArray(currentPos.line, outLine));
        numLinesWritten++;

        // loop until we get to the next chromosome
        while (true) {

            recordLine = recordReader.readNext();

            // we hit the end of the record file, bail
            // TODO: should exit cleaner
            if (recordLine == null) {
                throw new EOFException("Reached end of variant file");
            }
            numLinesRead++;

            nextLineChr = recordLine[VARIANT_INDEX_OF_CHR];

            // write this record to file if we're on the same chr
            if (currentChr.equals(nextLineChr)) {

                lastLine = recordLine;
                
                writer.writeNext(copyArray(recordLine, outLine));
                numLinesWritten++;
                
                // if we hit a new chr, bail
            } else {
                break;
            }
        }
        
        //log("Last variant: { chr=" + lastLine[VARIANT_INDEX_OF_CHR] + " pos=" + lastLine[VARIANT_INDEX_OF_POS] + "}");

        ServerLog.log("Next variant: " + new VariantRecord(recordLine));
        
        return new VariantRecord(recordLine);
    }

    private static boolean isChrAnnotatable(String chr, TabixReader annotationReader) {
        boolean b = annotationReader.chr2tid(chr) != -1;
        return b;
    }

    /*
    private static VariantRecord skipToNextAnnotatableChromosome(VariantRecord currentPosition, CSVReader recordReader, TabixReader annotationReader, CSVWriter writer, String[] recordLine, String[] outLine) throws EOFException, IOException {
    
    currentPosition = skipToNextChr(currentPosition, recordReader, writer, recordLine, outLine);
    
    while (!isChrAnnotatable(currentPosition.chrom, annotationReader)) {
    currentPosition = skipToNextChr(currentPosition, recordReader, writer, recordLine, outLine);
    }
    
    return currentPosition;
    }
     * 
     */
    private static int numMatches;
    private static int numLinesRead;
    private static int numLinesWritten;
    private static int numWarnings;

    private static void annotate(File inFile, Annotation annot, File outFile) throws Exception {
        System.out.printf("Record file: %s\nAnnotation file: %s\nOutput file: %s\n", inFile.getAbsolutePath(), annot.getDataPath(), outFile.getAbsolutePath());

        int numFieldsInInputFile = getNumFieldsInFile(inFile);
        int numFieldsInOutputFile = numFieldsInInputFile + annot.getAnnotationFormat().getNumFields();

        CSVReader recordReader = new CSVReader(new FileReader(inFile));
        TabixReader annotationReader = annot.getReader();
        CSVWriter writer = new CSVWriter(new FileWriter(outFile));

        boolean annotationHasRef = annot.getAnnotationFormat().hasRef();
        boolean annotationHasAlt = annot.getAnnotationFormat().hasAlt();

        VariantRecord nextPosition = new VariantRecord(recordReader.readNext());
        numLinesRead++;

        int totalLinesWritten = 0;
        numWarnings = 0;
        
        boolean eof = false;
        
        while (true) {
            try {
                ServerLog.log("Annotating variants in " + nextPosition.chrom);
                ServerLog.log("First variant for chrom: " + nextPosition.toString());
                numMatches = 0;
                numLinesWritten = 0;
                
                nextPosition = annotateForChromosome(nextPosition.chrom, nextPosition, recordReader, annotationReader, writer, annotationHasRef, annotationHasAlt, numFieldsInOutputFile);
                ServerLog.log("Done annotating this chromosome; " + numMatches + " matches found, " + numLinesWritten + " written");
                
                totalLinesWritten += numLinesWritten;
                
                if (totalLinesWritten+1 != numLinesRead) {
                    throw new Exception("error: missed some lines");
                }
                
                // an exception is thrown when we hit the end of the input file
            } catch (Exception e) {
                e.printStackTrace();
                eof = true;
                break;
            }
        }
        
        if (!eof) {
            numLinesWritten++;
            writer.writeNext(copyArray(nextPosition.line, new String[numFieldsInOutputFile]));
        }
        
        recordReader.close();
        writer.close();
        
        ServerLog.log("Done annotating file, " + numLinesRead + " read " + totalLinesWritten + " written with " + numWarnings + " warnings");
    }

    /**
     * 
     */
    private static class VariantRecord {

        public String chrom;
        public int position;
        public String ref;
        public String alt;
        public String[] line;

        public VariantRecord(String[] line) {
            setFromLine(line);
        }

        private void setFromLine(String[] line) {
            this.line = line;
            chrom = line[VARIANT_INDEX_OF_CHR];
            position = Integer.parseInt(line[VARIANT_INDEX_OF_POS]);
            ref = line[VARIANT_INDEX_OF_REF];
            alt = line[VARIANT_INDEX_OF_ALT];
        }

        @Override
        public String toString() {
            return "VariantRecord{" + "chrom=" + chrom + ", position=" + position + ", ref=" + ref + ", alt=" + alt + '}';
        }
    }

    private static class AnnotationRecord {

        public String chrom;
        public int position;
        public String ref;
        public String alt;
        public String[] line;
        private final boolean hasRef;
        private final boolean hasAlt;

        public AnnotationRecord(boolean hasRef, boolean hasAlt) {
            this.hasRef = hasRef;
            this.hasAlt = hasAlt;
        }

        public void setFromLine(String[] line) {
            this.line = line;
            chrom = line[ANNOT_INDEX_OF_CHR];
            position = Integer.parseInt(line[ANNOT_INDEX_OF_POS]);
            if (hasRef) {
                ref = line[ANNOT_INDEX_OF_REF];
            } else {
                ref = null;
            }
            if (hasAlt) {
                alt = line[ANNOT_INDEX_OF_ALT];
            } else {
                alt = null;
            }
        }

        @Override
        public String toString() {
            return "AnnotationRecord{" + "chrom=" + chrom + ", position=" + position + ", ref=" + ref + ", alt=" + alt + '}';
        }
    }

    /**
     * HELPER FUNCTIONS
     */
    private static int getNumFieldsInFile(File inFile) throws FileNotFoundException, IOException {
        CSVReader reader = new CSVReader(new FileReader(inFile));
        int result = reader.readNext().length;
        reader.close();
        return result;
    }

    private static void copyFile(String srFile, String dtFile) {
        try {
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);

            //For Append the file.
            //  OutputStream out = new FileOutputStream(f2,true);

            //For Overwrite the file.
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String[] copyArray(String[] inLine, String[] outLine) {
        Arrays.fill(outLine, null);
        System.arraycopy(inLine, 0, outLine, 0, inLine.length);
        return outLine;
    }

    

    private static void print(String[] nextLine) {
        for (String s : nextLine) {
            System.out.print(s + "\t");
        }
    }

    /**
     * MAIN
     */
    public static void main(String[] args) throws Exception {
        ServerLog.setMailRecipient("marcfiume@gmail.com");
        Annotate annot = new Annotate(variantFile.getAbsolutePath(), variantFile.getAbsolutePath() + ".annot", new int[]{3});
        annot.annotate();
    }
}
