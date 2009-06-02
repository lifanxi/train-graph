package org.paradise.etrc.filter;

import java.io.File;

public class TRCFilter extends javax.swing.filechooser.FileFilter {

	/**
	 * accept
	 *
	 * @param pathname File
	 * @return boolean
	 */
	public boolean accept(File pathname) {
		if (pathname.getName().endsWith("trc") || pathname.isDirectory())
			return true;
		else
			return false;
	}

	public String getDescription() {
		return "trc 列车运行图文件";
	}
}
