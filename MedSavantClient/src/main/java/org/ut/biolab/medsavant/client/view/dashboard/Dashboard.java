package org.ut.biolab.medsavant.client.view.dashboard;

import org.ut.biolab.medsavant.client.view.app.MenuFactory;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ut.biolab.medsavant.client.api.Listener;
import org.ut.biolab.medsavant.client.view.MedSavantFrame;
import org.ut.biolab.medsavant.client.view.component.NiceMenu;
import org.ut.biolab.medsavant.client.view.images.IconFactory;
import org.ut.biolab.medsavant.client.view.util.NavigationPanel;
import org.ut.biolab.medsavant.client.view.util.ViewUtil;
import org.ut.biolab.savant.analytics.savantanalytics.AnalyticsAgent;
import savant.util.swing.HyperlinkButton;
import org.apache.commons.httpclient.NameValuePair;

/**
 *
 * @author mfiume
 */
public class Dashboard extends JPanel implements Listener<DashboardSection> {

    private static Log LOG = LogFactory.getLog(Dashboard.class);

    int appIconWidth = 128;

    private final ArrayList<DashboardSection> dashboardSections;
    private final JPanel baseLayer;
    private LaunchableApp previousApp;
    private final JPanel appLayer;

    private final String BASE_LAYER = "0";
    private final String APP_LAYER = "1";
    private final CardLayout cardLayout;

    private NiceMenu appTopMenu;
    private NiceMenu homeMenu;
    private final LimitedQueue<LaunchableApp> history;
    private final HashSet<LaunchableApp> appHistoryBlackList;

    public Dashboard() {

        history = new LimitedQueue<LaunchableApp>(11);
        appHistoryBlackList = new HashSet<LaunchableApp>();

        this.setDoubleBuffered(true);

        this.setBackground(Color.white);

        cardLayout = new CardLayout();

        this.setLayout(cardLayout);

        dashboardSections = new ArrayList<DashboardSection>();

        baseLayer = new JPanel();
        baseLayer.setBackground(Color.white);

        this.add(baseLayer, BASE_LAYER);

        appLayer = new JPanel();
        appLayer.setBackground(Color.white);
        appLayer.setLayout(new BorderLayout());

        this.add(appLayer, APP_LAYER);

        cardLayout.show(this, BASE_LAYER);

        this.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                relayout();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
    }

    public void addDashboardSection(DashboardSection s) {
        this.dashboardSections.add(s);
    }

    private void relayout() {

        baseLayer.removeAll();

        JPanel middlePane = ViewUtil.getClearPanel();

        int gapHorizontal = 30;
        int gapVertical = gapHorizontal;
        int topAndBottomInsets = 100;

        int widthOfContainer = this.getParent().getSize().width;

        int centralWidth = Math.min((int)Math.round(widthOfContainer*0.75), 1200); // TODO: centralize width for other apps to use

        int numIconsPerRow = (centralWidth + gapHorizontal) / (appIconWidth + gapHorizontal);

        int leftInset = widthOfContainer / 2 - (numIconsPerRow * (appIconWidth + gapHorizontal)) / 2;
        int rightInset = 0;

        middlePane.setLayout(new MigLayout(String.format("gapy %d, insets %d %d %d %d", 0, topAndBottomInsets, leftInset, topAndBottomInsets, rightInset)));

        baseLayer.setOpaque(true);

        baseLayer.setLayout(new BorderLayout());

        for (DashboardSection s : this.dashboardSections) {

            if (!s.isEnabled()) {
                continue;
            }

            if (s.getApps().isEmpty()) {
                continue;
            }

            if (!s.getName().equals("Apps")) {
                JLabel l = ViewUtil.getSubtleHeaderLabel(s.getName().toUpperCase());
                middlePane.add(l, "wrap, center");
            }

            JPanel appPlaceholder = ViewUtil.getClearPanel();

            MigLayout layout = new MigLayout(String.format("gapx %d, gapy %d, wrap %d, insets 0", gapHorizontal, gapVertical, numIconsPerRow));
            appPlaceholder.setLayout(layout);
            for (final LaunchableApp launcher : s.getApps()) {
                try {
                    appPlaceholder.add(getRepresentationForLauncher(this, launcher, appIconWidth));
                } catch (Exception e) {
                    LOG.error("Error creating launcher for app " + launcher.toString(), e);
                    e.printStackTrace();
                }
            }
            middlePane.add(appPlaceholder, String.format("wrap, gapy 5 %d", gapVertical));
        }

        JScrollPane p = ViewUtil.getClearBorderlessScrollPane(middlePane);
        p.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        MenuFactory.generateMenu(); // initialize the Apps in the menus

        JPanel bottomDisclaimer = ViewUtil.getClearPanel();
        bottomDisclaimer.setLayout(new MigLayout("gapx 10, fillx, insets 8"));
        
        JLabel copy = new JLabel("Developed at University of Toronto");
        copy.setForeground(ViewUtil.getSubtleTitleColor());
        bottomDisclaimer.add(copy);
        
        JComponent feedback = ViewUtil.createHyperlinkButton("Send Feedback", ViewUtil.getMedSavantBlueColor(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    URI uri = URI.create(MedSavantFrame.FEEDBACK_URI);
                    Desktop.getDesktop().mail(uri);
                } catch (Exception ex) {
                }
            }
        });
        bottomDisclaimer.add(feedback,"split, right");
        
        JComponent userguide = ViewUtil.createHyperlinkButton("User Guide", ViewUtil.getMedSavantBlueColor(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    URI uri = URI.create(MedSavantFrame.USERGUIDE_URI);
                    Desktop.getDesktop().browse(uri);
                } catch (Exception ex) {
                }
            }
        });
        bottomDisclaimer.add(userguide,"right");
        
        homeMenu = new NiceMenu();
        
        homeMenu.setTitle("MedSavant");
        homeMenu.addRightComponent(getLogoutButton());
        
        baseLayer.add(homeMenu, BorderLayout.NORTH);

        baseLayer.add(p, BorderLayout.CENTER);
        baseLayer.add(bottomDisclaimer,BorderLayout.SOUTH);
        baseLayer.updateUI();
    }

    public void goHome() {

        if (previousApp != null) {
            previousApp.viewWillUnload();
        }

        cardLayout.show(this, BASE_LAYER);

        if (previousApp != null) {
            previousApp.viewDidUnload();
        }

        previousApp = null;
    }

    public void launchApp(LaunchableApp app) {
        
        if (history.contains(app)) {
            history.remove(app);
        }
        if (!appHistoryBlackList.contains(app)) {
            history.add(app);
        }

        if (previousApp != null) {
            previousApp.viewWillUnload();
        }
        app.viewWillLoad();
        appLayer.removeAll();

        JPanel p = app.getView();

        appTopMenu = new NiceMenu();

        final NavigationPanel navigationPanel = new NavigationPanel();
        navigationPanel.setTitle(app.getName());
        navigationPanel.setTitleClickAction(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                JPopupMenu m = MenuFactory.generatePrettyMenu();

                m.show(appTopMenu, (int) ((appTopMenu.getSize().getSize().getWidth() / 2) - (m.getPreferredSize().getWidth() / 2)), (int) (appTopMenu.getSize().getHeight()));
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        });

        appTopMenu.addLeftComponent(getHomeButton());
        appTopMenu.setCenterComponent(navigationPanel);
        appTopMenu.addRightComponent(getLogoutButton());

        appLayer.add(appTopMenu, BorderLayout.NORTH);
        appLayer.add(p, BorderLayout.CENTER);
        cardLayout.show(this, APP_LAYER);

        if (previousApp != null) {
            previousApp.viewDidUnload();
        }
        app.viewDidLoad();
        AnalyticsAgent.log(new NameValuePair("app-launched",app.getName()));
        previousApp = app;

        appLayer.updateUI();
    }

    public static JPanel getRepresentationForLauncher(String name, ImageIcon icon, int iconWidth, ActionListener actionListener) {
        return getRepresentationForLauncher(name, icon, iconWidth, actionListener, false);
    }

    public static JPanel getRepresentationForLauncher(String name, ImageIcon icon, int iconWidth, ActionListener actionListener, boolean disableButton) {
        JPanel p = ViewUtil.getClearPanel();
        ViewUtil.applyVerticalBoxLayout(p);

        JButton button = ViewUtil.getIconButton(resizeIconTo(icon, iconWidth));

        button.addActionListener(actionListener);

        p.add(ViewUtil.centerHorizontally(button));
        p.add(Box.createVerticalStrut(3));

        if (iconWidth <= 64) {
            JLabel title = ViewUtil.getGrayLabel(name);
            title.setFont(new Font(ViewUtil.getDefaultFontFamily(), disableButton ? Font.BOLD : Font.PLAIN, 12));
            ViewUtil.ellipsizeLabel(title, iconWidth);
            p.add(ViewUtil.centerHorizontally(title));
            //button.setEnabled(!disableButton); // disable if selected

            //button.setToolTipText(name);
        } else {
            JLabel title = ViewUtil.getGrayLabel(name);
            ViewUtil.ellipsizeLabel(title, iconWidth);
            title.setFont(new Font(ViewUtil.getDefaultFontFamily(), disableButton ? Font.BOLD : Font.PLAIN, 15));
            p.add(ViewUtil.centerHorizontally(title));
        }

        return p;
    }

    public static JPanel getRepresentationForLauncher(final Dashboard d, final LaunchableApp launcher, int iconWidth) {

        return getRepresentationForLauncher(launcher.getName(), launcher.getIcon(), iconWidth, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                d.launchApp(launcher);
            }
        });
    }

    private static ImageIcon resizeIconTo(ImageIcon icon, int itemSize) {
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(itemSize, itemSize, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

    public List<LaunchableApp> getLaunchHistory() {
        
        List<LaunchableApp> list = new ArrayList<LaunchableApp>(history.size());
        for (LaunchableApp e : history) {
            list.add(0, e);
        }
        
        return list;
    }

    public LaunchableApp getCurrentApp() {
        return this.previousApp;
    }

    public void blackListAppFromHistory(LaunchableApp app) {
        appHistoryBlackList.add(app);
    }

    @Override
    public void handleEvent(DashboardSection event) {
        relayout();
    }
    
    /*private JButton getHomeButton() {
        JButton b = ViewUtil.getIconButton(IconFactory.getInstance().getIcon(IconFactory.StandardIcon.DASHBOARD),3);
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                goHome();
            }
            
        });
        return b;
    }*/

    
    private JComponent getHomeButton() {
       

        final ActionListener goHomeActionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                goHome();
            }
        };
        
        JButton goHome = ViewUtil.getSoftButton("Home");
        goHome.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                goHomeActionListener.actionPerformed(null);
            }
            
        });
        
        return goHome;
        
        /*
         JLabel homeLabel = ViewUtil.getEmphasizedLabel("HOME", ViewUtil.getMedSavantBlueColor());
        
        homeLabel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                goHomeActionListener.actionPerformed(null);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        });
        
        homeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return homeLabel;
        */
    }

    private JComponent getLogoutButton() {
        
        final ActionListener signOutActionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                MedSavantFrame.getInstance().requestLogoutAndRestart();
            }
        };
       
        JButton signOut = ViewUtil.getSoftButton("Sign Out");
        signOut.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                signOutActionListener.actionPerformed(null);
            }
            
        });
        
        return signOut;
        
        /*
        final JLabel label = ViewUtil.getEmphasizedLabel("SIGN OUT", ViewUtil.getMedSavantBlueColor());
        
        label.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                goHomeActionListener.actionPerformed(null);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        });

        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return label;
                */
    }

    private class LimitedQueue<E> extends LinkedList<E> {

        private int limit;

        public LimitedQueue(int limit) {
            this.limit = limit;
        }

        @Override
        public boolean add(E o) {
            super.add(o);
            while (size() > limit) {
                super.remove();
            }
            return true;
        }

    }

}
