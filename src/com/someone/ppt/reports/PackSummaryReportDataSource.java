package com.someone.ppt.reports;

import dori.jasper.engine.*;

class PackSummaryReportDataSource implements JRDataSource {

    private String[][] data;
    private int index = -1;

    private static String[] headers;

    static {
        headers = new String[6];
        headers[0] = "mark";
        headers[1] = "pack";
        headers[2] = "Total";
        headers[3] = "Average";
        headers[4] = "Min";
        headers[5] = "Max";
    }


    public PackSummaryReportDataSource(final String[][] data) {
        this.data = data;
    }

    public boolean next() {
        index++;
        return index < data.length;
    }

    public Object getFieldValue(final JRField arg0) {
        for (int i = 0; i < headers.length; i++) {
            if (arg0.getName().equals(headers[i])) {
                return data[index][i];
            }
        }
        return null;
    }
}
