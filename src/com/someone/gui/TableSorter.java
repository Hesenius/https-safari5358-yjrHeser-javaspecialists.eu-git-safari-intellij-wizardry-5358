package com.someone.gui;

import com.someone.db.models.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.util.*;

public class TableSorter extends TableMap {
    private int[] indexes;
    private Vector sortingColumns = new Vector();
    private boolean ascending = true;

    protected TableSorter() {
        indexes = new int[0]; // For consistency.
    }

    public TableSorter(final TableModel model) {
        setModel(model);
    }

    void setModel(final TableModel model) {
        super.setModel(model);
        reallocateIndexes();
    }

    public boolean canUpdate() {
        boolean result = true;

        if (model instanceof ScrollableTableModel) {
            if (!((ScrollableTableModel) model).canUpdate()) {
                result = false;
            }
        }

        return result;
    }

    public int getModelRow(final int rowIndex) {
        return indexes[rowIndex];
    }

    protected int compareRowsByColumn(final int row1, final int row2, final int column) {
        final Class type = model.getColumnClass(column);
        final TableModel data = model;

        // Check for nulls
        final Object o1 = data.getValueAt(row1, column);
        final Object o2 = data.getValueAt(row2, column);

        // If both values are null return 0
        if ((o1 == null) && (o2 == null)) {
            return 0;
        } else if (o1 == null) { // Define null less than everything.

            return -1;
        } else if (o2 == null) {
            return 1;
        }

		/* We copy all returned values from the getValue call in case
		an optimised model is reusing one object to return many values.
		The Number subclasses in the JDK are immutable and so will not be used in
		this way but other subclasses of Number might want to do this to save
		space and avoid unnecessary heap allocation.
		*/
        if (type.getSuperclass() == java.lang.Number.class) {
            final Number n1 = (Number) data.getValueAt(row1, column);
            final double d1 = n1.doubleValue();
            final Number n2 = (Number) data.getValueAt(row2, column);
            final double d2 = n2.doubleValue();

            if (d1 < d2) {
                return -1;
            } else if (d1 > d2) {
                return 1;
            } else {
                return 0;
            }
        } else if (type == java.util.Date.class) {
            final Date d1 = (Date) data.getValueAt(row1, column);
            final long n1 = d1.getTime();
            final Date d2 = (Date) data.getValueAt(row2, column);
            final long n2 = d2.getTime();

            if (n1 < n2) {
                return -1;
            } else if (n1 > n2) {
                return 1;
            } else {
                return 0;
            }
        } else if (type == String.class) {
            final String s1 = (String) data.getValueAt(row1, column);
            final String s2 = (String) data.getValueAt(row2, column);
            final int result = s1.compareTo(s2);

            if (result < 0) {
                return -1;
            } else if (result > 0) {
                return 1;
            } else {
                return 0;
            }
        } else if (type == Boolean.class) {
            final Boolean bool1 = (Boolean) data.getValueAt(row1, column);
            final boolean b1 = bool1.booleanValue();
            final Boolean bool2 = (Boolean) data.getValueAt(row2, column);
            final boolean b2 = bool2.booleanValue();

            if (b1 == b2) {
                return 0;
            } else if (b1) // Define false < true
            {
                return 1;
            } else {
                return -1;
            }
        } else {
            final Object v1 = data.getValueAt(row1, column);
            final String s1 = v1.toString();
            final Object v2 = data.getValueAt(row2, column);
            final String s2 = v2.toString();
            final int result = s1.compareTo(s2);

            if (result < 0) {
                return -1;
            } else if (result > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private int compare(final int row1, final int row2) {
        for (int level = 0; level < sortingColumns.size(); level++) {
            final Integer column = (Integer) sortingColumns.elementAt(level);
            final int result = compareRowsByColumn(row1, row2, column.intValue());

            if (result != 0) {
                return ascending ? result : -result;
            }
        }
        return 0;
    }

    public void reallocateIndexes() {
        final int rowCount = model.getRowCount();

        // Set up a new array of indexes with the right number of elements
        // for the new data model.
        indexes = new int[rowCount];

        // Initialise with the identity mapping.
        for (int row = 0; row < rowCount; row++)
            indexes[row] = row;
    }

    public void tableChanged(final TableModelEvent e) {
        //	System.out.println("Sorter: tableChanged");
        reallocateIndexes();

        super.tableChanged(e);
    }

    private void checkModel() {
        if (indexes.length != model.getRowCount()) {
            System.err.println("Sorter not informed of a change in model.");
        }
    }

    private void sort(final Object sender) {
        checkModel();
        shuttlesort((int[]) indexes.clone(), indexes, 0, indexes.length);
    }

    public void n2sort() {
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = i + 1; j < getRowCount(); j++) {
                if (compare(indexes[i], indexes[j]) == -1) {
                    swap(i, j);
                }
            }
        }
    }

    // This is a home-grown implementation which we have not had time
    // to research - it may perform poorly in some circumstances. It
    // requires twice the space of an in-place algorithm and makes
    // NlogN assigments shuttling the values between the two
    // arrays. The number of compares appears to vary between N-1 and
    // NlogN depending on the initial order but the main reason for
    // using it here is that, unlike qsort, it is stable.
    private void shuttlesort(final int[] from, final int[] to, final int low, final int high) {
        if ((high - low) < 2) {
            return;
        }

        final int middle = (low + high) / 2;
        shuttlesort(to, from, low, middle);
        shuttlesort(to, from, middle, high);

        int p = low;
        int q = middle;

		/* This is an optional short-cut; at each recursive call,
		check to see if the elements in this subset are already
		ordered.  If so, no further comparisons are needed; the
		sub-array can just be copied.  The array must be copied rather
		than assigned otherwise sister calls in the recursion might
		get out of sinc.  When the number of elements is three they
		are partitioned so that the first set, [low, mid), has one
		element and and the second, [mid, high), has two. We skip the
		optimisation when the number of elements is three or less as
		the first compare in the normal merge will produce the same
		sequence of steps. This optimisation seems to be worthwhile
		for partially ordered lists but some analysis is needed to
		find out how the performance drops to Nlog(N) as the initial
		order diminishes - it may drop very quickly.  */
        if (((high - low) >= 4)
            && (compare(from[middle - 1], from[middle]) <= 0)) {
            for (int i = low; i < high; i++) {
                to[i] = from[i];
            }

            return;
        }

        // A normal merge.
        for (int i = low; i < high; i++) {
            if ((q >= high)
                || (p < middle && compare(from[p], from[q]) <= 0)) {
                to[i] = from[p++];
            } else {
                to[i] = from[q++];
            }
        }
    }

    private void swap(final int i, final int j) {
        final int tmp = indexes[i];
        indexes[i] = indexes[j];
        indexes[j] = tmp;
    }

    // The mapping only affects the contents of the data rows.
    // Pass all requests to these rows through the mapping array: "indexes".
    public Object getValueAt(final int aRow, final int aColumn) {
        checkModel();

        return model.getValueAt(indexes[aRow], aColumn);
    }

    public void setValueAt(final Object aValue, final int aRow, final int aColumn) {
        checkModel();
        model.setValueAt(aValue, indexes[aRow], aColumn);
    }

    public void sortByColumn(final int column) {
        sortByColumn(column, true);
    }

    private void sortByColumn(final int column, final boolean ascending) {
        this.ascending = ascending;
        sortingColumns.removeAllElements();
        sortingColumns.addElement(new Integer(column));
        sort(this);
        super.tableChanged(new TableModelEvent(this));
    }

    // There is no-where else to put this.
    // Add a mouse listener to the Table to trigger a table sort
    // when a column heading is clicked in the JTable.
    public void addMouseListenerToHeaderInTable(final JTable table) {
        final TableSorter sorter = this;
        final JTable tableView = table;
        tableView.setColumnSelectionAllowed(false);

        final MouseAdapter listMouseListener = new MouseAdapter() {
            public void mouseClicked(final MouseEvent e) {
                final TableColumnModel columnModel = tableView.getColumnModel();
                final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                final int column = tableView.convertColumnIndexToModel(viewColumn);

                if ((e.getClickCount() == 1) && (column != -1)) {
                    //                    System.out.println("Sorting ...");
                    final int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK;
                    final boolean ascending = (shiftPressed == 0);
                    sorter.sortByColumn(column, ascending);
                }
            }
        };

        final JTableHeader th = tableView.getTableHeader();
        th.addMouseListener(listMouseListener);
    }
}
