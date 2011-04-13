/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chica.action;

import java.io.File;
import java.io.FileFilter;
import java.util.Calendar;


/**
 * This class filters files with timestamps within a timePeriod
 *
 */
public class FileFilterByDate implements FileFilter {

	private Integer timePeriod = null;
	private String filenameEndsWith = null;
	
	public FileFilterByDate(Integer milliseconds,String filenameEndsWith){
    	this.timePeriod=milliseconds;
    	this.filenameEndsWith=filenameEndsWith;
	}
	
	/**
     * @see java.io.FileFilter#accept(java.io.File)
     */
    public boolean accept(File file) {
    	
    	if(this.timePeriod == null){
    		return false;
    	}
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.MILLISECOND, this.timePeriod*-1);
    	if(file.lastModified()>=calendar.getTime().getTime()){
    		if(file.getPath().endsWith(this.filenameEndsWith)){
    			return true;
    		}
    	}
    	
	    return false;
    }
    
}
