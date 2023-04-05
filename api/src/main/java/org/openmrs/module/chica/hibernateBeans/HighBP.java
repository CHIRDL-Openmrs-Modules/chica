package org.openmrs.module.chica.hibernateBeans;

/**
 * Holds information to store in the chica_high_bp table
 * 
 * @author Tammy Dugan
 * @version 1.0
 */
public class HighBP implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
	private Integer hiBPId=null;
	private Integer systolicHT5=null;
	private Integer systolicHT10=null;
	private Integer systolicHT25=null;
	private Integer systolicHT50=null;
	private Integer systolicHT75=null;
	private Integer systolicHT90=null;
	private Integer systolicHT95=null;
	private Integer diastolicHT5=null;
	private Integer diastolicHT10=null;
	private Integer diastolicHT25=null;
	private Integer diastolicHT50=null;
	private Integer diastolicHT75=null;
	private Integer diastolicHT90=null;
	private Integer diastolicHT95=null;
	private Integer bpPercentile=null;
	private Integer age=null;
	private String sex=null;

	// Constructors

	/** default constructor */
	public HighBP() {
	}

	public Integer getHiBPId()
	{
		return this.hiBPId;
	}

	public void setHiBPId(Integer hiBPId)
	{
		this.hiBPId = hiBPId;
	}

	public Integer getSystolicHT5()
	{
		return this.systolicHT5;
	}

	public void setSystolicHT5(Integer systolicHT5)
	{
		this.systolicHT5 = systolicHT5;
	}

	public Integer getSystolicHT10()
	{
		return this.systolicHT10;
	}

	public void setSystolicHT10(Integer systolicHT10)
	{
		this.systolicHT10 = systolicHT10;
	}

	public Integer getSystolicHT25()
	{
		return this.systolicHT25;
	}

	public void setSystolicHT25(Integer systolicHT25)
	{
		this.systolicHT25 = systolicHT25;
	}

	public Integer getSystolicHT50()
	{
		return this.systolicHT50;
	}

	public void setSystolicHT50(Integer systolicHT50)
	{
		this.systolicHT50 = systolicHT50;
	}

	public Integer getSystolicHT75()
	{
		return this.systolicHT75;
	}

	public void setSystolicHT75(Integer systolicHT75)
	{
		this.systolicHT75 = systolicHT75;
	}

	public Integer getSystolicHT90()
	{
		return this.systolicHT90;
	}

	public void setSystolicHT90(Integer systolicHT90)
	{
		this.systolicHT90 = systolicHT90;
	}

	public Integer getSystolicHT95()
	{
		return this.systolicHT95;
	}

	public void setSystolicHT95(Integer systolicHT95)
	{
		this.systolicHT95 = systolicHT95;
	}

	public Integer getDiastolicHT5()
	{
		return this.diastolicHT5;
	}

	public void setDiastolicHT5(Integer diastolicHT5)
	{
		this.diastolicHT5 = diastolicHT5;
	}

	public Integer getDiastolicHT10()
	{
		return this.diastolicHT10;
	}

	public void setDiastolicHT10(Integer diastolicHT10)
	{
		this.diastolicHT10 = diastolicHT10;
	}

	public Integer getDiastolicHT25()
	{
		return this.diastolicHT25;
	}

	public void setDiastolicHT25(Integer diastolicHT25)
	{
		this.diastolicHT25 = diastolicHT25;
	}

	public Integer getDiastolicHT50()
	{
		return this.diastolicHT50;
	}

	public void setDiastolicHT50(Integer diastolicHT50)
	{
		this.diastolicHT50 = diastolicHT50;
	}

	public Integer getDiastolicHT75()
	{
		return this.diastolicHT75;
	}

	public void setDiastolicHT75(Integer diastolicHT75)
	{
		this.diastolicHT75 = diastolicHT75;
	}

	public Integer getDiastolicHT90()
	{
		return this.diastolicHT90;
	}

	public void setDiastolicHT90(Integer diastolicHT90)
	{
		this.diastolicHT90 = diastolicHT90;
	}

	public Integer getDiastolicHT95()
	{
		return this.diastolicHT95;
	}

	public void setDiastolicHT95(Integer diastolicHT95)
	{
		this.diastolicHT95 = diastolicHT95;
	}

	public Integer getBpPercentile()
	{
		return this.bpPercentile;
	}

	public void setBpPercentile(Integer bpPercentile)
	{
		this.bpPercentile = bpPercentile;
	}

	public Integer getAge()
	{
		return this.age;
	}

	public void setAge(Integer age)
	{
		this.age = age;
	}

	public String getSex()
	{
		return this.sex;
	}

	public void setSex(String sex)
	{
		this.sex = sex;
	}

}