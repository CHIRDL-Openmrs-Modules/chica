package org.openmrs.module.chica.hibernateBeans;

/**
 * Holds information to store in the chica_study_attribute_value table
 * 
 * @author Tammy Dugan
 */
public class Chica1Patient implements java.io.Serializable {

	// Fields
	private Integer patientId = null;

	private String medicalRecordNumber = null;
	private String socialSecurityNumber = null;
	private String nameLast = null;
	private String nameFirst = null;
	private String nameMiddle = null;
	private String dateOfBirth = null;
	private String gender = null;
	private String race = null;
	private String motherMaidenName = null;
	private String dayPhoneNumber = null;
	private String streetAddress = null;
	private String streetAddress2 = null;
	private String city = null;
	private String state = null;
	private String zip = null;
	private String religion = null;
	private String maritalStatus = null;
	private Integer apptId = null;
	private Integer openmrsPatientId = null;
	private String skipLoadReason = null;
	
	// Constructors

	/** default constructor */
	public Chica1Patient() {
	}

	public Integer getPatientId() {
		return this.patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getMedicalRecordNumber() {
		return this.medicalRecordNumber;
	}

	public void setMedicalRecordNumber(String medicalRecordNumber) {
		this.medicalRecordNumber = medicalRecordNumber;
	}

	public String getSocialSecurityNumber() {
		return this.socialSecurityNumber;
	}

	public void setSocialSecurityNumber(String socialSecurityNumber) {
		this.socialSecurityNumber = socialSecurityNumber;
	}

	public String getNameLast() {
		return this.nameLast;
	}

	public void setNameLast(String nameLast) {
		this.nameLast = nameLast;
	}

	public String getNameFirst() {
		return this.nameFirst;
	}

	public void setNameFirst(String nameFirst) {
		this.nameFirst = nameFirst;
	}

	public String getNameMiddle() {
		return this.nameMiddle;
	}

	public void setNameMiddle(String nameMiddle) {
		this.nameMiddle = nameMiddle;
	}

	public String getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getRace() {
		return this.race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public String getMotherMaidenName() {
		return this.motherMaidenName;
	}

	public void setMotherMaidenName(String motherMaidenName) {
		this.motherMaidenName = motherMaidenName;
	}

	public String getDayPhoneNumber() {
		return this.dayPhoneNumber;
	}

	public void setDayPhoneNumber(String dayPhoneNumber) {
		this.dayPhoneNumber = dayPhoneNumber;
	}

	public String getStreetAddress() {
		return this.streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getStreetAddress2() {
		return this.streetAddress2;
	}

	public void setStreetAddress2(String streetAddress2) {
		this.streetAddress2 = streetAddress2;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getReligion() {
		return this.religion;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}

	public String getMaritalStatus() {
		return this.maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public Integer getApptId() {
		return this.apptId;
	}

	public void setApptId(Integer apptId) {
		this.apptId = apptId;
	}

	public Integer getOpenmrsPatientId() {
		return this.openmrsPatientId;
	}

	public void setOpenmrsPatientId(Integer openmrsPatientId) {
		this.openmrsPatientId = openmrsPatientId;
	}

	public String getSkipLoadReason()
	{
		return this.skipLoadReason;
	}

	public void setSkipLoadReason(String skipLoadReason)
	{
		this.skipLoadReason = skipLoadReason;
	}
}