package com.someone.db.models;

import javax.swing.event.*;
import javax.swing.table.*;
import java.sql.*;


public class ScrollableTableModel implements TableModel {
    private String[] columnNames;
    private boolean hasPrimaryKey;
    private ResultSet results;
    private ResultSetMetaData metadata;
    private int columns;
    private int rows;

    ScrollableTableModel(final ResultSet results, final boolean hasPrimaryKey) {
        this.results = results;

        recalculateModel();
        columnNames = new String[columns];

        for (int i = 0; i < columnNames.length; i++) {
            try {
                columnNames[i] = metadata.getColumnLabel(i + 1);
            } catch (final SQLException e) {
                columnNames[i] = "UNDEFINED";
            }
        }
        this.hasPrimaryKey = hasPrimaryKey;

    }

    public boolean canUpdate() {
        return hasPrimaryKey;
    }

    public void recalculateModel() {
        try {
            metadata = results.getMetaData();
            columns = metadata.getColumnCount();
            results.last();
            rows = results.getRow();
        } catch (final SQLException e) {
        }
    }

    public void close() {
        try {
            results.getStatement().close();
        } catch (final SQLException e) {
        }
    }

    /**
     * Automatically close when we're garbage collected
     */
    protected void finalize() {
        close();
    }

    public ResultSet getResultSet() {
        return results;
    }

    public int getColumnCount() {
        return columns;
    }

    public int getRowCount() {
        return rows;
    }

    public String getColumnName(final int column) {
        return columnNames[column];
    }

    public Class getColumnClass(final int column) {
        return String.class;
    }

    public Object getValueAt(final int row, final int column) {
        try {
            results.absolute(row + 1);

            final Object o = results.getObject(column + 1);

            if (o == null) {
                return null;
            } else {
                return o.toString();
            }
        } catch (final SQLException e) {
            return "Empty";
        }
    }

    public String[] getRow(final int row) {
        final String[] result = new String[columns];

        for (int i = 0; i < result.length; i++) {
            try {
                result[i] = getValueAt(row, i).toString();
            } catch (final RuntimeException e) {
                result[i] = "";
            }
        }

        return result;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public boolean isCellEditable(final int row, final int column) {
        return false;
    }

    public void setValueAt(final Object value, final int row, final int column) {
    }

    public void addTableModelListener(final TableModelListener l) {
    }

    public void removeTableModelListener(final TableModelListener l) {
    }
}
