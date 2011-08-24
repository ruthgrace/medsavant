/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ut.biolab.medsavant.view.account;
import org.ut.biolab.medsavant.view.genetics.*;

import java.awt.Component;
import javax.swing.JPanel;
import org.ut.biolab.medsavant.view.subview.SubSectionView;
import org.ut.biolab.medsavant.view.subview.SectionView;

/**
 *
 * @author mfiume
 */
public class AccountSection extends SectionView {
    private JPanel[] panels;

    public AccountSection() {
    }
    
    @Override
    public String getName() {
        return "Information";
    }

    @Override
    public SubSectionView[] getSubSections() {
        SubSectionView[] pages = new SubSectionView[1];
        pages[0] = new AccountInformationPage(this);
        return pages;
    }

    @Override
    public JPanel[] getPersistentPanels() {
        return panels;
    }

    @Override
    public Component[] getBanner() {
        return null;
    }

}
