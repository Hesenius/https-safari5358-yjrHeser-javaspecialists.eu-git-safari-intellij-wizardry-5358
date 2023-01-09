package com.someone.ppt.gui.db;

import com.someone.db.models.*;
import com.someone.ppt.models.*;

import java.sql.*;

public class TemplateDBSink {
    private String templateName;
    private String[][] data;

    public TemplateDBSink(final String templateName, final String[][] data) {
        this.templateName = templateName;
        this.data = data;

        if (templateExists()) {
            deleteTemplate();
        }

        insertData();
    }

    private boolean templateExists() {
        final ScrollableTableModel model;
        boolean result = false;
        try {
            model = TableModelFactory.getResultSetTableModel("templates", "TemplateName", templateName);
            if (model.getRowCount() != 0) {
                result = true;
                ;
            }
            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void deleteTemplate() {
        final ScrollableTableModel model;
        try {
            model = TableModelFactory.getResultSetTableModel("templates", "TemplateName", templateName);
            final ResultSet resultset = model.getResultSet();
            resultset.beforeFirst();
            while (resultset.next()) {
                resultset.deleteRow();
                resultset.beforeFirst();
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertData() {
        final ScrollableTableModel model;
        try {
            model = TableModelFactory.getResultSetTableModel("templates", "TemplateName", templateName);
            final ResultSet resultset = model.getResultSet();
            final String[] columnNames = FruitspecTableModel.columnNames;

            for (int i = 0; i < data.length; i++) {
                resultset.moveToInsertRow();
                resultset.updateString(1, templateName);
                resultset.updateString(2, data[i][1]);
                for (int j = 2; j < data[i].length; j++) {
                    String newdata = data[i][j];
                    if (newdata == null) {
                        newdata = "";
                    }
                    resultset.updateString(columnNames[j - 2], newdata);
                }
                resultset.insertRow();
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }

    }

    public String[][] getData() {
        return data;
    }

//	private String[][] extractTemplate(String templateName, int lineNumber) {
//		ScrollableTableModel model;
//		String[][] template = null;
//		try {
//			model = TableModelFactory.getResultSetTableModel("templates", "TemplateName", templateName);
//			String[] dbColumns = model.getColumnNames();
//
//			int rowCount = model.getRowCount();
//			int columnCount = model.getColumnCount();
//			template = new String[rowCount][];
//			for (int i = 0; i < rowCount; i++) {
//				template[i] = new String[columnCount];
//				template[i][0] = templateName;
//				template[i][1] = ""+lineNumber;
//
//				for (int j = 2; j < columnCount; j++) {
//					int k = FruitspecTableModel.findIndex(dbColumns[j]);
//					template[i][k+2] = (String)model.getValueAt(i, j);
//				}
//			}
//			model.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return template;
//	}

}
