var isChromeSafari = false;
$(document).ready(function () {
	isChromeSafari = checkForChromeSafari();
	$("#formFrame").height($(window).height() - 220);
	$(window).resize(function() {
		// Update the iframe height
		$("#formFrame").height($(window).height() - 220);
	});
	
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
	
	loadForms();
});

function checkForChromeSafari() {
	var isChrome = /Chrome/.test(navigator.userAgent) && /Google Inc/.test(navigator.vendor);
	var isSafari = /Safari/.test(navigator.userAgent) && /Apple Computer/.test(navigator.vendor);
	
	return isChrome || isSafari;
}

function iframeLoaded() {
	$("#frameLoading").hide();
	$("#frameContainer").show();
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
    	options.push("<option value='selectform'>Please select a form...</option>");
    	$(responseXML).find("forcePrintJIT").each(function () {
        	var formName = $(this).find("displayName").text();
            var formId = $(this).find("formId").text();
            
            options.push("<option value='" + formId + "'>" + formName + "</option>");
        });
    	
    	$("select#forms").append(options.join("")).selectmenu({
  		  open: function( event, ui ) {
  			  $("#frameContainer").hide();
  		  },
  		  select: function( event, ui ) {
  			  var formId = $("#forms").val();
  			  if (formId == "selectform") {
  				  // A valid form was not selected
  			  } else {
  				loadForm();
  			  }
  		  }
		}).selectmenu("menuWidget").addClass("overflow");
    	$( ".selector" ).selectmenu();


    	$("#formsLoading").hide();
    	$("#formsContainer").show();
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
	
	//$('#formFrame').attr("data", url + action);
	var obj       = $('object:first');
	var objdata   = $(obj).attr('data');
	var container = $(obj).parent();
	$(obj).attr('data', url + action);
	var newobj    = $(obj).clone();
	newobj.on("load", function () {
		$("#frameLoading").hide();
		$("#frameContainer").show();
    });
	$(obj).remove();
	$(container).append( newobj );
	
	// Chrome/Safari doesn't fire the onload event for the object tag.
	if (isChromeSafari) {
		setTimeout(iframeLoaded, 1000);
	}
}
