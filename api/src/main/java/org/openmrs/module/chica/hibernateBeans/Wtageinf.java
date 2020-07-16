package org.openmrs.module.chica.hibernateBeans;

import org.openmrs.module.chica.Percentile;
import org.openmrs.module.chirdlutilbackports.BaseChirdlMetadata;

/**
 * Holds information to store in the chica_wtageinf table
 * 
 * @author Tammy Dugan
 * @version 1.0
 */
public class Wtageinf extends BaseChirdlMetadata implements java.io.Serializable,Percentile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
	private Integer wtageinfId = null;
	private Double s = null;
	private Double m = null;
	private Double l = null;
	private Double agemos = null;
	private Integer sex = null;
	private String name = null;
	private String description = null;

	// Constructors

	/** default constructor */
	public Wtageinf() {
	}

	/**
	 * @return the wtageinfId
	 */
	public Integer getWtageinfId()
	{
		return this.wtageinfId;
	}

	/**
	 * @param wtageinfId the wtageinfId to set
	 */
	public void setWtageinfId(Integer wtageinfId)
	{
		this.wtageinfId = wtageinfId;
	}

	/**
	 * @return the s
	 */
	public Double getS()
	{
		return this.s;
	}

	/**
	 * @param s the s to set
	 */
	public void setS(Double s)
	{
		this.s = s;
	}

	/**
	 * @return the m
	 */
	public Double getM()
	{
		return this.m;
	}

	/**
	 * @param m the m to set
	 */
	public void setM(Double m)
	{
		this.m = m;
	}

	/**
	 * @return the l
	 */
	public Double getL()
	{
		return this.l;
	}

	/**
	 * @param l the l to set
	 */
	public void setL(Double l)
	{
		this.l = l;
	}

	/**
	 * @return the agemos
	 */
	public Double getAgemos()
	{
		return this.agemos;
	}

	/**
	 * @param agemos the agemos to set
	 */
	public void setAgemos(Double agemos)
	{
		this.agemos = agemos;
	}

	/**
	 * @return the sex
	 */
	public Integer getSex()
	{
		return this.sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(Integer sex)
	{
		this.sex = sex;
	}

	@Override
	public Integer getId() {
		return getWtageinfId();
	}

	@Override
	public void setId(Integer id) {
		setWtageinfId(id);
		
	}
	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return this.description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
}