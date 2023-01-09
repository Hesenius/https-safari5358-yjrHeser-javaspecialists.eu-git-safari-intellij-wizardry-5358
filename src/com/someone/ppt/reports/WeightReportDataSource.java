package com.someone.ppt.reports;

import dori.jasper.engine.*;

class WeightReportDataSource implements JRDataSource {

    private String[][] data = null;
    private int index = -1;

    private static String[] headers;

    static {
        headers = new String[34];
        headers[0] = "Head1";
        headers[1] = "Head2";
        headers[2] = "Pack1";
        headers[3] = "Pack1Fit";
        headers[4] = "Pack2";
        headers[5] = "Pack2Fit";
        headers[6] = "Pack3";
        headers[7] = "Pack3Fit";
        headers[8] = "Pack4";
        headers[9] = "Pack4Fit";
        headers[10] = "Pack5";
        headers[11] = "Pack5Fit";
        headers[12] = "Pack6";
        headers[13] = "Pack6Fit";
        headers[14] = "Pack7";
        headers[15] = "Pack7Fit";
        headers[16] = "Pack8";
        headers[17] = "Pack8Fit";
        headers[18] = "Pack9";
        headers[19] = "Pack9Fit";
        headers[20] = "Pack10";
        headers[21] = "Pack10Fit";
        headers[22] = "Pack11";
        headers[23] = "Pack11Fit";
        headers[24] = "Pack12";
        headers[25] = "Pack12Fit";
        headers[26] = "Pack13";
        headers[27] = "Pack13Fit";
        headers[28] = "Pack14";
        headers[29] = "Pack14Fit";
        headers[30] = "Pack15";
        headers[31] = "Pack15Fit";
        headers[32] = "Pack16";
        headers[33] = "Pack16Fit";


    }


    public WeightReportDataSource(final String[][] data) {
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
