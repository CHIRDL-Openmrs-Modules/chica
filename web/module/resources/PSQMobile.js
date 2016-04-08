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
	
	numQuestions = $("input[id^='PSQQuestion_']").length / 2; // Divide by 2 to handle Spanish version
}

function submitEmptyForm() {
	setLanguageField();
	document.getElementById("PSQForm").submit();
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
    var additionalQuestions = "Here are a number of questions regarding the behavior of your child during sleep and wakefulness.";
    var instructions = '<p>The questions apply to how your child acts in general, not necessarily during the past few days since these may not have been typical if your child has not been well. If you are not sure how to answer any question, please feel free to ask your husband or wife, child, or physician for help. When you see the word “usually” it means “more than half the time” or “on more than half the nights.”</p>';
    var startButtonText = "Start";
    var vitalsButtonText = "Vitals";
    var formTitleText = "PEDIATRIC SLEEP QUESTIONNAIRE";
    if (!english) {
        langButtonText = "English";
        additionalQuestions = "Por favor, conteste las preguntas del siguiente cuestionario acerca de la conducta de su hijo/a durante el sueño y también cuando está despierto/a.";
        instructions = '<p>Las preguntas se refieren a la conducta de su hijo/a en general y no únicamente en los últimos días, ya que en ese caso, la conducta de su hijo/a, puede haber estado algo alterada si él o ella no se encuentran bien estos últimos días por cualquier motivo. Si hay alguna pregunta que no sabe como contestar, por favor consulte con su mujer, su marido, su hijo/a o con su médico.  Cuando en alguna pregunta Vd lea “habitualmente”, quiere decir “más de la mitad de las veces” o “más de la mitad de las noches”.</p>';
        startButtonText = "Comienzo";
        vitalsButtonText = "Vitales";
        formTitleText = "CUESTIONARIO PEDIATRICO DE SUEÑO:";
    }
    
    $("#confirmLangButton .ui-btn-text").text(langButtonText);
    $("#additionalQuestions").text(additionalQuestions);
    $("#instructions").html(instructions);
    $("#startButton .ui-btn-text").text(startButtonText);
    $(".vitalsButton .ui-btn-text").text(vitalsButtonText);
    $("#formTitle").text(formTitleText);
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
    
    // Transfer the answers for the questions
    for (var i = 1; i <= numQuestions; i++) {
    	if (english) {
	    	setQuestionCheckboxes("PSQQuestionEntry_" + i + "_2", "PSQQuestionEntry_" + i);
	    } else {
	    	setQuestionCheckboxes("PSQQuestionEntry_" + i, "PSQQuestionEntry_" + i + "_2");
	    }
    }
        
    changePage(1);
}

function attemptFinishForm() {
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
	var submitForm = $("#PSQForm"); 
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
	// Determine if any of the radio buttons in the group are selected
	// If so, select the English/Spanish version
	if($("input[name='" + initialName + "']").is(':checked')) {
		var selectedValue = $("input[name='" + initialName + "']:checked").val();
		
		// Select the radio button by name and value
		$("input[name='" + newName + "'][value='" + selectedValue + "']").prop("checked",true);
		$("input[name='" + initialName + "'][value='" + selectedValue + "']").prop("checked",false);
		$("input[name='" + newName + "'][value='" + selectedValue + "']").checkboxradio('refresh');
		$("input[name='" + initialName + "'][value='" + selectedValue + "']").checkboxradio('refresh');
	}
}

function areAllQuestionsAnswered() {
	var spanishChar = "_2";
	if (english) {
		spanishChar = "";
	}
	

	var questionName = "PSQQuestionEntry_";
	for (var i = 1; i <= numQuestions; i++) {
		if(!$("input[name='" + questionName + i + spanishChar + "']").is(':checked')){
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