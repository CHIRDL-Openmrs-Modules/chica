package org.openmrs.module.chica.hibernateBeans;

/**
 * Holds information to store in the chica_old_rule table
 * 
 * @author Tammy Dugan
 */
public class OldRule implements java.io.Serializable
{

	private Integer ruleId;

	private String ruleType;
	private String ageMin;
	private String ageMax;
	private String title;
	private String version;
	private String institution;
	private String author;
	private String dateCreated;
	private String citations;
	private String minAge;
	private String minAgeUnit;
	private String maxAge;
	private String maxAgeUnit;
	private String priority;
	private String data;
	private String logic;
	private String printAction;
	private String box1Text;
	private String box2Text;
	private String box3Text;
	private String box4Text;
	private String box5Text;
	private String box6Text;
	private String scanAction;
	private String keywords;
	private String purpose;
	private String explanation;
	private String links;
	private String filename;
	private String jitFilename;
	private String dateModified;

	// Constructors

	/** default constructor */
	public OldRule()
	{
	}

	public Integer getRuleId()
	{
		return this.ruleId;
	}

	public void setRuleId(Integer ruleId)
	{
		this.ruleId = ruleId;
	}

	public String getRuleType()
	{
		return this.ruleType;
	}

	public void setRuleType(String ruleType)
	{
		this.ruleType = ruleType;
	}

	public String getAgeMin()
	{
		return this.ageMin;
	}

	public void setAgeMin(String ageMin)
	{
		this.ageMin = ageMin;
	}

	public String getAgeMax()
	{
		return this.ageMax;
	}

	public void setAgeMax(String ageMax)
	{
		this.ageMax = ageMax;
	}

	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getVersion()
	{
		return this.version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getInstitution()
	{
		return this.institution;
	}

	public void setInstitution(String institution)
	{
		this.institution = institution;
	}

	public String getAuthor()
	{
		return this.author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public String getDateCreated()
	{
		return this.dateCreated;
	}

	public void setDateCreated(String dateCreated)
	{
		this.dateCreated = dateCreated;
	}

	public String getCitations()
	{
		return this.citations;
	}

	public void setCitations(String citations)
	{
		this.citations = citations;
	}

	public String getMinAge()
	{
		return this.minAge;
	}

	public void setMinAge(String minAge)
	{
		this.minAge = minAge;
	}

	public String getMinAgeUnit()
	{
		return this.minAgeUnit;
	}

	public void setMinAgeUnit(String minAgeUnit)
	{
		this.minAgeUnit = minAgeUnit;
	}

	public String getMaxAge()
	{
		return this.maxAge;
	}

	public void setMaxAge(String maxAge)
	{
		this.maxAge = maxAge;
	}

	public String getMaxAgeUnit()
	{
		return this.maxAgeUnit;
	}

	public void setMaxAgeUnit(String maxAgeUnit)
	{
		this.maxAgeUnit = maxAgeUnit;
	}

	public String getPriority()
	{
		return this.priority;
	}

	public void setPriority(String priority)
	{
		this.priority = priority;
	}

	public String getData()
	{
		return this.data;
	}

	public void setData(String data)
	{
		this.data = data;
	}

	public String getLogic()
	{
		return this.logic;
	}

	public void setLogic(String logic)
	{
		this.logic = logic;
	}

	public String getPrintAction()
	{
		return this.printAction;
	}

	public void setPrintAction(String printAction)
	{
		this.printAction = printAction;
	}

	public String getBox1Text()
	{
		return this.box1Text;
	}

	public void setBox1Text(String box1Text)
	{
		this.box1Text = box1Text;
	}

	public String getBox2Text()
	{
		return this.box2Text;
	}

	public void setBox2Text(String box2Text)
	{
		this.box2Text = box2Text;
	}

	public String getBox3Text()
	{
		return this.box3Text;
	}

	public void setBox3Text(String box3Text)
	{
		this.box3Text = box3Text;
	}

	public String getBox4Text()
	{
		return this.box4Text;
	}

	public void setBox4Text(String box4Text)
	{
		this.box4Text = box4Text;
	}

	public String getBox5Text()
	{
		return this.box5Text;
	}

	public void setBox5Text(String box5Text)
	{
		this.box5Text = box5Text;
	}

	public String getBox6Text()
	{
		return this.box6Text;
	}

	public void setBox6Text(String box6Text)
	{
		this.box6Text = box6Text;
	}

	public String getScanAction()
	{
		return this.scanAction;
	}

	public void setScanAction(String scanAction)
	{
		this.scanAction = scanAction;
	}

	public String getKeywords()
	{
		return this.keywords;
	}

	public void setKeywords(String keywords)
	{
		this.keywords = keywords;
	}

	public String getPurpose()
	{
		return this.purpose;
	}

	public void setPurpose(String purpose)
	{
		this.purpose = purpose;
	}

	public String getExplanation()
	{
		return this.explanation;
	}

	public void setExplanation(String explanation)
	{
		this.explanation = explanation;
	}

	public String getLinks()
	{
		return this.links;
	}

	public void setLinks(String links)
	{
		this.links = links;
	}

	public String getFilename()
	{
		return this.filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public String getJitFilename()
	{
		return this.jitFilename;
	}

	public void setJitFilename(String jitFilename)
	{
		this.jitFilename = jitFilename;
	}

	public String getDateModified()
	{
		return this.dateModified;
	}

	public void setDateModified(String dateModified)
	{
		this.dateModified = dateModified;
	}

}