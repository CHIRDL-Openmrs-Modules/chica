var loadedOptionalHandouts = false;
function handleGetAvailableJITsError(xhr, textStatus, error) {
	$("#noForms").hide();
	$("#formServerErrorText").html('<span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span><span>Error occurred locating recommended forms: ' + error + '</span>');
	$("#formServerError").show();
}

function parseAvailableJITs(responseXML) {
    // no matches returned
    if (responseXML === null) {
    	$("#loading").hide();
        return false;
    } else {
    	var formList = null;
		formList = $("#formAccordion");
    	
        var content = "";
        var count = 0;
        var formInstance = "";
        $(responseXML).find("availableJIT").each(function () {
        	var formName = $(this).find("formName").text();
            var formId = $(this).find("formId").text();
            var formInstanceId = $(this).find("formInstanceId").text();
            var locationId = $(this).find("locationId").text();
            var locationTagId = $(this).find("locationTagId").text();
            
        	formInstance = locationId + "_" + locationTagId + "_" + formId + "_" + formInstanceId;
        	var action = "action=getPatientJITs&formInstances=" + formInstance + "#view=fit&navpanes=0";
        	var url = "/openmrs/moduleServlet/chica/chica?";
            content = content + '<h3>' + formName + '</h3><div><iframe class="recommended-forms" src="' + url + action + 
            	'"></iframe></div>';
            
            count++;
        });
        
    	if (count == 0) {
        	$("#noForms").show();
        } else {
        	formList.html(content);
            $("#formAccordion").accordion("refresh");
            $('#formAccordion').show();
            var divHeight = $("#dialogWrapper").height();
            var count = 0;
            $("#formAccordion > h3").each(function() {
            	count++;
            });
            
            var newFormHeight = (divHeight - (count*40) - 45);
            //alert(newFormHeight);
            $(".recommended-forms").css({"height":newFormHeight});
        }
    }
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
	var url = "/openmrs/moduleServlet/chica/chica";
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
	  "url": url,
	  "timeout": 30000, // optional if you want to handle timeouts (which you should)
	  "error": handleGetAvailableJITsError, // this sets up jQuery to give me errors
	  "success": function (xml) {
          parseAvailableJITs(xml);
      }
	});
}

$(function() {
	$("button, input:submit, input:button").button();
	$("#submitButtonTop").button();
	$("#submitButtonBottom").button();
	$("#formPrintButton").button();
	$("#problemButton").button();
	$("#forcePrintButton").button();
	$("#retryButton").button();
	
	$("#formAccordion").accordion({
	      heightStyle: "content",
	      collapsible: true,
	      active : false
	    });
	$('#formAccordion').hide();
	
	getAvailableJits();
	
    $("#problemDialog").dialog({
      open: function() { $(".ui-dialog").addClass("ui-dialog-shadow"); },
      autoOpen: false,
      modal: true,
      show: {
        effect: "clip",
        duration: 750
      },
      hide: {
        effect: "clip",
        duration: 750
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
          effect: "clip",
          duration: 750
        },
        hide: {
          effect: "clip",
          duration: 750
        }
    })

	$("#medButton").click(function(event) {
	  $("#medDialog").dialog("open");
	  event.preventDefault();
	});
	
	$("#formPrintButton").click(function(event) {
		$("#formAccordionDialog").dialog("open");
		event.preventDefault();
	});
	
	$("#confirmSubmitDialog").dialog({
	  open: function() { $(".ui-dialog").addClass("ui-dialog-shadow"); },
      autoOpen: false,
      modal: true,
      resizable: false,
      show: {
          effect: "clip",
          duration: 750
        },
        hide: {
          effect: "clip",
          duration: 750
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
	
	$("#submitButtonBottom").click(function(event) {
		$("#confirmSubmitDialog").dialog("open");
		event.preventDefault();
	});
	
	$("#submitButtonTop").click(function(event) {
		$("#confirmSubmitDialog").dialog("open");
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
        height: 50
    }).dialog("widget").find(".ui-dialog-titlebar").hide();
	
	$("#formAccordionDialog").dialog({
    	open: function() { 
    		$(".recommended-forms").show();
    		$(".ui-dialog").addClass("ui-dialog-shadow"); 
    		$("#formAccordionDialog").scrollTop(0);
    	},
    	beforeClose: function() { 
    		$(".recommended-forms").hide();
    	},
    	close: function() { 
    		$("#formAccordion").accordion({
    			active: false
    		});
    	},
        autoOpen: false,
        modal: true,
        minHeight: 350,
        minWidth: 450,
        width: $(window).width() * 0.70,
        height: $(window).height() * 0.90,
        show: {
          effect: "clip",
          duration: 750
        },
        hide: {
          effect: "clip",
          duration: 750
        },
        resize: function(e,ui) {
        	var divHeight = $("#dialogWrapper").height();
            var count = 0;
            $("#formAccordion > h3").each(function() {
            	count++;
            });
            
            var newFormHeight = (divHeight - (count*40) - 45);
            $(".recommended-forms").css({"height":newFormHeight});
        }
    });
	
	$("#forcePrintButton").click(function(event) {
		$("#forcePrintDialog").dialog("open");
		event.preventDefault();
	});
	
	$("#forcePrintDialog").dialog({
    	open: function() { 
    		$(".ui-dialog").addClass("ui-dialog-shadow"); 
    		if(!loadedOptionalHandouts) {
    			forcePrint_loadForms();
    			loadedOptionalHandouts = true;
    		}
    		var formSelectionHeight = $(".force-print-forms-container").height();
    		$(".force-print-form-object").height($(".pws-force-print-content").height() - formSelectionHeight);
    	},
    	beforeClose: function(event, ui) { 
        	// Have to do this nonsense to prevent Chrome and Firefox from sending an additional request  to the server for a PDF when the dialog is closed.
        	$(".force-print-form-container").hide();
        	var obj = $(".force-print-form-object");
        	var container = obj.parent();
        	var newobj = obj.clone();
        	obj.remove();
        	newobj.attr("data", "");
        	container.append(newobj);
        },
    	close: function() { 
    		$(".force-print-form-container").hide();
    		$('.force-print-forms').val("selectform").selectmenu("refresh");
    	},
        autoOpen: false,
        modal: true,
        minHeight: 350,
        minWidth: 450,
        width: $(window).width() * 0.90,
        height: $(window).height() * 0.90,
        show: {
          effect: "clip",
          duration: 750
        },
        hide: {
          effect: "clip",
          duration: 750
        },
        resize: function(e,ui) {
        	var divHeight = $(".pws-force-print-content").height();
        	var formSelectionHeight = $(".force-print-forms-container").height();
        	// Update the form height
        	$(".force-print-form-object").height($(".pws-force-print-content").height() - formSelectionHeight);
    		// Update the height of the select
    		$(".force-print-forms").selectmenu().selectmenu("menuWidget").css({"max-height":(divHeight * 0.60) + "px"});
        }
    });
	
	$("#formAccordionDialog").dialog("open");
  });