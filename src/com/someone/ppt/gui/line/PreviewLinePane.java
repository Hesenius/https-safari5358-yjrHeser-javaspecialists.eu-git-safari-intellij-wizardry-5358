package com.someone.ppt.gui.line;

import com.someone.db.*;
import com.someone.gui.*;
import com.someone.ppt.gui.db.*;
import com.someone.ppt.models.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

class PreviewLinePane extends JPanel {
    private JScrollPane northPane;
    private JPanel centerPanel;
    private String[][] data;
    private String templateName;

    public PreviewLinePane() {
    }

    public void updateModel() {
        initGui();
    }

    private void loadData() {
        final ComboSelectionFrame f = new ComboSelectionFrame(
            "Select Template",
            "templates",
            "TemplateName");
        f.setCloseAction(() -> {
            final TemplateDBSource source = new TemplateDBSource(
                "doesnotmatter");
            final String[][] newData =
                source.extractTemplate(f.getSelectedValue(), 1);


            data = new String[newData.length][newData[0].length - 2];
            for (int i = 0; i < newData.length; i++) {
                for (int j = 2; j < newData[0].length; j++) {
                    data[i][j - 2] = newData[i][j];
                }
            }

            templateName = f.getSelectedValue();
            SwingUtilities.invokeLater(this::initGui);

        });

        f.setVisible(true);

    }


    private void initGui() {
        if (northPane != null) {
            remove(northPane);
            if (centerPanel != null) {
                remove(centerPanel);
            }
        } else {
            setLayout(new BorderLayout());
            setSize(400, 400);
        }

        northPane = initNorth();
        add(northPane, BorderLayout.NORTH);

        if (data == null) {
            return;
        }

        final JTable table = new JTable(data, FruitspecTableModel.columnNames);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        final JScrollPane tablePanel = new JScrollPane(table);

        centerPanel = new JPanel(new BorderLayout());

        centerPanel.add(tablePanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        revalidate();
        updateUI();
    }

    private JScrollPane initNorth() {
        final JPanel northPane = new JPanel(GuiTools.getLeftFlowLayout());
        northPane.setPreferredSize(
            new Dimension(800, GuiTools.getCharHeight() * 3));

        final JScrollPane scroller = new JScrollPane(northPane);
        scroller.setBackground(Color.white);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setPreferredSize(
            new Dimension(800, GuiTools.getCharHeight() * 3));

        final Border topBorder = BorderFactory.createEtchedBorder();
        scroller.setBorder(topBorder);

        final JButton saveButton = new JButton("View");
        saveButton.addActionListener(e -> loadData());
        northPane.add(saveButton);
        if (templateName != null) {
            final JLabel templateLabel = new JLabel(templateName);
            templateLabel.setForeground(Color.BLUE);
            northPane.add(templateLabel);

            final JLabel loaded = new JLabel(" loaded");
            northPane.add(loaded);
        }
        northPane.validate();

        return scroller;
    }

    public String[][] getData() {
        return data;
    }
}
