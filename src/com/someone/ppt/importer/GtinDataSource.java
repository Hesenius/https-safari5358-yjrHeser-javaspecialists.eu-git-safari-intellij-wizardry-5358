package com.someone.ppt.importer;

import com.someone.io.*;

import java.io.*;
import java.util.*;

/**
 * Reads data GTIN data in from a , delimited textfile with no headers.
 */
public class GtinDataSource extends DelimitedFileDataSource {
    private static String[] lineHeaders;

    static {
        lineHeaders = new String[24];
        lineHeaders[0] = "GtinCode";
        lineHeaders[1] = "Organisation";
        lineHeaders[2] = "OrganisationDesc";
        lineHeaders[3] = "Commodity";
        lineHeaders[4] = "CommodityDesc";
        lineHeaders[5] = "Country";
        lineHeaders[6] = "Variety";
        lineHeaders[7] = "VarietyDesc";
        lineHeaders[8] = "Pack";
        lineHeaders[9] = "PackDesc";
        lineHeaders[10] = "Grade";
        lineHeaders[11] = "Mark";
        lineHeaders[12] = "MarkDesc";
        lineHeaders[13] = "InventoryCode";
        lineHeaders[14] = "SizeCount";
        lineHeaders[15] = "SortSequence";
        lineHeaders[16] = "PickType";
        lineHeaders[17] = "Mass";
        lineHeaders[18] = "StartDate";
        lineHeaders[19] = "EndDate";
        lineHeaders[20] = "TransactionUser";
        lineHeaders[21] = "TransactionDate";
        lineHeaders[22] = "TransactionTime";
        lineHeaders[23] = "Active";

    }

    public GtinDataSource(final String fileName) {
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
                }
            }

            fis.close();
            data = new String[lines.size()][];

            for (int i = 0; i < lines.size(); i++) {
                data[i] = populate((String) lines.get(i), ",");
            }
        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return data;
    }
}
