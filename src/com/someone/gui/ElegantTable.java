package com.someone.gui;

import com.someone.ppt.models.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class ElegantTable extends JTable {
    public ElegantTable(final TableModel dm) {
        super(dm);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        setDragEnabled(false);

        initGui();
    }

    public void initGui() {
        if (dataModel instanceof TableSorter) {

            final TableSorter sorter = (TableSorter) dataModel;

            if (!sorter.canUpdate()) {
                setBackground(Color.LIGHT_GRAY);
            }

            if (sorter.getModel() instanceof FruitspecTableModel) {
                initFruitSpecColumnSizes();
            }
        } else {
            initColumnSizes();
        }
    }

    private void initColumnSizes() {
        for (int i = 0; i < getColumnCount(); i++) {
            int width = 0;
            final String[] rowData = new String[dataModel.getRowCount() + 1];
            rowData[0] = (String) columnModel.getColumn(i).getHeaderValue();

            for (int j = 0; j < dataModel.getRowCount(); j++) {
                rowData[j + 1] = (String) dataModel.getValueAt(j, i);
            }

            width = findMaxWidth(rowData);
            columnModel.getColumn(i).setMinWidth(width);
            columnModel.getColumn(i).setMaxWidth(width);
        }
    }

    public void initFruitSpecColumnSizes() {
        String[] rowData;
        int width;
        final String bigString = "88888888888888888888888888888888888888888";
//        for (int i = 0; i < 3; i++) {
//            if (FruitspecTableModel.getHidden(i)) {
//                columnModel.getColumn(i).setMinWidth(0);
//                columnModel.getColumn(i).setMaxWidth(0);
//            } else {
//                width      = 0;
//                rowData    = new String[dataModel.getRowCount() + 1];
//                rowData[0] = "123";
//
//                for (int j = 0; j < dataModel.getRowCount(); j++) {
//                    rowData[j + 1] = (String) dataModel.getValueAt(j, i);
//                }
//
//                width = findMaxWidth(rowData);
//                columnModel.getColumn(i).setMinWidth(width);
//                columnModel.getColumn(i).setMaxWidth(width);
//            }
//        }

        for (int i = 0; i < FruitspecTableModel.columnNames.length; i++) {
            width = 0;

            if (FruitspecTableModel.getHidden(i)) {
                columnModel.getColumn(i).setMinWidth(0);
                columnModel.getColumn(i).setMaxWidth(0);
            } else {
                String[] data = null;
                try {
                    data = FruitspecTableModel.getColumnData(i);
                } catch (final RuntimeException e) {
                }
                if (data == null) {
                    data = new String[0];
                }
                rowData = new String[data.length + 1];
                rowData[0] = "1234567";
                final int size;
                if ((size = FruitspecTableModel.getMaxSize(i)) != 0) {
                    rowData[0] = bigString.substring(1, size);
                }
//                (String) columnModel.getColumn(i).getHeaderValue();

                for (int j = 0; j < data.length; j++) {
                    rowData[j + 1] = data[j];
                }

                width = findMaxWidth(rowData) +
                    (GuiTools.getCharWidth() * 2);
                columnModel.getColumn(i).setMinWidth(0);
                columnModel.getColumn(i).setPreferredWidth(width);

                if (columnModel.getColumn(i).getMaxWidth() == 0) {
                    columnModel.getColumn(i).setMaxWidth(10000);
                }
//                columnModel.getColumn(i).setMaxWidth(width);
            }
        }

//        for (int i = 12; i < FruitspecTableModel.columnNames.length; i++) {
//            if (FruitspecTableModel.getHidden(i)) {
//                columnModel.getColumn(i).setMinWidth(0);
//                columnModel.getColumn(i).setMaxWidth(0);
//          } else {
//                width      = 0;
//                rowData    = new String[dataModel.getRowCount() + 1];
//                rowData[0] = "1234567";
//                int size;
//                if ((size = FruitspecTableModel.getMaxSize(i)) != 0) {
//                	rowData[0] = bigString.substring(1, size);
//                }
//
//                for (int j = 0; j < dataModel.getRowCount(); j++) {
//                    rowData[j + 1] = (String) dataModel.getValueAt(j, i);
//                }
//
//                width = findMaxWidth(rowData);
//                columnModel.getColumn(i).setMinWidth(width);
//                columnModel.getColumn(i).setMaxWidth(width);
//            }
//        }
    }

    private int findMaxWidth(final String[] columnNames) {
        double pixels = 0.0;
        int maxpixels = 0;

        for (int t = 0; t < columnNames.length; t++) {
            if (columnNames[t] == null) {
                continue;
            }

            pixels = GuiTools.getCharWidth();
            ;
            pixels = pixels * columnNames[t].length();
            pixels = pixels - (pixels * .25);

            if (pixels > maxpixels) {
                maxpixels = (int) pixels;
            }
        }

        return maxpixels;
    }
}
