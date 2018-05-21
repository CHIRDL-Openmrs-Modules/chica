var english = false;
var formInstance = null;
var numQuestions = 0;
var formElementId = ""; // This is the <form> element id, example "SUDEPForm"
var questionPrefix = ""; // Used to determine the number of questions on the page, example "SUDEPQuestion_" would be passed into init
var questionEntryPrefix = ""; // Used to transfer answers when toggling between English/Spanish, example "SUDEPQuestionEntry_"

// CHICA-1226 Moved function into common .js file
// Call this from within the "pageinit"
// See SUDEPMobile.js and SUDEPMobile.jsp for examples
// NOTE: This requires a consistent naming for all question_page elements
function pageInit()
{
	// Initialize all pages because radio button reset will not work properly.
    $("div[type='question_page']").page();
    
    $(document).ajaxStart(function() {
		showBlockingMessage();
	});

	$(document).ajaxStop(function() {
		$.unblockUI();
	});
        
    // This is for the OK button on the passcode dialog from sharedMobile.jsp
    $("#quit_passcode_ok_button").click(function() {
        parent.completeForm();
    });
}

// CHICA-1226 Moved function into common .js file
// Shows the blocking message when the form is submitted
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

// CHICA-1226 Moved function into common .js file
// Initializes english, numQuestions, etc variables
function init(patientName, birthdate, formInst, language, formId, prefix, entryPrefix) {
	// This looks backwards, but the setLanguage method reverses it.
	if (language.toUpperCase() == "ENGLISH") {
		english = false;
	} else {
		english = true;
	}
	
	setLanguage(patientName, birthdate);
	formInstance = formInst;
	
	numQuestions = $("input[id^='" + prefix + "']").length / 2; // Divide by 2 to handle Spanish version
	
	formElementId = formId;
	questionPrefix = prefix;
	questionEntryPrefix = entryPrefix;
}

// CHICA-1226 Moved function into common .js file
// Checks to see if all questions have been answered and finishes the form if they have all been answered
// See SUDEPMobile.js and SUDEPMobile.jsp for examples
// NOTE: This version is to be used when the form is to be filled out by the care giver
// We have a separate version for adolescent. I have not added it as part of this ticket.
// Also NOTE: A custom version of this may need to be included in the eJIT .js file, but this
// version will work in most cases
function attemptFinishForm() {
	if (areAllQuestionsAnswered()) {
		finishForm(); 
	} else{
		if (english) {
    	    $("#not_finished_dialog").popup("open", { transition: "pop"});
    	} else {
    		$("#not_finished_dialog_sp").popup("open", { transition: "pop"});
    	}
	}
}

// CHICA-1226 Moved function into common .js file
// Displays error dialog if there was an error submitting the form (example, when a timeout occurs)
// See SUDEPMobile.js and SUDEPMobile.jsp for examples
function handleFinishFormError() {
	if (english) {
	    $("#finish_error_dialog").popup("open", { transition: "pop"});
	} else {
		$("#finish_error_dialog_sp").popup("open", { transition: "pop"});
	}
}

// CHICA-1226 Moved function into common .js file
// Changes page, must pass in page number and english variable from jsp and the .js file
// Make sure both files pass in the proper parameters
// See SUDEPMobile.js and SUDEPMobile.jsp for examples	
function changePage(newPageNum) {	

    var newPage = "#question_page_" + newPageNum;
    if (!english) {
        newPage = newPage + "_sp";
    }
    
    $.mobile.changePage(newPage, { transition: "none", reverse: false });
}

// CHICA-1226 Moved function into common .js file
// Sets the hidden input language field and submits the form
function submitEmptyForm() {
	setLanguageField();
	document.getElementById(formElementId).submit();
}

// CHICA-1226 Moved function into common .js file
// Sets the hidden input language field
function setLanguageField() {
	if (english) {
		$("#language").val("english");
	} else {
		$("#language").val("spanish");
	}
}

// CHICA-1226 Moved function into common .js file
// Sets the language and transfers the answers
function setLanguageFromForm(patientName, birthdate) {	
    setLanguage(patientName, birthdate);
    
    // Transfer the answers for the questions
    for (var i = 1; i <= numQuestions; i++) {
    	if (english) {
	    	setQuestionCheckboxes(questionEntryPrefix + i + "_2", questionEntryPrefix + i);
	    } else {
	    	setQuestionCheckboxes(questionEntryPrefix + i, questionEntryPrefix + i + "_2");
	    }
    }
        
    changePage(1);
}

// Sets english variable, title, instructions, and buttons text
function setLanguage(patientName, birthdate) {
	
	setEnglish();
	
	setTitleText();
	
	setAdditionalQuestionsText();
	
	setInstructionsHTML();

    setLanguageForButtons();
}

// Changes value of the english variable
function setEnglish()
{
	english = !english;
}

// Toggles the instructions between English/Spanish
// by changing the text for the "additionalQuestions" element
function setAdditionalQuestionsText()
{
	if(english){
		$("#additionalQuestions").text($("#instructions_additionalQuestions").val());
	}
	else{
		$("#additionalQuestions").text($("#instructions_additionalQuestions_sp").val());
	}
}

// Toggles the instructions between English/Spanish
// by changing the text for the "instructions" element
// NOTE: This element is not required, so we'll check to see if it exists
// on the page before attempting to change the text
function setInstructionsHTML()
{
	// Check to see if the section exists
	if($("#instructions").length)
	{
		if(english){
			$("#instructions").html($("#instructions_part2").val());
		}
		else{
			$("#instructions").html($("#instructions_part2_sp").val());
		}
	}
}

// Toggles the form header between English/Spanish
// by changing the text for the "formTitle" element
function setTitleText()
{
	if(english){
		$("#formTitle").text($("#formNameHeader").val());
	}
	else{
		$("#formTitle").text($("#formNameHeader_sp").val());
	}
}

// This function must be called after the setLanguage() function in the eJIT .js
// This sets the text for the English/Spanish and Staff buttons
function setLanguageForButtons()
{
	var langButtonText = "Español";
	var startButtonText = "Start";
	var quitButtonText = "Quit";
	var skipButtonText = "No Parent";
	
	if (!english) {
        langButtonText = "English";
        startButtonText = "Comienzo";
        quitButtonText = "Dejar";
        skipButtonText = "No Padre";
    }
	
	$("#confirmLangButton .ui-btn-text").text(langButtonText);
    $("#startButton .ui-btn-text").text(startButtonText);
    $(".quitButton .ui-btn-text").text(quitButtonText);
    $("#skipButton .ui-btn-text").text(skipButtonText);
}

// CHICA-1226 Moved function into common .js file
// Checks to see if all questions have been answered
// NOTE: A custom version of this may need to be included in the eJIT .js
// file if there are special situations to handle such as skip logic
// that doesn't require all questions to be displayed
function areAllQuestionsAnswered() {
	var spanishChar = "_2";
	if (english) {
		spanishChar = "";
	}
	
	for (var i = 1; i <= numQuestions; i++) {
		if(!$("input[name='" + questionEntryPrefix + i + spanishChar + "']").is(':checked')){
		   return false;
		}
	}
	
	return true;
}

// CHICA-1226 Moved function into common .js file
// Function to transfer answers when toggling between English/Spanish
// Note: This is a generic function and will work for MOST cases, but you 
// may need to create a more specific function in the eJIT .js file
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

// CHICA-1226 Moved function into common .js file
// Submits the form, also calls the function to perform any scoring that might be necessary
function finishForm() {
	//run an AJAX post request to your server-side script, $this.serialize() is the data from your form being added to the request
	$("#finish_error_dialog").popup("close");
	$("#finish_error_dialog_sp").popup("close");
	$("#not_finished_dialog").popup("close");
	$("#not_finished_dialog_sp").popup("close");
	setLanguageField(english);
	calculateScore(); // This function can be left empty if there is nothing to score
	var submitForm = $("#"+formElementId); 
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
        "error": function(){
        	handleFinishFormError(english); // this sets up jQuery to give me errors
        },
        "success": function (xml) {
        	window.parent.closeIframe();
        }
    });
}

//Insert choices for Yes/No
function insertYesNo(prefix, questionNumber, isSpanish)
{
	var choiceYes = "Yes";
	var choiceNo = "No";
	
	if(isSpanish)
	{
		choiceYes = "S&iacute;";
		questionNumber = questionNumber + "_2";
	}
	
	var fieldSetElement = $(document.createElement("fieldset"));
	fieldSetElement.attr({
		"data-role": "controlgroup",
		"data-type": "horizontal"
		});
	
	var fieldSet = '';
	
	fieldSet += '<input type="radio" name="' + prefix + questionNumber + '" id="' + prefix + questionNumber + '_Yes" value="yes" data-theme="c" />';
	fieldSet += '<label for="' + prefix + questionNumber + '_Yes">' + choiceYes + '</label>';
	fieldSet += '<input type="radio" name="' + prefix + questionNumber + '" id="' + prefix + questionNumber + '_No" value="no" data-theme="c" />';
	fieldSet += '<label for="' + prefix + questionNumber + '_No">' + choiceNo + '</label>';
	
	fieldSetElement.append(fieldSet);
	
	$(".choice_"+questionNumber).append(fieldSetElement);
	$(".choice_"+questionNumber).triggerHandler("create");
}

function confirmSkipForm()
{
	if (english) {
	    $("#skip_form_dialog").popup("open", { transition: "pop"});
	} else {
		$("#skip_form_dialog_sp").popup("open", { transition: "pop"});
	}
}