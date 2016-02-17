package org.paradise.etrc.filter;

import java.io.File;
import static org.paradise.etrc.ETRC._;

public class TRFFilter extends javax.swing.filechooser.FileFilter {

	/**
	 * accept
	 *
	 * @param pathname File
	 * @return boolean
	 */
	public boolean accept(File pathname) {
		if (pathname.getName().endsWith("trf") || pathname.isDirectory())
			return true;
		else
			return false;
	}

	public String getDescription() {
		return _("Train Description File (*.trf)");
	}
}
