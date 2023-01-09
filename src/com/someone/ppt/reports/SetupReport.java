package com.someone.ppt.reports;

import com.someone.db.*;
import dori.jasper.engine.*;

import java.util.*;

public class SetupReport {

    private String packhouseName;

    public SetupReport(final String packhouseName) {
        this.packhouseName = packhouseName;
    }

    public void print() {
        final Map parameters = new HashMap();
        parameters.put("PakhuisNaam", packhouseName);

        try {
            JasperFillManager.fillReportToFile("config/setup.jasper", parameters, ConnectionFactory.getConnection());
            JasperPrintManager.printReport("config/setup.jrprint", true);
        } catch (final JRException e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {
        final SetupReport test = new SetupReport("2");
        test.print();
    }
}
