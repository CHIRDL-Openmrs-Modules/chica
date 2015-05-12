$(function() {
	$("#viewPatientButton").button({
		icons : {
			primary : "ui-icon-newwin"
		}
	});
	$("#exitButton").button();
	$(".view-forms").selectmenu({
		select : function(event, ui) {
			var formInstance = ui.item.value;
			if (formInstance == "unselected") {
				// A valid form was not selected
			} else {
				$("#loadingDialog").dialog("open");
				var form = $(this).closest('form');
				form.submit();
			}
		}
	});

	$("#loadingDialog").dialog({
		open : function() {
			$(".ui-dialog").addClass("ui-dialog-shadow");
		},
		autoOpen : false,
		modal : true,
		maxWidth : 100,
		maxHeight : 50,
		width : 100,
		height : 50
	}).dialog("widget").find(".ui-dialog-titlebar").hide();
	
	$("#viewEncountersMRNDialog").dialog({
        open: function() { 
        	$("#encounterMrnLookup").val("");
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
        },
        close: function() { 
            $("#encounterMrnError").hide();
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
        	  id: "viewEncountersMRNOKButton",
	          text:"OK",
	          icons: {
	        	  primary: "ui-icon-newwin"
	          },
	          click: function() {
	            checkEncounterMRN();
	          }
          },
          {
        	  id: "viewEncountersMRNCancelButton",
	          text:"Cancel",
	          click: function() {
	        	  $("#viewEncountersMRNDialog").dialog("close");
	          }
          }
        ]
    });
	
	$("#encounterMrnLookup").keypress(function(e){
	    if (e.which == 13) {
	     e.preventDefault();
	     checkEncounterMRN();    
	    }
	});

	$("#encountersTable").floatThead({
		scrollContainer : function() {
			return $("#middle");
		}
	});
	
	$("#viewPatientButton").click(function() {
    	$("#viewEncountersMRNDialog").dialog("open");
    });
	
	$("#encounterMrnLoading").hide();

	$(window).bind("resize", resizeContent);
	resizeContent();
});

function resizeContent() {
	var windowHeight = $(window).height();
	$("#middle").css("height", windowHeight - 230);
}

function lookupPatient() {
	document.location.href = "viewPatient.form";
	return false;
}

function exitForm() {
	document.location.href = "greaseBoard.form";
	return false;
}

function checkEncounterMRN() {
	$("#encounterMrnError").hide();
	var url = "/openmrs/moduleServlet/chica/chica";
	  $.ajax({
		  beforeSend: function(){
			  $("#viewEncountersMRNOKButton").button("disable");
			  $("#encounterMrnLoading").show();
	      },
	      complete: function(){
	    	  $("#encounterMrnLoading").hide();
	    	  $("#viewEncountersMRNOKButton").button("enable");
	      },
	      "cache": false,
	      "dataType": "xml",
	      "data": "action=verifyMRN&mrn=" + encodeURIComponent($("#encounterMrnLookup").val()),
	      "type": "POST",
	      "url": url,
	      "timeout": 30000, // optional if you want to handle timeouts (which you should)
	      "error": handleVerifyEncounterMRNAjaxError, // this sets up jQuery to give me errors
	      "success": function (xml) {
	          verifyEncounterMRN(xml);
	      }
	  });
}

function handleVerifyEncounterMRNAjaxError(xhr, textStatus, error) {
    var error = "An error occurred on the server.";
    if (textStatus === "timeout") {
        error = "The server took too long to verify the MRN.";
    }
    
    $("#encounterMrnMessage").html("<p>" + error + "  Click OK to try again.</p>");
    $("#encounterMrnError").show("highlight", 750);
}

function verifyEncounterMRN(responseXML) {
    // no matches returned
    if (responseXML === null) {
    	$("#encounterMrnMessage").html("<p><b>Error retrieving MRN information.  Please try again.</b></p>");
        $("#encounterMrnError").show("highlight", 750);
    } else {
    	var result = $(responseXML).find("result").text();
        if (result == "true") {
        	$("#loadingDialog").dialog("open");
        	$("#viewEncountersMRNDialog").dialog("close");
        	document.location.href = "viewEncounter.form?mrn=" + $("#encounterMrnLookup").val();
        } else {
        	$("#encounterMrnMessage").html("<p><b>MRN is not valid.<br>Retype the MRN #. Press OK to display the encounters.</b></p>");
            $("#encounterMrnError").show("highlight", 750);
        }
    }
}