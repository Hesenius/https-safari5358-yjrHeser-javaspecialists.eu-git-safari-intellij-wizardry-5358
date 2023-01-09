package com.someone.ppt.reports;

import com.someone.db.models.*;
import com.someone.gui.*;
import com.someone.io.*;
import org.apache.tomcat.util.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.Date;
import java.util.*;

public class ReportDetailFrame extends JFrame {
    private static int frameHeight;
    private static int frameWidth;

    static {
        frameHeight = (GuiTools.getCharHeight() * 5) +
            (GuiTools.getCharHeight() * 25) +
            (GuiTools.getCharHeight() * 5) + 9;
        frameWidth = (int) (frameHeight * 0.80);
    }

    private JPanel topPanel;
    private JPanel mainPanel;
    private JPanel bottomPanel;
    private String[] rowNames;
    private String[] shortNames;
    private int charHeight;
    private Font labelFont;
    private FlowLayout leftFlowLayout;
    private JCheckBox[] boxes;
    private JTextField puc;
    private JTextField startDate;
    private JTextField endDate;
    private JTextField cartonField;
    private JRadioButton shift;
    private JRadioButton screen;
    private JRadioButton print;
    private String whereClause;
    private String titleString;
    private String dateInfo;
    private int cartons;
    private FastDateFormat formatter;
    private HashMap packhouseMap;
    private String dateClause;
    private int selected;

    public ReportDetailFrame(final String title)
        throws HeadlessException {
        super();
        formatter = new FastDateFormat(new SimpleDateFormat("yyyy-MM-dd kk:mm"));

        final ImageIcon icon = new ImageIcon(FileProxy.getURL("icons/home.gif"));
        this.setIconImage(icon.getImage());

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        charHeight = GuiTools.getCharHeight();
        labelFont = GuiTools.getLabelFont();
        leftFlowLayout = GuiTools.getLeftFlowLayout();
        setSize(frameWidth, frameHeight);

        setTitle("    " + title);
        getContentPane().setLayout(new BorderLayout());

        createTopPanel();
        getRows();

        createMainPanel();

        createBottomPanel();

        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        final JPanel dummy = new JPanel();
        dummy.add(bottomPanel);

        getContentPane().add(dummy, BorderLayout.SOUTH);

        validate();
        setLocation();
    }

    private void getRows() {
        final String query = "select distinct PackhouseName, LineName from packhouselayout order by packhouseName XXX";

        try {
            final ScrollableTableModel model = TableModelFactory.getResultSetTableModel(
                query, "", false);

            rowNames = new String[model.getRowCount()];
            shortNames = new String[model.getRowCount()];
            for (int i = 0; i < rowNames.length; i++) {
                shortNames[i] = "PH " + model.getValueAt(i, 0) + ": " + model.getValueAt(i, 1);
                rowNames[i] = "Packhouse " + model.getValueAt(i, 0) + ": " + model.getValueAt(i, 1);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }


    private void setLocation() {
        final Toolkit kit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = kit.getScreenSize();
        final GraphicsConfiguration config = getGraphicsConfiguration();
        final Insets insets = kit.getScreenInsets(config);
        final int width = screenSize.width -
            (insets.left + insets.right);
        final int height = screenSize.height -
            (insets.top + insets.bottom);

        final int left = (int) ((width - frameWidth) / 2);
        final int top = (int) ((height - frameHeight) / 2);

        setLocation(left, top);
    }

    private void doOK() {

        selected = 0;
        packhouseMap = new HashMap();
        final StringBuffer packhouses = new StringBuffer(100);
        final StringBuffer lines = new StringBuffer(100);

        try {
            cartons = Integer.parseInt(cartonField.getText());
        } catch (final RuntimeException e) {
            e.printStackTrace();
        }

        packhouses.append(" where (");
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i].isSelected()) {
                selected++;
                lines.append(shortNames[i]).append(", ");
                final int commaIndex = rowNames[i].indexOf(':');
                packhouses.append("(PackhouseName = '")
                    .append(rowNames[i].substring(10, commaIndex))
                    .append("' and LineName = '")
                    .append(rowNames[i].substring(commaIndex + 2))
                    .append("')")
                    .append(" or ");
                packhouseMap.put(rowNames[i].substring(10, commaIndex), rowNames[i].substring(10, commaIndex));
            }
        }


        packhouses.setLength(packhouses.length() - 4);
        packhouses.append(")");
        lines.setLength(lines.length() - 2);


        if (!puc.getText().equals("")) {
            packhouses
                .append(" and GrowerID = '")
                .append(puc.getText())
                .append("'");

            lines.append(", PUC = " + puc.getText());
        }

        final StringBuffer dateBuffer = new StringBuffer();

        if (shift.isSelected()) {
            try {
                dateInfo = startDate.getText() + " to " + endDate.getText() + " (shifts)";
                packhouses.append(" and (");
                dateBuffer.append(" and (");
                final Date shiftEnd = formatter.parse(endDate.getText());

                final String startDateStr = startDate.getText().substring(0, 10);
                final String startTimeStr = startDate.getText().substring(11);
                final String endTimeStr = endDate.getText().substring(11);

                Date start = formatter.parse(startDateStr + " " + startTimeStr);
                Date end = formatter.parse(startDateStr + " " + endTimeStr);

                do {

                    if (start.getTime() > end.getTime()) { // shift past midnight
                        end = new Date(end.getTime() + 24 * 60 * 60 * 1000);
                    } else {

                    }

                    end = new Date(end.getTime() + 24 * 60 * 60 * 1000);
                    start = new Date(start.getTime() + 24 * 60 * 60 * 1000);
                    packhouses
                        .append(" LMDate between '")
                        .append(formatter.format(start))
                        .append("' and '")
                        .append(formatter.format(end))
                        .append("' or ");

                    dateBuffer
                        .append(" LMDate between '")
                        .append(formatter.format(start))
                        .append("' and '")
                        .append(formatter.format(end))
                        .append("' or ");


                } while (end.getTime() <= shiftEnd.getTime());

                packhouses.setLength(packhouses.length() - 4);
                dateBuffer.setLength(packhouses.length() - 4);
                packhouses.append(" )");
                dateBuffer.append(" )");
            } catch (final ParseException e) {
                e.printStackTrace();
            }
        } else {
            dateInfo = startDate.getText() + " to " + endDate.getText() + " (period)";

            packhouses
                .append(" and LMDate between '")
                .append(startDate.getText())
                .append("' and '")
                .append(endDate.getText())
                .append("' ");

            dateBuffer
                .append(" and LMDate between '")
                .append(startDate.getText())
                .append("' and '")
                .append(endDate.getText())
                .append("' ");

        }

        if (screen.isSelected()) {
            System.out.println("Screen");
        } else {
            System.out.println("Print");
        }

        dateClause = dateBuffer.toString();
        whereClause = packhouses.toString();
        titleString = lines.toString();
    }


    private void createBottomPanel() {
        bottomPanel = new JPanel();

        bottomPanel.setPreferredSize(
            new Dimension(charHeight * 27, charHeight * 3));
        bottomPanel.setLayout(new FlowLayout());

        final Border bottomBorder = BorderFactory.createEtchedBorder();
        bottomPanel.setBorder(bottomBorder);

        final JPanel bottomFiller = new JPanel();

        final JButton cancel = new JButton("Cancel");
        final JButton mass = new JButton("Mass");
        final JButton cms = new JButton("Cms");
        final JButton pack = new JButton("Pack");
        final JButton line = new JButton("Line");

        cms.addActionListener(e -> {
            System.out.println("ppt");
            doOK();
            final SummaryReport report = new SummaryReport(dateInfo, titleString, whereClause);
            if (print.isSelected()) {
                report.print();
            } else {
                report.show();
            }
            ReportDetailFrame.this.dispose();
        });

        mass.addActionListener(e -> {
            doOK();
            final WeightReport report = new WeightReport(whereClause, titleString, dateInfo, cartons);
            if (print.isSelected()) {
                report.print();
            } else {
                report.show();
            }
            ReportDetailFrame.this.dispose();
        });

        pack.addActionListener(e -> {
            doOK();
            final PackSummaryReport report = new PackSummaryReport(whereClause, titleString, dateInfo);
            if (print.isSelected()) {
                report.print();
            } else {
                report.show();
            }
            ReportDetailFrame.this.dispose();
        });

        line.addActionListener(e -> {
            doOK();
            generateLineReportQueries();
            ReportDetailFrame.this.dispose();
        });

        cancel.addActionListener(e -> ReportDetailFrame.this.dispose());


        bottomFiller.add(cancel);
        bottomFiller.add(mass);
        bottomFiller.add(cms);
        bottomFiller.add(pack);
        bottomFiller.add(line);

        bottomPanel.add(bottomFiller);
    }

    private void generateLineReportQueries() {
        final String[][] data = new String[packhouseMap.size() + 1 + selected][4];
        final DecimalFormat decFormatter = new DecimalFormat("#0.000");
        try {
            ScrollableTableModel model;
            final String keys = " select count(*) from cartons where";
            StringBuffer queryBuffer;

            int count = 0;
            for (int i = 0; i < boxes.length; i++) {
                if (boxes[i].isSelected()) {
                    final int commaIndex = rowNames[i].indexOf(':');

                    queryBuffer = new StringBuffer(" XXX ");
                    queryBuffer.append("(PackhouseName = '")
                        .append(rowNames[i].substring(10, commaIndex))
                        .append("' and LineName = '")
                        .append(rowNames[i].substring(commaIndex + 2))
                        .append("') ")
                        .append(dateClause);

                    System.out.println(queryBuffer.toString());
                    model = TableModelFactory.getResultSetTableModel(
                        queryBuffer.toString(), keys, false);

                    data[count][0] = rowNames[i];
                    data[count][2] = "" + model.getValueAt(0, 0);
                    model.close();

                    queryBuffer = new StringBuffer(" XXX ");
                    queryBuffer.append("(PackhouseName = '")
                        .append(rowNames[i].substring(10, commaIndex))
                        .append("' and LineName = '")
                        .append(rowNames[i].substring(commaIndex + 2))
                        .append("') and (Pack = '') ")
                        .append(dateClause);

                    model = TableModelFactory.getResultSetTableModel(
                        queryBuffer.toString(), keys, false);

                    data[count][1] = "" + model.getValueAt(0, 0);
                    model.close();

                    count++;
                }
            }

            for (final Iterator it = packhouseMap.keySet().iterator(); it.hasNext(); ) {
                final String packhouse = (String) it.next();

                queryBuffer = new StringBuffer(" XXX ");
                queryBuffer.append("(PackhouseName = '")
                    .append(packhouse)
                    .append("') ")
                    .append(dateClause);

                model = TableModelFactory.getResultSetTableModel(
                    queryBuffer.toString(), keys, false);

                data[count][0] = "Packhouse " + packhouse;
                data[count][2] = "" + model.getValueAt(0, 0);
                model.close();

                queryBuffer = new StringBuffer(" XXX ");
                queryBuffer.append("(PackhouseName = '")
                    .append(packhouse)
                    .append("') and (Pack = '') ")
                    .append(dateClause);

                model = TableModelFactory.getResultSetTableModel(
                    queryBuffer.toString(), keys, false);

                data[count][1] = "" + model.getValueAt(0, 0);
                model.close();
                count++;
            }

            queryBuffer = new StringBuffer(" XXX ");
            queryBuffer.append(dateClause.substring(4));

            model = TableModelFactory.getResultSetTableModel(
                queryBuffer.toString(), keys, false);

            data[count][0] = "TAD";
            data[count][2] = "" + model.getValueAt(0, 0);
            model.close();

            queryBuffer = new StringBuffer(" XXX Pack = '' ");
            queryBuffer.append(dateClause);

            model = TableModelFactory.getResultSetTableModel(
                queryBuffer.toString(), keys, false);

            data[count][1] = "" + model.getValueAt(0, 0);
            model.close();
            count++;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < data.length; i++) {
            final int missed = Integer.parseInt(data[i][1]);
            final int total = Integer.parseInt(data[i][2]);
            double persentage = 0.0;
            try {
                if (missed != 0 && total != 0) {
                    persentage = (double) missed / ((double) total / 100);
                }
            } catch (final Exception e) {
            }
            data[i][3] = decFormatter.format(persentage);
        }

        final LineSummaryReport report = new LineSummaryReport(data, titleString, dateInfo);
        if (print.isSelected()) {
            report.print();
        } else {
            report.show();
        }

        // select count(*) from cartons where Line and packhouse
        // select count(*) from cartons where Line and packhouse and Pack='';

        // select count(*) from cartons where packhouse
        // select count(*) from cartons where packhouse and Pack='';

        // select count(*) from cartons
        // select count(*) from cartons where Pack='';
    }

    private void createMainPanel() {
        final int columns = rowNames.length + 4;

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(
            new Dimension(charHeight * 25, charHeight * 21));

        final JPanel gridPanel = new JPanel(new GridLayout(columns, 1, 0, 0));

        JPanel line = new JPanel(leftFlowLayout);
        JPanel labelPanel = new JPanel(leftFlowLayout);


        puc = new JTextField(13);
        startDate = new JTextField(formatter.format(new Date()), 13);
        endDate = new JTextField(formatter.format(new Date()), 13);
        cartonField = new JTextField("30", 8);


        boxes = new JCheckBox[rowNames.length];

        for (int i = 0; i < rowNames.length; i++) {
            line = new JPanel(leftFlowLayout);
            labelPanel = new JPanel(leftFlowLayout);
            labelPanel.setPreferredSize(
                new Dimension(200, (int) (labelFont.getSize() * 2)));

            labelPanel.add(new ElegantLabel(rowNames[i]));
            line.add(labelPanel);
            boxes[i] = new JCheckBox();
            line.add(boxes[i]);
            line.setPreferredSize(
                new Dimension(400, (int) (labelFont.getSize() * 2)));
            gridPanel.add(line);
        }

        JPanel space;
//		space = new JPanel();
//		gridPanel.add(space);


        line = new JPanel(leftFlowLayout);
        labelPanel = new JPanel(leftFlowLayout);
        labelPanel.add(new ElegantLabel("PUC"));
        line.add(labelPanel);
        line.add(puc);

        labelPanel = new JPanel(leftFlowLayout);
        labelPanel.add(new ElegantLabel("Cartons"));
        line.add(labelPanel);
        line.add(cartonField);

        gridPanel.add(line);


        line = new JPanel(leftFlowLayout);
        labelPanel = new JPanel(leftFlowLayout);
        labelPanel.add(new ElegantLabel("Start"));
        line.add(labelPanel);
        line.add(startDate);
        labelPanel = new JPanel(leftFlowLayout);
        labelPanel.add(new ElegantLabel("End"));
        line.add(labelPanel);
        line.add(endDate);
        gridPanel.add(line);


        space = new JPanel(leftFlowLayout);
        ButtonGroup group = new ButtonGroup();
        shift = new JRadioButton("Shift", false);
        group.add(shift);
        space.add(shift);
        final JRadioButton period = new JRadioButton("Period", true);
        group.add(period);
        space.add(period);
        gridPanel.add(space);

        space = new JPanel(leftFlowLayout);
        group = new ButtonGroup();
        screen = new JRadioButton("Screen", true);
        group.add(screen);
        space.add(screen);
        print = new JRadioButton("Print", false);
        group.add(print);
        space.add(print);
        gridPanel.add(space);

        final JPanel bla = new JPanel();
        bla.add(gridPanel);

        final JScrollPane scroller = new JScrollPane(bla,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setPreferredSize(
            new Dimension(charHeight * 27, charHeight * 28));
        mainPanel.add(scroller);
    }

    private void createTopPanel() {
        topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(charHeight * 5, charHeight * 2));
        topPanel.setBackground(Color.white);

        final Border topBorder = BorderFactory.createEtchedBorder();
        topPanel.setBorder(topBorder);
    }

    private class ElegantLabel extends JLabel {
        private ElegantLabel(final String text) {
            super(text);
            this.setFont(labelFont);
        }
    }

    public static void main(final String[] args) {
        final ReportDetailFrame frmae = new ReportDetailFrame("Report Detail");
        frmae.setVisible(true);
    }
}
