package org.paradise.etrc.dview;

import java.awt.*;
import javax.swing.JPanel;

import org.paradise.etrc.data.*;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class DirectPanel extends JPanel {
	private static final long serialVersionUID = 2449059376608773861L;

	private Chart chart;
	private DynamicView dView;

	public DirectPanel(Chart _chart, DynamicView _dView) {
		chart = _chart;
		dView = _dView;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		this.setLayout(new BorderLayout());
	}

	public void paint(Graphics g) {
		super.paint(g);
		
		g.drawLine(3, 
				   dView.topMargin, 
				   this.getWidth(), 
				   dView.topMargin);
		
		g.drawLine(3, 
				   this.getHeight() - dView.bottomMargin, 
				   this.getWidth(), 
				   this.getHeight() - dView.bottomMargin);
	}

	public Dimension getPreferredSize() {
		int w, h;
		w = dView.directPanelWeight;
		h = dView.runningPanelHeight;
		return new Dimension(w, h);
	}
}
