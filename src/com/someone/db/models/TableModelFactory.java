package com.someone.db.models;

import com.someone.db.*;

import java.sql.*;

public class TableModelFactory {
    public static void deleteTable(final String tableName) {
        try {
            final Connection connection = ConnectionFactory.getConnection();
            final int updateType;
            updateType = ResultSet.CONCUR_UPDATABLE;

            final Statement statement = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                updateType);

            statement.executeUpdate("delete from " + tableName);
        } catch (final SQLException e) {
            e.printStackTrace();
        }

    }

    public static ScrollableTableModel getGtinTableModel(final String tableName, final String[] columns)
        throws SQLException {

        return getResultSetTableModel(tableName, columns, null, true);
    }


    public static ScrollableTableModel getResultSetTableModel(final String tableName)
        throws SQLException {

        return getResultSetTableModel(tableName, null, null, false);
    }

    public static ScrollableTableModel getResultSetTableModel(String query, final String key, final boolean updateable)
        throws SQLException {

        final int startIndex = query.indexOf("XXX");

        query = query.substring(0, startIndex) + key
            + query.substring(startIndex + 3);

        final Connection connection = ConnectionFactory.getConnection();
        final int updateType;
        if (updateable) {
            updateType = ResultSet.CONCUR_UPDATABLE;
        } else {
            updateType = ResultSet.CONCUR_READ_ONLY;
        }

        final Statement statement = connection.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            updateType);

        final ResultSet data = statement.executeQuery(query);

//		System.out.println(query);
        return new ScrollableTableModel(data, updateable);
    }


    public static ScrollableTableModel getResultSetTableModel(final String tableName, final String column, final String key)
        throws SQLException {

        return getResultSetTableModel(tableName, new String[]{column}, new String[]{key}, false);
    }

    public static ScrollableTableModel getResultSetTableModel(final String tableName, final String[] columns, final String[] keys)
        throws SQLException {

        return getResultSetTableModel(tableName, columns, keys, false);
    }


    private static boolean findColumnNames(final String tableName, final StringBuffer buffer) throws SQLException {
        final Connection connection = ConnectionFactory.getConnection();

        final Statement statement = connection.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY);

        final ResultSet columnNames = statement.executeQuery("SHOW COLUMNS FROM " +
            tableName);

        boolean hasPrimaryKey = false;

        while (columnNames.next()) {
            buffer.append(columnNames.getString(1)).append(",");

            if ("PRI".equals(columnNames.getString(4))) {
                hasPrimaryKey = true;
            }
        }

        buffer.setLength(buffer.length() - 1);
        columnNames.close();

        return hasPrimaryKey;
    }

    private static String buildQuery(final String tableName, final String query, final String queryKey, final String[] columns, final String[] keys, final boolean distinct) throws SQLException {
        boolean hasPrimaryKey = false;
        final StringBuffer result = new StringBuffer(200);

        if (query != null) {
            final int startIndex = query.indexOf("XXX");

            result.append(query.substring(0, startIndex))
                .append(queryKey).append(query.substring(startIndex + 3));

        } else {
            result.append("select ");
            if (distinct) {
                result.append(" distinct ");
            }

            if (columns == null) {
                hasPrimaryKey = findColumnNames(tableName, result);

            } else {
                if (keys == null) {
                    for (int i = 0; i < (columns.length - 1); i++) {
                        result.append(columns[i] + ",");
                    }

                    result.append(columns[columns.length - 1]);

                } else {
                    findColumnNames(tableName, result);
                }
            }

            result.append(" from ").append(tableName);

            if (keys != null) {
                result.append(" where ");
                for (int i = 0; i < keys.length - 1; i++) {
                    result.append(columns[i]).append(" = '").append(keys[i]);
                    result.append("' AND ");
                }
                result.append(columns[keys.length - 1]).append(" = '")
                    .append(keys[keys.length - 1]).append("'");
            }
        }

        if (hasPrimaryKey) {
            result.append(";");
        }

//		System.out.println(result.toString());
        return result.toString();
    }


    public static ScrollableTableModel getResultSetTableModel(final String tableName, final String[] columns, final String[] keys, final boolean distinct)
        throws SQLException {

        final String query = buildQuery(tableName, null, null, columns, keys, distinct);

        final Connection connection = ConnectionFactory.getConnection();

        final Statement statement = connection.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_UPDATABLE);


        final ResultSet data = statement.executeQuery(query);

        return new ScrollableTableModel(data, query.endsWith(";"));
    }


}
