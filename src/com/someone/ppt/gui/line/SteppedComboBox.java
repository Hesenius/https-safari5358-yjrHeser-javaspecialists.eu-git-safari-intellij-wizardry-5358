package com.someone.ppt.gui.line;
    /* (swing1.1) */

import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;
import java.awt.*;
import java.util.*;

/**
 * @version 1.0 12/12/98
 */
class SteppedComboBoxUI extends MetalComboBoxUI {
    protected ComboPopup createPopup() {
        final BasicComboPopup popup = new BasicComboPopup(comboBox) {

            public void show() {
                final Dimension popupSize = ((SteppedComboBox) comboBox).getPopupSize();
                popupSize.setSize(popupSize.width,
                    getPopupHeightForRowCount(comboBox.getMaximumRowCount()));
                final Rectangle popupBounds = computePopupBounds(0,
                    comboBox.getBounds().height, popupSize.width, popupSize.height);
                scroller.setMaximumSize(popupBounds.getSize());
                scroller.setPreferredSize(popupBounds.getSize());
                scroller.setMinimumSize(popupBounds.getSize());
                list.invalidate();
                final int selectedIndex = comboBox.getSelectedIndex();
                if (selectedIndex == -1) {
                    list.clearSelection();
                } else {
                    list.setSelectedIndex(selectedIndex);
                }
                list.ensureIndexIsVisible(list.getSelectedIndex());
                setLightWeightPopupEnabled(comboBox.isLightWeightPopupEnabled());

                show(comboBox, popupBounds.x, popupBounds.y);
            }
        };
        popup.getAccessibleContext().setAccessibleParent(comboBox);
        return popup;
    }
}


class SteppedComboBox extends JComboBox {
    private int popupWidth;

    public SteppedComboBox(final ComboBoxModel aModel) {
        super(aModel);
        setUI(new SteppedComboBoxUI());
        popupWidth = 0;
    }

    public SteppedComboBox(final Object[] items) {
        super(items);
        setUI(new SteppedComboBoxUI());
        popupWidth = 0;
    }

    public SteppedComboBox(final Vector items) {
        super(items);
        setUI(new SteppedComboBoxUI());
        popupWidth = 0;
    }


    public void setPopupWidth(final int width) {
        popupWidth = width;
    }

    public Dimension getPopupSize() {
        final Dimension size = getSize();
        if (popupWidth < 1) popupWidth = size.width;
        return new Dimension(popupWidth, size.height);
    }

    public void updateUI() {
        //do nothing explicitly
    }


}



