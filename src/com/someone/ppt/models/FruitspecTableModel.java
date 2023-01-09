package com.someone.ppt.models;

import com.someone.db.*;
import com.someone.db.models.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class FruitspecTableModel extends AbstractTableModel {
    static public String[] columnNames; // Column names for the items in the "Hide" menu (not including "All" and "None")
    static private JComboBox[] modelValues;
    static private boolean[] updateAll;
    static private boolean[] everyWhere;
    static private boolean[] hidden; // Specifies the 'hide status' for each column
    static private int sizeCountIndex;
    static private int printCountIndex;
    static private int barCodeIndex;
    static private int[] maxSize;

    static {
        ConnectionFactory.getConnection();
        columnNames = new String[Layout.fields.length - 2 +
            FruitSpec.fields.length];

        int count = 0;

        // do not use PackhouseName and TemplateName fields (-2)
        // Get the first three fields from Layout
        for (int i = 2; i < Layout.fields.length; i++) {
            columnNames[count++] = Layout.fields[i];
        }

        // Get the remaining 24 fields from FruitSpec
        for (int i = 0; i < FruitSpec.fields.length; i++) {
            columnNames[count++] = FruitSpec.fields[i];
        }

        modelValues = new JComboBox[FruitSpec.fields.length];

        int index = 0;
        index = FruitSpec.findIndex("Shift");
        modelValues[index] = new JComboBox(new String[]{"D", "N"});

        index = FruitSpec.findIndex("Commodity");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "GTIN", "Commodity", "CommodityDesc"));

        index = FruitSpec.findIndex("Variety");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "GTIN", "Variety", "VarietyDesc"));

        index = FruitSpec.findIndex("Mark");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "GTIN", "Mark", "MarkDesc"));

        index = FruitSpec.findIndex("Pool");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "Pool", "PoolID"));

        index = FruitSpec.findIndex("Grade");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "GTIN", "Grade"));

        index = FruitSpec.findIndex("Pack");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "GTIN", "Pack", "PackDesc"));

        index = FruitSpec.findIndex("TargetMarket");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "TargetMarket", "TargetMarket", "TargetMarketDesc"));

        index = FruitSpec.findIndex("SizeCount");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                "GTIN", "SizeCount", true, "SortSequence"));

        index = FruitSpec.findIndex("PrintCount");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                "GTIN", "SizeCount", true, "SortSequence"));
        modelValues[index].setEditable(true);

        index = FruitSpec.findIndex("Remark1");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "Remark", "RemarkDesc"));
        modelValues[index].setEditable(true);

        index = FruitSpec.findIndex("Remark2");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "Remark", "RemarkDesc"));
        modelValues[index].setEditable(true);

        index = FruitSpec.findIndex("Remark3");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "Remark", "RemarkDesc"));
        modelValues[index].setEditable(true);

        index = FruitSpec.findIndex("Organization");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "GTIN", "Organisation", "OrganisationDesc"));

        index = FruitSpec.findIndex("Country");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "GTIN", "Country"));


        index = FruitSpec.findIndex("PUC");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "Grower", "PUC", "GrowerID"));
        modelValues[index].setEditable(true);

        index = FruitSpec.findIndex("GrowerID");
        modelValues[index] = new JComboBox(
            new DatabaseComboModel(
                ConnectionFactory.getConnection(),
                "Grower", "GrowerID", "PUC"));
        modelValues[index].setEditable(true);


        setHideColumns();
        setUpdateAllColumns();
        setEveryWhereColumns();
        setMaxSizes();

        sizeCountIndex = 3 + FruitSpec.findIndex("SizeCount");
        printCountIndex = 3 + FruitSpec.findIndex("PrintCount");
        barCodeIndex = 3;
    }

    private String[][] data;
    private int rowcount;
    private int[] rowdata;

    public static int findIndex(final String columnName) {
        int index = 0;

        while (index < columnNames.length) {
            if (columnName.equalsIgnoreCase(columnNames[index])) {
                break;
            }
            index++;
        }

        if (index == columnNames.length) {
            return -1;
        } else {
            return index;
        }
    }

    public FruitspecTableModel(final String[][] data, final int[] dropIndexes,
                               final int[] activeDrops) {
        super();
        setData(data, dropIndexes, activeDrops);
    }

    private static void setMaxSizes() {
        maxSize = new int[columnNames.length];
        Arrays.fill(maxSize, 0);
        maxSize[0] = 3; // Drop
        maxSize[1] = 3; // Table
        maxSize[2] = 3; // Station

        int index = FruitSpec.findIndex("BarCode");
        maxSize[index + 3] = 4;
        index = FruitSpec.findIndex("Commodity");
        maxSize[index + 3] = 2;
        index = FruitSpec.findIndex("Variety");
        maxSize[index + 3] = 3;
        index = FruitSpec.findIndex("Mark");
        maxSize[index + 3] = 5;
        index = FruitSpec.findIndex("Grade");
        maxSize[index + 3] = 4;
        index = FruitSpec.findIndex("Pack");
        maxSize[index + 3] = 4;
        index = FruitSpec.findIndex("TargetMarket");
        maxSize[index + 3] = 2;
        index = FruitSpec.findIndex("SizeCount");
        maxSize[index + 3] = 5;
        index = FruitSpec.findIndex("PrintCount");
        maxSize[index + 3] = 5;
        index = FruitSpec.findIndex("PUC");
        maxSize[index + 3] = 7;
        index = FruitSpec.findIndex("PickingReference");
        maxSize[index + 3] = 4;
        index = FruitSpec.findIndex("RunNumber");
        maxSize[index + 3] = 5;
        index = FruitSpec.findIndex("Country");
        maxSize[index + 3] = 2;
        index = FruitSpec.findIndex("Shift");
        maxSize[index + 3] = 2;
        index = FruitSpec.findIndex("GrowerID");
        maxSize[index + 3] = 5;
        index = FruitSpec.findIndex("DateCode");
        maxSize[index + 3] = 4;
        index = FruitSpec.findIndex("Organization");
        maxSize[index + 3] = 2;
        index = FruitSpec.findIndex("Pool");
        maxSize[index + 3] = 2;
    }

    private static void setEveryWhereColumns() {
        everyWhere = new boolean[columnNames.length];

        int index;
        index = FruitSpec.findIndex("PUC");
        everyWhere[index + 3] = true;
        index = FruitSpec.findIndex("GrowerID");
        everyWhere[index + 3] = true;
        index = FruitSpec.findIndex("RunNumber");
        everyWhere[index + 3] = true;
        index = FruitSpec.findIndex("Organization");
        everyWhere[index + 3] = true;
        index = FruitSpec.findIndex("Country");
        everyWhere[index + 3] = true;
        index = FruitSpec.findIndex("DateCode");
        everyWhere[index + 3] = true;
        index = FruitSpec.findIndex("PackhouseID");
        everyWhere[index + 3] = true;
    }

    private static void setUpdateAllColumns() {
        updateAll = new boolean[columnNames.length];

        int index = FruitSpec.findIndex("Commodity");
        updateAll[index + 3] = true;
        index = FruitSpec.findIndex("Shift");
        updateAll[index + 3] = true;
    }

    private static void setHideColumns() {

        hidden = new boolean[columnNames.length];

        try {

            loadHideColumns();

        } catch (final IOException exc) {

            hidden[0] = true; // DropName
            hidden[1] = true; // TableName
            hidden[2] = true; // StationName

            int index = FruitSpec.findIndex("Pool");
            hidden[index + 3] = true;
            index = FruitSpec.findIndex("PackhouseId");
            hidden[index + 3] = true;
            index = FruitSpec.findIndex("Remark2");
            hidden[index + 3] = true;
            index = FruitSpec.findIndex("Remark3");
            hidden[index + 3] = true;
            index = FruitSpec.findIndex("GrowerID");
            hidden[index + 3] = true;
            index = FruitSpec.findIndex("Inventory");
            hidden[index + 3] = true;

        }
    }

    /**
     * Retrieve the 'hide status' information from the file.
     */
    private static void loadHideColumns() throws IOException {

        // Open the file storing the records of the 'hide status' of the columns
        final DataInputStream fileIn = new DataInputStream(
            new BufferedInputStream(new FileInputStream("config/hide.dat")));

        // Retrieve the 'hide status' information from the file
        for (int i = 0; i < columnNames.length; i++) {
            hidden[i] = fileIn.readBoolean();
        }

        // Close the file
        fileIn.close();

    }

    /**
     * Save the 'hide status' information to the file.
     */
    public static void saveHideColumns() throws IOException {

        // Open the file to store the 'hide status' information
        final DataOutputStream fileOut = new DataOutputStream(
            new BufferedOutputStream(new FileOutputStream("config/hide.dat")));

        // Save the 'hide status' information to the file
        for (int i = 0; i < columnNames.length; i++) {
            fileOut.writeBoolean(hidden[i]);
        }

        // Close the file
        fileOut.close();

    }


    public static int getMaxSize(final int column) {
        return maxSize[column];
    }

    public static void resetComboBoxes() {
        if (modelValues == null) {
            return;
        }

        for (int i = 0; i < modelValues.length; i++) {
            if ((modelValues[i] == null) ||
                !(modelValues[i].getModel() instanceof DatabaseComboModel)) {
                continue;
            }

            modelValues[i].validate();
            modelValues[i].updateUI();
        }
    }

    public static JComboBox getColumnValues(final int column) {
        if (column < 4) {
            return null;
        }

        final int index = FruitSpec.findIndex(columnNames[column]);

        if (index != -1) {
            return modelValues[index];
        } else {
            return null;
        }
    }

    public static void setUpdateAll(final int column, final boolean value) {
        synchronized (FruitspecTableModel.class) {
            updateAll[column] = value;
        }
    }

    public static void setHidden(final int column, final boolean value) {
        synchronized (FruitspecTableModel.class) {
            hidden[column] = value;
        }
    }

    public static boolean getEveryWhere(final int column) {
        synchronized (FruitspecTableModel.class) {
            return everyWhere[column];
        }
    }

    public static void setEveryWhere(final int column, final boolean value) {
        synchronized (FruitspecTableModel.class) {
            everyWhere[column] = value;
        }
    }

    public static boolean getUpdateAll(final int column) {
        synchronized (FruitspecTableModel.class) {
            return updateAll[column];
        }
    }

    public static boolean getHidden(final int column) {
        synchronized (FruitspecTableModel.class) {
            return hidden[column];
        }
    }

    public static String[] getColumnData(final int column) {
        if (column < 4) {
            return null;
        }

        final int index = FruitSpec.findIndex(columnNames[column]);

        if ((index > 0) && (modelValues[index] != null)) {
            return ((DatabaseComboModel) modelValues[index].getModel()).getData();
        } else {
            return null;
        }
    }

    public void setData(final String[][] data, final int[] dropIndexes, final int[] activeDrops) {
        this.data = data;
        rowcount = 0;

        for (int i = 0; i < activeDrops.length; i++) {
            rowcount += (dropIndexes[activeDrops[i] + 1] - dropIndexes[activeDrops[i]]);
        }

        rowdata = new int[rowcount];

        int counter = 0;

        for (int i = 0; i < activeDrops.length; i++) {
            for (int j = dropIndexes[activeDrops[i]];
                 j < dropIndexes[activeDrops[i] + 1];
                 j++) {
                rowdata[counter++] = j;
            }
        }
    }

    public int getRowCount() {
        return rowcount;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if (rowIndex >= rowcount) {
            return "INVALID ROW INDEX " + rowIndex;
        } else if (columnIndex == barCodeIndex) {
            if ((data[rowdata[rowIndex]][columnIndex + 2] == null) ||
                data[rowdata[rowIndex]][columnIndex + 2].equals("")) {
                data[rowdata[rowIndex]][columnIndex + 2] = "" +
                    data[rowdata[rowIndex]][2] +
                    Integer.parseInt(
                        data[rowdata[rowIndex]][3]) +
                    Integer.parseInt(
                        data[rowdata[rowIndex]][4]);
            }

            final String dataAt = data[rowdata[rowIndex]][columnIndex + 2];

            return dataAt;
        } else {
            String dataAt = "";

            try {
                dataAt = data[rowdata[rowIndex]][columnIndex + 2];
            } catch (final RuntimeException e) {
                System.out.println("indexout: " + rowIndex + ", " +
                    columnIndex);
            }

            if ("".equals(dataAt)) {
                return "0";
            } else {
                return dataAt;
            }
        }
    }

    public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
        try {
            if (rowIndex >= rowcount) {
                System.out.println("INVALID ROW INDEX " + rowIndex);
            } else {
                if (columnIndex == sizeCountIndex) {
                    data[rowdata[rowIndex]][sizeCountIndex + 2] = (String) value;
                    data[rowdata[rowIndex]][printCountIndex + 2] = "count:" + (String) value;
                    fireTableCellUpdated(rowIndex, sizeCountIndex + 2);
                    fireTableCellUpdated(rowIndex, printCountIndex + 2);
                } else if (data[rowdata[rowIndex]][columnIndex + 2] == null) {
                    data[rowdata[rowIndex]][columnIndex + 2] = (String) value;
                    fireTableCellUpdated(rowIndex, columnIndex + 2);
                } else if (!data[rowdata[rowIndex]][columnIndex + 2].equals(
                    value)) {
                    data[rowdata[rowIndex]][columnIndex + 2] = (String) value;
                    fireTableCellUpdated(rowIndex, columnIndex + 2);
                }
            }
        } catch (final RuntimeException e) {
            System.out.println("out of bounds: " + rowIndex + "(r) " +
                columnIndex + "(c) " + value);
        }
    }

    public String getColumnName(final int col) {
        return columnNames[col];
    }

    public boolean isCellEditable(final int row, final int col) {
        if (col < 3) {
            return false;
        } else {
            return true;
        }
    }

    private static class PopupComboBox extends JComboBox
        implements JComboBox.KeySelectionManager {
        private String searchFor;
        private long lap;

        /**
         * Constructor for EditableComboBox.
         *
         * @param aModel
         */
        public PopupComboBox(final ComboBoxModel aModel) {
            super(aModel);
            final KeyListener[] lis = getKeyListeners();
            for (int i = 0; i < lis.length; i++) {
                removeKeyListener(lis[i]);
            }
            setKeySelectionManager(this);
        }

        public int selectionForKey(final char aKey, final ComboBoxModel aModel) {
            final long now = new java.util.Date().getTime();

            if ((lap + 500) < now) {
                searchFor = "" + aKey;
            }

            lap = now;

            String current;

            for (int i = 0; i < aModel.getSize(); i++) {
                current = aModel.getElementAt(i).toString().toLowerCase();

                if (current.startsWith(searchFor)) {
                    return i;
                }
            }

            return -1;
        }
    }
}
