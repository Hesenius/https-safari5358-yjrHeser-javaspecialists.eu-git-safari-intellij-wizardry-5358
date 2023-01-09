package com.someone.ppt.reports;

import com.someone.db.models.*;
import com.someone.gui.*;
import dori.jasper.engine.*;
import org.apache.tomcat.util.*;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.Date;
import java.util.*;

public class WeightReport {
    private static final int MAX_TYPES = 14;

    private int reportCount;
    private FastDateFormat formatter;
    private FastDateFormat shortFormat;
    private Date startDate;
    private Date endDate;
    private int cartons;
    private String packhouseName;
    private String whereClause;
    private String dateInfo;
    private String[][] types; //PackType, Commodity, Count
    private String[][] data;

    public WeightReport(final String packhouseName) {
        this.packhouseName = packhouseName;
        formatter = new FastDateFormat(
            new SimpleDateFormat("yyyy-MM-dd kk:mm:ss"));
        shortFormat = new FastDateFormat(
            new SimpleDateFormat("yyyy-MM-dd kk:mm"));
    }

    public WeightReport(final String whereClause, final String titleString, final String dateInfo,
                        final int cartons) {
        this(titleString);
        this.whereClause = whereClause;
        this.dateInfo = dateInfo;
        this.cartons = cartons;
    }

    private void createDetailedData(final boolean print) {
        getDetailedTypes();

        int column = 0;
        int type = 0;

        while (type < types.length) {
            if (column == MAX_TYPES) {
                column = 0;
            }

            createDetailedColumn(type, column);

            column++;
            type++;
        }

        if (print) {
            printPaper();
        } else {
            printScreen();
        }
    }

//	private void resetData() {
//		for (int i = 2; i < data.length; i++) {
//			for (int j = 2; j < data[i].length; j++) {
//				data[i][j] = "0";
//			}
//		}
//
//	}

    private void createData(final boolean print) {
        getTypes();

        int column = 0;
        int type = 0;

        while (type < types.length) {
            if (column == MAX_TYPES) {
                column = 0;
            }

            createColumn(type, column);

            column++;
            type++;
        }

        if (print) {
            printPaper();
        } else {
            printScreen();
        }
    }

    public void show() {
        if (whereClause == null) {
            showScreenDialog();
        } else {
            createDetailedData(false);
        }
    }

    public void print() {
        if (whereClause == null) {
            showPrintDialog();
        } else {
            createDetailedData(true);
        }
    }

    private void printScreen() {
        final Map parameters = new HashMap();

        if (!packhouseName.startsWith("PH")) {
            packhouseName = "Packhouse " + packhouseName;
        }

        parameters.put("PackhouseName", packhouseName);
        parameters.put("Cartons", "" + cartons);
        parameters.put("DateInfo", dateInfo);

        try {

//			for (int i = 0; i < data.length; i++) {
//				for (int j = 0; j < data[i].length; j++) {
//					System.out.print(data[i][j] + "\t");
//				}
//				System.out.println();
//			}

            final WeightReportDataSource source = new WeightReportDataSource(data);
            JasperFillManager.fillReportToFile(
                "config/colourmasssummary.jasper", parameters, source);
            JasperExportManager.exportReportToHtmlFile(
                "config/colourmasssummary.jrprint");

            try {
                Runtime.getRuntime()
                    .exec("explorer .\\config\\colourmasssummary.html");
            } catch (final IOException e) {
                e.printStackTrace();
            }
        } catch (final JRException e) {
            e.printStackTrace();
        }
    }

    private void printPaper() {
        final Map parameters = new HashMap();

        if (!packhouseName.startsWith("PH")) {
            packhouseName = "Packhouse " + packhouseName;
        }

        parameters.put("PackhouseName", packhouseName);
        parameters.put("Cartons", "" + cartons);
        parameters.put("DateInfo", dateInfo);

        try {
//        	for (int i = 0; i < data.length; i++) {
//        		for (int j = 0; i < data[i].length; j++) {
//        			System.out.print(data[i][j] + "\t");
//        		}
//        		System.out.println();
//        	}

            final WeightReportDataSource source = new WeightReportDataSource(data);
            JasperFillManager.fillReportToFile(
                "config/colourmasssummary.jasper", parameters, source);
            JasperPrintManager.printReport("config/colourmasssummary.jrprint",
                true);


        } catch (final JRException e) {
            e.printStackTrace();
        }
    }

    private void showScreenDialog() {
        final Date now = new Date();

        final String[] columnNames = {
            "Packhouse", "Cartons", "Start Date", "End Date"
        };
        final DataInputFrame frame = new DataInputFrame("Mass Summary",
            columnNames,
            false);
        final String[] data = {
            packhouseName, "30", shortFormat.format(now),
            shortFormat.format(now)
        };
        frame.setData(data);

        final Runnable modifier = () -> {
            if (frame.isModified()) {
                try {
                    final String[] inputs = frame.getNewData();
                    packhouseName = inputs[0];
                    cartons = Integer.parseInt(inputs[1]);
                    startDate = shortFormat.parse(inputs[2]);
                    endDate = shortFormat.parse(inputs[3]);
                    dateInfo = inputs[2] + " to " + inputs[3] +
                        " (period)";

                    createData(true);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else {
                // do nothing
            }
        };

        frame.setCloseAction(modifier);
        frame.setVisible(true);
    }

    private void showPrintDialog() {
        final Date now = new Date();

        final String[] columnNames = {
            "Packhouse", "Cartons", "Start Date", "End Date"
        };
        final DataInputFrame frame = new DataInputFrame("Mass Summary",
            columnNames,
            false);
        final String[] data = {
            packhouseName, "30", shortFormat.format(now),
            shortFormat.format(now)
        };
        frame.setData(data);

        final Runnable modifier = () -> {
            if (frame.isModified()) {
                try {
                    final String[] inputs = frame.getNewData();
                    packhouseName = inputs[0];
                    cartons = Integer.parseInt(inputs[1]);
                    startDate = shortFormat.parse(inputs[2]);
                    endDate = shortFormat.parse(inputs[3]);
                    dateInfo = inputs[2] + " to " + inputs[3] +
                        " (period)";

                    createData(false);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else {
                // do nothing
            }
        };

        frame.setCloseAction(modifier);
        frame.setVisible(true);
    }

//    private void database() {
//        try {
//            String[]             columns = new String[] { "ReportName" };
//            String[]             keys = new String[] { "TestReport" };
//
//            ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
//                                                 "masssummary", columns, keys);
//            ResultSet            resultSet = model.getResultSet();
//
//            for (int i = 0; i < data.length; i++) {
//                resultSet.moveToInsertRow();
//                resultSet.updateString(1, "TestReport");
//                resultSet.updateInt(2, i);
//
//                for (int j = 0; j < data[i].length; j++) {
//                    if (data[i][j] == null) {
//                        resultSet.updateString(j + 3, "");
//                    } else {
//                        resultSet.updateString(j + 3, data[i][j]);
//                    }
//                }
//
//                resultSet.insertRow();
//            }
//
//            model.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Find the PackTypes, Commodity groups in the time frame.
     */
    private void getTypes() {
        ScrollableTableModel model = null;

        try {
            final String query =
                "select pack, commodity, sizecount, count(XXX) from cartons " +
                    " where PackhouseName ='" + packhouseName + "' " +

                    //			     " and LineName = '" +
                    //			    lineName + "' " +
                    " and LMDate between '" + formatter.format(startDate) +
                    "' and '" + formatter.format(endDate) + "' " +
                    " group by pack, commodity, sizecount";

            model = TableModelFactory.getResultSetTableModel(query, "*", false);

            reportCount = model.getRowCount() / MAX_TYPES;
            reportCount++;


            types = new String[model.getRowCount()][];
            data = new String[(cartons + 6) * reportCount][34];
//            resetData();

            for (int k = 0; k < reportCount; k++) {
                final int index = (cartons + 6) * k;
                final int endIndex = (cartons + 6) * (k + 1);

                data[index][0] = "";
                data[index][1] = "";
                data[index][2] = "Std Mass";
                data[index + 1][0] = "";
                data[index + 1][1] = "Carton";
                data[index + 1][2] = "Missed";

                data[endIndex - 4][0] = "Average";
                data[endIndex - 3][0] = "Total";
                data[endIndex - 2][0] = "% below";
                data[endIndex - 1][0] = "% above";
                data[endIndex - 1][1] = null;
                data[endIndex - 2][1] = null;
                data[endIndex - 3][1] = null;
                data[endIndex - 4][1] = null;

                for (int i = index; i < index + cartons; i++) {
                    data[i + 2][1] = "" + (i + 1 - index);
                }
            } // for k

            for (int i = 0; i < types.length; i++) {
                types[i] = model.getRow(i);
//				System.out.print(i + " : ");
//                for (int j = 0; j  < types[i].length; j++) {
//                	System.out.print(types[i][j] + "\t");
//                	System.out.println();
//                }
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < types[i].length; j++) {
                types[i][j] = types[i][j].trim();
            }
        }
    }

    private void getDetailedTypes() {
        ScrollableTableModel model = null;

        try {
            final String query =
                "select pack, commodity, sizecount, count(XXX) from cartons " +
                    whereClause + " group by pack, commodity, sizecount";


//			System.out.println("whereclause: \n" + whereClause);
            model = TableModelFactory.getResultSetTableModel(query, "*", false);

            reportCount = model.getRowCount() / MAX_TYPES;
            reportCount++;

            types = new String[model.getRowCount()][];
            data = new String[(cartons + 6) * reportCount][34];
//            resetData();

            for (int k = 0; k < reportCount; k++) {
//				System.out.println("computing " + k);
                final int index = (cartons + 6) * k;
                final int endIndex = (cartons + 6) * (k + 1);

                data[index][0] = "";
                data[index][1] = "";
                data[index][2] = "Std Mass";
                data[index + 1][0] = "";
                data[index + 1][1] = "Carton";
                data[index + 1][2] = "Missed";

                data[endIndex - 4][0] = "Average";
                data[endIndex - 3][0] = "Total";
                data[endIndex - 2][0] = "% below";
                data[endIndex - 1][0] = "% above";
                data[endIndex - 1][1] = null;
                data[endIndex - 2][1] = null;
                data[endIndex - 3][1] = null;
                data[endIndex - 4][1] = null;

                for (int i = index; i < index + cartons; i++) {
                    data[i + 2][1] = "" + (i + 1 - index);
                }
            } // for k

            for (int i = 0; i < types.length; i++) {
                types[i] = model.getRow(i);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < types[i].length; j++) {
                types[i][j] = types[i][j].trim();
            }
        }
    }

    private void createColumn(final int typeIndex, final int offset) {
        int standardWeight = 0;
        double upperLimit = 0;
        double lowerLimit = 0;
        final int dataIndex = (offset + 1) * 2;
        final DecimalFormat decFormatter = new DecimalFormat("#.00");

        ScrollableTableModel model;
        final int start = (typeIndex / MAX_TYPES) * (cartons + 6);
        final int end = ((typeIndex / MAX_TYPES) + 1) * (cartons + 6);

        if (!types[typeIndex][0].equals("")) {
            final String[] columns = new String[]{"PackID", "CommodityID"};
            final String[] keys = new String[]{
                types[typeIndex][0], types[typeIndex][1]
            };


            try {
                model = TableModelFactory.getResultSetTableModel(
                    "packprintsetup", columns, keys);

                standardWeight = Integer.parseInt(
                    (String) model.getValueAt(0, 2));
                upperLimit = Double.parseDouble(
                    (String) model.getValueAt(0, 3));
                lowerLimit = Double.parseDouble((String) model.getValueAt(0, 4));

                model.close();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }

        try {
//            String query = "select XXX from cartons where " +
//                           " PackhouseName ='" + packhouseName + "'" +
//
//            //			    "' and LineName = '" +
//            //			    lineName + "' " +
//            " and pack =  '" + types[typeIndex][0] + "' and " +
//                           " SizeCount = '" + types[typeIndex][2] + "' and " +
//                           " LMDate between '" + formatter.format(startDate) +
//                           "' and '" + formatter.format(endDate) + "' " +
//                           " order by LMDate desc limit " + cartons;


            final String query = "select XXX from cartons  " + whereClause +
                " and pack =  '" + types[typeIndex][0] + "' and " +
                " SizeCount = '" + types[typeIndex][2] + "' " +
                " order by LMDate desc limit " + cartons;


            model = TableModelFactory.getResultSetTableModel(query, "weight", false);

            final ResultSet results = model.getResultSet();
            results.beforeFirst();

            int count = 0;
            long averageCount = 0;
            int above = 0;
            int below = 0;
            final int number;
            int value;
            data[start][dataIndex + 1] = "0";
            data[start + 1][dataIndex + 1] = "0";

            while (results.next()) {
                value = results.getInt("weight");
                averageCount += value;

                int difference = value - standardWeight;
                data[start + count + 2][dataIndex + 1] = "0";

                if (difference > 0) { // test above

                    final double d = difference / (double) (standardWeight / 100);

                    if (d > upperLimit) {
                        above++;
                        data[start + count + 2][dataIndex + 1] = "1";
                    }
                } else { //test below
                    difference = -difference;

                    final double d = difference / (double) (standardWeight / 100);

                    if (d > lowerLimit) {
                        below++;
                        data[start + count + 2][dataIndex + 1] = "-1";
                    }
                }

                data[start + count + 2][dataIndex] = "" + value;
                count++;
            }

            number = count;

            while (count < cartons) {
//                data[start+count + 2][dataIndex+1] = "0";
                data[start + count + 2][dataIndex] = "0";
                data[start + count + 2][dataIndex + 1] = "0";
                count++;
            }

            data[end - 4][dataIndex + 1] = "0";
            data[end - 3][dataIndex + 1] = "0";
            data[end - 2][dataIndex + 1] = "0";
            data[end - 1][dataIndex + 1] = "0";

            if (types[typeIndex][0] != null || !types[typeIndex][0].equals("0") || !types[typeIndex][0].equals("") || !types[typeIndex][2].equals("")) {
                data[start + 0][dataIndex] = "" + standardWeight;
                data[start + 1][dataIndex] = types[typeIndex][0] + " " +
                    types[typeIndex][2];
            } else {
                data[start + 1][dataIndex] = "Missed";
            }


            //calculate above, below and extra column
            data[end - 4][dataIndex] = "" +
                decFormatter.format(
                    (averageCount / number));
            data[end - 3][dataIndex] = "" + number;

            if (below != 0) {
                data[end - 2][dataIndex] = decFormatter.format(
                    ((double) below / (double) number) * 100);
            } else {
                data[end - 2][dataIndex] = decFormatter.format(0);
            }

            if (above != 0) {
                data[end - 1][dataIndex] = decFormatter.format(
                    ((double) above / (double) number) * 100);
            } else {
                data[end - 1][dataIndex] = decFormatter.format(0);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDetailedColumn(final int typeIndex, final int offset) {
        int standardWeight = 0;
        double upperLimit = 0;
        double lowerLimit = 0;
        final int dataIndex = (offset + 1) * 2;
        final DecimalFormat decFormatter = new DecimalFormat("#.00");

        ScrollableTableModel model;
        final int start = (typeIndex / MAX_TYPES) * (cartons + 6);
        final int end = ((typeIndex / MAX_TYPES) + 1) * (cartons + 6);


        if (!types[typeIndex][0].equals("")) {
            final String[] columns = new String[]{"PackID", "CommodityID"};
            final String[] keys = new String[]{
                types[typeIndex][0], types[typeIndex][1]
            };

            try {
                model = TableModelFactory.getResultSetTableModel(
                    "packprintsetup", columns, keys);

                final ResultSet results = model.getResultSet();
                results.beforeFirst();
                results.next();

                standardWeight = Integer.parseInt(
                    results.getString("PackMass"));
                upperLimit = Double.parseDouble(
                    results.getString("UpperTolerance"));
                lowerLimit = Double.parseDouble(results.getString("LowerTolerance"));

//                standardWeight = Integer.parseInt(
//                                         (String) model.getValueAt(0, 2));
//                upperLimit     = Double.parseDouble(
//                                         (String) model.getValueAt(0, 3));
//                lowerLimit = Double.parseDouble((String) model.getValueAt(0, 4));

                model.close();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            final String query = "select XXX from cartons  " + whereClause +
                " and pack =  '" + types[typeIndex][0] + "' and " +
                " SizeCount = '" + types[typeIndex][2] + "' " +
                " order by LMDate desc limit " + cartons;


            model = TableModelFactory.getResultSetTableModel(query, "weight", false);

            final ResultSet results = model.getResultSet();
            results.beforeFirst();

            int count = 0;
            long averageCount = 0;
            int above = 0;
            int below = 0;
            final int number;
            int value;
            data[start + 0][dataIndex + 1] = "0";
            data[start + 1][dataIndex + 1] = "0";

            while (results.next()) {
                value = results.getInt("weight");
                averageCount += value;

                int difference = value - standardWeight;
                data[start + count + 2][dataIndex + 1] = "0";

                if (difference > 0) { // test above

                    final double d = difference / (double) (standardWeight / 100);

                    if (d > upperLimit) {
                        above++;
                        data[start + count + 2][dataIndex + 1] = "1";
                    }
                } else { //test below
                    difference = -difference;

                    final double d = difference / (double) (standardWeight / 100);

                    if (d > lowerLimit) {
                        below++;
                        data[start + count + 2][dataIndex + 1] = "-1";
                    }
                }

                data[start + count + 2][dataIndex] = "" + value;
                count++;
            }

            number = count;

            while (count < cartons) {
//                data[start+count + 2][dataIndex+1] = "0";
                data[start + count + 2][dataIndex] = "0";
                count++;
            }

            data[end - 4][dataIndex + 1] = "0";
            data[end - 3][dataIndex + 1] = "0";
            data[end - 2][dataIndex + 1] = "0";
            data[end - 1][dataIndex + 1] = "0";

            if (!types[typeIndex][0].equals("")) {
                data[start + 0][dataIndex] = "" + standardWeight;
                data[start + 1][dataIndex] = types[typeIndex][0] + " " +
                    types[typeIndex][2];
            } else {
                data[start + 1][dataIndex] = "Missed";
            }


            //calculate above, below and extra column
            data[end - 4][dataIndex] = "" +
                decFormatter.format(
                    (averageCount / number));
            data[end - 3][dataIndex] = "" + number;

            if (below != 0) {
                data[end - 2][dataIndex] = decFormatter.format(
                    ((double) below / (double) number) * 100);
            } else {
                data[end - 2][dataIndex] = decFormatter.format(0);
            }

            if (above != 0) {
                data[end - 1][dataIndex] = decFormatter.format(
                    ((double) above / (double) number) * 100);
            } else {
                data[end - 1][dataIndex] = decFormatter.format(0);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {
        new WeightReport("2");
    }
}
