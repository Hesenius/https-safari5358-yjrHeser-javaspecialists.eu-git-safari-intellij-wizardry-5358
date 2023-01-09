package com.someone.ppt.reports;

import dori.jasper.engine.*;

import java.io.*;
import java.util.*;

class LineSummaryReport {
    private String packhouseName;
    private String titleString;
    private String dateInfo;

    private String[][] data;

    public LineSummaryReport(final String[][] data, final String titleString, final String dateInfo) {
        this.titleString = titleString;
        this.dateInfo = dateInfo;
        this.data = data;
    }


    public void show() {
        packhouseName = titleString;
        printScreen();
    }

    public void print() {
        packhouseName = titleString;
        printPaper();
    }

    private void printScreen() {
        final Map parameters = new HashMap();
        if (!packhouseName.startsWith("PH")) {
            packhouseName = "Packhouse " + packhouseName;
        }
        parameters.put("PackhouseName", packhouseName);
        parameters.put("DateInfo", "" + dateInfo);

        try {
            final LineReportDataSource source = new LineReportDataSource(data);
            JasperFillManager.fillReportToFile("config/linesummary.jasper", parameters, source);
            JasperExportManager.exportReportToHtmlFile("config/linesummary.jrprint");

            try {
                Runtime.getRuntime().exec("explorer .\\config\\linesummary.html");
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
        parameters.put("DateInfo", "" + dateInfo);

        try {
            final LineReportDataSource source = new LineReportDataSource(data);
            JasperFillManager.fillReportToFile("config/linesummary.jasper", parameters, source);
            JasperPrintManager.printReport("config/linesummary.jrprint", true);

        } catch (final JRException e) {
            e.printStackTrace();
        }
    }
}
