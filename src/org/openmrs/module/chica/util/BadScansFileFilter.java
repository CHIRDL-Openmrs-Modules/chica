package org.openmrs.module.chica.util;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Filters files in a directory based on the extension.  The file
 *         name must start with the provided text string and the 
 *         last modified time must be greater than the provided 
 *         time.
 *
 * @author Steve McKee
 */
public class BadScansFileFilter implements FilenameFilter {
	private Date date;
	private SimpleDateFormat sdf;
	private List<String> fileExtensionsToIgnore;

	public BadScansFileFilter(Date date, List<String> fileExtensionsToIgnore) {
		this.date = date;
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (fileExtensionsToIgnore == null) {
			fileExtensionsToIgnore = new ArrayList<String>();
		} else {
			this.fileExtensionsToIgnore = fileExtensionsToIgnore;
		}
	}

	public boolean accept(File directory, String filename) {
		boolean ok = true;

		// The File.isDirectory() check is extremely slow, so I chose to test the file
		// extension since the only files in the directories are tif files.
		if (!filename.endsWith(".tif")) {
			// We don't want the files in here because they've already been 
			// taken care of.
			if ("resolved bad scans".equals(filename)) {
				return false;
			}
			
			Iterator<String> i = fileExtensionsToIgnore.listIterator();
			while(i.hasNext()) {
				String extension = i.next();
				if (filename.endsWith(extension)) {
					return false;
				}
			}
			
			return true;
		}
		
		ok &= (filename.startsWith("~"));
		if (!ok) return false;
		
		if (date != null && ok) {
			String current = sdf.format(date);
			Date lastModDate = new Date(new File(directory, filename).lastModified());
			ok &= (current.compareTo(sdf.format(lastModDate))) == 0;
		}

		return ok;
	}
	
}
