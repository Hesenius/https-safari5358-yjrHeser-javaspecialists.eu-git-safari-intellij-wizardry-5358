package com.someone.ppt.gui.printer;

import com.someone.db.models.*;
import com.someone.gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class ValidateTablePanel extends JPanel {
    private JTabbedPane parent;
    private ElegantTable table;
    private ScrollableTableModel model;
    private TableSorter sorter;
    private String[][] tableDefinition;

    public ValidateTablePanel(final String[][] tableDefinition, final JTabbedPane parent) {
        super();
        this.parent = parent;
        this.tableDefinition = tableDefinition;
        setLayout(new BorderLayout());

        try {
            model = TableModelFactory.getResultSetTableModel(tableDefinition[0][0]);
        } catch (final SQLException e) {
            e.printStackTrace();
        }

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

        close.addActionListener(e -> parent.remove(ValidateTablePanel.this));

        northPane.add(close);

        if (model.canUpdate()) {

            final JButton add = new JButton("Add");
            final JButton delete = new JButton("Delete");
            final JButton modify = new JButton("Modify");

            add.addActionListener(e -> addRow());

            delete.addActionListener(e -> deleteRow());

            modify.addActionListener(e -> modifyRow());

            northPane.add(add);
            northPane.add(delete);
            northPane.add(modify);
        }

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
        final int rowToUpdate = sorter.getModelRow(table.getSelectedRow());
        final ValidateInputFrame frame = new ValidateInputFrame(tableDefinition);
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
//		final DataInputFrame frame = new DataInputFrame("Add Row", columnNames);

        final ValidateInputFrame frame = new ValidateInputFrame(tableDefinition);

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


//
//
//fetch size:
//        Statement stmt = connection.createStatement ();
//        int fetchSize = stmt.getFetchSize();
//
//        // Set the fetch size on the statement
//        stmt.setFetchSize(100);
//
//        // Create a result set
//        ResultSet resultSet = stmt.executeQuery("SELECT * FROM my_table");
//
//        // Change the fetch size on the result set
//        resultSet.setFetchSize(100);
//
//transactions supported:
//		try {
//	        DatabaseMetaData dmd = connection.getMetaData();
//	        if (dmd.supportsTransactions()) {
//	            System.out.println("Supports");
//	        } else {
//	            System.out.println("Do not support");
//	        }
//	    } catch (SQLException e) {
//	    }
//
//transactions:
//    try {
//        // Disable auto commit
//        connection.setAutoCommit(false);
//
//        // Do SQL updates...
//
//        // Commit updates
//        connection.commit();
//    } catch (SQLException e) {
//        // Rollback update
//        connection.rollback();
//    }
//
//datatypes:
//This example creates a MySQL table called mysql_all_table to store Java types.
//    try {
//        Statement stmt = connection.createStatement();
//
//        String sql = "CREATE TABLE mysql_all_table("
//            + "col_boolean       BOOL, "              // boolean
//            + "col_byte          TINYINT, "           // byte
//            + "col_short         SMALLINT, "          // short
//            + "col_int           INTEGER, "           // int
//            + "col_long          BIGINT, "            // long
//            + "col_float         FLOAT, "             // float
//            + "col_double        DOUBLE PRECISION, "  // double
//            + "col_bigdecimal    DECIMAL(13,0), "     // BigDecimal
//            + "col_string        VARCHAR(254), "      // String
//            + "col_date          DATE, "              // Date
//            + "col_time          TIME, "              // Time
//            + "col_timestamp     TIMESTAMP, "         // Timestamp
//            + "col_asciistream   TEXT, "              // AsciiStream (< 2^16 bytes)
//            + "col_binarystream  LONGBLOB, "          // BinaryStream (< 2^32 bytes)
//            + "col_blob          BLOB)";              // Blob (< 2^16 bytes)
//
//        stmt.executeUpdate(sql);
//    } catch (SQLException e) {
//    }
//
//get table names:
//  try {
//        // Gets the database metadata
//        DatabaseMetaData dbmd = connection.getMetaData();
//
//        // Specify the type of object; in this case we want tables
//        String[] types = {"TABLE"};
//        ResultSet resultSet = dbmd.getTables(null, null, "%", types);
//
//        // Get the table names
//        while (resultSet.next()) {
//            // Get the table name
//            String tableName = resultSet.getString(3);
//
//            // Get the table's catalog and schema names (if any)
//            String tableCatalog = resultSet.getString(1);
//            String tableSchema = resultSet.getString(2);
//        }
//    } catch (SQLException e) {
//    }
//
//number of rows:
//    try {
//        // Select the number of rows in the table
//        Statement stmt = connection.createStatement();
//        ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) FROM my_table");
//
//        // Get the number of rows from the result set
//        resultSet.next();
//        int rowcount = resultSet.getInt(1);
//    } catch (SQLException e) {
//    }
//
//deleting:
//   try {
//        // Create a statement
//        Statement stmt = connection.createStatement();
//
//        // Prepare a statement to insert a record
//        String sql = "DELETE FROM my_table WHERE col_string='a string'";
//
//        // Execute the delete statement
//        int deleteCount = stmt.executeUpdate(sql);
//        // deleteCount contains the number of deleted rows
//
//        // Use a prepared statement to delete
//
//        // Prepare a statement to delete a record
//        sql = "DELETE FROM my_table WHERE col_string=?";
//        PreparedStatement pstmt = connection.prepareStatement(sql);
//        // Set the value
//        pstmt.setString(1, "a string");
//        deleteCount = pstmt.executeUpdate();
//        System.err.println(e.getMessage());
//
//inserting:
//    try {
//        Statement stmt = connection.createStatement();
//
//        // Prepare a statement to insert a record
//        String sql = "INSERT INTO my_table (col_string) VALUES('a string')";
//
//        // Execute the insert statement
//        stmt.executeUpdate(sql);
//    } catch (SQLException e) {
//    }
//
//updating:
//This example updates a row in a table.
//    try {
//        Statement stmt = connection.createStatement();
//
//        // Prepare a statement to update a record
//        String sql = "UPDATE my_table SET col_string='a new string' WHERE col_string = 'a string'";
//
//        // Execute the insert statement
//        int updateCount = stmt.executeUpdate(sql);
//        // updateCount contains the number of updated rows
//    } catch (SQLException e) {
//    }
//
//scrollable resultsets supported:
//       DatabaseMetaData dmd = connection.getMetaData();
//        if (dmd.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)) {
//            System.out.println("Insensitive scrollable result sets are supported");//
//        }
//        if (dmd.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)) {
//            //
//            System.out.println("Sensitive scrollable result sets are supported");
//        }
//        if (!dmd.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)
//            && !dmd.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)) {
//            	System.out.println("Updatable result sets are not supported");
//            //
//        }
//    } catch (SQLException e) {
//    }
//
//is resultset scrollable?
//   try {
//        // Get type of the result set
//        int type = resultSet.getType();
//
//        if (type == ResultSet.TYPE_SCROLL_INSENSITIVE
//              || type == ResultSet.TYPE_SCROLL_SENSITIVE) {
//            // Result set is scrollable
//        } else {
//            // Result set is not scrollable
//        }
//    } catch (SQLException e) {
//    }
//


}
