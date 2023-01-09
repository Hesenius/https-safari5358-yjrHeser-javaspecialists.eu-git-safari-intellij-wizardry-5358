package com.someone.ppt.gui.packhouse;

import com.someone.xml.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class CisCellEditor extends AbstractCellEditor implements TableCellEditor,
    TreeCellEditor {
    private JComponent editorComponent;
    private EditorDelegate delegate;
    private int clickCountToStart = 2;

    /**
     * Constructs a NewCellEditor that uses a text field.
     */
    public CisCellEditor(final JTextField textField) {
        editorComponent = textField;

        this.clickCountToStart = 2;
        delegate = new EditorDelegate() {
            private SchemaElement myElement;
            private boolean editable;

            public void setValue(final Object value) {
                final CisTreeNode node = (CisTreeNode) value;
                editable = true;
                myElement = (SchemaElement) node.getUserObject();
                textField.setText(myElement.getValue());
            }

            public Object getCellEditorValue() {
                if (editable) {
                    if (textField.getText().equals("")) {
                        myElement.setValue(null);
                    } else {
                        myElement.setSpecial(textField.getText());
                    }
                }

                return myElement;
            }
        };


        textField.addActionListener(delegate);
    }

    /**
     * Returns the a reference to the editor component.
     *
     * @return the editor Component
     */
    public Component getComponent() {
        return editorComponent;
    }

    //
    //  Modifying
    //

    /**
     * Specifies the number of clicks needed to start editing.
     *
     * @param count an int specifying the number of clicks needed to start editing
     * @see #getClickCountToStart
     */
    public void setClickCountToStart(final int count) {
        clickCountToStart = count;
    }

    /**
     * ClickCountToStart controls the number of clicks required to start
     * editing.
     */
    public int getClickCountToStart() {
        return clickCountToStart;
    }

    //
    //  Override the implementations of the superclass, forwarding all methods
    //  from the CellEditor interface to our delegate.
    //
    public Object getCellEditorValue() {
        return delegate.getCellEditorValue();
    }

    public boolean isCellEditable(final EventObject anEvent) {
        return delegate.isCellEditable(anEvent);
    }

    public boolean shouldSelectCell(final EventObject anEvent) {
        return delegate.shouldSelectCell(anEvent);
    }

    public boolean stopCellEditing() {
        return delegate.stopCellEditing();
    }

    public void cancelCellEditing() {
        delegate.cancelCellEditing();
    }

    //
    //  Implementing the TreeCellEditor Interface
    //
    public Component getTreeCellEditorComponent(final JTree tree, final Object value,
                                                final boolean isSelected,
                                                final boolean expanded, final boolean leaf,
                                                final int row) {
        delegate.setValue(value);

        return editorComponent;
    }

    //
    //  Implementing the CellEditor Interface
    //
    public Component getTableCellEditorComponent(final JTable table, final Object value,
                                                 final boolean isSelected, final int row,
                                                 final int column) {
        delegate.setValue(value);

        return editorComponent;
    }

    class EditorDelegate implements ActionListener, ItemListener,
        Serializable {
        private Object value;

        Object getCellEditorValue() {
            return value;
        }

        void setValue(final Object value) {
            this.value = value;
        }

        private boolean isCellEditable(final EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                return ((MouseEvent) anEvent).getClickCount() >= clickCountToStart;
            }

            return true;
        }

        private boolean shouldSelectCell(final EventObject anEvent) {
            return true;
        }

        public boolean startCellEditing(final EventObject anEvent) {
            return true;
        }

        private boolean stopCellEditing() {
            fireEditingStopped();

            return true;
        }

        private void cancelCellEditing() {
            fireEditingCanceled();
        }

        public void actionPerformed(final ActionEvent e) {
            CisCellEditor.this.stopCellEditing();
        }

        public void itemStateChanged(final ItemEvent e) {
            CisCellEditor.this.stopCellEditing();
        }
    }
}
