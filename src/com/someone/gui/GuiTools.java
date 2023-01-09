package com.someone.gui;

import javax.swing.*;
import java.awt.*;


public class GuiTools {
    private static Font labelFont;
    private static FlowLayout leftFlowLayout;
    private static int charWidth;
    private static int charHeight;

    static {
        labelFont = new Font("Arial", Font.PLAIN, 12);
        leftFlowLayout = new FlowLayout(FlowLayout.LEFT);

        final JLabel label = new JLabel("hallo");
        label.setFont(labelFont);

        final FontMetrics metrics = label.getFontMetrics(labelFont);
        charWidth = metrics.charWidth('m');
        charHeight = metrics.getHeight();
    }

    public static int getCharHeight() {
        return charHeight;
    }

    public static int getCharWidth() {
        return charWidth;
    }

    public static Font getLabelFont() {
        return labelFont;
    }

    public static FlowLayout getLeftFlowLayout() {
        return leftFlowLayout;
    }
}
