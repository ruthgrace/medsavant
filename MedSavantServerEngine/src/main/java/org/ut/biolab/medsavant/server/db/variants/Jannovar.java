package org.ut.biolab.medsavant.server.db.variants;

import jannovar.exception.JannovarException;
import jannovar.io.SerializationManager;
import jannovar.reference.Chromosome;
import jannovar.reference.TranscriptModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ut.biolab.medsavant.shared.util.DirectorySettings;

/**
 *
 * @author mfiume
 */
class Jannovar {

    private static final Log LOG = LogFactory.getLog(Jannovar.class);

    private static SerializationManager sManager = new SerializationManager();
    private static HashMap<Byte, Chromosome> chromosomeMap;
    private static String dirPath;
    private static ArrayList<TranscriptModel> transcriptModelList = null;

    private static final String serializationFileName = "refseq_hg19.ser";

    /**
     * The main entry point to this class
     *
     * @param vcfFiles An array of VCF files to be annoted with Jannovar
     * @return An array of files that have been annotated with Jannovar
     * @throws JannovarException
     */
    public static File[] annotateVCFFiles(File[] vcfFiles) throws JannovarException, IOException {

        initialize();

        File[] jvFiles = new File[vcfFiles.length];

        int counter = 0;

        // annotate each file
        for (File file : vcfFiles) {
            LOG.info("Annotating " + file.getAbsolutePath() + " with Jannovar");
            jvFiles[counter++] = annotateVCFWithJannovar(file);
            LOG.info("Done annotating " + file.getAbsolutePath() + " with Jannovar");
        }

        return jvFiles;
    }

    private static String getJannovarDirectoryPath() {
        return new File(DirectorySettings.getCacheDirectory().getPath(), "jannovar").getAbsolutePath();
    }

    /**
     * Initialize Jannovar
     */
    private static synchronized boolean initialize() throws IOException {
        // download the serizalized files, if needed                
        if (!hasSerializedFile(serializationFileName)) {                        
            File f = new File(getJannovarDirectoryPath());
            if(!(f.exists() && f.isDirectory())){
                if(!f.mkdirs()){                    
                    LOG.error("Couldn't make jannovardirectorypath " + getJannovarDirectoryPath());
                    return false;
                }
            }
            
            jannovar.Jannovar.main(new String[]{"-U", getJannovarDirectoryPath(), "--create-refseq"});            
            String src = serializationFileName;
            String dst = getJannovarDirectoryPath() + File.separator + serializationFileName;

            File sf = new File(src);
            File df = new File(dst);
            if (sf.exists()) {            
                if (!sf.renameTo(df)) {                    
                    throw new IOException("Can't rename file " + src + " to " + dst);
                }
            } else {                
                throw new IOException("Can't locate Jannovar serialization file " + src);
            }
        }        
        return true;
    }

    /**
     * Check if the Jannovar serialized annotation file has been downloaded.
     */
    private static boolean hasSerializedFile(String filename) {
        File serFile = new File(Jannovar.getJannovarDirectoryPath(), filename);
        return serFile.exists();
    }

    /**
     * Uses Jannovar to create a new VCF file and sends that file to server. The
     * Jannovar VCF file is subsequently removed (treated as temporary data)
     *
     * Code modified from Jannovar class.
     */
    private static File annotateVCFWithJannovar(File sourceVCF) throws JannovarException, IOException {
        /* Annotated VCF name as determined by Jannovar. */
        String outname = sourceVCF.getName();
        int i = outname.lastIndexOf("vcf");
        if (i < 0) {
            i = outname.lastIndexOf("VCF");
        }
        if (i < 0) {
            outname = outname + ".jv.vcf";
        } else {
            outname = outname.substring(0, i) + "jv.vcf";
        }

        try {

            File outFile = new File(outname);

            jannovar.Jannovar.main(new String[]{"-D", Jannovar.getJannovarDirectoryPath()
                + File.separator + serializationFileName, "-V", sourceVCF.getAbsolutePath()});

            /* Since we can't seem to specify the output directory for Jannovar
             * VCF files, once the file is created, move it to the temp directory. */
            outFile.renameTo(new File(DirectorySettings.generateDateStampDirectory(DirectorySettings.getTmpDirectory()), outname));
            LOG.info("[Jannovar] Wrote annotated VCF file to \"" + outFile.getAbsolutePath() + "\"");

            return outFile;
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error(ex);
            throw new IOException(ex.getLocalizedMessage());
        }

    }

}
