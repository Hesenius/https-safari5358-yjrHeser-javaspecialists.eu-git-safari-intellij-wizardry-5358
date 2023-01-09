package com.someone.ppt.gui.db;

import com.someone.db.models.*;
import com.someone.ppt.gui.packhouse.*;
import com.someone.xml.*;

import java.sql.*;
import java.util.*;

public class PackhouseDBSink {
    private String packhouseName;
    private CisTreeNode rootNode;

    public PackhouseDBSink(final String packhouseName, final CisTreeNode rootNode) {
        this.packhouseName = packhouseName;
        this.rootNode = rootNode;

        if (packhouseExists()) {
            deletePackhouse();
        }

        insertData();
    }

    private boolean packhouseExists() {
        final ScrollableTableModel model;
        boolean result = false;

        try {
            model = TableModelFactory.getResultSetTableModel("packhouseLayout",
                "PackhouseName",
                packhouseName);

            if (model.getRowCount() != 0) {
                result = true;
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void deletePackhouse() {
        final ScrollableTableModel model;

        try {
            model = TableModelFactory.getResultSetTableModel("packhouseLayout",
                "PackhouseName",
                packhouseName);

            final ResultSet resultset = model.getResultSet();
            resultset.beforeFirst();

            while (resultset.next()) {
                resultset.deleteRow();
                resultset.beforeFirst();
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private String getName(final CisTreeNode node) {
        String name = ((SchemaElement) node.getUserObject()).getSpecial();

        try {
            final int i = Integer.parseInt(name);

            if ((i < 10) && (name.length() == 1)) {
                name = "0" + name;
            }
        } catch (final Exception e) {
        }

        return name;
    }

    private void insertData() {
        final ScrollableTableModel model;

        try {
            model = TableModelFactory.getResultSetTableModel("packhouseLayout",
                "PackhouseName",
                packhouseName);

            final ResultSet resultset = model.getResultSet();

            final CisTreeNode packhouse = (CisTreeNode) rootNode.getChildAt(0); // rootNode = CIS

            final Enumeration lines = packhouse.children();

            while (lines.hasMoreElements()) {
                final CisTreeNode line = (CisTreeNode) lines.nextElement();
                final Enumeration drops = line.children();

                while (drops.hasMoreElements()) {
                    final CisTreeNode drop = (CisTreeNode) drops.nextElement();
                    final Enumeration tables = drop.children();

                    while (tables.hasMoreElements()) {
                        final CisTreeNode table = (CisTreeNode) tables.nextElement();
                        final Enumeration stations = table.children();

                        while (stations.hasMoreElements()) {
                            final CisTreeNode station = (CisTreeNode) stations.nextElement();

                            resultset.moveToInsertRow();
                            resultset.updateString(1, packhouseName);
                            resultset.updateString(2, getName(line));
                            resultset.updateString(3, getName(drop));
                            resultset.updateString(4, getName(table));
                            resultset.updateString(5, getName(station));
                            resultset.insertRow();
                        }
                    }
                }
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }
}
