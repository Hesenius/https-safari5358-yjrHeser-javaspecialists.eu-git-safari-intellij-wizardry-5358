package com.someone.ppt.gui.line;

import com.someone.db.models.*;
import com.someone.gui.*;
import com.someone.ppt.cds.*;
import com.someone.ppt.models.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

class LinePane extends JPanel {
    JButton saveButton;
    private JToggleButton[] dropButtons;
    private FruitspecTableModel model;
    FruitSpecTable table;
    private LineSorter sorter;
    private JScrollPane northPane;
    private JPanel centerPanel;
    private String[][] data;
    private int dropCount;
    private int[] dropIndexes;
    private int lineIndex;
    private Packhouse packhouse;
    private LineGui lineGui;
    private JLabel statusLabel;
    private Random r;

    public LinePane(final int lineIndex, final Packhouse packhouse, final LineGui parent) {
        this.lineIndex = lineIndex;
        this.packhouse = packhouse;
        this.lineGui = parent;
        r = new Random();

    }

    public void updateModel() {
        data = packhouse.getLineData(lineIndex);
        initGui();
    }

    private void initGui() {
        if (northPane != null) {
            remove(northPane);
            remove(centerPanel);
        } else {
            setLayout(new BorderLayout());
            setSize(400, 400);
        }

        northPane = initNorth();
        add(northPane, BorderLayout.NORTH);

        final int[] usedDrops = new int[dropCount];

        for (int i = 0; i < dropCount; i++) {
            usedDrops[i] = i;
        }

        model = new FruitspecTableModel(data, dropIndexes, usedDrops);

        sorter = new LineSorter(model);
        table = new FruitSpecTable(sorter);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter.addMouseListenerToHeaderInTable(table);

        final JScrollPane tablePanel = new JScrollPane(table);

        TableColumn col;

        for (int i = 4; i < FruitspecTableModel.columnNames.length; i++) {
            final int column = i;
            col = table.getColumnModel().getColumn(column);

            if (FruitspecTableModel.getColumnValues(column) != null) {
                final JComboBox orgBox = FruitspecTableModel.getColumnValues(column);
                final SteppedComboBox box = new SteppedComboBox(orgBox.getModel());
                box.setEditable(orgBox.isEditable());
                col.setCellEditor(new MyComboBoxEditor(box, table, column));
            } else {
                col.setCellEditor(
                    new TextBoxEditor(new JTextField(), table, column));
            }
        }

        final JTableHeader header = table.getTableHeader();

        final ColumnHeaderToolTips tips = new ColumnHeaderToolTips();

        // Assign a tooltip for each of the columns
        for (int c = 0; c < table.getColumnCount(); c++) {
            final TableColumn column = table.getColumnModel().getColumn(c);
            tips.setToolTip(column, FruitspecTableModel.columnNames[c]);
        }

        header.addMouseMotionListener(tips);

        centerPanel = new JPanel(new BorderLayout());

        centerPanel.add(tablePanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JScrollPane initNorth() {
        final JPanel northPane = new JPanel(GuiTools.getLeftFlowLayout());
        northPane.setPreferredSize(
            new Dimension(800, GuiTools.getCharHeight() * 7));

        final JScrollPane scroller = new JScrollPane(northPane);
        scroller.setBackground(Color.white);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setPreferredSize(
            new Dimension(800, GuiTools.getCharHeight() * 7));

        final Border topBorder = BorderFactory.createEtchedBorder();
        scroller.setBorder(topBorder);

        final ActionListener toggleAction = e -> recalculate();

        countDrops();
        dropButtons = new JToggleButton[dropCount];

        for (int i = 0; i < dropCount; i++) {
            final String dropName = data[dropIndexes[i]][2];
            dropButtons[i] = new JToggleButton(dropName);
            dropButtons[i].setToolTipText("Show/Hide drop " + dropName + " on grid");
            dropButtons[i].setSelected(true);
            dropButtons[i].addActionListener(toggleAction);
        }

        Arrays.sort(dropButtons,
            new Comparator() {
                public int compare(final Object o1, final Object o2) {
                    final JToggleButton b1 = (JToggleButton) o1;
                    final JToggleButton b2 = (JToggleButton) o2;

                    final int i1 = Integer.parseInt(b1.getText());
                    final int i2 = Integer.parseInt(b2.getText());

                    if (i1 < i2) {
                        return -1;
                    } else if (i1 > i2) {
                        return 1;
                    } else {
                        return 0;
                    }
                }

                public boolean equals(final Object obj) {
                    return false;
                }
            });

        statusLabel = new JLabel("Modified");
//        statusLabel.setForeground(Color.red);
//        northPane.add(statusLabel);

        final JButton gtinButton = new JButton("1) GTIN");
        gtinButton.addActionListener(e -> lookupGTIN());
        gtinButton.setToolTipText("Lookup GTIN code for all barcodes");
        northPane.add(gtinButton);

        saveButton = new JButton("2) Save");
        saveButton.addActionListener(e -> lineGui.saveTemplateToDatabase());
        saveButton.setToolTipText("Save this template to the database");
        northPane.add(saveButton);


        final JButton testButton = new JButton("3) Test");
        testButton.addActionListener(e -> {
            final CdsGenerator tester = new CdsGenerator(packhouse,
                lineIndex);
            tester.testSetup();
        });
        testButton.setToolTipText("Test if this template will print correctly");
        northPane.add(testButton);

        JButton button = new JButton("All");
        button.addActionListener(e -> selectAll());
        button.setToolTipText("Show all drops");
        northPane.add(button);

        button = new JButton("None");
        button.addActionListener(e -> deSelectAll());
        button.setToolTipText("Hide all drops");
        northPane.add(button);

        for (int i = 0; i < dropCount; i++) {
            northPane.add(dropButtons[i]);
        }

        northPane.validate();

        return scroller;
    }

    private String calculateFakeGTIN() {
        final String firstPart = "6002200";
        final int firstPartStep1 = 2;
        final int firstPartStep2 = 8;
        int step1 = 0;
        int step2 = 0;

        int next = r.nextInt(99999);
        final int temp = next;

        step1 += next / 10000;
        next = next % 1000;
        step1 += next / 100;
        next = next % 10;
        step1 += next;

        next = temp;

        next = next % 10000;
        step2 += next / 1000;
        next = next % 100;
        step2 += next / 10;


        step1 = (firstPartStep1 + step1) * 3;
        step2 = step2 + firstPartStep2;
        step1 += step2;
        step1 = ((10 - (step1 % 10)) % 10);

        String sizer = "" + temp;
        while (sizer.length() < 5) {
            sizer = "0" + sizer;
        }

        return firstPart + sizer + step1;
    }

    private void lookupGTIN() {
        String organisation;
        String commodity;
        String variety;
        String pack;
        String grade;
        String mark;
        String inventory;
        String sizeCount;

        final String[] columns = new String[]{"Organisation", "Commodity", "Variety", "Pack",
            "Grade", "Mark", "InventoryCode", "SizeCount", "Active"};

        String[] keys;
        final int gtinIndex = FruitspecTableModel.findIndex("GTIN");
        for (int i = 0; i < model.getRowCount(); i++) {
//			model.setValueAt(value, i, column);
            organisation = (String) model.getValueAt(i, FruitspecTableModel.findIndex("Organization"));

            if (!"CA".equals(organisation)) {
                model.setValueAt(calculateFakeGTIN(), i, gtinIndex);
                continue;
            }

            commodity = (String) model.getValueAt(i, FruitspecTableModel.findIndex("Commodity"));
            variety = (String) model.getValueAt(i, FruitspecTableModel.findIndex("Variety"));
            pack = (String) model.getValueAt(i, FruitspecTableModel.findIndex("Pack"));
            grade = (String) model.getValueAt(i, FruitspecTableModel.findIndex("Grade"));
            mark = (String) model.getValueAt(i, FruitspecTableModel.findIndex("Mark"));
            inventory = (String) model.getValueAt(i, FruitspecTableModel.findIndex("Inventory"));
            sizeCount = (String) model.getValueAt(i, FruitspecTableModel.findIndex("SizeCount"));

            keys = new String[]{organisation, commodity, variety, pack, grade,
                mark, inventory, sizeCount, "Y"};

            try {
                final ScrollableTableModel sm = TableModelFactory.getResultSetTableModel("Gtin", columns, keys, false);
                final ResultSet resultSet = sm.getResultSet();

                if (sm.getRowCount() == 0) {
                    model.setValueAt("Unregistered", i, gtinIndex);
                } else {
                    resultSet.beforeFirst();
                    resultSet.next();
                    model.setValueAt(resultSet.getString("GtinCode"), i, gtinIndex);
                }

                sm.close();

            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        table.revalidate();
        table.updateUI();
    }

    public void setUpdated() {
        statusLabel.setText("Synchronized");
        statusLabel.setForeground(Color.blue);
    }

    public void setDirty() {

        if (saveButton.getBackground().equals(Color.red)) {
            return;
        }

        saveButton.setBackground(Color.red);
        if (!statusLabel.getForeground().equals(Color.red)) {
            lineGui.setDirtyTitle(lineIndex);
            final ScrollableTableModel model;

            try {
                final String lineName;

                if (lineIndex < 10) {
                    lineName = "0" + (lineIndex + 1);
                } else {
                    lineName = "" + (lineIndex + 1);
                }

                final String query = "select * from packhousetemplates  " +
                    " where PackhouseName = 'XXX' and " +
                    " lineName = '" + lineName + "' ";


                model = TableModelFactory.getResultSetTableModel(query, packhouse.getPackhouseName(), false);

                //		        model = TableModelFactory.getResultSetTableModel(
                //		                        "packhousetemplates", "PackhouseName",
                //		                        packhouse.getPackhouseName());
                final ResultSet resultset = model.getResultSet();
                resultset.absolute(1);
                resultset.updateString(1, packhouse.getPackhouseName());
                resultset.updateString(2, lineName);
                resultset.updateString(3, packhouse.getTemplateName(lineIndex));
                resultset.updateString(4, "changed");
                resultset.updateRow();
                model.close();
            } catch (final SQLException e) {
                e.printStackTrace();
                System.out.println(e.getSQLState());
            }
        }

        statusLabel.setForeground(Color.red);
        statusLabel.setText("Modified");
        lineGui.setDirty();
        updateUI();
    }

    private void countDrops() {
        String dropName = data[0][2];
        dropCount = 1;
        dropIndexes = new int[200];
        java.util.Arrays.fill(dropIndexes, -1);

        dropIndexes[0] = 0;

        for (int i = 1; i < data.length; i++) {
            try {
                if (!dropName.equals(data[i][2])) {
                    dropName = data[i][2];
                    dropIndexes[dropCount++] = i;
                }
            } catch (final RuntimeException e) {
            }
        }

        dropIndexes[dropCount] = data.length;
    }

    private void selectAll() {
        for (int i = 0; i < dropButtons.length; i++) {
            dropButtons[i].setSelected(true);
        }

        recalculate();
    }

    private void deSelectAll() {
        for (int i = 0; i < dropButtons.length; i++) {
            dropButtons[i].setSelected(false);
        }

        recalculate();
    }

    private void recalculate() {
        int count = 0;

        for (int i = 0; i < dropButtons.length; i++) {
            if (dropButtons[i].isSelected()) {
                count++;
            }
        }

        final int[] usedDrops = new int[count];

        count = 0;

        for (int i = 0; i < dropButtons.length; i++) {
            if (dropButtons[i].isSelected()) {
                usedDrops[count++] = i;
            }
        }

        model.setData(data, dropIndexes, usedDrops);
        sorter.reallocateIndexes();
        table.updateUI();
    }

    public String getTemplateName() {
        return packhouse.getTemplateName(lineIndex);
    }

    public String[][] getData() {
        return data;
    }

    public void updateEveryWhere(final int column, final String value) {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(value, i, column);
        }

        table.revalidate();
        table.updateUI();
        setDirty();
    }

    private class MyComboBoxEditor extends DefaultCellEditor
        implements ActionListener, TableModelListener {
        private JComboBox box;
        private int column;
        private int row;
        private FruitspecTableModel model;
        private ElegantTable table;
        private TableSorter sortModel;
        private boolean newRow;
        private Object myValue;
        private ActionEvent lastEvent;


        public void updateUI() {
            System.out.println("UpdateUI");
        }

        private MyComboBoxEditor(final JComboBox box, final ElegantTable table, final int column) {
            super(box);
            this.box = box;
            ((SteppedComboBox) box).setPopupWidth(200);
            box.addActionListener(this);
            this.column = column;
            this.sortModel = (TableSorter) table.getModel();
            this.model = (FruitspecTableModel) sortModel.getModel();
            this.table = table;
            sortModel.addTableModelListener(this);
            lastEvent = null;
        }

        public void actionPerformed(final ActionEvent e) {
            ((SteppedComboBox) box).setPopupWidth(200);

            if (e.getActionCommand().equals("comboBoxEdited")) {
                myValue = box.getEditor().getItem();

                //                System.out.println(e.getActionCommand());
            } else {
                //                System.out.println(e.getActionCommand());
                myValue = null;
                setDirty();
                saveButton.setBackground(Color.red);
            }
        }

        public boolean stopCellEditing() {
            ((SteppedComboBox) box).setPopupWidth(200);
            Object value = box.getSelectedItem();

            if (!newRow) {
                return true;
            }

            newRow = false;

            final boolean result = super.stopCellEditing();

            if (result) {
                if (myValue != null) {
                    value = myValue;
                }

                try {
                    if (!sortModel.getValueAt(row, column).equals(value)) {
                        sortModel.setValueAt(value, row, column);
                    }
                } catch (final RuntimeException e) {
                    sortModel.setValueAt(value, row, column);
                } finally {
                    setDirty();
                    saveButton.setBackground(Color.red);
                }
            }

            if (FruitspecTableModel.getUpdateAll(column)) {
                System.out.println(table.getColumnModel().getColumn(column).getMaxWidth());
                System.out.println(table.getColumnModel().getColumn(column).getPreferredWidth());
                System.out.println(table.getColumnModel().getColumn(column).getResizable());
                updateEveryWhere(column, (String) value);
                System.out.println(table.getColumnModel().getColumn(column).getMaxWidth());
                System.out.println(table.getColumnModel().getColumn(column).getPreferredWidth());
                System.out.println(table.getColumnModel().getColumn(column).getResizable());
            }

            if (FruitspecTableModel.getEveryWhere(column)) {
                lineGui.updateEveryWhere(column, (String) value);
            }

            myValue = null;

            return result;
        }

        public Component getTableCellEditorComponent(final JTable table, final Object value,
                                                     final boolean isSelected,
                                                     final int row, final int column) {
            newRow = true;
            this.row = row;
            ((SteppedComboBox) box).setPopupWidth(200);

            return editorComponent;
        }

        public Object getCellEditorValue() {
            ((SteppedComboBox) box).setPopupWidth(200);
            if (myValue != null) {
                return myValue;
            }

            return super.getCellEditorValue();
        }

        public boolean isCellEditable(final EventObject anEvent) {
            ((SteppedComboBox) box).setPopupWidth(200);
            return super.isCellEditable(anEvent);
        }

        public void tableChanged(final TableModelEvent e) {
        }
    }

    private class TextBoxEditor extends DefaultCellEditor
        implements ActionListener, TableModelListener {
        private JTextField field;
        private int column;
        private int row;
        private FruitspecTableModel model;
        private ElegantTable table;
        private TableSorter sortModel;
        private boolean newRow;
        private Object myValue;
        private ActionEvent lastEvent;
        private int maxLength;
        private String columnName;

        private TextBoxEditor(final JTextField field, final ElegantTable table, final int column) {
            super(field);
            this.field = field;
            field.addActionListener(this);
            this.column = column;
            this.sortModel = (TableSorter) table.getModel();
            this.model = (FruitspecTableModel) sortModel.getModel();
            this.table = table;
            sortModel.addTableModelListener(this);
            lastEvent = null;
            maxLength = FruitspecTableModel.getMaxSize(column);
            if (maxLength == 0) {
                maxLength = Integer.MAX_VALUE;
            }
            columnName = FruitspecTableModel.columnNames[column];
        }

        public void actionPerformed(final ActionEvent e) {
            setDirty();
            saveButton.setBackground(Color.red);
        }

        public boolean stopCellEditing() {
            if (field.getText().length() > maxLength) {
                JOptionPane.showMessageDialog(lineGui, "Invalid entry. " +
                        "Maximum Length for " + columnName + " is " + maxLength,
                    "Error Message",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            Object value = field.getText();

            if (!newRow) {
                return true;
            }

            newRow = false;

            final boolean result = super.stopCellEditing();

            if (result) {
                if (myValue != null) {
                    value = myValue;
                }

                try {
                    if (!sortModel.getValueAt(row, column).equals(value)) {
                        sortModel.setValueAt(value, row, column);
                    }
                } catch (final RuntimeException e) {
                    sortModel.setValueAt(value, row, column);
                } finally {
                    setDirty();
                    saveButton.setBackground(Color.red);
                }
            }

            if (FruitspecTableModel.getUpdateAll(column)) {
                updateEveryWhere(column, (String) value);
            }

            if (FruitspecTableModel.getEveryWhere(column)) {
                lineGui.updateEveryWhere(column, (String) value);
            }

            myValue = null;

            return result;
        }

        public Component getTableCellEditorComponent(final JTable table, final Object value,
                                                     final boolean isSelected,
                                                     final int row, final int column) {
            newRow = true;
            this.row = row;
            field.setText((String) value);


            return editorComponent;
        }

        public Object getCellEditorValue() {
            if (myValue != null) {
                return myValue;
            }

            return super.getCellEditorValue();
        }

        public boolean isCellEditable(final EventObject anEvent) {
            return super.isCellEditable(anEvent);
        }

        public void tableChanged(final TableModelEvent e) {
        }
    }

    private class ComboPanel extends JPanel {
        public ComboPanel(final Connection connection, final String tableName,
                          final String columnName) {
            super();

            final Border border = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                tableName, TitledBorder.LEADING,
                TitledBorder.BELOW_TOP);
            setBorder(border);
            this.add(new JComboBox(
                new DatabaseComboModel(connection, tableName,
                    columnName)));
        }
    }

    private class ColumnHeaderToolTips extends MouseMotionAdapter {
        // Current column whose tooltip is being displayed.
        // This variable is used to minimize the calls to setToolTipText().
        private TableColumn curCol;

        // Maps TableColumn objects to tooltips
        private Map tips = new HashMap();

        // If tooltip is null, removes any tooltip text.
        private void setToolTip(final TableColumn col, final String tooltip) {
            if (tooltip == null) {
                tips.remove(col);
            } else {
                tips.put(col, tooltip);
            }
        }

        public void mouseMoved(final MouseEvent evt) {
            TableColumn col = null;
            final JTableHeader header = (JTableHeader) evt.getSource();
            final JTable table = header.getTable();
            final TableColumnModel colModel = table.getColumnModel();
            final int vColIndex = colModel.getColumnIndexAtX(evt.getX());

            // Return if not clicked on any column header
            if (vColIndex >= 0) {
                col = colModel.getColumn(vColIndex);
            }

            if (col != curCol) {
                header.setToolTipText((String) tips.get(col));
                curCol = col;
            }
        }
    }

    private class SizeTextField extends JTextField {
        private int maxSize;

        public SizeTextField(final String text, final int maxSize) {
            super(text);
            this.maxSize = maxSize;
        }

        public void actionPerformed(final ActionEvent e) {
            final JTextField input = (JTextField) e.getSource();
            final String text = input.getText();

            if (text.length() <= maxSize) {
            } else {
                JOptionPane.showMessageDialog(lineGui, "Invalid entry. Maximum Length is " + maxSize,
                    "Error Message",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
