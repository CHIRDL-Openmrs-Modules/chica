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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((forms == null) ? 0 : forms.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FormsToDisplay)) {
			return false;
		}
		FormsToDisplay other = (FormsToDisplay) obj;
		if (forms == null) {
			if (other.forms != null) {
				return false;
			}
		} else if (!forms.equals(other.forms)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FormsToDisplay [forms=" + forms + "]";
	}
}
