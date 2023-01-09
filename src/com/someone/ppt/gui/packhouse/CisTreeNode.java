package com.someone.ppt.gui.packhouse;

import com.someone.xml.*;

import javax.swing.tree.*;


public class CisTreeNode extends DefaultMutableTreeNode {
    public CisTreeNode(final Object data) {
        super(data);
    }

    public void setUserObject(final Object obj) {
        if (obj == this) {
            return;
        }

        super.setUserObject(obj);
    }

    public Object clone() {
        return new CisTreeNode(((SchemaElement) userObject).clone());
    }
}
