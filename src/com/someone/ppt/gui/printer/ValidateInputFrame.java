package com.someone.ppt.gui.printer;

import com.someone.db.*;
import com.someone.db.models.*;
import com.someone.gui.*;
import com.someone.io.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

//import org.jdom.Element;


class ValidateInputFrame extends JFrame {
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
    private int charHeight;
    private int charWidth;
    private Font labelFont;
    private FlowLayout leftFlowLayout;
    private boolean finished;
    private boolean modified;
    private Runnable closeAction;
    private String tableName;
//	private Element tableElement;

    private JComponent[] textFields;
    private String[] rowNames;
    private String[][] definition;

    public String[] getNewData() {
        final String[] result = new String[textFields.length];

        for (int i = 0; i < result.length; i++) {
            if (textFields[i] instanceof JTextField) {
                result[i] = ((JTextField) textFields[i]).getText();
            } else {
                final JComboBox box = (JComboBox) textFields[i];
                result[i] = box.getSelectedItem().toString();
            }
        }
        return result;
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

    public ValidateInputFrame(final String[][] definition) throws HeadlessException {
        super();
        final ImageIcon icon = new ImageIcon(FileProxy.getURL("icons/home.gif"));
        this.setIconImage(icon.getImage());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        charHeight = GuiTools.getCharHeight();
        charWidth = GuiTools.getCharWidth();
        labelFont = GuiTools.getLabelFont();
        leftFlowLayout = GuiTools.getLeftFlowLayout();
        setSize(frameWidth, frameHeight);

        this.definition = definition;

        // set title, tableName
        parseFile(definition);


        setTitle("    " + tableName);
        getContentPane().setLayout(new BorderLayout());

        createTopPanel();

        createMainPanel();

        createBottomPanel();

        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        validate();
        setLocation();
    }

    private void parseFile(final String[][] definition) {
        tableName = definition[0][0];

        rowNames = new String[definition.length - 1];

        for (int i = 1; i < definition.length; i++) {
            rowNames[i - 1] = definition[i][0];
        }
    }


    public void setCloseAction(final Runnable action) {
        closeAction = action;
    }

    private void setLocation() {
        final Toolkit kit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = kit.getScreenSize();
        final GraphicsConfiguration config = getGraphicsConfiguration();
        final Insets insets = kit.getScreenInsets(config);
        final int width = screenSize.width - (insets.left + insets.right);
        final int height = screenSize.height - (insets.top + insets.bottom);

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
            ValidateInputFrame.this.dispose();
        });

        cancel.addActionListener(e -> {
            finished = true;
            modified = false;
            if (closeAction != null) {
                closeAction.run();
            }
            ValidateInputFrame.this.dispose();
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
        formattedNames[0] = "Column Name";

        for (int i = 1; i < formattedNames.length; i++) {
            formattedNames[i] = rowNames[i - 1].toUpperCase();
            System.out.println(i + " -> " + rowNames[i - 1].toUpperCase());
        }

        final int textWidth = findMaxWidth(formattedNames) + 10;

        JPanel line = new JPanel(leftFlowLayout);
        JPanel labelPanel = new JPanel(leftFlowLayout);
        labelPanel.setPreferredSize(
            new Dimension(textWidth, labelFont.getSize() * 2));

        labelPanel.add(new ElegantLabel(formattedNames[0]));
        line.add(labelPanel);
        line.add(new ElegantLabel("Value"));
        gridPanel.add(line);

        final int inputSize = (int) ((frameWidth - textWidth) / charWidth) - 2;

        textFields = new JComponent[rowNames.length];

        for (int i = 1; i < definition.length; i++) {

            line = new JPanel(leftFlowLayout);
            labelPanel = new JPanel(leftFlowLayout);
            labelPanel.setPreferredSize(
                new Dimension(textWidth, labelFont.getSize() * 2));

            labelPanel.add(new ElegantLabel(formattedNames[i]));


            line.add(labelPanel);
            if (!definition[i][1].equals("")) {
                textFields[i - 1] = new JComboBox(new DatabaseComboModel(ConnectionFactory.getConnection(), definition[i][1], definition[i][2]));
            } else {
                textFields[i - 1] = new JTextField(inputSize);
                if (!definition[i][3].equals("")) {
                    textFields[i - 1].setEnabled(false);
                }
            }

            line.add(textFields[i - 1]);
            gridPanel.add(line);
        }

        mainPanel.add(gridPanel);
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

    private class ElegantLabel extends JLabel {
        private ElegantLabel(final String text) {
            super(text);
            this.setFont(labelFont);
        }
    }

    /**
     * Returns the finished.
     *
     * @return boolean
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Returns the modified.
     *
     * @return boolean
     */
    public boolean isModified() {
        return modified;
    }

}
