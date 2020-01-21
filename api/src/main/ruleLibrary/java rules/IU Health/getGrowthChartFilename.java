package org.openmrs.module.chica.rule;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.debugger.ui.ImageUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
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
import org.openmrs.module.chirdlutil.util.DateUtil;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ChirdlLocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class getGrowthChartFilename implements Rule {
    
    private Log log = LogFactory.getLog(this.getClass());
    
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
            
            Image image = Image.getInstance(plotImageLocation);         
            GrowthCharts growthCharts = growthChartConfig.getGrowthCharts();
            if (growthCharts == null) {
                return null;
            }
            
            ArrayList<GrowthChart> growthChartList = growthCharts.getGrowthCharts();
            if (growthChartList == null) {
                return null;
            }
            
            for (GrowthChart growthChart : growthChartList) {
                if (typeOfChart.equals(growthChart.getChartType())) {
                    if (gender.equals(growthChart.getGender())) {
                        if (ageInMonths >= growthChart.getAgeInMonthsMin() && ageInMonths < growthChart.getAgeInMonthsMax()) {
                            try {
                                PdfReader pdfReader = new PdfReader(growthChart.getFileLocation());
                                pdfFilename = IOUtil.getFilenameWithoutExtension(growthChart.getFileLocation()) + "_" + suffix + 
                                    ".pdf";
                                File pdfFile = new File(growthChartDirectory+ pdfFilename);
                                PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(pdfFile));
                                
                                ChartConcepts concepts = growthChart.getChartConcepts();
                                if (concepts == null) {
                                    continue;
                                }
                                
                                ArrayList<ChartConcept> conceptList = 
                                    concepts.getChartConcepts();
                                if (conceptList == null) {
                                    continue;
                                }
                                
                                PdfContentByte content = pdfStamper.getOverContent(1);
                                for (ChartConcept chartConcept : conceptList) {
                                    ConceptXAxis conceptXAxis = chartConcept.getConceptXAxis();
                                    ConceptYAxis conceptYAxis = chartConcept.getConceptYAxis();
                                    if (conceptXAxis == null || conceptYAxis == null) {
                                        log.error("X axis and/or y axis concept are not specified in the growth chart " +
                                                "configuration file: " + growthChart.getFileLocation());
                                        continue;
                                    }
                                    
                                    addPlots(growthChart, patient, conceptXAxis, conceptYAxis, image, content, birthdate);
                                }
                                
                                pdfStamper.close();
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
    
    private void addPlots(GrowthChart growthChart, Patient patient, ConceptXAxis conceptXAxis, ConceptYAxis conceptYAxis, 
                          Image image, PdfContentByte content, Date birthdate) 
    throws Exception {
        ConceptService conceptService = Context.getConceptService();
        Concept yConcept = conceptService.getConceptByName(conceptYAxis.getName());
        ObsService obsService = Context.getObsService();
        List<Person> persons = new ArrayList<Person>();
        persons.add(patient);
        List<Concept> questions = new ArrayList<Concept>();
        questions.add(yConcept);
        List<Obs> obs = obsService.getObservations(persons, null, questions, null, null, null, null, null, null, null, null,
            false);
        Float ageInDays = null;
        Concept xAxisConcept = null;
        for (Obs currObs : obs) {
            Float xValue = null;
            Date yAxisDate = currObs.getObsDatetime();
            if (yAxisDate == null) {
                continue;
            }
            
            Float ageInMonths = Float.parseFloat(Integer.toString(org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate,
                yAxisDate, Util.MONTH_ABBR)));
            // Need to sort out any obs that don't fall into the age range.
            if (growthChart.getAgeInMonthsMin() > ageInMonths || growthChart.getAgeInMonthsMax() <= ageInMonths) {
                continue;
            }
            
            // We have to handle AGE explicitly because it's not tied to a particular concept.
            if ("AGE".equals(conceptXAxis.getName())) {
                ageInDays = Float.parseFloat(Integer.toString(org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate,
                    yAxisDate, Util.DAY_ABBR)));
                xValue = ageInDays;
            } else {
                // Get the obs for the x axis for the same encounter
                if (xAxisConcept == null) {
                    xAxisConcept = conceptService.getConceptByName(conceptXAxis.getName());
                    if (xAxisConcept == null) {
                        continue;
                    }
                }
                
                questions.clear();
                questions.add(xAxisConcept);
                List<Obs> matchingObs = obsService.getObservations(persons, null, questions, null, null, null, null, 
                    null, null, null, null, false);
                if (matchingObs == null || matchingObs.size() == 0) {
                    continue;
                }
                
                // We need to find the x axis value with same date as the y for IU Health historical data since it's all tied 
                // to the same encounter.
                yAxisDate = DateUtil.getDateTime(yAxisDate, 0, 0, 0, 0);
                for (Obs matchingOb : matchingObs) {
                    Date xAxisDate = matchingOb.getObsDatetime();
                    if (xAxisDate == null) {
                        continue;
                    }
                    
                    xAxisDate = DateUtil.getDateTime(xAxisDate, 0, 0, 0, 0);
                    if (yAxisDate.compareTo(xAxisDate) == 0) {
                        xValue = Float.parseFloat(matchingOb.getValueNumeric().toString());
                        break;
                    }
                }
            }
            
            if (xValue != null) {
                Float xPosition = computeAbsolutePosition(conceptXAxis.getMinPosition(), conceptXAxis.getMaxPosition(), 
                    conceptXAxis.getMinVal(), conceptXAxis.getMaxVal(), xValue);
                Float yValue = Float.parseFloat(currObs.getValueNumeric().toString());
                Float yPosition = computeAbsolutePosition(conceptYAxis.getMinPosition(), conceptYAxis.getMaxPosition(), 
                    conceptYAxis.getMinVal(), conceptYAxis.getMaxVal(), yValue);
                image.setAbsolutePosition(xPosition, yPosition);
                content.addImage(image);
            }
        }
    }
    
    private String convertPdfToImage(String growthChartDirectory, String pdfFilename, float rotation) {
        String newFilename = null;
        File file = null;
        try (PDDocument document = PDDocument.load(new File(growthChartDirectory, pdfFilename))) {
          PDFRenderer pdfRenderer = new PDFRenderer(document);
          
          // first page to BufferedImage
          BufferedImage buffImage = pdfRenderer.renderImageWithDPI(0, 72, ImageType.RGB);
          
          // This section is for performance reasons.  If we just try to write the buffered image 
          // directly to disk, it is extremely slow.
          BufferedImage indexedImage = new BufferedImage(buffImage.getWidth(),
          buffImage.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
          Graphics2D g = indexedImage.createGraphics();
          g.drawImage(buffImage, 0,0,null);
          buffImage = indexedImage;
          
          // rotate the image
          buffImage = ImageUtil.getRotatedImage(buffImage, Math.round(rotation));
          
          // write image to disk
          newFilename = pdfFilename.replace(".pdf", ".png");
          file = new File(growthChartDirectory, newFilename);
          ImageIOUtil.writeImage(buffImage, file.getAbsolutePath(), 300);
            
//              // open the file
//              Document document = new Document();
//              try {
//                 document.setFile(growthChartDirectory + pdfFilename);
//              } catch (PDFException ex) {
//                  log.error("Error parsing PDF document", ex);
//                  return null;
//              } catch (PDFSecurityException ex) {
//                  log.error("Error encryption not supported", ex);
//                  return null;
//              } catch (FileNotFoundException ex) {
//                  log.error("Error file not found", ex);
//                  return null;
//              } catch (IOException ex) {
//                  log.error("Error IOException", ex);
//                  return null;
//              }
//
//              // save page capture to file.
//              float scale = 1.0f;
//
//              // Paint the page content to an image and
//              // write the image to file
//              if (document.getNumberOfPages() > 0) {
//                 BufferedImage image = (BufferedImage) document.getPageImage(
//                     0, GraphicsRenderingHints.PRINT, Page.BOUNDARY_CROPBOX, rotation, scale);
//                 // This section is for performance reasons.  If we just try to write the buffered image 
//                 // directly to disk, it is extremely slow.
//                 BufferedImage indexedImage = new BufferedImage(image.getWidth(),
//                     image.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
//                 Graphics2D g = indexedImage.createGraphics();
//                 g.drawImage(image, 0, 0, null);
//                 image = indexedImage;
//                 try {
//                    newFilename = pdfFilename.replace(".pdf", ".png");
//                    file = new File(growthChartDirectory, newFilename);
//                    ImageIO.write(image, "png", file);
//                 } catch (IOException e) {
//                     log.error("Error saving image to file system", e);
//                     file = null;
//                 } finally {
//                     image.flush();
//                 }
//              }
//
//              // clean up resources
//              document.dispose();
        }
        catch (Exception ex) {
            log.error("Error converting PDF to image", ex);
            return null;
        }
        
        return growthChartDirectory + newFilename;
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