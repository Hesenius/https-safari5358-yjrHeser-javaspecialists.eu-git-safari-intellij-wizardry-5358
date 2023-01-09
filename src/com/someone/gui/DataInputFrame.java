package com.someone.gui;

import com.someone.io.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class DataInputFrame extends JFrame {
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
    private int charHeight;
    private int charWidth;
    private Font labelFont;
    private FlowLayout leftFlowLayout;
    private JTextField[] textFields;
    private boolean finished;
    private boolean modified;
    private Runnable closeAction;
    private boolean useTitles;

    public DataInputFrame(final String title, final String[] rowNames, final boolean useTitles)
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

        createTopPanel();

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

    public DataInputFrame(final String title, final String[] rowNames)
        throws HeadlessException {
        this(title, rowNames, true);
    }

    public void setData(final String[] data) {
        for (int i = 0; i < data.length; i++) {
            textFields[i].setText(data[i]);
        }
    }

    public String[] getNewData() {
        final String[] result = new String[textFields.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = textFields[i].getText();
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

    private void doOK() {
        finished = true;
        modified = true;

        if (closeAction != null) {
            closeAction.run();
        }

        DataInputFrame.this.dispose();
    }

    private void createBottomPanel() {
        bottomPanel = new JPanel();

        bottomPanel.setPreferredSize(
            new Dimension(charHeight * 27, charHeight * 3));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        final Border bottomBorder = BorderFactory.createEtchedBorder();
        bottomPanel.setBorder(bottomBorder);

        final JPanel bottomFiller = new JPanel();

        final JButton cancel = new JButton("Cancel");
        final JButton clear = new JButton("Clear");
        final JButton ok = new JButton("Finish");

        ok.addActionListener(e -> doOK());

        clear.addActionListener(e -> {
            for (int i = 0; i < textFields.length; i++) {
                textFields[i].setText("");
            }
        });

        cancel.addActionListener(e -> {
            finished = true;
            modified = false;

            if (closeAction != null) {
                closeAction.run();
            }

            DataInputFrame.this.dispose();
        });

        bottomFiller.add(cancel);
        bottomFiller.add(clear);
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

        textFields = new JTextField[rowNames.length];

        final KeyAdapter keyListener = new KeyAdapter() {
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doOK();
                }
            }
        };

        for (int i = 1; i < formattedNames.length; i++) {
            line = new JPanel(leftFlowLayout);
            labelPanel = new JPanel(leftFlowLayout);
            labelPanel.setPreferredSize(
                new Dimension(textWidth, labelFont.getSize() * 2));

            labelPanel.add(new ElegantLabel(formattedNames[i]));
            line.add(labelPanel);
            textFields[i - 1] = new JTextField(inputSize);
            textFields[i - 1].addKeyListener(keyListener);
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

    private class ElegantLabel extends JLabel {
        private ElegantLabel(final String text) {
            super(text);
            this.setFont(labelFont);
        }
    }
}
