package com.someone.ppt.cds;

import com.someone.db.models.*;
import com.someone.ppt.models.*;

import java.io.*;
import java.sql.*;
import java.util.*;

class PcdGenerator {
    private Hashtable packtypeIndex;
    private String[][] pcdMark;
    private String[][] pcdPool;
    private String[][] pcdClass;
    private String[][] pcdTargetMarket;
    private String[][] pcdPackType; //ftdData,prtTemplateIndexes
    private String[][] pcdCountry;
    private String[][] pcdOrganisation;
    private String[][] pcdPackhouseID;
    private String[][] pcdGrowerID;
    private String[][] pcdDateCode;
    private String[][] pcdInventory;
    private String[][] pcdPickingRef;
    private String[][] pcdRunNumber;
    private String[][] pcdPUC;
    private String[][] pcdGtin;
    private String[][] pcdRemarks;


    private String templateName;
    private String[][] ftdData;
    private String[][] prtTemplatesIndexes;

    public PcdGenerator(
        final Packhouse packhouse,
        final int lineNumber,
        final String[][] ftdData,
        final String[][] prtTemplatesIndexes) {

        this.ftdData = ftdData;
        this.prtTemplatesIndexes = prtTemplatesIndexes;
//		for (int i = 0; i < prtTemplatesIndexes.length; i++) {
//			System.out.println(i + " " + prtTemplatesIndexes[i][0] + " " + prtTemplatesIndexes[i][2]);
//		}

        templateName = packhouse.getTemplateName(lineNumber);
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

        return 1;
    }


    private void printPcdTemplate(final BufferedWriter writer, final String[][] bigTemplate) throws IOException {
        final String[][] data = bigTemplate;

        if (data == null || data.length == 0) {
            return;
        }

        final int columns = data[0].length;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < columns; j++) {
                if (data[i][j] == null) {
                    continue;
                }

                if (data[i][j].equals("-1")) {
                    data[i][j] = "0";
                }

                writer.write(data[i][j]);

                if (((j + 1) < columns) && (data[i][j + 1] != null)) {
                    writer.write("     ");
                }
            }
            writer.write("\n");
        }
    }


    public void writePcdToFile(final File directory, final String fileName) {
        try {
            final File outputFile = new File(directory, fileName);
            final BufferedWriter writer = new BufferedWriter(
                new FileWriter(outputFile));

            printPcdTemplate(writer, pcdPackType);
            printPcdTemplate(writer, pcdMark);
            printPcdTemplate(writer, pcdPool);
            printPcdTemplate(writer, pcdClass);
            printPcdTemplate(writer, pcdTargetMarket);
            printPcdTemplate(writer, pcdPackhouseID);
            printPcdTemplate(writer, pcdGrowerID);
            printPcdTemplate(writer, pcdDateCode);
            printPcdTemplate(writer, pcdInventory);
            printPcdTemplate(writer, pcdPickingRef);
            printPcdTemplate(writer, pcdRunNumber);
            printPcdTemplate(writer, pcdCountry);
            printPcdTemplate(writer, pcdOrganisation);
            printPcdTemplate(writer, pcdPUC);
            printPcdTemplate(writer, pcdGtin);
            printPcdTemplate(writer, pcdRemarks);

            writer.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }


    private void generatePcdMark() {
        final String query =
            "select distinct gtin.Mark, gtin.MarkDesc " +
                "from gtin XXX where gtin.MarkDesc != ''";

        try {
            final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
                query, "", false);

            pcdMark = new String[model.getRowCount() + 1][4];

            pcdMark[0][0] = "" + 160;
            pcdMark[0][1] = "" + (0 + 1);
            pcdMark[0][2] = "None";
            pcdMark[0][3] = "None";

            for (int i = 1; i < pcdMark.length; i++) {
                pcdMark[i][0] = "" + 160;
                pcdMark[i][1] = "" + (i + 1);
                pcdMark[i][2] = (String) model.getValueAt(i, 0);
                pcdMark[i][3] = (String) model.getValueAt(i, 1);
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPackIndex(final String code, final String fruit) {
        try {
            final Integer result = (Integer) packtypeIndex.get(fruit + code);
            return result.intValue();
        } catch (final RuntimeException e) {
            System.out.println("no packtype index for " + fruit + code);
            return 0;
        }

//			 int fruitStart = 0;
//			 int counter = 0;
//			 while (fruitStart == 0) {
//				 if (fruit.equals(pcdPackType[counter][1])) {
//					 fruitStart = counter;
//				 }
//				 counter++;
//			 }
//
//			 for (int i = 0; i < pcdPackType.length; i++) {
//				 if (fruit.equals(pcdPackType[i][1]) &&
//						 code.equals(pcdPackType[i][12])) {
//					 return (i + 1 - fruitStart);
//				 }
//			 }
//
//			 return 0;


    }

    private void generatePcdPackType() {
        try {
            final String query = "select XXX from packprintsetup where CommodityID in ('AP', 'PR', 'PL') order by CommodityID";

            final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
                query, "*", false);

            final ResultSet resultSet = model.getResultSet();

            pcdPackType = new String[model.getRowCount() + 4][12];
            resultSet.beforeFirst();
            resultSet.next();

            int count = 1;
            int fruitType = getIndex(ftdData, resultSet.getString("CommodityID"), 2);

            resultSet.beforeFirst();

            int i = 0;

            pcdPackType[i][0] = "152";
            pcdPackType[i][1] = "1";

            pcdPackType[i][2] = "1";
            pcdPackType[i][3] = "0"; //mass
            pcdPackType[i][4] = "0"; //dev-l
            pcdPackType[i][5] = "0"; //dev-h
            pcdPackType[i][6] = "0"; //print
            pcdPackType[i][7] = "0"; //export
            pcdPackType[i][8] = "0"; //glue
            pcdPackType[i][9] = "0"; //glue
            pcdPackType[i][10] = "0"; //glue
            pcdPackType[i][11] = "None"; //glue
            i++;

            packtypeIndex = new Hashtable();
            boolean nuut = true;
            while (resultSet.next()) {
                pcdPackType[i][0] = "152";
                pcdPackType[i][1] = "" +
                    getIndex(ftdData,
                        resultSet.getString("CommodityID"), 2); //fruitID

                if (nuut || fruitType != Integer.parseInt(pcdPackType[i][1])) {
                    nuut = false;
                    count = 1;
                    fruitType = Integer.parseInt(pcdPackType[i][1]);


                    pcdPackType[i][0] = "152";
                    pcdPackType[i][1] = "" +
                        getIndex(ftdData,
                            resultSet.getString("CommodityID"), 2);

                    pcdPackType[i][2] = "" + (count++);
                    pcdPackType[i][3] = "0"; //mass
                    pcdPackType[i][4] = "0"; //dev-l
                    pcdPackType[i][5] = "0"; //dev-h
                    pcdPackType[i][6] = "0"; //print
                    pcdPackType[i][7] = "0"; //export
                    pcdPackType[i][8] = "0"; //glue
                    pcdPackType[i][9] = "0"; //glue
                    pcdPackType[i][10] = "0"; //glue
                    pcdPackType[i][11] = "None"; //glue
                    i++;

                    pcdPackType[i][0] = "152";
                    pcdPackType[i][1] = "" +
                        getIndex(ftdData,
                            resultSet.getString("CommodityID"), 2); //fruitID
                }

                pcdPackType[i][2] = "" + (count++);
                pcdPackType[i][3] = resultSet.getString("PackMass"); //mass
                pcdPackType[i][4] = resultSet.getString("LowerTolerance"); //dev-l
                pcdPackType[i][5] = resultSet.getString("UpperTolerance"); //dev-h
                pcdPackType[i][6] = resultSet.getString("Print"); //print
                pcdPackType[i][7] = resultSet.getString("Export"); //export
                pcdPackType[i][8] = resultSet.getString("Glue"); //glue

//				packtypeIndex.put(pcdPackType[i][1]+resultSet.getString("PackID"), new Integer(count-1)); // new Integer count-1

                try {
//					System.out.println("Gettint templateIndex for " + resultSet.getString("PackID"));
                    pcdPackType[i][9] = prtTemplatesIndexes[getIndex(prtTemplatesIndexes,
                        resultSet.getString("PackID"), 0) - 1][2];
                } catch (final RuntimeException e) {
                    System.out.println("prtTemplatesIndexes failed for " + resultSet.getString("PackID"));
                    pcdPackType[i][9] = "0";
                }


                pcdPackType[i][10] = resultSet.getString("PackID"); //code
                pcdPackType[i][11] = resultSet.getString("Description"); //Description

                packtypeIndex.put(pcdPackType[i][1] + pcdPackType[i][10], new Integer(count - 1)); // new Integer count-1

                i++;
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void generatePcdRemarks() {
        final HashMap remarks = new HashMap();

        final String query = "select remark1, remark2, remark3 " +
            " from templates where " +
            "templates.templateName = 'XXX'";
        try {
            final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
                query, templateName, false);

            final ResultSet resultSet = model.getResultSet();
            resultSet.beforeFirst();
            while (resultSet.next()) {
                String remark = resultSet.getString("Remark1");
                if (!"".equals(remark)) {
                    remarks.put(remark, remark);
                }

                remark = resultSet.getString("Remark2");
                if (!"".equals(remark)) {
                    remarks.put(remark, remark);
                }

                remark = resultSet.getString("Remark3");
                if (!"".equals(remark)) {
                    remarks.put(remark, remark);
                }

            }

            model.close();

            pcdRemarks = new String[remarks.size() + 1][3];
            int count = 0;

            pcdRemarks[count][0] = "179";
            pcdRemarks[count][1] = "" + 1;
            pcdRemarks[count][2] = "None";
            count++;

            for (final Iterator i = remarks.keySet().iterator(); i.hasNext(); ) {
                pcdRemarks[count][0] = "179";
                pcdRemarks[count][1] = "" + (count + 1);
                pcdRemarks[count][2] = "" + i.next();
                count++;
            }

        } catch (final SQLException e) {
            e.printStackTrace();
        }

    }

    ;

    private String[][] generatePcdTemplate(final String fieldName, final int eventID,
                                           final String extra) {
        String[][] eventData = null;
        final String query = "select distinct " + fieldName +
            " from templates where " +
            "templates.templateName = 'XXX' and " + fieldName + " != ''";

        try {
            final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
                query, templateName, false);
            eventData = new String[model.getRowCount() + 1][3];
//			System.out.println(eventData.length);

            if (eventID == 175 || eventID == 176 || eventID == 178 || eventID == 168) {
                eventData[0][0] = "" + eventID;
                eventData[0][1] = "" + 1;
                eventData[0][2] = "zz";
            } else {
                eventData[0][0] = "" + eventID;
                eventData[0][1] = "" + 1;
                eventData[0][2] = "None";
            }

            for (int i = 1; i < eventData.length; i++) {
                eventData[i][0] = "" + eventID;
                eventData[i][1] = "" + (i + 1);
                eventData[i][2] = extra + model.getValueAt(i - 1, 0);
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        } finally {
            return eventData;
        }
    }

    public void generatePCD() {
        generatePcdMark();
        generatePcdPackType();
        generatePcdRemarks();

//		System.out.println("Pool: " + pcdPool);
        pcdPool = generatePcdTemplate("Pool", 161, "");
//		System.out.println("Pool: " + pcdPool);
//		System.out.println("Grade: " + pcdClass);
        pcdClass = generatePcdTemplate("Grade", 162, "");
//		pcdClass = generatePcdTemplate("Grade", 162, "Class");
//		System.out.println("Grade: " + pcdClass);
        pcdTargetMarket = generatePcdTemplate("TargetMarket", 163, "");
        pcdCountry = generatePcdTemplate("Country", 175, "");
        pcdOrganisation = generatePcdTemplate("Organization", 176, "");
        pcdPackhouseID = generatePcdTemplate("PackhouseID", 165, "");
        pcdGrowerID = generatePcdTemplate("GrowerID", 166, "");
        pcdDateCode = generatePcdTemplate("DateCode", 167, "");
        pcdInventory = generatePcdTemplate("Inventory", 168, "");
        pcdPickingRef = generatePcdTemplate("PickingReference", 169, "");
        pcdRunNumber = generatePcdTemplate("RunNumber", 174, "");
        pcdPUC = generatePcdTemplate("PUC", 177, "PUC:");
        pcdGtin = generatePcdTemplate("GTIN", 178, "");
    }


    public String[][] getPcdClass() {
        return pcdClass;
    }

    public String[][] getCountry() {
        return pcdCountry;
    }

    public String[][] getDateCode() {
        return pcdDateCode;
    }

    public String[][] getGrowerID() {
        return pcdGrowerID;
    }

    public String[][] getGtin() {
        return pcdGtin;
    }

    public String[][] getInventory() {
        return pcdInventory;
    }

    public String[][] getMark() {
        return pcdMark;
    }

    public String[][] getOrganisation() {
        return pcdOrganisation;
    }


    public String[][] getPackhouseID() {
        return pcdPackhouseID;
    }

    public String[][] getPackType() {
        return pcdPackType;
    }

    public String[][] getPickingRef() {
        return pcdPickingRef;
    }

    public String[][] getPool() {
        return pcdPool;
    }

    public String[][] getPUC() {
        return pcdPUC;
    }

    public String[][] getRemarks() {
        return pcdRemarks;
    }

    public String[][] getRunNumber() {
        return pcdRunNumber;
    }

    public String[][] getTargetMarket() {
        return pcdTargetMarket;
    }

}
