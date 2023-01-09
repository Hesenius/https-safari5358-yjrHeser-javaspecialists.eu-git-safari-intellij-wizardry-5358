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

public class SummaryReport {
    private FastDateFormat formatter;
    private FastDateFormat shortFormat;
    private Date startDate;
    private Date endDate;
    private String packhouseName;
    private String whereClause;
    private String titleString;
    private String dateInfo;


    private String[][] data;

    public SummaryReport(final String packhouseName) {
        this.packhouseName = packhouseName;
        formatter = new FastDateFormat(new SimpleDateFormat("yyyy-MM-dd kk:mm:ss"));
        shortFormat = new FastDateFormat(new SimpleDateFormat("yyyy-MM-dd kk:mm"));
    }

    public SummaryReport(final String dateInfo, final String titleString, final String whereClause) {
        this.titleString = titleString;
        this.whereClause = whereClause;
        this.dateInfo = dateInfo;
        formatter = new FastDateFormat(new SimpleDateFormat("yyyy-MM-dd kk:mm:ss"));
        shortFormat = new FastDateFormat(new SimpleDateFormat("yyyy-MM-dd kk:mm"));
    }


    public void show() {
        if (whereClause == null) {
            showScreenDialog();
        } else {
            createDetailedData();
            packhouseName = titleString;
            printScreen();
        }
    }

    public void print() {
        if (whereClause == null) {
            showPrintDialog();
        } else {
            createDetailedData();
            packhouseName = titleString;
            printPaper();
        }
    }

    private void printScreen() {
        final Map parameters = new HashMap();
        if (!packhouseName.startsWith("PH")) {
            packhouseName = "Packhouse " + packhouseName;
        }
        parameters.put("PackhouseName", packhouseName);
        parameters.put("DateInfo", dateInfo);

        try {
            final SummaryReportDataSource source = new SummaryReportDataSource(data);

            JasperFillManager.fillReportToFile("config/summary.jasper", parameters, source);
            JasperExportManager.exportReportToHtmlFile("config/summary.jrprint");

            try {
                Runtime.getRuntime().exec("explorer .\\config\\summary.html");
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
        parameters.put("DateInfo", dateInfo);

        try {
            final SummaryReportDataSource source = new SummaryReportDataSource(data);
            JasperFillManager.fillReportToFile("config/summary.jasper", parameters, source);
            JasperPrintManager.printReport("config/summary.jrprint", true);

        } catch (final JRException e) {
            e.printStackTrace();
        }
    }


    private void showPrintDialog() {
        final Date now = new Date();

        final String[] columnNames = {"Packhouse", "Start Date", "End Date"};
        final DataInputFrame frame = new DataInputFrame(
            "CMS Summary",
            columnNames, false);
        final String[] data = {packhouseName, shortFormat.format(now), shortFormat.format(now)};
        frame.setData(data);

        final Runnable modifier = () -> {
            if (frame.isModified()) {
                try {
                    final String[] inputs = frame.getNewData();
                    packhouseName = inputs[0];
                    startDate = shortFormat.parse(inputs[1]);
                    endDate = shortFormat.parse(inputs[2]);
                    dateInfo = inputs[1] + " to " + inputs[2] + " (period)";

                    createData();

                    printPaper();

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

    private void showScreenDialog() {
        final Date now = new Date();

        final String[] columnNames = {"Packhouse", "Start Date", "End Date"};
        final DataInputFrame frame = new DataInputFrame(
            "CMS Summary",
            columnNames, false);
        final String[] data = {packhouseName, shortFormat.format(now), shortFormat.format(now)};
        frame.setData(data);

        final Runnable modifier = () -> {
            if (frame.isModified()) {
                try {
                    final String[] inputs = frame.getNewData();
                    packhouseName = inputs[0];
                    startDate = shortFormat.parse(inputs[1]);
                    endDate = shortFormat.parse(inputs[2]);
                    dateInfo = inputs[1] + " to " + inputs[2] + " (period)";

                    createData();

                    printScreen();

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

    private void createData() {
        final DecimalFormat decFormatter = new DecimalFormat("#####.00");

        final ScrollableTableModel model;

        try {
            final String query =
                " select mark, pack, sizecount, count(XXX) as Total, " +
                    " avg(weight) as Average, min(weight) as 'Min'," +
                    " max(weight) as 'Max' from cartons " +
                    " where PackhouseName ='" + packhouseName + "' " +
                    " and LMDate between '" + formatter.format(startDate) +
                    "' and '" + formatter.format(endDate) + "' " +
                    " group by mark, pack, sizecount";


            model = TableModelFactory.getResultSetTableModel(query, "*", false);

            final ResultSet results = model.getResultSet();
            results.beforeFirst();
            data = new String[model.getRowCount()][7];

            int count = 0;
            while (results.next()) {
                data[count] = model.getRow(count);
                data[count][4] = decFormatter.format(Double.parseDouble(data[count][4]));
                count++;
            }

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDetailedData() {
        final DecimalFormat decFormatter = new DecimalFormat("######.00");

        final ScrollableTableModel model;

        try {
            final String query =
                " select mark, pack, sizecount, count(XXX) as Total, " +
                    " avg(weight) as Average, min(weight) as 'Min'," +
                    " max(weight) as 'Max' from cartons " +
                    whereClause +
                    " group by mark, pack, sizecount";

            model = TableModelFactory.getResultSetTableModel(query, "*", false);

            final ResultSet results = model.getResultSet();
            results.beforeFirst();
            data = new String[model.getRowCount()][6];

            int count = 0;
            while (results.next()) {
                data[count] = model.getRow(count);
                data[count][4] = decFormatter.format(Double.parseDouble(data[count][4]));
                count++;
            }

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {
        new SummaryReport("2");
    }
}
