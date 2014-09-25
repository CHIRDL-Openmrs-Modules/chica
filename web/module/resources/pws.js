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
  });
