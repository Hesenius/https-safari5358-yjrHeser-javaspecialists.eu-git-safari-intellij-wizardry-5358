package com.someone.ppt.models;

import com.someone.ppt.gui.db.*;
import com.someone.ppt.gui.packhouse.*;
import org.jdom.*;

/**
 * Keeps all the data of this packhouse associated templates together
 * with the packhouse physical layout .
 * Can only handle 50 lines per packhouse.
 */
public class Packhouse {
    private String packhouseName;
    private String[][] matrixLayout;
    private Element xmlLayout;
    private int[] lineIndexes;
    private int lineCount;
    private TemplateDBSource templates;
    private String[] templateNames;
    private String[][][] lineData;

    /**
     * Constructor sets only the packhouse name.
     * loadLayout() loads the physical layout and
     * loadTemplates() loads the template data
     */
    public Packhouse(final String packhouseName) {
        this.packhouseName = packhouseName;
    }

    public String getLineString(final int line) {
        final String lineString;
        if ((line + 1) < 10) {
            lineString = "0" + (line + 1);
        } else {
            lineString = "" + (line + 1);
        }
        return lineString;
    }

    public String getShift(final int line) {
        final int shiftIndex = 2 + FruitspecTableModel.findIndex("Shift");
        return lineData[line][0][shiftIndex];
    }

    /**
     * Count the number of tables defined for this line.
     */
    public int countTables(final int lineNumber) {
        final String[][] data = getLineData(lineNumber);
        String dropName = data[0][2];
        String tableName = data[0][3];

        int tableCount = 1;

        for (int i = 1; i < data.length; i++) {
            try {
                if (!dropName.equals(data[i][2])) {
                    tableCount++;
                    dropName = data[i][2];
                    tableName = data[i][3];
                } else if (!tableName.equals(data[i][3])) {
                    tableCount++;
                    tableName = data[i][3];
                }
            } catch (final RuntimeException e) {
            }
        }

        return tableCount;
    }

    /**
     * Loads the packhouse from the database and calculates data related indexes
     */
    public void loadLayout() {
        matrixLayout = PackhouseDbSource.getLayout(packhouseName);
        xmlLayout = LayoutConverter.convertLayout(matrixLayout);
        calculateLineIndexes();
    }

    /**
     * Calculates the begin and start indexes of each line in the layout matrix.
     */
    private void calculateLineIndexes() {
        final int MAX_LINES = 50;
        lineIndexes = new int[MAX_LINES];
        java.util.Arrays.fill(lineIndexes, -1);
        lineCount = 1;

        String lineName = getLineName(0);
        lineIndexes[0] = 0;

        for (int i = 0; i < matrixLayout.length; i++) {
            try {
                if (!lineName.equals(getLineName(i))) {
                    lineName = getLineName(i);
                    lineIndexes[lineCount++] = i;
                }
            } catch (final RuntimeException e) {
                System.out.println("Error at line: " + i + " -> " + e);
            }
        }

        lineIndexes[lineCount] = matrixLayout.length;
    }

    /**
     * Loads the data in the templates registered to this database.
     */
    public void loadTemplates() {
        templates = new TemplateDBSource(packhouseName);
        templates.loadData();
        calculateLineData();
        templateNames = new String[lineCount];

        for (int i = 0; i < lineCount; i++) {
            try {
                templateNames[i] = templates.getTemplateNames()[i];
            } catch (final Exception e) {
                templateNames[i] = "?";
            }
        }
    }


    private void calculateLineData() {
        lineData = new String[lineCount][][];

        String[][] templateData;

        for (int i = 0; i < lineCount; i++) {
            templateData = templates.getTemplateData(i);
            updateLineData(i, templateData);
        }
    }

    private String[] getMatch(final int layoutIndex, final String[][] templateData) {
        final String[] layout = matrixLayout[layoutIndex];
        boolean match;

        for (int i = 0; i < templateData.length; i++) {
            match = true;

            if ((templateData[i] == null) || (templateData[i][1] == null) ||
                (matrixLayout == null)) {
                match = false;
            }

            if (match) {
                match &= templateData[i][1].equals(layout[1]); //line
                match &= templateData[i][2].equals(layout[2]); //drop
                match &= templateData[i][3].equals(layout[3]); //table
                match &= templateData[i][4].equals(layout[4]); //station
            }

            if (match) {
                return templateData[i];
            }
        }

        final String[] emptyLine = new String[PackhouseFruitSpec.fields.length];
        emptyLine[0] = templateData[0][0];
        emptyLine[1] = layout[1];
        emptyLine[2] = layout[2];
        emptyLine[3] = layout[3];
        emptyLine[4] = layout[4];

        return emptyLine;
    }

    public void updateLineData(final int lineNumber, final String[][] templateData) {
        final String[][] line = new String[lineIndexes[lineNumber + 1] -
            lineIndexes[lineNumber]][templateData[0].length];
        final int offSet = lineIndexes[lineNumber];

        for (int j = 0; j < line.length; j++) {
            line[j] = getMatch(offSet + j, templateData);
        }

        lineData[lineNumber] = line;
    }

    public void setTemplateName(final int lineNumber, final String templateName) {
        templateNames[lineNumber] = templateName;

        //		for (int i = 0; i < templateNames.length; i++) {
        //			System.out.println(templateName + i + " ? " + templateNames[i]);
        //			if (i == lineNumber) {
        //				continue;
        //			}
        //
        //			if (templateNames[i].equals(templateName)) {
        //
        //                TemplateDBSource source = new TemplateDBSource(
        //                                                  "doesnotmatter");
        //                String[][]       templateData =
        //                        source.extractTemplate(templateName, (i+1));
        //
        //                updateLineData(i, templateData);
        //			}
        //		}
    }

    public String getPackhouseName() {
        return packhouseName;
    }

    public String[][] getMatrixLayout() {
        return matrixLayout;
    }

    public Element getXmlLayout() {
        return xmlLayout;
    }

    public String getTemplateName(final int lineNumber) {
        return templateNames[lineNumber];
    }

    public int getLineCount() {
        return lineCount;
    }

    private String getLineName(final int lineNumber) {
        return matrixLayout[lineNumber][1];
    }

    public int getLineStart(final int lineNumber) {
        return lineIndexes[lineNumber];
    }

    public int getLineEnd(final int lineNumber) {
        return lineIndexes[lineNumber + 1];
    }

    public String[][] getLineData(final int lineNumber) {
        return lineData[lineNumber];
    }
}
