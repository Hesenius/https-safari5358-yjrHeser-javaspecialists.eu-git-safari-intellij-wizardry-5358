package com.someone.db;

import java.sql.*;
import java.util.*;

public class ConnectionFactory {
    private static Connection connection;

    public static void resetConnection(final String connectionURL) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(connectionURL, "tadadmin", "tadadmin");
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static String[] getTableNames() {
        String[] tableNames = null;

        try {
            final ArrayList metaTableNames = new ArrayList();
            final DatabaseMetaData metadata = ConnectionFactory.getConnection()
                    .getMetaData();

            final String[] types = {"TABLE"};
            final ResultSet resultSet = metadata.getTables(null, null, "%", types);

            while (resultSet.next()) {
                final String tableName = resultSet.getString(3);
                metaTableNames.add(tableName);
            }

            tableNames = (String[]) metaTableNames.toArray(
                new String[metaTableNames.size()]);
        } catch (final SQLException e) {
        }

        return tableNames;
    }

    /**
     * Close the connection to the database.
     */
    public static void closeConnection() {
        try {
            connection.close();
        } catch (final SQLException e) {
            // do nothing
        }
    }
}
