var english = false;
var formInstance = null;
var numQuestions = 0;
var finishAttempts = 0;
var formTitleText = "";

var openParen = "&#40";
var closeParen = "&#41";
var nTilde = "&#241";
var colon = "&#58";
var comma = "&#44";
var slash = "&#47";
var semicolon = "&#58";
var questionMark = "&#63";
var apostrophe = "&#39";
var oAcute = "&#243";
var aAcute = "&#225";
var eAcute = "&#233";
var uAcute = "&#250";
var iAcute = "&#237";
var invQuestionMark = "&#191";
var copyrightSymbol = "&#169";
var ampersand = "&#38";
var hyphen = "$#45";
var NTilde = "&#181";
var period = "&#46";


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
	numQuestions = $("input[id^='DiabHistQuestion_']").length / 2; 

	$("#question_shots_container").hide();
	$("#question_shots_container_sp").hide();
	$("#question_insulin_pump_container").hide();
	$("#question_insulin_pump_container_sp").hide();
	
	$('#NextNoInsulin').hide();
	$('#PreviousNoInsulin').hide();
	$('#NextNoInsulin_sp').hide();
	$('#PreviousNoInsulin_sp').hide();

	$("#DiabetesHistory_2_SHOTS, #DiabetesHistory_2_2_SHOTS").click(function() {
		$("#question_insulin_pump_container").hide();
		$("#question_insulin_pump_container_sp").hide();
		$("#question_shots_container").show();
		$("#question_shots_container_sp").show();
		
		$("#DiabetesHistory_12_YES").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_12_NO").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		
		$("#DiabetesHistory_12_2_YES").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_12_2_NO").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		
		$('#Next').show();
		$('#NextNoInsulin').hide();
		$('#Next_sp').show();
		$('#NextNoInsulin_sp').hide();
		$('#Previous').show();
		$('#PreviousNoInsulin').hide();
		$('#Previous_sp').show();
		$('#PreviousNoInsulin_sp').hide();
		$("#question_hypoglycemia_container").show();
		$("#question_hypoglycemia_container_sp").show();
	});
	
	$("#DiabetesHistory_2_NOT_ON_INSULIN, #DiabetesHistory_2_2_NOT_ON_INSULIN").click(function() {
		$("#question_shots_container").hide();
		$("#question_shots_container_sp").hide();
		$("#question_insulin_pump_container").hide();
		$("#question_insulin_pump_container_sp").hide();
		
		$("#DiabetesHistory_3_YES").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_3_NO").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_4_YES").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_4_NO").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_5_YES").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_5_NO").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_6_YES").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_6_NO").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_7_YES").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_7_NO").prop("checked", false).checkboxradio('refresh');
		
		$("#DiabetesHistory_10_ME").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_PARENT_OTHER").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_ALL").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_12_YES").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_12_NO").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		
		$("#DiabetesHistory_3_2_YES").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_3_2_NO").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_4_2_YES").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_4_2_NO").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_5_2_YES").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_5_2_NO").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_6_2_YES").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_6_2_NO").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_7_2_YES").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_7_2_NO").prop("checked", false).checkboxradio('refresh');
		
		$("#DiabetesHistory_10_2_ME").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_2_PARENT_OTHER").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_2_ALL").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_12_2_YES").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_12_2_NO").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		
		$('#Next').hide();
		$('#NextNoInsulin').show();
		$('#Next_sp').hide();
		$('#NextNoInsulin_sp').show();
		$('#Previous').hide();
		$('#PreviousNoInsulin').show();
		$('#Previous_sp').hide();
		$('#PreviousNoInsulin_sp').show();
		
		$("#question_hypoglycemia_container").hide();
		$("#question_hypoglycemia_container_sp").hide();
	});

	$("#DiabetesHistory_2_INSULIN_PUMP, #DiabetesHistory_2_2_INSULIN_PUMP").click(function() {
		$("#question_shots_container").hide();
		$("#question_shots_container_sp").hide();
		$("#question_insulin_pump_container").show();
		$("#question_insulin_pump_container_sp").show();
		
		$("#DiabetesHistory_10_ME").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_PARENT_OTHER").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_ALL").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		
		$("#DiabetesHistory_10_2_ME").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_2_PARENT_OTHER").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_2_ALL").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		
		$('#Next').show();
		$('#NextNoInsulin').hide();
		$('#Next_sp').show();
		$('#NextNoInsulin_sp').hide();
		$('#Previous').show();
		$('#PreviousNoInsulin').hide();
		$('#Previous_sp').show();
		$('#PreviousNoInsulin_sp').hide();
		$("#question_hypoglycemia_container").show();
		$("#question_hypoglycemia_container_sp").show();
	});
}

function submitEmptyForm() {
	setLanguageField();
	document.getElementById("DiabetesHistoryForm").submit();
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
	
	var formTitleText = "Diabetes History Questionnaire";
    var langButtonText = "Español"; 
    var startButtonText = "Start";
    var vitalsButtonText = "Staff";
    var instructions = "The following are some additional questions about diabetes history.";
	
    if (!english) {
		formTitleText = "Spanish";
        langButtonText = "English";
        startButtonText = "Comienzo";
        vitalsButtonText = "Personal";
		instructions = "The following are some additional questions about diabetes history(SPANISH).";
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
	
	 for (var i = 1; i <= numQuestions; i++) {
		if (english) {
			setQuestionCheckboxes("DiabetesHistory_" + i + "_2", "DiabetesHistory_" + i);
		} else {
			setQuestionCheckboxes("DiabetesHistory_" + i, "DiabetesHistory_" + i + "_2");
		}
    }
    changePage(1);
}

function attemptFinishForm() {
	finishAttempts++;
	if (areAllQuestionsAnswered()) {
		finishForm();
	} else if (finishAttempts == 1) {
    	if (english) {
    	    $("#not_finished_dialog").popup("open", { transition: "pop"});
    	} else {
    		$("#not_finished_dialog_sp").popup("open", { transition: "pop"});
    	}
	} else if (finishAttempts >= 2) {
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
	$("#not_finished_dialog").popup("close");
	$("#not_finished_dialog_sp").popup("close");
	setLanguageField();
	evaluateDHInterpretations();
	var submitForm = $("#DiabetesHistoryForm"); 
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
	var spanishExtension = "_2";
	if (english) {
		spanishExtension = "";
	}

	var questionName = "DiabetesHistory_";
	var noInsulin = false;
	var shots = false;
	var pump = false;
	
	var insulinMethod = ["DiabetesHistory_2" + spanishExtension];
	var noInsulinArray = ["DiabetesHistory_3" + spanishExtension, "DiabetesHistory_4" + spanishExtension, "DiabetesHistory_5" + spanishExtension, "DiabetesHistory_6" + spanishExtension, "DiabetesHistory_7" + spanishExtension, "DiabetesHistory_10" + spanishExtension, "DiabetesHistory_11" + spanishExtension, "DiabetesHistory_12" + spanishExtension, "DiabetesHistory_13" + spanishExtension];
	var shotsArray = ["DiabetesHistory_12" + spanishExtension, "DiabetesHistory_13" + spanishExtension];
	var pumpArray = ["DiabetesHistory_10" + spanishExtension,"DiabetesHistory_11" + spanishExtension];
	
	for (var i = 1; i <= numQuestions; i++) {
		var choiceName = questionName + i + spanishExtension;

		if ($.inArray(choiceName, insulinMethod ) > -1 ) {
			var value =  $(":radio[name='" + choiceName + "']:checked").val(); 
			if (value == 1) {
				noInsulin = true;
			} else if (value == 2) {
				shots = true;
			} else if (value == 3) {
				pump = true;
			}
		}
		
		if ($.inArray(choiceName, noInsulinArray ) > -1  && noInsulin) {
			if (!noInsulin) { 
				if(!$("input[name='" + choiceName + "']").is(':checked')){
					return false;
				}
			}
		} else if ($.inArray(choiceName, shotsArray ) > -1 && shots) {
			if (!shots) {
				if(!$("input[name='" + choiceName + "']").is(':checked')){
					return false;
				}
			}
		} else if ($.inArray(choiceName, pumpArray ) > -1 && pump) {
			if (!pump) {
				if(!$("input[name='" + choiceName + "']").is(':checked')){
					return false;
				}
			}
		} else {
			if(!$("input[name='" + choiceName + "']").is(':checked')){
				return false;
			}
		}
	}
	return true;
}

function isSpanishQuestion(questionNumber){
	var spanishExtension = "_2";
	
	if (questionNumber.length <= spanishExtension.length){
		return false;
	}
	return (questionNumber.lastIndexOf(spanishExtension) == (questionNumber.length - spanishExtension.length));
}

function insertYESNO(questionNumber){
		
	var choiceYes = "Yes";
	var choiceNo = "No";
	
	if (isSpanishQuestion(questionNumber)){ 
		choiceYes  = "Si";
		choiceNo  = "No"; 
	}
	
	fieldSetElement = $(document.createElement("fieldset"));
	fieldSetElement.attr({
		"data-role": "controlgroup",
		"data-type": "horizontal"
	});
	
	var fieldSet = '';
	
	fieldSet += '<input type="radio" name="DiabetesHistory_' + questionNumber + '" id="DiabetesHistory_' + questionNumber + '_YES" value="yes" data-theme="c" />';
	fieldSet += '<label for="DiabetesHistory_' + questionNumber + '_YES">' + choiceYes + '</label>';
	fieldSet += '<input type="radio" name="DiabetesHistory_' + questionNumber +'" id="DiabetesHistory_' + questionNumber + '_NO" value="no" data-theme="c" />';
	fieldSet += '<label for="DiabetesHistory_' + questionNumber + '_NO">' + choiceNo + '</label>';
		
    fieldSetElement.append(fieldSet);
   
	$(".choice"+questionNumber).append(fieldSetElement);
	$(".choice"+questionNumber).triggerHandler("create");
}

function insertInsulinMethodChoices(questionNumber) {

	var choiceNotOnInsulin = "I am not on insulin";
	var choiceShots = "Shots";
	var choiceInsulinPump = "Insulin pump";

	if (isSpanishQuestion(questionNumber)){
		choiceNotOnInsulin  = "spanish";
		choiceShots = "spanish";
		choiceInsulinPump  = "spanish"; 
	}

	fieldSetElement = $(document.createElement("fieldset"));
	fieldSetElement.attr({
		"data-role": "controlgroup",
		"data-type": "vertical"
	});

	var fieldSet = '';
	
	fieldSet += '<input type="radio" name="DiabetesHistory_' + questionNumber + '" id="DiabetesHistory_' + questionNumber + '_NOT_ON_INSULIN" value="1" data-theme="b" />';
	fieldSet += '<label for="DiabetesHistory_' + questionNumber + '_NOT_ON_INSULIN">' + choiceNotOnInsulin + '</label>';
	fieldSet += '<input type="radio" name="DiabetesHistory_' + questionNumber + '" id="DiabetesHistory_' + questionNumber + '_SHOTS" value="2" data-theme="b" />';
	fieldSet += '<label for="DiabetesHistory_' + questionNumber + '_SHOTS">' + choiceShots + '</label>';
	fieldSet += '<input type="radio" name="DiabetesHistory_' + questionNumber + '" id="DiabetesHistory_' + questionNumber + '_INSULIN_PUMP" value="3" data-theme="b" />';
	fieldSet += '<label for="DiabetesHistory_' + questionNumber + '_INSULIN_PUMP">' + choiceInsulinPump + '</label>';
		
    fieldSetElement.append(fieldSet);
   
	$(".choice"+questionNumber).append(fieldSetElement);
	$(".choice"+questionNumber).triggerHandler("create");
}

function insertWhoGivesInsulinChoices(questionNumber) {
	var choiceMe = "Me";
	var choiceParentOrOther = "A parent / someone else";
	var choiceAll = "Both me and a parent/someone else";

	if (isSpanishQuestion(questionNumber)){
		choiceMe  = "spanish";
		choiceParentOrOther = "spanish";
		choiceAll  = "spanish"; 
	}

	fieldSetElement = $(document.createElement("fieldset"));
	fieldSetElement.attr({
		"data-role": "controlgroup",
		"data-type": "vertical"
	});

	var fieldSet = '';
	
	fieldSet += '<input type="radio" name="DiabetesHistory_' + questionNumber + '" id="DiabetesHistory_' + questionNumber + '_ME" value="1" data-theme="b" />';
	fieldSet += '<label for="DiabetesHistory_' + questionNumber + '_ME">' + choiceMe + '</label>';
	fieldSet += '<input type="radio" name="DiabetesHistory_' + questionNumber + '" id="DiabetesHistory_' + questionNumber + '_PARENT_OTHER" value="2" data-theme="b" />';
	fieldSet += '<label for="DiabetesHistory_' + questionNumber + '_PARENT_OTHER">' + choiceParentOrOther + '</label>';
	fieldSet += '<input type="radio" name="DiabetesHistory_' + questionNumber + '" id="DiabetesHistory_' + questionNumber + '_ALL" value="3" data-theme="b" />';
	fieldSet += '<label for="DiabetesHistory_' + questionNumber + '_ALL">' + choiceAll + '</label>';
		
    fieldSetElement.append(fieldSet);
   
	$(".choice"+questionNumber).append(fieldSetElement);
	$(".choice"+questionNumber).triggerHandler("create");
}

function insertMissInsulinChoices(questionNumber) {
	var choiceMoreThanOnceAWeek = "More than once a week";
	var choiceOnceAWeek = "About once a week";
	var choiceMoreThanOnceAMonth = "More than once a month";
	var choiceOnceAMonth = "About once a month";
	var choiceLessThanOnceAMonth = "Less than once a month";

	if (isSpanishQuestion(questionNumber)){
		choiceMoreThanOnceAWeek  = "spanish";
		choiceOnceAWeek = "spanish";
		choiceMoreThanOnceAMonth  = "spanish"; 
		choiceOnceAMonth = "spanish";
		choiceLessThanOnceAMonth = "spanish";
	}

	fieldSetElement = $(document.createElement("fieldset"));
	fieldSetElement.attr({
		"data-role": "controlgroup",
		"data-type": "vertical"
	});

	var fieldSet = '';
	
	fieldSet += '<input type="radio" name="DiabetesHistory_' + questionNumber + '" id="DiabetesHistory_' + questionNumber + '_MORE_THAN_ONCE_WEEK" value="1" data-theme="b" />';
	fieldSet += '<label for="DiabetesHistory_' + questionNumber + '_MORE_THAN_ONCE_WEEK">' + choiceMoreThanOnceAWeek + '</label>';
	fieldSet += '<input type="radio" name="DiabetesHistory_' + questionNumber + '" id="DiabetesHistory_' + questionNumber + '_ONCE_A_WEEK" value="2" data-theme="b" />';
	fieldSet += '<label for="DiabetesHistory_' + questionNumber + '_ONCE_A_WEEK">' + choiceOnceAWeek + '</label>';
	fieldSet += '<input type="radio" name="DiabetesHistory_' + questionNumber + '" id="DiabetesHistory_' + questionNumber + '_MORE_THAN_ONCE_MONTH" value="3" data-theme="b" />';
	fieldSet += '<label for="DiabetesHistory_' + questionNumber + '_MORE_THAN_ONCE_MONTH">' + choiceMoreThanOnceAMonth + '</label>';
	fieldSet += '<input type="radio" name="DiabetesHistory_' + questionNumber + '" id="DiabetesHistory_' + questionNumber + '_ONCE_A_MONTH" value="4" data-theme="b" />';
	fieldSet += '<label for="DiabetesHistory_' + questionNumber + '_ONCE_A_MONTH">' + choiceOnceAMonth + '</label>';
	fieldSet += '<input type="radio" name="DiabetesHistory_' + questionNumber + '" id="DiabetesHistory_' + questionNumber + '_LESS_THAN_ONCE_MONTH" value="5" data-theme="b" />';
	fieldSet += '<label for="DiabetesHistory_' + questionNumber + '_LESS_THAN_ONCE_MONTH">' + choiceLessThanOnceAMonth + '</label>';
		
    fieldSetElement.append(fieldSet);
   
	$(".choice"+questionNumber).append(fieldSetElement);
	$(".choice"+questionNumber).triggerHandler("create");
}

function evaluateDHInterpretations() { 

	var questionName = "DiabetesHistory_";
	var concerningHistory = [1,10,11,12,13,3];
	var hypoglycemia = [4,5,6,7];
	var hyperglycemia = [8,9];
	
	var spanishExtension = "_2";
	if (english) spanishExtension = "";
	
	
	var DHInterpretation = false;
	var DHConcerningHistory = {};	
	var DHHypoglycemiaMap = {};	
	var DHHyperglycemiaMap = {};	
	
	for (var i = 0, len = concerningHistory.length; i < len; i++) {
		var value =  $(":radio[name='" + questionName + concerningHistory[i] + spanishExtension +"']:checked").val();  
		if (value) {
			DHConcerningHistory[concerningHistory[i]] = value;
		}
	}
	for (var key in DHConcerningHistory) {
		if (DHConcerningHistory[1] == "yes" || DHConcerningHistory[10] == 2 || DHConcerningHistory[11] == 1 || DHConcerningHistory[12] == "no" || DHConcerningHistory[13] == 1 || 		DHConcerningHistory[3] == "no") {
			DHInterpretation = true;
			break;
		}
	}

	if (DHInterpretation) {
		$("#DiabetesInterpretation").val("ConcerningHistory");
		return;
	}
	
	for (var i = 0, len = hypoglycemia.length; i < len; i++) {
		var value =  $(":radio[name='" + questionName + hypoglycemia[i] + spanishExtension +"']:checked").val(); 
		if (value) {
			DHHypoglycemiaMap[hypoglycemia[i]] = value;
		}
	}
	for (var key in DHHypoglycemiaMap) {
		if (DHHypoglycemiaMap[4] == "no" || DHHypoglycemiaMap[5] == "yes" || DHHypoglycemiaMap[6] == "yes" || DHHypoglycemiaMap[7] == "no") {
			DHInterpretation = true;
			break;
		}
	}
	
	if (DHInterpretation) {
		$("#DiabetesInterpretation").val("Hypoglycemia");
		return;
	}
	
	for (var i = 0, len = hyperglycemia.length; i < len; i++) {
		var value =  $(":radio[name='" + questionName + hyperglycemia[i] + spanishExtension +"']:checked").val();  
		if (value) {
			DHHyperglycemiaMap[hyperglycemia[i]] = value;
		}
	}
	for (var key in DHHyperglycemiaMap) {
		if (DHHyperglycemiaMap[8] == "no" || DHHyperglycemiaMap[9] == "no") {
			DHInterpretation = true;
		}
	}
	
	if (DHInterpretation) {
		$("#DiabetesInterpretation").val("Hyperglycemia");
		return;
	}
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
