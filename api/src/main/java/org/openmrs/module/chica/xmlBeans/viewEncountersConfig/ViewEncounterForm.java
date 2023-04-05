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
		return this.name;
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
		return this.displayPosition;
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
		return this.stylesheet;
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
		return this.directory;
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
		String[] stateNamesArray = this.stateNames.split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
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
		return this.relatedForms;
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
		result = prime * result + ((this.directory == null) ? 0 : this.directory.hashCode());
		result = prime * result + ((this.displayPosition == null) ? 0 : this.displayPosition.hashCode());
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + ((this.relatedForms == null) ? 0 : this.relatedForms.hashCode());
		result = prime * result + ((this.stylesheet == null) ? 0 : this.stylesheet.hashCode());
		result = prime * result + ((this.stateNames == null) ? 0 : this.stateNames.hashCode());
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
		if (this.directory == null) {
			if (other.directory != null) {
				return false;
			}
		} else if (!this.directory.equals(other.directory)) {
			return false;
		}
		if (this.displayPosition == null) {
			if (other.displayPosition != null) {
				return false;
			}
		} else if (!this.displayPosition.equals(other.displayPosition)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.relatedForms == null) {
			if (other.relatedForms != null) {
				return false;
			}
		} else if (!this.relatedForms.equals(other.relatedForms)) {
			return false;
		}
		if (this.stylesheet == null) {
			if (other.stylesheet != null) {
				return false;
			}
		} else if (!this.stylesheet.equals(other.stylesheet)) {
			return false;
		}
		
		if (this.stateNames == null) {
			if (other.stateNames != null) {
				return false;
			}
		} else if (!this.stateNames.equals(other.stateNames)) {
			return false;
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ViewEncounterForm [name=" + this.name + ", displayPosition=" + this.displayPosition + ", stylesheet=" + this.stylesheet
				+ ", directory=" + this.directory + ", stateNames=" + this.stateNames + ", relatedForms=" + this.relatedForms + "]";
	}
}
