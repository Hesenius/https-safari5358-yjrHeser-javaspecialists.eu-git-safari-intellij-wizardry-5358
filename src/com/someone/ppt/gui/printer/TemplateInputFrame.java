package com.someone.ppt.gui.printer;

import com.someone.db.models.*;
import com.someone.gui.*;
import com.someone.io.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

class TemplateInputFrame extends JFrame {
    private static int frameHeight;
    private static int frameWidth;

    static {
        frameHeight = (GuiTools.getCharHeight() * 5) +
            (GuiTools.getCharHeight() * 25) +
            (GuiTools.getCharHeight() * 5);
        frameWidth = (int) (frameHeight * 0.80);
    }

    private JPanel topPanel;
    private JPanel mainPanel;
    private JPanel bottomPanel;
    private String[] rowNames;
    private int charHeight;
    private int charWidth;
    private Font labelFont;
    private FlowLayout leftFlowLayout;
    private JComponent[] textFields;
    private boolean finished;
    private boolean modified;
    private Runnable closeAction;
    private boolean useTitles;
    private String notUsed = "NONE";
    private JComboBox printFields;
    private JComboBox printCodes;

    private TemplateInputFrame(final String title, final String[] rowNames,
                               final boolean useTitles, final String printerID)
        throws HeadlessException {
        super();
        this.useTitles = useTitles;

        final ImageIcon icon = new ImageIcon(FileProxy.getURL("icons/home.gif"));
        this.setIconImage(icon.getImage());

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        this.rowNames = rowNames;
        charHeight = GuiTools.getCharHeight();
        charWidth = GuiTools.getCharWidth();
        labelFont = GuiTools.getLabelFont();
        leftFlowLayout = GuiTools.getLeftFlowLayout();
        setSize(frameWidth, frameHeight);

        setTitle("    " + title);
        getContentPane().setLayout(new BorderLayout());

        printFields = new JComboBox(
            new DatabaseComboModel("PrintFields",
                "PrintField"));
        printCodes = new JComboBox(
            new DatabaseComboModel("PrinterCodes", "CodeDesc",
                "PrinterID", printerID));
        printCodes.addItemListener(e -> {
            if (!printFields.getSelectedItem().equals(notUsed)) {
                printFields.setSelectedItem(notUsed);
                printFields.updateUI();
            }
        });
        printFields.addItemListener(e -> {
            if (!printCodes.getSelectedItem().equals(notUsed)) {
                printCodes.setSelectedItem(notUsed);
                printCodes.updateUI();
            }
        });

        createTopPanel();

        createMainPanel();

        createBottomPanel();

        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        validate();
        setLocation();
    }

    public TemplateInputFrame(final String title, final String[] rowNames, final String printerID)
        throws HeadlessException {
        this(title, rowNames, true, printerID);
    }

    public void setData(final String[] data) {
        for (int i = 0; i < data.length; i++) {
            if (textFields[i] instanceof JTextField) {
                ((JTextField) textFields[i]).setText(data[i]);
            } else {
                final JComboBox box = (JComboBox) textFields[i];
                final DatabaseComboModel model = (DatabaseComboModel) box.getModel();

                final int index = model.getIndexOf(data[i]);

                if (index != -1) {
                    box.setSelectedIndex(index);
                }
            }
        }
    }

    public String[] getNewData() {
        final String[] result = new String[textFields.length];

        for (int i = 0; i < result.length; i++) {
            if (textFields[i] instanceof JTextField) {
                result[i] = ((JTextField) textFields[i]).getText();
            } else {
                final JComboBox box = (JComboBox) textFields[i];
                result[i] = box.getSelectedItem().toString();

                if (result[i].equals(notUsed)) {
                    result[i] = "";
                }
            }
        }

        return result;
    }

    public void setCloseAction(final Runnable action) {
        closeAction = action;
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

    private void createBottomPanel() {
        bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(
            new Dimension(charHeight * 2, charHeight * 3));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        final Border bottomBorder = BorderFactory.createEtchedBorder();
        bottomPanel.setBorder(bottomBorder);

        final JPanel bottomFiller = new JPanel();

        final JButton cancel = new JButton("Cancel");
        final JButton ok = new JButton("Finish");

        ok.addActionListener(e -> {
            finished = true;
            modified = true;

            if (closeAction != null) {
                closeAction.run();
            }

            TemplateInputFrame.this.dispose();
        });

        cancel.addActionListener(e -> {
            finished = true;
            modified = false;

            if (closeAction != null) {
                closeAction.run();
            }

            TemplateInputFrame.this.dispose();
        });

        bottomFiller.add(cancel);
        bottomFiller.add(ok);

        bottomPanel.add(bottomFiller);
    }

    private void createMainPanel() {
        final int columns = rowNames.length + 1;

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(
            new Dimension(charHeight * 25, charHeight * 18));

        final JPanel gridPanel = new JPanel(new GridLayout(columns, 1));

        final String[] formattedNames = new String[rowNames.length + 1];

        if (useTitles) {
            formattedNames[0] = "Column Name";
        } else {
            formattedNames[0] = "";
        }

        for (int i = 1; i < formattedNames.length; i++) {
            formattedNames[i] = rowNames[i - 1].toUpperCase();
        }

        final int textWidth = findMaxWidth(formattedNames) + 10;

        JPanel line = new JPanel(leftFlowLayout);
        JPanel labelPanel = new JPanel(leftFlowLayout);
        labelPanel.setPreferredSize(
            new Dimension(textWidth, labelFont.getSize() * 2));

        labelPanel.add(new ElegantLabel(formattedNames[0]));
        line.add(labelPanel);

        if (useTitles) {
            line.add(new ElegantLabel("Value"));
        } else {
            line.add(new ElegantLabel(""));
        }

        gridPanel.add(line);

        final int inputSize = (int) ((frameWidth - textWidth) / charWidth) - 6;

        textFields = new JComponent[rowNames.length];

        for (int i = 1; i < formattedNames.length; i++) {
            line = new JPanel(leftFlowLayout);
            labelPanel = new JPanel(leftFlowLayout);
            labelPanel.setPreferredSize(
                new Dimension(textWidth, labelFont.getSize() * 2));

            labelPanel.add(new ElegantLabel(formattedNames[i]));
            line.add(labelPanel);

            if (i == 3) {
                printFields.setSelectedItem(notUsed);
                textFields[2] = printFields;
            } else if (i == 5) {
                printCodes.setSelectedItem(notUsed);
                textFields[i - 1] = printCodes;
            } else {
                textFields[i - 1] = new JTextField(inputSize);
            }

            if ((i < 3) || (i > 5)) {
                ((JTextField) textFields[i - 1]).setEditable(false);
            }

            line.add(textFields[i - 1]);
            gridPanel.add(line);
        }

        final JPanel bla = new JPanel();
        bla.add(gridPanel);

        final JScrollPane scroller = new JScrollPane(bla,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setPreferredSize(
            new Dimension(charHeight * 27, charHeight * 25));
        mainPanel.add(scroller);
    }

    private void createTopPanel() {
        topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(charHeight * 5, charHeight * 5));
        topPanel.setBackground(Color.white);

        final Border topBorder = BorderFactory.createEtchedBorder();
        topPanel.setBorder(topBorder);
    }

    private int findMaxWidth(final String[] columnNames) {
        double pixels = 0.0;
        int maxpixels = 0;

        for (int t = 0; t < columnNames.length; t++) {
            pixels = charWidth;
            pixels = pixels * columnNames[t].length();
            pixels = pixels - (pixels * .25);

            if (pixels > maxpixels) {
                maxpixels = (int) pixels;
            }
        }

        return maxpixels;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isModified() {
        return modified;
    }

    private class ElegantLabel extends JLabel {
        private ElegantLabel(final String text) {
            super(text);
            this.setFont(labelFont);
        }
    }
}
