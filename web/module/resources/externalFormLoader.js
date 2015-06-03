$(document).ready(function() {
	var errors = $("#hasErrors").val();
	if (!errors) {
		$("#loadForm").submit();
	}
});

