/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.view.images;

import javax.swing.ImageIcon;

/**
 *
 * @author Marc Fiume
 */
public class IconFactory {

    static IconFactory instance;

    public IconFactory() {
    }

    public static IconFactory getInstance() {
        if (instance == null) {
            instance = new IconFactory();
        }
        return instance;
    }

    public ImageIcon getIcon(String resourcePath) {
        return new ImageIcon(getClass().getResource(resourcePath));
    }

    public enum StandardIcon {
        SECTION_ADMIN,
        SECTION_OTHER,
        SECTION_VARIANTS,
        SECTION_PATIENTS,
        TRASH,
        DELETE,
        ADD_ON_TOOLBAR,
        REMOVE_ON_TOOLBAR,
        ADD,
        REMOVE,
        EXPAND,
        COLLAPSE,
        EDIT,
        FILTER,
        RESULTS,
        CHART,
        LOGGED_IN,
        SPIRAL,
        TAB_LEFT,
        TAB_RIGHT,
        LOGO,
        SAVE,
        FIRST,
        LAST,
        NEXT,
        PREVIOUS,
        GREEN,
        ORANGE,
        RED,
        WHITE
    };
    private static final String iconroot = "/org/ut/biolab/medsavant/view/images/icon/";

    public ImageIcon getIcon(StandardIcon icon) {
        switch (icon) {
            case SECTION_ADMIN:
                return getIcon(iconroot + "section_admin.png");
            case SECTION_OTHER:
                return getIcon(iconroot + "section_other3.png");
            case SECTION_PATIENTS:
                return getIcon(iconroot + "section_patients.png");
            case SECTION_VARIANTS:
                return getIcon(iconroot + "section_variants2.png");
            case TRASH:
                return getIcon(iconroot + "trash.png");
            case DELETE:
                return getIcon(iconroot + "delete.png");
            case SAVE:
                return getIcon(iconroot + "save2.png");
            case GREEN:
                return getIcon(iconroot + "green.png");
            case ORANGE:
                return getIcon(iconroot + "orange.png");
            case RED:
                return getIcon(iconroot + "red.png");
            case WHITE:
                return getIcon(iconroot + "white.png");
            case EXPAND:
                return getIcon(iconroot + "expand.png");
            case COLLAPSE:
                return getIcon(iconroot + "collapse.png");
            case ADD_ON_TOOLBAR:
                return getIcon(iconroot + "mac_add.png");
            case REMOVE_ON_TOOLBAR:
                return getIcon(iconroot + "mac_remove.png");
            case ADD:
                return getIcon(iconroot + "add_f.png");
            case REMOVE:
                return getIcon(iconroot + "rem_f.png");
            case EDIT:
                return getIcon(iconroot + "mac_edit.png");
            case FILTER:
                return getIcon(iconroot + "filter.gif");
            case RESULTS:
                return getIcon(iconroot + "results.png");
            case CHART:
                return getIcon(iconroot + "chart.png");
            case LOGGED_IN:
                return getIcon(iconroot + "loggedin.png");
            case SPIRAL:
                return getIcon(iconroot + "spiral_green.png");
            case LOGO:
                return getIcon(iconroot + "medsavantlogo.png");
            case FIRST:
                return getIcon(iconroot + "first.png");
            case PREVIOUS:
                return getIcon(iconroot + "previous.png");
            case NEXT:
                return getIcon(iconroot + "next.png");
            case LAST:
                return getIcon(iconroot + "last.png");
            case TAB_LEFT:
                return getIcon(iconroot + "tab_l.png");
            case TAB_RIGHT:
                return getIcon(iconroot + "tab_r.png");
            default:
                return null;
        }
    }
}
