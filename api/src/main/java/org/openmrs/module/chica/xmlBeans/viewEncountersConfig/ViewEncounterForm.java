package org.openmrs.module.chica.xmlBeans.viewEncountersConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;

public class ViewEncounterForm {
	
	private String name;
	private String displayPosition;
	private String stylesheet;
	private String directory;
	private String stateNames; // This can be a comma separated list of state names
	private ArrayList<ViewEncounterForm> relatedForms;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the displayPosition
	 */
	public String getDisplayPosition() {
		return displayPosition;
	}
	/**
	 * @param displayPosition the displayPosition to set
	 */
	public void setDisplayPosition(String displayPosition) {
		this.displayPosition = displayPosition;
	}
	/**
	 * @return the stylesheet
	 */
	public String getStylesheet() {
		return stylesheet;
	}
	/**
	 * @param stylesheet the stylesheet to set
	 */
	public void setStylesheet(String stylesheet) {
		this.stylesheet = stylesheet;
	}
	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return directory;
	}
	/**
	 * @param directory the directory to set
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	/**
	 * Creates a list of stateNames from the comma separated list of states in the stateNames field
	 * @return the list of stateNames
	 */
	public List<String> getStateNames() {
		List<String> stateNamesList = new ArrayList<String>();
		String[] stateNamesArray = stateNames.split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
		if(stateNamesArray != null)
		{
			stateNamesList = Arrays.asList(stateNamesArray);
		}
		return stateNamesList;
	}
	/**
	 * @param stateNames the stateNames to set
	 */
	public void setStateNames(String stateNames) {
		this.stateNames = stateNames;
	}
	/**
	 * @return the relatedForms
	 */
	public ArrayList<ViewEncounterForm> getRelatedForms() {
		return relatedForms;
	}
	/**
	 * @param relatedForms the relatedForms to set
	 */
	public void setRelatedForms(ArrayList<ViewEncounterForm> relatedForms) {
		this.relatedForms = relatedForms;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((directory == null) ? 0 : directory.hashCode());
		result = prime * result + ((displayPosition == null) ? 0 : displayPosition.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((relatedForms == null) ? 0 : relatedForms.hashCode());
		result = prime * result + ((stylesheet == null) ? 0 : stylesheet.hashCode());
		result = prime * result + ((stateNames == null) ? 0 : stateNames.hashCode());
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
		if (!(obj instanceof ViewEncounterForm)) {
			return false;
		}
		ViewEncounterForm other = (ViewEncounterForm) obj;
		if (directory == null) {
			if (other.directory != null) {
				return false;
			}
		} else if (!directory.equals(other.directory)) {
			return false;
		}
		if (displayPosition == null) {
			if (other.displayPosition != null) {
				return false;
			}
		} else if (!displayPosition.equals(other.displayPosition)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (relatedForms == null) {
			if (other.relatedForms != null) {
				return false;
			}
		} else if (!relatedForms.equals(other.relatedForms)) {
			return false;
		}
		if (stylesheet == null) {
			if (other.stylesheet != null) {
				return false;
			}
		} else if (!stylesheet.equals(other.stylesheet)) {
			return false;
		}
		
		if (stateNames == null) {
			if (other.stateNames != null) {
				return false;
			}
		} else if (!stateNames.equals(other.stateNames)) {
			return false;
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ViewEncounterForm [name=" + name + ", displayPosition=" + displayPosition + ", stylesheet=" + stylesheet
				+ ", directory=" + directory + ", stateNames=" + stateNames + ", relatedForms=" + relatedForms + "]";
	}
}
