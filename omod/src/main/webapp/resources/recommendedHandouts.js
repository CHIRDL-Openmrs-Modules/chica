var chicaServletUrl = ctx + "/moduleServlet/chica/chica?";
var recommendedHandoutsAction = "action=getPatientJITs&formInstances=";
var pageOptions = "#page=1&view=FitH,top&navpanes=0";
var previousRecommendedHandoutSelection = -1;
$(function() { 
	var loadRecommendedHandouts = $("#loadRecommendedHandouts").val();
	if (typeof loadRecommendedHandouts === "undefined" || loadRecommendedHandouts !=="false") {
		getAvailableJits();
	}
	
	$("#recommended-handouts-form-selection-dialog").dialog({
    	open: function() { 
    		$(".recommended-handout-container").hide();
    		$(".ui-dialog").addClass("ui-dialog-shadow"); 
    		$("#recommended-handouts-form-selection-dialog").scrollTop(0);
    		updateRecommendedHandoutDimensions();
    		displayFirstJIT();
    	},
    	beforeClose: function() { 
    		$(".recommended-handout-container").hide();
        	var obj = $(".recommended-handout-object");
        	var container = obj.parent();
        	var newobj = obj.clone();
        	obj.remove();
        	
        	// CHICA-948 Remove data and type attributes so IE doesn't cause an authentication error when loading the page.
			newobj.removeAttr("data");
			newobj.removeAttr("type");
        	container.append(newobj);
    	},
    	close: function() { 
    		$('#recommended-handouts-form-list .ui-selected').removeClass('ui-selected')
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
	        	  $("#recommended-handouts-form-selection-dialog").dialog("close");
	          }
          }
        ]
    });
	
	$("#recommended-handouts-no-selected-forms-dialog").dialog({
        resizable: false,
        modal: true,
        autoOpen: false,
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow");
            $("#recommended-handouts-form-list").selectable("disable");
          },
        close: function() { 
        	$("#recommended-handouts-form-list").selectable("enable");
        	var selectedForms = getSelectedForms();
        	if (selectedForms.length === 1) {
        		$(".recommended-handout-container").show();
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
        	$("#recommended-handouts-form-list").selectable("enable");
            $(this).dialog("close");
            var selectedForms = getSelectedForms();
        	if (selectedForms.length === 1) {
        		$(".recommended-handout-container").show();
        	}
          }
        }
    });
	
	$("#recommended-handouts-form-selection-dialog-container").css("background", "#f4f0ec"); 
	
	$("#recommended-handouts-retry-button").button();
	$("#recommended-handouts-retry-button").click(function(event) {
		getAvailableJits();
		event.preventDefault();
	});
	
	$("#recommended-handouts-select-all-button").button();
	$("#recommended-handouts-select-all-button").click(function() {
		$("#recommended-handouts-form-list li").not(".ui-selected").addClass("ui-selected");
		var selectedForms = getSelectedForms();
    	if (selectedForms.length > 1) {
    		$(".recommended-handout-container").hide();
    	} else if (selectedForms.length == 1) {
    		combineSelected(selectedForms);
    	}
    });
	
	$("#recommended-handouts-form-list").selectable({
		  stop: function() {
			var selectedForms = new Array();
		    $(".ui-selected", this ).each(function() {
		    	var id = this.id;
		    	selectedForms.push(id);
		    });
		    
		    if (selectedForms.length === 1) {
		    	var formInstance = selectedForms[0];
		    	var obj = $(".recommended-handout-object");
		    	var container = obj.parent();
		    	var newUrl = chicaServletUrl + recommendedHandoutsAction + formInstance + pageOptions;
		    	var newobj = obj.clone();
		    	obj.remove();
		    	newobj.attr("data", newUrl);
		    	newobj.attr("type", type); // CHICA-948 Set the type since it was removed in the close event
		    	$(".recommended-handout-container").show();
		    	
		    	container.append(newobj);
		    } else {
		    	$(".recommended-handout-container").hide();
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
	
	$("#recommended-handouts-combine-button").button();
    $("#recommended-handouts-combine-button").click(function() {
    	var selectedForms = getSelectedForms();
    	if (selectedForms.length < 2) {
    		$(".recommended-handout-container").hide();
    		$("#recommended-handouts-no-selected-forms-dialog").dialog("open");
    	} else {
    		combineSelected(selectedForms);
    	}
    });
});

function updateRecommendedHandoutDimensions() {
	var divHeight = $("#recommended-handouts-form-selection-dialog-container").height();
	var instructHeight = $(".recommended-handouts-multiselect").height();
	var listHeight = $("#recommended-handouts-form-list").height();
	var newDivHeight = divHeight * 0.75;
	if ((newDivHeight > listHeight) && (listHeight != 0)) {
		newDivHeight = listHeight;
	}
	
    // Update the height of the select
    $("#recommended-handouts-form-list").selectable().css({"height":(newDivHeight) + "px"});
    $(".recommended-handouts-form-list-container").css({"height":(newDivHeight + 10) + "px"});
    $("#recommended-handouts-container").css({"height":"100%"});
    divHeight = $("#recommended-handouts-container").height();
    $("#recommended-handouts-combine-button-panel").css({"height":(divHeight - instructHeight - (newDivHeight + 10)) + "px"});
}

function getAvailableJits() {
	$("#recommended-handouts-no-forms").hide();
	var encounterId = $("#encounterId").val();
	var action = "action=getAvailablePatientJITs&encounterId=" + encounterId;
	$.ajax({
	  beforeSend: function(){
		  $("#recommended-handouts-form-server-error").hide();
		  $("#recommended-handouts-form-loading").show();
      },
      complete: function(){
    	  $("#recommended-handouts-form-loading").hide();
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

function handleGetAvailableJITsError(xhr, textStatus, error) {
	$("#recommended-handouts-no-forms").hide();
	$("#recommended-handouts-form-server-error-text").html('<span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span><span>Error occurred locating recommended forms: ' + error + '</span>');
	$("#recommended-handouts-form-server-error").show();
}

function parseAvailableJITs(responseXML) {
    // no matches returned
    if (responseXML === null) {
    	$("#recommended-handouts-form-list").selectable();
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
            
            $('<li id="' + formInstance + '" title="' + formName + '">' + formName + '</li>').addClass('ui-widget-content').appendTo($('#recommended-handouts-form-list'));
        });
        
    	if (count == 0){
    		$("#recommended-handouts-container").hide();
        	$("#recommended-handouts-no-forms").show();
        }
    	
    	$('#recommended-handouts-form-list').selectable();
    }
}

//DWE CLINREQ-90
function displayFirstJIT()
{
    var count = 0;
    var formInstance = null;
    $("#recommended-handouts-form-list li").each(function() {
    	if (count === 0) {
    		$(this).addClass("ui-selected");
    		formInstance = this.id;
    		previousRecommendedHandoutSelection = count;
    	}
    	
    	count++;
    });
    
    if (count === 1) {
    	$(".recommended-handouts-combine-button-panel").hide();
    } else if (count > 1) {
    	$(".recommended-handouts-combine-button-panel").show();
    }
    
    if (formInstance != null) {
		var obj = $(".recommended-handout-object");
		var container = obj.parent();
		var newUrl = chicaServletUrl + recommendedHandoutsAction + formInstance + pageOptions;
		var newobj = obj.clone();
		obj.remove();
		newobj.attr("data", newUrl);
		newobj.attr("type", type); // CHICA-948 Set the type since it was removed in the close event
		$(".recommended-handout-container").show();;
		
		container.append(newobj);
    }
}

function getSelectedForms() {
	var selectedForms = new Array();
	$(".ui-selected", "#recommended-handouts-form-list").each(function() {
    	var id = this.id;
    	selectedForms.push(id);
    });
	
	return selectedForms;
}

//DWE CHICA-500
function combineSelected(selectedForms)
{
	var obj = $(".recommended-handout-object");
	var container = obj.parent();
	var newUrl = chicaServletUrl + recommendedHandoutsAction + selectedForms.toString() + pageOptions;
	var newobj = obj.clone();
	obj.remove();
	newobj.attr("data", newUrl);
	newobj.attr("type", type); // CHICA-948 Set the type since it was removed in the close event
	$(".recommended-handout-container").show();
	
	container.append(newobj);
}

function formLoaded() {
	var obj = $(".recommended-handout-object");
	if (obj != null) {
		var url = obj.attr("data");
		if (url != null && url.length > 0) {
			$(".recommended-handouts-form-loading").hide();
			$(".recommended-handout-container").show();
		}
	}
}