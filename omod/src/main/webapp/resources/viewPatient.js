$j("#Enter").click(function (e) {
	e.preventDefault(); // <------------------ stop default behaviour of button
	var element = this;    
	var url = ctx + "/moduleServlet/chica/chica";
	var newTab = window.open ('', '_blank');
	$j.ajax({
		"cache": false,
		  "dataType": "xml",
		  "data": "action=verifyMRN&mrn=" + encodeURIComponent($j("#mrn").val()),
		  "type": "POST",
		  "url": url,
		  "timeout": 60000, 
		  "error": handleVerifyEncounterMRNAjaxError, // this sets up jQuery to give me errors
		  "success": function (xml) {
				verifyEncounterMRN(xml, newTab);
		   },
		error: function () {
			newTab.close();
			alert("An error has occured!!!");
		}
	});
});

function verifyEncounterMRN(responseXML, newTab) {
	// no matches returned
	if (responseXML === null) { 
		$j("#encounterMrnError").html("<p><b>Error retrieving MRN information.  Please try again.</b></p>");
	} else {
		var result = $j(responseXML).find("result").text();
		var validEncounter = $j(responseXML).find("validEncounter").text();
		var mrn = $j("#mrn").val();
		if (result == "true" && validEncounter == "true") {
			newTabURL("viewEncounter.form?mrn=" + $j("#mrn").val(), newTab);
			$j("#encounterMrnError").removeClass("alert").html("");
			$j("#encounterMrnMessage h5").html("Enter the patient MRN to display all encounters for that patient");
			$j("#mrn").val('');
		} else if (result == "false"){
			newTab.close();
			if (mrn != "") {
				$j("#encounterMrnMessage h5").html("");
				$j("#encounterMrnError").addClass("alert").html("<b>MRN: "+ mrn +" is not a valid MRN.</b>");
			}
			$j("#encounterMrnMessage h5").html("Enter the patient MRN to display all encounters for that patient");
			$j("#encounterMrnError").removeClass("alert").html("");
		} else { 
			newTab.close();
			$j("#encounterMrnMessage h5").html("");
			$j("#encounterMrnError").addClass("alert").html("<b>There is no record of an existing patient with MRN: "+ mrn +"<br>You may need to add a patient through manual checkin.</b>");
		} 
	}
}

function newTabURL(url, newTab) {
	newTab.location = url;
	return false;
}
function handleVerifyEncounterMRNAjaxError(xhr, textStatus, error) {
	var error = "An error occurred on the server.";
	if (textStatus === "timeout") {
		error = "The server took too long to verify the MRN.";
	}
	$j("#encounterMrnError").html("<p>" + error + "  Click OK to try again.</p>");
}

