package org.paradise.etrc.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;
import static org.paradise.etrc.ETRC._;

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
		return _("GIF File (*.gif)");
	}

}
