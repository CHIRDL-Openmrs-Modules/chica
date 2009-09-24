/**
 * 
 */
package org.openmrs.module.chica.nonInMemoryTests;

import org.junit.Test;
import java.io.FileOutputStream;

import org.junit.Before;
import org.openmrs.module.chica.test.TestUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * @author Tammy Dugan
 * 
 */
@SkipBaseSetup
public class DBUnitXMLToDatabase extends BaseModuleContextSensitiveTest
{
	@Before
	public void runBeforeEachTest() throws Exception {
		// setComplete();
		// executeDataSet(TestUtil.DBUNIT_SETUP_FILE);
	}

	@Test
	public void testDBUnitXML() throws Exception
	{
		// String dtdFilename="test/dbunitFiles/tableSetupDTD.dtd";
		// TestUtil.createDTDFile(getConnection(),dtdFilename);

		// String filename = "C:\\Documents and
		// Settings\\tmdugan\\workspace\\chica\\test\\dbunitFiles\\tableSetup.xml";
		// TestUtil.dumpDatabase(getConnection(), new
		// FileOutputStream(filename));
		String filename = "C:\\Documents and Settings\\tmdugan\\Desktop\\out.xml";
		FileOutputStream out = new FileOutputStream(filename);
		//ArrayList<String> tableNames = new ArrayList<String>();
		//tableNames.add("field");
		//TestUtil.dumpTable(tableNames, getConnection(), out);
		String query = "select * from concept where concept_id in (select concept_id from concept_name where name='CALCIUM (SMA)')";
		TestUtil.dumpTable("concept", query, getConnection(), out);

		query = "select * from concept_name where name='CALCIUM (SMA)'";
		TestUtil.dumpTable("concept_name", query, getConnection(),out);

		query = "select * from concept_numeric where concept_id in (select concept_id from concept_name where name='CALCIUM (SMA)')";
		TestUtil.dumpTable("concept_numeric", query, getConnection(), out);

		out.flush();
		out.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase()
	{
		return false;
	}

}
