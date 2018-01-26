var isChromeSafari = false;
var hasUpdatedForcePrintDimensions = false;
var type = "application/pdf";
$(function() { 
	$(".force-print-no-forms").hide();
	isChromeSafari = forcePrint_checkForChromeSafari();

	$(".force-print-retry-button").button();
	$(".force-print-retry-button").click(function(event) {
		forcePrint_loadForms();
		event.preventDefault();
	});
	
    $("#force-print-create-forms-button").button();
    $("#force-print-create-forms-button").click(function() {
    	var selectedForms = forcePrint_getSelectedForms();
    	if (selectedForms.length === 0) {
    		$("#force-print-no-force-prints-dialog").dialog("open");
    	} else {
    		var pdfCount = 0;
    		var teleformCount = 0;
    		var index;
    		var teleformForms = "";
    		var outputTypes = forcePrint_getSelectedFormsOutputTypes();
    		for (index = 0; index < outputTypes.length; index++) {
    			var outputType = outputTypes[index][0];
    			outputType = outputType.toLowerCase();
    			var pdfPos = outputType.indexOf("pdf");
    			var teleformPos = outputType.indexOf("teleformxml");
    			if (pdfPos >= 0) {
    				pdfCount++;
    			}
    			
    			if (teleformPos >= 0) {
    				teleformCount++;
    				if (teleformForms.length > 0) {
    					teleformForms += ", ";
    				}
    				
    				teleformForms += outputTypes[index][1];
    			}
    		}
    		
    		if (teleformCount > 0) { // CHICA-962 Display this message even if the only form selected is a teleform file
    			$(".force-print-form-container").hide();
    			var message = "<p>The following " + (teleformCount > 1 ? 'forms' : 'form') + " will be automatically sent to the printer and will not be displayed here: " + 
    				teleformForms + "</p>";
    			$("#force-print-multiple-output-types-result-div").html(message);
    			$("#force-print-multiple-output-types-dialog").dialog("open");
    		} else {
    			forcePrint_loadForm();
    		}
    	}
    });
    
    $("#force-print-dialog").dialog({
        open: function() { 
        	$(".force-print-form-container").hide();
            forcePrint_removeForms();
            forcePrint_loadForms();
            $(".ui-dialog").addClass("ui-dialog-shadow");
            $(".force-print-form-list-container").scrollTop(0);
        },
        beforeClose: function(event, ui) { 
        	// Have to do this nonsense to prevent Chrome and Firefox from sending an additional request  to the server for a PDF when the dialog is closed.
        	$(".force-print-form-container").hide();
        	var obj = $(".force-print-form-object");
        	var container = obj.parent();
        	var newobj = obj.clone();
        	obj.remove();
        	
        	// CHICA-948 Remove data and type attributes so IE doesn't cause an authentication error when loading the page.
			newobj.removeAttr("data");
			newobj.removeAttr("type");
        	container.append(newobj);
        },
        close: function(event, ui) { 
        	event.preventDefault();
            $(".force-print-form-container").hide();
        },
        autoOpen: false,
        modal: true,
        minHeight: 350,
        minWidth: 950,
        width: 950,
        height: $(window).height() * 0.90,
        show: {
          effect: "fade",
          duration: 500
        },
        hide: {
          effect: "fade",
          duration: 500
        },
        resizable: false,
        buttons: [
          {
	          text:"Close",
	          click: function() {
	        	  $("#force-print-dialog").dialog("close");
	          }
          }
        ]
    });
    
    $("#force-print-no-force-prints-dialog").dialog({
        resizable: false,
        modal: true,
        autoOpen: false,
        open: function() { 
        	$(".force-print-form-container").hide();
            $(".ui-dialog").addClass("ui-dialog-shadow");
            $("#force-print-form-list").selectable("disable");
          },
        close: function() { 
        	$("#force-print-form-list").selectable("enable");
          },
        show: {
            effect: "fade",
            duration: 500
          },
          hide: {
            effect: "fade",
            duration: 500
          },
        buttons: {
          "Close": function() {
        	$("#force-print-form-list").selectable("enable");
            $(this).dialog("close");
          }
        }
    });
    
    $("#force-print-multiple-output-types-dialog").dialog({
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
            $("#force-print-form-list").selectable("disable");
        },
        close: function() { 
        	$("#force-print-form-list").selectable("enable");
        	forcePrint_loadForm();
        },
        autoOpen: false,
        modal: true,
        resizable: false,
        show: {
          effect: "fade",
          duration: 500
        },
        hide: {
          effect: "fade",
          duration: 500
        },
        buttons: [
          {
	          text:"OK",
	          click: function() {
	        	  $("#force-print-form-list").selectable("enable");
	        	  $(this).dialog("close");
	          }
          }
        ]
    });
    
    $("#force-print-error-dialog").dialog({
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
            $(".force-print-form-container").hide();
            $("#force-print-form-list").selectable("disable");
        },
        close: function() { 
        	$(".force-print-form-container").show();
        	$("#force-print-form-list").selectable("enable");
        	forcePrint_loadForm();
        },
        autoOpen: false,
        modal: true,
        resizable: false,
        show: {
          effect: "fade",
          duration: 500
        },
        hide: {
          effect: "fade",
          duration: 500
        },
        buttons: [
          {
	          text:"OK",
	          click: function() {
	        	  $(".force-print-form-container").show();
	        	  $("#force-print-form-list").selectable("enable");
	        	  $(this).dialog("close");
	          }
          }
        ]
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
	if ($("#force-print-multiple-output-types-dialog").hasClass("ui-dialog-content") && $("#force-print-no-force-prints-dialog").hasClass("ui-dialog-content") && 
			$("#force-print-multiple-output-types-dialog").dialog("isOpen") === false && $("#force-print-no-force-prints-dialog").dialog("isOpen") === false) {
		var obj = $(".force-print-form-object");
		if (obj != null) {
			var url = obj.attr("data");
			if (url != null && url.length > 0) {
				$(".force-print-form-loading").hide();
				$(".force-print-form-container").show();
			}
		}
	}
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
	var url = ctx + "/moduleServlet/chica/chica";
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
	$(".force-print-form-loading").hide();
	$(".force-print-button-panel").hide();
	$(".force-print-forms-server-error-text").html('<span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span><span>Error loading forms: ' + error + '</span>');
	$(".force-print-forms-server-error").show();
}

function forcePrint_parseAvailableForms(responseXML) {
	var foundForms = false;
	// no matches returned
    if (responseXML === null) {
    	$(".force-print-forms-loading").hide();
    	$("#force-print-form-list").selectable();
    	$(".force-print-forms-container").hide();
    	$(".force-print-form-container").hide();
    	$(".force-print-no-forms").show();
    } else {
		$('.force-print-accordion').remove();
		$('.force-print-panel').remove();
		$('.force-print-divider').remove();
		$("#force-print-form-list").empty();
		$(responseXML).find("group").each(function () {
			foundForms = true;
			var groupName = $(this).attr('name');
			if (groupName != null) {
				var groupNameNoSpaces = groupName.replace(/\s/g,''); // CHICA-1122 group name is used as the ID which CANNOT contain spaces, see ticket for details
				$('<button class="force-print-accordion">' + groupName + '</button><div id="' + groupNameNoSpaces + '" class="force-print-panel"></div><div class="force-print-divider"></div>').appendTo($('#force-print-form-list')); 
				$(responseXML).find('group[name="'+groupName+'"]').children().each(function(){
					$('.force-print-panel:last').append('<li id="' + $(this).find("formId").text() + '" title="' + $(this).find("displayName").text() + '" outputType="' + $(this).find("outputType").text() + '" class="selectList ui-widget-content">' + $(this).find("displayName").text() + '</li>');
				});
				$("#"+groupNameNoSpaces+"").hide();
			} else {
				$('<li class="selectList" id="' + $(this).find("formId").text() + '" title="' + $(this).find("displayName").text() + '" outputType="' + $(this).find("outputType").text() + '">' + $(this).find("displayName").text() + '</li>').addClass('ui-widget-content').appendTo($('#force-print-form-list'));
				
			}
		});
		togglePrintJITs();
    }
   
    $(".force-print-form-list").css({"max-width":"325px"});

  	$(".force-print-forms-loading").hide();
  	if (foundForms) {
  		$(".force-print-forms-container").show();
  	} else {
  		$(".force-print-forms-container").hide();
  		$(".force-print-no-forms").show();
  	}

  	$("#force-print-form-list").selectable({
	  filter: "LI"
	});
	var prevChecked = null;
	$('.selectList').click(function(e) {
		if(!prevChecked) {
			prevChecked = this;
			return;
		}
		if(e.shiftKey) {
			var startIndex = $('.selectList').index(this);
			var endIndex = $('.selectList').index(prevChecked);
			$('.selectList').slice(Math.min(startIndex,endIndex), 1 + Math.max(startIndex,endIndex)).addClass('ui-selected');
		}
		prevChecked = this;
	});

  	if (!hasUpdatedForcePrintDimensions) {
  		updateForcePrintDimensions();
  	}
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
			locationId + "&locationTagId=" + locationTagId + "&formIds=" + formIds + "&randomNumber=" + randomNumber;
	} else {
		action = "action=forcePrintForms&patientId=" + patientId + "&sessionId=" + sessionId + "&locationId=" + 
			locationId + "&locationTagId=" + locationTagId + "&formIds=" + formIds + "&randomNumber=" + randomNumber;
	}
	
	var url = ctx + "/moduleServlet/chica/chica?";
	$.ajax({
		  "cache": false,
		  "dataType": "xml",
		  "data": action,
		  "type": "POST",
		  "url": url,
		  "timeout": 90000, // optional if you want to handle timeouts (which you should)
		  "error": forcePrint_handleGetAvailableFormsError, // this sets up jQuery to give me errors
		  "success": function (xml) {
			  forcePrint_parseForcePrintedForms(xml);
	      }
		});
}

function forcePrint_parseForcePrintedForms(responseXML) {
	var foundForms = false;
	// no matches returned
    if (responseXML === null) {
    	$(".force-print-forms-loading").hide();
    	$("#force-print-form-list").selectable();
    	$(".force-print-forms-container").hide();
    	$(".force-print-form-container").hide();
    	$(".force-print-no-forms").show();
    } else {
    	var forcePrintedForms = new Array();
    	$(responseXML).find("forcePrintJIT").each(function () {
    		foundForms = true;
        	var formInstanceTag = $(this).find("formInstanceTag").text();
            var outputType = $(this).find("outputType").text();
            forcePrintedForms.push(formInstanceTag);
        });
    	
    	var error = "";
    	$(responseXML).find("errorMessage").each(function () {
    		error += "<br/>" + $(this).text();
        });
    	
    	if (error.length > 0) {
    		$(".force-print-form-container").hide();
			var message = "<p>The following error(s) occurred while creating the selected form(s): ";
			$("#force-print-error-result-div").html(message + error);
			$("#force-print-error-dialog").dialog("open");
    	}
    	
    	if (forcePrintedForms.length > 0) {
    		var patientId = $("#patientId").val();
    		var mrn = $("#mrn").val();
    		var sessionId = $("#sessionId").val();
    		var randomNumber = Math.floor((Math.random() * 10000) + 1);
    		if (patientId === "") {
    			action = "action=displayForcePrintForms&mrn=" + mrn + "&sessionId=" + sessionId + "&formInstances=" + 
    				forcePrintedForms.toString() + "&randomNumber=" + randomNumber +  "#view=fit&navpanes=0";
    		} else {
    			action = "action=displayForcePrintForms&patientId=" + patientId + "&sessionId=" + sessionId + "&formInstances=" + 
    				forcePrintedForms.toString() + "&randomNumber=" + randomNumber +  "#view=fit&navpanes=0";
    		}
    		
    		var url = ctx + "/moduleServlet/chica/chica?";
    		var obj = $(".force-print-form-object");
    		var container = obj.parent();
    		var newUrl = url + action;
    		var newobj = obj.clone();
    		obj.remove();
    		newobj.attr("data", newUrl);
    		newobj.attr("type", type); // CHICA-948 Set the type since it was removed in the close event
    		newobj.on("load", function () {
    			$(".force-print-form-loading").hide();
    			$(".force-print-form-container").show();
    	    });
    		
    		container.append(newobj);
    		
    		// Chrome/Safari doesn't fire the onload event for the object tag.
    		if (isChromeSafari) {
    			setTimeout(forcePrint_formLoaded, 3000);
    		} else {
    			setTimeout(forcePrint_formLoaded, 5000);
    		}
    	}
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

function updateForcePrintDimensions() {
	var divHeight = $(".force-print-content").height();
	var instructHeight = $(".force-print-multiple-select").height();
	var nameHeight = $(".force-print-patient-name").height();
	var listHeight = $("#force-print-form-list").height();
	var buttonHeight = $(".force-print-create-button-panel").height();
	var newDivHeight = divHeight - nameHeight - instructHeight - buttonHeight - 30;
	
    // Update the height of the select
	$(".force-print-form-list-container").css({"height":(newDivHeight) + "px"});
    $("#force-print-form-list").selectable().css({"height":(newDivHeight - 10) + "px"});
    $("force-print-forms-container").css({"height":"100%"});
    divHeight = $(".force-print-forms-container").height();
    $(".force-print-create-button-panel").css({"height":(divHeight - instructHeight - nameHeight - newDivHeight - 30) + "px"});
    hasUpdatedForcePrintDimensions = true;
}



function togglePrintJITs() {
	var acc = document.getElementsByClassName("force-print-accordion");
	var i;
	for (i = 0; i < acc.length; i++) {
	  acc[i].onclick = function() {
		this.classList.toggle("active");
		var panel = this.nextElementSibling; 
		var id = panel.id;
		if (panel.style.maxHeight){
		  panel.style.maxHeight = null;
		  $("#"+id+"").hide();
		} else {
		  $("#"+id+"").show();
		  panel.style.maxHeight = panel.scrollHeight -3 + "px";
		} 
	  }
	}
}

