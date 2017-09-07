var english = false;
var formInstance = null;
var ntilde = "\xF1";
var eacute = "\xE9";
var aacute = "\xE1";
var Ntilde = "\xD1";
var oacute = "\xF3";
var openParen = "\x28";
var closeParen = "\x29";
var comma = "\x2C";
var numberOfQuestions = 20;
var questionMark ="&#63";

 

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
	

}

function submitEmptyForm() {
	setLanguageField();
	document.getElementById("TRAQForm").submit();
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
	//Strings with spanish characters need hex code
	//HTML with spanish characters can use HTML codes
	
    var langButtonText = "Espa" + ntilde + "ol";
    var instructions = "<p>Please check the box that best describes your skill level in the following areas that "
    	+ "are important for transition to adult health care. There is no right or wrong answer and your answers  "
    	+ "will remain confidential and private.</p>";
    var startButtonText = "Start";
    var vitalsButtonText = "Staff";
    var formTitleText = "Transition Readiness Assessment Questionnaire " + openParen + "TRAQ" + closeParen;
    if (!english) {
        langButtonText = "English"; 
         instructions = "<p>Por favor marc" + aacute + " con una cruz la opci" + oacute + "n que mejor describa tu "
         + "capacidad para cada una de las siguientes áreas que son importantes para la transici" + oacute + "n del "
         + "cuidado de tu salud a la medicina del adulto. No hay respuestas correctas ni incorrectas y las respuestas "
         + "ser" + aacute + "n confidenciales y privadas</p>";
        startButtonText = "Comienzo";
        vitalsButtonText = "Personal";
        formTitleText = "Cuestionario de Evaluaci" + oacute + "n para la Preparaci" + oacute + "n de la Transici" 
        + oacute + "n " + openParen + "TRAQ" + closeParen;
    }
    
    $("#confirmLangButton .ui-btn-text").text(langButtonText);
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
    
    // Transfer the answers for the 10 questions
    for (var i = 1; i < numberOfQuestions; i++) {
    	if (english) {
	    	setQuestionCheckboxes("TRAQQuestionEntry_" + i + "_2", "TRAQQuestionEntry_" + i);
	    } else {
	    	setQuestionCheckboxes("TRAQQuestionEntry_" + i, "TRAQQuestionEntry_" + i + "_2");
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
	var submitForm = $("#TRAQForm"); 
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
	
	var questionName = "TRAQQuestionEntry_";
	for (var i = 1; i < 11; i++) {
		if(!$("input[name='" + questionName + i + spanishChar + "']").is(':checked')){
		   return false;
		}
	}
	
	return true;
}

function insertChoices1(){
	
	var choiceDoNotKnow = "No, I do not know how.";
	var choiceNoButWantToLearn = "No, but I want to learn.";
	var choiceNoButLearning = "No, but I am learning to do this.";
	var choiceYesStarted = "I have started doing this.";
	var choiceYesAlways = "Yes, I always do this when I need to.";
	if (!english) {
		choiceDoNotKnow  = "No, no s" + eacute + " c" + oacute + "mo hacerlo.";
		choiceNoButWantToLearn  = "No, pero quiero aprender a hacerlo."; 
		choiceNoButLearning  = "No, pero estoy aprendiendo a hacerlo."; 
		choiceYesStarted  = "Si, ya he comendzado a hacerlo."; 
		choiceYesAlways  = "Si, lo hago siempre que lo necesito."; 
	}
	
	
	var $label1 = $("<label>").text( choiceDoNotKnow);
	var $label2 = $("<label>").text( choiceNoButWantToLearn);
	var $label3 = $("<label>").text( choiceNoButLearning);
	var $label4 = $("<label>").text( choiceYesStarted);
	var $label5 = $("<label>").text( choiceYesAlways);
	
	var $input1 = $('<input type="radio">').attr(
			{	id: 'TRAQQuestionEntry_${QNumber}_DO_NOT_KNOW',
				name: 'TRAQQuestionEntry_${QNumber}_DO_NOT_KNOW',
				value: '1'
			});
	var $input2 = $('<input type="radio">').attr(
			{	id: 'TRAQQuestionEntry_${QNumber}_WANT_TO_LEARN',
				name: 'TRAQQuestionEntry_${QNumber}_WANT_TO_LEARN',
				value: '2'
			});
	var $input3 = $('<input type="radio">').attr(
			{	id: 'TRAQQuestionEntry_${QNumber}_LEARNING',
				name: 'TRAQQuestionEntry_${QNumber}_LEARNING',
				value: '3'
			});
	var $input4 = $('<input type="radio">').attr(
			{	id: 'TRAQQuestionEntry_${QNumber}_STARTED',
				name: 'TRAQQuestionEntry_${QNumber}_STARTED',
				value: '4'
			});
	var $input5 = $('<input type="radio">').attr(
			{	id: 'TRAQQuestionEntry_${QNumber}_ALWAYS',
				name: 'TRAQQuestionEntry_${QNumber}_ALWAYS',
				value: '5'
			});
	$input1.appendTo($label1);
	$input2.appendTo($label2);
	$input3.appendTo($label3);
	$input4.appendTo($label4);
	$input5.appendTo($label5);
	$(".choices").append($label1);
	$(".choices").append($label2);
	$(".choices").append($label3);
	$(".choices").append($label4);
	$(".choices").append($label5);
	$(".choices").trigger("create");

	
}



function insertChoices_2(){
	
	var choiceDoNotKnow = "No, I do not know how.";
	var choiceNoButWantToLearn = "No, but I want to learn.";
	var choiceNoButLearning = "No, but I am learning to do this.";
	var choiceYesStarted = "I have started doing this.";
	var choiceYesAlways = "Yes, I always do this when I need to.";
	if (!english) {
		choiceDoNotKnow  = "No, no s" + eacute + " c" + oacute + "mo hacerlo.";
		choiceNoButWantToLearn  = "No, pero quiero aprender a hacerlo."; 
		choiceNoButLearning  = "No, pero estoy aprendiendo a hacerlo."; 
		choiceYesStarted  = "Si, ya he comendzado a hacerlo."; 
		choiceYesAlways  = "Si, lo hago siempre que lo necesito."; 
	}
	
	
	fieldSetElement = $(document.createElement("fieldset"));
	fieldSetElement.attr({
		"data-role": "controlgroup",
		"data-type": "vertical"
	});
	
	var fieldSet = '';
	fieldSet += '<fieldset data-role="controlgroup" data-type="vertical">';
	fieldSet += '<input type="radio" name="TRAQQuestionEntry_\$\{QNumber\}" id="TRAQQuestionEntry_\$\{QNumber\}_DO_NOT_KNOW" value="1" data-theme="b" />';
	fieldSet += '<label for="TRAQQuestionEntry_\$\{QNumber\}_DO_NOT_KNOW">No, I do not know how.</label>';
	fieldSet += '<input type="radio" name="TRAQQuestionEntry_\$\{QNumber\}" id="TRAQQuestionEntry_\$\{QNumber\}_WANT_TO_LEARN" value="2" data-theme="b" />';
	fieldSet += '<label for="TRAQQuestionEntry_\$\{QNumber\}_WANT_TO_LEARN">No, but I want to learn.</label>';
	fieldSet += '<input type="radio" name="TRAQQuestionEntry_\$\{QNumber\}" id="TRAQQuestionEntry_\${\QNumber\}_LEARNING" value="3" data-theme="b" />';
    fieldSet += '<label for="TRAQQuestionEntry_\$\{QNumber\}_LEARNING">No, but I am learning to do this.</label>';
    fieldSet += '<input type="radio" name="TRAQQuestionEntry_${QNumber}" id="TRAQQuestionEntry_${QNumber}_STARTED" value="4" data-theme="b" />';
    fieldSet += '<label for="TRAQQuestionEntry_${QNumber}_STARTED">Yes, I have started doing this.</label>';
    fieldSet += '<input type="radio" name="TRAQQuestionEntry_${QNumber}" id="TRAQQuestionEntry_${QNumber}_ALWAYS" value="5" data-theme="b" />';
    fieldSet += '<label for="TRAQQuestionEntry_${QNumber}_ALWAYS">Yes, I always do this when I need to.</label>';
	fieldSet += '</fieldset>'
    
    fieldSetElement.append(fieldSet);
   
	$(".choices").append(fieldSetElement);
	$(".choices").trigger("create");
	
	
}

function insertChoices(questionNumber){
	var spanishExtension = "_2";
	var choiceDoNotKnow = "No, I do not know how.";
	var choiceNoButWantToLearn = "No, but I want to learn.";
	var choiceNoButLearning = "No, but I am learning to do this.";
	var choiceYesStarted = "I have started doing this.";
	var choiceYesAlways = "Yes, I always do this when I need to.";
	if (questionNumber.endsWith(spanishExtension)) {
		choiceDoNotKnow  = "No, no s" + eacute + " c" + oacute + "mo hacerlo.";
		choiceNoButWantToLearn  = "No, pero quiero aprender a hacerlo."; 
		choiceNoButLearning  = "No, pero estoy aprendiendo a hacerlo."; 
		choiceYesStarted  = "Si, ya he comendzado a hacerlo."; 
		choiceYesAlways  = "Si, lo hago siempre que lo necesito."; 
	}
	
	fieldSetElement = $(document.createElement("fieldset"));
	fieldSetElement.attr({
		"data-role": "controlgroup",
		"data-type": "vertical"
	});
	
	var fieldSet = '';
	
	fieldSet += '<input type="radio" name="TRAQQuestionEntry_' + questionNumber + '" id="TRAQQuestionEntry_' + questionNumber + '_DO_NOT_KNOW" value="1" data-theme="b" />';
	fieldSet += '<label for="TRAQQuestionEntry_' + questionNumber + '_DO_NOT_KNOW">' + choiceDoNotKnow + '</label>';
	fieldSet += '<input type="radio" name="TRAQQuestionEntry_' + questionNumber +'" id="TRAQQuestionEntry_' + questionNumber + '_WANT_TO_LEARN" value="2" data-theme="b" />';
	fieldSet += '<label for="TRAQQuestionEntry_' + questionNumber + '_WANT_TO_LEARN">' + choiceNoButWantToLearn + '</label>';
	fieldSet += '<input type="radio" name="TRAQQuestionEntry_' + questionNumber + '" id="TRAQQuestionEntry_' + questionNumber + '_LEARNING" value="3" data-theme="b" />';
    fieldSet += '<label for="TRAQQuestionEntry_' + questionNumber + '_LEARNING">' + choiceNoButLearning + '</label>';
    fieldSet += '<input type="radio" name="TRAQQuestionEntry_' + questionNumber + '" id="TRAQQuestionEntry_' + questionNumber + '_STARTED" value="4" data-theme="b" />';
    fieldSet += '<label for="TRAQQuestionEntry_' + questionNumber + '_STARTED">' + choiceYesStarted + '</label>';
    fieldSet += '<input type="radio" name="TRAQQuestionEntry_' + questionNumber + '" id="TRAQQuestionEntry_' + questionNumber + '_ALWAYS" value="5" data-theme="b" />';
    fieldSet += '<label for="TRAQQuestionEntry_' + questionNumber + '_ALWAYS">' + choiceYesStarted + '</label>';
	
    
    fieldSetElement.append(fieldSet);
   
	$(".choice"+questionNumber).append(fieldSetElement);
	$(".choice"+questionNumber).trigger("create");
	
	
}


function showBlockingMessage() {
	var message = "Saving Answers...";
	if (!english) {
		message = "Ahorrar Respuestas...";
	}
	
	var blockUIMessage = '<table><tr><td><h3><img src="' + ctx + '/moduleResources/chica/images/ajax-loader.gif" /></h3></td><td style="white-space: nowrap;vertical-align: center;"><h3>&nbsp;' + message + '</h3></td></tr></table>';
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