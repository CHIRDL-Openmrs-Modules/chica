var chicaServletUrl = ctx + "/moduleServlet/chica/chica?";
var recommendedHandoutsAction = "action=getPatientJITs&formInstances=";
var pageOptions = "#page=1&view=FitH,top&navpanes=0";
var previousRecommendedHandoutSelection = -1;
var timeoutDialog = null;
var keepAliveURL = ctx + "/moduleServlet/chica/chica?action=keepAlive";
var saveDraftURL = chicaServletUrl + "action=saveFormDraft";
var type = "application/pdf";

function handleGetAvailableJITsError(xhr, textStatus, error) {
	$("#noForms").hide();
	$("#formServerErrorText").html('<span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span><span>Error occurred locating recommended forms: ' + error + '</span>');
	$("#formServerError").show();
}

function parseAvailableJITs(responseXML) {
    // no matches returned
    if (responseXML === null) {
    	$("#recommendedHandoutsFormList").selectable();
    	$("#loading").hide();
        return false;
    } else {	
        var count = 0;
        $(responseXML).find("availableJIT").each(function () {
        	var formName = $(this).find("formName").text();
            var formId = $(this).find("formId").text();
            var formInstanceId = $(this).find("formInstanceId").text();
            var locationId = $(this).find("locationId").text();
            var locationTagId = $(this).find("locationTagId").text();
            
        	var formInstance = locationId + "_" + locationTagId + "_" + formId + "_" + formInstanceId;
            count++;
            
            $('<li id="' + formInstance + '" title="' + formName + '">' + formName + '</li>').addClass('ui-widget-content').appendTo($('#recommendedHandoutsFormList'));
        });
        
    	if (count == 0){
    		$("#recommendedHandoutsContainer").hide();
        	$("#noForms").show();
        }
    	
    	$('#recommendedHandoutsFormList').selectable();
    }
}

//DWE CHICA-500
function combineSelected(selectedForms)
{
	var obj = $(".recommendedHandoutObject");
	var container = obj.parent();
	var newUrl = chicaServletUrl + recommendedHandoutsAction + selectedForms.toString() + pageOptions;
	var newobj = obj.clone();
	obj.remove();
	newobj.attr("data", newUrl);
	newobj.attr("type", type); // CHICA-948 Set the type since it was removed in the close event
	$(".recommendedHandoutContainer").show();
	
	container.append(newobj);
	restartSessionCounter(true);
}

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

function getAvailableJits() {
	$("#noForms").hide();
	var encounterId = $("#encounterId").val();
	var action = "action=getAvailablePatientJITs&encounterId=" + encounterId;
	$.ajax({
	  beforeSend: function(){
		  $("#formServerError").hide();
		  $("#formLoading").show();
      },
      complete: function(){
    	  $("#formLoading").hide();
      },
	  "cache": false,
	  "dataType": "xml",
	  "data": action,
	  "type": "POST",
	  "url": chicaServletUrl,
	  "timeout": 30000, // optional if you want to handle timeouts (which you should)
	  "error": handleGetAvailableJITsError, // this sets up jQuery to give me errors
	  "success": function (xml) {
          parseAvailableJITs(xml);
      }
	});
}

$(function() {
	$("button, input:submit, input:button").button();
	$("#submitButtonTop, #submitButtonBottom, #saveDraftButtonTop, #saveDraftButtonBottom").button();
	$("#retryButton, #notesButton, #formPrintButton, #problemButton, #forcePrintButton").button();
	
	getAvailableJits();
	
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
		$("#formSelectionDialog").dialog("open");
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
		saveDraft();
		event.preventDefault();
	});
	
	$("#retryButton").click(function(event) {
		getAvailableJits();
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
	
	$("#formSelectionDialog").dialog({
    	open: function() { 
    		$(".recommendedHandoutContainer").hide();
    		$(".ui-dialog").addClass("ui-dialog-shadow"); 
    		$("#formSelectionDialog").scrollTop(0);
    		updateRecommendedHandoutDimensions();
    		displayFirstJIT();
    	},
    	beforeClose: function() { 
    		$(".recommendedHandoutContainer").hide();
        	var obj = $(".recommendedHandoutObject");
        	var container = obj.parent();
        	var newobj = obj.clone();
        	obj.remove();
        	
        	// CHICA-948 Remove data and type attributes so IE doesn't cause an authentication error when loading the page.
			newobj.removeAttr("data");
			newobj.removeAttr("type");
        	container.append(newobj);
    	},
    	close: function() { 
    		$('#recommendedHandoutsFormList .ui-selected').removeClass('ui-selected')
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
	        	  $("#formSelectionDialog").dialog("close");
	          }
          }
        ]
    });
	
	$("#noSelectedFormsDialog").dialog({
        resizable: false,
        modal: true,
        autoOpen: false,
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow");
            $("#recommendedHandoutsFormList").selectable("disable");
          },
        close: function() { 
        	$("#recommendedHandoutsFormList").selectable("enable");
        	var selectedForms = getSelectedForms();
        	if (selectedForms.length === 1) {
        		$(".recommendedHandoutContainer").show();
        	}
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
        	$("#recommendedHandoutsFormList").selectable("enable");
            $(this).dialog("close");
            var selectedForms = getSelectedForms();
        	if (selectedForms.length === 1) {
        		$(".recommendedHandoutContainer").show();
        	}
          }
        }
    });
	
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
	
    $("#formSelectionDialogContainer").css("background", "#f4f0ec"); 
	
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
            });
        });

    };
    
	$(".uncheckableRadioButton").uncheckableRadio();
	var patientName = $("#patientNameForcePrint").val();
	$(".force-print-patient-name").html("<p>Please choose form(s) for " + patientName + ".</p>");
	
	$("#recommendedHandoutsSelectAllButton").button();
	$("#recommendedHandoutsSelectAllButton").click(function() {
		$("#recommendedHandoutsFormList li").not(".ui-selected").addClass("ui-selected");
		var selectedForms = getSelectedForms();
    	if (selectedForms.length > 1) {
    		$(".recommendedHandoutContainer").hide();
    	} else if (selectedForms.length == 1) {
    		combineSelected(selectedForms);
    	}
    });
	
	$("#recommendedHandoutsCombineButton").button();
    $("#recommendedHandoutsCombineButton").click(function() {
    	var selectedForms = getSelectedForms();
    	if (selectedForms.length < 2) {
    		$(".recommendedHandoutContainer").hide();
    		$("#noSelectedFormsDialog").dialog("open");
    	} else {
    		combineSelected(selectedForms);
    	}
    });
    
    $("#recommendedHandoutsFormList").selectable({
	  stop: function() {
		var selectedForms = new Array();
	    $(".ui-selected", this ).each(function() {
	    	var id = this.id;
	    	selectedForms.push(id);
	    });
	    
	    if (selectedForms.length === 1) {
	    	var formInstance = selectedForms[0];
	    	var obj = $(".recommendedHandoutObject");
	    	var container = obj.parent();
	    	var newUrl = chicaServletUrl + recommendedHandoutsAction + formInstance + pageOptions;
	    	var newobj = obj.clone();
	    	obj.remove();
	    	newobj.attr("data", newUrl);
	    	newobj.attr("type", type); // CHICA-948 Set the type since it was removed in the close event
	    	$(".recommendedHandoutContainer").show();
	    	
	    	container.append(newobj);
	    	restartSessionCounter(true);
	    } else {
	    	$(".recommendedHandoutContainer").hide();
	    }
	  },
	  selecting: function(e, ui) { // on select
        var curr = $(ui.selecting.tagName, e.target).index(ui.selecting); // get selecting item index
        if(e.shiftKey && previousRecommendedHandoutSelection > -1) { // if shift key was pressed and there is previous - select them all
            $(ui.selecting.tagName, e.target).slice(Math.min(previousRecommendedHandoutSelection, curr), 1 + Math.max(previousRecommendedHandoutSelection, curr)).addClass('ui-selected');
            previousRecommendedHandoutSelection = -1; // and reset prev
        } else {
        	previousRecommendedHandoutSelection = curr; // othervise just save prev
        }
      }
	});
    
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

// DWE CLINREQ-90
function displayFirstJIT()
{
    var count = 0;
    var formInstance = null;
    $("#recommendedHandoutsFormList li").each(function() {
    	if (count === 0) {
    		$(this).addClass("ui-selected");
    		formInstance = this.id;
    		previousRecommendedHandoutSelection = count;
    	}
    	
    	count++;
    });
    
    if (count === 1) {
    	$(".recommendedHandoutsCombineButtonPanel").hide();
    } else if (count > 1) {
    	$(".recommendedHandoutsCombineButtonPanel").show();
    }
    
    if (formInstance != null) {
		var obj = $(".recommendedHandoutObject");
		var container = obj.parent();
		var newUrl = chicaServletUrl + recommendedHandoutsAction + formInstance + pageOptions;
		var newobj = obj.clone();
		obj.remove();
		newobj.attr("data", newUrl);
		newobj.attr("type", type); // CHICA-948 Set the type since it was removed in the close event
		$(".recommendedHandoutContainer").show();;
		
		container.append(newobj);
		restartSessionCounter(true);
    }
}

function formLoaded() {
	var obj = $(".recommendedHandoutObject");
	if (obj != null) {
		var url = obj.attr("data");
		if (url != null && url.length > 0) {
			$(".formLoading").hide();
			$(".recommendedHandoutContainer").show();
		}
	}
}

function getSelectedForms() {
	var selectedForms = new Array();
	$(".ui-selected", "#recommendedHandoutsFormList").each(function() {
    	var id = this.id;
    	selectedForms.push(id);
    });
	
	return selectedForms;
}

function updateRecommendedHandoutDimensions() {
	var divHeight = $("#formSelectionDialogContainer").height();
	var instructHeight = $(".recommendedHandoutsMultiselect").height();
	var listHeight = $("#recommendedHandoutsFormList").height();
	var newDivHeight = divHeight * 0.75;
	if ((newDivHeight > listHeight) && (listHeight != 0)) {
		newDivHeight = listHeight;
	}
	
    // Update the height of the select
    $("#recommendedHandoutsFormList").selectable().css({"height":(newDivHeight) + "px"});
    $(".recommendedHandoutsFormListContainer").css({"height":(newDivHeight + 10) + "px"});
    $("#recommendedHandoutsContainer").css({"height":"100%"});
    divHeight = $("#recommendedHandoutsContainer").height();
    $("#recommendedHandoutsCombineButtonPanel").css({"height":(divHeight - instructHeight - (newDivHeight + 10)) + "px"});
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

function saveDraft() {
	$("#saveDraftWaitDialog").dialog("open");
	processCheckboxes();
	var submitForm = $("#pwsForm"); 
    $.ajax({
    	"cache": false,
        "data": submitForm.serialize(),
        "type": "POST",
        "url": saveDraftURL,
        "timeout": 30000, // optional if you want to handle timeouts (which you should)
        "error": handleSaveDraftError, // this sets up jQuery to give me errors
        "success": function (text) {
        	$("#saveDraftWaitDialog").dialog("close");
        	if (text === "success") {
	        	$("#saveDraftSuccessDialog").dialog("open");
        	} else {
        		$("#saveDraftErrorMessage").html(text);
        		$("#saveDraftErrorDialog").dialog("open");
        	}
        }
    });
}

function handleSaveDraftError(xhr, textStatus, error) {
	$("#saveDraftWaitDialog").dialog("close");
	$("#saveDraftErrorMessage").html("<p><b>An error occurred saving the draft: " + error + "</b></p>");
	$("#saveDraftErrorDialog").dialog("open");
}
