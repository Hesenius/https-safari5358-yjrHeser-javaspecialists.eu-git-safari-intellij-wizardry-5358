package com.someone.ppt.gui.packhouse;

import com.someone.ppt.io.*;
import com.someone.ppt.models.*;
import com.someone.xml.*;
import org.jdom.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


class PackhouseTree extends JPanel implements ActionListener {
    private JTree packhouseTree;
    private DefaultTreeModel packhouseModel;
    private CisTreeNode rootNode;
    private CisTreeNode current; // selected node
    private CisTreeNode clone; // cut & paste
    private Element layout;
    private int x; // event coordinates, set by mouse listener
    private int y; // event coordinates, set by mouse listener
    private Hashtable table;

    public PackhouseTree(final Element layout) {
        table = new Hashtable();

        createTree();
        setTreeRenderers();
        addTreeListeners();

        this.layout = layout;

        buildPackhouse();

        this.setLayout(new GridLayout(1, 0));
        this.add(new JScrollPane(packhouseTree));

        addMouseListener(new MouseListener() {
            public void mouseClicked(final MouseEvent arg0) {
                System.out.println(" tree clicked");
            }

            public void mousePressed(final MouseEvent arg0) {
                System.out.println(" tree pressed");
            }

            public void mouseReleased(final MouseEvent arg0) {
                System.out.println(" tree released");
            }

            public void mouseEntered(final MouseEvent arg0) {
                System.out.println(" tree entered");
            }

            public void mouseExited(final MouseEvent arg0) {
                System.out.println(" tree exit");
            }
        });
    }

    private void createTree() {
        final Object cisNode = SchemaParser.getInstance().getRootElement();
        rootNode = new CisTreeNode(cisNode);
        packhouseModel = new DefaultTreeModel(rootNode);
        packhouseTree = new JTree(packhouseModel);
        packhouseTree.setEditable(true);
        packhouseTree.setShowsRootHandles(true);
        packhouseTree.setBackground(Color.lightGray);
        packhouseTree.getSelectionModel()
            .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    private void setTreeRenderers() {
        final TreeCellEditor cellEditor = new CisCellEditor(new JTextField());
        final TreeCellEditor editor = new DefaultTreeCellEditor(packhouseTree,
            new DefaultTreeCellRenderer(),
            cellEditor);
        packhouseTree.setCellEditor(editor);

        packhouseTree.setCellRenderer(new CisTreeCellRenderer());
    }

    private void addTreeListeners() {
        packhouseTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent e) {
                final JTree t = (JTree) e.getSource();
                final int selRow = packhouseTree.getRowForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    if (e.getClickCount() == 1) {
                        if (e.getModifiers() == MouseEvent.BUTTON3_MASK) { //right click
                            final JViewport v = (JViewport) t.getParent();
                            final Point p = v.getViewPosition();

                            x = e.getX();
                            y = e.getY();

                            if (p.getY() > 0) {
                                y = y - (int) p.getY();
                            }

                            createPopup();
                        }

                        packhouseTree.treeDidChange();
                    } else if (e.getClickCount() == 2) {
                        packhouseTree.startEditingAtPath(
                            packhouseTree.getSelectionPath());
                        packhouseTree.treeDidChange();
                    }
                }
            }
        });
    }

    public void read(final String file) {
        // remove current tree from model
        while (rootNode.getChildCount() > 0) {
            final CisTreeNode n = (CisTreeNode) rootNode.getChildAt(0);
            packhouseModel.removeNodeFromParent(n);
        }

        final LayoutDataSource source = new LayoutDataSource(file);
        layout = source.parseFile();

        buildPackhouse();
    }

    private void buildPackhouse() {
        final SchemaElement packhouseTemplate = SchemaParser.getInstance()
            .getFirstLevel();

        final Iterator packhouses = layout.getChildren().iterator();

        while (packhouses.hasNext()) {
            final Element packhouse = (Element) packhouses.next();
            final SchemaElement schemaInstance = (SchemaElement) packhouseTemplate.clone();
            final CisTreeNode packhouseNode = new CisTreeNode(schemaInstance);
            packhouseModel.insertNodeInto(packhouseNode, rootNode,
                rootNode.getChildCount());

            buildChildNodes(packhouse.getChildren().iterator(), packhouseNode,
                schemaInstance);
        }
    }

    private void buildChildNodes(final Iterator children, final CisTreeNode parent,
                                 final SchemaElement template) {
        while (children.hasNext()) {
            final Element child = (Element) children.next();
            final String childName = child.getName();
            SchemaElement schemaInstance = null;

            try {
                final SchemaElement[] templateChoices = SchemaParser.getInstance()
                    .getChoices(template);

                for (int i = 0; i < templateChoices.length; i++) {
                    if (childName.equals(templateChoices[i].getName())) {
                        schemaInstance = templateChoices[i];
                    }
                }

                if (schemaInstance == null) {
                    if (childName.equals("Name")) {
                        template.setSpecial(child.getTextTrim());
                    }

                    continue;
                }


                schemaInstance.setParent(template);
                schemaInstance.setValue(child.getTextTrim());

                final CisTreeNode childNode = new CisTreeNode(schemaInstance);
                packhouseModel.insertNodeInto(childNode, parent,
                    parent.getChildCount());

                if (child.hasChildren()) {
                    buildChildNodes(child.getChildren().iterator(), childNode,
                        schemaInstance);
                }
            } catch (final Exception e) {
                System.out.println("could not find schema for : " +
                    child.getName());
            }
        }
    }

    private void createPopup() {
        final JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem;
        final TreePath currentSelection = packhouseTree.getSelectionPath();
        current = (CisTreeNode) (currentSelection.getLastPathComponent());

        final SchemaElement element = (SchemaElement) current.getUserObject();
        final SchemaElement[] options = element.getChoices();

        if (!(element.getType() == SchemaElement.CHOICE &&
            current.getChildCount() > 0)) {
            for (int i = 0; i < options.length; i++) {
                boolean exist = false;

                if (element.getType() == SchemaElement.SEQUENCE) {
                    final Enumeration e = current.children();

                    while (e.hasMoreElements()) {
                        final CisTreeNode tn = (CisTreeNode) e.nextElement();

                        if (options[i].equals(tn.getUserObject())) {
                            exist = true;

                            break;
                        }
                    }
                }

                if (exist) {
                    continue;
                }

                String child = options[i] + "";
                for (int j = 0; j < Layout.fields.length; j++) {
                    if (child.equals(Layout.fields[j])) {
                        child = LayoutDataSource.termonology[j];
                        break;
                    }
                }

                menuItem = new JMenuItem(child);
                table.put(child, options[i]);
                menuItem.addActionListener(this);
                popup.add(menuItem);
            }
        }

        popup.addSeparator();

        final JMenu menu = new JMenu("Edit");
        menuItem = new JMenuItem("Copy Node");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        if (clone != null) {
            menuItem = new JMenuItem("Paste Node");
            menuItem.addActionListener(this);
            menu.add(menuItem);
            menuItem = new JMenuItem("Paste Next");
            menuItem.addActionListener(this);
            menu.add(menuItem);
        }

        menuItem = new JMenuItem("Delete Node");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        popup.add(menu);

        final int popEnd = y + (int) (popup.getPreferredSize().getHeight());

        if (popEnd > this.getSize().getHeight()) {
            y = (int) (y - popup.getPreferredSize().getHeight());
        }

        popup.show(this, x, y);
    }

    public void actionPerformed(final ActionEvent e) {
        final JMenuItem source = (JMenuItem) (e.getSource());
        final String action = source.getText();

        if (action.equals("Delete Node")) {
            removeCurrentNode();
        } else if (action.equals("Copy Node")) {
            cloneCurrent();
        } else if (action.equals("Paste Node")) {
            pasteClone();
        } else if (action.equals("Paste Next")) {
            pasteCloneNext();
        } else if (table.get(action) != null) {
            addObject(table.get(action));
        }
    }

    public void collapseCurrent() {
        final TreePath currentSelection = packhouseTree.getSelectionPath();
        current = (CisTreeNode) (currentSelection.getLastPathComponent());

        if (current == null) {
            return;
        }

        packhouseTree.collapsePath(packhouseTree.getSelectionPath());
    }

    public void expandCurrent() {
        final TreePath currentSelection = packhouseTree.getSelectionPath();
        current = (CisTreeNode) (currentSelection.getLastPathComponent());

        if (current == null) {
            return;
        }

        final Enumeration e = current.depthFirstEnumeration();

        while (e.hasMoreElements()) {
            final CisTreeNode n = (CisTreeNode) e.nextElement();
            packhouseTree.scrollPathToVisible(new TreePath(n.getPath()));
        }
    }

    public void moveUp() {
        CisTreeNode parentNode = null;
        CisTreeNode currentNode = null;
        final TreePath currentPath = packhouseTree.getSelectionPath();
        final int currentPosition;

        if (currentPath == null) {
            return;
        }

        currentNode = (CisTreeNode) (currentPath.getLastPathComponent());
        parentNode = (CisTreeNode) currentNode.getParent();
        currentPosition = parentNode.getIndex(currentNode);

        if (currentPosition > 0) {
            packhouseModel.removeNodeFromParent(currentNode);
            packhouseModel.insertNodeInto(currentNode, parentNode,
                currentPosition - 1);
        }

        packhouseTree.setSelectionPath(new TreePath(currentNode.getPath()));
    }

    public void moveDown() {
        CisTreeNode parentNode = null;
        CisTreeNode currentNode = null;
        final TreePath currentPath = packhouseTree.getSelectionPath();
        final int currentPosition;

        if (currentPath == null) {
            return;
        }

        currentNode = (CisTreeNode) (currentPath.getLastPathComponent());
        parentNode = (CisTreeNode) currentNode.getParent();
        currentPosition = parentNode.getIndex(currentNode);

        if ((currentPosition + 1) < parentNode.getChildCount()) {
            packhouseModel.removeNodeFromParent(currentNode);
            packhouseModel.insertNodeInto(currentNode, parentNode,
                currentPosition + 1);
        }

        packhouseTree.setSelectionPath(new TreePath(currentNode.getPath()));
    }

    private void addChildren(final CisTreeNode source, final CisTreeNode dest) {
        final Enumeration e = source.children();

        while (e.hasMoreElements()) {
            final CisTreeNode n = (CisTreeNode) e.nextElement();
            final CisTreeNode child = (CisTreeNode) n.clone();
            final SchemaElement element = (SchemaElement) n.getUserObject();
            child.setUserObject(element.clone());
            dest.add(child);

            if (n.getChildCount() > 0) {
                addChildren(n, child);
            }
        }
    }

    private void outputXML(final FileWriter writer, final CisTreeNode source, final int depth)
        throws IOException {
        final SchemaElement e = (SchemaElement) source.getUserObject();
        String pad = "";

        for (int i = 0; i < depth; i++) {
            pad += "  ";
        }

        writer.write(pad + e.xmlOpen() + "\n");

        if (source.getChildCount() > 0) {
            final Enumeration children = source.children();

            while (children.hasMoreElements()) {
                final CisTreeNode n = (CisTreeNode) children.nextElement();
                outputXML(writer, n, depth + 1);
            }
        } else {
            if (e.getValue().equals("")) {
            } else {
                writer.write(pad + "  " + e.getValue() + "\n");
            }
        }

        writer.write(pad + e.xmlClose() + "\n");
    }

    public void output(final File file) throws IOException {
        LayoutDataSink.saveToFile(rootNode, file.getAbsolutePath());
    }

    public void cloneCurrent() {
        final TreePath currentSelection = packhouseTree.getSelectionPath();

        if (currentSelection != null) {
            final CisTreeNode temp = (CisTreeNode) (currentSelection.getLastPathComponent());
            clone = (CisTreeNode) temp.clone();
            addChildren(temp, clone);
        }
    }

    public void pasteClone() {
        CisTreeNode parentNode = null;
        final TreePath parentPath = packhouseTree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else {
            parentNode = (CisTreeNode) (parentPath.getLastPathComponent());
        }

        final CisTreeNode newClone = (CisTreeNode) clone.clone();
        addChildren(clone, newClone);
        packhouseModel.insertNodeInto(clone, parentNode,
            parentNode.getChildCount());
        clone = newClone;
    }

    public void pasteCloneNext() {
        CisTreeNode parentNode = null;
        CisTreeNode currentNode = null;
        final TreePath currentPath = packhouseTree.getSelectionPath();
        final int currentPosition;
        final CisTreeNode newClone = (CisTreeNode) clone.clone();
        addChildren(clone, newClone);

        if (currentPath == null) {
            return;
        }

        currentNode = (CisTreeNode) (currentPath.getLastPathComponent());
        parentNode = (CisTreeNode) currentNode.getParent();
        currentPosition = parentNode.getIndex(currentNode);
        packhouseModel.insertNodeInto(clone, parentNode, currentPosition + 1);
        clone = newClone;
    }

    /**
     * Remove the currently selected node.
     */
    public void removeCurrentNode() {
        final TreePath currentSelection = packhouseTree.getSelectionPath();

        if (currentSelection != null) {
            final CisTreeNode currentNode = (CisTreeNode) (currentSelection.getLastPathComponent());
            final MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());

            if (parent != null) {
                packhouseModel.removeNodeFromParent(currentNode);

                return;
            }
        }
    }

    /**
     * Add child to the currently selected node.
     *
     * @param child DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    private CisTreeNode addObject(final Object child) {
        CisTreeNode parentNode = null;
        final TreePath parentPath = packhouseTree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else {
            parentNode = (CisTreeNode) (parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    private CisTreeNode addObject(CisTreeNode parent, final Object child,
                                  final boolean shouldBeVisible) {
        final CisTreeNode childNode = new CisTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }

        ((SchemaElement) child).setParent(parent.getUserObject());
        packhouseModel.insertNodeInto(childNode, parent, parent.getChildCount());

        final SchemaElement e = (SchemaElement) child;

        if (e.getType() == SchemaElement.SEQUENCE) {
            final SchemaElement[] choices = e.getChoices();

            for (int i = 0; i < choices.length; i++) {
                if (choices[i].isCompulsory()) {
                    addObject(childNode, choices[i], true);
                }
            }
        }

        if (shouldBeVisible) {
            packhouseTree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }

        return childNode;
    }

    public CisTreeNode getRootNode() {
        return rootNode;
    }

}
