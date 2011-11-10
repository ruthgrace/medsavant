package org.ut.biolab.medsavant.view.patients.cohorts;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.ut.biolab.medsavant.db.model.Cohort;
import org.ut.biolab.medsavant.db.util.query.CohortQueryUtil;
import org.ut.biolab.medsavant.view.MainFrame;
import org.ut.biolab.medsavant.view.dialog.AddCohortForm;
import org.ut.biolab.medsavant.view.list.DetailedListEditor;
import org.ut.biolab.medsavant.view.util.DialogUtils;

/**
 *
 * @author mfiume
 */
public class CohortDetailedListEditor extends DetailedListEditor {

    @Override
    public boolean doesImplementAdding() {
        return true;
    }

    @Override
    public boolean doesImplementDeleting() {
        return true;
    }

    @Override
    public void addItems() {
        new AddCohortForm();

    }

    @Override
    public void editItems(Vector results) {
    }

    @Override
    public void deleteItems(List<Vector> items) {

        int result;

        if (items.size() == 1) {
            String name = ((Cohort) items.get(0).get(0)).getName();
            result = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                    "Are you sure you want to remove " + name + "?\nThis cannot be undone.",
                    "Confirm", JOptionPane.YES_NO_OPTION);
        } else {
            result = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                    "Are you sure you want to remove these " + items.size() + " cohorts?\nThis cannot be undone.",
                    "Confirm", JOptionPane.YES_NO_OPTION);
        }


        if (result == JOptionPane.YES_OPTION) {
            int numCouldntRemove = 0;
            for (Vector v : items) {
                int id = ((Cohort) v.get(0)).getId();
                try {
                    CohortQueryUtil.removeCohort(id);
                } catch (SQLException ex) {
                    numCouldntRemove++;
                    DialogUtils.displayErrorMessage("Couldn't remove " + ((Cohort) v.get(0)).getName(), ex);
                }
            }

            if (numCouldntRemove != items.size()) {
                DialogUtils.displayMessage("Successfully removed " + (items.size() - numCouldntRemove) + " cohort(s)");
            }
        }
    }
}