/*
 *    Copyright 2011 University of Toronto
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/*
 * FilterPanel.java
 *
 * Created on 19-Oct-2011, 4:16:02 PM
 */
package org.ut.biolab.medsavant.view.genetics.filter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.ut.biolab.medsavant.view.images.IconFactory;
import org.ut.biolab.medsavant.view.util.ViewUtil;

/**
 *
 * @author Andrew
 */
public class FilterPanel extends javax.swing.JPanel {

    private List<FilterPanelSub> subs = new ArrayList<FilterPanelSub>();
    private int subNum = 1;

    /** Creates new form FilterPanel */
    public FilterPanel() {
        initComponents();

        container.setBorder(BorderFactory.createLineBorder(container.getBackground(), 10));
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        createNewSubPanel();
    }

    private JPanel createNewOrButton(){

        final JLabel addLabel = ViewUtil.createIconLabel(IconFactory.getInstance().getIcon(IconFactory.StandardIcon.ADD));
        addLabel.setToolTipText("Add filter set");
        addLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                createNewSubPanel();
            }
        });


        JPanel tmp1 = ViewUtil.getPrimaryBannerPanel();//ViewUtil.getClearPanel();
        tmp1.setBorder(BorderFactory.createCompoundBorder(
                          ViewUtil.getTinyLineBorder(),
                          ViewUtil.getMediumBorder()));
        tmp1.add(addLabel);
        tmp1.setMaximumSize(new Dimension(9999,40));
        tmp1.add(Box.createHorizontalStrut(5));
        JLabel addLabelText = new JLabel("Add filter set");
        addLabelText.setForeground(Color.white);
        tmp1.add(addLabelText);
        tmp1.add(Box.createHorizontalGlue());

        return tmp1;
    }

    public void createNewSubPanel(){
        subs.add(new FilterPanelSub(this, subNum++));
        refreshSubPanels();
    }

    public void refreshSubPanels(){
        container.removeAll();

        JLabel l = new JLabel("Filter out variants that don't pass any of these filter sets:");
        container.add(l);
        container.add(Box.createVerticalStrut(5));
        //check for removed items
        for(int i = subs.size()-1; i >= 0; i--){
            if(subs.get(i).isRemoved()){
                subs.remove(i);
            }
        }

        //refresh panel
        for(int i = 0; i < subs.size(); i++){
            container.add(subs.get(i));
            container.add(Box.createVerticalStrut(5));
        }
        container.add(createNewOrButton());
        container.add(Box.createVerticalGlue());

        this.updateUI();
    }
    
    public List<FilterPanelSub> getFilterPanelSubs(){
        return this.subs;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        container = new javax.swing.JPanel();

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));
        jPanel1.setLayout(new java.awt.GridLayout(1, 3));

        javax.swing.GroupLayout containerLayout = new javax.swing.GroupLayout(container);
        container.setLayout(containerLayout);
        containerLayout.setHorizontalGroup(
            containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 657, Short.MAX_VALUE)
        );
        containerLayout.setVerticalGroup(
            containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 509, Short.MAX_VALUE)
        );

        jPanel1.add(container);

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 661, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel container;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
