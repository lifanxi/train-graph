package org.paradise.etrc.filter;

import java.io.File;
import org.paradise.etrc.ETRC;

public class CSVFilter extends javax.swing.filechooser.FileFilter {

	/**
	 * accept
	 *
	 * @param pathname File
	 * @return boolean
	 */
	public boolean accept(File pathname) {
		if (pathname.getName().endsWith("csv") || pathname.isDirectory())
			return true;
		else
			return false;
	}

	public String getDescription() {
		return ETRC.getString("Excel CSV File (*.csv)");
	}
}