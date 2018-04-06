
$(document).on("pageinit", function() {
	pageInit();
	
	// All other custom initialization goes here
    
});

// Sets the text for the instructions page
function setLanguage(patientName, birthdate) {
	english = !english;
	
	if(english){
		$("#additionalQuestions").text($("#instructions_additionalQuestions").val());
		$("#formTitle").text($("#formNameHeader").val());
	}
	else{
		$("#additionalQuestions").text($("#instructions_additionalQuestions_sp").val());
		$("#formTitle").text($("#formNameHeader_sp").val());
	}

    setLanguageForButtons();
}

// Calculate score is intentionally left empty
// for this form
function calculateScore() {
	
}

// Insert choices for Yes/No
function insertYesNo(questionNumber, isSpanish)
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
	
	fieldSet += '<input type="radio" name="SUDEPQuestionEntry_' + questionNumber + '" id="SUDEPQuestionEntry_' + questionNumber + '_Yes" value="yes" data-theme="c" />';
	fieldSet += '<label for="SUDEPQuestionEntry_' + questionNumber + '_Yes">' + choiceYes + '</label>';
	fieldSet += '<input type="radio" name="SUDEPQuestionEntry_' + questionNumber + '" id="SUDEPQuestionEntry_' + questionNumber + '_No" value="no" data-theme="c" />';
	fieldSet += '<label for=SUDEPQuestionEntry_' + questionNumber + '_No>' + choiceNo + '</label>';
	
	fieldSetElement.append(fieldSet);
	
	$(".choice_"+questionNumber).append(fieldSetElement);
	$(".choice_"+questionNumber).triggerHandler("create");
}

// Insert choices for 0, 1, 2, 3, 4, 5 and More than 5
function insertChoices(questionNumber, isSpanish)
{
	var choiceNone = "0";
	var choiceOne = "1";
	var choiceTwo = "2";
	var choiceThree = "3";
	var choiceFour = "4";
	var choiceFive = "5";
	var choiceGTFive = "More than 5";
	
	if(isSpanish)
	{
		choiceGTFive = "M&aacute;s de 5";
		questionNumber = questionNumber + "_2";
	}
	
	var fieldSetElement = $(document.createElement("fieldset"));
	fieldSetElement.attr({
		"data-role": "controlgroup",
		"data-type": "vertical"
		});
	
	var fieldSet = '';
	
	fieldSet += '<input type="radio" name="SUDEPQuestionEntry_' + questionNumber + '" id="SUDEPQuestionEntry_' + questionNumber + '_None" value="0" data-theme="b" />';
	fieldSet += '<label for="SUDEPQuestionEntry_' + questionNumber + '_None">' + choiceNone + '</label>';
	fieldSet += '<input type="radio" name="SUDEPQuestionEntry_' + questionNumber + '" id="SUDEPQuestionEntry_' + questionNumber + '_One" value="1" data-theme="b" />';
	fieldSet += '<label for=SUDEPQuestionEntry_' + questionNumber + '_One>' + choiceOne + '</label>';
	fieldSet += '<input type="radio" name="SUDEPQuestionEntry_' + questionNumber + '" id="SUDEPQuestionEntry_' + questionNumber + '_Two" value="2" data-theme="b" />';
	fieldSet += '<label for=SUDEPQuestionEntry_' + questionNumber + '_Two>' + choiceTwo + '</label>';
	fieldSet += '<input type="radio" name="SUDEPQuestionEntry_' + questionNumber + '" id="SUDEPQuestionEntry_' + questionNumber + '_Three" value="3" data-theme="b" />';
	fieldSet += '<label for=SUDEPQuestionEntry_' + questionNumber + '_Three>' + choiceThree + '</label>';
	fieldSet += '<input type="radio" name="SUDEPQuestionEntry_' + questionNumber + '" id="SUDEPQuestionEntry_' + questionNumber + '_Four" value="4" data-theme="b" />';
	fieldSet += '<label for=SUDEPQuestionEntry_' + questionNumber + '_Four>' + choiceFour + '</label>';
	fieldSet += '<input type="radio" name="SUDEPQuestionEntry_' + questionNumber + '" id="SUDEPQuestionEntry_' + questionNumber + '_Five" value="5" data-theme="b" />';
	fieldSet += '<label for=SUDEPQuestionEntry_' + questionNumber + '_Five>' + choiceFive + '</label>';
	fieldSet += '<input type="radio" name="SUDEPQuestionEntry_' + questionNumber + '" id="SUDEPQuestionEntry_' + questionNumber + '_GT5" value="6" data-theme="b" />';
	fieldSet += '<label for=SUDEPQuestionEntry_' + questionNumber + '_GT5>' + choiceGTFive + '</label>';
	
	fieldSetElement.append(fieldSet);
	
	$(".choice_"+questionNumber).append(fieldSetElement);
	$(".choice_"+questionNumber).triggerHandler("create");
}
