package com.someone.ppt.server;

import com.someone.db.models.*;
import com.someone.ppt.cds.*;
import com.someone.ppt.importer.*;
import com.someone.ppt.models.*;
import com.someone.ppt.server.gui.*;
import com.someone.util.*;
import org.apache.tomcat.util.*;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

public class LineControl {
    private static String dateformat = "ddMM";
    private static SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
    private Line line;
    private LineDisplay display;
    private LineSocket socket;
    private String baseDirectory;
    private String tempDir;
    private String receiveDir;
    private String sendDir;


    public LineControl(final Line line, final String baseDirectory) {
        this.line = line;
        this.baseDirectory = baseDirectory;
    }

    public void initialise() {
        createDirectories(line, baseDirectory);
        display = new LineDisplay(line);
        socket = new LineSocket(line.getIp(), line.getPort());
    }

    private void createDirectories(final Line line, final String baseDirectory) {
        receiveDir = baseDirectory + "/" + line.getPackhouseName() +
            "/" + line.getLineName() +
            "/received/";
        tempDir = baseDirectory + "/" + line.getPackhouseName() +
            "/" + line.getLineName() +
            "/temp/";
        sendDir = baseDirectory + "/" + line.getPackhouseName() +
            "/" + line.getLineName() +
            "/send/";

        File testDir = new File(receiveDir);

        if (!testDir.exists()) {
            testDir.mkdirs();
        }

        testDir = new File(tempDir);

        if (!testDir.exists()) {
            testDir.mkdirs();
        }

        testDir = new File(sendDir);

        if (!testDir.exists()) {
            testDir.mkdirs();
        }
    }

    public LineDisplay getDisplay() {
        return display;
    }

    public Line getLine() {
        return line;
    }

    public void pollSocket() {
        boolean active = true;
        try {
            display.recover();
            socket.openSocket();

            display.setSocketSend(true);

            final File f = new File(new File(tempDir),
                "CISNEWD.L0" + line.getLineID());


            final Calendar now = Calendar.getInstance();
            final int downloadMinute = now.get(Calendar.MINUTE);

            socket.receiveFile(f, "[4 ");


            //        	System.out.println("END Request data: " + f.length());
            display.setSocketSend(false);
            display.setPssReceive(true);

            if (f.length() == 0) {
                //        		System.out.println("Nothing here");
                return;
            }

            final File oldie = new File(new File(receiveDir),
                "CISD" + sdf.format(new Date()) + ".L0" +
                    line.getLineID());
            BufferedWriter writer = new BufferedWriter(
                new FileWriter(
                    oldie.getAbsolutePath(),
                    true));

            try {
                final BufferedReader reader = new BufferedReader(new FileReader(f));
                String line;

                while ((line = reader.readLine()) != null) {
                    writer.write(line + "\n");
                }

                reader.close();
                writer.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }

            final File f2 = new File(new File(receiveDir),
                "CISNEWD.L0" + line.getLineID());
            writer = new BufferedWriter(new FileWriter(f2));

            try {
                final BufferedReader reader = new BufferedReader(new FileReader(f));
                String line;

                while ((line = reader.readLine()) != null) {
                    writer.write(line + "\n");
                }

                reader.close();
                writer.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }

            display.setPssReceive(true);

            final CdsParser parser = new CdsParser(line.getPackhouseName(),
                line.getLineName(),
                f2.getAbsolutePath());
            parser.parse();

            display.setPssReceive(false);

            // read f2 into database;
        } catch (final Exception e) {
            display.error(e);
            active = false;
        } finally {
            display.setPssReceive(false);

            try {
                socket.closeSocket();
            } catch (final SocketException e) {
            }
        }
        if (active) {
            timeSync();
        }
    }

    public boolean sendToSocket(final FileEvent[] events) {
        boolean success = true;

        try {
            display.recover();
            display.setPssReceive(true);

            socket.openSocket();

            System.out.println("Sending files on : " + new Date());
            for (int i = 0; i < events.length; i++) {
                final File f = events[i].getFile();
                System.out.println("sending " + f.getAbsolutePath());
                display.setSocketSend(true);
                socket.sendFile(events[i]);

                System.out.println("Deleting " + f.getAbsolutePath());
                events[i].getFile().delete();
                display.setSocketSend(false);
                display.setPssReceive(false);

                try {
                    //	        		System.out.println("Sleeping 1 second");
                    Thread.sleep(1500);
                } catch (final Exception exxx) {
                }
            }
            System.out.println("Sending completed on : " + new Date());
        } catch (final Exception e) {
            final Exception ex = e;
            e.printStackTrace();
            display.error(ex);
            success = false;
        } finally {
            display.setPssReceive(false);
            display.setSocketSend(false);

            try {
                socket.closeSocket();
            } catch (final SocketException e) {
            }
        }

        return success;
    }

    public void sendUpdate() {
        System.out.println("Query db for update?");
        try {
            final String[] columns = new String[]{
                "PackhouseName", "LineName"
            };
            final String[] keys = new String[]{
                line.getPackhouseName(), line.getLineName()
            };
            final ScrollableTableModel queryLine = TableModelFactory.getResultSetTableModel(
                "packhousetemplates",
                columns, keys);
            final ResultSet resultSet = queryLine.getResultSet();
            resultSet.absolute(1);
            final String status = resultSet.getString("Status");
            queryLine.close();
            if (!"updated".equals(status)) {
                return;
            }
        } catch (final SQLException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Sending new setup to " + line.getPackhouseName() + " line " + line.getLineName());
        System.out.println("Get line data from db on" + new Date());
        final Packhouse packhouse = new Packhouse(line.getPackhouseName());
        packhouse.loadLayout();
        packhouse.loadTemplates();
        display.setPssSend(true);

        System.out.println("Generating Line Module data on" + new Date());
        final CdsGenerator generator = new CdsGenerator(packhouse,
            line.getLineID() - 1);
        generator.generateFiles(sendDir);

        display.setPssSend(false);

        FileEvent[] events = null;
        final ArrayList files = new ArrayList();
        final File directory = new File(sendDir);

        final File[] dirFiles = directory.listFiles(
            new AbstractFileListener.DirFilter());

        System.out.println("Generating send events");
        try {
            for (int i = 0; i < dirFiles.length; i++) {
                files.add(new FileEvent(dirFiles[i]));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        events = new FileEvent[files.size()];
        files.toArray(events);

        System.out.println("Trying to send files on " + new Date());
        final boolean success = sendToSocket(events);

        System.out.println("Updating db status on " + new Date());
        if (success) {
            try {
                final String[] columns = new String[]{
                    "PackhouseName", "LineName"
                };
                final String[] keys = new String[]{
                    line.getPackhouseName(), line.getLineName()
                };
                final ScrollableTableModel updatedLine = TableModelFactory.getResultSetTableModel(
                    "packhousetemplates",
                    columns, keys);
                final ResultSet resultSet = updatedLine.getResultSet();
                resultSet.absolute(1);
                resultSet.updateString("Status", "synchronized");
                resultSet.updateRow();
                updatedLine.close();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("could not upload setup");
        }

        if (success) {
            timeSync();
        }
    }

    private void timeSync() {
        try {
            socket.openSocket();
            String dateString = "[92 ";

            final FastDateFormat formatter = new FastDateFormat(
                new SimpleDateFormat("dd MM yyyy"));

            final FastDateFormat formatter2 = new FastDateFormat(
                new SimpleDateFormat("kk mm ss"));
            final Date now = new Date();

            dateString += formatter.format(now);
            final Calendar c = Calendar.getInstance();
            c.get(Calendar.DAY_OF_WEEK);
            dateString += " " + (c.get(Calendar.DAY_OF_WEEK) - 1) + " ";
            dateString += formatter2.format(now);

//    		dateString = "[92 19 12 2001 3 10 54 24";
            socket.transmit(dateString);
        } catch (final Exception e) {
//    		e.printStackTrace();
        } finally {
            try {
                socket.closeSocket();
            } catch (final SocketException e) {
//				e.printStackTrace();
            }
        }
    }

    public static void main(final String[] args) {
        String dateString = "[92 ";

        final FastDateFormat formatter = new FastDateFormat(
            new SimpleDateFormat("dd MM yyyy"));

        final FastDateFormat formatter2 = new FastDateFormat(
            new SimpleDateFormat("kk mm ss"));
        final Date now = new Date();

        dateString += formatter.format(now);
        final Calendar c = Calendar.getInstance();
        c.get(Calendar.DAY_OF_WEEK);
        dateString += " " + c.get(Calendar.DAY_OF_WEEK) + " ";
        dateString += formatter2.format(now);
        System.out.println(dateString);
    }
}

//	private void handleMidnightOverRoll() {
//		Calendar now = Calendar.getInstance();
//		int hour = now.get(Calendar.HOUR_OF_DAY);
//		int minute = now.get(Calendar.MINUTE);
//
//		if (hour == 0) { // 12 something
//			if ((minute - downloadMinute) < 0) {
//				// there is data left that must be imported from previous cycle
//				downloadMinute = 0;
//				overRoll();
//			}
//		}
//	}
//
//	private void overRoll() {
//		try {
//
//			display.recover();
//			socket.openSocket();
//
//			display.setSocketSend(true);
//
//			File f = new File(new File(tempDir),
//							  "CISNEWD.L0" + line.getLineID());
//
//			socket.receiveFile(f, "[5 ");
//
//
//			//        	System.out.println("END Request data: " + f.length());
//			display.setSocketSend(false);
//			display.setPssReceive(true);
//
//			if (f.length() == 0) {
//				//        		System.out.println("Nothing here");
//				return;
//			}
//
//			File oldie = new File(new File(receiveDir),
//								  "CISD" + sdf.format(new Date()) + ".L0" +
//								  line.getLineID());
//			BufferedWriter writer = new BufferedWriter(
//											new FileWriter(
//													oldie.getAbsolutePath(),
//													true));
//
//			try {
//				BufferedReader reader = new BufferedReader(new FileReader(f));
//				String         line;
//
//				while ((line = reader.readLine()) != null) {
//					writer.write(line + "\n");
//				}
//
//				reader.close();
//				writer.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			File f2 = new File(new File(receiveDir),
//							   "CISNEWD.L0" + line.getLineID());
//			writer = new BufferedWriter(new FileWriter(f2));
//
//			try {
//				BufferedReader reader = new BufferedReader(new FileReader(f));
//				String         line;
//
//				while ((line = reader.readLine()) != null) {
//					writer.write(line + "\n");
//				}
//
//				reader.close();
//				writer.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			display.setPssReceive(true);
//
//			CdsParser parser = new CdsParser(line.getPackhouseName(),
//											 line.getLineName(),
//											 f2.getAbsolutePath());
//			parser.parse();
//
//			display.setPssReceive(false);
//
//			// read f2 into database;
//		} catch (Exception e) {
//			display.error(e);
//		} finally {
//			display.setPssReceive(false);
//
//			try {
//				socket.closeSocket();
//			} catch (SocketException e) {
//			}
//		}
//	}
