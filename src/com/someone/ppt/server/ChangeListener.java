package com.someone.ppt.server;

import com.someone.db.models.*;
import com.someone.util.*;

import java.sql.*;


public class ChangeListener implements Tickable {
    private CmsServer server;

    public ChangeListener(final CmsServer server) {
        new Timer(this, 1000);
        this.server = server;
    }

    public synchronized void fire() {
        final String query = "select distinct packhousename, linename from packhousetemplates where status='XXX'";
        final String keys = "updated";
        try {
            final ScrollableTableModel updatedLines = TableModelFactory.getResultSetTableModel(query, keys, false);
            final int updateCount = updatedLines.getRowCount();

            for (int i = 0; i < updateCount; i++) {

                final String packhouseName = (String) updatedLines.getValueAt(i, 0);
                final String lineName = (String) updatedLines.getValueAt(i, 1);

                final int lineId = server.getLineID(packhouseName, lineName);
                if (lineId != -1) {
                    server.fireUpdate(lineId);
                }
            }

            updatedLines.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }


        //select distinct packhousename, LineName from packhouselinesetup where status='updated';


        // check database for update
        // if update, for each line
        //   generate files
        //   generate file events
        //    server.fireLine(lineNumber-1, events)
    }

    /**
     * Return the name of the client.
     */
    public String getName() {
        return "CIS Change Listener";
    }

}
