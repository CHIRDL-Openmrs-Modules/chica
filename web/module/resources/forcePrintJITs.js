var isChromeSafari = false;
$(document).ready(function () {
	isChromeSafari = forcePrint_checkForChromeSafari();

	$(".force-print-retry-button").button();
	$(".force-print-retry-button").click(function(event) {
		forcePrint_loadForms();
		event.preventDefault();
	});
	
	$(".force-print-forms-server-error").hide();
	$(".force-print-forms-container").hide();
	$(".force-print-form-container").hide();
	$(".force-print-form-loading").hide();
	$(".force-print-forms-loading").show();
	$(".force-print-button-panel").show();
	
	$("#force-print-form-list li").tooltip();
});

function forcePrint_checkForChromeSafari() {
	var isChrome = /chrom(e|ium)/.test(navigator.userAgent.toLowerCase());
	var isSafari = /safari/.test(navigator.userAgent.toLowerCase());
	return isChrome || isSafari;
}

function forcePrint_formLoaded() {
	$(".force-print-form-loading").hide();
	$(".force-print-form-container").show();
}

function forcePrint_loadForms() {
	$(".force-print-forms-server-error").hide();
	$(".force-print-forms-loading").show();
  	$(".force-print-forms-container").hide();
	var patientId = $("#patientId").val();
	var mrn = $("#mrn").val();
	var sessionId = $("#sessionId").val();
	var locationId = $("#locationId").val();
	var locationTagId = $("#locationTagId").val();
	var action = "";
	if (patientId === "") {
		action = "action=getForcePrintForms&mrn=" + mrn + "&sessionId=" + sessionId + "&locationId=" + 
			locationId + "&locationTagId=" + locationTagId;
	} else {
		action = "action=getForcePrintForms&patientId=" + patientId + "&sessionId=" + sessionId + "&locationId=" + 
			locationId + "&locationTagId=" + locationTagId;
	}
	var url = "/openmrs/moduleServlet/chica/chica";
	$.ajax({
	  "cache": false,
	  "dataType": "xml",
	  "data": action,
	  "type": "POST",
	  "url": url,
	  "timeout": 30000, // optional if you want to handle timeouts (which you should)
	  "error": forcePrint_handleGetAvailableFormsError, // this sets up jQuery to give me errors
	  "success": function (xml) {
		  forcePrint_parseAvailableForms(xml);
      }
	});
}

function forcePrint_handleGetAvailableFormsError(xhr, textStatus, error) {
	$(".force-print-forms-loading").hide();
	$(".force-print-button-panel").hide();
	$(".force-print-forms-server-error-text").html('<span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span><span>Error loading forms: ' + error + '</span>');
	$(".force-print-forms-server-error").show();
}

function forcePrint_parseAvailableForms(responseXML) {
	// no matches returned
    if (responseXML === null) {
    	$(".force-print-forms-loading").hide();
    	$("#force-print-form-list").selectable();
    	$(".force-print-forms-container").show();
    } else {
    	$(responseXML).find("forcePrintJIT").each(function () {
        	var formName = $(this).find("displayName").text();
            var formId = $(this).find("formId").text();
            var outputType = $(this).find("outputType").text();
            $('<li id="' + formId + '" title="' + formName + '" outputType="' + outputType + '">' + formName + '</li>').addClass('ui-widget-content').appendTo($('#force-print-form-list'));
        });
    }
    
    $(".force-print-form-list").css({"max-width":"325px"});

  	$(".force-print-forms-loading").hide();
  	$(".force-print-forms-container").show();
  	$('#force-print-form-list').selectable("refresh");

  	var divHeight = $(".force-print-forms-list").parent().parent().parent().height();
  	$(".force-print-form-list").selectable().css({"max-height":(divHeight * 0.60) + "px"});
}

function forcePrint_loadForm() {
	$(".force-print-form-container").hide();
	$(".force-print-form-loading").show();
	
	var patientId = $("#patientId").val();
	var mrn = $("#mrn").val();
	var sessionId = $("#sessionId").val();
	var locationId = $("#locationId").val();
	var locationTagId = $("#locationTagId").val();
	var formIds = forcePrint_getSelectedForms().toString();
	var randomNumber = Math.floor((Math.random() * 10000) + 1); 
	var action = "";
	if (patientId === "") {
		action = "action=forcePrintForms&mrn=" + mrn + "&sessionId=" + sessionId + "&locationId=" + 
			locationId + "&locationTagId=" + locationTagId + "&formIds=" + formIds + "&randomNumber=" + randomNumber + 
			"#view=fit&navpanes=0";
	} else {
		action = "action=forcePrintForms&patientId=" + patientId + "&sessionId=" + sessionId + "&locationId=" + 
			locationId + "&locationTagId=" + locationTagId + "&formIds=" + formIds + "&randomNumber=" + randomNumber + 
			"#view=fit&navpanes=0";
	}
	
	var url = "/openmrs/moduleServlet/chica/chica?";
	var obj = $(".force-print-form-object");
	var container = obj.parent();
	var newUrl = url + action;
	var newobj = obj.clone();
	obj.remove();
	newobj.attr("data", url + action);
	newobj.on("load", function () {
		$(".force-print-form-loading").hide();
		$(".force-print-form-container").show();
    });
	
	container.append(newobj);
	
	// Chrome/Safari doesn't fire the onload event for the object tag.
	if (isChromeSafari) {
		setTimeout(forcePrint_formLoaded, 3000);
	} else {
		setTimeout(forcePrint_formLoaded, 10000);
	}
}

function forcePrint_removeForms() {
	$("#force-print-form-list").find("li").remove();
}

function forcePrint_getSelectedForms() {
	var selectedForms = new Array();
	$(".ui-selected", "#force-print-form-list").each(function() {
    	var id = this.id;
    	selectedForms.push(id);
    });
	
	return selectedForms;
}

function forcePrint_getSelectedFormsOutputTypes() {
	var outputTypes = new Array();
	$(".ui-selected", "#force-print-form-list").each(function() {
		var formArray = new Array();
    	var outputType = $(this).attr("outputType");
    	var formName = $(this).attr("title");
    	formArray.push(outputType);
    	formArray.push(formName);
    	outputTypes.push(formArray);
    });
	
	return outputTypes;
}
