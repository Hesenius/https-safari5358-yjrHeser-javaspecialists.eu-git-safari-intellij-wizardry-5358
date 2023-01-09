package com.someone.io;

import java.util.*;

public class DelimitedFileReader {
    protected String fileName;
    protected String[] headers;
    protected int numberOfFields;

    DelimitedFileReader(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Reads the headers of the data
     */
    void initHeaders(final String headerLine) {
        int commaCount = 0;
        int commaIndex = 0;

        do {
            commaIndex = headerLine.indexOf(',', commaIndex + 1);

            if (commaIndex != -1) {
                commaCount++;
            }
        } while (commaIndex != -1);

        numberOfFields = commaCount + 1;

        try {
            headers = populate(headerLine, ",");
        } catch (final Exception e) {
            System.out.println(
                "com.someone.DelimitedFileReader: Header Line is invalid - NOTHING WILL WORK NOW...");
        }
    }

    /**
     * Reads one line of data.
     */
    protected String[] populate(final String data, final String delimiter)
         {
        final String[] result = new String[numberOfFields];

        final StringTokenizer tokenizer = new StringTokenizer(data, delimiter, true);
        int overflow = 0;

        for (int i = 0; i < numberOfFields; i++) {
            String next;
            int j = overflow;

            do {
                if (j == 2) { // handles empty elements
                    next = " ";
                    overflow = 1;
                } else {
                    try {
                        next = tokenizer.nextToken();
                        overflow = 0;
                    } catch (RuntimeException e) {
                        next = " ";

                        if (i != (numberOfFields - 1)) {
                            throw e;
                        }
                    }
                }

                j++;
            } while (delimiter.equals(next) && (j < 3));

            result[i] = next;
        }

        return result;
    }

    public String[] getHeaders() {
        return headers;
    }
}
