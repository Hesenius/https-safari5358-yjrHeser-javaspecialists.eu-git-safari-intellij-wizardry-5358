package com.someone.ppt.server;

class Importer {
    //    private String   directory;
//    private int      updateMinute;
//    private int      updateHour;
//    private Calendar calendar;

    public Importer(final int readCycle) {
        final int readCycle1 = readCycle;
    }

    public void init() {
        System.out.println("Automatic Importer is deactivated!!!!!");
//        calendar = Calendar.getInstance();
//
//        try {
//            String               query = "Select ImportDirectory, ImportTime from imports XXX";
//
//            ScrollableTableModel queryLine = TableModelFactory.getResultSetTableModel(
//                                                     query, "", false);
//            ResultSet            resultSet = queryLine.getResultSet();
//            resultSet.beforeFirst();
//            resultSet.next();
//
//            directory = resultSet.getString("ImportDirectory");
//
//            String updateTime = resultSet.getString("ImportTime");
//
//            System.out.println("time to import is: " + updateTime);
//            updateHour   = Integer.parseInt(updateTime.substring(0, 2));
//            updateMinute = Integer.parseInt(updateTime.substring(3, 5));
//
//            queryLine.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void checkState() {
//        try {
//            System.out.println(new Date());
//            calendar = Calendar.getInstance();
//
//            if ((updateHour == calendar.get(Calendar.HOUR_OF_DAY)) &&
//                    (calendar.get(Calendar.MINUTE) - updateMinute >= 0) &&
//                    ((calendar.get(Calendar.MINUTE) - updateMinute) <= readCycle)) {
//                importData();
//            } else {
//                ScrollableTableModel queryLine = TableModelFactory.getResultSetTableModel(
//                                                         "imports",
//                                                         "ForceImport", "yes");
//                ResultSet            resultSet = queryLine.getResultSet();
//                resultSet.beforeFirst();
//
//                if (resultSet.next()) {
//                    importData();
//                    resultSet.updateString("ForceImport", "no");
//                    resultSet.updateRow();
//                }
//
//                queryLine.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

//    private void importData() {
//		System.out.println("Automatic Importer is deactivated!!!!!");
//
//        //		import GTIN starts with GT
//        File   importDirectory = new File(directory);
//        File[] files = importDirectory.listFiles(new FilenameFilter() {
//            public boolean accept(File dir, String name) {
//                if (name.startsWith("GT")) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
//
//        if (files.length > 0) {
//            GtinParser gtParser = new GtinParser(files[0].getAbsolutePath());
//            gtParser.parse();
//            gtParser = null;
//            files[0].delete();
//        }
//
//        files = importDirectory.listFiles(new FilenameFilter() {
//            public boolean accept(File dir, String name) {
//                if (name.startsWith("TM")) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
//
//        if (files.length > 0) {
//            TargetMarketParser tmParser = new TargetMarketParser(
//                                                  files[0].getAbsolutePath());
//            tmParser.parse();
//            tmParser = null;
//            files[0].delete();
//        }
//
//        files = importDirectory.listFiles(new FilenameFilter() {
//            public boolean accept(File dir, String name) {
//                if (name.startsWith("grower")) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
//
//        if (files.length > 0) {
//            GrowerParser gParser = new GrowerParser(files[0].getAbsolutePath());
//            gParser.parse();
//            gParser = null;
//            files[0].delete();
//        }
//    }
}
