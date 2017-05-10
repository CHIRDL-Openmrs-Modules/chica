/**
 * 
 */
package org.openmrs.module.chica.web;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.xmlBeans.FindObsvTerm;
import org.openmrs.module.chica.xmlBeans.ObsvDictionary;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * @author tmdugan
 * 
 */
public class ParseDictionaryController extends SimpleFormController
{

	protected final Log log = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception
	{
		return "testing";
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception
	{
		Map<String, Object> map = new HashMap<String, Object>();
		String parse = request.getParameter("parse");
		int conceptsCreated = 0;

		if (parse != null)
		{
			try
			{
				ConceptService conceptService = Context.getConceptService();

				// parse the dictionary file
				String filename = "C:/Documents and Settings/tmdugan/Desktop/Dictionary.xml";
				InputStream input = new FileInputStream(filename);
				ObsvDictionary dictionary = (ObsvDictionary) XMLUtil
						.deserializeXML(ObsvDictionary.class, input);
				input.close();

				// create the dictionary terms
				ArrayList<FindObsvTerm> terms = dictionary.getTerms();

				for (FindObsvTerm currTerm : terms)
				{
					Concept newConcept = null;

					if (currTerm.getUnits() != null&&
							currTerm.getAnswers()==null)
					{
						newConcept = new ConceptNumeric();
					} else
					{
						newConcept = new Concept();
					}
					ConceptName conceptName = new ConceptName();
					conceptName.setLocale(new Locale("en_US"));
					conceptName.setName(currTerm.getName().trim());
					conceptName.setDateCreated(new java.util.Date());

					newConcept.addName(conceptName);
					if (newConcept instanceof ConceptNumeric)
					{
						((ConceptNumeric) newConcept).setUnits(currTerm
								.getUnits());
					}

					String type = currTerm.getType();

					ConceptClass conceptClass = getConceptClass(type);
					newConcept.setConceptClass(conceptClass);

					newConcept
							.setConceptId(conceptService.getMaxConceptId());

					String datatype = "";

					if (newConcept instanceof ConceptNumeric)
					{
						datatype = "Numeric";
					} else if (currTerm.getAnswers() != null)
					{
						datatype = "Coded";
					}else
					{
						datatype = "Text";
					}

					ConceptDatatype conceptDatatype = conceptService
							.getConceptDatatypeByName(datatype);
					newConcept.setDatatype(conceptDatatype);
					newConcept.setDateCreated(new java.util.Date());
					conceptService.saveConcept(newConcept);
					processAnswers(currTerm.getAnswers(), conceptClass,
							newConcept);
					conceptsCreated++;
					
					if(conceptsCreated % 500 == 0){
						log.info("Context.clear session");
						Context.clearSession();
					}
					log.info("Number concepts created: "+conceptsCreated);
				}
			} catch (Exception e)
			{
				this.log.error(e.getMessage());
				this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
			}
		}

		return map;
	}

	private void processAnswers(String answerString, ConceptClass conceptClass,
			Concept newConcept)
	{
		if (answerString == null)
		{
			return;
		}
		ConceptService conceptService = Context.getConceptService();
		StringTokenizer tokenizer = new StringTokenizer(answerString, "\n\r");

		while (tokenizer.hasMoreTokens())
		{
			String currToken = tokenizer.nextToken().trim();
			Concept currAnswer = conceptService.getConceptByName(currToken);
			if (currAnswer == null)
			{
				currAnswer = new Concept();
				ConceptName conceptName = new ConceptName();
				conceptName.setLocale(new Locale("en_US"));
				conceptName.setName(currToken);
				conceptName.setDateCreated(new java.util.Date());
				currAnswer.addName(conceptName);
				currAnswer.setConceptClass(conceptClass);
				currAnswer.setConceptId(conceptService.getMaxConceptId());
				ConceptDatatype answerDatatype = conceptService
						.getConceptDatatypeByName("Coded");
				currAnswer.setDatatype(answerDatatype);
				currAnswer.setDateCreated(new java.util.Date());
				conceptService.saveConcept(currAnswer);
			}
			newConcept.addAnswer(new ConceptAnswer(currAnswer));
			conceptService.saveConcept(newConcept);
		}
	}
	
	private ConceptClass getConceptClass(String type){
		ConceptService conceptService = Context.getConceptService();
		ConceptClass conceptClass = conceptService.getConceptClassByName(type);
		if(conceptClass == null){
			conceptClass = conceptService.getConceptClassByName("CHICA");
		}
		return conceptClass;
	}
}
