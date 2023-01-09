package com.someone.ppt.importer;

import com.someone.db.models.*;
import org.apache.tomcat.util.*;

import java.sql.*;
import java.text.*;

class GrowerParser {
    private String fileName;

    private GrowerParser(final String fileName) {
        this.fileName = fileName;
    }

    private void parse() {
        final GrowerDataSource source = new GrowerDataSource(fileName);
        String[][] data = null;
        try {
            data = source.getData();
        } catch (final RuntimeException e) {
        }
        System.out.println("Data length: " + data.length);

        final long now = new java.util.Date().getTime();

        final FastDateFormat paltracDate = new FastDateFormat(
            new SimpleDateFormat("dd/MM/yyyy"));
        final FastDateFormat paltracTime = new FastDateFormat(
            new SimpleDateFormat("kk:mm"));

        TableModelFactory.deleteTable("Grower");

        try {
            final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
                "Grower");

            final ResultSet resultSet = model.getResultSet();

            for (int i = 0; i < data.length; i++) {
                resultSet.moveToInsertRow();

                for (int j = 0; j < GrowerDataSource.lineHeaders.length; j++) {
                    resultSet.updateString(GrowerDataSource.lineHeaders[j], data[i][j]);
                }

                resultSet.insertRow();
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {
        final GrowerParser parser = new GrowerParser("D:/grower.csv");
        parser.parse();
        System.out.println("finished");
    }
}
