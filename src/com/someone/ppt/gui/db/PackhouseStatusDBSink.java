package com.someone.ppt.gui.db;

import com.someone.db.models.*;
import com.someone.ppt.models.*;

import java.sql.*;

public class PackhouseStatusDBSink {
    private Packhouse packhouse;
    private String status;
    private String[] allStatus;

    public PackhouseStatusDBSink(final Packhouse packhouse, final String status) {
        this.packhouse = packhouse;
        this.status = status;
        allStatus = new String[20];

        if (packhouseExists()) {
            deletePackhouse();
        }

        insertData();
    }

    private boolean packhouseExists() {
        final ScrollableTableModel model;
        boolean result = false;

        try {
            model = TableModelFactory.getResultSetTableModel(
                "packhousetemplates", "PackhouseName",
                packhouse.getPackhouseName());

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
            model = TableModelFactory.getResultSetTableModel(
                "packhousetemplates", "PackhouseName",
                packhouse.getPackhouseName());

            final ResultSet resultset = model.getResultSet();
            resultset.beforeFirst();

            int i = 0;
            while (resultset.next()) {
                allStatus[i] = resultset.getString(4);
                if (!allStatus[i].equals("updated")) {
                    allStatus[i] = status;
                }
                resultset.deleteRow();
                resultset.beforeFirst();
                i++;
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertData() {
        final ScrollableTableModel model;

        try {
            model = TableModelFactory.getResultSetTableModel(
                "packhousetemplates", "PackhouseName",
                packhouse.getPackhouseName());

            final ResultSet resultset = model.getResultSet();

            int count = 0;
            for (int i = 1; i <= packhouse.getLineCount(); i++) {
                final String lineName;

                if (i < 10) {
                    lineName = "0" + i;
                } else {
                    lineName = "" + i;
                }

                resultset.moveToInsertRow();
                resultset.updateString(1, packhouse.getPackhouseName());
                resultset.updateString(2, lineName);
                resultset.updateString(3, packhouse.getTemplateName(i - 1));
                resultset.updateString(4, allStatus[count++]);

                resultset.insertRow();
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }
}
