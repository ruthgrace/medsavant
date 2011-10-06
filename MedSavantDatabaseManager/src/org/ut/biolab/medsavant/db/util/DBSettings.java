package org.ut.biolab.medsavant.db.util;

/**
 *
 * @author mfiume
 */
public class DBSettings {
    
    public static String DBNAME = "medsavantkb";
    
    public static String TABLENAME_USER = "user";
    public static String TABLENAME_ANNOTATION = "annotation";
    public static String TABLENAME_REFERENCE = "reference";
    public static String TABLENAME_PROJECT = "project";
    public static String TABLENAME_PATIENTTABLEINFO = "patient_tablemap";
    public static String TABLENAME_VARIANTTABLEINFO = "variant_tablemap";
    public static String TABLENAME_REGIONSET = "region_set";
    public static String TABLENAME_REGIONSETMEMBERSHIP = "region_set_membership";
    public static String TABLENAME_COHORT = "cohort";
    public static String TABLENAME_COHORTMEMBERSHIP = "cohort_membership";
    public static String TABLENAME_VARIANTPENDINGUPDATE = "variant_pending_update";
    public static String TABLENAME_SERVELOG = "server_log";
    public static String TABLENAME_ANNOTATIONTABLEMAP = "annotation_tablemap";

    
    
    
    public static String FIELDNAME_LOG_USER = "user";
    public static String FIELDNAME_LOG_EVENT = "event";
    public static String FIELDNAME_LOG_DESCRIPTION = "description";
    public static String FIELDNAME_LOG_TIMESTAMP = "timestamp";
    
    public static String FIELDNAME_ANNOTATION_ID = "annotation_id";
    public static String FIELDNAME_ANNOTATION_PROGRAM = "program";
    public static String FIELDNAME_ANNOTATION_VERSION = "version";
    public static String FIELDNAME_ANNOTATION_REFERENCEID = "reference_id";
    public static String FIELDNAME_ANNOTATION_PATH = "path";
    public static String FIELDNAME_ANNOTATION_HASREF = "has_ref";
    public static String FIELDNAME_ANNOTATION_HASALT = "has_alt";
    public static String FIELDNAME_ANNOTATION_TYPE = "type";
    
    public static String FIELDNAME_ANNOTATION_FORMAT_POSITION = "position";
    public static String FIELDNAME_ANNOTATION_FORMAT_COLUMNNAME = "column_name";
    public static String FIELDNAME_ANNOTATION_FORMAT_COLUMNTYPE = "column_type";
    public static String FIELDNAME_ANNOTATION_FORMAT_FILTERABLE = "filterable";
    public static String FIELDNAME_ANNOTATION_FORMAT_ALIAS = "alias";
    public static String FIELDNAME_ANNOTATION_FORMAT_DESCRIPTION = "description";
    
    public static String FIELDNAME_COHORT_ID = "cohort_id";
    public static String FIELDNAME_COHORT_NAME = "name";
    
    public static String FIELDNAME_COHORTMEMBERSHIP_COHORTID = "cohort_id";
    public static String FIELDNAME_COHORTMEMBERSHIP_HOSPITALID = "hospital_id";
    
    public static String FIELDNAME_PATIENTTABLEINFO_PROJECTID = "project_id";
    public static String FIELDNAME_PATIENTTABLEINFO_PATIENTTABLENAME = "patient_tablename";
    
    public static String FIELDNAME_PROJECT_ID = "project_id";
    public static String FIELDNAME_PROJECT_NAME = "name";
    
    public static String FIELDNAME_REFERENCE_ID = "reference_id";
    public static String FIELDNAME_REFERENCE_NAME = "name";
    
    public static String FIELDNAME_REGIONSET_ID = "regionset_id";
    public static String FIELDNAME_REGIONSET_NAME = "name";
    
    public static String FIELDNAME_REGIONSETMEMBERSHIP_REGIONSETID = "regionset_id";
    public static String FIELDNAME_REGIONSETMEMBERSHIP_GENOMEID = "genome_id";
    public static String FIELDNAME_REGIONSETMEMBERSHIP_CHROM = "chrom";
    public static String FIELDNAME_REGIONSETMEMBERSHIP_START = "start";
    public static String FIELDNAME_REGIONSETMEMBERSHIP_END = "end";
    public static String FIELDNAME_REGIONSETMEMBERSHIP_DESCRIPTION = "description";
    
    public static String FIELDNAME_USER_ID = "id";
    public static String FIELDNAME_USER_NAME = "name";
    public static String FIELDNAME_USER_ISADMIN = "is_admin";
    
    public static String FIELDNAME_VARIANTPENDINGUPDATE_UPDATEID = "update_id";
    public static String FIELDNAME_VARIANTPENDINGUPDATE_PROJECTID = "project_id";
    public static String FIELDNAME_VARIANTPENDINGUPDATE_REFERENCEID = "reference_id";
    public static String FIELDNAME_VARIANTPENDINGUPDATE_ACTION = "action";
    public static String FIELDNAME_VARIANTPENDINGUPDATE_STATUS = "status";
    public static String FIELDNAME_VARIANTPENDINGUPDATE_TIMESTAMP = "timestamp";
    
    public static String FIELDNAME_VARIANTTABLEINFO_PROJECTID = "project_id";
    public static String FIELDNAME_VARIANTTABLEINFO_REFERENCEID = "reference_id";
    public static String FIELDNAME_VARIANTTABLEINFO_VARIANTTABLENAME = "variant_tablename";
    public static String FIELDNAME_VARIANTTABLEINFO_ANNOTATIONIDS = "annotation_ids";
    
    public static String FIELDNAME_PATIENT_PATIENTID = "patient_id";
    public static String FIELDNAME_PATIENT_FIRSTNAME = "first_name";
    public static String FIELDNAME_PATIENT_LASTNAME = "last_name";
    
    public static String FIELDNAME_VARIANT_UPLOADID = "upload_id";
    public static String FIELDNAME_VARIANT_FILEID = "file_id";
    public static String FIELDNAME_VARIANT_VARIANTID = "variant_id";
    public static String FIELDNAME_VARIANT_DNAID = "dna_id";
    public static String FIELDNAME_VARIANT_CHROM = "chrom";
    public static String FIELDNAME_VARIANT_POSITION = "position";
    public static String FIELDNAME_VARIANT_DBSNPID = "dbsnp_id";
    public static String FIELDNAME_VARIANT_REF = "ref";
    public static String FIELDNAME_VARIANT_ALT = "alt";
    public static String FIELDNAME_VARIANT_QUAL = "qual";
    public static String FIELDNAME_VARIANT_FILTER = "filter";
    public static String FIELDNAME_VARIANT_AA = "aa";
    public static String FIELDNAME_VARIANT_AC = "ac";
    public static String FIELDNAME_VARIANT_AF = "af";
    public static String FIELDNAME_VARIANT_AN = "an";
    public static String FIELDNAME_VARIANT_BQ = "bq";
    public static String FIELDNAME_VARIANT_CIGAR = "cigar";
    public static String FIELDNAME_VARIANT_DB = "db";
    public static String FIELDNAME_VARIANT_DP = "dp";
    public static String FIELDNAME_VARIANT_END = "end";
    public static String FIELDNAME_VARIANT_H2 = "h2";
    public static String FIELDNAME_VARIANT_MQ = "mq";
    public static String FIELDNAME_VARIANT_MQ0 = "mq0";
    public static String FIELDNAME_VARIANT_NS = "ns";
    public static String FIELDNAME_VARIANT_SB = "sb";
    public static String FIELDNAME_VARIANT_SOMATIC = "somatic";
    public static String FIELDNAME_VARIANT_VALIDATED = "validated";
    public static String FIELDNAME_VARIANT_CUSTOMINFO = "custom_info";
    
    public static String FIELDNAME_ANNOTATION_TABLEMAP_ID = "annotation_id";
    public static String FIELDNAME_ANNOTATION_TABLEMAP_TABLENAME = "format_tablename";
    
    
    
    public static String createVariantTableName(int projectId, int referenceId){
        return "z_variant_proj" + projectId + "_ref" + referenceId;
    }
    
    public static String createVariantStagingTableName(int projectId, int referenceId, int updateId){
        return "z_variant_staging_proj" + projectId + "_ref" + referenceId + "_update" + updateId;
    }
    
    public static String createAnnotationFormatTableName(int annotationId){
        return "z_annotation_format" + annotationId;
    }

}
