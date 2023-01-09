package com.someone.ppt.gui.line;

import com.someone.gui.*;

import javax.swing.table.*;
import java.util.*;

public class LineSorter extends TableSorter {
    public LineSorter() {
        super();
    }

    public LineSorter(final TableModel model) {
        super(model);
    }

    public int compareRowsByColumn(final int row1, final int row2, final int column) {
        final Class type = String.class;
        final TableModel data = model;

        // Check for nulls
        final Object o1 = data.getValueAt(row1, column);
        final Object o2 = data.getValueAt(row2, column);

        // If both values are null return 0
        if ((o1 == null) && (o2 == null)) {
            return 0;
        } else if (o1 == null) { // Define null less than everything.

            return 1;
        } else if (o2 == null) {
            return -1;
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

            if (s1.equals("0") || s1.equals("")) {
                return 1;
            }

            if (s2.equals("0") || s2.equals("")) {
                return -1;
            }

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
}
