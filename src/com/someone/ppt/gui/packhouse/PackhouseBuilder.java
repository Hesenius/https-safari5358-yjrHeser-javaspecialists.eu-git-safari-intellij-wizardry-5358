package com.someone.ppt.gui.packhouse;

import com.someone.db.*;
import com.someone.gui.*;
import com.someone.ppt.gui.*;
import com.someone.ppt.gui.db.*;
import com.someone.ppt.io.*;
import com.someone.ppt.models.*;
import com.someone.xml.*;
import org.jdom.*;
import org.jdom.input.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class PackhouseBuilder extends CmsFrame {
    private Element layout;
    private PackhouseTree packhouseTree;
    private Packhouse packhouse;

    public PackhouseBuilder(final CmsGui mainGui, final Packhouse packhouse) {
        super(mainGui, "Packhouse Builder: " + packhouse.getPackhouseName());
        this.packhouse = packhouse;
        this.layout = packhouse.getXmlLayout();
        readConfig();
        initGui();
    }

    private void initGui() {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        packhouseTree = new PackhouseTree(layout);

        final JPanel packhousePanel = new JPanel();
        packhousePanel.setLayout(new BorderLayout());
        packhousePanel.add(packhouseTree, BorderLayout.CENTER);

        contentPane.add(createToolBar(), BorderLayout.NORTH);
        contentPane.add(packhousePanel, BorderLayout.CENTER);
    }

    protected void defineLocalMenus() {
        guiType = PACKHOUSE_GUI;

        JMenuItem item;
        item = new JMenuItem("Load Layout");
        item.setMnemonic('l');
        item.addActionListener(e -> loadPackhouse());

        menuFile.add(item);
        item = new JMenuItem("Save Layout");
        item.setMnemonic('s');
        item.addActionListener(e -> savePackhouse());
        menuFile.add(item);
    }

    private void loadPackhouse() {
        final ComboSelectionFrame f = new ComboSelectionFrame(
            "Select Packhouse",
            "packhouselayout",
            "PackhouseName");

        final Runnable modifier = () -> {
            if (f.isModified()) {
                try {
                    final String packhouseName = f.getSelectedValue();
                    mainGui.loadData(new Packhouse(packhouseName));
                    PackhouseBuilder.this.setVisible(false);
                    mainGui.initGui(CmsFrame.PACKHOUSE_GUI);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else {
                // do nothing
            }
        };

        f.setCloseAction(modifier);

        f.setVisible(true);
    }

    private void savePackhouse() {

        final String[] columnNames = {"Packhouse Name"};
        final DataInputFrame frame = new DataInputFrame("Save Packhouse As", columnNames, false);
        final String[] data = {packhouse.getPackhouseName()};
        frame.setData(data);

        final Runnable modifier = () -> {
            if (frame.isModified()) {
                try {
                    final String packhouseName = frame.getNewData()[0];
                    new PackhouseDBSink(packhouseName, packhouseTree.getRootNode());
                    mainGui.loadData(new Packhouse(packhouseName));
                    PackhouseBuilder.this.setVisible(false);
                    mainGui.initGui(CmsFrame.PACKHOUSE_GUI);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else {
                // do nothing
            }
        };

        frame.setCloseAction(modifier);
        frame.setVisible(true);

    }

    private JPanel createToolBar() {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        JButton button;
        final JPanel toolPanel = new JPanel();

        final Border bottomBorder = BorderFactory.createEtchedBorder();
        toolPanel.setBorder(bottomBorder);
        toolPanel.setBackground(Color.white);
        toolPanel.setPreferredSize(
            new Dimension(20, GuiTools.getCharHeight() * 3));

        button = new JButton(
            new ImageIcon(classLoader.getResource(
                "icons/blueup.gif")));
        button.addActionListener(e -> packhouseTree.moveUp());
        toolPanel.add(button);

        button = new JButton(
            new ImageIcon(classLoader.getResource(
                "icons/bluedown.gif")));
        button.addActionListener(e -> packhouseTree.moveDown());

        toolPanel.add(button);

        button = new JButton(
            new ImageIcon(classLoader.getResource(
                "icons/blueright.gif")));
        button.addActionListener(e -> packhouseTree.expandCurrent());
        toolPanel.add(button);

        button = new JButton(
            new ImageIcon(classLoader.getResource(
                "icons/blueleft.gif")));
        button.addActionListener(e -> packhouseTree.collapseCurrent());
        toolPanel.add(button);
        button = new JButton("copy");
        button.addActionListener(e -> packhouseTree.cloneCurrent());
        toolPanel.add(button);
        button = new JButton("delete");
        button.addActionListener(e -> packhouseTree.removeCurrentNode());
        toolPanel.add(button);
        button = new JButton("paste");
        button.addActionListener(e -> packhouseTree.pasteClone());
        toolPanel.add(button);
        button = new JButton("paste next");
        button.addActionListener(e -> packhouseTree.pasteCloneNext());
        toolPanel.add(button);

        toolPanel.setVisible(true);

        return toolPanel;
    }

    private void readConfig() {
        final String schemaFile;
        final String namespace;
        final String nsURL;
        final String rootElement;

        try {
            final SAXBuilder builder = new SAXBuilder();
            final Document language = builder.build(this.getClass()
                .getClassLoader()
                .getResource("config/config.xml"));
            final Element root = language.getRootElement();
            schemaFile = root.getChildText("Schema");
            namespace = root.getChildText("Namespace");
            nsURL = root.getChildText("nsURL");
            rootElement = root.getChildText("RootNode");

            for (int i = 0; i < Layout.fields.length; i++) {
                LayoutDataSource.termonology[i] = root.getChildText(
                    Layout.fields[i]);

                if (LayoutDataSource.termonology[i] == null) {
                    LayoutDataSource.termonology[i] = Layout.fields[i];
                }
            }

            SchemaParser.getInstance(schemaFile, rootElement, namespace, nsURL);
        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println(
                "error reading config file in ./config/config.xml");
        }
    }
}

