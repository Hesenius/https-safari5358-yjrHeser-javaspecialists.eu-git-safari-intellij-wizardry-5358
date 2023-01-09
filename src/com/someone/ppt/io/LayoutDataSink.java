package com.someone.ppt.io;

import com.someone.io.*;
import com.someone.ppt.gui.packhouse.*;
import com.someone.xml.*;
import org.jdom.*;

import java.io.*;
import java.util.*;

public class LayoutDataSink {
    public static void saveToFile(final Element rootNode, final String fileName)
        throws IOException {
        final BufferedWriter writer = FileProxy.getFileWriter(fileName);

        if (writer == null) {
            System.out.println("Could not initialise file writer... " +
                fileName);
        }

        try {
            writer.write(
                "PackhouseName,LineName,DropName,TableName,StationName,Commodity,Variety,Mark,Pool,Grade,Pack,TargetMarket,Count,PrintCount,Remark1,Remark2,Remark3\n");

            final List packhouses = rootNode.getChildren("Packhouse");
            final int packhouseCount = packhouses.size();

            for (int i = 0; i < packhouseCount; i++) {
                final Element packhouse = (Element) packhouses.get(i);
                final List lines = packhouse.getChildren("Line");
                final int lineCount = lines.size();

                for (int j = 0; j < lineCount; j++) {
                    final Element line = (Element) lines.get(j);
                    final List drops = line.getChildren("Drop");
                    final int dropCount = drops.size();

                    for (int k = 0; k < dropCount; k++) {
                        final Element drop = (Element) drops.get(k);
                        final List tables = drop.getChildren("Table");
                        final int tableCount = tables.size();

                        for (int l = 0; l < tableCount; l++) {
                            final Element table = (Element) tables.get(l);
                            final List stations = table.getChildren("Station");
                            final int stationCount = stations.size();
                            System.out.println(stationCount + " stations ");

                            for (int m = 0; m < stationCount; m++) {
                                final Element station = (Element) stations.get(m);
                                writer.write(packhouse.getChild("Name")
                                    .getTextTrim() + "," +
                                    line.getChild("Name")
                                        .getTextTrim() + "," +
                                    drop.getChild("Name")
                                        .getTextTrim() + "," +
                                    table.getChild("Name")
                                        .getTextTrim() + "," +
                                    station.getChild("Name")
                                        .getTextTrim() + "," +
                                    "\n");
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        writer.close();
    }

    public static void saveToFile(final CisTreeNode rootNode, final String fileName)
        throws IOException {
        final BufferedWriter writer = FileProxy.getFileWriter(fileName);

        if (writer == null) {
            System.out.println("Could not initialise file writer... " +
                fileName);
        }

        try {
            writer.write(
                "PackhouseName,LineName,DropName,TableName,StationName\n");

            final CisTreeNode packhouse = (CisTreeNode) rootNode.getChildAt(0); // rootNode = CIS

            writeLines(writer, packhouse);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        writer.close();
    }

    private static void writeLines(final BufferedWriter writer, final CisTreeNode packhouse) throws IOException {
        final Enumeration lines = packhouse.children();

        while (lines.hasMoreElements()) {
            final CisTreeNode line = (CisTreeNode) lines.nextElement();
            writeDrops(writer, packhouse, line);
        }
    }

    private static void writeDrops(final BufferedWriter writer, final CisTreeNode packhouse, final CisTreeNode line) throws IOException {
        final Enumeration drops = line.children();

        while (drops.hasMoreElements()) {
            final CisTreeNode drop = (CisTreeNode) drops.nextElement();
            writeTables(writer, packhouse, line, drop);
        }
    }

    private static void writeTables(final BufferedWriter writer, final CisTreeNode packhouse, final CisTreeNode line, final CisTreeNode drop) throws IOException {
        final Enumeration tables = drop.children();

        while (tables.hasMoreElements()) {
            final CisTreeNode table = (CisTreeNode) tables.nextElement();
            writeStations(writer, packhouse, line, drop, table);
        }
    }

    private static void writeStations(final BufferedWriter writer, final CisTreeNode packhouse, final CisTreeNode line, final CisTreeNode drop, final CisTreeNode table) throws IOException {
        final Enumeration stations = table.children();

        while (stations.hasMoreElements()) {
            final CisTreeNode station = (CisTreeNode) stations.nextElement();
            writer.write(getName(packhouse) + "," +
                getName(line) + "," + getName(drop) +
                "," + getName(table) + "," +
                getName(station) + "," + "\n");
        }
    }

    private static String getName(final CisTreeNode node) {
        return ((SchemaElement) node.getUserObject()).getSpecial();
    }

    public static void saveLineToFile(final Element rootNode, final String lineName,
                                      final String fileName)
        throws IOException {
        final BufferedWriter writer = FileProxy.getFileWriter(fileName);

        if (writer == null) {
            System.out.println("Could not initialise file writer... " +
                fileName);
        }

        try {
            writer.write(
                "PackhouseName,LineName,DropName,TableName,StationName,Commodity,Variety,Mark,Pool,Grade,Pack,TargetMarket,Count,PrintCount,Remark1,Remark2,Remark3\n");

            final List packhouses = rootNode.getChildren("Packhouse");
            final int packhouseCount = packhouses.size();

            for (int i = 0; i < packhouseCount; i++) {
                final Element packhouse = (Element) packhouses.get(i);
                final List lines = packhouse.getChildren("Line");
                final int lineCount = lines.size();

                for (int j = 0; j < lineCount; j++) {
                    final Element line = (Element) lines.get(j);

                    final String name = line.getChildText("Name");

                    if (!lineName.equals(name)) {
                        continue;
                    }

                    final List drops = line.getChildren("Drop");
                    final int dropCount = drops.size();

                    for (int k = 0; k < dropCount; k++) {
                        final Element drop = (Element) drops.get(k);
                        final List tables = drop.getChildren("Table");
                        final int tableCount = tables.size();

                        for (int l = 0; l < tableCount; l++) {
                            final Element table = (Element) tables.get(l);
                            final List stations = table.getChildren("Station");
                            final int stationCount = stations.size();
                            System.out.println(stationCount + " stations ");

                            for (int m = 0; m < stationCount; m++) {
                                final Element station = (Element) stations.get(m);
                                writer.write(packhouse.getChild("Name")
                                    .getTextTrim() + "," +
                                    line.getChild("Name")
                                        .getTextTrim() + "," +
                                    drop.getChild("Name")
                                        .getTextTrim() + "," +
                                    table.getChild("Name")
                                        .getTextTrim() + "," +
                                    station.getChild("Name")
                                        .getTextTrim() + "," +
                                    station.getChild("Commodity")
                                        .getTextTrim() + "," +
                                    station.getChild("Variety")
                                        .getTextTrim() + "," +
                                    station.getChild("Mark")
                                        .getTextTrim() + "," +
                                    station.getChild("Pool")
                                        .getTextTrim() + "," +
                                    station.getChild("Grade")
                                        .getTextTrim() + "," +
                                    station.getChild("Pack")
                                        .getTextTrim() + "," +
                                    station.getChild("TargetMarket")
                                        .getTextTrim() + "," +
                                    station.getChild("Count")
                                        .getTextTrim() + "," +
                                    station.getChild("PrintCount")
                                        .getTextTrim() + "," +
                                    station.getChild("Remark1")
                                        .getTextTrim() + "," +
                                    station.getChild("Remark2")
                                        .getTextTrim() + "," +
                                    station.getChild("Remark3")
                                        .getTextTrim() + "\n");
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        writer.close();
    }
}

//	public static void saveToFile(CisTreeNode rootNode, String fileName) throws IOException {
//		BufferedWriter writer = FileProxy.getFileWriter(fileName);
//
//		if (writer == null) {
//			System.out.println("Could not initialise file writer... " + fileName);
//		}
//
//        try {
//            SchemaElement cisNode = (SchemaElement) rootNode.getUserObject();
//            Element cisElement = cisNode.getElement();
//
//            int packhouseCount = rootNode.getChildCount();
//
//            for (int i = 0; i < packhouseCount; i++) {
//                CisTreeNode packhouseNode = (CisTreeNode) rootNode.getChildAt(i);
//                SchemaElement packhouse = (SchemaElement) packhouseNode.getUserObject();
//                int lineCount = packhouseNode.getChildCount();
//
//                for (int j = 0; j < lineCount; j++) {
//                    CisTreeNode lineNode = (CisTreeNode) packhouseNode.getChildAt(j);
//                    SchemaElement line = (SchemaElement) lineNode.getUserObject();
//                    int dropCount = lineNode.getChildCount();
//
//                    for (int k = 0; k < dropCount; k++) {
//                        CisTreeNode dropNode = (CisTreeNode) lineNode.getChildAt(k);
//                        SchemaElement drop = (SchemaElement) dropNode.getUserObject();
//                        int tableCount = dropNode.getChildCount();
//
//                        for (int l = 0; l < tableCount; l++) {
//                            CisTreeNode tableNode = (CisTreeNode) dropNode.getChildAt(l);
//                            SchemaElement table = (SchemaElement) tableNode.getUserObject();
//	                        int stationCount = tableNode.getChildCount();
//
//	                        for (int m = 0; m < stationCount; m++) {
//	                            CisTreeNode stationNode = (CisTreeNode) tableNode.getChildAt(m);
//	                            SchemaElement station = (SchemaElement) stationNode.getUserObject();
//	                            Element element = station.getData();
//	                            java.util.List values = element.getChildren();
//	                            for (int z = 0; z < values.size(); z++) {
//									Element child = (Element)values.get(z);
//									System.out.println(child.getTextTrim());
//	                            }
//	                            System.out.println("----->");
//	                            writer.write(	                            	packhouse.getChild("Name").getTextTrim() + "," +
//	                            	line.getChild("Name").getTextTrim() + "," +
//	                            	drop.getChild("Name").getTextTrim() + "," +
//	                            	table.getChild("Name").getTextTrim() + "," +
//	                            	station.getChild("Name").getTextTrim() + "," +
//	                            	station.getChild("Commodity").getTextTrim() + "," +
//	                            	station.getChild("Variety").getTextTrim() + "," +
//	                            	station.getChild("Mark").getTextTrim() + "," +
//	                            	station.getChild("Pool").getTextTrim() + "," +
//	                            	station.getChild("Grade").getTextTrim() + "," +
//	                            	station.getChild("Pack").getTextTrim() + "," +
//	                            	station.getChild("TargetMarket").getTextTrim() + "," +
//	                            	station.getChild("Count").getTextTrim() + "," +
//	                            	station.getChild("PrintCount").getTextTrim() + "," +
//	                            	station.getChild("Remark1").getTextTrim() + "," +
//	                            	station.getChild("Remark2").getTextTrim() + "," +
//	                            	station.getChild("Remark3").getTextTrim()+"\n");
//	                        }
//
//                        }
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        writer.close();
//	}
