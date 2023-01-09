package com.someone.ppt.gui.line;

import com.someone.gui.*;
import com.someone.ppt.models.*;

import javax.swing.table.*;
import java.awt.*;

class FruitSpecTable extends ElegantTable {
    public FruitSpecTable(final TableModel dm) {
        super(dm);
        getTableHeader().setReorderingAllowed(false);
    }

    public Component prepareRenderer(final TableCellRenderer renderer, final int rowIndex,
                                     final int vColIndex) {
        final Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);

        if (FruitspecTableModel.getUpdateAll(vColIndex)) {
            c.setBackground(new Color(0.0f, 0.4f, 0.9f));
        } else if (FruitspecTableModel.getEveryWhere(vColIndex)) {
            c.setBackground(new Color(0.0f, 0.8f, 0.0f));
        } else {
            c.setBackground(getBackground());
        }

        return c;
    }
}
