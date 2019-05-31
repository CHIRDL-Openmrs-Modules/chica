var loadedOptionalHandouts = false;
$(document).ready(function() {
	$("#forcePrintButton, #recommendedHandoutsButton").button();
	$("#forcePrintButton").click(function(event) {
		$("#force-print-dialog").css("cursor", "default");
		$("#force-print-dialog").dialog("open");
		event.preventDefault();
	});
	
	$("#recommendedHandoutsButton").click(function(event) {
		$("#recommended-handouts-form-selection-dialog").dialog("open");
		event.preventDefault();
	});
	
	var errors = $("#hasErrors").val();
	if (!errors) {
		$("#loadForm").submit();
	}
});

