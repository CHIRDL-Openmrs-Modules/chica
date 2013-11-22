package org.openmrs.module.chica.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.service.ChicaService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class WeeklyReportsController extends SimpleFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return "testing";
	}
	
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object obj,
	                                org.springframework.validation.BindException errors) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String locationName = request.getParameter("locationName");
		if (locationName != null && locationName.length() == 0) {
			locationName = null;
		}
		if (locationName != null) {
			map.put("locationName", locationName);
			
			ChicaService chicaService = Context.getService(ChicaService.class);
			
			//# PSF Printed
			List<Object[]> psfsPrintedByWeek = chicaService.getFormsPrintedByWeek("PSF", locationName);
			List<WeeklyReportRow> rows = populateRows(psfsPrintedByWeek);
			LinkedHashMap<String, WeeklyReportRow> psfsPrintedMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				psfsPrintedMap.put(row.getDateRange(), row);
			}
			
			map.put("psfsPrintedMap", psfsPrintedMap);
			
			//# PSF scanned
			List<Object[]> psfsScannedByWeek = chicaService.getFormsScannedByWeek("PSF", locationName);
			rows = populateRows(psfsScannedByWeek);
			LinkedHashMap<String, WeeklyReportRow> psfsScannedMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				psfsScannedMap.put(row.getDateRange(), row);
			}
			map.put("psfsScannedMap", psfsScannedMap);
			
			//% of PSF Scanned
			rows = new ArrayList<WeeklyReportRow>();
			for (WeeklyReportRow row : psfsPrintedMap.values()) {
				Integer psfPrintedCount = row.getData();
				Integer psfScannedCount = null;
				if (psfsScannedMap.get(row.getDateRange()) != null) {
					psfScannedCount = psfsScannedMap.get(row.getDateRange()).getData();
				}
				if (psfScannedCount != null) {
					WeeklyReportRow percentRow = new WeeklyReportRow();
					percentRow.setDateRange(row.getDateRange());
					percentRow.setData(org.openmrs.module.chirdlutil.util.Util.round(
					    ((double) psfScannedCount / psfPrintedCount) * 100, 0).intValue());
					rows.add(percentRow);
				}
			}
			LinkedHashMap<String, WeeklyReportRow> psfsPercentScannedMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				psfsPercentScannedMap.put(row.getDateRange(), row);
			}
			
			map.put("psfsPercentScannedMap", psfsPercentScannedMap);
			
			//# scanned PSFs w >=1 Box Chked
			List<Object[]> psfsScannedAnsweredByWeek = chicaService.getFormsScannedAnsweredByWeek("PSF", locationName);
			rows = populateRows(psfsScannedAnsweredByWeek);
			LinkedHashMap<String, WeeklyReportRow> psfsScannedAnsweredMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				psfsScannedAnsweredMap.put(row.getDateRange(), row);
			}
			map.put("psfsScannedAnsweredMap", psfsScannedAnsweredMap);
			
			//# scanned PSFs w anything marked
			List<Object[]> psfsScannedAnythingMarkedByWeek = chicaService.getFormsScannedAnythingMarkedByWeek("PSF",
			    locationName);
			rows = populateRows(psfsScannedAnythingMarkedByWeek);
			LinkedHashMap<String, WeeklyReportRow> psfsScannedAnythingMarkedMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				psfsScannedAnythingMarkedMap.put(row.getDateRange(), row);
			}
			map.put("psfsScannedAnythingMarkedMap", psfsScannedAnythingMarkedMap);
			
			//% of scanned PSFs w >=1 Box Chked
			rows = new ArrayList<WeeklyReportRow>();
			for (WeeklyReportRow row : psfsScannedMap.values()) {
				Integer psfScannedCount = row.getData();
				Integer psfScannedAnsweredCount = null;
				if (psfsScannedAnsweredMap.get(row.getDateRange()) != null) {
					psfScannedAnsweredCount = psfsScannedAnsweredMap.get(row.getDateRange()).getData();
				}
				if (psfScannedAnsweredCount != null) {
					WeeklyReportRow percentRow = new WeeklyReportRow();
					percentRow.setDateRange(row.getDateRange());
					percentRow.setData(org.openmrs.module.chirdlutil.util.Util.round(
					    ((double)psfScannedAnsweredCount / psfScannedCount) * 100, 0).intValue());
					rows.add(percentRow);
				}
			}
			LinkedHashMap<String, WeeklyReportRow> psfsPercentScannedAnsweredMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				psfsPercentScannedAnsweredMap.put(row.getDateRange(), row);
			}
			map.put("psfsPercentScannedAnsweredMap", psfsPercentScannedAnsweredMap);
			
			//% of scanned PSFs with anything marked
			rows = new ArrayList<WeeklyReportRow>();
			for (WeeklyReportRow row : psfsScannedMap.values()) {
				Integer psfScannedCount = row.getData();
				Integer psfScannedAnythingMarkedCount = null;
				if (psfsScannedAnythingMarkedMap.get(row.getDateRange()) != null) {
					psfScannedAnythingMarkedCount = psfsScannedAnythingMarkedMap.get(row.getDateRange()).getData();
				}
				if (psfScannedAnythingMarkedCount != null) {
					WeeklyReportRow percentRow = new WeeklyReportRow();
					percentRow.setDateRange(row.getDateRange());
					percentRow.setData(org.openmrs.module.chirdlutil.util.Util.round(
					    ((double) psfScannedAnythingMarkedCount / psfScannedCount) * 100, 0).intValue());
					rows.add(percentRow);
				}
			}
			LinkedHashMap<String, WeeklyReportRow> psfsPercentScannedAnythingMarkedMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				psfsPercentScannedAnythingMarkedMap.put(row.getDateRange(), row);
			}
			map.put("psfsPercentScannedAnythingMarkedMap", psfsPercentScannedAnythingMarkedMap);
			
			//# PWS Printed
			List<Object[]> pwssPrintedByWeek = chicaService.getFormsPrintedByWeek("PWS", locationName);
			rows = populateRows(pwssPrintedByWeek);
			LinkedHashMap<String, WeeklyReportRow> pwssPrintedMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				pwssPrintedMap.put(row.getDateRange(), row);
			}
			map.put("pwssPrintedMap", pwssPrintedMap);
			
			//# PWS scanned
			List<Object[]> pwssScannedByWeek = chicaService.getFormsScannedByWeek("PWS", locationName);
			rows = populateRows(pwssScannedByWeek);
			LinkedHashMap<String, WeeklyReportRow> pwssScannedMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				pwssScannedMap.put(row.getDateRange(), row);
			}
			map.put("pwssScannedMap", pwssScannedMap);
			
			//% of PWS Scanned
			rows = new ArrayList<WeeklyReportRow>();
			for (WeeklyReportRow row : pwssPrintedMap.values()) {
				Integer pwsPrintedCount = row.getData();
				Integer pwsScannedCount = null;
				if (pwssScannedMap.get(row.getDateRange()) != null) {
					pwsScannedCount = pwssScannedMap.get(row.getDateRange()).getData();
				}
				if (pwsScannedCount != null) {
					WeeklyReportRow percentRow = new WeeklyReportRow();
					percentRow.setDateRange(row.getDateRange());
					percentRow.setData(org.openmrs.module.chirdlutil.util.Util.round(
					    ((double) pwsScannedCount / pwsPrintedCount) * 100, 0).intValue());
					rows.add(percentRow);
				}
			}
			LinkedHashMap<String, WeeklyReportRow> pwssPercentScannedMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				pwssPercentScannedMap.put(row.getDateRange(), row);
			}
			map.put("pwssPercentScannedMap", pwssPercentScannedMap);
			
			//# scanned PWSs w >=1 Box Chked
			List<Object[]> pwssScannedAnsweredByWeek = chicaService.getFormsScannedAnsweredByWeek("PWS", locationName);
			rows = populateRows(pwssScannedAnsweredByWeek);
			LinkedHashMap<String, WeeklyReportRow> pwssScannedAnsweredMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				pwssScannedAnsweredMap.put(row.getDateRange(), row);
			}
			map.put("pwssScannedAnsweredMap", pwssScannedAnsweredMap);
			
			//# scanned PWSs w anything marked
			List<Object[]> pwssScannedAnythingMarkedByWeek = chicaService.getFormsScannedAnythingMarkedByWeek("PWS",
			    locationName);
			rows = populateRows(pwssScannedAnythingMarkedByWeek);
			LinkedHashMap<String, WeeklyReportRow> pwssScannedAnythingMarkedMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				pwssScannedAnythingMarkedMap.put(row.getDateRange(), row);
			}
			map.put("pwssScannedAnythingMarkedMap", pwssScannedAnythingMarkedMap);
			
			//% of scanned PWSs w >=1 Box Chked
			rows = new ArrayList<WeeklyReportRow>();
			for (WeeklyReportRow row : pwssScannedMap.values()) {
				Integer pwsScannedCount = row.getData();
				Integer pwsScannedAnsweredCount = null;
				if (pwssScannedAnsweredMap.get(row.getDateRange()) != null) {
					pwsScannedAnsweredCount = pwssScannedAnsweredMap.get(row.getDateRange()).getData();
				}
				if (pwsScannedAnsweredCount != null) {
					WeeklyReportRow percentRow = new WeeklyReportRow();
					percentRow.setDateRange(row.getDateRange());
					percentRow.setData(org.openmrs.module.chirdlutil.util.Util.round(
					    ((double) pwsScannedAnsweredCount / pwsScannedCount) * 100, 0).intValue());
					rows.add(percentRow);
				}
			}
			LinkedHashMap<String, WeeklyReportRow> pwssPercentScannedAnsweredMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				pwssPercentScannedAnsweredMap.put(row.getDateRange(), row);
			}
			map.put("pwssPercentScannedAnsweredMap", pwssPercentScannedAnsweredMap);
			
			//% of scanned PWSs with anything marked
			rows = new ArrayList<WeeklyReportRow>();
			for (WeeklyReportRow row : pwssScannedMap.values()) {
				Integer pwsScannedCount = row.getData();
				Integer pwsScannedAnythingMarkedCount = null;
				if (pwssScannedAnythingMarkedMap.get(row.getDateRange()) != null) {
					pwsScannedAnythingMarkedCount = pwssScannedAnythingMarkedMap.get(row.getDateRange()).getData();
				}
				if (pwsScannedAnythingMarkedCount != null) {
					WeeklyReportRow percentRow = new WeeklyReportRow();
					percentRow.setDateRange(row.getDateRange());
					percentRow.setData(org.openmrs.module.chirdlutil.util.Util.round(
					    ((double) pwsScannedAnythingMarkedCount / pwsScannedCount) * 100, 0).intValue());
					rows.add(percentRow);
				}
			}
			LinkedHashMap<String, WeeklyReportRow> pwssPercentScannedAnythingMarkedMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				pwssPercentScannedAnythingMarkedMap.put(row.getDateRange(), row);
			}
			map.put("pwssPercentScannedAnythingMarkedMap", pwssPercentScannedAnythingMarkedMap);
			
			//# PSF Questions Printed & Scanned
			List<Object[]> psfQuestionsScannedByWeek = chicaService.getQuestionsScanned("PSF", locationName);
			rows = populateRows(psfQuestionsScannedByWeek);
			LinkedHashMap<String, WeeklyReportRow> psfQuestionsScannedMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				psfQuestionsScannedMap.put(row.getDateRange(), row);
			}
			map.put("psfQuestionsScannedMap", psfQuestionsScannedMap);
			
			//# PSF Questions w >= 1 Box Chked
			List<Object[]> psfQuestionsScannedAnsweredByWeek = chicaService.getQuestionsScannedAnswered("PSF", locationName);
			rows = populateRows(psfQuestionsScannedAnsweredByWeek);
			LinkedHashMap<String, WeeklyReportRow> psfQuestionsScannedAnsweredMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				psfQuestionsScannedAnsweredMap.put(row.getDateRange(), row);
			}
			map.put("psfQuestionsScannedAnsweredMap", psfQuestionsScannedAnsweredMap);
			
			//% PSF Prompts w Response
			rows = new ArrayList<WeeklyReportRow>();
			for (WeeklyReportRow row : psfQuestionsScannedMap.values()) {
				Integer psfQuestionsScannedCount = row.getData();
				Integer psfQuestionsScannedAnsweredCount = null;
				if (psfQuestionsScannedAnsweredMap.get(row.getDateRange()) != null) {
					psfQuestionsScannedAnsweredCount = psfQuestionsScannedAnsweredMap.get(row.getDateRange()).getData();
				}
				if (psfQuestionsScannedAnsweredCount != null) {
					WeeklyReportRow percentRow = new WeeklyReportRow();
					percentRow.setDateRange(row.getDateRange());
					percentRow.setData(org.openmrs.module.chirdlutil.util.Util.round(
					    ((double) psfQuestionsScannedAnsweredCount / psfQuestionsScannedCount) * 100, 0).intValue());
					rows.add(percentRow);
				}
			}
			LinkedHashMap<String, WeeklyReportRow> psfPercentQuestionsScannedAnsweredMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				psfPercentQuestionsScannedAnsweredMap.put(row.getDateRange(), row);
			}
			map.put("psfPercentQuestionsScannedAnsweredMap", psfPercentQuestionsScannedAnsweredMap);
			
			//% PSF Prompts w Response - adjusted for blanks
			rows = new ArrayList<WeeklyReportRow>();
			for (WeeklyReportRow row : psfQuestionsScannedMap.values()) {
				Integer psfQuestionsScannedCount = row.getData();
				Integer psfQuestionsScannedAnsweredCount = null;
				Integer psfsScannedCount = null;
				Integer psfsScannedAnsweredCount = null;
				
				if (psfQuestionsScannedAnsweredMap.get(row.getDateRange()) != null) {
					psfQuestionsScannedAnsweredCount = psfQuestionsScannedAnsweredMap.get(row.getDateRange()).getData();
				}
				if (psfsScannedMap.get(row.getDateRange()) != null) {
					psfsScannedCount = psfsScannedMap.get(row.getDateRange()).getData();
				}
				if (psfsScannedAnsweredMap.get(row.getDateRange()) != null) {
					psfsScannedAnsweredCount = psfsScannedAnsweredMap.get(row.getDateRange()).getData();
				}
				if (psfQuestionsScannedAnsweredCount != null && psfsScannedCount != null && psfsScannedAnsweredCount != null) {
					WeeklyReportRow percentRow = new WeeklyReportRow();
					percentRow.setDateRange(row.getDateRange());
					percentRow
					        .setData(org.openmrs.module.chirdlutil.util.Util
					                .round(
					                    ((double) psfQuestionsScannedAnsweredCount / (psfQuestionsScannedCount - ((psfsScannedCount - psfsScannedAnsweredCount) * 20))) * 100,
					                    0).intValue());
					rows.add(percentRow);
				}
			}
			LinkedHashMap<String, WeeklyReportRow> psfPercentQuestionsScannedAnsweredAdjustedMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				psfPercentQuestionsScannedAnsweredAdjustedMap.put(row.getDateRange(), row);
			}
			map.put("psfPercentQuestionsScannedAnsweredAdjustedMap", psfPercentQuestionsScannedAnsweredAdjustedMap);
			
			//# PWS Questions Printed & Scanned
			List<Object[]> pwsQuestionsScannedByWeek = chicaService.getQuestionsScanned("PWS", locationName);
			rows = populateRows(pwsQuestionsScannedByWeek);
			LinkedHashMap<String, WeeklyReportRow> pwsQuestionsScannedMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				pwsQuestionsScannedMap.put(row.getDateRange(), row);
			}
			map.put("pwsQuestionsScannedMap", pwsQuestionsScannedMap);
			
			//# PWS Questions w >= 1 Box Chked
			List<Object[]> pwsQuestionsScannedAnsweredByWeek = chicaService.getQuestionsScannedAnswered("PWS", locationName);
			rows = populateRows(pwsQuestionsScannedAnsweredByWeek);
			LinkedHashMap<String, WeeklyReportRow> pwsQuestionsScannedAnsweredMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				pwsQuestionsScannedAnsweredMap.put(row.getDateRange(), row);
			}
			map.put("pwsQuestionsScannedAnsweredMap", pwsQuestionsScannedAnsweredMap);
			
			//% PWS Prompts w Response
			rows = new ArrayList<WeeklyReportRow>();
			for (WeeklyReportRow row : pwsQuestionsScannedMap.values()) {
				Integer pwsQuestionsScannedCount = row.getData();
				Integer pwsQuestionsScannedAnsweredCount = null;
				if (pwsQuestionsScannedAnsweredMap.get(row.getDateRange()) != null) {
					pwsQuestionsScannedAnsweredCount = pwsQuestionsScannedAnsweredMap.get(row.getDateRange()).getData();
				}
				if (pwsQuestionsScannedAnsweredCount != null) {
					WeeklyReportRow percentRow = new WeeklyReportRow();
					percentRow.setDateRange(row.getDateRange());
					percentRow.setData(org.openmrs.module.chirdlutil.util.Util.round(
					    ((double) pwsQuestionsScannedAnsweredCount / pwsQuestionsScannedCount) * 100, 0).intValue());
					rows.add(percentRow);
				}
			}
			LinkedHashMap<String, WeeklyReportRow> pwsPercentQuestionsScannedAnsweredMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				pwsPercentQuestionsScannedAnsweredMap.put(row.getDateRange(), row);
			}
			
			map.put("pwsPercentQuestionsScannedAnsweredMap", pwsPercentQuestionsScannedAnsweredMap);
			
			//% PWS Prompts w Response - adjusted for blanks
			rows = new ArrayList<WeeklyReportRow>();
			for (WeeklyReportRow row : pwsQuestionsScannedMap.values()) {
				Integer pwsQuestionsScannedCount = row.getData();
				Integer pwsQuestionsScannedAnsweredCount = null;
				Integer pwssScannedCount = null;
				Integer pwssScannedAnsweredCount = null;
				
				if (pwsQuestionsScannedAnsweredMap.get(row.getDateRange()) != null) {
					pwsQuestionsScannedAnsweredCount = pwsQuestionsScannedAnsweredMap.get(row.getDateRange()).getData();
				}
				if (pwssScannedMap.get(row.getDateRange()) != null) {
					pwssScannedCount = pwssScannedMap.get(row.getDateRange()).getData();
				}
				if (pwssScannedAnsweredMap.get(row.getDateRange()) != null) {
					pwssScannedAnsweredCount = pwssScannedAnsweredMap.get(row.getDateRange()).getData();
				}
				if (pwsQuestionsScannedAnsweredCount != null && pwssScannedCount != null && pwssScannedAnsweredCount != null) {
					WeeklyReportRow percentRow = new WeeklyReportRow();
					percentRow.setDateRange(row.getDateRange());
					percentRow
					        .setData(org.openmrs.module.chirdlutil.util.Util
					                .round(
					                    ((double) pwsQuestionsScannedAnsweredCount / (pwsQuestionsScannedCount - ((pwssScannedCount - pwssScannedAnsweredCount) * 6))) * 100,
					                    0).intValue());
					rows.add(percentRow);
				}
			}
			LinkedHashMap<String, WeeklyReportRow> pwsPercentQuestionsScannedAnsweredAdjustedMap = new LinkedHashMap<String, WeeklyReportRow>();
			for (WeeklyReportRow row : rows) {
				pwsPercentQuestionsScannedAnsweredAdjustedMap.put(row.getDateRange(), row);
			}
			map.put("pwsPercentQuestionsScannedAnsweredAdjustedMap", pwsPercentQuestionsScannedAnsweredAdjustedMap);
			
		}
		
		
		return showForm(request, response, errors, map);
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#showForm(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, org.springframework.validation.BindException,
	 *      java.util.Map)
	 */
	@Override
	protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors,
	                                Map controlModel) throws Exception {
		LocationService locationService = Context.getLocationService();
		List<Location> locations = locationService.getAllLocations();
		
		if (controlModel == null) {
			controlModel = new HashMap<String, Object>();
		}
		
		controlModel.put("locations", locations);
		
		return super.showForm(request, response, errors, controlModel);
	}
	
	private List<WeeklyReportRow> populateRows(List<Object[]> databaseRows) throws ParseException {
		List<WeeklyReportRow> rows = new ArrayList<WeeklyReportRow>();
		
		if (databaseRows == null) {
			return rows;
		}
		
		for (Object[] databaseRow : databaseRows) {
			WeeklyReportRow row = new WeeklyReportRow();
			String startDateString = (String) databaseRow[0];
			String endDateString = (String) databaseRow[1];
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = formatter.parse(startDateString);
			Date endDate = formatter.parse(endDateString);
			SimpleDateFormat displayFormat = new SimpleDateFormat("MM/dd");
			
			row.setDateRange(displayFormat.format(startDate) + " - " + displayFormat.format(endDate));
			row.setData((java.math.BigInteger) databaseRow[2]);
			rows.add(row);
		}
		return rows;
	}
	
}
