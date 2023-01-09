package com.someone.db;

import com.someone.io.*;

import java.io.*;
import java.sql.*;

public class TextImporter {
    public static void importFile(final File file) {
        final Connection connection = ConnectionFactory.getConnection();
        String tableName = file.getName();
        tableName = tableName.substring(0, tableName.indexOf('.'));

        final DelimitedFileDataSource source = new DelimitedFileDataSource(
            file.getAbsolutePath());
        source.getData();

        final String[] columnNames = source.getHeaders();

        Statement statement = null;

        try {
            statement = connection.createStatement();
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        while (source.next()) {
            String query = "Insert into " + tableName + " Values (";

            for (int i = 0; i < (columnNames.length - 1); i++) {
                query += ("'" + source.getFieldValue(columnNames[i]) + "',");
            }

            query += ("'" + source.getFieldValue(
                columnNames[columnNames.length - 1]) + "');");

            try {
                statement.executeUpdate(query);
            } catch (final SQLException e) {
                System.out.println(e);
            }
        }

        try {
            statement.close();
        } catch (final SQLException e) {
        }
    }
}
