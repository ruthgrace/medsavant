/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.view.genetics.charts;

import org.ut.biolab.medsavant.view.genetics.charts.SummaryChart;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.ut.biolab.medsavant.olddb.table.VariantTableSchema;
import org.ut.biolab.medsavant.view.genetics.charts.ChartMapGenerator;
import org.ut.biolab.medsavant.view.genetics.charts.VariantFieldChartMapGenerator;
import org.ut.biolab.medsavant.view.util.ViewUtil;

/**
 *
 * @author mfiume
 */
public class ChartView extends JPanel {

    private SummaryChart sc;
    private JComboBox chartChooser;
    private Map<String, ChartMapGenerator> mapGenerators;
    private JCheckBox bPie;
    private JCheckBox bSort;
    private JCheckBox bLog;

    public ChartView() {
        mapGenerators = new HashMap<String, ChartMapGenerator>();
        initGUI();
        this.chartChooser.setSelectedItem(VariantTableSchema.ALIAS_CHROM);
    }

    private void initGUI() {
        this.setLayout(new BorderLayout());
        initToolBar();
        initCards();
        initBottomBar();
    }

    private void initToolBar() {

        JPanel toolbar = ViewUtil.getSubBannerPanel("Chart");
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));

        chartChooser = new JComboBox();
        toolbar.add(chartChooser);

        chartChooser.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String alias = (String) chartChooser.getSelectedItem();
                sc.setChartMapGenerator(mapGenerators.get(alias));
                if (bSort == null) { return; }
                if (alias.equals(VariantTableSchema.ALIAS_CHROM)) {
                    bSort.setEnabled(false);
                    sc.setIsSortedKaryotypically(true);
                } else {
                    bSort.setEnabled(true);
                    sc.setIsSortedKaryotypically(false);
                }
                
            }
        });

        ButtonGroup rg = new ButtonGroup();

        JRadioButton b1 = new JRadioButton("All");
        JRadioButton b2 = new JRadioButton("Cohort");

        rg.add(b1);
        rg.add(b2);

        //toolbar.add(ViewUtil.getMediumSeparator());

        //toolbar.add(ViewUtil.clear(b1));
        //toolbar.add(ViewUtil.clear(b2));

        toolbar.add(Box.createHorizontalGlue());

        b1.setSelected(true);

        this.add(toolbar, BorderLayout.NORTH);
    }

    private void initCards() {
        initAllCard();
        addCMGs();
    }

    private void initAllCard() {

        JPanel h1 = new JPanel();
        h1.setLayout(new GridLayout(1, 1));

        sc = new SummaryChart();

        h1.add(sc, BorderLayout.CENTER);

        this.add(h1, BorderLayout.CENTER);
    }

    private void addCMG(ChartMapGenerator cmg) {
        sc.setChartMapGenerator(cmg);
        chartChooser.addItem(cmg.getName());
        mapGenerators.put(cmg.getName(), cmg);
    }

    private void addCMGs() {
        addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_DNAID));
        addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_CHROM));
        addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_QUALITY));
        //addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_GT));
        addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_DP));
        addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_AA));
        addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_AC));
        addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_AF));
        addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_AN));
        //addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_BQ));
        //addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_MQ));
        //addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_MQ0));
        addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_REFERENCE));
        addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_ALTERNATE));
        //addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_Transv));
        //addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_inCodingRegion));
        //addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_prediction));
        //addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_pph2_class));
        //addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_functionalClass));
        //addCMG(new VariantFieldChartMapGenerator(VariantTableSchema.ALIAS_sift_prediction));
        
    }

    private void initBottomBar() {
        JPanel bottomToolbar = ViewUtil.getBannerPanel();
        bottomToolbar.setBorder(ViewUtil.getTinyLineBorder());
        bottomToolbar.setLayout(new BoxLayout(bottomToolbar, BoxLayout.X_AXIS));

        bottomToolbar.add(Box.createHorizontalGlue());

        //bottomToolbar.add(chartChooser);

        bPie = new JCheckBox("Pie chart");
        bSort = new JCheckBox("Sort by frequency");
        bLog = new JCheckBox("Log scale");

        bPie.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setIsPie(!sc.isPie());
            }
        });
        
         bSort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setIsSorted(!sc.isSorted());
            }
        });

         
          bLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setIsLogscale(!sc.isLogscale());
            }
        });
        bottomToolbar.add(ViewUtil.getMediumSeparator());

        bottomToolbar.add(ViewUtil.clear(bPie));
        bottomToolbar.add(ViewUtil.clear(bSort));
        bottomToolbar.add(ViewUtil.clear(bLog));

        bottomToolbar.add(Box.createHorizontalGlue());

        // b1.setSelected(true);

        this.add(bottomToolbar, BorderLayout.SOUTH);
    }

    public void setIsPie(boolean b) {
        if (bPie.isEnabled()) {
            sc.setIsPie(!sc.isPie());
            bPie.setSelected(sc.isPie());
        }
    }
    
    public void setIsSorted(boolean b) {
        if (bSort.isEnabled()) {
            sc.setIsSorted(!sc.isSorted());
            bSort.setSelected(sc.isSorted());
        }
    }
    
    public void setIsLogscale(boolean b) {
        if (bLog.isEnabled()) {
            sc.setIsLogscale(!sc.isLogscale());
            bLog.setSelected(sc.isLogscale());
        }
    }
}