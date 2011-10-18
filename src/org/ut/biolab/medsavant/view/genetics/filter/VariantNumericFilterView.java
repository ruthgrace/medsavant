/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.view.genetics.filter;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.ComboCondition;
import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.jidesoft.swing.RangeSlider;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.ut.biolab.medsavant.controller.ProjectController;
import org.ut.biolab.medsavant.db.util.query.VariantQueryUtil;
import org.ut.biolab.medsavant.db.exception.NonFatalDatabaseException;
import org.ut.biolab.medsavant.controller.FilterController;
import org.ut.biolab.medsavant.model.Filter;
import org.ut.biolab.medsavant.model.QueryFilter;
import org.ut.biolab.medsavant.db.model.Range;
import org.ut.biolab.medsavant.view.util.ViewUtil;

/**
 *
 * @author AndrewBrook
 */
public class VariantNumericFilterView {
    
    public static FilterView createFilterView(String tablename, final String columnname, final int queryId, final String alias) throws SQLException, NonFatalDatabaseException {

        Range extremeValues = null;

        if (columnname.equals("position")) {
            extremeValues = new Range(1,250000000);
        } else if (columnname.equals("sb")) {
            extremeValues = new Range(-100,100);
        } else {
            extremeValues = new Range(VariantQueryUtil.getExtremeValuesForColumn(tablename, columnname));
        }

        if (columnname.equals("dp")) {
            extremeValues = new Range(Math.min(0, extremeValues.getMin()),extremeValues.getMax());
        }

        JPanel container = new JPanel();
        container.setBorder(ViewUtil.getMediumBorder());
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        final RangeSlider rs = new com.jidesoft.swing.RangeSlider();

        final int min = (int) Math.floor(extremeValues.getMin());
        final int max = (int) Math.ceil(extremeValues.getMax());

        rs.setMinimum(min);
        rs.setMaximum(max);

        rs.setMajorTickSpacing(5);
        rs.setMinorTickSpacing(1);

        rs.setLowValue(min);
        rs.setHighValue(max);

        JPanel rangeContainer = new JPanel();
        rangeContainer.setLayout(new BoxLayout(rangeContainer, BoxLayout.X_AXIS));

        final JTextField frombox = new JTextField(ViewUtil.numToString(min));
        final JTextField tobox = new JTextField(ViewUtil.numToString(max));
        frombox.setMaximumSize(new Dimension(10000,24));
        tobox.setMaximumSize(new Dimension(10000,24));

        final JLabel fromLabel = new JLabel(ViewUtil.numToString(min));
        final JLabel toLabel = new JLabel(ViewUtil.numToString(max));

        rangeContainer.add(fromLabel);
        rangeContainer.add(rs);
        rangeContainer.add(toLabel);

        container.add(frombox);
        container.add(tobox);
        container.add(rangeContainer);
        container.add(Box.createVerticalBox());

        final JButton applyButton = new JButton("Apply");
        applyButton.setEnabled(false);

        rs.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {
                frombox.setText(ViewUtil.numToString(rs.getLowValue()));
                tobox.setText(ViewUtil.numToString(rs.getHighValue()));
            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });

        frombox.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    try {
                        Range acceptableRange = new Range(getNumber(frombox.getText().replaceAll(",", "")), getNumber(tobox.getText().replaceAll(",", "")));
                        acceptableRange.bound(min, max, true);                     
                        frombox.setText(ViewUtil.numToString(acceptableRange.getMin()));
                        tobox.setText(ViewUtil.numToString(acceptableRange.getMax()));
                        rs.setLowValue((int)acceptableRange.getMin());
                        rs.setHighValue((int)acceptableRange.getMax());           
                        applyButton.setEnabled(true);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        frombox.requestFocus();
                    }
                }
            }                
        });

        tobox.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    try {
                        Range acceptableRange = new Range(getNumber(frombox.getText().replaceAll(",", "")), getNumber(tobox.getText().replaceAll(",", "")));
                        acceptableRange.bound(min, max, false);                     
                        frombox.setText(ViewUtil.numToString(acceptableRange.getMin()));
                        tobox.setText(ViewUtil.numToString(acceptableRange.getMax()));
                        rs.setLowValue((int)acceptableRange.getMin());
                        rs.setHighValue((int)acceptableRange.getMax());      
                        applyButton.setEnabled(true);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        frombox.requestFocus();
                    }
                }   
            }                   
        });

        //
                
        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                applyButton.setEnabled(false);

                Range acceptableRange = new Range(getNumber(frombox.getText().replaceAll(",", "")), getNumber(tobox.getText().replaceAll(",", "")));
                acceptableRange.bound(min, max, true);                     
                frombox.setText(ViewUtil.numToString(acceptableRange.getMin()));
                tobox.setText(ViewUtil.numToString(acceptableRange.getMax()));
                rs.setLowValue((int)acceptableRange.getMin());
                rs.setHighValue((int)acceptableRange.getMax());

                //if (min == acceptableRange.getMin() && max == acceptableRange.getMax()) {
                //    FilterController.removeFilter(columnname, queryId);
                //} else {
                    Filter f = new QueryFilter() {

                        @Override
                        public Condition[] getConditions() {
                            Condition[] results = new Condition[2];
                            results[0] = BinaryCondition.greaterThan(new DbColumn(ProjectController.getInstance().getCurrentVariantTable(), columnname, "decimal", 1), getNumber(frombox.getText().replaceAll(",", "")), true);
                            results[1] = BinaryCondition.lessThan(new DbColumn(ProjectController.getInstance().getCurrentVariantTable(), columnname, "decimal", 1), getNumber(tobox.getText().replaceAll(",", "")), true);

                            Condition[] resultsCombined = new Condition[1];
                            resultsCombined[0] = ComboCondition.and(results);

                            return resultsCombined;
                        }

                        @Override
                        public String getName() {
                            return alias;
                        }

                        @Override
                        public String getId() {
                            return columnname;
                        }
                    };
                    //Filter f = new VariantRecordFilter(acceptableValues, fieldNum);
                    FilterController.addFilter(f, queryId);
                //}

                //TODO: why does this not work? Freezes GUI
                //apply.setEnabled(false);
            }
        };
        applyButton.addActionListener(al);

        rs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                applyButton.setEnabled(true);
            }
        });

        JButton selectAll = ViewUtil.createHyperLinkButton("Select All");
        selectAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rs.setLowValue(min);
                rs.setHighValue(max);
                frombox.setText(ViewUtil.numToString(min));
                tobox.setText(ViewUtil.numToString(max));
                applyButton.setEnabled(true);
            }
        });

        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.X_AXIS));

        bottomContainer.add(selectAll);
        bottomContainer.add(Box.createHorizontalGlue());
        bottomContainer.add(applyButton);

        container.add(bottomContainer);

        al.actionPerformed(null);
        return new FilterView(alias, container);
    }
    
    public static double getNumber(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ex){
            return 0;
        }
    }
         
}
