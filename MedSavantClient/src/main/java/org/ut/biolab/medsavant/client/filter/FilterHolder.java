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
package org.ut.biolab.medsavant.client.filter;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import org.ut.biolab.medsavant.client.appapi.MedSavantVariantSearchApp;
import org.ut.biolab.medsavant.client.ontology.OntologyFilter;
import org.ut.biolab.medsavant.client.ontology.OntologyFilterView;
import org.ut.biolab.medsavant.client.plugin.AppDescriptor;
import org.ut.biolab.medsavant.shared.appapi.MedSavantApp;
import org.ut.biolab.medsavant.client.plugin.AppController;
import org.ut.biolab.medsavant.client.util.ClientMiscUtils;
import org.ut.biolab.medsavant.client.view.component.KeyValuePairPanel;
import org.ut.biolab.medsavant.client.view.images.IconFactory;
import org.ut.biolab.medsavant.client.view.util.DialogUtils;
import org.ut.biolab.medsavant.client.view.util.ViewUtil;
import org.ut.biolab.medsavant.shared.format.BasicPatientColumns;
import org.ut.biolab.medsavant.shared.format.CustomField;
import org.ut.biolab.medsavant.shared.model.OntologyType;

/**
 * Class which lets us create the user interface around a filter-view without having to instantiate it.  The actual UI is maintained
 * using Marc's KeyValuePairPanel, using the filterID as the key.
 *
 * @author Andrew
 */
public abstract class FilterHolder {

    private final String name;
    private final String filterID;
    protected final int queryID;
    protected FilterView filterView;

    private KeyValuePairPanel parent;
    private JToggleButton editButton;
    private JButton clearButton;

    public FilterHolder(String name, String filterID, int queryID) {
        this.name = name;
        this.filterID = filterID;
        this.queryID = queryID;
    }

    public String getFilterID() {
        return filterID;
    }

    public String getFilterName() {
        return name;
    }

    public boolean hasFilterView() {
        return filterView != null;
    }

    public FilterView getFilterView() throws Exception {
        if (filterView == null) {
            filterView = createFilterView();
        }
        return filterView;
    }

    public abstract FilterView createFilterView() throws Exception;

    public abstract void loadFilterView(FilterState state) throws Exception;

    void addTo(KeyValuePairPanel kvp, final boolean longRunning) {
        parent = kvp;
        kvp.addKey(name);

        kvp.setKeyColour(name, QueryPanel.INACTIVE_KEY_COLOR);

        JLabel detailString = new JLabel("");
        detailString.setForeground(Color.orange);

        kvp.setValue(name, detailString);

        clearButton = ViewUtil.getTexturedButton(IconFactory.getInstance().getIcon(IconFactory.StandardIcon.CLEAR));
        clearButton.setVisible(false);
        kvp.setAdditionalColumn(name, 0, clearButton);

        editButton = ViewUtil.getTexturedToggleButton(IconFactory.getInstance().getIcon(IconFactory.StandardIcon.CONFIGURE));
        editButton.addActionListener(new ActionListener() {

            boolean acceptedBeingLong = false;

            @Override
            public void actionPerformed(ActionEvent ae) {

                int result = DialogUtils.YES;
                if (!acceptedBeingLong && editButton.isSelected() && longRunning) {
                    result = DialogUtils.askYesNo("Warning", "<html>This is a complex search condition that may take a long time to complete.<br>Would you like to continue anyways?</html>");
                    if (result == DialogUtils.YES) {
                        acceptedBeingLong = true;
                    }
                }

                if (result == DialogUtils.YES) {
                    openFilterView();
                } else {
                    editButton.setSelected(false);
                }

            }
        });
        kvp.setAdditionalColumn(name, 1, editButton);

        clearButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    FilterController.getInstance().removeFilter(filterID, queryID);
                    filterView = null;
                    clearButton.setVisible(false);
                    editButton.setSelected(false);
                    parent.toggleDetailVisibility(name, false);
                } catch (Exception ex) {
                    ClientMiscUtils.reportError("Error removing filter: %s", ex);
                }
            }
        });
    }

    public void openFilterView() {
        try {
            parent.setDetailComponent(name, getFilterView());
            parent.toggleDetailVisibility(name);
            parent.updateUI();
        } catch (Exception ex) {
            filterView = null;
            editButton.setSelected(false);
            if (!(ex instanceof InterruptedException)) {
                DialogUtils.displayException("Problem displaying filter", "Problem getting values for filter " + name, ex);
            }
        }
    }
}


/**
 * Many filters follow the same pattern, with an id called "FILTER_ID", a
 * name called "FILTER_NAME", and a constructor which takes a single
 * <code>int queryID</code> parameter.
 */
class SimpleFilterHolder extends FilterHolder {

    private final Class viewClass;

    SimpleFilterHolder(Class clazz, int queryID) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        super((String)clazz.getField("FILTER_NAME").get(null), (String)clazz.getField("FILTER_ID").get(null), queryID);
        viewClass = clazz;
    }

    @Override
    public FilterView createFilterView() throws Exception {
        return (FilterView)viewClass.getDeclaredConstructor(int.class).newInstance(queryID);
    }

    @Override
    public void loadFilterView(FilterState state) throws Exception {
        filterView = (FilterView)viewClass.getDeclaredConstructor(FilterState.class, int.class).newInstance(state, queryID);
    }
}



/**
 * A common placeholder which wraps the filter for a custom field in the
 * patient or variant tables.
 */
class FieldFilterHolder extends FilterHolder {

    private final CustomField field;
    private final WhichTable whichTable;

    FieldFilterHolder(CustomField field, WhichTable table, int queryID) {
        super(field.getAlias(), field.getColumnName(), queryID);
        this.field = field;
        this.whichTable = table;
    }

    @Override
    public FilterView createFilterView() throws Exception {
        String colName = field.getColumnName();
        String alias = field.getAlias();
        switch (field.getColumnType()) {
            case INTEGER:
                if (!colName.equals(BasicPatientColumns.PATIENT_ID.getColumnName()) && !colName.equals(BasicPatientColumns.GENDER.getColumnName())) {
                    return new NumericFilterView(whichTable, colName, queryID, alias, false);
                }
                break;
            case FLOAT:
            case DECIMAL:
                return new NumericFilterView(whichTable, colName, queryID, alias, true);
        }
        // If nothing else claimed this, make it a StringListFilter.
        return new StringListFilterView(whichTable, colName, queryID, alias);

    }

    @Override
    public void loadFilterView(FilterState state) throws Exception {
        switch (state.getType()) {
            case NUMERIC:
                filterView = new NumericFilterView(state, queryID);
                break;
            case STRING:
                filterView = new StringListFilterView(state, queryID);
                break;
            case BOOLEAN:
                filterView = new BooleanFilterView(state, queryID);
                break;
            default:
                throw new Exception("Unknown filter type " + state.getType());

        }
    }
}

class OntologyFilterHolder extends FilterHolder {

    private final OntologyType ontology;

    OntologyFilterHolder(OntologyType ont, int queryID) {
        super(OntologyFilter.ontologyToTitle(ont), OntologyFilter.ontologyToFilterID(ont), queryID);
        this.ontology = ont;
    }

    @Override
    public FilterView createFilterView() throws Exception {
        return new OntologyFilterView(ontology, queryID);
    }

    @Override
    public void loadFilterView(FilterState state) throws Exception {
        filterView = new OntologyFilterView(state, queryID);
    }
}
