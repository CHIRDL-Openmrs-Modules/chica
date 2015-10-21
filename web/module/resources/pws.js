var loadedOptionalHandouts = false;
var adobeSpanTag = '<span style="color: #000;font-size:14px;">It appears your Web browser is not configured to display PDF files. ' +
            '<a href="http://get.adobe.com/reader/" target="_blank"><font style="color: #0000FF;text-decoration: none; border-bottom: 1px solid #0000FF;">Click here to download the Adobe PDF Reader.</font></a>  ' + 
            'Please restart your browser once the installation is complete.</span>'; 

var selectFormsDiv = '<div><h4>Please select one or more forms to combine. Then click the <a style="color: blue;" href="#" onclick="combineSelected(); return false;" title="Combine selected forms">Combine Selected Forms</a> tab to update the PDF with the combined forms.</h4></div>';

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
    	
		// DWE CHICA-500
		// Combine selected forms option
		var tabList = '<ul id="tabList">';
		var divList = '';
		
    	tabList += '<li><table class="tabsTable"><tr><td class="tabsLeftCell selectAllCell" onclick="selectAllForms();">All</td>' + 
    	           '<td class="tabsRightCell selectAllCell">' +
    	           '<a style="text-decoration: underline; cursor: pointer;" href="#tabs-0" onclick="combineSelected();" title="Combine selected forms">Combine Selected Forms</a></td></tr></table></li>';
    	
    	divList += '<div class="combineSelectedClass" id="tabs-0">' +
    			   '<object id="combinedForms" type="application/pdf" class="recommended-forms" ' + 
    	           ' data="">'; // Setting data to empty to make IE happy. If data attribute is not set, an "Access Denied" error will display.
    	
    	divList += adobeSpanTag + '</object></div>';	
			
        var count = 1; // Start count at 1 instead of 0. The first one will now be the "Combine selected forms" tab
        var formInstance = "";
        $(responseXML).find("availableJIT").each(function () {
        	var formName = $(this).find("formName").text();
            var formId = $(this).find("formId").text();
            var formInstanceId = $(this).find("formInstanceId").text();
            var locationId = $(this).find("locationId").text();
            var locationTagId = $(this).find("locationTagId").text();
            
        	formInstance = locationId + "_" + locationTagId + "_" + formId + "_" + formInstanceId;
        	var action = "action=getPatientJITs&formInstances=" + formInstance + "#page=1&view=FitH,top&navpanes=0";
        	var url = "/openmrs/moduleServlet/chica/chica?";
        	tabList += '<li><table class="tabsTable"><tr><td class="tabsLeftCell"><input class="formsCheckbox" type="checkbox" onclick="resetCombinedForms()" id="' + formInstance + '" name="' + formInstance + '"></td><td class="tabsRightCell"><a href="#tabs-' + count + '" onclick="getJIT(\''+url+'\', \''+action+'\', \''+count+'\');" title="' + formName + '">' + formName + '</a></td></tr></table></li>';
        	divList += '<div id="tabs-' + count + '"><object id="object_'+ count + '" type="application/pdf" class="recommended-forms"';   
            	
        	// DWE CHICA-500 Only set the data attribute for the first one
        	// This will prevent the offsetParent script error in Firefox
        	if(count == 1)
        	{
        		divList += ' data="' + url + action + '>';
        	}
        	else 
        	{
        		divList += ' data="">'; // Setting this to empty to make IE happy. If data attribute is not set, an "Access Denied" error will display.
        	}
        	
        	divList += adobeSpanTag + '</object></div>';
        	
            count++;
        });
        
    	if (count == 1){
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
            $("#tabs").tabs( "option", "active", 1 ); // DWE CHICA-500 Show the first form by default and not the "Combine Selected Forms" tab           
        }
    }
}

// DWE CHICA-500
function selectAllForms()
{
	var $cbs = $(".formsCheckbox:checkbox:enabled");
	var checked = $cbs.filter(":first").prop("checked");
	$(".formsCheckbox").each(function(){ this.checked = !checked});
	
	resetCombinedForms();
}

// DWE CHICA-500
function resetCombinedForms()
{
	// If the user is viewing the "Combine selected forms" tab already, 
	// we need to clear the display so the user will know that the area needs to be refreshed before printing
	if($(".combineSelectedClass").is(':visible'))
	{
		$(".combineSelectedClass").html(selectFormsDiv);
	}
}

//DWE CHICA-500
function getJIT(url, action, count)
{
	if($("#object_" + count).attr("data").length === 0) // Get the form only if we don't already have it
	{
		var divList = '<object id="object_'+ count + '" type="application/pdf" class="recommended-forms" data="'+ url + action + '">' + adobeSpanTag + '</object></div>';
		
		$("#tabs-"+count).html(divList); // Setting the inner html of the div seems to be the best solution for all browsers instead of setting the data attribute. 
	}
}

//DWE CHICA-500
function combineSelected()
{
	var url = "/openmrs/moduleServlet/chica/chica?";
	var action = "action=getPatientJITs&formInstances=";
	var pageOptions = "#page=1&view=FitH,top&navpanes=0";
	
	// Gets all of the currently selected checkboxes
	var count = 0;
	var formInstances = "";
	$('.formsCheckbox:checkbox').each(function(){ 
		
		if(this.checked)
		{
			formInstances += this.id + ",";
			count++;
		}
	});
	
	if(count > 0)
	{
		divList = '<object id="combinedForms" type="application/pdf" class="recommended-forms" data="'+ url + action + formInstances + pageOptions + '">' + adobeSpanTag + '</object>';
	}
	else
	{
		divList = selectFormsDiv;
	}
	
	// Set the inner html so that the PDF will be refreshed
	$(".combineSelectedClass").html(divList);
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
    $("#formTabDialogContainer").css("background", "#f4f0ec"); 
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
  });