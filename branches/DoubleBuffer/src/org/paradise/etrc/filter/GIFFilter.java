package org.paradise.etrc.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;
import org.paradise.etrc.ETRC;

public class GIFFilter extends FileFilter {

	@Override
	public boolean accept(File pathname) {
		if (pathname.getName().endsWith("gif") || pathname.isDirectory())
			return true;
		else
			return false;
	}

	@Override
	public String getDescription() {
		return ETRC.getString("GIF File (*.gif)");
	}

}
