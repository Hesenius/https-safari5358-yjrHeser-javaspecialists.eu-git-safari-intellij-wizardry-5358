package com.someone.ppt.importer;

import com.someone.db.*;
import com.someone.db.models.*;
import org.apache.tomcat.util.*;

import java.sql.*;
import java.text.*;
import java.util.Date;

public class CdsParser {
    private String fileName;
    private String packhouseName;
    private String lineName;

    public CdsParser(final String packhouseName, final String lineName, final String fileName) {
        this.packhouseName = packhouseName;
        this.lineName = lineName;
        this.fileName = fileName;
        System.out.println("Parse : " + packhouseName + " " + lineName + " " + fileName);
    }

    public void parse() {
        final CdsDataSource source = new CdsDataSource(fileName);
        String[][] data = null;
        System.out.println("Computing data " + new Date());
        try {
            data = source.getData();
        } catch (final RuntimeException e) {
        }
        System.out.println("Data length: " + data.length);

        final long now = new java.util.Date().getTime();

        final FastDateFormat formatter = new FastDateFormat(
            new SimpleDateFormat("dd-MM-yyyy kk:mm:ss"));
        final FastDateFormat newFormatter = new FastDateFormat(
            new SimpleDateFormat("yyyy-MM-dd kk:mm:ss"));

        final String[] columns = new String[]{
            "PackhouseName", "LineName", "InsertTime"
        };
        final String[] keys = new String[]{packhouseName, lineName, "" + now};

        System.out.println("Get table models for data insertion " + new Date());
        try {
            final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
                "cartons", columns, keys);

            System.out.println("model1 ready");

            final ScrollableTableModel model2 = TableModelFactory.getResultSetTableModel(
                "cartonsBackup", columns, keys);
            System.out.println("model2 ready");

            final ScrollableTableModel model3 = TableModelFactory.getResultSetTableModel(
                "verification", columns, keys);
            System.out.println("model3 ready " + new Date());

            final ResultSet resultSet = model.getResultSet();
            final ResultSet resultSet2 = model2.getResultSet();
            final ResultSet resultSet3 = model3.getResultSet();

            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals("06")) {

                    resultSet3.moveToInsertRow();

                    resultSet3.updateString("PackhouseName", packhouseName);
                    resultSet3.updateString("LineName", lineName);
                    resultSet3.updateLong("InsertTime", now);
                    resultSet3.updateLong("InsertIndex", i);

                    try {
                        final java.util.Date date = (java.util.Date) formatter.parse(
                            data[i][1] + " " + data[i][2]);

                        resultSet3.updateTimestamp("LMDate",
                            new java.sql.Timestamp(
                                date.getTime()));
                    } catch (final ParseException e) {
                        System.out.println("i: " + i);
                        e.printStackTrace();
                    }


                    resultSet3.updateString("Shift", data[i][4].trim()); //SHIFT
                    resultSet3.updateString("RunNumber", data[i][14].trim()); //RUNNUMBER
                    resultSet3.updateString("DateCode", data[i][15].trim()); //DATECODE

                    resultSet3.updateString("PackhouseID", data[i][12].trim()); //pACKHOUSEID
                    resultSet3.updateString("GrowerID", data[i][13].trim()); // GROWERID

                    try {
                        if (data[i][26].length() > 4) {
                            resultSet3.updateString("PUC", data[i][26].trim().substring(4)); //PUC
                        }
                    } catch (final RuntimeException e) {
                    }
                    resultSet3.updateString("PickingReference", data[i][16].trim()); // PICKINGREFERENCE

                    resultSet3.updateString("Country", data[i][10].trim()); // COUNTRY

                    resultSet3.updateString("BarCode", data[i][5].trim()); // BARCODE
                    resultSet3.updateString("Weight", data[i][6].trim()); // WEIGHT
                    resultSet3.updateString("TargetWeight", data[i][8].trim()); // TARGETWEIGHT
                    resultSet3.updateString("InternalScaleSetup", data[i][7].trim()); // INTERNALSCALESETUP
                    resultSet3.updateString("PackPoint", data[i][9].trim()); // PACKPOINT
                    resultSet3.updateString("Commodity", data[i][17].trim()); // COMMODITY
                    resultSet3.updateString("Variety", data[i][18].trim()); // VARIETY
                    resultSet3.updateString("SizeCount", data[i][19].trim()); // SIZECOUNT
                    resultSet3.updateString("Pack", data[i][20].trim()); // PACK
                    resultSet3.updateString("SalesMark", data[i][21].trim()); // sALESmARK
                    resultSet3.updateString("Pool", data[i][22].trim()); // POOL
                    resultSet3.updateString("Grade", data[i][23].trim()); // GRADE
                    resultSet3.updateString("TargetMarket", data[i][24].trim()); // TARGETMARKET
                    resultSet3.updateString("Organisation", data[i][11].trim()); // ORGANIZTION
                    resultSet3.updateString("Inventory", data[i][25].trim()); // INVENTORY
                    resultSet3.updateString("GTIN", data[i][27].trim()); // GTIN
                    resultSet3.updateString("Remark1", data[i][28].trim()); // REMARK1
                    resultSet3.updateString("Remark2", data[i][29].trim()); // REMARK2
                    resultSet3.updateString("Remark3", data[i][30].trim()); // REMARK3
                    resultSet3.updateString("Mark", data[i][21].trim()); // MARK
                    resultSet3.updateString("PackDescription", ""); // PACKDESCRIPTION

                    resultSet3.insertRow();

                    continue;
                }


//            	System.out.println(i);
//            	System.out.println(data[i]);
//            	System.out.println(data.length);
                resultSet.moveToInsertRow();
                resultSet2.moveToInsertRow();

//                resultSet.updateString(1, packhouseName);
//                resultSet.updateString(2, lineName);
//                resultSet.updateLong(3, now);
//                resultSet.updateLong(4, i);
                resultSet.updateString("PackhouseName", packhouseName);
                resultSet.updateString("LineName", lineName);
                resultSet.updateLong("InsertTime", now);
                resultSet.updateLong("InsertIndex", i);

                resultSet2.updateString("PackhouseName", packhouseName);
                resultSet2.updateString("LineName", lineName);
                resultSet2.updateLong("InsertTime", now);
                resultSet2.updateLong("InsertIndex", i);


//            	resultSet.updateString(5, data[i][12].trim());
//            	resultSet.updateString(6, data[i][3].trim());

                try {
                    final java.util.Date date = (java.util.Date) formatter.parse(
                        data[i][1] + " " + data[i][2]);

                    resultSet.updateTimestamp("LMDate",
                        new java.sql.Timestamp(
                            date.getTime()));

                    resultSet2.updateTimestamp("LMDate",
                        new java.sql.Timestamp(
                            date.getTime()));

//                    resultSet.updateTimestamp(7,
//                                              new java.sql.Timestamp(
//                                                      date.getTime()));
                } catch (final ParseException e) {
                    System.out.println("i: " + i);
                    e.printStackTrace();
                }

//                resultSet.updateString(8, data[i][4].trim()); //SHIFT
//                resultSet.updateString(9, data[i][14].trim()); //RUNNUMBER
//                resultSet.updateString(10, data[i][15].trim()); //DATECODE

                resultSet.updateString("Shift", data[i][4].trim()); //SHIFT
                resultSet.updateString("RunNumber", data[i][14].trim()); //RUNNUMBER
                resultSet.updateString("DateCode", data[i][15].trim()); //DATECODE

                resultSet.updateString("PackhouseID", data[i][12].trim()); //pACKHOUSEID
                resultSet.updateString("GrowerID", data[i][13].trim()); // GROWERID

                resultSet2.updateString("Shift", data[i][4].trim()); //SHIFT
                resultSet2.updateString("RunNumber", data[i][14].trim()); //RUNNUMBER
                resultSet2.updateString("DateCode", data[i][15].trim()); //DATECODE

                resultSet2.updateString("PackhouseID", data[i][12].trim()); //pACKHOUSEID
                resultSet2.updateString("GrowerID", data[i][13].trim()); // GROWERID


                try {
                    if (data[i][26].length() > 4) {
                        resultSet.updateString("PUC", data[i][26].trim().substring(4)); //PUC
                        resultSet2.updateString("PUC", data[i][26].trim().substring(4)); //PUC
                    }
                } catch (final RuntimeException e) {
                }
                resultSet.updateString("PickingReference", data[i][16].trim()); // PICKINGREFERENCE

                resultSet.updateString("Country", data[i][10].trim()); // COUNTRY

                resultSet2.updateString("PickingReference", data[i][16].trim()); // PICKINGREFERENCE

                resultSet2.updateString("Country", data[i][10].trim()); // COUNTRY


//				try {
//					if (data[i][5].length() > 1)
//					resultSet.updateString("BarCode", ""+Integer.parseInt(data[i][5].trim())); // BARCODE
//				} catch (RuntimeException e) {
//				}

                resultSet.updateString("BarCode", data[i][5].trim()); // BARCODE
                resultSet.updateString("Weight", data[i][6].trim()); // WEIGHT
                resultSet.updateString("TargetWeight", data[i][8].trim()); // TARGETWEIGHT
                resultSet.updateString("InternalScaleSetup", data[i][7].trim()); // INTERNALSCALESETUP
                resultSet.updateString("PackPoint", data[i][9].trim()); // PACKPOINT
                resultSet.updateString("Commodity", data[i][17].trim()); // COMMODITY
                resultSet.updateString("Variety", data[i][18].trim()); // VARIETY
                resultSet.updateString("SizeCount", data[i][19].trim()); // SIZECOUNT
                resultSet.updateString("Pack", data[i][20].trim()); // PACK
                resultSet.updateString("SalesMark", data[i][21].trim()); // sALESmARK
                resultSet.updateString("Pool", data[i][22].trim()); // POOL
                resultSet.updateString("Grade", data[i][23].trim()); // GRADE
                resultSet.updateString("TargetMarket", data[i][24].trim()); // TARGETMARKET
                resultSet.updateString("Organisation", data[i][11].trim()); // ORGANIZTION
                resultSet.updateString("Inventory", data[i][25].trim()); // INVENTORY
                resultSet.updateString("GTIN", data[i][27].trim()); // GTIN
                resultSet.updateString("Remark1", data[i][28].trim()); // REMARK1
                resultSet.updateString("Remark2", data[i][29].trim()); // REMARK2
                resultSet.updateString("Remark3", data[i][30].trim()); // REMARK3
                resultSet.updateString("Mark", data[i][21].trim()); // MARK
                resultSet.updateString("PackDescription", ""); // PACKDESCRIPTION

                resultSet2.updateString("BarCode", data[i][5].trim()); // BARCODE
                resultSet2.updateString("Weight", data[i][6].trim()); // WEIGHT
                resultSet2.updateString("TargetWeight", data[i][8].trim()); // TARGETWEIGHT
                resultSet2.updateString("InternalScaleSetup", data[i][7].trim()); // INTERNALSCALESETUP
                resultSet2.updateString("PackPoint", data[i][9].trim()); // PACKPOINT
                resultSet2.updateString("Commodity", data[i][17].trim()); // COMMODITY
                resultSet2.updateString("Variety", data[i][18].trim()); // VARIETY
                resultSet2.updateString("SizeCount", data[i][19].trim()); // SIZECOUNT
                resultSet2.updateString("Pack", data[i][20].trim()); // PACK
                resultSet2.updateString("SalesMark", data[i][21].trim()); // sALESmARK
                resultSet2.updateString("Pool", data[i][22].trim()); // POOL
                resultSet2.updateString("Grade", data[i][23].trim()); // GRADE
                resultSet2.updateString("TargetMarket", data[i][24].trim()); // TARGETMARKET
                resultSet2.updateString("Organisation", data[i][11].trim()); // ORGANIZTION
                resultSet2.updateString("Inventory", data[i][25].trim()); // INVENTORY
                resultSet2.updateString("GTIN", data[i][27].trim()); // GTIN
                resultSet2.updateString("Remark1", data[i][28].trim()); // REMARK1
                resultSet2.updateString("Remark2", data[i][29].trim()); // REMARK2
                resultSet2.updateString("Remark3", data[i][30].trim()); // REMARK3
                resultSet2.updateString("Mark", data[i][21].trim()); // MARK
                resultSet2.updateString("PackDescription", ""); // PACKDESCRIPTION

                resultSet.insertRow();
                resultSet2.insertRow();
            }

            model.close();
            model2.close();
            model3.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Data inserted " + new Date());
    }

//    private void printRow(String[] row) {
//        System.out.print(packhouseName + "\t");
//        System.out.print(lineName + "\t");
//
//        System.out.print(row[3] + "\t");
//        System.out.print(row[4] + "\t");
//        System.out.print(row[5] + "\t");
//        System.out.print(row[6] + "\t");
//        System.out.print(row[7] + "\t");
//        System.out.print(row[8] + "\t");
//        System.out.print(row[9] + "\t");
//        System.out.print(row[11] + "\t");
//        System.out.print(row[12] + "\t");
//        System.out.print(row[13] + "\t");
//        System.out.print(row[14] + "\t");
//        System.out.print(row[15] + "\t");
//        System.out.print(row[16] + "\t");
//        System.out.print(row[17] + "\t");
//        System.out.print(row[18] + "\t");
//        System.out.print(row[19] + "\t");
//        System.out.print(row[20] + "\t");
//        System.out.print(row[21] + "\t");
//        System.out.print(row[22] + "\t");
//        System.out.print(row[23] + "\n");
//    }

    public static void main(final String[] args) {
        ConnectionFactory.resetConnection(args[0]);
        if (args.length < 4) {
            System.out.println("usage: CdsParser jdbcurl Packhouse Line File");
        }
        final CdsParser parser = new CdsParser(args[1], args[2], args[3]);
        parser.parse();
        System.out.println("finished");
    }
}
