package org.openmrs.module.chica.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * @author Tammy Dugan
 * 
 */
public class TestUtil
{
	public static final String DBUNIT_SETUP_FILE = "dbunitFiles/tableSetup.xml";
	public static final String EXPORT_PSF_FILE = "testFiles/export_PSF.xml";
	public static final String WORK_DOC_SHEET_FILE = "testFiles/DocWorkSheet.XML";
	public static final String PRESCREENER_FILE = "testFiles/PreScreener.xml";
	public static final String LENGTH_AGE_FILE = "dbunitFiles/lengthAgeCalculation.xml";
	public static final String BP_FILE = "dbunitFiles/highBPCalculation.xml";
	public static final String PATIENT_PROVIDER_FILE = "dbunitFiles/patientProviderInfo.xml";
	public static final String ENCOUNTERS_FILE = "dbunitFiles/encounters.xml";
	public static final String WEIGHT_AGE_FILE = "dbunitFiles/weightAgeCalculation.xml";
	public static final String BMI_AGE_FILE = "dbunitFiles/bmiAgeCalculation.xml";
	public static final String CONCEPTS_FILE = "dbunitFiles/concepts.xml";
	public static final String RULES_FILE = "dbunitFiles/rules.xml";
	public static final String PATIENT_FORMS_FILE = "dbunitFiles/patientForms.xml";

	/**
	 * Turns database table into dbunit xml
	 * @param tableNames
	 * @param con
	 * @param out
	 * @throws Exception
	 */
	public static void dumpTable(ArrayList<String> tableNames, Connection con,
			OutputStream out) throws Exception
	{
		IDatabaseConnection connection = new DatabaseConnection(con);

		QueryDataSet outputSet = new QueryDataSet(connection);
		for(String tableName:tableNames)
		{
			outputSet.addTable(tableName);
		}
		FlatXmlDataSet.write(outputSet, out);
	}
	
	public static void dumpTable(String tableName, String query, Connection con,
			OutputStream out) throws Exception
	{
		IDatabaseConnection connection = new DatabaseConnection(con);

		QueryDataSet outputSet = new QueryDataSet(connection);
		outputSet.addTable(tableName, query);
		FlatXmlDataSet.write(outputSet, out);
	}
	
	public static void dumpDatabase(Connection con,OutputStream out) throws Exception{
		IDatabaseConnection connection = new DatabaseConnection(con);
        IDataSet fullDataSet = connection.createDataSet();
        FlatXmlDataSet.write(fullDataSet, out);
	}
	
	/**
	 * creates a DTD file
	 * @param jdbcConnection
	 * @param dtdFilename
	 * @throws DataSetException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void createDTDFile(Connection jdbcConnection,
			String dtdFilename) throws DataSetException, FileNotFoundException, IOException, SQLException
	{
		//IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        // write DTD file
        //FlatDtdDataSet.write(connection.createDataSet(),
          //      new FileOutputStream(dtdFilename));
	}
}

