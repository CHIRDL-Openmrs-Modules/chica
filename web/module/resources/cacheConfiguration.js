var chicaServletUrl = "/openmrs/moduleServlet/chica/chica?";
$(function() {
    $( "#cacheTabs" ).tabs();
    $( "#submitButton" ).button();
    $("#submitButton").click(function(event) {
		$("#submitConfirmationDialog").dialog("open");
		event.preventDefault();
	});
    
    $( "#clearEHRMedicalRecordCacheButton" ).button();
    $("#clearEHRMedicalRecordCacheButton").click(function(event) {
		$("#clearEHRMedicalRecordCacheConfirmationDialog").dialog("open");
		event.preventDefault();
	});
    
    $( "#clearImmunizationCacheButton" ).button();
    $("#clearImmunizationCacheButton").click(function(event) {
		$("#clearImmunizationCacheConfirmationDialog").dialog("open");
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
    
    $("#clearImmunizationCacheConfirmationDialog").dialog({
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
	        	  clearImmunizationCache();
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

function clearImmunizationCache() {
	clearCache("immunization", "java.lang.Integer", "org.openmrs.module.chica.ImmunizationQueryOutput");
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