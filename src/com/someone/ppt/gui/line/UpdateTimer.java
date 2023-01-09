package com.someone.ppt.gui.line;

import com.someone.db.models.*;
import com.someone.util.*;

import java.sql.*;

public class UpdateTimer implements Tickable {
    private String packhouseName;
    private LineGui gui;

    public UpdateTimer(final String packhouseName, final LineGui gui) {
        this.packhouseName = packhouseName;
        this.gui = gui;
    }

    public void start() {
        final Timer timer = new Timer(this, 10000);
    }

    public String getName() {
        return "Update Timer";
    }

    public void fire() {
        try {
            final String query = "select packhouselines.LineName from packhousetemplates, packhouselines " +
                "where packhousetemplates.PackhouseName = 'XXX' and  " +
                "(packhouselines.LineName = packhousetemplates.lineName and " +
                "packhouselines.packhouseName = packhousetemplates.packhousename) " +
                " and Active = 'yes' and (status = 'updated' or status = 'changed')";


            final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(query, packhouseName, false);
            if (model.getRowCount() == 0) {
                gui.synchronizeDb();
                gui.cleanActivatingTitles();
            } else {

                gui.cleanActivatingTitles();
                for (int i = 0; i < model.getRowCount(); i++) {
                    final int line = Integer.parseInt((String) model.getValueAt(i, 0));
                    gui.setActivateTitle(line - 1);
                }


            }
            model.close();
        } catch (final SQLException e) {
        }
    }


}
