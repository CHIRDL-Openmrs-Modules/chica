var selectedForms = [];
var downloadID = "";
var formDialogWidth = 500;
$( document ).ready(function() {
	var encounterId = $("#encounterId").val();
	var action = "action=getAvailablePatientJITs&encounterId=" + encounterId;
	var url = "/openmrs/moduleServlet/chica/chicaMobile";
	$.ajax({
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
});

function handleGetAvailableJITsError(xhr, textStatus, error) {
//	$("#loading").hide();
	$("#formLoading").hide();
//	$("#serverError").html("<span>" + error + "</span>");
	$("#formServerError").html("<span>" + error + "</span>");
//	$("#serverError").show();
	$("#formServerError").show();
}

function parseAvailableJITs(responseXML) {
    // no matches returned
    if (responseXML === null) {
    	$("#loading").hide();
        return false;
    } else {
//    	var formList = $("#formSelector");
    	var formList = $("#formAccordion");
        var content = "";
        var count = 0;
        $(responseXML).find("availableJIT").each(function () {
        	var formName = $(this).find("formName").text();
            var formId = $(this).find("formId").text();
            var formInstanceId = $(this).find("formInstanceId").text();
            var locationId = $(this).find("locationId").text();
            var locationTagId = $(this).find("locationTagId").text();
            var description = $(this).find("description").text();
            if (description == null) {
            	description = "No description available for this form.";
            }
            
//            content = content + '<li id="form_' + count + '" class="ui-widget-content" formId="' + formId + 
//            	'" formInstanceId="' + formInstanceId + '" locationId="' + locationId + '" locationTagId="' + locationTagId + 
//            	'" title="' + description + '"><span>' + formName + '</span></li>';
            var formInstance = locationId + "_" + locationTagId + "_" + formId + "_" + formInstanceId;
        	var action = "action=getPatientJITs&formInstances=" + formInstance;
        	var url = "/openmrs/moduleServlet/chica/chicaMobile?";
            content = content + '<h3>' + formName + '<span style="float:right;" class="ui-icon ui-icon-print" title="Print" frame="' + formInstance + '"></span></h3><div class="accordionItem"><iframe id="' + formInstance + '" src="' + url + action + '"></iframe></div>';
            
            count++;
        });
        
        formList.html(content);
//        $("#loading").hide();
        $("#formLoading").hide();
        $("#formAccordion").accordion("refresh");
//        $('#formAccordion').accordion("option", "active", "false");
        $('#formAccordion').show();
        
        $("span.ui-icon-print").click(function(e) {
        	var frame = $(this).attr("frame");
        	var iframeish = $("#" + frame);
            
        	iframeish.focus();
//        	var contentWin = iframeish.contentWindow;
        	var contentWin=document.getElementById(frame).contentWindow;
        	document.getElementById(frame).print();
        	e.preventDefault();
            e.stopPropagation();
        });
        $("#formAccordion h3 span").tooltip();
//        $("#formList").show();
//        resizeFormDialog();
    }
}


$("#pwsForm").submit(function(event) {
	processCheckboxes(this);
});

function resizeFormDialog() {
	var total = 0;
	var i=0;

	$('#formSelector > li').each(function(index) {
		var tesi = $(this.firstChild).width();
		total = parseInt(total) + parseInt(tesi);
	});
	
	if (total > formDialogWidth) {
		$('#formPrintDialog').dialog().animate({width: total}, 400);
		$('#formPrintDialog').dialog().animate({position: ['center', 'middle']}, 400);
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

	hidden.value = strSel;
}

function processCheckboxes(form1) {
	outputSelected(form1.sub_Choice1, form1.Choice1);
	outputSelected(form1.sub_Choice2, form1.Choice2);
	outputSelected(form1.sub_Choice3, form1.Choice3);
	outputSelected(form1.sub_Choice4, form1.Choice4);
	outputSelected(form1.sub_Choice5, form1.Choice5);
	outputSelected(form1.sub_Choice6, form1.Choice6);
}

$(function() {
	$('#formAccordion').hide();
	$("#formList").hide();
	$("#selectionError").hide();
//	$("#serverError").hide();
	$("#formServerError").hide();
	$("#downloading").hide();
	
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
    }).prev(".ui-dialog-titlebar").css("background","#75A3A3");

    $("#problemButton").click(function() {
      $("#problemDialog").dialog("open");
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
    }).prev(".ui-dialog-titlebar").css("background","#75A3A3");

	$("#medButton").click(function() {
	  $("#medDialog").dialog("open");
	});
	
	$("#formPrintDialog").dialog({
    	open: function() { 
    		$(".ui-dialog").addClass("ui-dialog-shadow"); 
    		$("#formPrintDialog").scrollTop(0);
    	},
        autoOpen: false,
        modal: true,
        maxHeight: 400,
        minWidth: 250,
        width: formDialogWidth,
        height: "auto",
        show: {
          effect: "clip",
          duration: 750
        },
        hide: {
          effect: "clip",
          duration: 750
        }
    }).prev(".ui-dialog-titlebar").css("background","#75A3A3");
	
	$("#formPrintButton").click(function() {
//	  $("#formPrintDialog").dialog("open");
		$("#formAccordionDialog").dialog("open");
	});
	
	$("#confirmSubmitDialog").dialog({
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
    }).prev(".ui-dialog-titlebar").css("background","#75A3A3");
	
	$("#okSubmitButton").click(function() {
		$("#confirmSubmitDialog").dialog("close");
		$("#submitWaitDialog").dialog("open");
		$("#pwsForm").submit();
	});
	
	$("#cancelSubmitButton").click(function() {
		$("#confirmSubmitDialog").dialog("close");
	});
	
	$("#submitButtonBottom").click(function() {
		$("#confirmSubmitDialog").dialog("open");
	});
	
	$("#submitButtonTop").click(function() {
		$("#confirmSubmitDialog").dialog("open");
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
    		$(".ui-dialog").addClass("ui-dialog-shadow"); 
    		$("#formAccordionDialog").scrollTop(0);
    	},
        autoOpen: false,
        modal: true,
        maxHeight: 400,
        minWidth: 250,
        //width: formDialogWidth,
        width: $(window).width() * 0.70,
        height: $(window).height() * 0.70,
        show: {
          effect: "clip",
          duration: 750
        },
        hide: {
          effect: "clip",
          duration: 750
        }
    }).prev(".ui-dialog-titlebar").css("background","#75A3A3");
	
	$("#loadingDialog").dialog({
		open: function() { $(".ui-dialog").addClass("ui-dialog-shadow"); },
        autoOpen: false,
        modal: true,
        maxWidth: 150,
        maxHeight: 100,
        width: 150,
        height: 100
    }).dialog("widget").find(".ui-dialog-titlebar").hide();
	
	$( "#formSelector" ).selectable();
	
	$('#selectAllButton').click(function() {
	    $("li", "#formSelector").removeClass("ui-unselecting").addClass("ui-selected");
	});
	
	$('#unselectAllButton').click(function() {
	    $("li", "#formSelector").removeClass("ui-selected").addClass("ui-unselecting");
	});
	
	$("#printButton").click(function() {
		$("#selectionError").hide();
		$("#serverError").hide();
		$("#formList").hide();
		$("#downloading").show();
		var i = 0;
		var formInstances = "";
		$(".ui-selected", "#formSelector").each(function() {
			if (i != 0) {
				formInstances = formInstances + ",";
			}
			
            var index = $("#formSelector li").index( this );
            var formId = $(this).attr("formId");
            var formInstanceId = $(this).attr("formInstanceId");
            var locationId = $(this).attr("locationId");
            var locationTagId = $(this).attr("locationTagId");
            var formInstance = locationId + "_" + locationTagId + "_" + formId + "_" + formInstanceId;
            formInstances = formInstances + formInstance;
            i++;
         });
		
		if (i == 0) {
			$("#downloading").hide();
		    $("#formList").show();
		    $("#selectionError").show();
		} else {
			getForms(formInstances);
		}
		
		return false;
	});
	
	$(document).tooltip();
	$("#formSelector li").tooltip({
      show: null,
      hide: null
    });
	
	$("#formAccordion").accordion({
      heightStyle: "content",
      collapsible: true,
      active : 'none'
    });

//	$("#formPrintDialog").dialog("open");
//	$( "#accordion" ).accordion( "resize" );
	$("#formAccordionDialog").dialog("open");
  });

function getForms(formInstances) {
//	var action = "action=getPatientJITs&formInstances=" + formInstances;
//	var url = "/openmrs/moduleServlet/chica/chicaMobile?";
//	window.open(url + action);
	
//	$.fileDownload(url + action, {
//        successCallback: function(url) {
//        	$("#downloading").hide();
//        	$("#formList").show();
//        	deleteDownloadCookie();
//        },
//        failCallback: function(responseHtml, url) {
//        	$("#downloading").hide();
//        	$("#formList").show();
//        	deleteDownloadCookie();
//        	$("#serverError").html("<span>An error occurred downloading the file</span>");
//        	$("#serverError").show();
//        }
//    });
}

function deleteDownloadCookie() {
	document.cookie="fileDownload=;path=/openmrs;expires=Wed; 01 Jan 1970"
}
