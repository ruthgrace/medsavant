package org.ut.biolab.medsavant.db.model.structure;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import org.ut.biolab.medsavant.db.model.structure.TableSchema;

public class MedSavantDatabase {

	public static class AnnotationTableSchema extends TableSchema {
		public static final String TABLE_NAME = "annotation";
		public AnnotationTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// annotation.annotation_id
		public static final int INDEX_OF_ANNOTATION_ID = 0;
		public static final ColumnType TYPE_OF_ANNOTATION_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_ANNOTATION_ID = 11;
		public static final String COLUMNNAME_OF_ANNOTATION_ID = "annotation_id";
		// annotation.program
		public static final int INDEX_OF_PROGRAM = 1;
		public static final ColumnType TYPE_OF_PROGRAM = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_PROGRAM = 100;
		public static final String COLUMNNAME_OF_PROGRAM = "program";
		// annotation.version
		public static final int INDEX_OF_VERSION = 2;
		public static final ColumnType TYPE_OF_VERSION = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_VERSION = 100;
		public static final String COLUMNNAME_OF_VERSION = "version";
		// annotation.reference_id
		public static final int INDEX_OF_REFERENCE_ID = 3;
		public static final ColumnType TYPE_OF_REFERENCE_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_REFERENCE_ID = 11;
		public static final String COLUMNNAME_OF_REFERENCE_ID = "reference_id";
		// annotation.path
		public static final int INDEX_OF_PATH = 4;
		public static final ColumnType TYPE_OF_PATH = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_PATH = 500;
		public static final String COLUMNNAME_OF_PATH = "path";
		// annotation.has_ref
		public static final int INDEX_OF_HAS_REF = 5;
		public static final ColumnType TYPE_OF_HAS_REF = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_HAS_REF = 1;
		public static final String COLUMNNAME_OF_HAS_REF = "has_ref";
		// annotation.has_alt
		public static final int INDEX_OF_HAS_ALT = 6;
		public static final ColumnType TYPE_OF_HAS_ALT = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_HAS_ALT = 1;
		public static final String COLUMNNAME_OF_HAS_ALT = "has_alt";
		// annotation.type
		public static final int INDEX_OF_TYPE = 7;
		public static final ColumnType TYPE_OF_TYPE = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_TYPE = 11;
		public static final String COLUMNNAME_OF_TYPE = "type";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_ANNOTATION_ID,COLUMNNAME_OF_ANNOTATION_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_PROGRAM,COLUMNNAME_OF_PROGRAM,TableSchema.ColumnType.VARCHAR,100);
			addColumn(COLUMNNAME_OF_VERSION,COLUMNNAME_OF_VERSION,TableSchema.ColumnType.VARCHAR,100);
			addColumn(COLUMNNAME_OF_REFERENCE_ID,COLUMNNAME_OF_REFERENCE_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_PATH,COLUMNNAME_OF_PATH,TableSchema.ColumnType.VARCHAR,500);
			addColumn(COLUMNNAME_OF_HAS_REF,COLUMNNAME_OF_HAS_REF,TableSchema.ColumnType.INTEGER,1);
			addColumn(COLUMNNAME_OF_HAS_ALT,COLUMNNAME_OF_HAS_ALT,TableSchema.ColumnType.INTEGER,1);
			addColumn(COLUMNNAME_OF_TYPE,COLUMNNAME_OF_TYPE,TableSchema.ColumnType.INTEGER,11);
		}

	}

	public static class AnnotationtablemapTableSchema extends TableSchema {
		public static final String TABLE_NAME = "annotation_tablemap";
		public AnnotationtablemapTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// annotation_tablemap.annotation_id
		public static final int INDEX_OF_ANNOTATION_ID = 0;
		public static final ColumnType TYPE_OF_ANNOTATION_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_ANNOTATION_ID = 10;
		public static final String COLUMNNAME_OF_ANNOTATION_ID = "annotation_id";
		// annotation_tablemap.format_tablename
		public static final int INDEX_OF_FORMAT_TABLENAME = 1;
		public static final ColumnType TYPE_OF_FORMAT_TABLENAME = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_FORMAT_TABLENAME = 45;
		public static final String COLUMNNAME_OF_FORMAT_TABLENAME = "format_tablename";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_ANNOTATION_ID,COLUMNNAME_OF_ANNOTATION_ID,TableSchema.ColumnType.INTEGER,10);
			addColumn(COLUMNNAME_OF_FORMAT_TABLENAME,COLUMNNAME_OF_FORMAT_TABLENAME,TableSchema.ColumnType.VARCHAR,45);
		}

	}

	public static class ChromosomeTableSchema extends TableSchema {
		public static final String TABLE_NAME = "chromosome";
		public ChromosomeTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// chromosome.reference_id
		public static final int INDEX_OF_REFERENCE_ID = 0;
		public static final ColumnType TYPE_OF_REFERENCE_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_REFERENCE_ID = 11;
		public static final String COLUMNNAME_OF_REFERENCE_ID = "reference_id";
		// chromosome.contig_id
		public static final int INDEX_OF_CONTIG_ID = 1;
		public static final ColumnType TYPE_OF_CONTIG_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_CONTIG_ID = 11;
		public static final String COLUMNNAME_OF_CONTIG_ID = "contig_id";
		// chromosome.contig_name
		public static final int INDEX_OF_CONTIG_NAME = 2;
		public static final ColumnType TYPE_OF_CONTIG_NAME = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_CONTIG_NAME = 100;
		public static final String COLUMNNAME_OF_CONTIG_NAME = "contig_name";
		// chromosome.contig_length
		public static final int INDEX_OF_CONTIG_LENGTH = 3;
		public static final ColumnType TYPE_OF_CONTIG_LENGTH = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_CONTIG_LENGTH = 11;
		public static final String COLUMNNAME_OF_CONTIG_LENGTH = "contig_length";
		// chromosome.centromere_pos
		public static final int INDEX_OF_CENTROMERE_POS = 4;
		public static final ColumnType TYPE_OF_CENTROMERE_POS = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_CENTROMERE_POS = 11;
		public static final String COLUMNNAME_OF_CENTROMERE_POS = "centromere_pos";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_REFERENCE_ID,COLUMNNAME_OF_REFERENCE_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_CONTIG_ID,COLUMNNAME_OF_CONTIG_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_CONTIG_NAME,COLUMNNAME_OF_CONTIG_NAME,TableSchema.ColumnType.VARCHAR,100);
			addColumn(COLUMNNAME_OF_CONTIG_LENGTH,COLUMNNAME_OF_CONTIG_LENGTH,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_CENTROMERE_POS,COLUMNNAME_OF_CENTROMERE_POS,TableSchema.ColumnType.INTEGER,11);
		}

	}

	public static class CohortTableSchema extends TableSchema {
		public static final String TABLE_NAME = "cohort";
		public CohortTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// cohort.cohort_id
		public static final int INDEX_OF_COHORT_ID = 0;
		public static final ColumnType TYPE_OF_COHORT_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_COHORT_ID = 11;
		public static final String COLUMNNAME_OF_COHORT_ID = "cohort_id";
		// cohort.project_id
		public static final int INDEX_OF_PROJECT_ID = 1;
		public static final ColumnType TYPE_OF_PROJECT_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_PROJECT_ID = 11;
		public static final String COLUMNNAME_OF_PROJECT_ID = "project_id";
		// cohort.name
		public static final int INDEX_OF_NAME = 2;
		public static final ColumnType TYPE_OF_NAME = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_NAME = 255;
		public static final String COLUMNNAME_OF_NAME = "name";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_COHORT_ID,COLUMNNAME_OF_COHORT_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_PROJECT_ID,COLUMNNAME_OF_PROJECT_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_NAME,COLUMNNAME_OF_NAME,TableSchema.ColumnType.VARCHAR,255);
		}

	}

	public static class CohortmembershipTableSchema extends TableSchema {
		public static final String TABLE_NAME = "cohort_membership";
		public CohortmembershipTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// cohort_membership.cohort_id
		public static final int INDEX_OF_COHORT_ID = 0;
		public static final ColumnType TYPE_OF_COHORT_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_COHORT_ID = 11;
		public static final String COLUMNNAME_OF_COHORT_ID = "cohort_id";
		// cohort_membership.patient_id
		public static final int INDEX_OF_PATIENT_ID = 1;
		public static final ColumnType TYPE_OF_PATIENT_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_PATIENT_ID = 11;
		public static final String COLUMNNAME_OF_PATIENT_ID = "patient_id";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_COHORT_ID,COLUMNNAME_OF_COHORT_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_PATIENT_ID,COLUMNNAME_OF_PATIENT_ID,TableSchema.ColumnType.INTEGER,11);
		}

	}

	public static class PatienttablemapTableSchema extends TableSchema {
		public static final String TABLE_NAME = "patient_tablemap";
		public PatienttablemapTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// patient_tablemap.project_id
		public static final int INDEX_OF_PROJECT_ID = 0;
		public static final ColumnType TYPE_OF_PROJECT_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_PROJECT_ID = 11;
		public static final String COLUMNNAME_OF_PROJECT_ID = "project_id";
		// patient_tablemap.patient_tablename
		public static final int INDEX_OF_PATIENT_TABLENAME = 1;
		public static final ColumnType TYPE_OF_PATIENT_TABLENAME = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_PATIENT_TABLENAME = 100;
		public static final String COLUMNNAME_OF_PATIENT_TABLENAME = "patient_tablename";
		// patient_tablemap.format_tablename
		public static final int INDEX_OF_FORMAT_TABLENAME = 2;
		public static final ColumnType TYPE_OF_FORMAT_TABLENAME = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_FORMAT_TABLENAME = 100;
		public static final String COLUMNNAME_OF_FORMAT_TABLENAME = "format_tablename";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_PROJECT_ID,COLUMNNAME_OF_PROJECT_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_PATIENT_TABLENAME,COLUMNNAME_OF_PATIENT_TABLENAME,TableSchema.ColumnType.VARCHAR,100);
			addColumn(COLUMNNAME_OF_FORMAT_TABLENAME,COLUMNNAME_OF_FORMAT_TABLENAME,TableSchema.ColumnType.VARCHAR,100);
		}

	}

	public static class ProjectTableSchema extends TableSchema {
		public static final String TABLE_NAME = "project";
		public ProjectTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// project.project_id
		public static final int INDEX_OF_PROJECT_ID = 0;
		public static final ColumnType TYPE_OF_PROJECT_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_PROJECT_ID = 11;
		public static final String COLUMNNAME_OF_PROJECT_ID = "project_id";
		// project.name
		public static final int INDEX_OF_NAME = 1;
		public static final ColumnType TYPE_OF_NAME = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_NAME = 50;
		public static final String COLUMNNAME_OF_NAME = "name";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_PROJECT_ID,COLUMNNAME_OF_PROJECT_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_NAME,COLUMNNAME_OF_NAME,TableSchema.ColumnType.VARCHAR,50);
		}

	}

	public static class ReferenceTableSchema extends TableSchema {
		public static final String TABLE_NAME = "reference";
		public ReferenceTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// reference.reference_id
		public static final int INDEX_OF_REFERENCE_ID = 0;
		public static final ColumnType TYPE_OF_REFERENCE_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_REFERENCE_ID = 11;
		public static final String COLUMNNAME_OF_REFERENCE_ID = "reference_id";
		// reference.name
		public static final int INDEX_OF_NAME = 1;
		public static final ColumnType TYPE_OF_NAME = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_NAME = 50;
		public static final String COLUMNNAME_OF_NAME = "name";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_REFERENCE_ID,COLUMNNAME_OF_REFERENCE_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_NAME,COLUMNNAME_OF_NAME,TableSchema.ColumnType.VARCHAR,50);
		}

	}

	public static class RegionsetTableSchema extends TableSchema {
		public static final String TABLE_NAME = "region_set";
		public RegionsetTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// region_set.region_set_id
		public static final int INDEX_OF_REGION_SET_ID = 0;
		public static final ColumnType TYPE_OF_REGION_SET_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_REGION_SET_ID = 11;
		public static final String COLUMNNAME_OF_REGION_SET_ID = "region_set_id";
		// region_set.name
		public static final int INDEX_OF_NAME = 1;
		public static final ColumnType TYPE_OF_NAME = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_NAME = 255;
		public static final String COLUMNNAME_OF_NAME = "name";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_REGION_SET_ID,COLUMNNAME_OF_REGION_SET_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_NAME,COLUMNNAME_OF_NAME,TableSchema.ColumnType.VARCHAR,255);
		}

	}

	public static class RegionsetmembershipTableSchema extends TableSchema {
		public static final String TABLE_NAME = "region_set_membership";
		public RegionsetmembershipTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// region_set_membership.region_set_id
		public static final int INDEX_OF_REGION_SET_ID = 0;
		public static final ColumnType TYPE_OF_REGION_SET_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_REGION_SET_ID = 11;
		public static final String COLUMNNAME_OF_REGION_SET_ID = "region_set_id";
		// region_set_membership.genome_id
		public static final int INDEX_OF_GENOME_ID = 1;
		public static final ColumnType TYPE_OF_GENOME_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_GENOME_ID = 11;
		public static final String COLUMNNAME_OF_GENOME_ID = "genome_id";
		// region_set_membership.chrom
		public static final int INDEX_OF_CHROM = 2;
		public static final ColumnType TYPE_OF_CHROM = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_CHROM = 255;
		public static final String COLUMNNAME_OF_CHROM = "chrom";
		// region_set_membership.start
		public static final int INDEX_OF_START = 3;
		public static final ColumnType TYPE_OF_START = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_START = 11;
		public static final String COLUMNNAME_OF_START = "start";
		// region_set_membership.end
		public static final int INDEX_OF_END = 4;
		public static final ColumnType TYPE_OF_END = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_END = 11;
		public static final String COLUMNNAME_OF_END = "end";
		// region_set_membership.description
		public static final int INDEX_OF_DESCRIPTION = 5;
		public static final ColumnType TYPE_OF_DESCRIPTION = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_DESCRIPTION = 255;
		public static final String COLUMNNAME_OF_DESCRIPTION = "description";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_REGION_SET_ID,COLUMNNAME_OF_REGION_SET_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_GENOME_ID,COLUMNNAME_OF_GENOME_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_CHROM,COLUMNNAME_OF_CHROM,TableSchema.ColumnType.VARCHAR,255);
			addColumn(COLUMNNAME_OF_START,COLUMNNAME_OF_START,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_END,COLUMNNAME_OF_END,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_DESCRIPTION,COLUMNNAME_OF_DESCRIPTION,TableSchema.ColumnType.VARCHAR,255);
		}

	}

	public static class ServerlogTableSchema extends TableSchema {
		public static final String TABLE_NAME = "server_log";
		public ServerlogTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// server_log.id
		public static final int INDEX_OF_ID = 0;
		public static final ColumnType TYPE_OF_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_ID = 11;
		public static final String COLUMNNAME_OF_ID = "id";
		// server_log.user
		public static final int INDEX_OF_USER = 1;
		public static final ColumnType TYPE_OF_USER = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_USER = 50;
		public static final String COLUMNNAME_OF_USER = "user";
		// server_log.event
		public static final int INDEX_OF_EVENT = 2;
		public static final ColumnType TYPE_OF_EVENT = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_EVENT = 50;
		public static final String COLUMNNAME_OF_EVENT = "event";
		// server_log.description
		public static final int INDEX_OF_DESCRIPTION = 3;
		public static final ColumnType TYPE_OF_DESCRIPTION = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_DESCRIPTION = -1;
		public static final String COLUMNNAME_OF_DESCRIPTION = "description";
		// server_log.timestamp
		public static final int INDEX_OF_TIMESTAMP = 4;
		public static final ColumnType TYPE_OF_TIMESTAMP = TableSchema.ColumnType.DATE;
		public static final int LENGTH_OF_TIMESTAMP = -1;
		public static final String COLUMNNAME_OF_TIMESTAMP = "timestamp";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_ID,COLUMNNAME_OF_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_USER,COLUMNNAME_OF_USER,TableSchema.ColumnType.VARCHAR,50);
			addColumn(COLUMNNAME_OF_EVENT,COLUMNNAME_OF_EVENT,TableSchema.ColumnType.VARCHAR,50);
			addColumn(COLUMNNAME_OF_DESCRIPTION,COLUMNNAME_OF_DESCRIPTION,TableSchema.ColumnType.VARCHAR,-1);
			addColumn(COLUMNNAME_OF_TIMESTAMP,COLUMNNAME_OF_TIMESTAMP,TableSchema.ColumnType.DATE,-1);
		}

	}

	public static class TestibTableSchema extends TableSchema {
		public static final String TABLE_NAME = "test_ib";
		public TestibTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// test_ib.chrom
		public static final int INDEX_OF_CHROM = 0;
		public static final ColumnType TYPE_OF_CHROM = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_CHROM = 5;
		public static final String COLUMNNAME_OF_CHROM = "chrom";
		// test_ib.position
		public static final int INDEX_OF_POSITION = 1;
		public static final ColumnType TYPE_OF_POSITION = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_POSITION = 11;
		public static final String COLUMNNAME_OF_POSITION = "position";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_CHROM,COLUMNNAME_OF_CHROM,TableSchema.ColumnType.VARCHAR,5);
			addColumn(COLUMNNAME_OF_POSITION,COLUMNNAME_OF_POSITION,TableSchema.ColumnType.INTEGER,11);
		}

	}

	public static class TestmyisamTableSchema extends TableSchema {
		public static final String TABLE_NAME = "test_myisam";
		public TestmyisamTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// test_myisam.chrom
		public static final int INDEX_OF_CHROM = 0;
		public static final ColumnType TYPE_OF_CHROM = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_CHROM = 5;
		public static final String COLUMNNAME_OF_CHROM = "chrom";
		// test_myisam.position
		public static final int INDEX_OF_POSITION = 1;
		public static final ColumnType TYPE_OF_POSITION = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_POSITION = 11;
		public static final String COLUMNNAME_OF_POSITION = "position";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_CHROM,COLUMNNAME_OF_CHROM,TableSchema.ColumnType.VARCHAR,5);
			addColumn(COLUMNNAME_OF_POSITION,COLUMNNAME_OF_POSITION,TableSchema.ColumnType.INTEGER,11);
		}

	}

	public static class UserTableSchema extends TableSchema {
		public static final String TABLE_NAME = "user";
		public UserTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// user.id
		public static final int INDEX_OF_ID = 0;
		public static final ColumnType TYPE_OF_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_ID = 11;
		public static final String COLUMNNAME_OF_ID = "id";
		// user.name
		public static final int INDEX_OF_NAME = 1;
		public static final ColumnType TYPE_OF_NAME = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_NAME = 50;
		public static final String COLUMNNAME_OF_NAME = "name";
		// user.is_admin
		public static final int INDEX_OF_IS_ADMIN = 2;
		public static final ColumnType TYPE_OF_IS_ADMIN = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_IS_ADMIN = 1;
		public static final String COLUMNNAME_OF_IS_ADMIN = "is_admin";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_ID,COLUMNNAME_OF_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_NAME,COLUMNNAME_OF_NAME,TableSchema.ColumnType.VARCHAR,50);
			addColumn(COLUMNNAME_OF_IS_ADMIN,COLUMNNAME_OF_IS_ADMIN,TableSchema.ColumnType.INTEGER,1);
		}

	}

	public static class VariantpendingupdateTableSchema extends TableSchema {
		public static final String TABLE_NAME = "variant_pending_update";
		public VariantpendingupdateTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// variant_pending_update.update_id
		public static final int INDEX_OF_UPDATE_ID = 0;
		public static final ColumnType TYPE_OF_UPDATE_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_UPDATE_ID = 11;
		public static final String COLUMNNAME_OF_UPDATE_ID = "update_id";
		// variant_pending_update.project_id
		public static final int INDEX_OF_PROJECT_ID = 1;
		public static final ColumnType TYPE_OF_PROJECT_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_PROJECT_ID = 11;
		public static final String COLUMNNAME_OF_PROJECT_ID = "project_id";
		// variant_pending_update.reference_id
		public static final int INDEX_OF_REFERENCE_ID = 2;
		public static final ColumnType TYPE_OF_REFERENCE_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_REFERENCE_ID = 11;
		public static final String COLUMNNAME_OF_REFERENCE_ID = "reference_id";
		// variant_pending_update.action
		public static final int INDEX_OF_ACTION = 3;
		public static final ColumnType TYPE_OF_ACTION = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_ACTION = 11;
		public static final String COLUMNNAME_OF_ACTION = "action";
		// variant_pending_update.status
		public static final int INDEX_OF_STATUS = 4;
		public static final ColumnType TYPE_OF_STATUS = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_STATUS = 5;
		public static final String COLUMNNAME_OF_STATUS = "status";
		// variant_pending_update.timestamp
		public static final int INDEX_OF_TIMESTAMP = 5;
		public static final ColumnType TYPE_OF_TIMESTAMP = TableSchema.ColumnType.DATE;
		public static final int LENGTH_OF_TIMESTAMP = -1;
		public static final String COLUMNNAME_OF_TIMESTAMP = "timestamp";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_UPDATE_ID,COLUMNNAME_OF_UPDATE_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_PROJECT_ID,COLUMNNAME_OF_PROJECT_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_REFERENCE_ID,COLUMNNAME_OF_REFERENCE_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_ACTION,COLUMNNAME_OF_ACTION,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_STATUS,COLUMNNAME_OF_STATUS,TableSchema.ColumnType.INTEGER,5);
			addColumn(COLUMNNAME_OF_TIMESTAMP,COLUMNNAME_OF_TIMESTAMP,TableSchema.ColumnType.DATE,-1);
		}

	}

	public static class VarianttablemapTableSchema extends TableSchema {
		public static final String TABLE_NAME = "variant_tablemap";
		public VarianttablemapTableSchema(DbSchema s) {
			super(s.addTable(TABLE_NAME));
			addColumns();
		}

		// variant_tablemap.project_id
		public static final int INDEX_OF_PROJECT_ID = 0;
		public static final ColumnType TYPE_OF_PROJECT_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_PROJECT_ID = 11;
		public static final String COLUMNNAME_OF_PROJECT_ID = "project_id";
		// variant_tablemap.reference_id
		public static final int INDEX_OF_REFERENCE_ID = 1;
		public static final ColumnType TYPE_OF_REFERENCE_ID = TableSchema.ColumnType.INTEGER;
		public static final int LENGTH_OF_REFERENCE_ID = 11;
		public static final String COLUMNNAME_OF_REFERENCE_ID = "reference_id";
		// variant_tablemap.variant_tablename
		public static final int INDEX_OF_VARIANT_TABLENAME = 2;
		public static final ColumnType TYPE_OF_VARIANT_TABLENAME = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_VARIANT_TABLENAME = 100;
		public static final String COLUMNNAME_OF_VARIANT_TABLENAME = "variant_tablename";
		// variant_tablemap.annotation_ids
		public static final int INDEX_OF_ANNOTATION_IDS = 3;
		public static final ColumnType TYPE_OF_ANNOTATION_IDS = TableSchema.ColumnType.VARCHAR;
		public static final int LENGTH_OF_ANNOTATION_IDS = 500;
		public static final String COLUMNNAME_OF_ANNOTATION_IDS = "annotation_ids";
		private void addColumns() {
			addColumn(COLUMNNAME_OF_PROJECT_ID,COLUMNNAME_OF_PROJECT_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_REFERENCE_ID,COLUMNNAME_OF_REFERENCE_ID,TableSchema.ColumnType.INTEGER,11);
			addColumn(COLUMNNAME_OF_VARIANT_TABLENAME,COLUMNNAME_OF_VARIANT_TABLENAME,TableSchema.ColumnType.VARCHAR,100);
			addColumn(COLUMNNAME_OF_ANNOTATION_IDS,COLUMNNAME_OF_ANNOTATION_IDS,TableSchema.ColumnType.VARCHAR,500);
		}

	}

	private static final DbSchema schema = (new DbSpec()).addDefaultSchema();

	//AnnotationTableSchema
	public static final AnnotationTableSchema AnnotationTableSchema = new AnnotationTableSchema(schema);

	//AnnotationtablemapTableSchema
	public static final AnnotationtablemapTableSchema AnnotationtablemapTableSchema = new AnnotationtablemapTableSchema(schema);

	//ChromosomeTableSchema
	public static final ChromosomeTableSchema ChromosomeTableSchema = new ChromosomeTableSchema(schema);

	//CohortTableSchema
	public static final CohortTableSchema CohortTableSchema = new CohortTableSchema(schema);

	//CohortmembershipTableSchema
	public static final CohortmembershipTableSchema CohortmembershipTableSchema = new CohortmembershipTableSchema(schema);

	//PatienttablemapTableSchema
	public static final PatienttablemapTableSchema PatienttablemapTableSchema = new PatienttablemapTableSchema(schema);

	//ProjectTableSchema
	public static final ProjectTableSchema ProjectTableSchema = new ProjectTableSchema(schema);

	//ReferenceTableSchema
	public static final ReferenceTableSchema ReferenceTableSchema = new ReferenceTableSchema(schema);

	//RegionsetTableSchema
	public static final RegionsetTableSchema RegionsetTableSchema = new RegionsetTableSchema(schema);

	//RegionsetmembershipTableSchema
	public static final RegionsetmembershipTableSchema RegionsetmembershipTableSchema = new RegionsetmembershipTableSchema(schema);

	//ServerlogTableSchema
	public static final ServerlogTableSchema ServerlogTableSchema = new ServerlogTableSchema(schema);

	//TestibTableSchema
	public static final TestibTableSchema TestibTableSchema = new TestibTableSchema(schema);

	//TestmyisamTableSchema
	public static final TestmyisamTableSchema TestmyisamTableSchema = new TestmyisamTableSchema(schema);

	//UserTableSchema
	public static final UserTableSchema UserTableSchema = new UserTableSchema(schema);

	//VariantpendingupdateTableSchema
	public static final VariantpendingupdateTableSchema VariantpendingupdateTableSchema = new VariantpendingupdateTableSchema(schema);

	//VarianttablemapTableSchema
	public static final VarianttablemapTableSchema VarianttablemapTableSchema = new VarianttablemapTableSchema(schema);

}
