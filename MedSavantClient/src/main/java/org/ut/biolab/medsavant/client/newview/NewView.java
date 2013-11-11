package org.ut.biolab.medsavant.client.newview;

import javax.swing.JFrame;

/**
 *
 * @author mfiume
 */
public class NewView {

    public static void main(String[] s) {
        NewFrame m = new NewFrame();
        JFrame f = new JFrame("MedSavant");
        f.add(m);
        f.pack();
        f.setExtendedState( f.getExtendedState()|JFrame.MAXIMIZED_BOTH );
        f.setVisible(true);
    }
}
