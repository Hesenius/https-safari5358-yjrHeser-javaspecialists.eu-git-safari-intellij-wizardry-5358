package com.someone.ppt.gui;

import com.someone.db.*;
import com.someone.ppt.gui.reporting.*;

class ReportGuiEntrance {

    public ReportGuiEntrance() {
    }

    public static void main(final String[] args) {
        ConnectionFactory.resetConnection(args[0]);
        final ReportGui gui = new ReportGui(args[1]);
        gui.setVisible(true);
    }
}
