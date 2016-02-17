package org.paradise.etrc.wizard.addtrain;

import org.paradise.etrc.data.Stop;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class TrainTableCellRanderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = -3005467491964709634L;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!(comp instanceof JLabel))
            return comp;

        JLabel lb = (JLabel) comp;

        Stop stop = (Stop) value;
        // 设置文字
        lb.setText((value == null) ? "" : ((column == 1) ? stop.arrive : stop.leave));
        lb.setHorizontalAlignment(SwingConstants.CENTER);

        return lb;
    }
}
