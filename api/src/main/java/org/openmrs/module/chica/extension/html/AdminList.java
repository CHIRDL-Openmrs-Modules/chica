package org.openmrs.module.chica.extension.html;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

/**
 * @author Tammy Dugan
 *
 */
public class AdminList extends AdministrationSectionExt {

	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	@Override
	public String getTitle() {
		return "chica.title";
	}
	
	@Override
	public Map<String, String> getLinks() {
		
		Map<String, String> map = new HashMap<String, String>();
				
		map.put("module/chica/greaseBoard.form", "Grease Board");
		map.put("module/chica/viewEncounter.form", "View Encounters");
		map.put("module/chica/chicaRuleTester.form", "Rule Tester");
		map.put("module/chica/chicaNoteTester.form", "Note Tester");
		map.put("module/chica/cacheConfiguration.form", "Cache Configuration");
		return map;
	}
	
}
