/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ut.biolab.medsavant.util;

import fiume.vcf.VariantRecord;
import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import org.ut.biolab.medsavant.model.VariantRecordModel;

/**
 *
 * @author mfiume
 */
public class Util {

    public static Vector listToVector(List l) {
        Vector v = new Vector(l.size());
        v.addAll(l);
        return v;
    }

    public static Vector getVariantRecordsVector(List<VariantRecord> list) {
        Vector result = new Vector();
        for (VariantRecord r : list) {
            Vector v = VariantRecordModel.convertToVector(r);
            result.add(v);
        }
        return result;
    }

   private static Random numGen = new Random();

   public static Color getRandomColor() {
      return new Color(numGen.nextInt(256), numGen.nextInt(256), numGen.nextInt(256));
   }

}
