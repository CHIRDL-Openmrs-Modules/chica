var english = false;
var formInstance = null;
var numQuestions = 0;

$(document).on("pageinit", function() {
    // Initialize all pages because radio button reset will not work properly.
    $("div[type='question_page']").page();
    
    $(document).ajaxStart(function() {
		showBlockingMessage();
	});

	$(document).ajaxStop(function() {
		$.unblockUI();
	});
	
	// Change the button for both landscape and portrait view
	// The id is different depending on which position the tablet is being held
	// Landscape view causes the list to be too long so jquery automatically opens a new "page"
	// Question 1
	$("#Informant_1-dialog a[data-icon=delete]").buttonMarkup({theme: "b", iconpos: "left", icon: "home"}); // landscape
	$("#Informant_1-dialog").addClass("selectPopup"); // landscape
	$("#Informant_1-listbox-popup div.ui-header").removeClass("ui-bar-b").addClass("ui-bar-a"); // Make the title background black instead of blue
	
	// Question 2
	$("#Informant_2-listbox-popup a[data-icon=delete]").buttonMarkup({theme: "b", iconpos: "left", icon: "home"}); // portrait
	$("#Informant_2-dialog a[data-icon=delete]").buttonMarkup({theme: "b", iconpos: "left", icon: "home"}); // landscape
	$("#Informant_2-listbox-popup").addClass("selectPopup"); // portrait
	$("#Informant_2-listbox-popup div.ui-header").removeClass("ui-bar-b").addClass("ui-bar-a"); // Make the title background black instead of blue
	$("#Informant_2-dialog").addClass("selectPopup"); // landscape
	
	// Spanish Question 1
	$("#Informant_1_2-dialog a[data-icon=delete]").buttonMarkup({theme: "b", iconpos: "left", icon: "home"}); // landscape
	$("#Informant_1_2-dialog").addClass("selectPopup"); // landscape
	$("#Informant_1_2-listbox-popup div.ui-header").removeClass("ui-bar-b").addClass("ui-bar-a"); // Make the title background black instead of blue
	
	// Spanish Question 2
	$("#Informant_2_2-listbox-popup a[data-icon=delete]").buttonMarkup({theme: "b", iconpos: "left", icon: "home"}); // landscape
	$("#Informant_2_2-dialog a[data-icon=delete]").buttonMarkup({theme: "b", iconpos: "left", icon: "home"}); // portrait
	$("#Informant_2_2-listbox-popup").addClass("selectPopup");
	$("#Informant_2_2-listbox-popup div.ui-header").removeClass("ui-bar-b").addClass("ui-bar-a"); // Make the title background black instead of blue
	$("#Informant_2_2-dialog").addClass("selectPopup");
});

$(document).on("pagebeforeshow", "#Informant_1-dialog", function(){
	changeCloseButtonText();
});

$(document).on("pagebeforeshow", "#Informant_1_2-dialog", function(){
	changeCloseButtonText();
});

$(document).on("pagebeforeshow", "#Informant_2-dialog", function(){
	changeCloseButtonText();
});

$(document).on("pagebeforeshow", "#Informant_2_2-dialog", function(){
	changeCloseButtonText();
});

function init(patientName, birthdate, formInst, language) {
	// This looks backwards, but the setLanguage method reverses it.
	if (language.toUpperCase() == "ENGLISH") {
		english = false;
	} else {
		english = true;
	}
	
	setLanguage(patientName, birthdate);
	formInstance = formInst;
	
	var showVitals = window.parent.shouldShowVitalsButton();
	if (!showVitals) {
		$(".vitalsButton").hide();
	}
	numQuestions = $("select[id^='Informant_']").length / 2; // Divide by 2 to handle Spanish version
}

function submitEmptyForm() {
	setLanguageField();
	document.getElementById("AdditionalInformationForm").submit();
}

function setLanguageField() {
	if (english) {
		$("#language").val("english");
	} else {
		$("#language").val("spanish");
	}
}

function setLanguage(patientName, birthdate) {
	english = !english;
    var langButtonText = "Español";
    var startButtonText = "Start";
    var vitalsButtonText = "Vitals";
    var formTitleText = "Additional Information Form:";
	var additionalQuestions = "Please complete some additional information about this visit.";
    if (!english) {
        langButtonText = "English";
        startButtonText = "Comienzo";
        vitalsButtonText = "Vitales";
        formTitleText = "El formulario de información adicional";
		additionalQuestions = "Por favor, complete la información adicional acerca de esta visita a la clínica.";
    }
    $("#confirmLangButton .ui-btn-text").text(langButtonText);
    $("#startButton .ui-btn-text").text(startButtonText);
    $(".vitalsButton .ui-btn-text").text(vitalsButtonText);
    $("#formTitle").text(formTitleText);
	$("#additionalQuestions").text(additionalQuestions);
	changeCloseButtonText();
}

function changePage(newPageNum) {
    var newPage = "#question_page_" + newPageNum;
    if (!english) {
        newPage = newPage + "_sp";
    }
    
    $.mobile.changePage(newPage, { transition: "none", reverse: false });
}

function setLanguageFromForm(patientName, birthdate) {
    setLanguage(patientName, birthdate);
    for (var i = 1; i <= numQuestions; i++) {
    	if (english) {
	    	setQuestionCheckboxes("Informant_" + i + "_2", "Informant_" + i);
	    } else {
	    	setQuestionCheckboxes("Informant_" + i, "Informant_" + i + "_2");
	    }
    }
    
    changePage(1);
}

function attemptFinishForm() {
	concatSelectOption();
	if (areAllQuestionsAnswered()) {
		finishForm();
	}else {
		if (english) {
    	    $("#not_finished_final_dialog").popup("open", { transition: "pop"});
    	} else {
    		$("#not_finished_final_dialog_sp").popup("open", { transition: "pop"});
    	}
	}
}

function finishForm() {
	//run an AJAX post request to your server-side script, $this.serialize() is the data from your form being added to the request
	$("#finish_error_dialog").popup("close");
	$("#finish_error_dialog_sp").popup("close");
	$("#not_finished_final_dialog").popup("close");
	$("#not_finished_final_dialog_sp").popup("close");
	setLanguageField();
	var submitForm = $("#AdditionalInformationForm"); 
	var token = getAuthenticationToken();
    $.ajax({
    	beforeSend: function (xhr) {
		    xhr.setRequestHeader ("Authorization", token );
	    },
        "cache": false,
        "data": submitForm.serialize(),
        "type": "POST",
        "url": submitForm.attr("action"),
        "timeout": 30000, // optional if you want to handle timeouts (which you should)
        "error": handleFinishFormError, // this sets up jQuery to give me errors
        "success": function (xml) {
        	window.parent.closeIframe();
        }
    });
}

function handleFinishFormError() {
	if (english) {
	    $("#finish_error_dialog").popup("open", { transition: "pop"});
	} else {
		$("#finish_error_dialog_sp").popup("open", { transition: "pop"});
	}
}

function setQuestionCheckboxes(initialName, newName) {
	// Determine if any of the select options in the group are selected
	// If so, select the English/Spanish version
	
	if(!$("#"+initialName+"").val() == "" && $("option[name='" + initialName + "']").is(':checked')) {
		var selectedValue = $("#"+initialName+"").val();
		if ($("#"+initialName).prop('multiple')){
			for (var i=0; i<selectedValue.length; i++){
				$("select[name='" + newName + "'] option[value='" + selectedValue[i] + "']").prop("selected",true);
				$("select[name='" + initialName + "'] option[value='" + selectedValue[i] + "']").prop("selected",false);
				$("#"+newName).selectmenu().selectmenu('refresh', true);
				$("#"+initialName).selectmenu().selectmenu('refresh', true);
			}
		} else {
			$("select[name='" + newName + "'] option[value='" + selectedValue + "']").prop("selected",true);
			$("select[name='" + initialName + "'] option[value='" + selectedValue + "']").prop("selected",false);
			$("#"+newName).selectmenu().selectmenu('refresh', true);
			$("#"+initialName).selectmenu().selectmenu('refresh', true);
		}
	}
}
  
function concatSelectOption() {
	var spanishChar = "_2";
	if (english) {
		spanishChar = "";
	} 
	var selectedVal = $("#Informant_2" + spanishChar).val();
	var valueSplit = null;
	if (selectedVal!=null) {
		valueSplit = selectedVal.toString().split(',').join('^^');
	}
	document.getElementById('Visit_Attendee').value = valueSplit;
}

function areAllQuestionsAnswered() {
	var spanishChar = "_2";
	if (english) {
		spanishChar = "";
	}
	for (var i = 1; i <= numQuestions; i++) {
		if ( $("#Informant_" +  i + spanishChar).val() == "" || $("#Informant_" +  i + spanishChar).val() == null ) {
			return false;
		}
	}
	return true;
}

function showBlockingMessage() {
	var message = "Saving Answers...";
	if (!english) {
		message = "Ahorrar Respuestas...";
	}
	
	var blockUIMessage = '<table><tr><td><h3><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif" /></h3></td><td style="white-space: nowrap;vertical-align: center;"><h3>&nbsp;' + message + '</h3></td></tr></table>';
	$.blockUI({ css: { 
        border: "1px solid black", 
        padding: "15px", 
        width: "300px",
        backgroundColor: "#A9A9A9", 
        "-webkit-border-radius": "10px", 
        "-moz-border-radius": "10px", 
        color: "#000" 
    }, 
    message: blockUIMessage});
}

function changeCloseButtonText()
{
	var doneText = "Done";
	if(!english)
	{
		doneText = "Hecho";
	}
    
	$(".selectPopup .ui-header a").attr("title", doneText);
    $(".selectPopup .ui-header a .ui-btn-inner .ui-btn-text").text(doneText); 
}