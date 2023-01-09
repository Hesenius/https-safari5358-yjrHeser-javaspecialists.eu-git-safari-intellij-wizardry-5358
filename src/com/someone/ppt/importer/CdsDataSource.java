package com.someone.ppt.importer;

import com.someone.io.*;

import java.io.*;
import java.util.*;

/**
 * Reads data in from a | delimited textfile with no headers.
 */
public class CdsDataSource extends DelimitedFileDataSource {
    private static String[] lineHeaders;

    static {
        lineHeaders = new String[31];
        lineHeaders[0] = "Event";
        lineHeaders[1] = "Date";
        lineHeaders[2] = "Time";
        lineHeaders[3] = "LineID";
        lineHeaders[4] = "Shift";
        lineHeaders[5] = "BarCode";
        lineHeaders[6] = "Weight";
        lineHeaders[7] = "WeightSamples";
        lineHeaders[8] = "TargetWeight";
        lineHeaders[9] = "PackPoint";
        lineHeaders[10] = "Country";
        lineHeaders[11] = "Organization";
        lineHeaders[12] = "PackhouseID";
        lineHeaders[13] = "GrowerID";
        lineHeaders[14] = "RunNumber";
        lineHeaders[15] = "DateCode";
        lineHeaders[16] = "PickingReference";
        lineHeaders[17] = "Commodity";
        lineHeaders[18] = "Variety";
        lineHeaders[19] = "SizeCount";
        lineHeaders[20] = "Pack";
        lineHeaders[21] = "Mark";
        lineHeaders[22] = "Pool";
        lineHeaders[23] = "Grade";
        lineHeaders[24] = "TargetMarket";
        lineHeaders[25] = "Inventory";
        lineHeaders[26] = "PUC";
        lineHeaders[27] = "GTIN";
        lineHeaders[28] = "Remark1";
        lineHeaders[29] = "Remark2";
        lineHeaders[30] = "Remark3";
    }

    public CdsDataSource(final String fileName) {
        super(fileName);
    }

    public static String[] getLineHeaders() {
        return lineHeaders;
    }

    public String[][] getData() {
        try {
            if (data != null) {
                return data;
            }

            String line;
            final ArrayList lines = new ArrayList();

            numberOfFields = lineHeaders.length;
            headers = lineHeaders;

            final BufferedReader fis = FileProxy.getFileReader(fileName);

            while ((line = fis.readLine()) != null) {
                if (!line.equals("")) {
                    lines.add(line);
//
//            		if (!line.startsWith("05")) {
//		                lines.add("05 | " + line);
//            		} else {
//		                lines.add(line);
//            		}
                }
            }

            fis.close();
            data = new String[lines.size()][];

            for (int i = 0; i < lines.size(); i++) {
//            	System.out.print(i + " ");
//            	if (i % 20 == 0) {
//            		System.out.println();
//            	}
                data[i] = populate((String) lines.get(i), "|");
            }
        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return data;
    }
}
