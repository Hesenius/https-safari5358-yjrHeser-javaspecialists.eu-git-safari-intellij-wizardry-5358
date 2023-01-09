package com.someone.io;

import java.io.*;
import java.util.*;


public class DelimitedFileDataSource extends DelimitedFileReader {
    protected String[][] data = null;
    private int index = -1;

    public DelimitedFileDataSource(final String fileName) {
        super(fileName);
    }

    public String[][] getData() {
        try {
            if (data != null) {
                return data;
            }

            final BufferedReader fis = FileProxy.getFileReader(fileName);
            final ArrayList lines = new ArrayList();
            String line;

            initHeaders(fis.readLine());
            while ((line = fis.readLine()) != null) {
                lines.add(line);
            }

            fis.close();
            data = new String[lines.size()][];

            for (int i = 0; i < lines.size(); i++) {
                data[i] = populate((String) lines.get(i), ",");
            }

        } catch (final Exception e) {
            System.out.println(e.getMessage());
        }

        return data;
    }

    public boolean next() {
        if (data == null) {
            getData();
        }

        index++;

        return (index < data.length);
    }

    public String getFieldValue(final String arg0) {

        for (int i = 0; i < headers.length; i++) {
            if (arg0.equals(headers[i])) {
                try {
                    return (String) data[index][i];
                } catch (final RuntimeException e) {
                }
            }
        }

        return "";
    }

}
