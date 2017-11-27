package org.openmrs.module.chica.rule;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.xmlBeans.growthcharts.ChartConcept;
import org.openmrs.module.chica.xmlBeans.growthcharts.ChartConcepts;
import org.openmrs.module.chica.xmlBeans.growthcharts.ConceptXAxis;
import org.openmrs.module.chica.xmlBeans.growthcharts.ConceptYAxis;
import org.openmrs.module.chica.xmlBeans.growthcharts.GrowthChart;
import org.openmrs.module.chica.xmlBeans.growthcharts.GrowthChartConfig;
import org.openmrs.module.chica.xmlBeans.growthcharts.GrowthCharts;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ChirdlLocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

public class getGrowthChartFilename implements Rule {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private static final String HEAD_CIRCUMFERENCE = "HC";
	
	private static final String WEIGHT = "WEIGHT";
	
	private static final String HEIGHT = "HEIGHT";
	
	private static final String BMI = "BMI CHICA";
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[] {};
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		if (parameters != null) {
			PatientService patientService = Context.getPatientService();
			Patient patient = patientService.getPatient(patientId);
			String typeOfChart = (String) parameters.get("param1");
			
			String rotationStr = (String) parameters.get("param2");
			float rotation = 0f;
			if (rotationStr != null && rotationStr.trim().length() != 0) {
				try {
					rotation = Float.parseFloat(rotationStr);
				} catch (NumberFormatException e){}
			}
			
			FormInstance formInstance = (FormInstance) parameters.get("formInstance");
			if (formInstance == null) {
				log.error("Error retrieving form instance from parameters.");
				return Result.emptyResult();
			}
			
			ChirdlUtilBackportsService chirdlUtilBackportsService = Context.getService(ChirdlUtilBackportsService.class);
			Integer locationId = formInstance.getLocationId();
			ChirdlLocationAttributeValue locationAttributeValue = 
				chirdlUtilBackportsService.getLocationAttributeValue(locationId, "growthChartDirectory");
		
			if(locationAttributeValue == null){
				log.error("No growthChartDirectory location attribute value set for location " + locationId);
				return Result.emptyResult();
			}
			
			String growthChartDirectory = locationAttributeValue.getValue();
			if(growthChartDirectory == null){
				log.error("No growthChartDirectory location attribute value set for location " + locationId);
				return Result.emptyResult();
			}
			
			String filename = printGrowthChart(patient, typeOfChart, growthChartDirectory + File.separator, rotation);
			if (filename != null) {
				return new Result(filename);
			}
		}
		
		return Result.emptyResult();
	}
	
	private String printGrowthChart(Patient patient, String typeOfChart, String growthChartDirectory, float rotation) {	
		AdministrationService adminService = Context.getAdministrationService();
		Date birthdate = patient.getBirthdate();
		String gender = patient.getGender();
		Float ageInMonths = Float.parseFloat(Integer.toString(org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(
		    birthdate, new java.util.Date(), Util.MONTH_ABBR)));
		
		String plotImageLocation = adminService.getGlobalProperty("chica.growthChartPlotImage");
		if (plotImageLocation == null) {
			log.error("You must set a value for global property: chica.growthChartPlotImage");
			return null;
		}
		
		String mrn = patient.getPatientIdentifier().getIdentifier();
		String suffix = Util.archiveStamp() + "_" + mrn;
		String pdfFilename = null;
		
		GrowthChartConfig growthChartConfig = null;
		try {
	        growthChartConfig = getGrowthChartConfig();
        }
        catch (FileNotFoundException e) {
	        log.error("Error parsing growth chart configuration file.", e);
	        return null;
        }
        catch (JiBXException e) {
	        log.error("Error parsing growth chart configuration file.", e);
	        return null;
        }
		
		try {
			
			GrowthCharts growthCharts = growthChartConfig.getGrowthCharts();
			if (growthCharts == null) {
				return null;
			}
			
			ArrayList<GrowthChart> growthChartList = growthCharts.getGrowthCharts();
			if (growthChartList == null) {
				return null;
			}
			
			for (GrowthChart growthChart : growthChartList) {
				PDDocument document = PDDocument.load(new File(growthChart.getFileLocation()));
				PDPage page1 = document.getPage(0);
				PDImageXObject image = PDImageXObject.createFromFile(plotImageLocation,document);	
				PDPageContentStream contentStream = new PDPageContentStream(document, page1, true, true, true);
				if (typeOfChart.equals(growthChart.getChartType())) {
					if (gender.equals(growthChart.getGender())) {
						if (ageInMonths >= growthChart.getAgeInMonthsMin() && ageInMonths < growthChart.getAgeInMonthsMax()) {
							try {
								pdfFilename = IOUtil.getFilenameWithoutExtension(growthChart.getFileLocation()) + "_" + suffix + 
									".pdf";
							
								ChartConcepts concepts = growthChart.getChartConcepts();
								if (concepts == null) {
									continue;
								}
								
								ArrayList<ChartConcept> conceptList = 
									concepts.getChartConcepts();
								if (conceptList == null) {
									continue;
								}
								
								for (ChartConcept chartConcept : conceptList) {
									ConceptXAxis conceptXAxis = chartConcept.getConceptXAxis();
									ConceptYAxis conceptYAxis = chartConcept.getConceptYAxis();
									if (conceptXAxis == null || conceptYAxis == null) {
										log.error("X axis and/or y axis concept are not specified in the growth chart " +
												"configuration file: " + growthChart.getFileLocation());
										continue;
									}
									
									addPlots(growthChart, patient, conceptXAxis, conceptYAxis, image, birthdate, contentStream, document);
								}
								contentStream.close();
								document.save(new FileOutputStream(growthChartDirectory+ pdfFilename));
								document.close();
								break;
							}
							catch (Exception e) {
								log.error("Error creating growth chart", e);
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			log.error("Error creating growth chart", e);
			return null;
		}
		
		String imageFilename = null;
		if (pdfFilename != null) {
			imageFilename = convertPdfToImage(growthChartDirectory, pdfFilename, rotation);
			
			// Delete the PDF file.  It is no longer needed since we now have the image.
			File pdfFile = new File(growthChartDirectory, pdfFilename);
			if (pdfFile.exists()) {
				pdfFile.delete();
			}
		} else {
			log.error("No growth chart created for patient: " + patient.getPatientId() + ", age: " + ageInMonths + 
				" months, gender: " + gender + ", type of chart: " + typeOfChart + ".");
		}
		
		return imageFilename;
	}
	
	/**
	 * This method fills in the biometric grid in the growth charts with the most recent height, weight, BMI, HC
	 * 
	 * @param pdfStamper
	 * @param encounter
	 * @param birthdate
	 * @param count
	 * @throws Exception
	 */
	private void writeBiometrics(PDDocument document, Encounter encounter, Date birthdate, Integer count, List<Obs> obs) throws Exception{
		//fill in most recent biometrics
		PDAcroForm form = document.getDocumentCatalog().getAcroForm();
		
		if (encounter != null) {
			for (Obs currObs : obs) {
				String name = currObs.getConcept().getName().getName();
				Double value = currObs.getValueNumeric();
				if (value != null) {
					value = Math.round(value * 100) / 100D;
					if (name.equalsIgnoreCase(WEIGHT)) {
						form.getField("Weight"+count).setValue(value.toString()+" lbs");
					}
					if (name.equalsIgnoreCase(HEIGHT)) {
						form.getField("Height"+count).setValue(value.toString()+" in");
					}
					if (name.equalsIgnoreCase(HEAD_CIRCUMFERENCE)) {
						form.getField("HC"+count).setValue(value.toString()+" cm");
					}
					if (name.equalsIgnoreCase(BMI)) {
						form.getField("BMI"+count).setValue(value.toString());
					}
				}	
			}
			String value = Util.adjustAgeUnits(birthdate, encounter.getEncounterDatetime());
			form.getField("Age"+count).setValue(value);
			String pattern = "M/d/yyyy";
			SimpleDateFormat dateForm = new SimpleDateFormat(pattern);
			String encounterDate = dateForm.format(encounter.getEncounterDatetime());
			form.getField("Date"+count).setValue(encounterDate);
		}
	}
	
	private void addPlots(GrowthChart growthChart, Patient patient, ConceptXAxis conceptXAxis, ConceptYAxis conceptYAxis, 
	                      PDImageXObject image, Date birthdate, PDPageContentStream contents, PDDocument document) 
	throws Exception {
		ConceptService conceptService = Context.getConceptService();
		Concept yConcept = conceptService.getConceptByName(conceptYAxis.getName());
		Concept xConcept = conceptService.getConceptByName(conceptXAxis.getName());

		if(yConcept == null || (xConcept == null&&!"AGE".equals(conceptXAxis.getName()))){
			log.error("yConcept is: "+yConcept + " xConcept is: "+xConcept+". Neither can be null in addPlots of getGrowthChartfilename");
			return;
		}
		
		ObsService obsService = Context.getObsService();
		List<Person> persons = new ArrayList<Person>();
		persons.add(patient);

		EncounterService encounterService = Context.getEncounterService();
		List<Encounter> encounters = encounterService.getEncounters(patient);
		Float ageInDays = null;
		Integer biometricsIndex = 1;
		Concept heightConcept = conceptService.getConceptByName(HEIGHT);
		Concept weightConcept = conceptService.getConceptByName(WEIGHT);
		Concept hcConcept = conceptService.getConceptByName(HEAD_CIRCUMFERENCE);
		Concept bmiConcept = conceptService.getConceptByName(BMI);

		Collections.reverse(encounters);

		for (Encounter encounter : encounters) {
			/**
			 * Edited to null check encounter date/time before calculating ageInMonths
			 */
			Date encounterDate = encounter.getEncounterDatetime();
			if (encounterDate == null) {
				continue;
			}
			
			Float ageInMonths = Float.parseFloat(Integer.toString(org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(
			    birthdate, encounterDate, Util.MONTH_ABBR)));
			
			// Need to sort out any obs that don't fall into the age range.
			if (growthChart.getAgeInMonthsMin() > ageInMonths || growthChart.getAgeInMonthsMax() <= ageInMonths) {
				continue;
			}
			Float xValue = null;
			Float yValue = null;
			
			// We have to handle AGE explicitly because it's not tied to a particular concept.
			if ("AGE".equals(conceptXAxis.getName())) {
				ageInDays = Float.parseFloat(Integer.toString(org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(
				    birthdate, encounterDate, Util.DAY_ABBR)));
				xValue = ageInDays;
			}
			
			List<Concept> questions = new ArrayList<Concept>();
			questions.add(yConcept);
			questions.add(xConcept);
			questions.add(heightConcept);
			questions.add(weightConcept);
			questions.add(hcConcept);
			questions.add(bmiConcept);
			
			List<Encounter> currEncounter = new ArrayList<Encounter>();
			currEncounter.add(encounter);
			List<Obs> obs = obsService.getObservations(persons, currEncounter, questions, null, null, null, null, null, null, null, null,
			    false);
			
			for (Obs currObs : obs) {
				
				if (currObs.getConcept().getName().getName().equals(conceptXAxis.getName())) {
					xValue = Float.parseFloat(currObs.getValueNumeric().toString());
				}else if (currObs.getConcept().getName().getName().equals(conceptYAxis.getName())) {
					yValue = Float.parseFloat(currObs.getValueNumeric().toString());
				}
			}
						
			if (xValue != null && yValue != null) {
				Float xPosition = computeAbsolutePosition(conceptXAxis.getMinPosition(), conceptXAxis.getMaxPosition(),
				    conceptXAxis.getMinVal(), conceptXAxis.getMaxVal(), xValue);
				Float yPosition = computeAbsolutePosition(conceptYAxis.getMinPosition(), conceptYAxis.getMaxPosition(),
				    conceptYAxis.getMinVal(), conceptYAxis.getMaxVal(), yValue);
				contents.drawImage(image, xPosition, yPosition);
				writeBiometrics(document, encounter, birthdate,biometricsIndex,obs);

				biometricsIndex++;
			}
		}
	}
	
	private String convertPdfToImage(String growthChartDirectory, String pdfFilename, float rotation) {
		String newFilename = null;
		File file = null;
		try {
//			// load PDF document
//			PDDocument document = PDDocument.load(new File(growthChartDirectory, pdfFilename));
//			
//			// get all pages
//			List<PDPage> pages = document.getDocumentCatalog().getAllPages();
//			
//			if (pages.size() > 0) {
//				// single page
//				PDPage singlePage = pages.get(0);
//				
//				// to BufferedImage
//				BufferedImage buffImage = singlePage.convertToImage();
//				
//				// This section is for performance reasons.  If we just try to write the buffered image 
//				// directly to disk, it is extremely slow.
//				BufferedImage indexedImage = new BufferedImage(buffImage.getWidth(),
//				buffImage.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
//				Graphics2D g = indexedImage.createGraphics();
//				g.drawImage(buffImage, 0,0,null);
//				buffImage = indexedImage;
//				
//				// write image to disk
//				newFilename = pdfFilename.replace(".pdf", ".png");
//				file = new File(growthChartDirectory, newFilename);
//				ImageIO.write(buffImage, "png", new BufferedOutputStream(new FileOutputStream(file)));
//			}
			
		      // open the file
		      Document document = new Document();
		      try {
		         document.setFile(growthChartDirectory + pdfFilename);
		      } catch (PDFException ex) {
		    	  log.error("Error parsing PDF document", ex);
		    	  return null;
		      } catch (PDFSecurityException ex) {
		    	  log.error("Error encryption not supported", ex);
		    	  return null;
		      } catch (FileNotFoundException ex) {
		    	  log.error("Error file not found", ex);
		    	  return null;
		      } catch (IOException ex) {
		    	  log.error("Error IOException", ex);
		    	  return null;
		      }

		      // save page capture to file.
		      float scale = 1.0f;

		      // Paint the page content to an image and
		      // write the image to file
		      if (document.getNumberOfPages() > 0) {
		         BufferedImage image = (BufferedImage) document.getPageImage(
		             0, GraphicsRenderingHints.PRINT, Page.BOUNDARY_CROPBOX, rotation, scale);
		         // This section is for performance reasons.  If we just try to write the buffered image 
		         // directly to disk, it is extremely slow.
		         BufferedImage indexedImage = new BufferedImage(image.getWidth(),
		        	 image.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
		         Graphics2D g = indexedImage.createGraphics();
		         g.drawImage(image, 0, 0, null);
		         image = indexedImage;
		         try {
		        	newFilename = pdfFilename.replace(".pdf", ".png");
		        	file = new File(growthChartDirectory, newFilename);
		            ImageIO.write(image, "png", file);
		         } catch (IOException e) {
		        	 log.error("Error saving image to file system", e);
		 			 file = null;
		         } finally {
		        	 image.flush();
		         }
		      }

		      // clean up resources
		      document.dispose();
		}
		catch (Exception ex) {
			log.error("Error converting PDF to image", ex);
			return null;
		}
        
		if (file != null) {
			return growthChartDirectory + newFilename;
		}
		
		return null;
	}
	
	private Float computeAbsolutePosition(Float minAbsolutePosition, Float maxAbsolutePosition, Float minValue,
	                                      Float maxValue, Float value) {
		return (((maxAbsolutePosition - minAbsolutePosition) / (maxValue - minValue)) * (value - minValue))
		        + minAbsolutePosition;
		
	}
	
	private GrowthChartConfig getGrowthChartConfig() throws JiBXException, FileNotFoundException {
		String configFileStr = Context.getAdministrationService().getGlobalProperty("chica.growthChartConfigFile");
		if (configFileStr == null) {
			log.error("You must set a value for global property: chica.growthChartConfigFile");
			return null;
		}
		
		File configFile = new File(configFileStr);
		if (!configFile.exists()) {
			log.error("The file location specified for the global property "
				+ "chica.growthChartConfigFile does not exist.");
			return null;
		}
		
		IBindingFactory bfact = BindingDirectory.getFactory(GrowthChartConfig.class);
		
		IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
		return (GrowthChartConfig)uctx.unmarshalDocument(
			new FileInputStream(configFile), null);
	}
}
