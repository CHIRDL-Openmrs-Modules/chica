var selectedForms = [];
var downloadID = "";
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
	
}

function parseAvailableJITs(responseXML) {
    // no matches returned
    if (responseXML === null) {
    	$("#loading").hide();
        return false;
    } else {
    	var formList = $("#formSelector");
        var content = "";
        $(responseXML).find("availableJIT").each(function () {
        	var formName = $(this).find("formName").text();
            var formId = $(this).find("formId").text();
            var formInstanceId = $(this).find("formInstanceId").text();
            var locationId = $(this).find("locationId").text();
            var locationTagId = $(this).find("locationTagId").text();
            
            content = content + '<li class="ui-widget-content" formId="' + formId + '" formInstanceId="' + formInstanceId + 
            	'" locationId="' + locationId + '" locationTagId="' + locationTagId + '">' + formName + '</li>';
        });
        
        formList.html(content);
        $("#loading").hide();
        $("#formList").show();
    }
}


$("#pwsForm").submit(function(event) {
	processCheckboxes(this);
});

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
	$("#formList").hide();
	$("#selectionError").hide();
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
	  $("#formPrintDialog").dialog("open");
	});
	
	$("#confirmSubmitDialog").dialog({
	  open: function() { $(".ui-dialog").addClass("ui-dialog-shadow"); },
      autoOpen: false,
      modal: true
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
	});
	
  });

function getForms(formInstances) {
	var action = "action=getPatientJITs&formInstances=" + formInstances;
	var url = "/openmrs/moduleServlet/chica/chicaMobile?";
	$("#downloadLink").attr("href", url + action);
	$("#downloadLink").get(0).click();
	$("#downloading").hide();
    $("#formList").show();
}

function handleGetJITsError(xhr, textStatus, error) {
	alert(error);
}

function parseJITs(responseXML) {
	
}
