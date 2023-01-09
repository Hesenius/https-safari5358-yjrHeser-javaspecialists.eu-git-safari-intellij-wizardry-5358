package com.someone.ppt.gui.line;

import com.someone.db.*;
import com.someone.gui.*;
import com.someone.ppt.cds.*;
import com.someone.ppt.gui.*;
import com.someone.ppt.gui.db.*;
import com.someone.ppt.models.*;
import com.someone.ppt.reports.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class LineGui extends CmsFrame {
    private Packhouse packhouse;
    private JTabbedPane linesTabPane;
    private LinePane[] linePanes;
    private JCheckBoxMenuItem[] hideBoxes;
    private JCheckBoxMenuItem[] updateAllBoxes;
    private JButton activateButton;
    private final LineGui thisGui = this; // used to reference this object from within a local inner class

    public LineGui(final CmsGui mainGui, final Packhouse packhouse) {
        super(mainGui, "Line Setup : Packhouse " + packhouse.getPackhouseName());
        this.packhouse = packhouse;
        menuBar.add(Box.createHorizontalGlue());
        activateButton = new JButton("Save and Activate");
        activateButton.setToolTipText("Save all line templates, associate it with the packhouse and send it to the printing machines.");
        activateButton.setBackground(Color.RED);
        activateButton.addActionListener(e -> {
            activateButton.setText("Activating...");
            refreshDatabase();
        });
        menuBar.add(activateButton);
    }

    public void initGui() {
        removeCurrentPanes();

        linesTabPane = new JTabbedPane(JTabbedPane.TOP);
        linePanes = new LinePane[packhouse.getLineCount()];

        for (int i = 0; i < packhouse.getLineCount(); i++) {
            String name;

            try {
                name = packhouse.getTemplateName(i);
            } catch (final Exception e) {
                name = "?";
            }

            linePanes[i] = new LinePane(i, packhouse, this);
            linePanes[i].updateModel();
            linesTabPane.add("Line " + (i + 1) + " : " + name, linePanes[i]);
        }

        final PreviewLinePane preview = new PreviewLinePane();
        preview.updateModel();
        linesTabPane.add("Template Preview", preview);

        this.getContentPane().add(linesTabPane, BorderLayout.CENTER);

        final UpdateTimer timer = new UpdateTimer(packhouse.getPackhouseName(), this);
        timer.start();
    }

    private void removeCurrentPanes() {
        if (linesTabPane != null) {
            this.getContentPane().remove(linesTabPane);
            linesTabPane = null;
        }
    }

    protected void defineLocalMenus() {
        guiType = LINE_GUI;

        JMenuItem item;

        menuFile.setText("Packhouse");
        menuFile.setMnemonic('p');

        item = new JMenuItem("1) Save Packhouse");
        item.setMnemonic('s');
        item.addActionListener(e -> {
            LineGui.this.setEnabled(false);

            try {
                new PackhouseStatusDBSink(packhouse, "changed");
            } catch (final RuntimeException ex) {
            }

            LineGui.this.setEnabled(true);
        });
        menuFile.add(item);

        item = new JMenuItem("2) Activate Setup");
        item.setMnemonic('a');
        menuFile.add(item);
        item.addActionListener(e -> refreshDatabase());
        menuFile.add(item);

        defineLineMenu();
        defineReportMenu();
        defineUpdateAllMenu();
        defineEverywhereMenu();
        defineHideMenu();

        final JMenu menu = new JMenu("Manual");
        menu.setMnemonic('m');
        item = new JMenuItem("Show Manual");
        item.setMnemonic('s');
        item.addActionListener(e -> {
            try {
                //	Runtime.getRuntime().exec("explorer .\\manual\\manual.htm");
                final BufferedReader fileIn = new BufferedReader(
                    new FileReader("config/iexplore location.txt"));
                final String location = fileIn.readLine(); // get the location of iexplore.exe from the file
                // if a string was received from the file
                if (location != null) {
                    // open manual.htm in iexplore
                    Runtime.getRuntime().exec(location + ' ' + System.getProperty("user.dir") + "\\manual\\manual.htm");
                } else {
                    JOptionPane.showMessageDialog(thisGui,
                        "Error: \"config/iexplore location.txt\" is empty.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                fileIn.close();
                // if "config/iexplore loaction.txt" is not found...
            } catch (final FileNotFoundException exc) {
                JOptionPane.showMessageDialog(thisGui,
                    "Error: Unable to locate \"config/iexplore location.txt\"",
                    "File not found error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (final IOException ex) {
                JOptionPane.showMessageDialog(thisGui,
                    "Error: Unable to open \"manual/manual.htm\".\n" +
                        "Check the contents of \"config/iexplore location.txt\".",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        menu.add(item);

        menuBar.add(menu);
    }

    private void defineHideMenu() {
        JMenuItem item;
        final JMenu menu;
        menu = new HorizontalMenu("Hide", 15);
        menu.setToolTipText(
            "All selected columns will not be displayed on the grid.");
        menu.setMnemonic('H');

        item = new JMenuItem("All");
        item.setMnemonic('a');

        // ActionListener for "All" button
        item.addActionListener(e -> {
            // Trigger click event on all hide buttons
            for (int i = 0; i < hideBoxes.length; i++) {
                hideBoxes[i].doClick();
            }
        });

        menu.add(item);

        item = new JMenuItem("None");
        item.setMnemonic('n');

        // ActionListener for "None" button
        item.addActionListener(e -> {
            // Trigger click event on all hide buttons
            for (int i = 0; i < hideBoxes.length; i++) {
                hideBoxes[i].doClick();
            }
        });

        menu.add(item);

        // ActionListener for hide buttons
        final ActionListener checkListener2 = e -> {
            // Get origin of event
            final JCheckBoxMenuItem item1 = (JCheckBoxMenuItem) e.getSource();
            final String name = item1.getText();

            for (int i = 0; i < FruitspecTableModel.columnNames.length; i++) {
                if (name.equals(FruitspecTableModel.columnNames[i])) {
                    //	System.out.println("Column " + i + " : " +
                    //	                   item.isSelected());
                    FruitspecTableModel.setHidden(i, item1.isSelected());

                    for (int k = 0; k < linePanes.length; k++) {
                        final LinePane pane = linePanes[k];
                        pane.table.initFruitSpecColumnSizes();
                        pane.table.initGui();
                        pane.table.validate();
                        pane.table.updateUI();
                        pane.validate();
                        pane.updateUI();
                    }

                    break;
                }
            }
        };

        // Initialize hide boxes array.
        hideBoxes = new JCheckBoxMenuItem[FruitspecTableModel.columnNames.length];

        // Get the name and check status for all hide buttons from the
        // FruitspecTableModel and add the buttons to the menu.
        for (int i = 0; i < FruitspecTableModel.columnNames.length; i++) {
            final JCheckBoxMenuItem check = new JCheckBoxMenuItem(
                FruitspecTableModel.columnNames[i]);
            check.setSelected(FruitspecTableModel.getHidden(i));
            check.addActionListener(checkListener2);
            hideBoxes[i] = check;
            menu.add(check);
        }

        menuBar.add(menu);
    }

    private void defineEverywhereMenu() {
        final JMenu menu;
        menu = new HorizontalMenu("Green", 12);
        menu.setToolTipText(
            "Green columns automatically change all the barcodes of the whole packhouse with their new value.");
        menu.setMnemonic('g');

        final ActionListener everyCheckListener = e -> {
            final JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
            final String name = item.getText();

            for (int i = 0; i < FruitspecTableModel.columnNames.length; i++) {
                if (name.equals(FruitspecTableModel.columnNames[i])) {
                    FruitspecTableModel.setEveryWhere(i, item.isSelected());

                    final LinePane pane = (LinePane) linesTabPane.getSelectedComponent();
                    pane.updateUI();

                    break;
                }
            }
        };

        JCheckBoxMenuItem check;
        final JCheckBoxMenuItem[] boxesEveryWhere = new JCheckBoxMenuItem[FruitspecTableModel.columnNames.length - 3];

        for (int i = 3; i < FruitspecTableModel.columnNames.length; i++) {
            check = new JCheckBoxMenuItem(FruitspecTableModel.columnNames[i]);
            check.setSelected(FruitspecTableModel.getEveryWhere(i));
            check.addActionListener(everyCheckListener);
            boxesEveryWhere[i - 3] = check;
            menu.add(check);
        }

        menuBar.add(menu);
    }

    private void defineUpdateAllMenu() {
        JMenuItem item;
        final JMenu menu;
        menu = new HorizontalMenu("Blue", 13);
        menu.setToolTipText(
            "Blue columns automatically change all the barcodes of the current line with their new value.");

        updateAllBoxes = new JCheckBoxMenuItem[FruitspecTableModel.columnNames.length - 3];
        menu.setMnemonic('b');

        item = new JMenuItem("All");
        item.setMnemonic('a');
        item.addActionListener(e -> {
            for (int i = 0; i < updateAllBoxes.length; i++) {
                updateAllBoxes[i].doClick();
            }
        });
        menu.add(item);

        item = new JMenuItem("None");
        item.setMnemonic('n');
        item.addActionListener(e -> {
            for (int i = 0; i < updateAllBoxes.length; i++) {
                updateAllBoxes[i].doClick();
            }
        });
        menu.add(item);

        JCheckBoxMenuItem check;
        final ActionListener updateAllListener = e -> {
            final JCheckBoxMenuItem item1 = (JCheckBoxMenuItem) e.getSource();
            final String name = item1.getText();

            for (int i = 0; i < FruitspecTableModel.columnNames.length; i++) {
                if (name.equals(FruitspecTableModel.columnNames[i])) {
                    FruitspecTableModel.setUpdateAll(i, item1.isSelected());

                    final LinePane pane = (LinePane) linesTabPane.getSelectedComponent();
                    pane.updateUI();

                    break;
                }
            }
        };

        for (int i = 3; i < FruitspecTableModel.columnNames.length; i++) {
            check = new JCheckBoxMenuItem(FruitspecTableModel.columnNames[i]);
            check.setSelected(FruitspecTableModel.getUpdateAll(i));
            check.addActionListener(updateAllListener);
            updateAllBoxes[i - 3] = check;
            menu.add(check);
        }

        menuBar.add(menu);
    }

    private void defineReportMenu() {
        JMenuItem item;
        final JMenu menu;
        menu = new JMenu("Reports");
        menu.setMnemonic('r');
        item = new JMenuItem("Print Setup");
        item.setMnemonic('s');
        item.addActionListener(e -> {
            final SetupReport printer = new SetupReport(
                packhouse.getPackhouseName());
            printer.print();
        });
        menu.add(item);

        item = new JMenuItem("Report Chooser");
        item.setMnemonic('c');
        item.addActionListener(e -> {
            final ReportDetailFrame frame = new ReportDetailFrame(
                "Report Chooser");
            frame.setVisible(true);
        });
        menu.add(item);

        final JMenu subMenu = new JMenu("Current Line");

        item = new JMenuItem("Mass Summary");
        item.setMnemonic('m');
        item.addActionListener(e -> {
            final WeightReport tester = new WeightReport(
                packhouse.getPackhouseName());
            tester.show();
        });
        subMenu.add(item);

        item = new JMenuItem("Updating Mass Summary");
        item.setMnemonic('u');
        item.addActionListener(e -> {
            final WeightReportUpdater tester = new WeightReportUpdater(
                packhouse.getPackhouseName());
            tester.show();
        });
        subMenu.add(item);

        item = new JMenuItem("Print Mass Summary");
        item.addActionListener(e -> {
            final WeightReport tester = new WeightReport(
                packhouse.getPackhouseName());
            tester.print();
        });
        subMenu.add(item);

        item = new JMenuItem("CMS Summary");
        item.setMnemonic('c');
        item.addActionListener(e -> {
            final SummaryReport tester = new SummaryReport(
                packhouse.getPackhouseName());
            tester.show();
        });
        subMenu.add(item);

        item = new JMenuItem("Print CMS Summary");
        item.addActionListener(e -> {
            final SummaryReport tester = new SummaryReport(
                packhouse.getPackhouseName());
            tester.print();
        });
        subMenu.add(item);

        item = new JMenuItem("Pack Summary");
        item.setMnemonic('p');
        item.addActionListener(e -> {
            final PackSummaryReport tester = new PackSummaryReport(
                packhouse.getPackhouseName());
            tester.show();
        });
        subMenu.add(item);

        item = new JMenuItem("Print Pack Summary");
        item.addActionListener(e -> {
            final PackSummaryReport tester = new PackSummaryReport(
                packhouse.getPackhouseName());
            tester.print();
        });
        subMenu.add(item);


        //		menu.add(subMenu);
        menuBar.add(menu);
    }

    private void defineLineMenu() {
        JMenuItem item;
        final JMenu menu;
        menu = new JMenu("Line");
        menu.setMnemonic('l');
        item = new JMenuItem("Load Template");
        item.setMnemonic('l');
        menu.add(item);
        item.addActionListener(e -> {
            final ComboSelectionFrame f = new ComboSelectionFrame(
                "Select Template",
                "templates",
                "TemplateName");
            f.setCloseAction(() -> {
                final LinePane pane = linePanes[linesTabPane.getSelectedIndex()];
                pane.setDirty();

                final TemplateDBSource source = new TemplateDBSource(
                    "doesnotmatter");
                final String[][] templateData =
                    source.extractTemplate(f.getSelectedValue(),
                        linesTabPane.getSelectedIndex() + 1);

                packhouse.updateLineData(
                    linesTabPane.getSelectedIndex(), templateData);
                packhouse.setTemplateName(
                    linesTabPane.getSelectedIndex(),
                    f.getSelectedValue());

                linesTabPane.setTitleAt(linesTabPane.getSelectedIndex(),
                    ("Line " + (linesTabPane.getSelectedIndex() + 1) +
                        " : " +
                        f.getSelectedValue()));

                pane.updateModel();
            });

            f.setVisible(true);
        });
        item = new JMenuItem("Save Template");
        item.setMnemonic('s');
        item.addActionListener(e -> saveTemplateToDatabase());
        menu.add(item);

        item = new JMenuItem("Export Data files");
        item.setMnemonic('e');
        item.addActionListener(e -> {
            final String[] columnNames = {"Directory"};
            final DataInputFrame frame = new DataInputFrame(
                "Export Data Files",
                columnNames, false);

            final Runnable modifier = () -> {
                if (frame.isModified()) {
                    try {
                        final CdsGenerator g = new CdsGenerator(packhouse,
                            linesTabPane.getSelectedIndex());
                        g.generateFiles(frame.getNewData()[0] + "/");
                    } catch (final Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    // do nothing
                }
            };

            frame.setCloseAction(modifier);
            frame.setVisible(true);
        });
        menu.add(item);

        item = new JMenuItem("Test Setup");
        item.setMnemonic('T');
        item.addActionListener(e -> {
            final CdsGenerator tester = new CdsGenerator(packhouse,
                linesTabPane.getSelectedIndex());
            tester.testSetup();
        });
        menu.add(item);

        menuBar.add(menu);
    }

    private void refreshDatabase() {
        this.setEnabled(false);


        try {
            for (int i = 0; i < packhouse.getLineCount(); i++) {
                cleanTitle(i);
                setActivateTitle(i);
                final LinePane pane = linePanes[i];
                pane.saveButton.setBackground(Color.LIGHT_GRAY);
                new TemplateDBSink(pane.getTemplateName(), pane.getData());
            }
            new PackhouseStatusDBSink(packhouse, "updated");
        } finally {
            this.setEnabled(true);
        }
    }

    void saveTemplateToDatabase() {
        this.setEnabled(false);

        final LinePane pane = linePanes[linesTabPane.getSelectedIndex()];

        final String[] columnNames = {"Template Name"};
        final DataInputFrame frame = new DataInputFrame(
            "Save Template As",
            columnNames, false);
        final String[] data = {pane.getTemplateName()};
        frame.setData(data);

        final Runnable modifier = () -> {
            if (frame.isModified()) {

                if (!pane.saveButton.getBackground().equals(Color.red)) {
                    final String newName = frame.getNewData()[0];
                    final String oldName = pane.getTemplateName();

                    if (newName.equals(oldName)) {
                        LineGui.this.setEnabled(true);
                        return;
                    }
                }

                try {
                    new TemplateDBSink(frame.getNewData()[0],
                        pane.getData());

                    linesTabPane.setTitleAt(linesTabPane.getSelectedIndex(), "Line " +
                        (linesTabPane.getSelectedIndex() + 1) +
                        " : " +
                        frame.getNewData()[0]);

                    packhouse.setTemplateName(
                        linesTabPane.getSelectedIndex(),
                        frame.getNewData()[0]);

                    pane.saveButton.setBackground(Color.lightGray);
                } catch (final Exception e) {
                    e.printStackTrace();
                } finally {
                    LineGui.this.setEnabled(true);
                }
            } else {
                LineGui.this.setEnabled(true);
            }
        };

        frame.setCloseAction(modifier);
        frame.setVisible(true);
    }

    public void updateEveryWhere(final int column, final String value) {
        for (int i = 0; i < linePanes.length; i++) {
            linePanes[i].updateEveryWhere(column, value);
        }
    }

    public void cleanTitles() {
        for (int i = 0; i < linePanes.length; i++) {
            cleanTitle(i);
        }
        linesTabPane.validate();
    }

    private void cleanTitle(final int line) {
        final String title = linesTabPane.getTitleAt(line);
        final int star = title.indexOf("(Modified) ");

        if (star >= 0) {
            linesTabPane.setTitleAt(line, title.substring(11));
        }
        linesTabPane.validate();
    }

    public void cleanActivatingTitles() {
        for (int i = 0; i < linePanes.length; i++) {
            cleanActivatingTitle(i);
        }
        linesTabPane.validate();
    }

    private void cleanActivatingTitle(final int line) {
        final String title = linesTabPane.getTitleAt(line);
        final int star = title.indexOf("(Activating) ");

        if (star >= 0) {
            linesTabPane.setTitleAt(line, title.substring(13));
            linePanes[line].table.setEnabled(true);
            linePanes[line].table.setToolTipText(null);
        }
        linesTabPane.validate();
    }

    public void setDirtyTitle(final int index) {
        final String title = linesTabPane.getTitleAt(index);
        final int star = title.indexOf("(Modified) ");

        if (star < 0) {
            linesTabPane.setTitleAt(index, "(Modified) " + title);
            linesTabPane.validate();
        }
    }

    public void setActivateTitle(final int index) {
        final String title = linesTabPane.getTitleAt(index);
        final int star = title.indexOf("(Activating) ");

        if (star < 0) {
            linesTabPane.setTitleAt(index, "(Activating) " + title);
            linePanes[index].table.setEnabled(false);
            linePanes[index].table.setToolTipText("Line editing is deactivated until setup is received at printing machine. ");

        }
        linesTabPane.doLayout();
        linesTabPane.revalidate();
    }


    public void setDirty() {
        activateButton.setText("Activate");
        activateButton.setBackground(Color.RED);
    }

    public void synchronizeDb() {
        SwingUtilities.invokeLater(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                activateButton.setText("Activated");
                activateButton.setBackground(Color.GREEN);

                for (int i = 0; i < linePanes.length; i++) {
                    linePanes[i].setUpdated();
                }
            }
        });
    }

    public void setVisible(final boolean b) {
        FruitspecTableModel.resetComboBoxes();
        super.setVisible(b);
    }

    private class HorizontalMenu extends JMenu {
        private HorizontalMenu(final String label, final int size) {
            super(label);

            final JPopupMenu pm = getPopupMenu();
            pm.setLayout(new GridLayout(size, 2));
            setMinimumSize(getPreferredSize());
        }

        public void setPopupMenuVisible(final boolean b) {
            final boolean isVisible = isPopupMenuVisible();

            if (b != isVisible) {
                if ((b == true) && isShowing()) {
                    // Set location of popupMenu (pulldown or pullright)
                    // Perhaps this should be dictated by L&F
                    int x = 0;
                    int y = 0;
                    final Container parent = getParent();

                    if (parent instanceof JPopupMenu) {
                        x = 0;
                        y = getHeight() + 20;
                    } else {
                        x = getWidth();
                        y = 20;
                    }

                    getPopupMenu().show(this, x, y);
                } else {
                    getPopupMenu().setVisible(false);
                }
            }
        }
    }
}
