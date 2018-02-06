package org.openmrs.module.chica.xmlBeans.viewEncountersConfig;

public class ViewEncountersConfig {
	
	private FormsToDisplay formsToDisplay;

	/**
	 * @return the formsToDisplay
	 */
	public FormsToDisplay getFormsToDisplay() {
		return formsToDisplay;
	}

	/**
	 * @param formsToDisplay the formsToDisplay to set
	 */
	public void setFormsToDisplay(FormsToDisplay formsToDisplay) {
		this.formsToDisplay = formsToDisplay;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formsToDisplay == null) ? 0 : formsToDisplay.hashCode());
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
		if (!(obj instanceof ViewEncountersConfig)) {
			return false;
		}
		ViewEncountersConfig other = (ViewEncountersConfig) obj;
		if (formsToDisplay == null) {
			if (other.formsToDisplay != null) {
				return false;
			}
		} else if (!formsToDisplay.equals(other.formsToDisplay)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ViewEncountersConfig [formsToDisplay=" + formsToDisplay + "]";
	}
}
