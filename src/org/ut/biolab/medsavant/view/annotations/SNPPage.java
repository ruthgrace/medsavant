/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.view.annotations;

import java.awt.Component;
import javax.swing.JPanel;
import org.ut.biolab.medsavant.view.patients.SplitScreenView;
import org.ut.biolab.medsavant.view.subview.SectionView;
import org.ut.biolab.medsavant.view.subview.SubSectionView;

/**
 *
 * @author mfiume
 */
public class SNPPage extends SubSectionView {

    private SplitScreenView view;

    public SNPPage(SectionView parent) { super(parent); }

    
    public String getName() {
        return "SNPs";
    }

    public JPanel getView() {
        view =  new SplitScreenView(
                new SNPAnnotationListModel(), 
                new SNPAnnotationDetailedView());
        return view;
    }
    
}
