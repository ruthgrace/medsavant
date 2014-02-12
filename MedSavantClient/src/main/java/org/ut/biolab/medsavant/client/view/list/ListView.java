/**
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.ut.biolab.medsavant.client.view.list;

import com.explodingpixels.macwidgets.MacIcons;
import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListClickListener;
import com.explodingpixels.macwidgets.SourceListContextMenuProvider;
import com.explodingpixels.macwidgets.SourceListControlBar;
import com.explodingpixels.macwidgets.SourceListDarkColorScheme;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.explodingpixels.macwidgets.SourceListSelectionListener;
import com.explodingpixels.widgets.PopupMenuCustomizer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ut.biolab.medsavant.client.login.LoginController;

import org.ut.biolab.medsavant.shared.model.ProgressStatus;
import org.ut.biolab.medsavant.client.util.ClientMiscUtils;
import org.ut.biolab.medsavant.client.util.MedSavantWorker;
import org.ut.biolab.medsavant.client.view.component.ListViewTablePanel;
import org.ut.biolab.medsavant.client.view.component.WaitPanel;
import org.ut.biolab.medsavant.client.view.images.IconFactory;

/**
 *
 * @author tarkvara
 */
public class ListView extends JPanel {

    private static final Log LOG = LogFactory.getLog(ListView.class);
    //TODO: handle limits better!
    static final int LIMIT = 10000;
    private static final String CARD_WAIT = "wait";
    private static final String CARD_SHOW = "show";
    private static final String CARD_ERROR = "error";
    private final String pageName;
    private final DetailedListModel detailedModel;
    private final DetailedView detailedView;
    private final DetailedListEditor detailedEditor;
    Object[][] data;
    private final JPanel showCard;
    private final JLabel errorMessage;
    //private int limit = 10000;
    SourceList sourceList;
    final SourceListModel listModel;
    private HashMap<SourceListItem, Integer> itemToIndexMap;
    private final SourceListControlBar controlBar;
    private String selectedItemMemory;

    public ListView(String page, DetailedListModel model, DetailedView view, DetailedListEditor editor) {
        pageName = page;
        detailedModel = model;
        detailedView = view;
        detailedEditor = editor;

        setLayout(new CardLayout());

        WaitPanel wp = new WaitPanel("Getting list");
        add(wp, CARD_WAIT);

        showCard = new JPanel();
        add(showCard, CARD_SHOW);

        listModel = new SourceListModel();

        sourceList = new SourceList(listModel);

        //sourceList.
        //sourceList.setColorScheme(new CustomColorScheme());
        controlBar = new SourceListControlBar();
        sourceList.installSourceListControlBar(controlBar);

        if (detailedEditor.doesImplementAdding()) {
            controlBar.createAndAddButton(MacIcons.PLUS, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    detailedEditor.addItems();
                    // In some cases, such as uploading/publishing variants, the addItems() method may have logged us out.
                    if (LoginController.getInstance().isLoggedIn()) {
                        if (detailedEditor.doesRefreshAfterAdding()) {
                            refreshList();
                        }
                    }
                }

            });
        }

        if (detailedEditor.doesImplementDeleting()) {

            final Runnable removeServer = new Runnable() {

                @Override
                public void run() {
                    SourceListItem item = sourceList.getSelectedItem();
                    if (item == null) {
                        return;
                    }

                    Integer selectedIndex = itemToIndexMap.get(item);

                    if (selectedIndex == null) {
                        return;
                    }

                    List<Object[]> selectedObjects = new ArrayList<Object[]>();
                    selectedObjects.add(data[selectedIndex]);
                    detailedEditor.deleteItems(selectedObjects);

                    // In some cases, such as removing/publishing variants, the deleteItems() method may have logged us out.
                    if (LoginController.getInstance().isLoggedIn()) {
                        if (detailedEditor.doesRefreshAfterDeleting()) {
                            refreshList();
                        }
                    }
                }

            };

            sourceList.addSourceListClickListener(new SourceListClickListener() {

                @Override
                public void sourceListItemClicked(SourceListItem sli, SourceListClickListener.Button button, int i) {
                    if (button == SourceListClickListener.Button.RIGHT) {

                        final JPopupMenu m = new JPopupMenu();

                        JMenuItem removeButton = new JMenuItem("Remove");
                        removeButton.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                m.setVisible(false);
                                removeServer.run();
                            }

                        });

                        //Point listPosition = sourceList.getComponent().getLocationOnScreen();
                        //m.setLocation(listPosition.x, listPosition.y);
                        m.add(removeButton);

                        m.setVisible(true);
                    }
                }

                @Override
                public void sourceListCategoryClicked(SourceListCategory slc, SourceListClickListener.Button button, int i) {
                }

            });

            controlBar.createAndAddButton(MacIcons.MINUS, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    removeServer.run();
                }
            });
        }

        if (detailedEditor.doesImplementEditing()) {
            controlBar.createAndAddButton(IconFactory.getInstance().getIcon(IconFactory.StandardIcon.GEAR), new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    SourceListItem item = sourceList.getSelectedItem();
                    if (item == null) {
                        return;
                    }

                    int selectedIndex = itemToIndexMap.get(item);

                    detailedEditor.editItem(data[selectedIndex]);
                    if (detailedEditor.doesRefreshAfterEditing()) {
                        refreshList();
                    }
                }

            });
        }

        if (detailedEditor.doesImplementImporting()) {
            controlBar.createAndAddButton(IconFactory.getInstance().getIcon(IconFactory.StandardIcon.IMPORT), new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    detailedEditor.importItems();
                    refreshList();
                }

            });
        }

        if (detailedEditor.doesImplementExporting()) {
            controlBar.createAndAddButton(IconFactory.getInstance().getIcon(IconFactory.StandardIcon.EXPORT), new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    detailedEditor.exportItems();
                    refreshList();

                }

            });
        }

        if (detailedEditor.doesImplementLoading()) {
            controlBar.createAndAddButton(IconFactory.getInstance().getIcon(IconFactory.StandardIcon.LOAD_ON_TOOLBAR), new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    SourceListItem item = sourceList.getSelectedItem();
                    if (item == null) {
                        return;
                    }

                    int selectedIndex = itemToIndexMap.get(item);

                    List<Object[]> selectedObjects = new ArrayList<Object[]>();
                    selectedObjects.add(data[selectedIndex]);

                    detailedEditor.loadItems(selectedObjects);
                    refreshList();

                }

            });
        }

        sourceList.setSourceListContextMenuProvider(new SourceListContextMenuProvider() {

            @Override
            public JPopupMenu createContextMenu() {
                return null;
            }

            @Override
            public JPopupMenu createContextMenu(SourceListItem sli) {
                return detailedView.createPopup();
            }

            @Override
            public JPopupMenu createContextMenu(SourceListCategory slc) {
                return null;
            }

        });

        sourceList.addSourceListSelectionListener(new SourceListSelectionListener() {

            @Override
            public void sourceListItemSelected(SourceListItem sli) {

                if (sli == null) {
                    detailedView.setSelectedItem(new Object[]{});
                    return;
                }

                selectedItemMemory = sli.getText();
                System.out.println("Remembering " + selectedItemMemory);

                Integer index = itemToIndexMap.get(sli);
                if (index == null) {
                    detailedView.setSelectedItem(new Object[]{});
                    return;
                }

                if (detailedView != null) {
                    detailedView.setSelectedItem(data[index]);
                }
            }

        });

        JPanel errorPanel = new JPanel();
        errorPanel.setLayout(new BorderLayout());
        errorMessage = new JLabel("An error occurred:");
        errorPanel.add(errorMessage, BorderLayout.NORTH);

        add(errorPanel, CARD_ERROR);

        showWaitCard();
        fetchList();
    }

    private void showWaitCard() {
        ((CardLayout) getLayout()).show(this, CARD_WAIT);
    }

    private void showShowCard() {
        Component c = this.getParent();
        if (c instanceof JSplitPane) {
            controlBar.installDraggableWidgetOnSplitPane((JSplitPane) c);
        }
        ((CardLayout) getLayout()).show(this, CARD_SHOW);
        this.updateUI();
    }

    private void showErrorCard(String message) {
        errorMessage.setText(String.format("<html><font color=\"#ff0000\">An error occurred:<br><font size=\"-2\">%s</font></font></html>", message));
        ((CardLayout) getLayout()).show(this, CARD_ERROR);
    }

    private synchronized void setList(Object[][] list) {
        data = list;
        try {
            updateShowCard();
            showShowCard();
        } catch (Exception ex) {
            LOG.error("Unable to load list.", ex);
            showErrorCard(ClientMiscUtils.getMessage(ex));
        }
    }

    void refreshList() {
        System.out.println("Refreshing list");

        showWaitCard();
        fetchList();
    }

    private void fetchList() {

        new MedSavantWorker<Object[][]>(pageName) {
            @Override
            protected Object[][] doInBackground() throws Exception {
                try {
                    Object[][] result = detailedModel.getList(LIMIT);
                    return result;
                } catch (Throwable t) {
                    t.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void showProgress(double ignored) {
            }

            @Override
            protected void showSuccess(Object[][] result) {
                setList(result);
            }

            @Override
            protected ProgressStatus checkProgress() {
                return new ProgressStatus("Working", 0.5);
            }
        }.execute();
    }

    private void updateShowCard() {
        showCard.removeAll();

        showCard.setLayout(new BorderLayout());

        showCard.add(sourceList.getComponent(), BorderLayout.CENTER);

        int visibleColumn = 0;
        // determine the first visible column from the list of hidden ones
        if (detailedModel.getHiddenColumns().length != 0) {
            while (detailedModel.getHiddenColumns()[visibleColumn] == visibleColumn) {
                visibleColumn++;
            }
        }

        if (!listModel.getCategories().isEmpty()) {
            listModel.removeCategoryAt(0);
        }

        String categoryName = pageName;
        if (data.length > 10) {
            categoryName += " (" + data.length + ")";
        }

        SourceListCategory category = new SourceListCategory(categoryName);
        listModel.addCategory(category);

        itemToIndexMap = new HashMap<SourceListItem, Integer>();
        int counter = 0;
        SourceListItem item;

        SourceListItem itemToSelect = null;

        System.out.println("Updating index map");
        for (Object[] row : data) {

            String label = row[visibleColumn].toString();
            listModel.addItemToCategory(item = new SourceListItem(label), category);

            itemToIndexMap.put(item, counter++);

            if (item.getText().equals(selectedItemMemory)) {
                itemToSelect = item;
            }
        }

        if (itemToSelect != null) {
            System.out.println("Reselecting " + selectedItemMemory);
            sourceList.setSelectedItem(itemToSelect);
        } else {
            System.out.println("Could not reselect " + selectedItemMemory);
        }

        // need to add an item otherwise the category title doesn't display
        if (data.length == 0) {
            listModel.addItemToCategory(item = new SourceListItem(""), category);
        }
    }

    void selectItemWithKey(String key) {

        System.out.println("Remembering " + key);
        this.selectedItemMemory = key;

        try {
            for (SourceListItem i : listModel.getCategories().get(0).getItems()) {
                if (i.getText().equals(key)) {
                    sourceList.setSelectedItem(i);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
