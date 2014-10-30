$(document).ready(function () {
	setSize(800, 475);
	
	$("#retryButton").button();
	$("#retryButton").click(function(event) {
		loadForms()
		event.preventDefault();
	});
	
	loadForms();
});

function setSize(width,height) {
	if (window.outerWidth) {
		window.outerWidth = width;
		window.outerHeight = height;
	}
	else if (window.resizeTo) {
		window.resizeTo(width,height);
	}
	window.moveTo(50,50);
}

function loadForms() {
	$("#formsServerError").hide();
	$("#formsContainer").hide();
	$("#formsLoading").show();
	
	var patientId = $("#patientId").val();
	var sessionId = $("#sessionId").val();
	var locationId = $("#locationId").val();
	var locationTagId = $("#locationTagId").val();
	var action = "action=getForcePrintForms&patientId=" + patientId + "&sessionId=" + sessionId + "&locationId=" + 
		locationId + "&locationTagId=" + locationTagId;
	var url = "/openmrs/moduleServlet/chica/chicaMobile";
	$.ajax({
	  "cache": false,
	  "dataType": "xml",
	  "data": action,
	  "type": "POST",
	  "url": url,
	  "timeout": 30000, // optional if you want to handle timeouts (which you should)
	  "error": handleGetForcePrintFormsError, // this sets up jQuery to give me errors
	  "success": function (xml) {
          parseForcePrintForms(xml);
      }
	});
}

function handleGetForcePrintFormsError(xhr, textStatus, error) {
	$("#formsLoading").hide();
	$("#formsServerErrorText").html('<span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span><span>Error loading forms: ' + error + '</span>');
	$("#formsServerError").show();
}

function parseForcePrintForms(responseXML) {
	// no matches returned
    if (responseXML === null) {
    	$("#formsLoading").hide();
    	$("#forms").selectmenu();
    	$("#formsContainer").show();
        return false;
    } else {
    	var options = [];
    	$(responseXML).find("forcePrintJIT").each(function () {
        	var formName = $(this).find("displayName").text();
            var formId = $(this).find("formId").text();
            
            options.push("<option value='" + formId + "'>" + formName + "</option>");
        });
    	
    	$("select#forms").append(options.join("")).selectmenu().selectmenu("menuWidget").addClass("overflow");
    	$("#formsLoading").hide();
    	$("#formsContainer").show();
    }
}