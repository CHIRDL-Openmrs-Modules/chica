package org.openmrs.module.chica.hl7.immunization;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.EncounterService;

public class Vaccine {

	private Integer locationId;
	private Integer providerId;
	private String lotNumber;
	private String RL;
	private String AT;
	private String vaccineCode;
	private String vaccineName;
	private Date order;
	private String providerFN;
	private String providerLN;
	private Integer encounterId;
	private Date dateGiven;
	private String siteCombined;
	private String siteCode;
	private String facility;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zipcode;
	private String providerMN;
	private String route;
	private String routeCode;
	private String vaccineDose;
	
	
	
	//what are the codes for CHIRP for rl at, because they are combined
	private String source;
	public Integer getLocationId() {
		return locationId;
	}
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	public Integer getProviderId() {
		return providerId;
	}
	/**
	 * @param vaccineCode
	 * @param vaccineName
	 */
	public Vaccine(String vaccineCode, String vaccineName) {
		this.vaccineCode = vaccineCode;
		this.vaccineName = vaccineName;
	}
	
	public Vaccine() {
	}
	
	public void setProviderId(Integer providerId) {
		this.providerId = providerId;
	}
	public String getLotNumber() {
		return lotNumber;
	}
	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}
	public String getLR() {
		return RL;
	}
	public void setLR(String rL) {
		RL = rL;
	}
	public String getAT() {
		return AT;
	}
	public void setAT(String aT) {
		AT = aT;
	}
	public String getVaccineCode() {
		return vaccineCode;
	}
	public void setVaccineCode(String vaccineCode) {
		this.vaccineCode = vaccineCode;
	}
	public String getVaccineName() {
		return vaccineName;
	}
	public void setVaccineName(String vaccineName) {
		this.vaccineName = vaccineName;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Date getOrder() {
		return order;
	}
	public void setOrder(Date order) {
		this.order = order;
	}
	public String getProviderFN() {
		return providerFN;
	}
	public void setProviderFN(String providerFN) {
		this.providerFN = providerFN;
	}
	public void setProviderMN(String middleName) {
		this.providerMN = middleName;
	}
	public String getProviderLN() {
		return providerLN;
	}
	public void setProviderLN(String providerLN) {
		this.providerLN = providerLN;
	}
	public Integer getEncounterId() {
		return encounterId;
	}
	public void setEncounterId(Integer encounterId) {
		this.encounterId = encounterId;
	}
	public Date getDateGiven() {
		return dateGiven;
	}
	public void setDateGiven(Date dateGiven) {
		this.dateGiven = dateGiven;
	}
	public String getSiteCombined() {
		return siteCombined;
	}
	public void setSiteCombined(String siteCombined) {
		this.siteCombined = siteCombined;
	}
	public String getSiteCode() {
		return siteCode;
	}
	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}
	public String getFacility() {
		return facility;
	}
	public void setFacility(String facility) {
		this.facility = facility;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getRL() {
		return RL;
	}
	public void setRL(String rL) {
		RL = rL;
	}
	public String getProviderMN() {
		return providerMN;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public String getRouteCode() {
		return routeCode;
	}
	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}
	public String getVaccineDose() {
		return vaccineDose;
	}
	public void setVaccineDose(String vaccineDose) {
		this.vaccineDose = vaccineDose;
	}
	
	
	
	
}
