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
});

function forcePrint_checkForChromeSafari() {
	var isChrome = /Chrome/.test(navigator.userAgent) && /Google Inc/.test(navigator.vendor);
	var isSafari = /Safari/.test(navigator.userAgent) && /Apple Computer/.test(navigator.vendor);
	var isFirefox = /Mozilla/.test(navigator.userAgent);
	return isChrome || isSafari || isFirefox;
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
	var options = [];
	options.push("<option value='selectform'>Please select a form...</option>");
    if (responseXML === null) {
    	$(".force-print-forms-loading").hide();
    	$(".force-print-forms").selectmenu();
    	$(".force-print-forms-container").show();
    } else {
    	$(responseXML).find("forcePrintJIT").each(function () {
        	var formName = $(this).find("displayName").text();
            var formId = $(this).find("formId").text();
            
            options.push("<option value='" + formId + "'>" + formName + "</option>");
        });
    }
    
    $(".force-print-forms").append(options.join("")).selectmenu({
		  select: function( event, ui ) {
			  var formId = $(".force-print-forms").val();
			  if (formId == "selectform") {
				  // A valid form was not selected
			  } else {
				forcePrint_loadForm();
			  }
		  }
		}).selectmenu("menuWidget").css({"max-height":($(window).height() * 0.60) + "px"});
    $(".force-print-forms").css({"max-width":"325px"});

  	$(".force-print-forms-loading").hide();
  	$(".force-print-forms-container").show();
  	$('.force-print-forms').val("selectform").selectmenu("refresh");
  	var divHeight = $(".force-print-forms").parent().parent().parent().height();
  	$(".force-print-forms").selectmenu().selectmenu("menuWidget").css({"max-height":(divHeight * 0.60) + "px"});
}

function forcePrint_loadForm() {
	$(".force-print-form-container").hide();
	$(".force-print-form-loading").show();
	
	var patientId = $("#patientId").val();
	var mrn = $("#mrn").val();
	var sessionId = $("#sessionId").val();
	var locationId = $("#locationId").val();
	var locationTagId = $("#locationTagId").val();
	var formId = $(".force-print-forms").val();
	var randomNumber = Math.floor((Math.random() * 10000) + 1); 
	var action = "";
	if (patientId === "") {
		action = "action=forcePrintForm&mrn=" + mrn + "&sessionId=" + sessionId + "&locationId=" + 
			locationId + "&locationTagId=" + locationTagId + "&formId=" + formId + "&randomNumber=" + randomNumber + 
			"#view=fit&navpanes=0";
	} else {
		action = "action=forcePrintForm&patientId=" + patientId + "&sessionId=" + sessionId + "&locationId=" + 
			locationId + "&locationTagId=" + locationTagId + "&formId=" + formId + "&randomNumber=" + randomNumber + 
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
		setTimeout(forcePrint_formLoaded, 2000);
	}
}

function forcePrint_removeForms() {
	$(".force-print-forms").find("option").remove();
}
