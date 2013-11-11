package org.ut.biolab.medsavant.client.newview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.ut.biolab.medsavant.client.view.util.ViewUtil;

/**
 *
 * @author mfiume
 */
class MenuFrame extends JPanel {
    private JButton tablesButton;
    private JButton adminButton;
    private JTextField searchField;

    public MenuFrame() {
        initUI();
    }

    private void initUI() {
        this.setBackground(new Color(241,241,241));
        this.setPreferredSize(new Dimension(1,60));

        this.setLayout(new BorderLayout());
        this.setBorder(ViewUtil.getMediumBorder());

        this.add(Components.getLeftMarginComponent(),BorderLayout.WEST);

        JPanel menuContainer = ViewUtil.getClearPanel();
        ViewUtil.applyHorizontalBoxLayout(menuContainer);

        searchField = new JTextField();
        searchField.setFocusable(false);
        ViewUtil.fixSize(searchField, new Dimension(500,35));

        tablesButton = ViewUtil.getSoftButton("Data Explorer");
        adminButton = ViewUtil.getSoftButton("Admin");

        menuContainer.add(searchField);
        menuContainer.add(Box.createHorizontalGlue());
        menuContainer.add(tablesButton);
        menuContainer.add(adminButton);

        this.add(menuContainer,BorderLayout.CENTER);
    }

}
