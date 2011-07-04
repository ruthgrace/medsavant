/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ut.biolab.medsavant.view.genetics;

import com.jidesoft.utils.SwingWorker;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.ut.biolab.medsavant.db.ConnectionController;
import org.ut.biolab.medsavant.db.MedSavantDatabase;
import org.ut.biolab.medsavant.db.QueryUtil;
import org.ut.biolab.medsavant.db.table.TableSchema;
import org.ut.biolab.medsavant.exception.NonFatalDatabaseException;
import org.ut.biolab.medsavant.model.record.Chromosome;

/**
 *
 * @author mfiume, AndrewBrook
 */
public class ChromosomeDiagramPanel extends JPanel {

    private long scaleWRTLength;
    private final Chromosome chr;
    private List<RangeAnnotation> annotations;
    private static final int BINSIZE = 15000000;

    public ChromosomeDiagramPanel(Chromosome c) {
        this.chr = c;
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(20,999));
        this.setMaximumSize(new Dimension(20,999));
        annotations = new ArrayList<RangeAnnotation>();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        long cent = chr.getCentromerepos();
        if (cent == -1) {
            cent = chr.getLength()/2;
        }
        
        int centView = translateModelToView(cent, chr.getLength(), this.getEffectiveHeight());
        GradientPaint p = new GradientPaint(0,0,Color.white,0, this.getHeight(), Color.lightGray);

        int bend = 20;

        g2.setPaint(p);
        g2.fillRoundRect(0, 0, this.getWidth(), centView,bend,bend);
        g2.setColor(Color.gray);
        RoundRectangle2D rec1 = new RoundRectangle2D.Double(0, 0, this.getWidth(), centView,bend,bend);
        g2.draw(rec1);

        g2.setPaint(p);
        g2.fillRoundRect(0, centView, this.getWidth(), this.getEffectiveHeight()-centView,bend,bend);
        g2.setColor(Color.gray);
        RoundRectangle2D rec2 = new RoundRectangle2D.Double(0, centView, this.getWidth(), this.getEffectiveHeight()-centView,bend,bend);
        g2.draw(rec2);

        Area shape = new Area(rec1);
        shape.add(new Area(rec2));
            
        g2.clip(shape);
        for (RangeAnnotation a : annotations) {
            int viewStart = translateModelToView(a.getStart(), chr.getLength(), this.getEffectiveHeight());
            int viewEnd = translateModelToView(a.getEnd(), chr.getLength(), this.getEffectiveHeight());
            if (viewEnd-viewStart < 1) { viewEnd = viewStart+1; }
            g2.setColor(a.getColor());           
            g2.fillRect(0, viewStart, this.getWidth(), viewEnd-viewStart);           
        }
    }

    protected void setScaleWithRespectToLength(long len) {
        this.scaleWRTLength = len;
        repaint();
    }

    private int getEffectiveHeight() {
        return (int) ((this.getHeight()*chr.getLength())/scaleWRTLength);
    }

    private static int translateModelToView(long modelPosition, long totalViewSize, int totalModelSize) {
        return (int) (modelPosition*totalModelSize/totalViewSize);
    }

    public synchronized void setAnnotations(List<RangeAnnotation> annotations) {
        this.annotations = annotations;
        repaint();
    }
    
    public void update(int totalNum){
        updateAnnotations(totalNum);
    }
    
    private void updateAnnotations(int totalNum) {
        
        TableSchema tableSchema = MedSavantDatabase.getInstance().getVariantTableSchema();
        List<RangeAnnotation> as = new ArrayList<RangeAnnotation>();
        
        List<Integer> nums = new ArrayList<Integer>();
       
        try {
            for(int i = 0; i < chr.getLength(); i += BINSIZE){
                int numVariants = QueryUtil.getNumVariantsInRange(                  
                        ConnectionController.connect(),
                        tableSchema,
                        chr.getName(),
                        i,
                        i + BINSIZE);
                nums.add(numVariants); 
                if(numVariants > 0 && totalNum >= 1){
                    float alpha = 0.15f + (0.85f * (float)((double)numVariants / (double)totalNum));                 
                    as.add(new RangeAnnotation(i, i + BINSIZE, new Color(0.0f, 0.7f, 0.87f, alpha)));
                }
            }
            
        } catch (NonFatalDatabaseException ex) {
            Logger.getLogger(ChromosomeDiagramPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ChromosomeDiagramPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setAnnotations(as);
    }
    
}
