package org.ut.biolab.medsavant.client.newview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.ut.biolab.medsavant.client.view.subview.SubSectionView;

/**
 *
 * @author mfiume
 */
class NewFrame extends JPanel {

    private ContentFrame contentFrame;
    private MenuFrame menuFrame;

    public NewFrame() {
        initUI();
    }

    private void initUI() {
        this.setBackground(Color.white);
        this.setLayout(new BorderLayout());

        menuFrame = new MenuFrame();
        contentFrame = new ContentFrame();

        JPanel p = new JPanel();
        p.setBackground(Color.white);
        p.setPreferredSize(new Dimension(300, 300));
        contentFrame.setContentTo(p);

        this.add(menuFrame,BorderLayout.NORTH);
        this.add(contentFrame,BorderLayout.CENTER);
    }

    private void setSubSectionView(SubSectionView v) {
        contentFrame.setContentTo(v.getView());
    }
}
