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
package org.openmrs.module.chica;

import java.util.ArrayList;
import java.util.List;


/**
 * Bean to hold information that will be displayed at the end of the tablet process.
 * 
 * @author Steve McKee
 */
public class TabletNotification {
	
	private String statement;
	private List<String> subStatements;
	
    /**
     * @return the statement
     */
    public String getStatement() {
    	return statement;
    }
	
    /**
     * @param statement the statement to set
     */
    public void setStatement(String statement) {
    	this.statement = statement;
    }
	
    /**
     * @return the subStatements
     */
    public List<String> getSubStatements() {
    	if (subStatements == null) {
    		return new ArrayList<String>();
    	}
    	
    	return subStatements;
    }
	
    /**
     * @param subStatements the subStatements to set
     */
    public void setSubStatements(List<String> subStatements) {
    	this.subStatements = subStatements;
    }
    
    /**
     * @param subStatement The subStatement to add
     */
    public void addSubStatement(String subStatement) {
    	if (subStatements == null) {
    		subStatements = new ArrayList<String>();
    	}
    	
    	subStatements.add(subStatement);
    }
}
