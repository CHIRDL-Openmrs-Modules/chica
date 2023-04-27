var chicaServletUrl = ctx + "/moduleServlet/chica/chica?";

var $ = jQuery.noConflict();

$(function() {
    $( "#cacheTabs" ).tabs();
    $( "#submitButton, #clearEHRMedicalRecordCacheButton, #clearFormDraftCacheButton, #clearFormDraftButton" ).button();
    $("#submitButton").click(function(event) {
		$("#submitConfirmationDialog").dialog("open");
		event.preventDefault();
	});
    
    $("#clearEHRMedicalRecordCacheButton").click(function(event) {
		$("#clearEHRMedicalRecordCacheConfirmationDialog").dialog("open");
		event.preventDefault();
	});
    
    $("#clearFormDraftCacheButton").click(function(event) {
		$("#clearFormDraftCacheConfirmationDialog").dialog("open");
		event.preventDefault();
	});
    
    $("#clearFormDraftButton").click(function(event) {
		clearForm();
		event.preventDefault();
	});
    
    $("#errorDialog").dialog({
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
            $(".ui-dialog").addClass("no-close");
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
	        	  $(this).dialog("close");
	          }
          }
        ]
    });
    
    $("#submitConfirmationDialog").dialog({
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
            $(".ui-dialog").addClass("no-close");
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
	          text:"Yes",
	          click: function() {
	        	  $("#cacheForm").submit();
	          }
          },
          {
	          text:"No",
	          click: function() {
	        	  $(this).dialog("close");
	          }
          }
        ]
    });
    
    $("#clearEHRMedicalRecordCacheConfirmationDialog").dialog({
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
            $(".ui-dialog").addClass("no-close");
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
	          text:"Yes",
	          click: function() {
	        	  $(this).dialog("close");
	        	  clearEHRMedicalRecordCache();
	          }
          },
          {
	          text:"No",
	          click: function() {
	        	  $(this).dialog("close");
	          }
          }
        ]
    });
    
    $("#clearFormDraftCacheConfirmationDialog").dialog({
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
            $(".ui-dialog").addClass("no-close");
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
	          text:"Yes",
	          click: function() {
	        	  $(this).dialog("close");
	        	  clearFormDraftCache();
	          }
          },
          {
	          text:"No",
	          click: function() {
	        	  $(this).dialog("close");
	          }
          }
        ]
    });
    
    $("#clearCacheCompleteDialog").dialog({
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
            $(".ui-dialog").addClass("no-close");
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
	        	  $(this).dialog("close");
	          }
          }
        ]
    });
    
    var errorMessage = $( "#errorMessage" ).html();
    if (errorMessage != null && errorMessage.length > 0) {
    	$( "#errorDialog" ).dialog("open");
    }
});

function clearEHRMedicalRecordCache() {
	clearCache("ehrMedicalRecord", "java.lang.Integer", "java.util.HashMap");
}

function clearCache(cacheName, keyType, valueType) {
	var action = "action=clearCache&cacheName=" + cacheName + "&cacheKeyType=" + keyType + "&cacheValueType=" + valueType;
	$.ajax({
	  beforeSend: function(){
		  $("#formServerError").hide();
		  $("#formLoading").show();
      },
      complete: function(){
    	  $("#formLoading").hide();
      },
	  "cache": false,
	  "dataType": "text",
	  "data": action,
	  "type": "POST",
	  "url": chicaServletUrl,
	  "timeout": 30000, // optional if you want to handle timeouts (which you should)
	  "error": handleClearCacheError, // this sets up jQuery to give me errors
	  "success": function (text) {
		  clearCacheComplete(text);
      }
	});
}

function handleClearCacheError(xhr, textStatus, error) {
	$( "#cacheMessage" ).html("An error occurred clearing the cache:\n" + error);
    $( "#clearCacheCompleteDialog" ).dialog("open");
}

function clearCacheComplete(text) {
	if (text === "success") {
		$( "#cacheMessage" ).html("Clearing the cache was successful");
	} else {
		$( "#cacheMessage" ).html("An error occurred clearing the cache.  Please check the logs for more information.");
	}
	
	$( "#clearCacheCompleteDialog" ).dialog("open");
}

function clearForm() {
	var formId = $("#formCacheFormId").val();
	if (isNaN(parseInt(formId)) || !isInteger(formId)) {
		$( "#cacheMessage" ).html("Form ID must be an integer");
	    $( "#clearCacheCompleteDialog" ).dialog("open");
		return false;
	}
	
	var formInstanceId = $("#formCacheFormInstanceId").val();
	if (isNaN(parseInt(formInstanceId)) || !isInteger(formInstanceId)) {
		$( "#cacheMessage" ).html("Form Instance ID must be an integer");
	    $( "#clearCacheCompleteDialog" ).dialog("open");
		return false;
	}
	
	var locationId = $("#formCacheLocationId").val();
	if (isNaN(parseInt(locationId)) || !isInteger(locationId)) {
		$( "#cacheMessage" ).html("Location ID must be an integer");
	    $( "#clearCacheCompleteDialog" ).dialog("open");
		return false;
	}
	
	var locationTagId = $("#formCacheLocationTagId").val();
	if (isNaN(parseInt(locationTagId)) || !isInteger(locationTagId)) {
		$( "#cacheMessage" ).html("Location Tag ID must be an integer");
	    $( "#clearCacheCompleteDialog" ).dialog("open");
		return false;
	}
	
	var action = "action=clearFormInstanceFromFormCache&formInstance=" + locationId + "_" + locationTagId + "_" + formId + "_" + formInstanceId;
	$.ajax({
	  beforeSend: function(){
		  $("#formServerError").hide();
		  $("#formLoading").show();
      },
      complete: function(){
    	  $("#formLoading").hide();
      },
	  "cache": false,
	  "dataType": "text",
	  "data": action,
	  "type": "POST",
	  "url": chicaServletUrl,
	  "timeout": 30000, // optional if you want to handle timeouts (which you should)
	  "error": handleClearFormFromCacheError, // this sets up jQuery to give me errors
	  "success": function (text) {
		  clearFormFromCacheComplete(text);
      }
	});
}

function handleClearFormFromCacheError(xhr, textStatus, error) {
	$( "#cacheMessage" ).html("An error occurred clearing the form from the cache:\n" + error);
    $( "#clearCacheCompleteDialog" ).dialog("open");
}

function clearFormFromCacheComplete(text) {
	if (text === "true") {
		$( "#cacheMessage" ).html("The form draft was successfully cleared from the cache.");
	} else if (text === "false") {
		$( "#cacheMessage" ).html("The cache does not contain a draft for the specified form.");
	} else {
		$( "#cacheMessage" ).html("An error occurred clearing the form from the cache.  Please check the logs for more information.");
	}
	
	$( "#clearCacheCompleteDialog" ).dialog("open");
}

function isInteger(x) {
    return x % 1 === 0;
}