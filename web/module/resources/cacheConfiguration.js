$(function() {
    $( "#cacheTabs" ).tabs();
    $( "#submitButton" ).button();
    $("#submitButton").click(function(event) {
		$("#submitConfirmationDialog").dialog("open");
		event.preventDefault();
	});
    
    $("#errorDialog").dialog({
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
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
    
    var errorMessage = $( "#errorMessage" ).html();
    if (errorMessage != null && errorMessage.length > 0) {
    	$( "#errorDialog" ).dialog("open");
    }
});

