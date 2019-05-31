var chicaServletUrl = ctx + "/moduleServlet/chica/chica?";
var timeoutDialog = null;
var keepAliveURL = ctx + "/moduleServlet/chica/chica?action=keepAlive";
var saveDraftURL = chicaServletUrl + "action=saveFormDraft";
var type = "application/pdf";

function getSelected(opt) {
	var selected = new Array();
	var index = 0;
	for (var intLoop = 0; intLoop < opt.length; intLoop++) {
		if ((opt[intLoop].selected) || (opt[intLoop].checked)) {
			index = selected.length;
			selected[index] = new Object;
			selected[index].value = opt[intLoop].value;
			selected[index].index = intLoop;
		}
	}
	return selected;
}

function outputSelected(opt, hidden) {
	var sel = getSelected(opt);
	var strSel = "";
	for ( var item in sel)
		strSel += sel[item].value;

	hidden.val(strSel);
}

function processCheckboxes(form1) {
	outputSelected($("input[name='sub_Choice1']"), $("#Choice1"));
	outputSelected($("input[name='sub_Choice2']"), $("#Choice2"));
	outputSelected($("input[name='sub_Choice3']"), $("#Choice3"));
	outputSelected($("input[name='sub_Choice4']"), $("#Choice4"));
	outputSelected($("input[name='sub_Choice5']"), $("#Choice5"));
	outputSelected($("input[name='sub_Choice6']"), $("#Choice6"));
}

$(function() {
	$("button, input:submit, input:button").button();
	$("#submitButtonTop, #submitButtonBottom, #saveDraftButtonTop, #saveDraftButtonBottom").button();
	$("#notesButton, #formPrintButton, #problemButton, #forcePrintButton").button();
	
    $("#problemDialog").dialog({
      open: function() { $(".ui-dialog").addClass("ui-dialog-shadow"); },
      autoOpen: false,
      modal: true,
      show: {
        effect: "fade",
        duration: 500
      },
      hide: {
        effect: "fade",
        duration: 500
      }
    })

    $("#problemButton").click(function(event) {
      $("#problemDialog").dialog("open");
      event.preventDefault();
    });
    
    $("#medDialog").dialog({
    	open: function() { $(".ui-dialog").addClass("ui-dialog-shadow"); },
        autoOpen: false,
        modal: true,
        show: {
          effect: "fade",
          duration: 500
        },
        hide: {
          effect: "fade",
          duration: 500
        }
    })

	$("#medButton").click(function(event) {
	  $("#medDialog").dialog("open");
	  event.preventDefault();
	});
	
	$("#formPrintButton").click(function(event) {
		$("#recommended-handouts-form-selection-dialog").dialog("open");
		event.preventDefault();
	});
	
	$("#notesDialog").dialog({
    	open: function() { $(".ui-dialog").addClass("ui-dialog-shadow"); },
        autoOpen: false,
        minHeight: 350,
        minWidth: 450,
        width: 950,
        height: $(window).height() * 0.85,
        modal: true,
        resizable: false,
        open : function(){
        	$("#notesTabs").tabs({
        		overflowTabs: true,
        		heightStyle: "content",
        		tabPadding: 23,
        		containerPadding: 40
        	}).addClass("ui-tabs-vertical ui-helper-clearfix");
        	$("#notesTabs li").removeClass("ui-corner-top").addClass( "ui-corner-left");
        },
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
        	 text:"Close",
        	 click: function() {
        		 validateTextNotes();     	       
        	 }
           }
        ]
    });
	
//	updateCount("historyAndPhysicalText");
//	updateCount("assessmentAndPlanText");
	$("#historyAndPhysicalText").keyup(function() {
		updateCount('historyAndPhysicalText');
	});
	 
	$("#assessmentAndPlanText").keyup(function() {
		updateCount('assessmentAndPlanText');
	    	
	});
	
	// Append the notes dialog to the parent form so that it can be submitted
	$('#notesDialog').parent().appendTo($("form:first"));
	
	$("#notesButton").click(function(event) {
		$("#notesDialog").dialog("open");
		event.preventDefault();
	});
	
	$("#confirmSubmitDialog").dialog({
	  open: function() { $(".ui-dialog").addClass("ui-dialog-shadow"); },
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
	        	  $("#confirmSubmitDialog").dialog("close");
	      		  $("#submitWaitDialog").dialog("open");
	      		  processCheckboxes();
	      		  $("#pwsForm").submit();
	          }
          },
	      {   text:"Cancel",
	          click: function() {
	        	  $("#confirmSubmitDialog").dialog("close");
	          }
          }
        ]
    })
	
	$("#submitButtonBottom, #submitButtonTop").click(function(event) {
		$("#confirmSubmitDialog").dialog("open");
		event.preventDefault();
	});
	
	$("#saveDraftButtonBottom, #saveDraftButtonTop").click(function(event) {
		saveDraft(this.id);
		event.preventDefault();
	});
	
	$(":checkbox").change(function(event) {
		saveDraft();
		event.preventDefault();
	});
	
	$("#submitWaitDialog").dialog({
		open: function() { $(".ui-dialog").addClass("ui-dialog-shadow"); },
        autoOpen: false,
        modal: true,
        maxWidth: 100,
        maxHeight: 50,
        width: 100,
        height: 50,
        show: {
            effect: "fade",
            duration: 500
          },
          hide: {
            effect: "fade",
            duration: 500
          }
    }).dialog("widget").find(".ui-dialog-titlebar").hide();
	
	$("#saveDraftWaitDialog").dialog({
		open: function() { $(".ui-dialog").addClass("ui-dialog-shadow"); },
        autoOpen: false,
        modal: true,
        maxWidth: 100,
        maxHeight: 50,
        width: 100,
        height: 50,
        show: {
            effect: "fade",
            duration: 500
          },
          hide: {
            effect: "fade",
            duration: 500
          }
    }).dialog("widget").find(".ui-dialog-titlebar").hide();
	
	$("#saveDraftErrorDialog").dialog({
        resizable: false,
        modal: true,
        autoOpen: false,
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
            $(this).dialog("close");
          }
        }
    });
	
	$("#saveDraftSuccessDialog").dialog({
        resizable: false,
        modal: true,
        autoOpen: false,
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
            $(this).dialog("close");
          }
        }
    });
	
	$("#serverErrorDialog").dialog({
        resizable: false,
        modal: true,
        autoOpen: false,
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
            $(this).dialog("close");
          }
        }
    });
	
	if ($("#serverErrorMessage").html() != null && $("#serverErrorMessage").html().length > 0) {
		$("#serverErrorDialog").dialog("open");
	}
	
	$("#forcePrintButton").click(function(event) {
		$("#force-print-dialog").dialog("open");
		event.preventDefault();
	});
	
	$.fn.uncheckableRadio = function() {

        return this.each(function() {
            $(this).mousedown(function() {
                $(this).data("wasChecked", this.checked);
            });

            $(this).click(function() {
                if ($(this).data("wasChecked"))
                    this.checked = false;
				saveDraft();
            });
        });

    };
    
	$(".uncheckableRadioButton").uncheckableRadio();
	var patientName = $("#patientNameForcePrint").val();
	$(".force-print-patient-name").html("<p>Please choose form(s) for " + patientName + ".</p>");
    
    // Leave this at the very end of the function
    $(document).ajaxStart(function() {
    	restartSessionCounter(false);
	});
    
    if (timeoutDialog === null) {
    	$.timeoutDialog({timeout: $("#sessionTimeout").val(), countdown: $("#sessionTimeoutWarning").val(), logout_url: ctx + '/logout', logout_redirect_url: ctx + '/module/chica/sessionTimeout.form', 
    		keep_alive_url: keepAliveURL, dialog_width: '400', title: 'Your CHICA session is about to expire'});
    	timeoutDialog = getTimeoutDialog();
    	
    	$(document).on("dialogopen", "#timeout-dialog", function() {
    		$("object").hide();
    	});
    	
    	$(document).on("dialogclose", "#timeout-dialog", function() {
    		$("object").show();
    	});
    }
	$("#patient_header a").click(function(event) {
		if ($("#patient_name").is(":visible")) {
			$("#patient_header div").removeClass("ui-icon ui-icon-minusthick white");
			$("#patient_header div").addClass("ui-icon ui-icon-plusthick white");
    		$("#patient_name").slideUp("slow", function() {
				if (!$("#handouts").is(":visible")) {
					$("#patient_container").addClass("patient_container_collapse");
					$("#handouts_container").addClass("patient_container_collapse");
					$("#patient_container").animate({
						height: '30px'
					}, 500);
					$("#handouts_container").animate({
						height: '30px'
					}, 500);
				}
				$("#patient_header").addClass("round_corners");
  			});
		} else {
			$("#patient_header div").removeClass("ui-icon ui-icon-plusthick white");
			$("#patient_header div").addClass("ui-icon ui-icon-minusthick white");
			$("#patient_name").slideDown("slow");
			$("#patient_header").removeClass("round_corners");
			$("#patient_container").removeClass("patient_container_collapse");
			$("#handouts_container").removeClass("patient_container_collapse");
			$("#patient_container").animate({
						height: '165px'
					}, 500);
					$("#handouts_container").animate({
						height: '165px'
					}, 500);
		}
    	event.preventDefault();
	});
	
	$("#handouts_header a").click(function(event) {
		if ($("#handouts").is(":visible")) {
			$("#handouts_header div").removeClass("ui-icon ui-icon-minusthick white");
			$("#handouts_header div").addClass("ui-icon ui-icon-plusthick white");
    		$("#handouts").slideUp("slow", function() {
				if (!$("#patient_name").is(":visible")) {
					$("#handouts_container").addClass("patient_container_collapse");
					$("#handouts_container").addClass("patient_container_collapse");
					$("#handouts_container").animate({
						height: '30px'
					}, 500);
					$("#patient_container").animate({
						height: '30px'
					}, 500);
				}
				$("#handouts_header").addClass("round_corners");
  			});
		} else {
			$("#handouts_header div").removeClass("ui-icon ui-icon-plusthick white");
			$("#handouts_header div").addClass("ui-icon ui-icon-minusthick white");
			$("#handouts").slideDown("slow");
			$("#handouts_header").removeClass("round_corners");
			$("#handouts_container").removeClass("patient_container_collapse");
			$("#handouts_container").removeClass("patient_container_collapse");
			$("#handouts_container").animate({
						height: '165px'
					}, 500);
					$("#patient_container").animate({
						height: '165px'
					}, 500);
		}
    	event.preventDefault();
	});
	
	$("#vitals_header a").click(function(event) {
		if ($("#vitals").is(":visible")) {
			$("#vitals_header div").removeClass("ui-icon ui-icon-minusthick white");
			$("#vitals_header div").addClass("ui-icon ui-icon-plusthick white");
    		$("#vitals").slideUp("slow", function() {
				$("#vitals_header").addClass("round_corners");
  			});
		} else {
			$("#vitals_header div").removeClass("ui-icon ui-icon-plusthick white");
			$("#vitals_header div").addClass("ui-icon ui-icon-minusthick white");
			$("#vitals").slideDown("slow");
			$("#vitals_header").removeClass("round_corners");
		}
    	event.preventDefault();
	});
	
	$("#quality_indicators_header a").click(function(event) {
		if ($("#quality_indicators").is(":visible")) {
			$("#quality_indicators_header div").removeClass("ui-icon ui-icon-minusthick white");
			$("#quality_indicators_header div").addClass("ui-icon ui-icon-plusthick white");
    		$("#quality_indicators").slideUp("slow", function() {
				$("#quality_indicators_header").addClass("round_corners");
  			});
		} else {
			$("#quality_indicators_header div").removeClass("ui-icon ui-icon-plusthick white");
			$("#quality_indicators_header div").addClass("ui-icon ui-icon-minusthick white");
			$("#quality_indicators").slideDown("slow");
			$("#quality_indicators_header").removeClass("round_corners");
		}
    	event.preventDefault();
	});
	
	$("#psf_results_header a").click(function(event) {
		if ($("#psf_results").is(":visible")) {
			$("#psf_results_header div").removeClass("ui-icon ui-icon-minusthick white");
			$("#psf_results_header div").addClass("ui-icon ui-icon-plusthick white");
    		$("#psf_results").slideUp("slow", function() {
				$("#psf_results_header").addClass("round_corners");
  			});
		} else {
			$("#psf_results_header div").removeClass("ui-icon ui-icon-plusthick white");
			$("#psf_results_header div").addClass("ui-icon ui-icon-minusthick white");
			$("#psf_results").slideDown("slow");
			$("#psf_results_header").removeClass("round_corners");
		}
    	event.preventDefault();
	});
	
	$("#pws_prompts_header a").click(function(event) {
		if ($("#pws_prompts").is(":visible")) {
			$("#pws_prompts_header div").removeClass("ui-icon ui-icon-minusthick white");
			$("#pws_prompts_header div").addClass("ui-icon ui-icon-plusthick white");
    		$("#pws_prompts").slideUp("slow", function() {
				$("#pws_prompts_header").addClass("round_corners");
  			});
		} else {
			$("#pws_prompts_header div").removeClass("ui-icon ui-icon-plusthick white");
			$("#pws_prompts_header div").addClass("ui-icon ui-icon-minusthick white");
			$("#pws_prompts").slideDown("slow");
			$("#pws_prompts_header").removeClass("round_corners");
		}
    	event.preventDefault();
	});
  });
 

//DWE CLINREQ-90
function updateCount(objectId)
{
	var max = 62000;
	var length = $("#" + objectId).val().length;
	$("#" + objectId + "Count").html(length + " of " + max + " character max");
}

//DWE CLINREQ-90
function validateTextNotes()
{
	var invalidExp = /]]>/;
	if($("#historyAndPhysicalText").val().search(invalidExp) > -1 || $("#assessmentAndPlanText").val().search(invalidExp) > -1)
	{
		$('<div id="errorMsg" title="Invalid Note">The CHICA Note cannot contain \"]]>\". Please remove the invalid characters before closing the CHICA Notes dialog.</div>').dialog({
			modal: true,
			height: "auto",
			width: 300,
			resizable: false,
			buttons: {
		        OK: function() {
		          $( this ).dialog( "close" );
		        }
		      }
		});
	}
	else
	{
		$("#notesDialog").dialog("close");
	}	
}

function restartSessionCounter(doRenewSession) {
	if (timeoutDialog != null) {
		timeoutDialog.restartCounter();
	}
	
	if (doRenewSession) {
		renewSession();
	}
}

function renewSession() {
	 $.ajax({
     	  "cache": false,
     	  "global": false,
     	  "dataType": "html",
     	  "type": "GET",
     	  "url": keepAliveURL,
     	  "timeout": 30000, // optional if you want to handle timeouts (which you should)
     	  "success": function (html) {
     		if (html == "OK") {
             // Do nothing.  The session was renewed.
            }
           }
     	});
}

function saveDraft(eventId) {
	if (eventId === "saveDraftButtonBottom" || eventId === "saveDraftButtonTop") {
		$("#saveDraftWaitDialog").dialog("open");
	}
	processCheckboxes();
	var submitForm = $("#pwsForm"); 
    $.ajax({
    	"cache": false,
        "data": submitForm.serialize(),
        "type": "POST",
        "url": saveDraftURL,
        "timeout": 30000, // optional if you want to handle timeouts (which you should)
        "error": function (xhr, textStatus, error) {
			if (eventId === "saveDraftButtonBottom" || eventId === "saveDraftButtonTop") {
			   $("#saveDraftWaitDialog").dialog("close");
			   $("#saveDraftErrorMessage").html("<p><b>An error occurred saving the draft: " + error + "</b></p>");
			   $("#saveDraftErrorDialog").dialog("open");
			} else {
				console.error(error);
			}
        }, // this sets up jQuery to give me errors
        "success": function (text) {
			if (eventId === "saveDraftButtonBottom" || eventId === "saveDraftButtonTop") {
				$("#saveDraftWaitDialog").dialog("close"); 
			}
			if (text != "success" ) {
				$("#saveDraftErrorMessage").html(text);
        		$("#saveDraftErrorDialog").dialog("open");
			} else if (eventId === "saveDraftButtonBottom" || eventId === "saveDraftButtonTop") {
				$("#saveDraftSuccessDialog").dialog("open"); 
			}
        }
    });
}