package org.openmrs.module.chica.rule;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

/**
 * Places an order to the orders table.
 * 
 * @author Steve McKee
 */
public class PlaceOrder implements Rule {
	
	private static final Logger log = LoggerFactory.getLogger(PlaceOrder.class);
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) {
		if (patientId == null) {
			log.error("Cannot place order.  Patient ID is null.");
			return Result.emptyResult();
		}
		
		if (parameters == null) {
			log.error("Cannot place order.  Parameters are null.");
			return Result.emptyResult();
		}
		
		String conceptName = (String)parameters.get("param1");
		if (StringUtils.isBlank(conceptName)) {
			log.error("Cannot place order.  No order concept name provided.");
			return Result.emptyResult();
		}
		
		Integer encounterId = (Integer)parameters.get("encounterId");
		if (encounterId == null) {
			log.error("Cannot place order.  No encounter ID available.");
			return Result.emptyResult();
		}
		
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null) {
			log.error("Cannot place order.  No patient found for ID " + patientId);
			return Result.emptyResult();
		}
		
		Encounter encounter = Context.getEncounterService().getEncounter(encounterId);
		if (encounter == null) {
			log.error("Cannot place order.  No encounter found for ID " + encounterId);
			return Result.emptyResult();
		}
		
		if (encounter.getActiveEncounterProviders().isEmpty()) {
			log.error("Cannot place order.  No encounter provider found for encounter " + encounterId);
			return Result.emptyResult();
		}
		
		String orderTypeName = (String)parameters.get("param2");
		if (StringUtils.isBlank(orderTypeName)) {
			log.error("Cannot place order.  Order type not provided.");
			return Result.emptyResult();
		}
		
		OrderType orderType = Context.getOrderService().getOrderTypeByName(orderTypeName);
		if (orderType == null) {
			log.error("Cannot place order.  Order type with the following name cannot be found: " + orderTypeName);
			return Result.emptyResult();
		}
		
		String careSettingName = (String)parameters.get("param3");
		if (StringUtils.isBlank(careSettingName)) {
			log.error("Cannot place order.  Care setting not provided.");
			return Result.emptyResult();
		}
		
		CareSetting careSetting = Context.getOrderService().getCareSettingByName(careSettingName);
		if (careSetting == null) {
			log.error("Cannot place order.  Care setting with the following name cannot be found: " 
				+ careSettingName);
			return Result.emptyResult();
		}
		
		return placeOrder(patient, encounter, careSetting, orderType, conceptName);
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	@Override
	public Set<RuleParameterInfo> getParameterList() {
		return new HashSet<>();
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies() {
		return new String[0];
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	@Override
	public int getTTL() {
		return 0;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	@Override
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	/**
	 * Places the order into the OpenMrs orders table.
	 * 
	 * @param patient The patient the order is for
	 * @param encounter The encounter for the order
	 * @param careSetting The care setting
	 * @param orderType The order type
	 * @param orderConceptName The concept name for the order
	 * @return Result object if the order is successfully placed
	 */
	private Result placeOrder(Patient patient, Encounter encounter, CareSetting careSetting, OrderType orderType, 
			String orderConceptName) {
		Concept orderConcept = Context.getConceptService().getConceptByName(orderConceptName);
		if (orderConcept == null) {
			log.error("Cannot place order.  Order concept with the following name cannot be found: "
				+ orderConceptName);
			return Result.emptyResult();
		}
		
		Order order = null;
		try {
			order = (Order)orderType.getJavaClass().newInstance();
		}
		catch (InstantiationException e) {
			log.error("Error instantiating order for order type " + orderType.getOrderTypeId(), e);
			return Result.emptyResult();
		}
		catch (IllegalAccessException e) {
			log.error("Illegal access error instantiating order for order type " + orderType.getOrderTypeId(), e);
			return Result.emptyResult();
		}
		catch (APIException e) {
			log.error("API exception instantiating order for order type " + orderType.getOrderTypeId(), e);
			return Result.emptyResult();
		}
		
		order.setPatient(patient);
		order.setEncounter(encounter);
		order.setConcept(orderConcept);
		order.setOrderer(encounter.getActiveEncounterProviders().iterator().next().getProvider());
		order.setAction(Order.Action.NEW);
		order.setCareSetting(careSetting);
		order.setOrderType(orderType);
		
		try {
			Context.getOrderService().saveOrder(order, null);
		}
		catch (APIException e) {
			log.error("Error saving order for patient ID " + patient.getPatientId() + ", encounter ID " 
				+ encounter.getEncounterId() + ", and order concpet ID " + orderConcept.getConceptId());
			return Result.emptyResult();
		}
		
		return new Result(orderConcept);
	}
}
