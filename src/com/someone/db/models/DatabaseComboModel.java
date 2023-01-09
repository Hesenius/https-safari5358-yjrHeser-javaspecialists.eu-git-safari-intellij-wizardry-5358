package com.someone.db.models;

/*
import java.sql.*;
import java.util.Arrays;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

import com.someone.db.ConnectionFactory;
public class DatabaseComboModel implements ComboBoxModel {
	private String[] data;
	private int size;
	private int index;
	private String tableName;
	private String column;

	public DatabaseComboModel(Connection connection, String tableName, String column) {

		Statement statement = null;
		ResultSet resultset = null;
		this.tableName = tableName;
		this.column = column;

		try {
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			resultset = statement.executeQuery("Select DISTINCT " + column + " from " + tableName );
			resultset.last();
        	size = resultset.getRow();
        	data = new String[size];
        	resultset.beforeFirst();

			int i = 0;
			while (resultset.next()) {
				data[i++] = resultset.getString(1);
			}

			if (data.length > 0) {
				Arrays.sort(data);
			}

			resultset.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public DatabaseComboModel(String tableName, String column) {

		Statement statement = null;
		ResultSet resultset = null;
		this.tableName = tableName;
		this.column = column;
		Connection connection = ConnectionFactory.getConnection();
		try {
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			resultset = statement.executeQuery("Select DISTINCT " + column + " from " + tableName );
			resultset.last();
        	size = resultset.getRow();
        	data = new String[size];
        	resultset.beforeFirst();

			int i = 0;
			while (resultset.next()) {
				data[i++] = resultset.getString(1);
			}

			Arrays.sort(data);

			resultset.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public DatabaseComboModel(String tableName, String column, String matchColumn, String matchValue) {

		Statement statement = null;
		ResultSet resultset = null;

		try {
			Connection connection = ConnectionFactory.getConnection();
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			resultset = statement.executeQuery("Select DISTINCT " + column +
				" from " + tableName + " where " + matchColumn +
				" = '" + matchValue + "';");
			resultset.last();
        	size = resultset.getRow();
        	data = new String[size];
        	resultset.beforeFirst();

			int i = 0;
			while (resultset.next()) {
				data[i++] = resultset.getString(1);
			}

			Arrays.sort(data);

			resultset.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		if (tableName == null) {
			return;
		}
		String selected = (String)getSelectedItem();

		Statement statement = null;
		ResultSet resultset = null;
		Connection connection = ConnectionFactory.getConnection();

		try {
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			resultset = statement.executeQuery("Select DISTINCT " + column + " from " + tableName );
			resultset.last();
        	size = resultset.getRow();
        	data = new String[size];
        	resultset.beforeFirst();

			int i = 0;
			while (resultset.next()) {
				data[i++] = resultset.getString(1);
			}

			Arrays.sort(data);

			resultset.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		setSelectedItem(selected);
	}

	public String[] getData() {
		return data;
	}

	public int getIndexOf(String value) {
		int result = -1;

		for (int i = 0; i < data.length; i++) {
			if (data[i].equals(value)) {
				result = i;
				break;
			}
		}

		return result;
	}

	public void setSelectedItem(Object anItem) {
		if (anItem == null) {
			return;
		}
		for(int i = 0; i < data.length; i++) {
			if (anItem.equals(data[i])) {
				index = i;
				break;
			}
		}
	}

	public Object getSelectedItem() {
		if (index >= data.length) {
			return null;
		} else {
			return data[index];
		}
	}

	public int getSize() {
		return size;
	}

	public Object getElementAt(int index) {
		if (index >= data.length) {
			return null;
		} else {
			return data[index];
		}
	}

	public void addListDataListener(ListDataListener l) {
	}

	public void removeListDataListener(ListDataListener l) {
	}
} */


import com.someone.db.*;

import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;
import java.util.*;

public class DatabaseComboModel implements ComboBoxModel {
    private String[] data;
    private String[] showData;
    private int size;
    private int index;

    public DatabaseComboModel(final Connection connection, final String tableName, final String column) {

        Statement statement = null;
        ResultSet resultset = null;

        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            resultset = statement.executeQuery("Select DISTINCT " + column + " from " + tableName + " where " + column + " <> '' ");
            resultset.last();
            size = resultset.getRow();
            showData = new String[size];
            data = new String[size];
            resultset.beforeFirst();

            int i = 0;
            while (resultset.next()) {
                showData[i] = resultset.getString(1);
                data[i++] = resultset.getString(1);
            }

            Arrays.sort(data);
            Arrays.sort(showData);

            resultset.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public DatabaseComboModel(final Connection connection, final String tableName, final String column, final String showColumn) {

        Statement statement = null;
        ResultSet resultset = null;

        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            resultset = statement.executeQuery("Select DISTINCT " + column + ", " + showColumn + " from " + tableName + " where " + column + " <> '' ");
            resultset.last();
            size = resultset.getRow();
            data = new String[size];
            showData = new String[size];
            resultset.beforeFirst();

            int i = 0;
            while (resultset.next()) {
                data[i] = resultset.getString(1);
                showData[i++] = resultset.getString(1) + " - " + resultset.getString(2);
            }

            Arrays.sort(showData);
            Arrays.sort(data);

            resultset.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public DatabaseComboModel(final String tableName, final String column) {

        Statement statement = null;
        ResultSet resultset = null;
        final Connection connection = ConnectionFactory.getConnection();
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            resultset = statement.executeQuery("Select DISTINCT " + column + " from " + tableName + " where " + column + " <> '' ");
            resultset.last();
            size = resultset.getRow();
            data = new String[size];
            showData = new String[size];
            resultset.beforeFirst();

            int i = 0;
            while (resultset.next()) {
                data[i] = resultset.getString(1);
                showData[i] = data[i];
                i++;
            }

            Arrays.sort(showData);
            Arrays.sort(data);

            resultset.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public DatabaseComboModel(final String tableName, final String column, final boolean sort, final String sortColumn) {
        if (!sort) {
            throw new RuntimeException("This constructor is only for sorted columns by specific column");
        }

        Statement statement = null;
        ResultSet resultset = null;
        final Connection connection = ConnectionFactory.getConnection();
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            resultset = statement.executeQuery("Select DISTINCT " + column + ", " + sortColumn +
                " from " + tableName + " where " + column + " <> '' " + " order by " + sortColumn);
            resultset.last();
            size = resultset.getRow();
            data = new String[size];
            showData = new String[size];
            resultset.beforeFirst();

            int i = 0;
            while (resultset.next()) {
                data[i] = resultset.getString(1);
                showData[i] = data[i];
                i++;
            }

            Arrays.sort(showData);
            Arrays.sort(data);

            resultset.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }


    public DatabaseComboModel(final String tableName, final String column, final String matchColumn, final String matchValue) {

        Statement statement = null;
        ResultSet resultset = null;

        try {
            final Connection connection = ConnectionFactory.getConnection();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            resultset = statement.executeQuery("Select DISTINCT " + column +
                " from " + tableName + " where " + matchColumn +
                " = '" + matchValue + "';");
            resultset.last();
            size = resultset.getRow();
            data = new String[size];
            showData = new String[size];
            resultset.beforeFirst();

            int i = 0;
            while (resultset.next()) {
                showData[i] = resultset.getString(1);
                data[i] = showData[i];
                i++;
            }

            Arrays.sort(data);
            Arrays.sort(showData);

            resultset.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }


    public int getIndexOf(final String value) {
        int result = -1;

        for (int i = 0; i < showData.length; i++) {
            if (data[i].equals(value)) {
                result = i;
                break;
            }
        }

        return result;
    }

    public String[] getData() {
        return data;
    }

    public void setSelectedItem(final Object anItem) {
        for (int i = 0; i < data.length; i++) {
            if (anItem.equals(showData[i])) {
                index = i;
                break;
            }
        }
    }

    public Object getSelectedItem() {
        if (index >= showData.length) {
            return null;
        } else {
            return data[index];
        }
    }

    public int getSize() {
        return size;
    }

    public Object getElementAt(final int index) {
        if (index >= showData.length) {
            return null;
        } else {
            return showData[index];
        }
    }

    public void addListDataListener(final ListDataListener l) {
    }

    public void removeListDataListener(final ListDataListener l) {
    }

}

