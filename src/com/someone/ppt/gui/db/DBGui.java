package com.someone.ppt.gui.db;

import com.someone.db.*;
import com.someone.gui.*;
import com.someone.io.*;
import com.someone.ppt.gui.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class DBGui extends CmsFrame {
    private JTabbedPane tabbedPane;

    public DBGui(final CmsGui mainApp) {
        super(mainApp, "Database Administration");

        try {
            initGui();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    protected void defineLocalMenus() {
        guiType = DB_GUI;

        final JMenuItem item = new JMenuItem("Import Data");
        item.setMnemonic('i');
        item.addActionListener(e -> importData());
        menuFile.add(item);
    }

//    private void exportData() {
//    }

    private void importData() {
        final File myFile = FileProxy.chooseFile(null);

        if (myFile != null) {
            TextImporter.importFile(myFile);
        }
    }

    private void initGui() throws Exception {
        initNorth();
        tabbedPane = new JTabbedPane();
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    private void initNorth() {
        final Dimension frameSize = this.getSize();

        final JPanel northPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northPane.setBackground(Color.white);
        northPane.setPreferredSize(
            new Dimension(frameSize.width, GuiTools.getCharHeight() * 20));

        final String[] usedTables = new String[]{
            "grower", "gtin", "pool", "remark", "targetmarket", "cartons", "packhousetemplates",
            "packhouselines", "serverconfig", "imports"
        };

        for (int i = 0; i < usedTables.length; i++) {
            northPane.add(new TableButton(usedTables[i]));
        }

        final JScrollPane scroller = new JScrollPane(northPane);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scroller.setPreferredSize(
            new Dimension(frameSize.width, GuiTools.getCharHeight() * 7));

        final Border topBorder = BorderFactory.createEtchedBorder();
        scroller.setBorder(topBorder);
        getContentPane().add(scroller, BorderLayout.NORTH);
    }

    class TableButton extends JButton {
        private String tableName;

        private TableButton(final String name) {
            super(name);
            tableName = name;
            this.addActionListener(arg0 -> {
                try {
                    tabbedPane.remove(0);
                } catch (final RuntimeException e) {
                }
                tabbedPane.add(tableName,
                    new TablePanel(tableName, tabbedPane));
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            });
        }
    }
}
