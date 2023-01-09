package com.someone.ppt.gui.db;

import com.someone.db.models.*;
import com.someone.ppt.models.*;

import java.sql.*;
import java.util.*;

/**
 * To Do:
 * Don't extract data for the same template twice
 * Get data for individual templates
 * Sorting of lines?
 **/
public class TemplateDBSource {
    private String[] templateNames;
    private int[] lines;
    private String[][] data;
    private ArrayList allTemplates;
    private String packhouseName;

    public TemplateDBSource(final String packhouseName) {
        this.packhouseName = packhouseName;
    }

    public void loadData() {
        readTemplateNames();
        allTemplates = new ArrayList();

        for (int i = 0; i < lines.length; i++) {
            allTemplates.add(extractTemplate(templateNames[i], lines[i]));
        }

        int totalRows = 0;

        for (int i = 0; i < lines.length; i++) {
            final String[][] template = (String[][]) allTemplates.get(i);
            totalRows += template.length;
        }

        data = new String[totalRows][];

        int count = 0;

        for (int i = 0; i < lines.length; i++) {
            final String[][] template = (String[][]) allTemplates.get(i);

            for (int j = 0; j < template.length; j++) {
                data[count++] = template[j];
            }
        }
    }

    private void readTemplateNames() {
        final ScrollableTableModel model;

        try {
            model = TableModelFactory.getResultSetTableModel(
                "packhousetemplates", "PackhouseName",
                packhouseName);

            final int rowCount = model.getRowCount();
            templateNames = new String[rowCount];
            lines = new int[rowCount];

            for (int i = 0; i < rowCount; i++) {
                lines[i] = Integer.parseInt(
                    (String) model.getValueAt(i, 1));
                templateNames[i] = (String) model.getValueAt(i, 2);
            }

            model.close();
        } catch (final SQLException e) {
        }
    }

    public String[][] getTemplateData(final int templateIndex) {
        try {
            return (String[][]) allTemplates.get(templateIndex);
        } catch (final Exception e) {
            return new String[][]
                {
                    {null, null}
                };
        }
    }

    public String[][] getData() {
        return data;
    }

    public String[][] extractTemplate(final String templateName, final int lineNumber) {
        final ScrollableTableModel model;
        String[][] template = null;

        try {
            model = TableModelFactory.getResultSetTableModel("templates",
                "TemplateName",
                templateName);

            final int rowCount = model.getRowCount();
            final int columnCount = model.getColumnCount();
            template = new String[rowCount][];

            for (int i = 0; i < rowCount; i++) {
                template[i] = new String[columnCount];

                final String[] dbColumns = model.getColumnNames();

                template[i][0] = templateName;
                template[i][1] = "" + lineNumber;

                for (int j = 2; j < columnCount; j++) {
                    final int k = FruitspecTableModel.findIndex(dbColumns[j]);
                    template[i][k + 2] = (String) model.getValueAt(i, j);
                }
            }

            model.close();
            setLineNumber(template, lineNumber);
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        if ((template.length == 0) || (template.length == 1)) {
            return new String[][]
                {
                    {null, null}
                };
        } else {
            return template;
        }
    }

    private void setLineNumber(final String[][] templateData, final int lineNumber) {
        final String lineString;

        if (lineNumber < 10) {
            lineString = "0" + lineNumber;
        } else {
            lineString = "" + lineNumber;
        }

        for (int i = 0; i < templateData.length; i++) {
            templateData[i][1] = lineString;
        }
    }

    public String[] getTemplateNames() {
        return templateNames;
    }
}
