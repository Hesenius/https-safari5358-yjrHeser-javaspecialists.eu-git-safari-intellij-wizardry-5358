package com.someone.ppt.reports;

import dori.jasper.engine.*;

class SummaryReportDataSource implements JRDataSource {

    private String[][] data = null;
    private int index = -1;

    private static String[] headers;

    static {
        headers = new String[7];
        headers[0] = "mark";
        headers[1] = "pack";
        headers[2] = "sizecount";
        headers[3] = "Total";
        headers[4] = "Average";
        headers[5] = "Min";
        headers[6] = "Max";
    }


    public SummaryReportDataSource(final String[][] data) {
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
