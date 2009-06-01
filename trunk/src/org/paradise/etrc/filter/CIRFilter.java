package org.paradise.etrc.filter;

import java.io.File;

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
		return "cir 线路描述文件";
	}
}

