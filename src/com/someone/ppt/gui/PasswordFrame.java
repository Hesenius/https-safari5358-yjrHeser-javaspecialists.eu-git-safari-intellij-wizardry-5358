package com.someone.ppt.gui;

import com.someone.gui.*;
import com.someone.io.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

class PasswordFrame extends JFrame {
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
    private int charHeight;
    private int charWidth;
    private Font labelFont;
    private FlowLayout leftFlowLayout;
    private CmsGui caller;
    private JTextField[] textFields;

    public PasswordFrame(final CmsGui caller)
        throws HeadlessException {
        super();
        this.caller = caller;
        final ImageIcon icon = new ImageIcon(FileProxy.getURL("icons/home.gif"));
        this.setIconImage(icon.getImage());

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        final String title = "Please Enter Password";
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
            new Dimension(charHeight * 27, charHeight * 3));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        final Border bottomBorder = BorderFactory.createEtchedBorder();
        bottomPanel.setBorder(bottomBorder);

        final JPanel bottomFiller = new JPanel();

        final JButton ok = new JButton("OK");

        ok.addActionListener(e -> doOK());


        bottomFiller.add(ok);

        bottomPanel.add(bottomFiller);
    }

    private void doOK() {
        if (textFields[0].getText().equals("see")) {
            caller.readAccess();
            PasswordFrame.this.dispose();
        } else if (textFields[0].getText().equals("tadadmin")) {
            caller.writeAccess();
            PasswordFrame.this.dispose();
        } else {
            textFields[0].setText("");
        }
    }

    private void createMainPanel() {

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(
            new Dimension(charHeight * 25, charHeight * 18));

        final JPanel gridPanel = new JPanel(new GridLayout(2, 1));

        final String[] formattedNames = new String[1];


        formattedNames[0] = "Password";


        final int textWidth = findMaxWidth(formattedNames) + 10;

        JPanel line = new JPanel(leftFlowLayout);
        JPanel labelPanel = new JPanel(leftFlowLayout);
//        labelPanel.setPreferredSize(
//                new Dimension(textWidth, labelFont.getSize() * 2));

//        labelPanel.add(new ElegantLabel(formattedNames[0]));
//        line.add(labelPanel);

        gridPanel.add(line);

        final int inputSize = (int) ((frameWidth - textWidth) / charWidth) - 6;

        textFields = new JTextField[1];

        final KeyAdapter keyListener = new KeyAdapter() {
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doOK();
                }
            }
        };


        for (int i = 0; i < formattedNames.length; i++) {
            line = new JPanel(leftFlowLayout);
            labelPanel = new JPanel(leftFlowLayout);
            labelPanel.setPreferredSize(
                new Dimension(textWidth, labelFont.getSize() * 2));

            labelPanel.add(new ElegantLabel(formattedNames[i]));
            line.add(labelPanel);
            textFields[i] = new JPasswordField(inputSize);
            textFields[i].addKeyListener(keyListener);


            line.add(textFields[i]);
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

    private class ElegantLabel extends JLabel {
        private ElegantLabel(final String text) {
            super(text);
            this.setFont(labelFont);
        }
    }
}
