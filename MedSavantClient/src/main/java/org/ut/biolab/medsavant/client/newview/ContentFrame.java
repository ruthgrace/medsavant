package org.ut.biolab.medsavant.client.newview;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.ut.biolab.medsavant.client.view.util.ViewUtil;

/**
 *
 * @author mfiume
 */
class ContentFrame extends JPanel {
    private final JPanel content;

    public ContentFrame() {
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        this.add(Components.getLeftMarginComponent(),BorderLayout.WEST);
        content = ViewUtil.getClearPanel();
        content.setLayout(new BorderLayout());
        this.add(ViewUtil.getClearBorderlessScrollPane(content),BorderLayout.CENTER);
    }

    void setContentTo(JPanel view) {
        content.removeAll();
        content.add(view,BorderLayout.CENTER);
        content.updateUI();
    }
}
