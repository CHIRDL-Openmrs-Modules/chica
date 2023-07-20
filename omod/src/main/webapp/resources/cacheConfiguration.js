var chicaServletUrl = ctx + "/moduleServlet/chica/chica?";


$j(function() {
    $j( "#cacheTabs" ).tabs();
    $j( "#submitButton, #clearEHRMedicalRecordCacheButton, #clearFormDraftCacheButton, #clearFormDraftButton" ).button();
    $j("#submitButton").click(function(event) {
		$j("#submitConfirmationDialog").dialog("open");
		event.preventDefault();
	});
    
    $j("#clearEHRMedicalRecordCacheButton").click(function(event) {
		$j("#clearEHRMedicalRecordCacheConfirmationDialog").dialog("open");
		event.preventDefault();
	});
    
    $j("#clearFormDraftCacheButton").click(function(event) {
		$j("#clearFormDraftCacheConfirmationDialog").dialog("open");
		event.preventDefault();
	});
    
    $j("#clearFormDraftButton").click(function(event) {
		clearForm();
		event.preventDefault();
	});
    
    $j("#errorDialog").dialog({
        open: function() { 
            $j(".ui-dialog").addClass("ui-dialog-shadow"); 
            $j(".ui-dialog").addClass("no-close");
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
	        	  $j(this).dialog("close");
	          }
          }
        ]
    });
    
    $j("#submitConfirmationDialog").dialog({
        open: function() { 
            $j(".ui-dialog").addClass("ui-dialog-shadow"); 
            $j(".ui-dialog").addClass("no-close");
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
	        	  $j("#cacheForm").submit();
	          }
          },
          {
	          text:"No",
	          click: function() {
	        	  $j(this).dialog("close");
	          }
          }
        ]
    });
    
    $j("#clearEHRMedicalRecordCacheConfirmationDialog").dialog({
        open: function() { 
            $j(".ui-dialog").addClass("ui-dialog-shadow"); 
            $j(".ui-dialog").addClass("no-close");
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
	        	  $j(this).dialog("close");
	        	  clearEHRMedicalRecordCache();
	          }
          },
          {
	          text:"No",
	          click: function() {
	        	  $j(this).dialog("close");
	          }
          }
        ]
    });
    
    $j("#clearFormDraftCacheConfirmationDialog").dialog({
        open: function() { 
            $j(".ui-dialog").addClass("ui-dialog-shadow"); 
            $j(".ui-dialog").addClass("no-close");
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
	        	  $j(this).dialog("close");
	        	  clearFormDraftCache();
	          }
          },
          {
	          text:"No",
	          click: function() {
	        	  $j(this).dialog("close");
	          }
          }
        ]
    });
    
    $j("#clearCacheCompleteDialog").dialog({
        open: function() { 
            $j(".ui-dialog").addClass("ui-dialog-shadow"); 
            $j(".ui-dialog").addClass("no-close");
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
	        	  $j(this).dialog("close");
	          }
          }
        ]
    });
    
    var errorMessage = $j( "#errorMessage" ).html();
    if (errorMessage != null && errorMessage.length > 0) {
    	$j( "#errorDialog" ).dialog("open");
    }
});

function clearEHRMedicalRecordCache() {
	clearCache("ehrMedicalRecord", "java.lang.Integer", "java.util.HashMap");
}

function clearCache(cacheName, keyType, valueType) {
	var action = "action=clearCache&cacheName=" + cacheName + "&cacheKeyType=" + keyType + "&cacheValueType=" + valueType;
	$j.ajax({
	  beforeSend: function(){
		  $j("#formServerError").hide();
		  $j("#formLoading").show();
      },
      complete: function(){
    	  $j("#formLoading").hide();
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
	$j( "#cacheMessage" ).html("An error occurred clearing the cache:\n" + error);
    $j( "#clearCacheCompleteDialog" ).dialog("open");
}

function clearCacheComplete(text) {
	if (text === "success") {
		$j( "#cacheMessage" ).html("Clearing the cache was successful");
	} else {
		$j( "#cacheMessage" ).html("An error occurred clearing the cache.  Please check the logs for more information.");
	}
	
	$j( "#clearCacheCompleteDialog" ).dialog("open");
}

function clearForm() {
	var formId = $j("#formCacheFormId").val();
	if (isNaN(parseInt(formId)) || !isInteger(formId)) {
		$j( "#cacheMessage" ).html("Form ID must be an integer");
	    $j( "#clearCacheCompleteDialog" ).dialog("open");
		return false;
	}
	
	var formInstanceId = $j("#formCacheFormInstanceId").val();
	if (isNaN(parseInt(formInstanceId)) || !isInteger(formInstanceId)) {
		$j( "#cacheMessage" ).html("Form Instance ID must be an integer");
	    $j( "#clearCacheCompleteDialog" ).dialog("open");
		return false;
	}
	
	var locationId = $j("#formCacheLocationId").val();
	if (isNaN(parseInt(locationId)) || !isInteger(locationId)) {
		$j( "#cacheMessage" ).html("Location ID must be an integer");
	    $j( "#clearCacheCompleteDialog" ).dialog("open");
		return false;
	}
	
	var locationTagId = $j("#formCacheLocationTagId").val();
	if (isNaN(parseInt(locationTagId)) || !isInteger(locationTagId)) {
		$j( "#cacheMessage" ).html("Location Tag ID must be an integer");
	    $j( "#clearCacheCompleteDialog" ).dialog("open");
		return false;
	}
	
	var action = "action=clearFormInstanceFromFormCache&formInstance=" + locationId + "_" + locationTagId + "_" + formId + "_" + formInstanceId;
	$j.ajax({
	  beforeSend: function(){
		  $j("#formServerError").hide();
		  $j("#formLoading").show();
      },
      complete: function(){
    	  $j("#formLoading").hide();
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
	$j( "#cacheMessage" ).html("An error occurred clearing the form from the cache:\n" + error);
    $j( "#clearCacheCompleteDialog" ).dialog("open");
}

function clearFormFromCacheComplete(text) {
	if (text === "true") {
		$j( "#cacheMessage" ).html("The form draft was successfully cleared from the cache.");
	} else if (text === "false") {
		$j( "#cacheMessage" ).html("The cache does not contain a draft for the specified form.");
	} else {
		$j( "#cacheMessage" ).html("An error occurred clearing the form from the cache.  Please check the logs for more information.");
	}
	
	$j( "#clearCacheCompleteDialog" ).dialog("open");
}

function isInteger(x) {
    return x % 1 === 0;
}