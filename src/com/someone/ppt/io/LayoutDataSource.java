package com.someone.ppt.io;

import com.someone.io.*;
import com.someone.ppt.models.*;
import org.jdom.*;

import java.io.*;
import java.util.*;


public class LayoutDataSource extends DelimitedFileDataSource {
    private static String[] lineHeaders;
    public static String[] termonology;

    static {
        lineHeaders = Layout.fields;
        termonology = new String[5];
        termonology[0] = Layout.fields[0];
        termonology[1] = Layout.fields[1];
        termonology[2] = Layout.fields[2];
        termonology[3] = Layout.fields[3];
        termonology[4] = Layout.fields[4];
    }

    public static String getNodeName(final String level) {

        String name = "";
        try {
            name = level.substring(level.indexOf('('));
        } catch (final Exception e) {
        }

        if (level.indexOf("CMS") != -1) {
            return level;
        }
        if (level.indexOf(Layout.fields[0]) != -1) {
            return termonology[0] + name;
        } else if (level.indexOf(Layout.fields[1]) != -1) {
            return termonology[1] + name;
        } else if (level.indexOf(Layout.fields[2]) != -1) {
            return termonology[2] + name;
        } else if (level.indexOf(Layout.fields[3]) != -1) {
            return termonology[3] + name;
        } else {
            return termonology[4] + name;
        }
    }

    public LayoutDataSource(final String fileName) {
        super(fileName);
    }

    public static String[] getLineHeaders() {
        return lineHeaders;
    }

    public Element parseFile() {

        String packhouse = null;
        String line = null;
        String drop = null;
        String table = null;
        String station = null;

        final Element layout = new Element("CIS");
        Element packhouseElement = null;
        Element lineElement = null;
        Element dropElement = null;
        Element tableElement = null;
        Element stationElement = null;
        getData();
//        next(); // skip header line

        while (next()) {
            if (!getFieldValue(lineHeaders[0]).equals(packhouse)) {
                packhouse = getFieldValue(lineHeaders[0]);

                packhouseElement = new Element(Layout.fields[0]);
                packhouseElement.addContent(new Element("Name").addContent(
                    packhouse));
                layout.addContent(packhouseElement);

                line = null;
                drop = null;
                table = null;
                station = null;
            }

            if (!getFieldValue(lineHeaders[1]).equals(line)) {
                line = getFieldValue(lineHeaders[1]);
                drop = null;
                table = null;
                station = null;

                lineElement = new Element(Layout.fields[1]);
                lineElement.addContent(new Element("Name").addContent(line));
                packhouseElement.addContent(lineElement);
            }

            if (!getFieldValue(lineHeaders[2]).equals(drop)) {
                drop = getFieldValue(lineHeaders[2]);
                table = null;
                station = null;

                dropElement = new Element(Layout.fields[2]);
                dropElement.addContent(new Element("Name").addContent(drop));
                lineElement.addContent(dropElement);
            }

            if (!getFieldValue(lineHeaders[3]).equals(table)) {
                table = getFieldValue(lineHeaders[3]);
                station = null;

                tableElement = new Element(Layout.fields[3]);
                tableElement.addContent(new Element("Name").addContent(table));
                dropElement.addContent(tableElement);
            }

            if (!getFieldValue(lineHeaders[4]).equals(station)) {
                station = getFieldValue(lineHeaders[4]);

                stationElement = new Element(Layout.fields[4]);
                stationElement.addContent(new Element("Name").addContent(
                    station));

//                for (int i = 2; i < lineHeaders.length; i++) {
//                	String value = getFieldValue(lineHeaders[i]);
//                	if (value != null) {
//	                    stationElement.addContent(new Element(lineHeaders[i]).addContent(value));
//                	}
//                }

                tableElement.addContent(stationElement);
            }
        }

        return layout;
    }

    public static Element parseFile(final String[][] externalData) {

        String packhouse = null;
        String line = null;
        String drop = null;
        String table = null;
        String station = null;

        final Element layout = new Element("CIS");
        Element packhouseElement = null;
        Element lineElement = null;
        Element dropElement = null;
        Element tableElement = null;
        Element stationElement = null;
        for (int i = 0; i < externalData.length; i++) {
            if (!externalData[i][0].equals(packhouse)) {
                packhouse = externalData[i][0];

                packhouseElement = new Element(Layout.fields[0]);
                packhouseElement.addContent(new Element("Name").addContent(
                    packhouse));
                layout.addContent(packhouseElement);

                line = null;
                drop = null;
                table = null;
                station = null;
            }

            if (!externalData[i][1].equals(line)) {
                line = externalData[i][1];
                drop = null;
                table = null;
                station = null;

                lineElement = new Element(Layout.fields[1]);
                lineElement.addContent(new Element("Name").addContent(line));
                packhouseElement.addContent(lineElement);
            }

            if (!externalData[i][2].equals(drop)) {
                drop = externalData[i][2];
                table = null;
                station = null;

                dropElement = new Element(Layout.fields[2]);
                dropElement.addContent(new Element("Name").addContent(drop));
                lineElement.addContent(dropElement);
            }

            if (!externalData[i][3].equals(table)) {
                table = externalData[i][3];
                station = null;

                tableElement = new Element(Layout.fields[3]);
                tableElement.addContent(new Element("Name").addContent(table));
                dropElement.addContent(tableElement);
            }

            if (!externalData[i][4].equals(station)) {
                station = externalData[i][4];

                stationElement = new Element(Layout.fields[4]);
                stationElement.addContent(new Element("Name").addContent(
                    station));

//                for (int i = 2; i < lineHeaders.length; i++) {
//                	String value = getFieldValue(lineHeaders[i]);
//                	if (value != null) {
//	                    stationElement.addContent(new Element(lineHeaders[i]).addContent(value));
//                	}
//                }

                tableElement.addContent(stationElement);
            }
        }

        return layout;
    }

    public String[][] getData() {
        try {
            if (data != null) {
                return data;
            }


            numberOfFields = lineHeaders.length;
            headers = lineHeaders;

            final ArrayList lines = readFileIntoList();

            data = new String[lines.size()][];

            for (int i = 0; i < lines.size(); i++) {
                data[i] = populate((String) lines.get(i), ",");
            }
        } catch (final Exception e) {
            System.out.println(e.getMessage());
        }

        return data;
    }

    private ArrayList readFileIntoList() throws IOException {
        final ArrayList lines = new ArrayList();
        String line;
        final BufferedReader fis = FileProxy.getFileReader(fileName);
        while ((line = fis.readLine()) != null) {
            lines.add(line);
        }
        fis.close();
        return lines;
    }
}
