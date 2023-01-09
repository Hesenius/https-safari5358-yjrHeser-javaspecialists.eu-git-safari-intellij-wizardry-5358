package com.someone.db;

import com.someone.db.models.*;
import com.someone.gui.*;
import com.someone.io.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ComboSelectionFrame extends JFrame {
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
    private boolean finished;
    private boolean modified;
    private Runnable closeAction;
    private String columnName;
    private String tableName;
    private String selectedValue;
    private JList list;

    public ComboSelectionFrame(final String title, final String tableName,
                               final String columnName) throws HeadlessException {
        super();

        final ImageIcon icon = new ImageIcon(FileProxy.getURL("icons/home.gif"));
        this.setIconImage(icon.getImage());
        this.columnName = columnName;
        this.tableName = tableName;
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        charHeight = GuiTools.getCharHeight();
        charWidth = GuiTools.getCharWidth();
        setSize(frameWidth, frameHeight);

        setTitle("    " + title);
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
        selectedValue = list.getSelectedValue() + "";

        if (closeAction != null) {
            closeAction.run();
        }

        ComboSelectionFrame.this.dispose();
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

        ok.addActionListener(e -> doOK());

        cancel.addActionListener(e -> {
            finished = true;
            modified = false;

            ComboSelectionFrame.this.dispose();
        });

        bottomFiller.add(cancel);
        bottomFiller.add(ok);

        bottomPanel.add(bottomFiller);
    }

    private void createMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setPreferredSize(
            new Dimension(charHeight * 25, charHeight * 18));

        final KeyAdapter keyListener = new KeyAdapter() {
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doOK();
                }
            }
        };

        list = new JList(new DatabaseComboModel(
            ConnectionFactory.getConnection(), tableName,
            columnName));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);

        list.addKeyListener(keyListener);

        final JScrollPane listScrollPane = new JScrollPane(list,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScrollPane.setPreferredSize(
            new Dimension(charWidth * 20, charHeight * 15));

        final JPanel fillerPane = new JPanel();
        fillerPane.setPreferredSize(
            new Dimension(charWidth * 20, charHeight * 4));
        mainPanel.add(fillerPane);
        mainPanel.add(listScrollPane);
    }

    private void createTopPanel() {
        topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(charHeight * 5, charHeight * 5));
        topPanel.setBackground(Color.white);

        final Border topBorder = BorderFactory.createEtchedBorder();
        topPanel.setBorder(topBorder);
    }

//    private int findMaxWidth(String[] columnNames) {
//        double pixels    = 0.0;
//        int    maxpixels = 0;
//
//        for (int t = 0; t < columnNames.length; t++) {
//            pixels = charWidth;
//            pixels = pixels * columnNames[t].length();
//            pixels = pixels - (pixels * .25);
//
//            if (pixels > maxpixels) {
//                maxpixels = (int) pixels;
//            }
//        }
//
//        return maxpixels;
//    }

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

    public String getSelectedValue() {
        return selectedValue;
    }

//    private class ElegantLabel extends JLabel {
//        ElegantLabel(String text) {
//            super(text);
//            this.setFont(labelFont);
//        }
//    }
}
