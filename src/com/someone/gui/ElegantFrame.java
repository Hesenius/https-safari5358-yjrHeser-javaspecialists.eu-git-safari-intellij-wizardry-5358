package com.someone.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;


class ElegantFrame extends JFrame {
    private static Font labelFont;
    private static FlowLayout leftFlowLayout;
    private static int charWidth;
    private static int charHeight;
    private static int frameHeight;
    private static int frameWidth;

    static {
        labelFont = new Font("Arial", Font.PLAIN, 12);
        leftFlowLayout = new FlowLayout(FlowLayout.LEFT);

        final JLabel label = new JLabel("hallo");
        label.setFont(labelFont);

        final FontMetrics metrics = label.getFontMetrics(labelFont);
        charWidth = metrics.charWidth('m');
        charHeight = metrics.getHeight();
        frameHeight = (charHeight * 5) + (charHeight * 25) + (charHeight * 2);
        frameWidth = (int) (frameHeight * 0.80);
    }

    private JPanel topPanel;
    private JPanel mainPanel;
    private JPanel bottomPanel;
    private String[] rowNames = {
        "Yellow", "Green", "White", "Red", "Blue", "Purple", "Magenta", "Cyan"
    };

    //	private String[] rowNames = {"Yellow Goeie Griet! Sommer nog", "Green", "White", "Red", "Blue", "Purple", "Magenta", "Cyan" };
    //	private String[] rowNames = { "Magenta", "Cyan" };
    private ElegantFrame() throws HeadlessException {
        super();
        setSize(frameWidth, frameHeight);

        final String title = "Insert Row";
        setTitle(title);
        getContentPane().setLayout(new BorderLayout());

        createTopPanel();

        createMainPanel();

        createBottomPanel();

        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        validate();
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
        final JButton clear = new JButton("Clear");
        final JButton ok = new JButton("Finish");
        ok.addActionListener(e -> System.exit(0));

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
        formattedNames[0] = "Column Name";

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
        line.add(new ElegantLabel("Value"));
        gridPanel.add(line);

        final int inputSize = (int) ((frameWidth - textWidth) / charWidth) - 2;

        for (int i = 1; i < formattedNames.length; i++) {
            line = new JPanel(leftFlowLayout);
            labelPanel = new JPanel(leftFlowLayout);
            labelPanel.setPreferredSize(
                new Dimension(textWidth, labelFont.getSize() * 2));

            labelPanel.add(new ElegantLabel(formattedNames[i]));
            line.add(labelPanel);
            line.add(new JTextField(inputSize));
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

    public static void main(final String[] args) {
        final ElegantFrame frame = new ElegantFrame();
        frame.setVisible(true);
    }

    private class ElegantLabel extends JLabel {
        private ElegantLabel(final String text) {
            super(text);
            this.setFont(labelFont);
        }
    }
}
