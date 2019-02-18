var aAcute = "&#225";
var iAcute = "&#237";

$(document).on("pageinit", function() {
	pageInit();
});

function insertChoices(prefix, questionNumber, isSpanish){
	
	
	var choiceNotAtAll = "Not at all";
	var choiceSeveralDays = "Several days";
	var choiceMoreThanHalfDays = "More than half the days";
	var choiceNearlyEveryDay = "Nearly every day";
	
	if(isSpanish)
	{
		choiceNotAtAll = "Nunca";
		choiceSeveralDays = "Varios d" + iAcute + "as";
		choiceMoreThanHalfDays = "M" + aAcute + "s de la mitad de los d" + iAcute + "as";
		choiceNearlyEveryDay = "Casi todos los d" + iAcute + "as";
		if (!questionNumber.endsWith("_2")){
			questionNumber = questionNumber + "_2";
		}
	}
	
	var fieldSetElement = $(document.createElement("fieldset"));
	fieldSetElement.attr({
		"data-role": "controlgroup",
		"data-type": "vertical"
	});
	
	var fieldSet = '';
	
	fieldSet += '<input type="radio" name="' + prefix + questionNumber + '" id="' + prefix + questionNumber + '_NOT_AT_ALL" value="0" data-theme="b" />';
	fieldSet += '<label for="' + prefix + questionNumber + '_NOT_AT_ALL">' + choiceNotAtAll + '</label>';
	fieldSet += '<input type="radio" name="' + prefix + questionNumber + '" id="' + prefix + questionNumber + '_SEVERAL_DAYS" value="1" data-theme="b" />';
    fieldSet += '<label for="' + prefix + questionNumber + '_SEVERAL_DAYS">' + choiceSeveralDays + '</label>';
    fieldSet += '<input type="radio" name="' + prefix + questionNumber + '" id="' + prefix + questionNumber + '_MORE_THAN_HALF_DAYS" value="2" data-theme="b" />';
    fieldSet += '<label for="' + prefix + questionNumber + '_MORE_THAN_HALF_DAYS">' + choiceMoreThanHalfDays + '</label>';
	fieldSet += '<input type="radio" name="' + prefix + questionNumber + '" id="' + prefix + questionNumber + '_EVERY_DAY" value="3" data-theme="b" />';
    fieldSet += '<label for="' + prefix + questionNumber + '_EVERY_DAY">' + choiceNearlyEveryDay + '</label>';
	
	fieldSetElement.append(fieldSet);
	
	$(".choice_"+questionNumber).append(fieldSetElement);
	$(".choice_"+questionNumber).triggerHandler("create");

}

function calculateScore() {
	var score = 0;
	var valueFound = false;
	var spanish_extension = "";
	if (!english){
		spanish_extension = "_2";
	}
	
	for (var i = 1; i < 10; i++) {
		//value = $("input:radio[name=PHQ9QuestionEntry_" + i + "]").val();
    	$("input[name=CAD7QuestionEntry_" + i + spanish_extension + "]:checked").each(function() {
    		valueFound = true;
    		var value = parseInt($(this).val())
            score = score + value;
        });
		
    }
	
	if (valueFound) {
		$("#CAD7Score").val(score);
		
		if (score <= 4) {
			$("#CAD7Interpretation").val("minimal");
		} else if (score >= 5  && score <=9) {
			$("#CAD7Interpretation").val("mild");
		} else if (score >=10 && score <= 14) {
			$("#CAD7Interpretation").val("moderate");
		} else if (score >=15 ){
			$("#CAD7Interpretation").val("severe");
		}
	}
}