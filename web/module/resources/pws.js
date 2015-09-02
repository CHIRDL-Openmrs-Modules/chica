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
    	var tabs = null;
		tabs = $("#tabs");
    	
        var tabList = '<ul id="tabList">';
        var divList = "";
        var count = 0;
        var formInstance = "";
        $(responseXML).find("availableJIT").each(function () {
        	var formName = $(this).find("formName").text();
            var formId = $(this).find("formId").text();
            var formInstanceId = $(this).find("formInstanceId").text();
            var locationId = $(this).find("locationId").text();
            var locationTagId = $(this).find("locationTagId").text();
            
        	formInstance = locationId + "_" + locationTagId + "_" + formId + "_" + formInstanceId;
        	var action = "action=getPatientJITs&formInstances=" + formInstance  + "#page=1&view=FitH,top&navpanes=0";
        	var url = "/openmrs/moduleServlet/chica/chica?";
        	tabList += '<li><a href="#tabs-' + count + '" title="' + formName + '">' + formName + '</a></li>';
        	divList += '<div id="tabs-' + count + '" style="float:left;height:100%"><object type="application/pdf" class="recommended-forms" data="' + url + action + 
            	'"><span style="color: #000;font-size:14px;">It appears your Web browser is not configured to display PDF files. ' +
            	'<a href="http://get.adobe.com/reader/" target="_blank"><font style="color: #0000FF;text-decoration: none; border-bottom: 1px solid #0000FF;">Click here to download the Adobe PDF Reader.</font></a>  ' + 
            	'Please restart your browser once the installation is complete.</span></object></div>';
            
            count++;
        });
        
    	if (count == 0) {
        	$("#noForms").show();
        } else {
        	tabList += "</ul>"
        	tabs.html(tabList + divList);
        	$("#tabs").tabs({
        		overflowTabs: true,
        		heightStyle: "content",
        		tabPadding: 23,
        		containerPadding: 40
        	}).addClass("ui-tabs-vertical ui-helper-clearfix");
            $("#tabs li").removeClass("ui-corner-top").addClass( "ui-corner-left");
            $('#tabs').show();
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
	$("#notesButton").button();
	
	$('#tabs').hide();
	
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
		$("#formTabDialog").dialog("open");
		event.preventDefault();
	});
	
	$("#notesDialog").dialog({
    	open: function() { $(".ui-dialog").addClass("ui-dialog-shadow"); },
        autoOpen: false,
        minHeight: 350,
        minWidth: 450,
        width: 950,
        height: $(window).height() * 0.95,
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
        	       $("#notesDialog").dialog("close");
        	 }
           }
        ]
    });
	
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
	
	$("#formTabDialog").dialog({
    	open: function() { 
    		$("#tabs").show();
    		$(".recommended-forms").show();
    		$(".ui-dialog").addClass("ui-dialog-shadow"); 
    		$("#formTabDialog").scrollTop(0);
    	},
    	beforeClose: function() { 
    		$(".recommended-forms").hide();
    	},
    	close: function() { 
    		$("#tabs").hide();
    	},
        autoOpen: false,
        modal: true,
        minHeight: 350,
        minWidth: 450,
        width: 950,
        height: $(window).height() * 0.95,
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
	        	  $("#formTabDialog").dialog("close");
	          }
          }
        ]
    });
	
	var tabDivHeight = $(window).height() * 0.95 - 135;
	$("#formTabDialogContainer").css({"height":tabDivHeight});
    $("#tabs").css({"height":tabDivHeight});
    $("#formTabDialogContainer").css("background", "#cc9966");
    $(".recommended-forms").css({"height":tabDivHeight});
	
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
    		$(".force-print-forms").val("selectForm").selectmenu("refresh");
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
	        	  $("#forcePrintDialog").dialog("close");
	          }
          }
        ]
    });
 
	$("#tabList").tooltip();
	$("#formTabDialog").dialog("open");
  });