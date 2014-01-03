package org.openmrs.module.chica.nonInMemoryTests;

import org.junit.Test;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.OldRule;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * @author Tammy Dugan
 * 
 */
@SkipBaseSetup
public class ParseTableToMLM extends BaseModuleContextSensitiveTest {
	
	@Test
	@SkipBaseSetup
	public void testParseTableToMLM() throws Exception
	{
		String baseDirectory = "C:\\Documents and Settings\\tmdugan\\workspace\\chica\\ruleLibrary\\test\\";
		ChicaService chicaService = Context
				.getService(ChicaService.class);
		List<OldRule> rules = chicaService.getAllOldRules();
		ArrayList<String> filenames = new ArrayList<String>();
		filenames.add("Pain");
				
		for (OldRule currRule : rules)
		{
			String filename = currRule.getFilename();
			if(!filenames.contains(filename))
			{
				continue;
			}
			if (filename != null)
			{
				filename = filename.trim();
				filename = filename.replaceAll("-", "_");
				filename = filename.replaceAll(" ", "_");
				filename = filename.replaceAll("\\+", "_plus_");
				if (!filename.endsWith(".mlm"))
				{
					filename += ".mlm";
				}
				try
				{
					PrintWriter fileOutput = new PrintWriter(new FileWriter(
							baseDirectory + filename));
					writeMLM(currRule, fileOutput);
					fileOutput.flush();
					fileOutput.close();
				} catch (Exception e)
				{
					System.out.println("Error writing file: " + filename);
				}
			} else
			{
				System.out.println("No filename for rule: "
						+ currRule.getRuleId());
			}
		}
	}

	private void writeMLM(OldRule obj, PrintWriter output)
	{
		output.println("Maintenance:");
		String title = obj.getTitle();
		if(title != null){
			title = title.replaceAll("-", "");
		}
		output.println("	Title:		" + title + ";;");
		String filename = obj.getFilename();
		if (filename != null)
		{
			filename = filename.replaceAll("-", "_");
		}
		output.println("	Filename:		" + filename + ";;");
		output.println("	Version:	" + obj.getVersion() + ";;");
		output.println("	Institution:	" + obj.getInstitution() + ";;");
		output.println("	Author:		" + obj.getAuthor() + ";;");
		output.println("	Specialist:	Pediatrics;;");
		String dateCreated = obj.getDateCreated();
		dateCreated = dateCreated.trim();
		
		SimpleDateFormat sdf = new SimpleDateFormat
	     ("MM/dd/yyyy HH:mm:ss a", Locale.US);
		
		try
		{
			Date date = sdf.parse(dateCreated);
			sdf = new SimpleDateFormat
		     ("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
			dateCreated = sdf.format(date);
		} catch (ParseException e)
		{
		}
		output.println("	Date:		" + dateCreated + ";;");
		output.println("	Validation:	;;");
		output.println("Library:");
		output.println("	Purpose:		" + obj.getPurpose() + ";;");
		output.println("	Explanation:	" + obj.getExplanation() + ";;");
		output.println("	Keywords:	" + obj.getKeywords() + ";;");
		output.println("	Citations:		" + obj.getCitations() + ";;");
		output.println("	Links:		" + obj.getLinks() + ";;");
		output.println("Knowledge:");
		output.println("	Type:		data_driven;;");
		output.println("Data:\n");
		output.println("mode:=read {mode from Parameters};");
		output.println("Box1:=read {box1 from Parameters};");
		output.println("Box2:=read {box2 from Parameters};");
		output.println("Box3:=read {box3 from Parameters};");
		output.println("Box4:=read {box4 from Parameters};");
		output.println("Box5:=read {box5 from Parameters};");
		output.println("Box6:=read {box6 from Parameters};\n");
		output.println("If (mode = PRODUCE) then");
		output.println(obj.getData());
		output.println("endif");
		output.println(";;");
		output.println("Priority:		" + obj.getPriority() + ";;");
		output.println("	Evoke:		;;");
		output.println("Logic:");
		output.println("If (mode = PRODUCE) then\n");
		output.println(obj.getLogic().replaceAll(";;;", ";"));
		output.println("");
		output.println("endif\n");

		String scanAction = obj.getScanAction();
		if(scanAction != null){
			scanAction = scanAction.trim();
		}

		if (scanAction != null && scanAction.length() > 0)
		{
			output.println("If (mode = CONSUME) then\n");

			scanAction = scanAction.replaceAll("\\(Box1\\)", "(Box1=true)");
			scanAction = scanAction.replaceAll("\\(Box2\\)", "(Box2=true)");
			scanAction = scanAction.replaceAll("\\(Box3\\)", "(Box3=true)");
			scanAction = scanAction.replaceAll("\\(Box4\\)", "(Box4=true)");
			scanAction = scanAction.replaceAll("\\(Box5\\)", "(Box5=true)");
			scanAction = scanAction.replaceAll("\\(Box6\\)", "(Box6=true)");
			String searchString = scanAction;
			String finalString = "";
			while (searchString.length() > 0)
			{
				int index = searchString.indexOf(";");
				index++;
				String statement = searchString.substring(0, index);
				searchString = searchString.substring(index).trim();

				index = statement.indexOf("then");
				if (index >= 0)
				{
					String parameters = statement.substring(index + 4,
							statement.length());
					parameters = parameters.replace(" = ", "\",\"");
					parameters = parameters.replace("=", ",");
					parameters = parameters.replace(";", "");
					statement = statement.substring(0, index)
							+ "then\n CALL storeObs With \"" + parameters.trim() + "\";\n";
					statement += "endif;\n";
					finalString += statement;
				}

			}
			output.println(finalString);

			output.println("\nendif");
		}

		output.println(";;");
		output.println("Action:");
		output.println(obj.getPrintAction());
		if(obj.getRuleType().trim().equalsIgnoreCase("PWS")){
			output.println("write (\""+obj.getBox1Text()+"\");");
			output.println("write (\""+obj.getBox2Text()+"\");");
			output.println("write (\""+obj.getBox3Text()+"\");");
			output.println("write (\""+obj.getBox4Text()+"\");");
			output.println("write (\""+obj.getBox5Text()+"\");");
			output.println("write (\""+obj.getBox6Text()+"\");");
		}
		output.println(";;");
		output.println("Age_Min: " + obj.getMinAge() + " "
				+ obj.getMinAgeUnit() + ";;");
		output.println("Age_Max: " + obj.getMaxAge() + " "
				+ obj.getMaxAgeUnit() + ";;");

		output.println("end:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase()
	{
		return false;
	}

}