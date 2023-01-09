package com.someone.gui;

import com.someone.io.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class InfoFrame extends JFrame {
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
    private boolean finished;
    private boolean modified;
    private StringBuffer buffer;

    public InfoFrame(final String title, final StringBuffer buffer)
        throws HeadlessException {
        super();
        this.buffer = buffer;

        final ImageIcon icon = new ImageIcon(FileProxy.getURL("icons/home.gif"));
        this.setIconImage(icon.getImage());

        this.setResizable(false);
        charHeight = GuiTools.getCharHeight();
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

    public InfoFrame(final String title)
        throws HeadlessException {
        this(title, null);
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

        final JButton ok = new JButton("OK");

        ok.addActionListener(e -> {
            finished = true;
            modified = true;
            InfoFrame.this.dispose();
        });

        bottomFiller.add(ok);

        bottomPanel.add(bottomFiller);
    }

    private void createMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setPreferredSize(
            new Dimension(charHeight * 25, charHeight * 18));

        final JTextPane text = new JTextPane();
        text.setForeground(Color.red);
        text.setPreferredSize(new Dimension(charHeight * 25, charHeight * 18));
        text.setText(buffer.toString());


        final JScrollPane scroller = new JScrollPane(text,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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

//    private class ElegantLabel extends JLabel {
//        ElegantLabel(String text) {
//            super(text);
//            this.setFont(labelFont);
//        }
//    }
}
