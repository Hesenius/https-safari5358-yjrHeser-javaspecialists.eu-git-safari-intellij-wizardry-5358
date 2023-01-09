package com.someone.ppt.gui.reporting;

import com.someone.ppt.gui.*;
import com.someone.ppt.reports.*;

import javax.swing.*;
import java.awt.event.*;

public class ReportGui extends CmsFrame {
    private String packhouseName;

    public ReportGui(final String packhouseName) {
        super(null, "Reporting Gui : " + packhouseName, CmsFrame.REPORT_GUI);
        this.packhouseName = packhouseName;
        defineReportMenu();
    }

    private void defineReportMenu() {
        JMenuItem item;
        final JMenu menu;
        menu = new JMenu("Reports");
        menu.setMnemonic('r');
        item = new JMenuItem("Print Setup");
        item.setMnemonic('s');
        item.addActionListener(this::printSetup);
        menu.add(item);

        item = new JMenuItem("Report Chooser");
        item.setMnemonic('c');
        item.addActionListener(this::reportChooser);
        menu.add(item);

        final JMenu subMenu = new JMenu("Current Line");

        item = new JMenuItem("Mass Summary");
        item.setMnemonic('m');
        item.addActionListener(e -> {
            final WeightReport tester = new WeightReport(
                packhouseName);
            tester.show();
        });
        subMenu.add(item);

        item = new JMenuItem("Updating Mass Summary");
        item.setMnemonic('u');
        item.addActionListener(e -> {
            final WeightReportUpdater tester = new WeightReportUpdater(
                packhouseName);
            tester.show();
        });
        subMenu.add(item);

        item = new JMenuItem("Print Mass Summary");
        item.addActionListener(e -> {
            final WeightReport tester = new WeightReport(
                packhouseName);
            tester.print();
        });
        subMenu.add(item);

        item = new JMenuItem("CMS Summary");
        item.setMnemonic('c');
        item.addActionListener(e -> {
            final SummaryReport tester = new SummaryReport(
                packhouseName);
            tester.show();
        });
        subMenu.add(item);

        item = new JMenuItem("Print CMS Summary");
        item.addActionListener(e -> {
            final SummaryReport tester = new SummaryReport(
                packhouseName);
            tester.print();
        });
        subMenu.add(item);

        item = new JMenuItem("Pack Summary");
        item.setMnemonic('p');
        item.addActionListener(e -> {
            final PackSummaryReport tester = new PackSummaryReport(
                packhouseName);
            tester.show();
        });
        subMenu.add(item);

        item = new JMenuItem("Print Pack Summary");
        item.addActionListener(e -> {
            final PackSummaryReport tester = new PackSummaryReport(
                packhouseName);
            tester.print();
        });
        subMenu.add(item);
        menuBar.add(menu);
    }

    private void reportChooser(final ActionEvent e) {
        final ReportDetailFrame frame = new ReportDetailFrame(
            "Report Chooser");
        frame.setVisible(true);
    }

    private void printSetup(final ActionEvent e) {
        final SetupReport printer = new SetupReport(
            packhouseName);
        printer.print();
    }

}
