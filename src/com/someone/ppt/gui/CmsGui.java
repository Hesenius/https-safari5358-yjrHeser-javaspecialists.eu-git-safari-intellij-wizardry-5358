package com.someone.ppt.gui;

import com.someone.db.*;
import com.someone.ppt.gui.db.*;
import com.someone.ppt.gui.line.*;
import com.someone.ppt.gui.packhouse.*;
import com.someone.ppt.gui.printer.*;
import com.someone.ppt.models.*;
import com.someone.util.*;
import com.someone.util.Timer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CmsGui implements Tickable {
    private CmsFrame dbGui;
    private LineGui lineGui;
    private CmsFrame packhouseBuilder;
    private PrinterGui printerGui;
    private Packhouse packhouse;
    private long lastAction;
    private int timeout;
    private int lastGui;
    private boolean passwordOn;

    private CmsGui(final String packhouseName, final String timeoutString) {
        // log in user
        loadData(new Packhouse(packhouseName));

        // create the GUI with the Line GUI visible
        initGui(CmsFrame.LINE_GUI);

        lastAction = new Date().getTime();
        timeout = Integer.parseInt(timeoutString) * 1000;
        new Timer(this, 1000);
        lastGui = 0;
    }

    public void initGui(final int visibleGui) {
        packhouseBuilder = new PackhouseBuilder(this, packhouse);
        dbGui = new DBGui(this);
        printerGui = new PrinterGui(this);
        lineGui = new LineGui(this, packhouse);
        lineGui.initGui();

        switch (visibleGui) {
            case CmsFrame.PACKHOUSE_GUI:
                packhouseBuilder.setVisible(true);
                break;
            case CmsFrame.DB_GUI:
                dbGui.setVisible(true);
                break;
            case CmsFrame.LINE_GUI:
                lineGui.setVisible(true);
                break;
            case CmsFrame.PRINTER_GUI:
                printerGui.setVisible(true);
                break;

            default:
                break;
        }

        lineGui.getToolkit().addAWTEventListener(event -> lastAction = new Date().getTime(), AWTEvent.MOUSE_EVENT_MASK);
    }

    public void loadData(final Packhouse packhouse) {
        this.packhouse = packhouse;
        packhouse.loadLayout();
        packhouse.loadTemplates();
    }

    public void showLineConfiguration() {
        lineGui.setVisible(true);
    }

    public void showPackhouseBuilder() {
        packhouseBuilder.setVisible(true);
    }

    public void showDbGui() {
        dbGui.setVisible(true);
    }

    public void showPrinterConfiguration() {
        printerGui.setVisible(true);
    }

    public static void main(final String[] args) {
        ConnectionFactory.resetConnection(args[0]);
        new CmsGui(args[1], args[2]);
    }

    public synchronized void fire() {
        if (passwordOn) {
            return;
        }

        if ((new Date().getTime() - lastAction) > timeout) {


            if (packhouseBuilder.isVisible()) {
                packhouseBuilder.setVisible(false);
                lastGui = CmsFrame.PACKHOUSE_GUI;
            } else if (dbGui.isVisible()) {
                lastGui = CmsFrame.DB_GUI;
                dbGui.setVisible(false);
            } else if (printerGui.isVisible()) {
                lastGui = CmsFrame.PRINTER_GUI;
                printerGui.setVisible(false);
            } else if (lineGui.isVisible()) {
                lineGui.setVisible(false);
                lastGui = CmsFrame.LINE_GUI;
            }

            final PasswordFrame passer = new PasswordFrame(this);
            passwordOn = true;
            passer.setVisible(true);
        }
    }

    public void writeAccess() {
        if (lastGui == CmsFrame.PACKHOUSE_GUI) {
            packhouseBuilder.setVisible(true);
        } else if (lastGui == CmsFrame.DB_GUI) {
            dbGui.setVisible(true);
        } else if (lastGui == CmsFrame.PRINTER_GUI) {
            printerGui.setVisible(true);
        } else if (lastGui == CmsFrame.LINE_GUI) {
            lineGui.setVisible(true);
        }
        lastGui = 0;
        lastAction = new Date().getTime();
        passwordOn = false;

    }

    public void readAccess() {
        if (lastGui == CmsFrame.PACKHOUSE_GUI) {
            packhouseBuilder.setVisible(true);
        } else if (lastGui == CmsFrame.DB_GUI) {
            dbGui.setVisible(true);
        } else if (lastGui == CmsFrame.PRINTER_GUI) {
            printerGui.setVisible(true);
        } else if (lastGui == CmsFrame.LINE_GUI) {
            lineGui.setVisible(true);
        }
        lastGui = 0;
        lastAction = new Date().getTime();
        passwordOn = false;

    }

    public String getName() {
        return "CMS Gui";
    }

    /**
     * Close connections and perform other pre-close operations
     * and shut down the program.
     */
    public void shutDownProgram() {

        // Close connection to database
        ConnectionFactory.closeConnection();

        // Save 'hide status' information
        try {
            FruitspecTableModel.saveHideColumns();
        } catch (final java.io.IOException exc) {
            // do nothing
        }

        // Shut down the program
        System.exit(0);

    }

}
