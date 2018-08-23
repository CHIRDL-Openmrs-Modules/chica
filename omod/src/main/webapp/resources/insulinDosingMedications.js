
$(document).on("pageinit", function() {
	pageInit();
});

function insertChoices(prefix, questionNumber, isSpanish){
	
	
	var choiceNever = "Almost never";
	var choiceSometimes = "Sometimes (less than half of the time)";
	var choiceUsually = "Usually (more than half of the time)";
	var choiceAlways = "Always";
	
	if(isSpanish)
	{
		choiceNever = "Casi nunca";
		choiceSometimes = "Algunas veces (menos de la mitad del tiempo)";
		choiceUsually = "Usualmente (más de la mitad del tiempo)";
		choiceAlways = "Siempre";
		questionNumber = questionNumber + "_2";
	}
	
	var fieldSetElement = $(document.createElement("fieldset"));
	fieldSetElement.attr({
		"data-role": "controlgroup",
		"data-type": "vertical"
	});
	
	var fieldSet = '';
	
	fieldSet += '<input type="radio" name="' + prefix + questionNumber + '" id="' + prefix + questionNumber + '_NEVER" value="0" data-theme="b" />';
	fieldSet += '<label for="' + prefix + questionNumber + '_NEVER">' + choiceNever + '</label>';
	fieldSet += '<input type="radio" name="' + prefix + questionNumber + '" id="' + prefix + questionNumber + '_SOMETIMES" value="1" data-theme="b" />';
    fieldSet += '<label for="' + prefix + questionNumber + '_SOMETIMES">' + choiceSometimes + '</label>';
    fieldSet += '<input type="radio" name="' + prefix + questionNumber + '" id="' + prefix + questionNumber + '_USUALLY" value="2" data-theme="b" />';
    fieldSet += '<label for="' + prefix + questionNumber + '_USUALLY">' + choiceUsually + '</label>';
	fieldSet += '<input type="radio" name="' + prefix + questionNumber + '" id="' + prefix + questionNumber + '_ALWAYS" value="3" data-theme="b" />';
    fieldSet += '<label for="' + prefix + questionNumber + '_ALWAYS">' + choiceAlways + '</label>';
	
	fieldSetElement.append(fieldSet);
	
	$(".choice_"+questionNumber).append(fieldSetElement);
	$(".choice_"+questionNumber).triggerHandler("create");

}

function calculateScore() {
	
	var questionName = "InsulinDosingQuestionEntry_";
	var spanishExtension = "_2";
	if (english) spanishExtension = "";
	
	for (var i = 1; i <= 6; i++) {
		var value =  $("input[name='" + questionName + i + spanishExtension + "']:checked").val(); 
		if (value == 0 || value == 1) {
			$("#insulin_dosing_interpretation").val("positive");
			break;
		}
	}
}