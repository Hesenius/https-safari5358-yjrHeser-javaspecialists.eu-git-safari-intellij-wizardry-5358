package com.someone.ppt.reports;

import com.someone.db.models.*;
import com.someone.gui.*;
import com.someone.util.*;
import org.apache.tomcat.util.*;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.Date;

public class WeightReportUpdater implements Tickable {
    private FastDateFormat formatter;
    private Date startDate;
    private Date endDate;
    private int cartons;
    private String packhouseName;
    private int minutes;
    private int period;
    private JFrame display;
    private JTable table;
    private JScrollPane scroller;

    private String[][] types; //PackType, Commodity, Count
    private String[][] data;

    public WeightReportUpdater(final String packhouseName) {
        this.packhouseName = packhouseName;
        formatter = new FastDateFormat(new SimpleDateFormat("yyyy-MM-dd kk:mm:ss"));
        display = new JFrame("Updating Mass Summary");
        display.addWindowListener(new WindowAdapter() {
            public void windowClosed(final WindowEvent e) {
                display.setVisible(false);
            }
        });

        table = new JTable();
        scroller = new JScrollPane(table);
        display.getContentPane().add(scroller);
        display.setVisible(true);

    }


    public void show() {
        showScreenDialog();
    }


    private void printScreen() {
        display.remove(scroller);
        display.setTitle("Updating Mass Summary " + new Date());
        table = new JTable(data, null);
        scroller = new JScrollPane(table);
        display.getContentPane().add(scroller);
        display.setState(JFrame.MAXIMIZED_BOTH);
        display.setVisible(true);

//		Map parameters = new HashMap();
//		parameters.put("PackhouseName", packhouseName);
//		parameters.put("Cartons", ""+cartons);
//		parameters.put("DateFrom", ""+shortFormat.format(startDate));
//		parameters.put("DateTo", ""+shortFormat.format(endDate));
//
//		try {
//			try {
//				File f = new File("config/colourmasssummary.html");
//				f.delete();
//			} catch (Exception e) {
//			}
//
//			WeightReportDataSource source = new WeightReportDataSource(data);
//			JasperFillManager.fillReportToFile("config/colourmasssummary.jasper", parameters, source);
//			JasperExportManager.exportReportToHtmlFile("config/colourmasssummary.jrprint");
//
//
//			try {
//				Runtime.getRuntime().exec("explorer .\\config\\colourmasssummary.html");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		} catch (JRException e) {
//			e.printStackTrace();
//		}
    }

    private void showScreenDialog() {
        final String[] columnNames = {"Packhouse", "Cartons", "Period", "Update Minutes"};
        final DataInputFrame frame = new DataInputFrame(
            "Mass Summary UpLMDate",
            columnNames, false);
        final String[] data = {packhouseName, "30", "30", "5"};
        frame.setData(data);

        final Runnable modifier = () -> {
            if (frame.isModified()) {
                try {
                    final String[] inputs = frame.getNewData();
                    packhouseName = inputs[0];
                    cartons = Integer.parseInt(inputs[1]);
                    period = Integer.parseInt(inputs[2]);
                    minutes = Integer.parseInt(inputs[3]);

                    new com.someone.util.Timer(WeightReportUpdater.this, minutes * 60 * 1000);
                    fire();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else {
                // do nothing
            }
        };

        frame.setCloseAction(modifier);
        frame.setVisible(true);

    }


//	private void database() {
//		try {
//			String[] columns = new String[] {"ReportName" };
//			String[] keys = new String[] {"TestReport"};
//
//			ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
//			                                     "masssummary", columns, keys);
//			ResultSet            resultSet = model.getResultSet();
//
//			for (int i = 0; i < data.length; i++) {
//			    resultSet.moveToInsertRow();
//		        resultSet.updateString(1, "TestReport");
//		        resultSet.updateInt(2, i);
//
//				for (int j = 0; j < data[i].length; j++) {
//					if (data[i][j] == null) {
//				        resultSet.updateString(j+3, "");
//					} else {
//				        resultSet.updateString(j+3, data[i][j]);
//					}
//				}
//	            resultSet.insertRow();
//			}
//
//			model.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

    /**
     * Find the PackTypes, Commodity groups in the time frame.
     */
    private void getTypes() {
        ScrollableTableModel model = null;

        try {
            final String query =
                "select pack, commodity, sizecount, count(XXX) from cartons " +
                    " where PackhouseName ='" + packhouseName + "' " +

//			     " and LineName = '" +
//			    lineName + "' " +
                    " and LMDate between '" + formatter.format(startDate) +
                    "' and '" + formatter.format(endDate) + "' " +
                    " group by pack, commodity, sizecount";

            model = TableModelFactory.getResultSetTableModel(query, "*", false);

            types = new String[model.getRowCount()][];
            data = new String[cartons + 7][34];

            data[0][0] = "";
            data[0][1] = "";
            data[0][2] = "Std Mass";
            data[1][0] = "";
            data[1][1] = "Carton";
            data[1][2] = "Missed";


            data[data.length - 4][0] = "Average";
            data[data.length - 3][0] = "Total";
            data[data.length - 2][0] = "% below";
            data[data.length - 1][0] = "% above";
            data[data.length - 1][1] = null;
            data[data.length - 2][1] = null;
            data[data.length - 3][1] = null;
            data[data.length - 4][1] = null;

            for (int i = 0; i < cartons; i++) {
//				data[i+2][0] = "";
                data[i + 2][1] = "" + (i + 1);
            }

            for (int i = 0; i < types.length; i++) {
                types[i] = model.getRow(i);
            }

        } catch (final SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < types[i].length; j++) {
                types[i][j] = types[i][j].trim();
            }
        }

    }

    private void createColumn(final int typeIndex) {
        int standardWeight = 0;
        double upperLimit = 0;
        double lowerLimit = 0;
        final int dataIndex = (typeIndex + 1) * 2;
        final DecimalFormat decFormatter = new DecimalFormat("#.00");

        ScrollableTableModel model;

        if (!types[typeIndex][0].equals("")) {

            final String[] columns = new String[]{"PackID", "CommodityID"};
            final String[] keys = new String[]{
                types[typeIndex][0], types[typeIndex][1]
            };

            try {
                model = TableModelFactory.getResultSetTableModel(
                    "packprintsetup", columns, keys);

                standardWeight = Integer.parseInt((String) model.getValueAt(0, 2));
                upperLimit = Double.parseDouble((String) model.getValueAt(0, 3));
                ;
                lowerLimit = Double.parseDouble((String) model.getValueAt(0, 4));
                ;

                model.close();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            final String query =
                "select XXX from cartons where " +
                    " PackhouseName ='" + packhouseName + "'" +

//			    "' and LineName = '" +
//			    lineName + "' " +
                    " and pack =  '" + types[typeIndex][0] + "' and " +
                    " SizeCount = '" + types[typeIndex][2] + "' and " +
                    " LMDate between '" + formatter.format(startDate) +
                    "' and '" + formatter.format(endDate) + "' " +
                    " order by LMDate desc limit " + cartons;


            model = TableModelFactory.getResultSetTableModel(query, "weight", false);

            final ResultSet results = model.getResultSet();
            results.beforeFirst();

            int count = 0;
            double average = 0;
            int above = 0;
            int below = 0;
            final int number;
            int value;
            data[0][dataIndex + 1] = "0";
            data[1][dataIndex + 1] = "0";

            while (results.next()) {
                value = results.getInt("weight");
                average += value;

                int difference = value - standardWeight;
                data[count + 2][dataIndex + 1] = "0";

                if (difference > 0) { // test above

                    final double d = difference / ((double) (standardWeight / 100));
                    if (d > upperLimit) {
                        above++;
                        data[count + 2][dataIndex + 1] = "1";
                    }

                } else { //test below
                    difference = -difference;
                    final double d = difference / ((double) (standardWeight / 100));
                    if (d > lowerLimit) {
                        below++;
                        data[count + 2][dataIndex + 1] = "-1";
                    }
                }

                data[count + 2][dataIndex] = "" + value;
                count++;
            }

            number = count;

            while (count < cartons) {
                data[count + 2][dataIndex] = "0";
                count++;
            }

            data[data.length - 4][dataIndex + 1] = "0";
            data[data.length - 3][dataIndex + 1] = "0";
            data[data.length - 2][dataIndex + 1] = "0";
            data[data.length - 1][dataIndex + 1] = "0";


            if (!types[typeIndex][0].equals("")) {
                data[0][dataIndex] = "" + standardWeight;
                data[1][dataIndex] = types[typeIndex][0] + " " + types[typeIndex][2];
            } else {
                data[1][dataIndex] = "Missed";
            }
            //calculate above, below and extra column

            data[data.length - 4][dataIndex] = "" + decFormatter.format((average / number));
            data[data.length - 3][dataIndex] = "" + number;

            if (below != 0) {
                data[data.length - 2][dataIndex] = decFormatter.format(((double) below / ((double) (number))) * 100);
            } else {
                data[data.length - 2][dataIndex] = decFormatter.format(0);
            }

            if (above != 0) {
                data[data.length - 1][dataIndex] = decFormatter.format(((double) above / ((double) (number))) * 100);
            } else {
                data[data.length - 1][dataIndex] = decFormatter.format(0);
            }

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(final String[] args) {
        final WeightReportUpdater tester = new WeightReportUpdater("2");
        tester.show();
    }

    public void fire() {
        final Date now = new Date();
        final long nowTime = now.getTime();
        final long thenTime = nowTime - (60 * 1000 * period);


        startDate = new Date(thenTime);
        endDate = now;

        System.out.println("start: " + startDate);
        System.out.println("end: " + endDate);

        getTypes();
        for (int i = 0; i < types.length; i++) {
            createColumn(i);
        }

        printScreen();
    }

    public String getName() {
        return "WeightReportUpDater";
    }

}
