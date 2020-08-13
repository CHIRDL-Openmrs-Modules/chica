package org.openmrs.module.chica.hibernateBeans;

import org.openmrs.module.chica.Percentile;

/**
 * Holds information to store in the hcageinf table
 * 
 * @author Tammy Dugan
 * @version 1.0
 */
public class Hcageinf implements java.io.Serializable, Percentile {

	// Fields
	private Integer hcageinfId = null;
	private Double s = null;
	private Double m = null;
	private Double l = null;
	private Double agemos = null;
	private Integer sex = null;

	// Constructors

	/** default constructor */
	public Hcageinf() {
	}

	/**
	 * @return the hcageinfId
	 */
	public Integer getHcageinfId()
	{
		return this.hcageinfId;
	}

	/**
	 * @param hcageinfId the hcageinfId to set
	 */
	public void setHcageinfId(Integer hcageinfId)
	{
		this.hcageinfId = hcageinfId;
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
	
}