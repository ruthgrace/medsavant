/**
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.ut.biolab.medsavant.client.query.medsavant;

import org.ut.biolab.medsavant.client.query.medsavant.complex.CohortConditionGenerator;
import com.healthmarketscience.sqlbuilder.Condition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ut.biolab.medsavant.client.appapi.MedSavantVariantSearchApp;
import org.ut.biolab.medsavant.client.filter.WhichTable;
import org.ut.biolab.medsavant.client.plugin.AppDescriptor;
import org.ut.biolab.medsavant.shared.appapi.MedSavantApp;
import org.ut.biolab.medsavant.client.plugin.AppController;
import org.ut.biolab.medsavant.client.project.ProjectController;
import org.ut.biolab.medsavant.shared.db.ColumnType;
import org.ut.biolab.medsavant.shared.format.AnnotationFormat;
import org.ut.biolab.medsavant.shared.format.CustomField;
import org.ut.biolab.medsavant.shared.model.OntologyType;
import org.ut.biolab.medsavant.client.query.ConditionViewGenerator;
import org.ut.biolab.medsavant.client.query.SearchConditionItem;
import org.ut.biolab.medsavant.client.query.medsavant.complex.ComprehensiveConditionGenerator;
import org.ut.biolab.medsavant.client.query.medsavant.complex.GenesConditionGenerator;
import org.ut.biolab.medsavant.client.query.medsavant.complex.OntologyConditionGenerator;
import org.ut.biolab.medsavant.client.query.medsavant.complex.PatientConditionGenerator;
import org.ut.biolab.medsavant.client.query.medsavant.complex.RegionSetConditionGenerator;
import org.ut.biolab.medsavant.client.query.medsavant.complex.TagConditionGenerator;
import org.ut.biolab.medsavant.client.query.medsavant.complex.VariantConditionGenerator;
import org.ut.biolab.medsavant.client.query.value.DatabaseConditionGenerator;
import org.ut.biolab.medsavant.client.query.view.SearchConditionEditorView;
import org.ut.biolab.medsavant.client.query.view.SearchConditionItemView;
import static org.ut.biolab.medsavant.shared.format.AnnotationFormat.ANNOTATION_FORMAT_CUSTOM_VCF;
import static org.ut.biolab.medsavant.shared.format.AnnotationFormat.ANNOTATION_FORMAT_DEFAULT;

/**
 *
 * @author mfiume
 */
public class MedSavantConditionViewGenerator implements ConditionViewGenerator {

    private static final Log LOG = LogFactory.getLog(MedSavantConditionViewGenerator.class);
    public static String REGIONBASED_CONDITIONS = "Region and Ontologies";
    private final HashMap<String, DatabaseFieldStruct> itemToCustomFieldMap;
    private final HashMap<SearchConditionItem, DatabaseConditionGenerator> itemToConditionGeneratorMap;

    private static MedSavantConditionViewGenerator instance;
    private TreeMap<String, List<String>> allowedMap;
    private Map<String, ComprehensiveConditionGenerator> conditionGenerators;
    //private Map<String, ComprehensiveConditionGenerator> patientConditionGenerators;
    //private Map<String, ComprehensiveConditionGenerator> variantConditionGenerators;
    public static final String PATIENT_CONDITIONS = "Patients";
    public static final String VARIANT_CONDITIONS = "Variants";
    public static final String OTHER_CONDITIONS = "Other Conditions";

    private MedSavantConditionViewGenerator() {
        itemToCustomFieldMap = new HashMap<String, DatabaseFieldStruct>();
        itemToConditionGeneratorMap = new HashMap<SearchConditionItem, DatabaseConditionGenerator>();

        conditionGenerators = new HashMap<String, ComprehensiveConditionGenerator>();
        //patient
        try {
            for (CustomField field : ProjectController.getInstance().getCurrentPatientFormat()) {
                if (field.isFilterable() && isFilterable(field.getColumnType())) {
                    String name = field.getAlias();
                    ComprehensiveConditionGenerator patientFieldCondition = new PatientConditionGenerator(field);
                    conditionGenerators.put(name, patientFieldCondition);
                }
            }
        } catch (Exception e) {
            LOG.error(e);
        }

        // variant
        try {
            AnnotationFormat[] afs = ProjectController.getInstance().getCurrentAnnotationFormats();
            for (AnnotationFormat af : afs) {
                for (CustomField field : af.getCustomFields()) {
                    if (field.isFilterable() && isFilterable(field.getColumnType())) {
                        //catHolders.add(new FieldFilterHolder(field, WhichTable.VARIANT, queryID));

                        String program = af.getProgram();
                        program = (program.equals(ANNOTATION_FORMAT_DEFAULT) || program.equals(ANNOTATION_FORMAT_CUSTOM_VCF)) ? "" : " - " + program;

                        String name = field.getAlias() + program;
                        ComprehensiveConditionGenerator variantFieldCondition = new VariantConditionGenerator(name,field);
                        conditionGenerators.put(name, variantFieldCondition);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e);
        }

        // other
        ComprehensiveConditionGenerator cohort = new CohortConditionGenerator();
        conditionGenerators.put(cohort.getName(), cohort);

        ComprehensiveConditionGenerator regions = new RegionSetConditionGenerator();
        conditionGenerators.put(regions.getName(), regions);

        ComprehensiveConditionGenerator tag = new TagConditionGenerator();
        conditionGenerators.put(tag.getName(), tag);

        ComprehensiveConditionGenerator go = new OntologyConditionGenerator(OntologyType.GO);
        conditionGenerators.put(go.getName(), go);

        ComprehensiveConditionGenerator hpo = new OntologyConditionGenerator(OntologyType.HPO);
        conditionGenerators.put(hpo.getName(), hpo);

        ComprehensiveConditionGenerator omim = new OntologyConditionGenerator(OntologyType.OMIM);
        conditionGenerators.put(omim.getName(), omim);
        
        ComprehensiveConditionGenerator genes = new GenesConditionGenerator();
        conditionGenerators.put("Genes", genes);
        
        // plugin
        MedSavantVariantSearchApp[] searchApps = loadSearchApps();
        for (final MedSavantVariantSearchApp searchApp : searchApps) {
            ComprehensiveConditionGenerator generator = new ComprehensiveConditionGenerator() {

                @Override
                public String getName() {
                    return searchApp.getName();
                }

                @Override
                public String category() {
                    return searchApp.category();
                }

                @Override
                public Condition getConditionsFromEncoding(String encoding) throws Exception {
                    return searchApp.getConditionsFromEncoding(encoding);
                }

                @Override
                public SearchConditionEditorView getViewGeneratorForItem(SearchConditionItem item) {
                    return searchApp.getViewGeneratorForItem(item);
                }
            };
            conditionGenerators.put(generator.getName(), generator);
        }

        init();
    }

    public static MedSavantConditionViewGenerator getInstance() {
        if (instance == null) {
            instance = new MedSavantConditionViewGenerator();
        }
        return instance;
    }

    @Override
    public SearchConditionItemView generateViewForItem(SearchConditionItem item) {

        String conditionName = item.getName();

        if (conditionGenerators.containsKey(conditionName)) {
            SearchConditionItemView view = new SearchConditionItemView(item,conditionGenerators.get(conditionName).getViewGeneratorForItem(item));
            return view;
        }

        throw new UnsupportedOperationException("No view for item " + conditionName);
    }

    @Override
    public Condition generateConditionForItem(SearchConditionItem item) throws Exception {

        DatabaseConditionGenerator cg = itemToConditionGeneratorMap.get(item);


        String conditionName = item.getName();

        // non basic conditions
        if (conditionGenerators.containsKey(conditionName)) {
            return conditionGenerators.get(conditionName).getConditionsFromEncoding(item.getSearchConditionEncoding());
        }


        /*
         if (itemToCustomFieldMap.containsKey(conditionName) || cg == null) {
         DatabaseFieldStruct s = itemToCustomFieldMap.get(item.getName());
         String encoding = item.getSearchConditionEncoding();

         if (s.whichTable == WhichTable.PATIENT) {
         // TODO
         } else if (s.whichTable == WhichTable.VARIANT) {
         return generateVariantConditionForDatabaseField(s, encoding);
         }
         }*/

        throw new UnsupportedOperationException("No condition generator for " + item.getName());
    }

    public final void init() {
        allowedMap = new TreeMap<String, List<String>>();
        addMap(conditionGenerators);
    }

    @Override
    public Map<String, List<String>> getAllowableItemNames() {
        return allowedMap;
    }

    private boolean isFilterable(ColumnType type) {
        switch (type) {
            case INTEGER:
            case FLOAT:
            case DECIMAL:
            case BOOLEAN:
            case VARCHAR:
                return true;
            default:
                return false;
        }
    }

    private void addMap(Map<String, ComprehensiveConditionGenerator> conditionNameToGeneratorMap) {
        for (String name : conditionNameToGeneratorMap.keySet()) {
            ComprehensiveConditionGenerator gen = conditionNameToGeneratorMap.get(name);
            if (allowedMap.containsKey(gen.category())) {
                allowedMap.get(gen.category()).add(gen.getName());
            } else {
                ArrayList<String> arr = new ArrayList<String>();
                arr.add(gen.getName());
                allowedMap.put(gen.category(), arr);
            }
        }
    }

    private MedSavantVariantSearchApp[] loadSearchApps() {
        List<MedSavantVariantSearchApp> results = new LinkedList<MedSavantVariantSearchApp>();
        int counter = 0;

        for(AppDescriptor ad : AppController.getInstance().getDescriptors()){
            MedSavantApp ap = AppController.getInstance().getPlugin(ad.getID());
            if(ap instanceof MedSavantVariantSearchApp){
                results.add((MedSavantVariantSearchApp)ap);
                counter++;
            }
        }

        return results.toArray(new MedSavantVariantSearchApp[counter]);
    }

    public static class DatabaseFieldStruct {

        private final CustomField field;
        private final WhichTable whichTable;
        private SearchConditionItem item;

        public DatabaseFieldStruct(CustomField field, WhichTable whichTable) {
            this.field = field;
            this.whichTable = whichTable;
        }

        public void setItem(SearchConditionItem item) {
            this.item = item;
        }
    }
}
