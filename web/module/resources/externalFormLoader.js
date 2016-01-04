var loadedOptionalHandouts = false;
$(document).ready(function() {
	$("#forcePrintButton").button();
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
	
	var errors = $("#hasErrors").val();
	if (!errors) {
		$("#loadForm").submit();
	}
});

