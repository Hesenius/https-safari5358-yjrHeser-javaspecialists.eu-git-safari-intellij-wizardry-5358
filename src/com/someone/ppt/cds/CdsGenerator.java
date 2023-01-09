package com.someone.ppt.cds;

import com.someone.db.models.*;
import com.someone.gui.*;
import com.someone.ppt.models.*;

import java.io.*;
import java.sql.*;
import java.util.*;

public class CdsGenerator {
    private Packhouse packhouse;
    private String templateName;
    private int lineNumber;
    private int tableCount; // used in: ppd, sid, cfg
    private String lineString;
    private String[][] ftdData;
    private String[][] fvdData; //ftdData
    private String[][] ctdData; //ftdData
    private String[][] ppdData; //ftdData, fvdData, pcd
    private String[][] sidData;
    private String[][] cfgData;
    private PrtGenerator printGenerator;
    private PcdGenerator pcdGenerator;
    private StringBuffer errors;

    public CdsGenerator(final Packhouse packhouse, final int line) {
        this.packhouse = packhouse;
        this.lineNumber = line;
        errors = new StringBuffer();

        try {
            lineString = packhouse.getLineString(line);
            templateName = packhouse.getTemplateName(lineNumber);
            tableCount = packhouse.countTables(lineNumber);
        } catch (final RuntimeException e) {
            System.out.println("Cds generator could not construct properly!!");
            e.printStackTrace();
        }
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

    private void generateFtd() {
        try {
            //        	String[] columns = new String[] { "Commodity", "CommodityDesc" };
            final String query = "select distinct Commodity, CommodityDesc " +
                " from gtin where CommodityDesc != '' XXX order by Commodity";

            //            ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
            //				"gtin", columns, null, true);
            final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
                query, "", false);

            ftdData = new String[model.getRowCount() + 1][4];

            final ResultSet resultSet = model.getResultSet();

            int i = 0;
            resultSet.beforeFirst();
            ftdData[i][0] = "" + 150;
            ftdData[i][1] = "" + (i + 1);
            ftdData[i][2] = "None";
            ftdData[i][3] = "None";
            i++;

            while (resultSet.next()) {
                ftdData[i][0] = "" + 150;
                ftdData[i][1] = "" + (i + 1);
                ftdData[i][2] = resultSet.getString("Commodity");
                ftdData[i][3] = resultSet.getString("CommodityDesc");
                i++;
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        if (ftdData.length == 1) {
            errors.append("Could not find Commodity data for Packhouse " +
                packhouse.getPackhouseName() + " and line " +
                lineString + "\n");
        }
    }

    private void generateFvd() {
        final String query = "select distinct templates.commodity, templates.variety," +
            "gtin.VarietyDesc from templates, gtin " +
            "where  templates.templateName = 'XXX' " +
            "and gtin.Variety =  templates.variety and templates.variety != ''" +
            "group by commodity, variety;";

        try {
            final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
                query, templateName, false);

            fvdData = new String[model.getRowCount() + 1][5];

            final ResultSet resultSet = model.getResultSet();
            resultSet.beforeFirst();
            resultSet.next();

            final String firstCommodity = resultSet.getString("Commodity");

            int commodityIndex = getIndex(ftdData, firstCommodity, 2);
            int varietyIndex = 1;

            int i = 0;

            resultSet.beforeFirst();

            boolean first = true;

            while (resultSet.next()) {
                final int nextCommodity = getIndex(ftdData,
                    resultSet.getString("Commodity"), 2);

                if (nextCommodity != commodityIndex) {
                    varietyIndex = 1;
                    commodityIndex = nextCommodity;
                }

                if (first) {
                    fvdData[i][0] = "" + 151;
                    fvdData[i][1] = "" + commodityIndex;
                    ;
                    fvdData[i][2] = "" + varietyIndex++;
                    fvdData[i][3] = "None";
                    fvdData[i][4] = "None";
                    i++;
                    first = false;
                }

                fvdData[i][0] = "" + 151;
                fvdData[i][1] = "" + commodityIndex;
                fvdData[i][2] = "" + varietyIndex++;
                fvdData[i][3] = resultSet.getString("Variety");
                fvdData[i][4] = resultSet.getString("VarietyDesc");
                i++;
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        if (fvdData.length == 1) {
            errors.append("Could not find Variety data for Packhouse " +
                packhouse.getPackhouseName() + " and line " +
                lineString + "\n");
        }
    }

    private void generateCtd() {
        final String query = "select commodity, SizeCount, PrintCount from templates " +
            "where templates.templateName = 'XXX' group by commodity, SizeCount, PrintCount";

        try {
            final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
                query, templateName, false);
            ctdData = new String[model.getRowCount() + 1][5];

            final ResultSet resultSet = model.getResultSet();
            resultSet.beforeFirst();
            resultSet.next();

            final String firstCommodity = resultSet.getString("Commodity");
            int commodityIndex = getIndex(ftdData, firstCommodity, 2);
            int printIndex = 1;

            int i = 0;

            boolean first = true;
            resultSet.beforeFirst();

            while (resultSet.next()) {
                final int nextCommodity = getIndex(ftdData,
                    resultSet.getString("Commodity"), 2);

                if (nextCommodity != commodityIndex) {
                    printIndex = 1;
                    commodityIndex = nextCommodity;
                }

                if (first) {
                    ctdData[i][0] = "" + 155;
                    ctdData[i][1] = "" + commodityIndex;
                    ctdData[i][2] = "" + printIndex++;
                    ctdData[i][3] = "None";
                    ctdData[i][4] = "None";
                    i++;
                    first = false;
                }

                ctdData[i][0] = "" + 155;
                ctdData[i][1] = "" + commodityIndex;
                ctdData[i][2] = "" + printIndex++;
                ctdData[i][3] = resultSet.getString("SizeCount");
                ctdData[i][4] = resultSet.getString("PrintCount");


                //				ctdData[i][4] = "Count:" + resultSet.getString("PrintCount");
                i++;
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void generateSid() {
        final String[][] data = packhouse.getLineData(lineNumber);
        sidData = new String[tableCount * 10][5];

        String dropName = data[0][2];
        String tableName = data[0][3];
        boolean changed = true;
        int index = 0;
        int packpointIndex = 1;
        int productIndex = 1;
        final int barCodeIndex = 2 +
            FruitspecTableModel.findIndex("BarCode");

        for (int i = 0; i < data.length; i++) {
            try {
                if (!dropName.equals(data[i][2])) {
                    changed = true;
                    dropName = data[i][2];
                    tableName = data[i][3];
                } else if (!tableName.equals(data[i][3])) {
                    changed = true;
                    tableName = data[i][3];
                } else {
                    changed = false;
                }

                if (changed) {
                    while (productIndex < 11) {
                        sidData[index][0] = "156";
                        sidData[index][1] = "" + packpointIndex;
                        sidData[index][2] = "" + productIndex++;
                        sidData[index][3] = "0";
                        sidData[index][4] = "0";
                        index++;
                    }

                    packpointIndex++;
                    productIndex = 1;
                }

                sidData[index][0] = "156";
                sidData[index][1] = "" + packpointIndex;
                sidData[index][2] = "" + productIndex++;
                sidData[index][3] = data[i][barCodeIndex]; // barcode
                sidData[index][4] = "0";

                index++;
            } catch (final RuntimeException e) {
            }
        }

        while (productIndex < 11) {
            //			System.out.print(productIndex + "\t");
            sidData[index][0] = "156";
            sidData[index][1] = "" + packpointIndex;
            sidData[index][2] = "" + productIndex++;
            sidData[index][3] = "0";
            sidData[index][4] = "0";
            index++;
        }
    }

    private void generateCfg() {
        cfgData = new String[5][3];

        cfgData[0][0] = "129";
        cfgData[0][1] = "" + tableCount;
        cfgData[0][2] = "Packing points/line";

        cfgData[1][0] = "130";
        cfgData[1][1] = ppdData[0][2];
        cfgData[1][2] = "Fruit On Sizer";

        cfgData[2][0] = "171";
        cfgData[2][1] = packhouse.getShift(lineNumber);
        cfgData[2][2] = "Shift";

        if ("".equals(cfgData[2][1])) {
            cfgData[2][1] = "D";

            final Calendar c = Calendar.getInstance();
            final int hour = c.get(Calendar.HOUR_OF_DAY);

            if ((hour > 18) || (hour < 8)) {
                cfgData[2][1] = "N";
            }
        }

        int printer = 0;
        cfgData[3][0] = "127";

        final String query = "select PrinterID " +
            " from PackhousePrinters where " +
            "PackhouseName = 'XXX' and LineName = '" + lineString +
            "'";

        try {
            final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
                query,
                packhouse.getPackhouseName(),
                false);

            final ResultSet resultSet = model.getResultSet();
            resultSet.beforeFirst();
            resultSet.next();

            final String printerID = resultSet.getString("PrinterID");

            if (printerID.equalsIgnoreCase("M10")) {
                printer = 1;
            } else if (printerID.equalsIgnoreCase("Mark")) {
                printer = 2;
            }

            model.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        cfgData[3][1] = "" + printer;

        switch (printer) {
            case 0:
                cfgData[3][2] = "None";

                break;

            case 1:
                cfgData[3][2] = "Marsh";

                break;

            case 2:
                cfgData[3][2] = "Markem";

                break;
        }

        cfgData[4][0] = "128";
        cfgData[4][1] = "" + (lineNumber + 1);
        cfgData[4][2] = "Line ID";
    }

    private void fillPpd(final String[] ppdData, final int packpointIndex, final int productIndex) {
        ppdData[0] = "154";
        ppdData[1] = "" + packpointIndex;
        ppdData[2] = "" + productIndex;
        ppdData[3] = "0";
        ppdData[4] = "0";
        ppdData[5] = "0";
        ppdData[6] = "0";
        ppdData[7] = "0";
        ppdData[8] = "0";
        ppdData[9] = "0";
        ppdData[10] = "0";
        ppdData[11] = "0";
        ppdData[12] = "0";
        ppdData[13] = "0";
        ppdData[14] = "0";
        ppdData[15] = "0";
        ppdData[16] = "0";
        ppdData[17] = "0";
        ppdData[18] = "0";
        ppdData[19] = "0";
        ppdData[20] = "0";
        ppdData[21] = "0";
        ppdData[22] = "0";
    }

    private void generatePpd() {
        final String[][] data = packhouse.getLineData(lineNumber);

        if (tableCount == 0) {
            errors.append("No tables defined for Packhouse " +
                packhouse.getPackhouseName() + " line " +
                lineString);

            return;
        }

        ppdData = new String[(tableCount * 7) + 1][23];

        String dropName = "";
        String tableName = "";
        boolean changed = true;
        int index = 0;
        int packpointIndex = 0;
        int productIndex = 1;
        final int commodityIndex = 2 +
            FruitspecTableModel.findIndex("Commodity");
        final int poolIndex = 2 +
            FruitspecTableModel.findIndex("Pool");
        final int gradeIndex = 2 +
            FruitspecTableModel.findIndex("Grade");
        final int barCodeIndex = 2 +
            FruitspecTableModel.findIndex("BarCode");
        final int packIndex = 2 +
            FruitspecTableModel.findIndex("Pack");
        final int countIndex = 2 +
            FruitspecTableModel.findIndex("SizeCount");
        final int printCountIndex = 2 +
            FruitspecTableModel.findIndex("PrintCount");
        final int targetMarketIndex = 2 +
            FruitspecTableModel.findIndex(
                "TargetMarket");
        final int markIndex = 2 +
            FruitspecTableModel.findIndex("Mark");
        ;

        final int countryIndex = 2 + FruitspecTableModel.findIndex("Country");
        ;

        final int remarkIndex = 2 + FruitspecTableModel.findIndex("Remark1");
        final int pucIndex = 2 + FruitspecTableModel.findIndex("PUC");
        final int runIndex = 2 +
            FruitspecTableModel.findIndex("RunNumber");
        final int pickingRefIndex = 2 +
            FruitspecTableModel.findIndex(
                "PickingReference");
        final int inventoryIndex = 2 +
            FruitspecTableModel.findIndex("Inventory");
        final int dateCodeIndex = 2 +
            FruitspecTableModel.findIndex("DateCode");
        final int growerIDIndex = 2 +
            FruitspecTableModel.findIndex("GrowerId");
        final int packhouseIndex = 2 +
            FruitspecTableModel.findIndex("PackhouseId");
        final int organizationIndex = 2 +
            FruitspecTableModel.findIndex("Organization");
        final int gtinIndex = 2 + FruitspecTableModel.findIndex("GTIN");
        final int varietyIndex = 2 + FruitspecTableModel.findIndex("Variety");

        final String[][] pcdMark = pcdGenerator.getMark();
        final String[][] pcdPool = pcdGenerator.getPool();
        final String[][] pcdClass = pcdGenerator.getPcdClass();
        final String[][] pcdTargetMarket = pcdGenerator.getTargetMarket();
        final String[][] pcdCountry = pcdGenerator.getCountry();
        final String[][] pcdOrganisation = pcdGenerator.getOrganisation();
        final String[][] pcdPackhouseID = pcdGenerator.getPackhouseID();
        final String[][] pcdGrowerID = pcdGenerator.getGrowerID();
        final String[][] pcdDateCode = pcdGenerator.getDateCode();
        final String[][] pcdInventory = pcdGenerator.getInventory();
        final String[][] pcdPickingRef = pcdGenerator.getPickingRef();
        final String[][] pcdRunNumber = pcdGenerator.getRunNumber();
        final String[][] pcdPUC = pcdGenerator.getPUC();
        final String[][] pcdGtin = pcdGenerator.getGtin();
        final String[][] pcdRemark = pcdGenerator.getRemarks();

        for (int i = 0; i < data.length; i++) {
            try {
                if (!dropName.equals(data[i][2])) {
                    changed = true;
                    dropName = data[i][2];
                    tableName = data[i][3];
                } else if (!tableName.equals(data[i][3])) {
                    changed = true;
                    tableName = data[i][3];
                } else {
                    changed = false;
                }

                if (changed) {
                    while ((i > 0) && (productIndex < 7)) {
                        fillPpd(ppdData[index], packpointIndex, productIndex);
                        productIndex++;
                        index++;
                    }

                    packpointIndex++;
                    productIndex = 1;

                    ppdData[index][0] = "153";
                    ppdData[index][1] = "" + packpointIndex;
                    ppdData[index][2] = "" +
                        getIndex(ftdData,
                            data[i][commodityIndex], 2);

                    if (ppdData[index][2].equals("1")) {
                        ppdData[index][2] = ppdData[0][2];
                    }

                    ppdData[index][3] = "" +
                        getIndex(ftdData, data[i][poolIndex], 2);
                    ppdData[index][4] = "" +
                        getIndex(ftdData, data[i][gradeIndex], 2);

                    ppdData[index][5] = "Pack" + packpointIndex;

                    index++;
                }

                if ((data[i][barCodeIndex] == null) ||
                    data[i][barCodeIndex].equals("0") ||
                    data[i][barCodeIndex].equals("")) {
                    fillPpd(ppdData[index], packpointIndex, productIndex);
                    productIndex++;
                    index++;

                    continue;
                }

                //                String fruitID = ppdData[index - 1][2];
                final String fruitID = ppdData[0][2];

                final String packTypeIndex = "" +
                    pcdGenerator.getPackIndex(
                        data[i][packIndex], fruitID);

                if (packTypeIndex.equals("0")) {
                    errors.append(data[i][barCodeIndex] +
                        " will not print\n");
                    errors.append("Warning, no packprintsetup defined for: " +
                        "Commodity " + data[i][commodityIndex] +
                        " and PackType " + data[i][packIndex] +
                        "\n");
                } else {
                    final String[][] packTypes = pcdGenerator.getPackType();
                    boolean canPrint = false;

                    for (int jj = 0; jj < packTypes.length; jj++) {
                        if (packTypes[jj][1].equals(fruitID) &&
                            packTypes[jj][10].equals(data[i][packIndex])) {
                            if (!pcdGenerator.getPackType()[jj][6].equals("0")) {
                                canPrint = true;

                                break;
                            }
                        }
                    }

                    //                    if (pcdGenerator.getPackType()[ppackIndex][6].equals("0")) {
                    if (!canPrint) {
                        errors.append(data[i][barCodeIndex] +
                            " will not print\n");
                        errors.append(
                            "packprintsetup defined as non print for: " +
                                "Commodity " + data[i][commodityIndex] +
                                " and PackType " + data[i][packIndex] + "\n");
                    }
                }

                if (!printGenerator.getDefinedPackTypes()
                    .contains(data[i][packIndex])) {
                    errors.append(data[i][barCodeIndex] +
                        " will not print\n");
                    errors.append("Warning, no Printertemplate defined for: " +
                        "PackType " + data[i][packIndex] +
                        " for printer " +
                        printGenerator.getPrinterName() +
                        " for packhouse " +
                        packhouse.getPackhouseName() +
                        " and line " + lineString + "\n");
                }


                // check for packhouse, linename, packid
                ppdData[index][0] = "154";
                ppdData[index][1] = "" + packpointIndex;
                ppdData[index][2] = "" + productIndex++;
                ppdData[index][3] = "" +
                    getIndex(fvdData, data[i][varietyIndex], 3);

                ppdData[index][4] = packTypeIndex;

                int countOffset = getIndex(ctdData, data[i][countIndex], 3) - 1;

                if (countOffset < 0) {
                    countOffset = 0;
                } else {
                    while ((countOffset < ctdData.length) &&
                        !data[i][printCountIndex].equalsIgnoreCase(
                            ctdData[countOffset][4])) {
                        //	                	System.out.println(data[i][printCountIndex] + " => "+ctdData[countOffset][4]);
                        countOffset++;
                    }
                }


                //                System.out.println("!! " + data[i][printCountIndex] + " => "+ctdData[countOffset][4]);
                countOffset++;

                ppdData[index][5] = "" + countOffset;


                //                                    getIndex(ctdData, data[i][countIndex], 3);
                ppdData[index][6] = "" +
                    getIndex(pcdPool, data[i][poolIndex], 2);

                ppdData[index][7] = "" +
                    getIndex(pcdClass,
                        /*"Class" + */ data[i][gradeIndex], 2);

                ppdData[index][8] = "" +
                    getIndex(pcdTargetMarket,
                        data[i][targetMarketIndex], 2);

                ppdData[index][9] = "" +
                    getIndex(pcdMark, data[i][markIndex], 2);

                ppdData[index][10] = "" +
                    getIndex(pcdCountry, data[i][countryIndex], 2);
                ppdData[index][11] = "" +
                    getIndex(pcdOrganisation,
                        data[i][organizationIndex], 2);
                ppdData[index][12] = "" +
                    getIndex(pcdPackhouseID,
                        data[i][packhouseIndex], 2);
                ppdData[index][13] = "" +
                    getIndex(pcdGrowerID,
                        data[i][growerIDIndex], 2);

                ppdData[index][14] = "" +
                    getIndex(pcdDateCode,
                        data[i][dateCodeIndex], 2);
                ppdData[index][15] = "" +
                    getIndex(pcdInventory,
                        data[i][inventoryIndex], 2);
                ppdData[index][16] = "" +
                    getIndex(pcdPickingRef,
                        data[i][pickingRefIndex], 2);
                ppdData[index][17] = "" +
                    getIndex(pcdRunNumber, data[i][runIndex], 2);
                ppdData[index][18] = "" +
                    getIndex(pcdPUC,
                        "PUC:" + data[i][pucIndex], 2);
                ppdData[index][19] = "" +
                    getIndex(pcdGtin, data[i][gtinIndex], 2);

                ppdData[index][20] = "" +
                    getIndex(pcdRemark, data[i][remarkIndex], 2);

                ppdData[index][21] = "" +
                    getIndex(pcdRemark,
                        data[i][remarkIndex + 1], 2);

                ppdData[index][22] = "" +
                    getIndex(pcdRemark,
                        data[i][remarkIndex + 2], 2);

                if ("0".equals(ppdData[index][4])) {
                    ppdData[index][4] = "1";
                }

                index++;
            } catch (final RuntimeException e) {
                e.printStackTrace();
            }
        }

        try {
            while (productIndex < 7) {
                fillPpd(ppdData[index], packpointIndex, productIndex);
                productIndex++;
                index++;
            }
        } catch (final RuntimeException e) {
            //            System.out.println(ppdData.length + " < " + index);
            e.printStackTrace();
        }
    }

    /***************************/
    public void testSetup() {
        generateFtd();
        generateFvd();
        printGenerator = new PrtGenerator(packhouse, lineNumber, errors);
        printGenerator.generatePrt();
        pcdGenerator = new PcdGenerator(packhouse, lineNumber, ftdData,
            printGenerator.getPrtTemplatesIndexes());
        pcdGenerator.generatePCD();
        generateCtd();
        generatePpd();
        generateSid();
        generateCfg();

        if (!errors.toString().equals("")) {
            final InfoFrame fr = new InfoFrame("Printing Warnings for line " +
                lineString, errors);
            fr.setVisible(true);
        } else {
            errors.append("Setup OK");

            final InfoFrame fr = new InfoFrame("Printing Warnings for line " +
                lineString, errors);
            fr.setVisible(true);
        }
    }

    public void generateFiles(final String outputDirectory) {
        generateFtd();
        generateFvd();
        printGenerator = new PrtGenerator(packhouse, lineNumber, errors);
        printGenerator.generatePrt();
        pcdGenerator = new PcdGenerator(packhouse, lineNumber, ftdData,
            printGenerator.getPrtTemplatesIndexes());
        pcdGenerator.generatePCD();
        generateCtd();
        generatePpd();
        generateSid();
        generateCfg();

        //		System.out.println("ftd");
        String fileName = outputDirectory + "FTD" + lineString + ".DAT";
        writeDataToFile(outputDirectory, fileName, ftdData);
        pcdGenerator.writePcdToFile(new File(outputDirectory),
            "PCD" + lineString + ".DAT");


        //		System.out.println("ppt");
        fileName = outputDirectory + "PRT" + lineString + ".DAT";
        writeDataToFile(outputDirectory, fileName, printGenerator.getPrtData());


        //		System.out.println("fvd");
        fileName = outputDirectory + "FVD" + lineString + ".DAT";
        writeDataToFile(outputDirectory, fileName, fvdData);


        //		System.out.println("ctd");
        fileName = outputDirectory + "CTD" + lineString + ".DAT";
        writeDataToFile(outputDirectory, fileName, ctdData);


        //		System.out.println("sid");
        fileName = outputDirectory + "SID" + lineString + ".DAT";
        writeDataToFile(outputDirectory, fileName, sidData);


        //		System.out.println("ppd");
        fileName = outputDirectory + "PPD" + lineString + ".DAT";
        writeDataToFile(outputDirectory, fileName, ppdData);


        //		System.out.println("ppt");
        fileName = outputDirectory + "CMS" + lineString + ".CFG";
        writeDataToFile(outputDirectory, fileName, cfgData);


        //		System.out.println("pcd");
        fileName = outputDirectory + "PCD" + lineString + ".DAT";

        System.out.println(errors.toString());
    }

    protected void printData(final String[][] data) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                System.out.print(data[i][j] + "\t");
            }

            System.out.println();
        }
    }

    private void writeDataToFile(final String directory, final String fileName,
                                 final String[][] data) {
        final File outputDirectory = new File(directory);

        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        if (data.length == 0) {
            return;
        }

        try {
            final File outputFile = new File(fileName);
            final BufferedWriter writer = new BufferedWriter(
                new FileWriter(outputFile));
            final int columns = data[0].length;

            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < columns; j++) {
                    if (data[i][j] == null) {
                        continue;
                    }

                    try {
                        if (data[i][j].equals("-1")) {
                            data[i][j] = "0";
                        }

                        writer.write(data[i][j]);
                    } catch (final RuntimeException e) {
                        writer.write("0");
                    }

                    if (((j + 1) < columns) && (data[i][j + 1] != null)) {
                        writer.write("     ");
                    }
                }

                writer.write("\n");
            }

            writer.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {
        final Packhouse pack = new Packhouse("2");
        pack.loadLayout();
        pack.loadTemplates();

        final CdsGenerator xgen = new CdsGenerator(pack, 3);
        xgen.generateFiles("D:/temp/cmsserver/");

        //        System.out.println("finito");
    }
}
