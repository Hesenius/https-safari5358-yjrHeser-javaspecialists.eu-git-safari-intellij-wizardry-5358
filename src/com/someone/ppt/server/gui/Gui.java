package com.someone.ppt.server.gui;

import com.someone.ppt.server.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Gui extends JFrame {
    JMenuItem helpMenu;
    JMenuItem subMenu1;
    JMenuItem subMenu2;
    JMenuItem subMenu3;
    JTextField textField;
    JButton button1;
    JButton button2;
    JButton button3;

    public Gui(final LineControl[] lines, final CmsServer cserver) {
        final CmsServer server = cserver;
        //
        //		Create Server Menu
        //
        final JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEtchedBorder());

        final JMenu menu = new JMenu("Server");

        final JMenuItem startMenu = new JMenuItem("   Start", KeyEvent.VK_S);
        startMenu.addActionListener(e -> setTitle("CMS Server - running"));
        menu.add(startMenu);

        final JMenuItem stopMenu = new JMenuItem("   Stop", KeyEvent.VK_T);
        stopMenu.addActionListener(e -> {
            server.setServerState(CmsServer.OFF);
            setTitle("CMS Server - stopping");
        });
        menu.add(stopMenu);

        final JMenuItem restartMenu = new JMenuItem("   Restart", KeyEvent.VK_R);
        restartMenu.addActionListener(e -> {
            server.setServerState(CmsServer.RESTART);
            setTitle("CMS Server - restarting");
        });
        menu.add(restartMenu);
        menu.addSeparator();

        menu.addSeparator();

        final JMenuItem exitMenu = new JMenuItem("   Exit", KeyEvent.VK_X);
        exitMenu.addActionListener(e -> System.exit(0));
        menu.add(exitMenu);

        final JLabel JJ = new JLabel("J&J Multi-Tier      ");

        menuBar.add(menu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(JJ);
        setJMenuBar(menuBar);
        menuBar.setVisible(true);


        //
        //		End Server Menu
        //
        setTitle("CMS Server - running");

        final JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(lines.length, 1));

        for (int i = 0; i < lines.length; i++) {
            final JPanel p = new JPanel();
            p.add(lines[i].getDisplay());
            panel.add(p);
        }

        final JScrollPane pane = new JScrollPane(panel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setAutoscrolls(true);

        setSize(600, 500);
        getContentPane().add(pane);
        getContentPane().setBackground(Color.lightGray);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public void stopServer() {
        setTitle("CMS Server - stopping");
        setTitle("CMS Server - running");
    }

    public void restartServer() {
        setTitle("CMS Server - restarting");
        setTitle("CMS Server - running");
    }
}
