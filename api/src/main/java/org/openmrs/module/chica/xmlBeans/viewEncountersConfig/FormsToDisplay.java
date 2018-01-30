package org.openmrs.module.chica.xmlBeans.viewEncountersConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormsToDisplay {
	
	private ArrayList<ViewEncounterForm> forms;

	/**
	 * @return the forms
	 */
	public ArrayList<ViewEncounterForm> getForms() {
		return forms;
	}

	/**
	 * @param forms the forms to set
	 */
	public void setForms(ArrayList<ViewEncounterForm> forms) {
		this.forms = forms;
	}
	
	/**
	 * Returns a list of names for all the forms in the formsToDisplay section
	 * @return
	 */
	public List<String> getFormNames(){
		ArrayList<String> formDisplayNames = new ArrayList<String>();
		for(ViewEncounterForm form : forms){
			formDisplayNames.add(form.getName());
		}
		return formDisplayNames;
	}
	
	/**
	 * Returns a map of ViewEncounterForm objects
	 * @return
	 */
	public Map<String, ViewEncounterForm> getViewEncounterFormMap(){
		Map<String, ViewEncounterForm> viewEncounterFormMap = new HashMap<>();
		
		for(ViewEncounterForm form : forms){
			viewEncounterFormMap.put(form.getName(), form);
		}
		
		return viewEncounterFormMap;
	}
}
