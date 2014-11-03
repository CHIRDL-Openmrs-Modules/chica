var isIE = true;
$(document).ready(function () {
	isIE = checkForIE();
	$(window).resize(function() {
		// Update the iframe height
		$("#formFrame").height($(window).height() - 220);
	});
	
	setSize();
	
	$("#formFrame").on("load", function () {
		$("#frameLoading").hide();
		$("#frameContainer").show();
    });
	
	$("#createButton").button({ disabled: true });
	$("#closeButton").button();
	$("#retryCloseButton").button();
	$("#retryButton").button();
	$("#retryButton").click(function(event) {
		loadForms();
		event.preventDefault();
	});
	
	$("#retryCloseButton").click(function(event) {
		loadForms();
		event.preventDefault();
	});
	
	$("#closeButton").click(function(event) {
		window.close();
		event.preventDefault();
	});
	
	$("#createButton").click(function(event) {
		loadForm();
		event.preventDefault();
	});
	
	loadForms();
});

function checkForIE() {
    var ua = window.navigator.userAgent;
    var msie = ua.indexOf("MSIE ");

    if (msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./))      // If Internet Explorer, return version number
        return true;
    else                 // If another browser, return 0
        return false;
}

function iframeLoaded() {
	$("#frameLoading").hide();
	$("#frameContainer").show();
}

function setSize() {
	window.resizeTo($(window).width() * 0.95,$(window).height() * 0.95);
}

function loadForms() {
	$("#formsServerError").hide();
	$("#formsContainer").hide();
	$("#frameContainer").hide();
	$("#frameLoading").hide();
	$("#frameError").hide();
	$("#formsLoading").show();
	$("#buttonPanel").show();
	
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
	  "error": handleGetAvailableFormsError, // this sets up jQuery to give me errors
	  "success": function (xml) {
		  parseAvailableForms(xml);
      }
	});
}

function handleGetAvailableFormsError(xhr, textStatus, error) {
	$("#formsLoading").hide();
	$("#buttonPanel").hide();
	$("#formsServerErrorText").html('<span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span><span>Error loading forms: ' + error + '</span>');
	$("#formsServerError").show();
}

function parseAvailableForms(responseXML) {
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
    	
    	if (options.length > 0) {
    		$("#createButton").button("option", "disabled", false);
    	}
    }
}

function loadForm() {
	$("#frameContainer").hide();
	$("#frameLoading").show();
	
	var patientId = $("#patientId").val();
	var sessionId = $("#sessionId").val();
	var locationId = $("#locationId").val();
	var locationTagId = $("#locationTagId").val();
	var formId = $("#forms").val();
	var randomNumber = Math.floor((Math.random() * 10000) + 1); 
	var action = "action=forcePrintForm&patientId=" + patientId + "&sessionId=" + sessionId + "&locationId=" + 
		locationId + "&locationTagId=" + locationTagId + "&formId=" + formId + "&randomNumber=" + randomNumber + 
		"#view=fit&navpanes=0";
	var url = "/openmrs/moduleServlet/chica/chicaMobile?";
	
	$('#formFrame').attr("src", url + action);
	
	// IE doesn't fire the onload event for the iframe.
	if (isIE) {
		setTimeout(iframeLoaded, 2000);
	}
}
