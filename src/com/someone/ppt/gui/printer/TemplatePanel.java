package com.someone.ppt.gui.printer;

import com.someone.db.*;
import com.someone.db.models.*;
import com.someone.gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class TemplatePanel extends JPanel {
    private String templateName;
    private JTabbedPane parent;
    //    private JToolBar                toolBar;
    private ElegantTable table;
    private ScrollableTableModel model;
    private TableSorter sorter;
    private JComboBox dbChooser;
    private int currentLine;

    public TemplatePanel(final String templateName,
                         final JTabbedPane parent) {
        super();
        this.templateName = templateName;
        this.parent = parent;
        setLayout(new BorderLayout());

        try {
            final String query =
                "select TemplateName, LineNumber, PrintField, Length, Control, PrinterID from printertemplates where TemplateName = 'XXX' order by " +
                    "lineNumber";


            model = TableModelFactory.getResultSetTableModel(query, templateName, true);
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        try {
            currentLine = Integer.parseInt(
                (String) model.getValueAt(model.getRowCount() - 1, 1)) + 1;
        } catch (final RuntimeException e) {
            currentLine = 0;
        }

        dbChooser = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "Printer", "PrinterID"));
        initToolBar();

        sorter = new TableSorter(model);
        table = new ElegantTable(sorter);
        sorter.addMouseListenerToHeaderInTable(table);

        final JScrollPane pane = new JScrollPane(table);

        add(pane, BorderLayout.CENTER);
        setVisible(true);
    }

    private void initToolBar() {
        //        toolBar = new JToolBar();
        final JPanel northPane = new JPanel(new FlowLayout(FlowLayout.LEFT));

        final JButton close = new JButton("Close");

        close.addActionListener(e -> parent.remove(TemplatePanel.this));

        final JButton add = new JButton("Add Line");
        final JButton delete = new JButton("Delete Line");
        final JButton modify = new JButton("Modify Line");

        add.addActionListener(e -> addRow());

        delete.addActionListener(e -> deleteRow());

        modify.addActionListener(e -> modifyRow());

        northPane.add(dbChooser);
        northPane.add(close);
        northPane.add(add);
        northPane.add(delete);
        northPane.add(modify);

        add(northPane, BorderLayout.NORTH);
    }

    /**
     * Method modifyRow.
     */
    private void modifyRow() {
        if (!model.canUpdate()) {
            return;
        }

        final String[] columnNames = model.getColumnNames();
        final int rowToUpdate = sorter.getModelRow(
            table.getSelectedRow());
        final TemplateInputFrame frame = new TemplateInputFrame(
            "Modify Row",
            columnNames,
            (String) dbChooser.getSelectedItem());

        //		final DataInputFrame frame = new DataInputFrame("Modify Row", columnNames);
        final String[] originalData = model.getRow(rowToUpdate);

        frame.setData(originalData);

        final Runnable modifier = () -> {
            if (frame.isModified()) {
                try {
                    final ResultSet resultSet = model.getResultSet();
                    resultSet.absolute(rowToUpdate + 1);

                    final String[] newData = frame.getNewData();

                    for (int i = 0; i < originalData.length; i++) {
                        resultSet.updateString(columnNames[i], newData[i]);
                    }

                    resultSet.updateRow();
                    model.recalculateModel();
                    sorter.reallocateIndexes();
                    table.updateUI();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            } else {
            }
        };

        frame.setCloseAction(modifier);
        frame.setVisible(true);
    }

    /**
     * Method deleteRow.
     */
    private void deleteRow() {
        if (!model.canUpdate()) {
            return;
        }

        final int rowToDelete = sorter.getModelRow(table.getSelectedRow());

        try {
            final ResultSet resultSet = model.getResultSet();
            resultSet.absolute(rowToDelete + 1);
            resultSet.deleteRow();
            model.recalculateModel();
            sorter.reallocateIndexes();
            table.updateUI();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method addRow.
     */
    private void addRow() {
        if (!model.canUpdate()) {
            return;
        }

        final String[] columnNames = model.getColumnNames();
        final TemplateInputFrame frame = new TemplateInputFrame("Add Row",
            columnNames,
            (String) dbChooser.getSelectedItem());
        final String[] data = new String[]{
            templateName, "" + currentLine++, "", "10", "",
            (String) dbChooser.getSelectedItem()
        };
        frame.setData(data);

        final Runnable modifier = () -> {
            if (frame.isModified()) {
                try {
                    final ResultSet resultSet = model.getResultSet();
                    resultSet.moveToInsertRow();

                    final String[] newData = frame.getNewData();

                    for (int i = 0; i < columnNames.length; i++) {
                        resultSet.updateString(columnNames[i], newData[i]);
                    }

                    resultSet.insertRow();

                    model.recalculateModel();
                    sorter.reallocateIndexes();
                    table.updateUI();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            } else {
                // do nothing
            }
        };

        frame.setCloseAction(modifier);
        frame.setVisible(true);
    }
}
