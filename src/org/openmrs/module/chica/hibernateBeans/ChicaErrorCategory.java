package org.openmrs.module.chica.hibernateBeans;

public class ChicaErrorCategory implements java.io.Serializable {
	
	// Fields
	
	private Integer errorCategoryId = null;
	private String name = null;
	private String description = null;
	
	
	//constructor
	public ChicaErrorCategory(){}

	
	/**
	 * @return the errorCategoryId
	 */
	public Integer getErrorCategoryId() {
		return errorCategoryId;
	}


	/**
	 * @param errorCategoryId the errorCategoryId to set
	 */
	public void setErrorCategoryId(Integer errorCategoryId) {
		this.errorCategoryId = errorCategoryId;
	}


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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	
	

	

}