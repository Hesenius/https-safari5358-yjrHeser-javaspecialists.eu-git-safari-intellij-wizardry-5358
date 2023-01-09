package com.someone.ppt.server;

import com.someone.db.*;
import com.someone.ppt.server.gui.*;
import com.someone.util.*;
import com.someone.util.Timer;

import java.util.*;


public class CmsServer implements Tickable {

    public static final int OFF = 0;
    private static final int ON = 1;
    public static final int RESTART = 2;

    private LineControl[] lines; // entry points to individual lines
    private Object[] lineLocks;  // locks to synchronize communication with lines
    private boolean[] activity;

    private int serverState;
    private Gui gui;
    private Importer importer;

    public int getLineID(final String packhouseName, final String lineName) {
        for (int i = 0; i < lines.length; i++) {
            final Line line = lines[i].getLine();
            if (line.getPackhouseName().equals(packhouseName) &&
                line.getLineName().equals(lineName)) {
                return i;
            }
        }
        return -1;
    }

    private void readSetup() {
        // load linegroup and associated lines from database
    }

    /**
     * Set up and starts the line controls, locks and gui.
     */
    private void startServer() {
        System.out.println(new Date());
        readSetup();

        final LineGroup lineGroup = new LineGroup();
        lineGroup.loadLines();
        final Line[] configuredLines = lineGroup.getLines();
        activity = new boolean[configuredLines.length];
        lineLocks = new Object[configuredLines.length];
        lines = new LineControl[configuredLines.length];
        for (int i = 0; i < configuredLines.length; i++) {
            lineLocks[i] = new Object();
            lines[i] = new LineControl(configuredLines[i], lineGroup.getBaseDirectory());
            lines[i].initialise();
        }

        gui = new Gui(lines, this);
        System.out.println("Server started on : " + new Date());
        serverState = ON;
        importer = new Importer(lineGroup.getReadCycle());
        importer.init();
        new Timer(this, lineGroup.getReadCycle() * 60 * 1000);
    }

    public void setServerState(final int newState) {
        serverState = newState;
    }

    private void reboot() {
        boolean active = false;
        for (int i = 0; i < activity.length; i++) {
            active |= activity[i];
        }

        if (!active) {
            if (serverState == RESTART) {
                gui.setVisible(false);
                startServer();
            } else {
                System.out.println("Shutting down..");
                System.exit(0);
            }
        }
    }

    public void fireLine(final int lineNumber, final FileEvent[] events) {
        if (serverState != ON) {
            reboot();
        }


        synchronized (lineLocks[lineNumber]) {
            activity[lineNumber] = true;
            try {
                lines[lineNumber].sendToSocket(events);
            } catch (final Throwable e) {
                e.printStackTrace();
            }
            activity[lineNumber] = false;
        }
        if (serverState != ON) {
            reboot();
        }
    }

    public void fireUpdate(final int lineNumber) {
        if (serverState != ON) {
            reboot();
        }
        synchronized (lineLocks[lineNumber]) {
            activity[lineNumber] = true;
            try {
                lines[lineNumber].sendUpdate();
            } catch (final Throwable e) {
                e.printStackTrace();
            }
            activity[lineNumber] = false;
        }
        if (serverState != ON) {
            reboot();
        }
    }

    public void fire() {
        if (serverState != ON) {
            reboot();
        }

        for (int i = 0; i < lines.length; i++) {
            synchronized (lineLocks[i]) {
                importer.checkState();
                activity[i] = true;
                try {
                    lines[i].pollSocket();
                } catch (final Throwable e) {
                    e.printStackTrace();
                }
                activity[i] = false;
            }
        }

        if (serverState != ON) {
            reboot();
        }
    }

    public String getName() {
        return "CisServer";
    }

    public static void main(final String[] args) throws Exception {
        ConnectionFactory.resetConnection(args[0]);
        final CmsServer server = new CmsServer();
        server.startServer();
    }
}
