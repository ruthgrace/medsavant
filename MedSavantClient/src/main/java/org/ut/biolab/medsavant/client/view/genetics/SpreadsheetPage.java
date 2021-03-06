/**
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.ut.biolab.medsavant.client.view.genetics;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ut.biolab.medsavant.client.api.Listener;
import org.ut.biolab.medsavant.client.filter.FilterController;
import org.ut.biolab.medsavant.client.filter.FilterEvent;
import org.ut.biolab.medsavant.client.reference.ReferenceController;
import org.ut.biolab.medsavant.client.reference.ReferenceEvent;
import org.ut.biolab.medsavant.client.util.ClientMiscUtils;
import org.ut.biolab.medsavant.client.util.ThreadController;
import org.ut.biolab.medsavant.client.view.component.SplitScreenPanel;
import org.ut.biolab.medsavant.shared.vcf.VariantRecord;
import org.ut.biolab.medsavant.client.view.genetics.inspector.ComprehensiveInspector;
import org.ut.biolab.medsavant.client.view.genetics.inspector.stat.StaticInspectorPanel;
import org.ut.biolab.medsavant.client.view.app.MultiSectionApp;
import org.ut.biolab.medsavant.client.view.app.AppSubSection;
import org.ut.biolab.medsavant.client.view.util.PeekingPanel;
import org.ut.biolab.medsavant.client.view.component.WaitPanel;
import org.ut.biolab.medsavant.client.view.util.PeekingPanelContainer;

/**
 *
 * @author mfiume
 */
public class SpreadsheetPage extends AppSubSection implements Listener<FilterEvent> {

    private static final Log LOG = LogFactory.getLog(SpreadsheetPage.class);
    private Thread viewPreparationThread;
    private JPanel view;
    private JPanel outerTablePanel;
    private TablePanel tablePanel;
    private Component[] settingComponents;
    private PeekingPanel detailView;
    private ComprehensiveInspector inspectorPanel;

    //SplitScreenAdapter(JPanel panel, SplitScreenAdapter.Location location){
    @Override
    public void clearSelection() {
        if (tablePanel != null) {
            tablePanel.clearSelection();
        }
    }

    public SpreadsheetPage(MultiSectionApp parent) {
        super(parent, "Spreadsheet");
        FilterController.getInstance().addListener(new Listener<FilterEvent>() {
            @Override
            public void handleEvent(FilterEvent event) {
                queueTableUpdate();
            }
        });
        ReferenceController.getInstance().addListener(new Listener<ReferenceEvent>() {
            @Override
            public void handleEvent(ReferenceEvent event) {
                if (event.getType() == ReferenceEvent.Type.CHANGED) {
                    queueTableUpdate();
                }
            }
        });
        FilterController.getInstance().addListener(this);
    }

    @Override
    public Component[] getSubSectionMenuComponents() {
        if (settingComponents == null) {
            settingComponents = new Component[1];
            try {
                viewPreparationThread.join();
            } catch (Exception e) {
                System.err.println(e);
            }

            if (detailView == null) {
                System.err.println("detailView is null!");
            }
            settingComponents[0] = PeekingPanel.getToggleButtonForPanel(detailView, "Inspector");
        }
        return settingComponents;
    }

   
    @Override
    public JPanel getView() {
        try {
            if (view == null) {

                view = new JPanel();
                view.setLayout(new BorderLayout());
                view.add(new WaitPanel("Preparing Spreadsheet..."));

                Runnable prepareViewInBackground = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            LOG.debug("Running thread prepareViewINBackground!");
                            final JPanel tmpView = new JPanel();
                            tmpView.setLayout(new BorderLayout());

                            tablePanel = new TablePanel(pageName);
                            SplitScreenPanel ssp = new SplitScreenPanel(tablePanel);

                            inspectorPanel = new ComprehensiveInspector(true, true, true, true, true, true, true, true, true, ssp);

                            inspectorPanel.addSelectionListener(new Listener<Object>() {
                                @Override
                                public void handleEvent(Object event) {
                                    clearSelection();
                                }
                            });

                            TablePanel.addVariantSelectionChangedListener(new Listener<VariantRecord>() {
                                @Override
                                public void handleEvent(final VariantRecord r) {
                                    inspectorPanel.setVariantRecord(r);
                                }
                            });
                            LOG.debug("Constructing detailView");
                            
                            tmpView.add(ssp, BorderLayout.CENTER);
                            
                            final PeekingPanelContainer ppc = new PeekingPanelContainer(tmpView);
                            detailView = ppc.addPeekingPanel("Inspector", BorderLayout.EAST, inspectorPanel, false, ComprehensiveInspector.INSPECTOR_WIDTH);

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    view.removeAll();
                                    view.add(ppc, BorderLayout.CENTER);
                                    view.updateUI();
                                }
                            });

                        } catch (Exception ex) {
                            LOG.error(ex);
                            System.out.println("Caught spreadsheet loading error: " + ex);
                            ex.printStackTrace();
                            view.removeAll();
                            WaitPanel p = new WaitPanel("Error loading Spreadsheet");
                            p.setComplete();
                            view.add(p);

                        }
                    }
                };

                viewPreparationThread = new Thread(prepareViewInBackground);
                viewPreparationThread.start();

            }

            return view;

        } catch (Exception ex) {
            ClientMiscUtils.reportError("Error generating genome view: %s", ex);
        }
        return view;
    }

    @Override
    public void viewWillLoad() {
        super.viewWillLoad();
        tablePanel.setTableShowing(true);        
        if(inspectorPanel != null && this.detailView.isExpanded()){            
            inspectorPanel.refresh();
        }        
    }

    @Override
    public void viewDidUnload() {
        super.viewDidUnload();
        tablePanel.setTableShowing(false);
    }

    public void queueTableUpdate() {
        ThreadController.getInstance().cancelWorkers(pageName);
        if (tablePanel == null) {
            return;
        }
        tablePanel.queueUpdate();
    }

    @Override
    public void handleEvent(FilterEvent event) {
        if (tablePanel != null) {
            tablePanel.clearSelection();
        }
        if (inspectorPanel != null) {
            inspectorPanel.setVariantRecord(null);
            inspectorPanel.setGene(null);
        }
    }
}
