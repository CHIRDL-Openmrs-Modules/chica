$(document).on("pageshow", "#finished_form", function(){
    $("#finished_dialog").popup("open", { 
        transition: "pop",
    });
});

function finish() {
    $("#loadingDialog").popup("open", { 
        transition: "pop"
    });
    document.getElementById('complete_form').submit();
}

function closeFinishDialog() {
    $("#finished_dialog").popup("close");
}