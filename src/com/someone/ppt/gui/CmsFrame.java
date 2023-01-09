package com.someone.ppt.gui;

import com.someone.io.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CmsFrame extends JFrame {
    protected static final int DB_GUI = 1;
    protected static final int LINE_GUI = 2;
    protected static final int PACKHOUSE_GUI = 3;
    protected static final int PRINTER_GUI = 4;
    protected static final int REPORT_GUI = 5;
    protected JMenuBar menuBar;
    protected JMenu menuFile;
    protected int guiType;
    protected CmsGui mainGui;

    protected CmsFrame(final CmsGui mainGui, final String title) throws HeadlessException {
        super("    " + title);

        try {
            final ImageIcon icon = new ImageIcon(FileProxy.getURL("icons/home.gif"));
            //ImageIcon icon = new ImageIcon("icons/Home.gif");
            this.setIconImage(icon.getImage());
        } catch (final RuntimeException e) {
            System.out.println("could not load icon from /icons directory");
        }

        this.mainGui = mainGui;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        createMenus();
        maximise();
        setVisible(false);

        addWindowListener(new CloseWindow());

    }

    /**
     * WindowAdapter extension to close the window
     */
    private class CloseWindow extends WindowAdapter {
        public void windowClosing(final WindowEvent evt) {
            mainGui.shutDownProgram();
        }
    }

/*   public CmsFrame(CmsGui mainGui, String title, int guiType) throws HeadlessException {
	   super("    " + title);
	   this.guiType = guiType;
	   try {
		   ImageIcon icon = new ImageIcon(FileProxy.getURL("icons/home.gif"));
		   this.setIconImage(icon.getImage());
	   } catch (RuntimeException e) {
		   System.out.println("could not load icon from /icons directory");
	   }

	   this.mainGui = mainGui;

	   setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	   createMenus();
	   maximise();
	   setVisible(false);
  } */


    protected CmsFrame(final CmsGui mainGui, final String title, final int guiType) throws HeadlessException {
        this(mainGui, title);
        this.guiType = guiType;
    }

    private void maximise() {
        final Toolkit kit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = kit.getScreenSize();
        final GraphicsConfiguration config = getGraphicsConfiguration();
        final Insets insets = kit.getScreenInsets(config);
        screenSize.width -= (insets.left + insets.right);
        screenSize.height -= (insets.top + insets.bottom);
        setSize(screenSize);
        setLocation(insets.left, insets.top);
    }

    protected void defineLocalMenus() {
    }

    private void createMenus() {
        menuBar = new JMenuBar();


        // File menu -------------------------
        menuFile = new JMenu();

        final JMenuItem menuFileExit = new JMenuItem();

        menuFile.setText("File");
        menuFile.setMnemonic('f');
        menuBar.add(menuFile);


        // Local menus -------------------------
        defineLocalMenus();


        // Application menus -------------------------
        menuFileExit.setText("Exit");
        menuFileExit.setMnemonic('x');
        menuFileExit.addActionListener(ae -> mainGui.shutDownProgram());

        menuFile.add(menuFileExit);

        if (guiType == REPORT_GUI) {
            menuBar.add(menuFile);
            setJMenuBar(menuBar);
            return;
        }

        final JMenu menuApplication = new JMenu();
        menuApplication.setText("Applications");
        menuApplication.setMnemonic('a');

        final JMenuItem databaseGui = new JMenuItem();
        final JMenuItem packhouseBuilder = new JMenuItem();
        final JMenuItem lineSetup = new JMenuItem();
        final JMenuItem printerGui = new JMenuItem();

        databaseGui.setText("Database Administration");
        databaseGui.setMnemonic('d');
        databaseGui.addActionListener(ae -> {
            CmsFrame.this.setVisible(false);
            mainGui.showDbGui();
        });

        packhouseBuilder.setText("Packhouse Builder");
        packhouseBuilder.addActionListener(ae -> {
            CmsFrame.this.setVisible(false);
            mainGui.showPackhouseBuilder();
        });
        packhouseBuilder.setMnemonic('p');

        lineSetup.setText("Line Setup");
        lineSetup.addActionListener(ae -> {
            CmsFrame.this.setVisible(false);
            mainGui.showLineConfiguration();
        });
        lineSetup.setMnemonic('l');

        printerGui.setText("Printing");
        printerGui.addActionListener(ae -> {
            CmsFrame.this.setVisible(false);
            mainGui.showPrinterConfiguration();
        });
        printerGui.setMnemonic('r');

        switch (guiType) {
            case DB_GUI:
                databaseGui.setEnabled(false);

                break;

            case LINE_GUI:
                lineSetup.setEnabled(false);

                break;

            case PACKHOUSE_GUI:
                packhouseBuilder.setEnabled(false);

                break;

            case PRINTER_GUI:
                printerGui.setEnabled(false);

                break;
        }

        menuApplication.add(databaseGui);
        menuApplication.add(packhouseBuilder);
        menuApplication.add(lineSetup);
        menuApplication.add(printerGui);

        menuBar.add(menuApplication);

        setJMenuBar(menuBar);
    }


}
