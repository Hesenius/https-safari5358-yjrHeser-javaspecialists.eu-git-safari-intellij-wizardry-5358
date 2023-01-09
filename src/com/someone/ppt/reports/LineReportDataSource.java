package com.someone.ppt.reports;

import dori.jasper.engine.*;

class LineReportDataSource implements JRDataSource {

    private String[][] data = null;
    private int index = -1;

    private static String[] headers;

    static {
        headers = new String[4];
        headers[0] = "LineName";
        headers[1] = "Missed";
        headers[2] = "Total";
        headers[3] = "MissedPercentage";
    }


    public LineReportDataSource(final String[][] data) {
        this.data = data;
    }

    public boolean next() throws JRException {
        index++;
        return (index < data.length);
    }

    public Object getFieldValue(final JRField arg0) throws JRException {
        for (int i = 0; i < headers.length; i++) {
            if (arg0.getName().equals(headers[i])) {
                return data[index][i];
            }
        }
        return null;
    }
}
