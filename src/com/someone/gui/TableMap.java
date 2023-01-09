package com.someone.gui;

import javax.swing.event.*;
import javax.swing.table.*;

public class TableMap extends AbstractTableModel implements TableModelListener {
    protected TableModel model;

    void setModel(final TableModel model) {
        this.model = model;
        model.addTableModelListener(this);
    }

    public TableModel getModel() {
        return model;
    }

    // By default, Implement TableModel by forwarding all messages
    // to the model.
    public Object getValueAt(final int aRow, final int aColumn) {
        return model.getValueAt(aRow, aColumn);
    }

    public void setValueAt(final Object aValue, final int aRow, final int aColumn) {
        model.setValueAt(aValue, aRow, aColumn);
    }

    public int getRowCount() {
        return (model == null) ? 0 : model.getRowCount();
    }

    public int getColumnCount() {
        return (model == null) ? 0 : model.getColumnCount();
    }

    public String getColumnName(final int aColumn) {
        return model.getColumnName(aColumn);
    }

    public Class getColumnClass(final int aColumn) {
        return model.getColumnClass(aColumn);
    }

    public boolean isCellEditable(final int row, final int column) {
        return model.isCellEditable(row, column);
    }

    //
    // Implementation of the TableModelListener interface,
    //
    // By default forward all events to all the listeners.
    public void tableChanged(final TableModelEvent e) {
        fireTableChanged(e);
    }
}
