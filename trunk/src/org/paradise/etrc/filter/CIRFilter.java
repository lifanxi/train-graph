package org.paradise.etrc.filter;

import java.io.File;
import org.paradise.etrc.ETRC;

public class CIRFilter extends javax.swing.filechooser.FileFilter {

	/**
	 * accept
	 *
	 * @param pathname File
	 * @return boolean
	 */
	public boolean accept(File pathname) {
		if (pathname.getName().endsWith("cir") || pathname.isDirectory())
			return true;
		else
			return false;
	}

	public String getDescription() {
		return ETRC.getString("Line Description File (*.cir)");
	}
}

