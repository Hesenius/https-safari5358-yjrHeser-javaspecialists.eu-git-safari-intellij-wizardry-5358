package com.someone.ppt.cds;

import com.someone.db.models.*;
import com.someone.ppt.models.*;

import java.sql.*;
import java.util.*;

class PrtGenerator {
    private String templateName;
    private Packhouse packhouse;
    private String lineString;
    private StringBuffer errors;
    private String[][] prtTemplatesIndexes;
    private String[][] prtData;
    private String printerName;
    private ArrayList definedPackTypes;

    public PrtGenerator(final Packhouse packhouse, final int lineNumber,
                        final StringBuffer errors) {
        this.errors = errors;
        this.packhouse = packhouse;
        templateName = packhouse.getTemplateName(lineNumber);
        lineString = packhouse.getLineString(lineNumber);
    }

    private int getIndex(final String[][] data, final String value, final int column) {
        for (int i = 0; i < data.length; i++) {
            try {
                if (value.equalsIgnoreCase(data[i][column])) {
                    return (i + 1);
                }
            } catch (final RuntimeException e) {
            }
        }

        return -1;
    }

    public void generatePrt() {
        try {
            String[][] staticCodeMatrix = null;
            String[][] dynamicCodeMatrix = null;
            String[][] templates = null;
            String[][] printFields = null;

            try {
                loadPrinterName();
//				System.out.println("Printer for line: " + printerName);
                getPackTypes();
                staticCodeMatrix = getStaticCodes();
//				System.out.println("static length: " + staticCodeMatrix.length);
                dynamicCodeMatrix = getDynamicCodes();
//				System.out.println("dynamicCodeMatrix length: " + dynamicCodeMatrix.length);
            } catch (final RuntimeException e) {
                e.printStackTrace();
            }

            // Templates      *****************
            final String query =
                "select * from printertemplates where PrinterID = 'XXX' order by " +
                    "templateName, lineNumber";

//			System.out.println(printerName);

            ScrollableTableModel model = TableModelFactory.getResultSetTableModel(query, printerName, false);

            templates = new String[model.getRowCount()][5];

            ResultSet resultSet = model.getResultSet();
            resultSet.beforeFirst();
            int i = 0;
            while (resultSet.next()) {
                templates[i][0] = resultSet.getString("TemplateName");
                templates[i][1] = resultSet.getString("LineNumber");
                templates[i][2] = resultSet.getString("PrintField").trim();
                templates[i][3] = resultSet.getString("Length");
                templates[i][4] = resultSet.getString("Control");
//				for (int j = 0; j < 5; j++) {
//					System.out.print(templates[i][j] + " ");
//				}
//				System.out.println();
                i++;

            }

            model.close();


            model = TableModelFactory.getResultSetTableModel("printFields");
            printFields = new String[model.getRowCount()][3];

            resultSet = model.getResultSet();
            resultSet.beforeFirst();
            i = 0;
            while (resultSet.next()) {
                printFields[i][0] = resultSet.getString("PrintField").trim();
                printFields[i][1] = resultSet.getString("EventID");
                printFields[i][2] = resultSet.getString("Position");
//				for (int j = 0; j < 3; j++) {
//					System.out.print(printFields[i][j] + " ");
//				}
//				System.out.println();
                i++;
            }

            model.close();

            prtData = new String[staticCodeMatrix.length +
                dynamicCodeMatrix.length + templates.length][6];

            // output static codes
            // output control chars
            // ouput templates
            int count = 0;

//			System.out.println("prt length: " + prtData.length);
            for (i = 0; i < staticCodeMatrix.length; i++) {
                prtData[count][0] = "250";
                prtData[count][1] = "" + (i + 1);
                prtData[count][2] = staticCodeMatrix[i][1];
                prtData[count][3] = null;
                prtData[count][4] = null;
                prtData[count][5] = null;
//				for (int j = 0; j < 3; j++) {
//					System.out.print(prtData[count][j] + " ");
//				}
//				System.out.println();

                count++;
            }

            for (i = 0; i < dynamicCodeMatrix.length; i++) {
                prtData[count][0] = "251";
                prtData[count][1] = "0" + (i + 1);
                prtData[count][2] = dynamicCodeMatrix[i][1];
                prtData[count][3] = null;
                prtData[count][4] = null;
                prtData[count][5] = null;
//				for (int j = 0; j < 3; j++) {
//					System.out.print(prtData[count][j] + " ");
//				}
//				System.out.println();
                count++;
            }

            int templateNumber = 1;
            int templateCounter = 1;

            try {
                templateName = templates[0][0];
            } catch (final RuntimeException e) {
                errors.append("No printer defined for line " + lineString +
                    "\n");

                System.out.println("No printer defined for line " + lineString);
                return;
            }

            final int templateStart = count;
            final ArrayList templateNames = new ArrayList();
            final ArrayList templateCounters = new ArrayList();
            templateNames.add(templateName);
            templateCounters.add("" + templateNumber);
            prtData[count][0] = "252";
//			System.out.println("TemplateName " + templateName + " : " + templateNumber);

            for (i = 0; i < templates.length; i++) {
//            	System.out.println(i + ": " + templates[i][0]);
                if (!templates[i][0].equals(templateName)) {
                    templateName = templates[i][0];
                    templateNames.add(templateName);
                    templateNumber++;
                    templateCounters.add("" + templateNumber);
                    templateCounter = 1;
//                    System.out.println("TemplateName " + templateName + " : " + templateNumber);
                }

                prtData[count][0] = "252";
                prtData[count][1] = "" + templateNumber;
                prtData[count][2] = "" + templateCounter++;

                if (!"".equals(templates[i][2])) {
                    final int index = getIndex(printFields, templates[i][2], 0) - 1;

                    try {
                        prtData[count][3] = printFields[index][1];
                        prtData[count][4] = printFields[index][2];
                        prtData[count][5] = templates[i][3];
                    } catch (final RuntimeException e) {
                        prtData[count][3] = "0";
                        prtData[count][4] = "0";
                        prtData[count][5] = "0";
//                        System.out.println("Index for " + templates[i][2] +
//                                           ": " + index);
                    }
                } else {
                    final int index = getIndex(dynamicCodeMatrix, templates[i][4], 0);

                    try {
                        prtData[count][3] = "251";
                        prtData[count][4] = "" +
                            getIndex(dynamicCodeMatrix,
                                templates[i][4], 0);
                        if ("SPACE".equals(templates[i][4])) {
                            prtData[count][5] = templates[i][3];
                        } else {
                            prtData[count][5] = "0";
                        }
                    } catch (final RuntimeException e) {
                        prtData[count][3] = "0";
                        prtData[count][4] = "0";
                        prtData[count][5] = "0";
//                        System.out.println("printercodeIndex for " +
//                                           templates[i][4] + ": " + index);
                    }
                }

                count++;
            }

            for (i = 0; i < prtTemplatesIndexes.length; i++) {

                final String name = prtTemplatesIndexes[i][1];
                final int index = templateNames.indexOf(name);
                if (index < 0) {
                    prtTemplatesIndexes[i][2] = "" + 0;
                } else {
                    prtTemplatesIndexes[i][2] = (String) templateCounters.get(index);
                }

//				System.out.println(prtTemplatesIndexes[i][0] + "\t" +
//					prtTemplatesIndexes[i][1] + "\t" +
//					prtTemplatesIndexes[i][2]);

            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void getPackTypes() throws SQLException {
        definedPackTypes = new ArrayList();
        final String[] packcolumns = new String[]{"PackhouseName", "LineName"};
        final String[] packkeys = new String[]{
            packhouse.getPackhouseName(), lineString
        };

        final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
            "lineprintertemplates", packcolumns, packkeys);

        prtTemplatesIndexes = new String[model.getRowCount()][3];
        final ResultSet resultSet = model.getResultSet();
        resultSet.beforeFirst();
        int i = 0;
        while (resultSet.next()) {
            definedPackTypes.add(resultSet.getString("PackID"));
            prtTemplatesIndexes[i][0] = resultSet.getString("PackID");
            prtTemplatesIndexes[i][1] = resultSet.getString("TemplateName");
            prtTemplatesIndexes[i][2] = "0";


//		   	System.out.println(prtTemplatesIndexes[i][0] + "\t" +
//				prtTemplatesIndexes[i][1] + "\t" +
//				prtTemplatesIndexes[i][2]);
            i++;
        }

        model.close();
    }

    private String[][] getDynamicCodes() throws SQLException {
        final String[][] dynamicCodeMatrix;
        final ScrollableTableModel model = TableModelFactory.getResultSetTableModel("printercodes",
            "PrinterID",
            printerName);
        final ResultSet resultSet = model.getResultSet();
        dynamicCodeMatrix = new String[model.getRowCount()][2];
//		System.out.println("dynamic length: " + dynamicCodeMatrix.length);
        resultSet.beforeFirst();
        int i = 0;

        while (resultSet.next()) {
            dynamicCodeMatrix[i][0] = resultSet.getString("CodeDesc");
            ;

            final int code = Integer.parseInt(resultSet.getString("Code"));
            String hex = Integer.toHexString(code);

            if (hex.length() < 2) {
                hex = "0" + hex;
            }

            dynamicCodeMatrix[i][1] = hex;
            i++;
        }

        model.close();
        return dynamicCodeMatrix;
    }

    private String[][] getStaticCodes() throws SQLException {
        final String[][] staticCodeMatrix;
        final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
            "printerstaticcodes", "PrinterID", printerName);
        final ResultSet resultSet = model.getResultSet();
        staticCodeMatrix = new String[model.getRowCount()][2];
        resultSet.beforeFirst();
        int i = 0;
        while (resultSet.next()) {
            staticCodeMatrix[i][0] = resultSet.getString("CodeDesc");

            final int code = Integer.parseInt(resultSet.getString("Code"));
            String hex = Integer.toHexString(code);

            if (hex.length() < 2) {
                hex = "0" + hex;
            }

            staticCodeMatrix[i][1] = hex;
            i++;
        }

        model.close();
        return staticCodeMatrix;
    }

    private void loadPrinterName() throws SQLException {
        // Printer Name *****************
        final String[] lineColumns = new String[]{"PackhouseName", "LineName"};
        final String[] lineKeys = new String[]{
            packhouse.getPackhouseName(), lineString
        };
        final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
            "packhouseprinters",
            lineColumns, lineKeys);
        final ResultSet resultSet = model.getResultSet();
        resultSet.beforeFirst();
        resultSet.next();
        printerName = resultSet.getString("PrinterID");
        model.close();
    }

    public String[][] getPrtData() {
        return prtData;
    }

    public String[][] getPrtTemplatesIndexes() {
        return prtTemplatesIndexes;
    }

    public String getPrinterName() {
        return printerName;
    }

    public ArrayList getDefinedPackTypes() {
        return definedPackTypes;
    }

    private static void calculateFakeGTIN(final int nextGTIN) {
        final String prefix = "6002200";
        int alternateDigit = 2;
        int remainingDigit = 8;
        final int modResult;

        int next = nextGTIN;
        final int allocatedGTIN = next;

        alternateDigit += next / 10000;
        next = next % 10000;
        remainingDigit += next / 1000;
        next = next % 1000;
        alternateDigit += next / 100;
        next = next % 100;
        remainingDigit += next / 10;
        next = next % 10;
        alternateDigit += next;

        alternateDigit *= 3;
        alternateDigit += remainingDigit;
        modResult = ((10 - (alternateDigit % 10)) % 10);

        String paddedNumber = "" + allocatedGTIN;
        while (paddedNumber.length() < 5) {
            paddedNumber = "0" + paddedNumber;
        }

        System.out.println("Final number is " + prefix + paddedNumber + modResult);
    }

    private static void calculateFakeGTIN2(final int number) {
        final String firstPart = "6002200";
        String finalNumber;
        final int firstPartStep1 = 2;
        final int firstPartStep2 = 8;
        final int max = 10000;
        int step1 = 0;
        int step2 = 0;


        final Random r = new Random();
//		int next = r.nextInt(99000);
        int next = number;
        final int temp = next;


        step1 += next / 10000;
        next = next % 1000;
        step1 += next / 100;
        next = next % 10;
        step1 += next;

        next = temp;

        next = next % 10000;
        step2 += next / 1000;
        next = next % 100;
        step2 += next / 10;


        step1 = (firstPartStep1 + step1) * 3;
        step2 = step2 + firstPartStep2;
        step1 += step2;
        step1 = ((10 - (step1 % 10)) % 10);

        String sizer = "" + temp;
        while (sizer.length() < 5) {
            sizer = "0" + sizer;
        }

        System.out.println("Final number is " + firstPart + sizer + step1);

    }


    public static void main(final String[] args) {
        calculateFakeGTIN(12345);
        calculateFakeGTIN(2);
        calculateFakeGTIN(3);
        calculateFakeGTIN(4);
        calculateFakeGTIN(5);
        calculateFakeGTIN(6);
        calculateFakeGTIN(7);
        calculateFakeGTIN(8);
        calculateFakeGTIN(9);
        calculateFakeGTIN(10);
        calculateFakeGTIN(11);
        calculateFakeGTIN(22);
//		System.out.println((10-(17%10))%10);
    }

}
