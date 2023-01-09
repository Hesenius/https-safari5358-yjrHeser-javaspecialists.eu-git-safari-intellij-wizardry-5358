package com.someone.ppt.server;

import com.someone.db.*;
import com.someone.db.models.*;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Date;
import java.util.*;

class CdsPopulater {
    private int max;
    private int barCodeCount;
    private String[][] data;
    private String[] barCodes;
    private int[] targetWeight;
    private double upperDeviation;
    private double lowerDeviation;
    private long delay;

    public CdsPopulater() {
        max = 30;
        delay = 1000;
        upperDeviation = 0.025;
        lowerDeviation = 0.045;
    }

    private CdsPopulater(final int max, final long delay, final double lowerDeviation,
                         final double upperDeviation) {
        this.delay = delay;
        this.lowerDeviation = lowerDeviation;
        this.max = max;
        this.upperDeviation = upperDeviation;
    }

    public void readTemplate(final String templateName) {
        final String query = "select templates.BarCode, packprintsetup.PackMass " +
            " from templates, packprintsetup where templates.TemplateName = 'XXX' " +
            " and (templates.Pack = packprintsetup.PackID) and (templates.Commodity = packprintsetup.CommodityID)";

        try {
            final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
                query, templateName, false);

            barCodeCount = model.getRowCount();
            barCodes = new String[barCodeCount];
            targetWeight = new int[barCodeCount];

            final ResultSet resultSet = model.getResultSet();
            resultSet.beforeFirst();

            int i = 0;

            while (resultSet.next()) {
                barCodes[i] = resultSet.getString("BarCode");
                targetWeight[i] = resultSet.getInt("PackMass");
                i++;
            }

            model.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < barCodeCount; i++) {
            System.out.println(barCodes[i] + " : " + targetWeight[i]);
        }
    }

    public void generateData() {
        data = new String[max][2];

        final Date d = new Date();
        final Random random = new Random(d.getTime());
        int r;
        double newWeight;
        double db;

        for (int i = 0; i < max; i++) {
            r = random.nextInt(barCodeCount);
            newWeight = targetWeight[r];
            data[i][0] = barCodes[r];

            if (random.nextBoolean()) { //upperDeviation
                db = (double) (random.nextDouble() * upperDeviation);
                newWeight = (newWeight * db) + newWeight;
            } else { // lowerDeviation
                db = (double) (random.nextDouble() * lowerDeviation);
                newWeight = newWeight - (newWeight * db);
            }

            data[i][1] = "" + (int) newWeight;
        }

        for (int i = 0; i < max; i++) {
            System.out.println(data[i][0] + " : " + data[i][1]);
        }
    }

    public void sendData() {
        final LineSocket socket = new LineSocket("172.16.1.196", 2000);
//		try {
//			socket.openSocket();
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

        for (int i = 0; i < max; i++) {
            try {
                socket.openSocket();

                final String dataString = "[93 " + data[i][0] + " " + data[i][1];
                System.out.println(dataString + " -> ");

                final String result = socket.transmitTest(dataString);
                System.out.print(result);
                final byte[] bytes = result.getBytes();
                System.out.println("9de: " + Integer.toHexString((int) bytes[9]));
                System.out.println("laaste twee: " + Integer.toHexString((int) bytes[bytes.length - 3]) + Integer.toHexString((int) bytes[bytes.length - 4]));
                System.out.println();
//                System.out.println(socket.transmitTest(dataString));
//				socket.transmit(dataString);
            } catch (final Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.closeSocket();
                } catch (final SocketException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(delay);
            } catch (final Exception e) {
            }
        }


//		try {
//			socket.openSocket();
//
//			String dataString = "[93 1722 12496";
////			System.out.println(dataString + " -> ");
//			String result = socket.transmitTest(dataString);
//			System.out.println(result);
//			byte[] bytes = result.getBytes();
//			System.out.println("9de: " + bytes[9]);
//			System.out.println("laaste twee: " + bytes[bytes.length-4] + " " + bytes[bytes.length-3]);
//
////			socket.transmit(dataString);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				socket.closeSocket();
//			} catch (SocketException e) {
//				e.printStackTrace();
//			}
//		}


//    	try {
//    		socket.closeSocket();
//    	} catch (SocketException e) {
//    		e.printStackTrace();
//    	}

    }

    public String[][] getData() {
        return data;
    }


    private void sendFiles() throws UnknownHostException, IOException {
        final LineSocket socket = new LineSocket("172.16.1.196", 2000);
        socket.openSocket();

        final File directory = new File("D:/temp/ppt");
        final File[] dirFiles = directory.listFiles();

        for (int i = 0; i < dirFiles.length; i++) {
            try {
                final FileEvent e = new FileEvent(dirFiles[i]);
                System.out.println("Sending file " + dirFiles[i]);
                socket.sendFile(e);
            } catch (final Exception ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }

        }

        socket.closeSocket();
    }

    public static void main(final String[] args) throws UnknownHostException, IOException {

        ConnectionFactory.resetConnection("jdbc:mysql://localhost:3306/tad2");

        final CdsPopulater populater = new CdsPopulater(1, 1000, 0.045, 0.025);
        for (int i = 0; i < 100; i++) {
            System.out.println(i);
            populater.sendFiles();
            try {
                Thread.sleep(2000);
            } catch (final InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

//		populater.readTemplate("21RGL");
//        populater.generateData();
//        populater.sendData();
//        System.out.println("finished!");


    }
}
