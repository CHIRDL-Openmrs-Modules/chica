$(document).on("pagebeforeshow", "#finished_form", function() {
    startSubmitTimer();
});


/**
 * Timer to automatically submit the page after 5 minutes of inactivity.
 */
function startSubmitTimer() {
    var timer = $.timer(function () {
    	finish();
    });

    timer.set({
        time: 600000,
        autostart: true
    });
}

function finish() {
	$("#loadingDialog").popup("open", { transition: "pop"});
	document.getElementById('complete_form').submit();
}