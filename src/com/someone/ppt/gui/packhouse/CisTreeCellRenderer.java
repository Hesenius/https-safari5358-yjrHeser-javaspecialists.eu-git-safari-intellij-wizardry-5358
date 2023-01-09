package com.someone.ppt.gui.packhouse;

import com.someone.ppt.io.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;


class CisTreeCellRenderer extends DefaultTreeCellRenderer {

    private IconRenderer liner;
    private IconRenderer dropper;
    private IconRenderer stationer;
    private IconRenderer cisser;
    private IconRenderer tabler;
    private IconRenderer packhouser;

    public CisTreeCellRenderer() {
        super();

        final ClassLoader classLoader = this.getClass().getClassLoader();

        final String LINE_GIF = "icons/arrow01.gif";
        liner = new IconRenderer(new ImageIcon(classLoader.getResource(LINE_GIF)));
        final String DROP_GIF = "icons/gauge.gif";
        dropper = new IconRenderer(new ImageIcon(classLoader.getResource(DROP_GIF)));
        final String STATION_GIF = "icons/user.gif";
        stationer = new IconRenderer(new ImageIcon(classLoader.getResource(STATION_GIF)));
        final String CIS_GIF = "icons/World.gif";
        cisser = new IconRenderer(new ImageIcon(classLoader.getResource(CIS_GIF)));
        final String TABLE_GIF = "icons/box.gif";
        tabler = new IconRenderer(new ImageIcon(classLoader.getResource(TABLE_GIF)));
        final String PACKHOUSE_GIF = "icons/home.gif";
        packhouser = new IconRenderer(new ImageIcon(classLoader.getResource(PACKHOUSE_GIF)));

        final String EDIT_GIF = "icons/blueright.gif";
        final ImageIcon icon = new ImageIcon(classLoader.getResource(EDIT_GIF));
        setClosedIcon(icon);
        setOpenIcon(icon);
        setDisabledIcon(icon);
        setIcon(icon);
    }

    public Component getTreeCellRendererComponent(final JTree tree, final Object value,
                                                  final boolean selected,
                                                  final boolean expanded,
                                                  final boolean leaf, final int row,
                                                  final boolean hasFocus) {

        String node = null;
        if (value != null) {
            node = value.toString();
        }

        if (node == null) {
//            return liner.getTreeCellRendererComponent(tree, value, selected,
//                                                      expanded, leaf, row,
//                                                      hasFocus);
            return null;
        }

        if (node.indexOf("Line") != -1) {
            return liner.getTreeCellRendererComponent(tree, value, selected,
                expanded, leaf, row,
                hasFocus);
        } else if (node.indexOf("Drop") != -1) {
            return dropper.getTreeCellRendererComponent(tree, value, selected,
                expanded, leaf, row,
                hasFocus);
        } else if (node.indexOf("Station") != -1) {
            return stationer.getTreeCellRendererComponent(tree, value, selected,
                expanded, leaf, row,
                hasFocus);
        } else if (node.indexOf("CMS") != -1) {
            return cisser.getTreeCellRendererComponent(tree, value, selected,
                expanded, leaf, row,
                hasFocus);
        } else if (node.indexOf("Table") != -1) {
            return tabler.getTreeCellRendererComponent(tree, value, selected,
                expanded, leaf, row,
                hasFocus);
        } else if (node.indexOf("Packhouse") != -1) {
            return packhouser.getTreeCellRendererComponent(tree, value, selected,
                expanded, leaf, row,
                hasFocus);
        } else {
            super.getTreeCellRendererComponent(tree, value, selected, expanded,
                leaf, row, hasFocus);

            return this;
        }
    }

    private class IconRenderer extends DefaultTreeCellRenderer {
        private ImageIcon icon;

        private IconRenderer(final ImageIcon icon) {
            this.icon = icon;
            setClosedIcon(icon);
            setOpenIcon(icon);
        }

        public Component getTreeCellRendererComponent(final JTree tree, final Object value,
                                                      final boolean selected,
                                                      final boolean expanded,
                                                      final boolean leaf, final int row,
                                                      final boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded,
                leaf, row, hasFocus);

            final String node = value.toString();

            final JPanel panel = new JPanel();
            panel.setBackground(Color.lightGray);

            panel.add(new JLabel(icon));

            final JLabel name = new JLabel(LayoutDataSource.getNodeName(node));

            if (selected || hasFocus) {
                panel.setBackground(Color.blue);
            }

            panel.add(name);

            return panel;
        }
    }
}
