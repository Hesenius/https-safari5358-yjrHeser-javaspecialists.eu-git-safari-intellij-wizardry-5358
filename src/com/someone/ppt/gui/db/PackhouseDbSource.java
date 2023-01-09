package com.someone.ppt.gui.db;

import com.someone.db.models.*;

import java.sql.*;

/**
 * Loads a packhouse layout from the database.
 * Layout contains: lines, drops, tables, stations.
 */
public class PackhouseDbSource {
    public static String[][] getLayout(final String packhouseName) {
        String[][] data = null;
        ScrollableTableModel model = null;

        try {
            model = TableModelFactory.getResultSetTableModel("PackhouseLayout",
                "PackhouseName",
                packhouseName);

            final int rowCount = model.getRowCount();
            data = new String[rowCount][];

            for (int i = 0; i < rowCount; i++) {
                data[i] = model.getRow(i);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        } finally {
            model.close();
            model = null;
        }

        return data;
    }
}
