var aAcute = "&#225";
var iAcute = "&#237";
var numQuestions = 7;
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
	var NOT_AT_ALL = 0;
	var SEVERAL_DAYS = 1;
	var GT_HALF_THE_DAYS = 2;
	var NEARLY_EVERY_DAY = 3;
	var subtotal_not_at_all = 0;
	var subtotal_several_days = 0;
	var subtotal_GT_half_days = 0;
	var subtotal_nearly_ever_day = 0;
	var valueFound = false;
	var spanish_extension = "";
	if (!english){
		spanish_extension = "_2";
	}
	
	for (var i = 1; i < numQuestions; i++) {
		//Calculate the total score and also sub-totals of each choice.  
		//Sub-totals are needed for the GAD-7 results form.
    	$("input[name=GAD7QuestionEntry_" + i + spanish_extension + "]:checked").each(function() {
    		valueFound = true;
    		var value = parseInt($(this).val())
    		switch (value){
	    		case (NOT_AT_ALL):
	    			subtotal_not_at_all += value;
	    			break;
	    		case (SEVERAL_DAYS):
	    			subtotal_several_days += value;
	    			break;
	    		case (GT_HALF_THE_DAYS):
	    			subtotal_GT_half_days += value;
	    			break;
	    		case (NEARLY_EVERY_DAY):
	    			subtotal_nearly_ever_day += value;
    		}
    		
            score += value;
        });
		
    }
	
	if (valueFound) {
		
		$("#GAD7_Score").val(score);
		
		$("#GAD7_Score_NotAtAll").val(subtotal_not_at_all);
		$("#GAD7_Score_SeveralDays").val(subtotal_several_days);
		$("#GAD7_Score_GTHalfDays").val(subtotal_GT_half_days);
		$("#GAD7_Score_NearlyEveryDay").val(subtotal_nearly_ever_day);
		
	}
}