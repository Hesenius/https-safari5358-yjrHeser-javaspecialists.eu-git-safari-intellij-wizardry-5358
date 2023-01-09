package com.someone.ppt.server;

import com.someone.db.models.*;

import java.sql.*;

class LineGroup {
    private int readCycle;
    private String baseDirectory;
    private Line[] lines;

    public void loadLines() {
        readServerInfo();
        readActiveLines();
    }

    private void readServerInfo() {
        try {
            System.out.println("Reading serverinfo");
            final ScrollableTableModel serverSetup = TableModelFactory.getResultSetTableModel("ServerConfig");
            readCycle = Integer.parseInt((String) serverSetup.getValueAt(0, 1));
            baseDirectory = (String) serverSetup.getValueAt(0, 2);
            serverSetup.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void readActiveLines() {
        try {
            System.out.println("Reading active lines");
            final String[] keys = new String[]{"Active", "ServerID"};
            final String[] values = new String[]{"yes", "2"};
            final ScrollableTableModel activeLines = TableModelFactory.getResultSetTableModel("packhouselines", keys, values);
            lines = new Line[activeLines.getRowCount()];

            System.out.println("Active lines:" + lines.length);
            for (int i = 0; i < lines.length; i++) {
                final String packhouseName = (String) activeLines.getValueAt(i, 0);
                final String lineName = (String) activeLines.getValueAt(i, 1);
                final String description = (String) activeLines.getValueAt(i, 6);
                final String ip = (String) activeLines.getValueAt(i, 3);
                final int serverID = Integer.parseInt((String) activeLines.getValueAt(i, 2));
                final int port = Integer.parseInt((String) activeLines.getValueAt(i, 4));

                lines[i] = new Line(packhouseName, lineName);
                lines[i].setDescription(description);
                lines[i].setIp(ip);
                lines[i].setPort(port);
                lines[i].setServerID(serverID);
                System.out.println(
                    "PH Name:" + lines[i].getPackhouseName() +
                        " Ln:" + lines[i].getLineName() +
                        " IP:" + lines[i].getIp() +
                        " Port:" + lines[i].getPort() +
                        " ServerID:" + lines[i].getServerID());
            }

            activeLines.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public Line[] getLines() {
        return lines;
    }

    public int getReadCycle() {
        return readCycle;
    }

    public void setLines(final Line[] lines) {
        this.lines = lines;
    }

    public void setReadCycle(final int readCycle) {
        this.readCycle = readCycle;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(final String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public static void main(final String[] args) {
        final LineGroup groupie = new LineGroup();
        groupie.loadLines();
        System.out.println("Finished");
    }
}
