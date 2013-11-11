package org.ut.biolab.medsavant.client.newview;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.ut.biolab.medsavant.client.view.util.ViewUtil;

/**
 *
 * @author mfiume
 */
class Components {

    static Component getLeftMarginComponent() {
        JPanel p = ViewUtil.getClearPanel();
        setMinimumWidthForPanel(p,100);
        return p;
    }

    private static void setMinimumWidthForPanel(JPanel p, int i) {
        Dimension d = new Dimension(i,1);
        p.setMinimumSize(d);
        p.setPreferredSize(d);
        p.setMaximumSize(d);
    }

}
