package com.someone.ppt.gui.printer;

import com.someone.db.*;
import com.someone.gui.*;
import com.someone.ppt.gui.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;


public class PrinterGui extends CmsFrame {
    private JTabbedPane tabbedPane;
    private Dimension frameSize;

    public PrinterGui(final CmsGui mainApp) {
        super(mainApp, "Printing");

        try {
            frameSize = this.getSize();
            initGui();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    protected void defineLocalMenus() {
        guiType = PRINTER_GUI;

        JMenuItem item = new JMenuItem("Import Data");


        final JMenu menu = new JMenu("Reports");
        menu.setMnemonic('r');
        item = new JMenuItem("Report 1");
        menu.add(item);
        item = new JMenuItem("Report 2");
        menu.add(item);
        item = new JMenuItem("Report 3");
        menu.add(item);
        item = new JMenuItem("Report 4");
        menu.add(item);
        item = new JMenuItem("Report 5");
        menu.add(item);

        menuBar.add(menu);
    }

    private void initNorth() {
        final String[] tables = new String[]{"Printer", "PrinterCodes", "PrintFields", "PackPrintSetup"};

        final JPanel northPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northPane.setBackground(Color.white);
        northPane.setPreferredSize(
            new Dimension(frameSize.width, GuiTools.getCharHeight() * 6));

        for (int i = 0; i < tables.length; i++) {
            northPane.add(new TableButton(tables[i]));
        }

        JButton button = new JButton("PackhousePrinters");
        button.addActionListener(e -> printerLayout());
        northPane.add(button);

        button = new JButton("LinePrinterTemplates");
        button.addActionListener(e -> templateSetup());
        northPane.add(button);

        button = new JButton("New Template");
        button.addActionListener(e -> addNewTemplate());
        northPane.add(button);

        button = new JButton("Load Template");
        button.addActionListener(e -> loadTemplate());
        northPane.add(button);


        final JScrollPane scroller = new JScrollPane(northPane);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scroller.setPreferredSize(
            new Dimension(frameSize.width, GuiTools.getCharHeight() * 6));

        final Border topBorder = BorderFactory.createEtchedBorder();
        scroller.setBorder(topBorder);
        getContentPane().add(scroller, BorderLayout.NORTH);
    }

    private void addNewTemplate() {
        final String[] columnNames = {"Template Name"};
        final DataInputFrame frame = new DataInputFrame("Create New Template",
            columnNames, false);

        final Runnable modifier = () -> {
            if (frame.isModified()) {
                try {
                    tabbedPane.add(frame.getNewData()[0],
                        new TemplatePanel(frame.getNewData()[0], tabbedPane));
                    tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else {
                // do nothing
            }
        };

        frame.setCloseAction(modifier);
        frame.setVisible(true);
    }

    private void loadTemplate() {
        final ComboSelectionFrame frame = new ComboSelectionFrame(
            "Select Template",
            "printertemplates",
            "TemplateName");

        final Runnable modifier = () -> {
            if (frame.isModified()) {
                try {
                    tabbedPane.add(frame.getSelectedValue(),
                        new TemplatePanel(frame.getSelectedValue(), tabbedPane));
                    tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else {
                // do nothing
            }
        };

        frame.setCloseAction(modifier);
        frame.setVisible(true);
    }

    private void printerLayout() {
        final String[][] definition = new String[][]{
            {"packhouseprinters", "", "", ""},
            {"packhousename", "packhousetemplates", "packhousename", ""},
            {"linename", "packhousetemplates", "linename", ""},
            {"printerid", "printer", "printerid", ""}};

        tabbedPane.add("PackhousePrinters",
            new ValidateTablePanel(definition, tabbedPane));
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

    }

    private void templateSetup() {
        final String[][] definition = new String[][]{
            {"lineprintertemplates", "", "", ""},
            {"packhousename", "packhousetemplates", "packhousename", ""},
            {"linename", "packhousetemplates", "linename", ""},
            {"packtype", "packprintsetup", "packID", ""},
            {"template", "printertemplates", "templatename", ""},
        };

        tabbedPane.add("LinePrinterTemplates",
            new ValidateTablePanel(definition, tabbedPane));
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    private void initGui() throws Exception {
        initNorth();
        tabbedPane = new JTabbedPane();
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    class TableButton extends JButton {
        private String tableName;

        private TableButton(final String name) {
            super(name);
            tableName = name;
            this.addActionListener(arg0 -> {
                tabbedPane.add(tableName,
                    new TablePanel(tableName,
                        tabbedPane));
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            });
        }
    }
}
