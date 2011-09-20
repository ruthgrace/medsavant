package org.ut.biolab.medsavant.db.admin;

import java.sql.SQLException;
import org.ut.biolab.medsavant.db.util.DBSettings;
import org.ut.biolab.medsavant.db.util.DBUtil;

/**
 *
 * @author mfiume
 */
public class Info {
    public static void main(String[] args) throws SQLException {
        DBUtil.importTable(DBSettings.DBNAME,"project");
        DBUtil.importTable(DBSettings.DBNAME,"patient_tableinfo");
    }
}
