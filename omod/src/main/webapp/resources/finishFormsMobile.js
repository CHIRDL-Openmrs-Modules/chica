$(document).on("pagebeforeshow", "#finished_form", function() {
    startSubmitTimer();
});

$(document).on("pageshow", "#finished_form", function(){
	if ($("#userQuitForm").val() !== "true") {
	    $("#finished_dialog").popup("open", { 
	        transition: "pop",
	    });
	}
});

/**
 * Timer to automatically submit the page after 15 minutes of inactivity.
 */
function startSubmitTimer() {
    var timer = $.timer(function () {
    	finish();
    });

    timer.set({
        time: 900000,
        autostart: true
    });
}

function finish() {
    $("#loadingDialog").popup("open", { 
        transition: "pop"
    });
    
    document.getElementById("complete_form").submit();
}

function closeFinishDialog() {
    $("#finished_dialog").popup("close");
}