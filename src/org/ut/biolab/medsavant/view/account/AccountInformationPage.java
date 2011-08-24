/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.view.account;

import org.ut.biolab.medsavant.view.genetics.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.ut.biolab.medsavant.view.subview.SectionView;
import org.ut.biolab.medsavant.view.subview.SubSectionView;
import org.ut.biolab.medsavant.model.record.Chromosome;
import org.ut.biolab.medsavant.model.record.Genome;
import org.ut.biolab.medsavant.util.view.PeekingPanel;
import org.ut.biolab.medsavant.view.dialog.SavantExportForm;

/**
 *
 * @author mfiume
 */
public class AccountInformationPage extends SubSectionView {

    private JPanel panel;

    public AccountInformationPage(SectionView parent) { super(parent); }

    public String getName() {
        return "Account";
    }

    public JPanel getView() {
        if (panel == null) {
            setPanel();
        }
        return panel;
    }

    private void setPanel() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        //panel.setBackground(Color.red);
        //panel.add(new TablePanel(), BorderLayout.CENTER);
    }

    public Component[] getBanner() {
        return null;
    }
    
    
    @Override
    public void viewLoading() {
    }

    @Override
    public void viewDidUnload() {
    }
    
    
    
}
