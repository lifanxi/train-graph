package org.paradise.etrc.view.sheet;

import org.paradise.etrc.data.Train;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableCellRenderer;

public class SheetHeaderRanderer extends JLabel implements TableCellRenderer {
    private static final long serialVersionUID = -3005467491964709634L;

    public SheetHeaderRanderer() {
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setFont(new Font("Dialog", Font.BOLD, 12));
        setBackground(SheetView.headerBK);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String trainName = (value == null) ? "" : value.toString();

        setText(trainName);
        setForeground(Train.getTrainColorByName(trainName));

        if (table.getSelectedColumn() == column)
            setBackground(SheetView.selectBK);
        else
            setBackground(SheetView.headerBK);

        return this;
    }

}
