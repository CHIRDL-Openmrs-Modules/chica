package org.openmrs.module.chica.rule;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.op.OperandConcept;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;

import java.util.Date;

public class testChicaObsDatasource implements Rule
{
	private LogicService logicService = Context.getLogicService();
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[]
		{};
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL()
	{
		return 0; // 60 * 30; // 30 minutes
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.CODED;
	}

	public Result eval(LogicContext context, Integer patientId,
			Map<String, Object> parameters) throws LogicException
	{
		try{
		String conceptName = null;
		
		if (parameters != null)
		{
			conceptName = (String) parameters.get("conceptName");
		}
		
		//test all
		LogicCriteria criteria  = new LogicCriteriaImpl(conceptName);

		Result ruleResult = context.read(patientId, this.logicService
			.getLogicDataSource("RMRS"), criteria);
		
		System.out.println("Results of all:\n"+string(ruleResult));

		
		//test last
		criteria  = new LogicCriteriaImpl(conceptName).last();

		ruleResult = context.read(patientId, this.logicService
			.getLogicDataSource("CHICA"), criteria);
		
		System.out.println("Results of last:\n"+string(ruleResult));
		
		
		//test first
		criteria = new LogicCriteriaImpl(conceptName).first();

		ruleResult = context.read(patientId, this.logicService
			.getLogicDataSource("CHICA"), criteria);
		
		System.out.println("Results of first:\n"+string(ruleResult));

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -25);
		Date date = calendar.getTime();
		
		System.out.println("date is: "+date.toString()+"\n");
		//test before
		criteria = new LogicCriteriaImpl(conceptName).
			before(date);

		ruleResult = context.read(patientId, this.logicService
				.getLogicDataSource("CHICA"), criteria);
		
		System.out.println("Results of before:\n"+string(ruleResult));
		
		//test after
		criteria = new LogicCriteriaImpl(conceptName).
			after(date);

		ruleResult = context.read(patientId, this.logicService
			.getLogicDataSource("CHICA"), criteria);
		
		System.out.println("Results of after:\n"+string(ruleResult));
		
		//test gt
		criteria = new LogicCriteriaImpl(conceptName).
			gt(10);

		ruleResult = context.read(patientId, this.logicService
		.getLogicDataSource("CHICA"), criteria);
	
		System.out.println("Results of gt:\n"+string(ruleResult));
		
		//test lt
		criteria = new LogicCriteriaImpl(conceptName).
			lt(10);

		ruleResult = context.read(patientId, this.logicService
		.getLogicDataSource("CHICA"), criteria);
	
		System.out.println("Results of lt:\n"+string(ruleResult));
		
		//test lte
		criteria = new LogicCriteriaImpl(conceptName).
			lte(10.2);

		ruleResult = context.read(patientId, this.logicService
		.getLogicDataSource("CHICA"), criteria);
	
		System.out.println("Results of lte:\n"+string(ruleResult));
		
		//test gte
		criteria = new LogicCriteriaImpl(conceptName).
			gte(10.2);

		ruleResult = context.read(patientId, this.logicService
		.getLogicDataSource("CHICA"), criteria);
	
		System.out.println("Results of gte:\n"+string(ruleResult));
		
		//test contains
		String answerName = "neg";
		Concept answer = new Concept();
		answer.setConceptId(1325);
		ConceptName name = new ConceptName();
		name.setName( answerName);
		name.setLocale(new Locale("en_US"));
		answer.addName(name);
		criteria = new LogicCriteriaImpl("GLUCOSE-UA").contains(new OperandConcept(answer));

		ruleResult = context.read(patientId, this.logicService
					.getLogicDataSource("CHICA"), criteria);
	
		System.out.println("Results of contains:\n"+string(ruleResult));
		
		//test eq numeric
		criteria = new LogicCriteriaImpl(conceptName).
			equalTo(10.2);

		ruleResult = context.read(patientId, this.logicService
		.getLogicDataSource("CHICA"), criteria);
	
		System.out.println("Results of eq:\n"+string(ruleResult));
		
		//test eq concept
		answerName = "neg";
		answer = new Concept();
		answer.setConceptId(1325);
		name = new ConceptName();
		name.setName( answerName);
		name.setLocale(new Locale("en_US"));
		answer.addName(name);
		criteria = new LogicCriteriaImpl("GLUCOSE-UA").equalTo(new OperandConcept(answer));

		ruleResult = context.read(patientId, this.logicService
					.getLogicDataSource("CHICA"), criteria);
	
		System.out.println("Results of eq:\n"+string(ruleResult));
		
		
		//test eq text
		criteria = new LogicCriteriaImpl("SP GRAV-UA").
			equalTo("<1.005");

		ruleResult = context.read(patientId, this.logicService
		.getLogicDataSource("CHICA"), criteria);
	
		System.out.println("Results of eq:\n"+string(ruleResult));
		
		//test or text
		criteria = new LogicCriteriaImpl("SP GRAV-UA").
			equalTo("<1.005").or(new LogicCriteriaImpl("SP GRAV-UA").
			lt(1.025));

		ruleResult = context.read(patientId, this.logicService
		.getLogicDataSource("CHICA"), criteria);
	
		System.out.println("Results of eq:\n"+string(ruleResult));
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return Result.emptyResult();
	}
	
	private String getResultString(Result ruleResult)
	{
		String result = ruleResult.toString();
		
		if(result == null||result.length()==0)
		{
			Concept concept = ruleResult.toConcept();
			if(concept!=null)
			{
				result = concept.toString();
			}
		}
		
		if(result == null||result.length()==0)
		{
			result = String.valueOf(ruleResult.toNumber());
		}
		return result;
	}
	
	private String string(Result ruleResult)
	{
		StringBuffer buf = new StringBuffer();
		
		if(ruleResult.size()==0)
		{
			return ruleResult.getResultDate()+" "+getResultString(ruleResult)+"\n";
		}
		
		for(Result currResult:ruleResult)
		{
			buf.append(currResult.getResultDate()+" "+getResultString(currResult)+"\n");
		}
		return buf.toString();
	}

}