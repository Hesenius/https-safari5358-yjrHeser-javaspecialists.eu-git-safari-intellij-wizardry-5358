package com.someone.ppt.importer;

import com.someone.io.*;

import java.io.*;
import java.util.*;

/**
 * Reads data GTIN data in from a , delimited textfile with no headers.
 */
public class GrowerDataSource extends DelimitedFileDataSource {
    static String[] lineHeaders;

    static {
        lineHeaders = new String[3];
        lineHeaders[0] = "GrowerID";
        lineHeaders[1] = "GrowerName";
        lineHeaders[2] = "PUC";
    }

    public GrowerDataSource(final String fileName) {
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
