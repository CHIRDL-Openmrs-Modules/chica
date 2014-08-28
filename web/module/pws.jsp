<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
<html style="height:100%;" xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="${pageContext.request.contextPath}/moduleResources/chica/pws.css" type="text/css" rel="stylesheet" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui.min.css"/>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui.min.js"></script>
        <script>
            $(function() {
            $("#problemDialog").dialog({
              autoOpen: false,
              modal: true,
              show: {
                effect: "clip",
                duration: 1000
              },
              hide: {
                effect: "clip",
                duration: 1000
              }
            });

            $("#problemButton").click(function() {
              $("#problemDialog").dialog("open");
            });
          });
        </script>
        <style>
            .ui-dialog-titlebar {
                background-color: #75A3A3;
                background-image: none;
                color: #FFF;
            }
        </style>
        <title>CHICA Physician Encounter Form</title>
    </head>

    <body>
    	<div id="formContainer">
            <form name="input" action="pws.form" method="post">
                <div id="titleContainer">
                    <div id="title">
                        <h3>CHICA Physician Encounter Form</h3>
                    </div>
                    <div id="mrn">
                        <h3>${MRN}</h3>
                    </div>
                </div>
                <div id="infoLeft">
                    <b>Patient:</b> ${PatientName}<br/>
                    <b>DOB:</b> ${DOB} <b>Age:</b> ${Age}<br/>
                    <b>Doctor:</b> ${Doctor}
                </div>
                <div id="infoRight">
                    <b>MRN:</b> ${MRN}<br/>
                    <b>Date:</b> ${VisitDate}<br/>
                <b>Time:</b> ${VisitTime}</div>
                <div id="vitals">
                    <div class="flagCell">
                        <b>A</b>
                    </div>
                    <div class="vitalsNames">
                        <b>Vital Signs:</b>
                    </div>
                    <div class="vitalsValues">
                        &nbsp;&nbsp;
                    </div>
                    <div class="flagCell">
                        <b>${HeightA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Height:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${Height}&nbsp;${HeightSUnits}&nbsp;(${HeightP}%)
                    </div>
                    <div class="flagCell">
                        <b>${WeightA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Weight:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${WeightKG}&nbsp;kg.&nbsp;(${WeightP}%)
                    </div>
                    <div class="flagCell">
                        <b>${BMIA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        BMI:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${BMI}&nbsp;(${BMIP}%)
                    </div>
                    <div class="flagCell">
                        <b>${HCA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Head Circ:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${HC} cm. (${HCP}%)
                    </div>
                    <div class="flagCell">
                        <b>${TempA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Temp:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${Temperature} F (${Temperature_Method})
                    </div>
                    <div class="flagCell">
                        <b>${PulseA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Pulse:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${Pulse}
                    </div>
                    <div class="flagCell">
                        <b>${RRA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        RR:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${RR}
                    </div>
                    <div class="flagCell">
                        <b>${BPA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        BP:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${BP} (${BPP})
                    </div>
                    <div class="flagCell">
                        <b>${PulseOxA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Pulse Ox:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${PulseOx}%
                    </div>
                    <div class="flagCell">
                        <b>${HearA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Hear (L):<br/>
                    </div>
                    <div class="vitalsValues">
                        ${HearL}
                    </div>
                    <div class="flagCell">
                        <b>${HearA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Hear (R):<br/>
                    </div>
                    <div class="vitalsValues">
                        ${HearR}
                    </div>
                    <div class="flagCell">
                        <b>${VisionLA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Vision (L):<br/>
                    </div>
                    <div class="vitalsValues">
                        ${VisionL}&nbsp;${VisionL_Corrected}
                    </div>
                    <div class="flagCell">
                        <b>${VisionRA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Vision (R):<br/>
                    </div>
                    <div class="vitalsValues">
                        ${VisionL}&nbsp;${VisionL_Corrected}
                    </div>
                    <div class="flagCell">
                        <b></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Weight:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${Weight}
                    </div>
                    <div class="flagCell">
                        <b></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Prev WT:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${PrevWeight}&nbsp;(${PrevWeightDate})
                    </div>
                    <div id="vitalsLegend">
                    <b>*=Abnormal, U=Uncooperative</b></div>
                </div>
                <div id="exam">
                	<div id="physicalExam">
                    	<div id="examTitle">
                        	<b>Physical Exam:</b>
                        </div>
                        <div class="examNames">
                            &nbsp;<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	Nl<br/>
                        </div>
                        <div class="examHeader">
                        	Abnl<br/>
                        </div>
                        <div class="examNames">
                            General:<br/>
                        </div>
                        <div class="examFlag">
                            ${GeneralExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_General" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_General" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Head:<br/>
                        </div>
                        <div class="examFlag">
                            ${HeadExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Head" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Head" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Skin:<br/>
                        </div>
                        <div class="examFlag">
                            ${SkinExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Skin" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Skin" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Eyes:<br/>
                        </div>
                        <div class="examFlag">
                            ${EyesVisionExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Eyes" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Eyes" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Ears:<br/>
                        </div>
                        <div class="examFlag">
                            ${EarsHearingExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Ears" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Ears" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Nose/Throat:<br/>
                        </div>
                        <div class="examFlag">
                            ${NoseThroatExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Nose" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Nose" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Teeth/Gums:<br/>
                        </div>
                        <div class="examFlag">
                            ${TeethGumsExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Teeth" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Teeth" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Nodes:<br/>
                        </div>
                        <div class="examFlag">
                            ${NodesExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Nodes" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Nodes" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Chest/Lungs:<br/>
                        </div>
                        <div class="examFlag">
                            ${ChestLungsExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Chest" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Chest" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Heart/Pulses:<br/>
                        </div>
                        <div class="examFlag">
                            ${HeartPulsesExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Heart" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Heart" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Abdomen:<br/>
                        </div>
                        <div class="examFlag">
                            ${AbdomenExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Abdomen" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Abdomen" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Ext Genitalia:<br/>
                        </div>
                        <div class="examFlag">
                            ${ExtGenitaliaExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_ExtGenitalia" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_ExtGenitalia" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Back:<br/>
                        </div>
                        <div class="examFlag">
                            ${BackExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Back" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Back" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Neuro:<br/>
                        </div>
                        <div class="examFlag">
                            ${NeuroExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Neuro" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Neuro" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Extremities:<br/>
                        </div>
                        <div class="examFlag">
                            ${ExtremitiesExamA}<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Extremities" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Extremities" value="A"/><br/>
                        </div>
                        <div id="examLegend">
                        	<b>* = Previously Abnormal</b>
                        </div>
                    </div>
                  <div id="examExtras">
                   	<div class="examExtraCheckbox">
                        	<input type="checkbox">Special Need Child</input><br/>
                        </div>
                        <div>
                        	&nbsp;
                        </div>
                        <div class="examExtraCheckbox">
                        	<input type="checkbox">Two ID's Checked</input><br/>
                        </div>
                        <div class="examExtraCheckbox">
                        	<input type="checkbox">Screened for abuse</input><br/>
                        </div>
                        <div class="examExtraCheckbox">
                        	<input type="checkbox">Discussed physical activity</input><br/>
                        </div>
                    	<div class="examExtraCheckbox">
                        	<input type="checkbox">Discussed healthy diet</input><br/>
                      	</div>
                        <div class="examExtraCheckbox">
                        	<input type="checkbox">Updated Med List given to patient</input><br/>
                      	</div>
                        <div>
                        	&nbsp;
                        </div>
                        <div class="examExtraData">
                        	Language: ${Language}
                        </div>
                        <div>
                        	&nbsp;
                        </div>
                        <div class="examExtraData">
                        	${TobaccoLabel} ${TobaccoAnswer}
                        </div>
                        <div class="examExtraData">
                        	${AlcoholLabel} ${AlcoholAnswer}
                        </div>
                        <div class="examExtraData">
                        	${DrugsLabel} ${DrugsAnswer}
                        </div>
                        <div>
                        	&nbsp;
                        </div>
                        <div class="examExtraDataHighlight">
                        	Pain (0-10):${Pain}
                        </div>
                        <div class="examExtraData">
                        	Allergies:${Allergy}
                        </div>
                        <div>
                            &nbsp;
                        </div>
                        <div class="examExtraData">
                            <input id="problemButton" type="button" value="Problem List"/>
                        </div>
                  </div>
                </div>
                <div id="pain">
                	
                </div>
                <div id="questionOneContainer" class="questionContainer">
                	<div id="questionOneStem" class="questionStem">
                    	${Prompt1_Text}
                    </div>
                    <div id="questionOneAnswerLeftContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">${Answer1_1}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer1_3}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer1_5}</input><br/>
                        </div>
                    </div>
                    <div id="questionOneAnswerRightContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">${Answer1_2}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer1_4}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer1_6}</input><br/>
                        </div>
                    </div>
                </div>
                <div id="questionTwoContainer" class="questionContainer">
                	<div id="questionTwoStem" class="questionStem">
                    	${Prompt2_Text}
                    </div>
                    <div id="questionTwoAnswerLeftContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">${Answer2_1}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer2_3}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer2_5}</input><br/>
                        </div>
                    </div>
                    <div id="questionTwoAnswerRightContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">${Answer2_2}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer2_4}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer2_6}</input><br/>
                        </div>
                    </div>
                </div>
                <div id="questionThreeContainer" class="questionContainer">
                	<div id="questionThreeStem" class="questionStem">
                    	${Prompt3_Text}
                    </div>
                    <div id="questionThreeAnswerLeftContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">${Answer3_1}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer3_3}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer3_5}</input><br/>
                        </div>
                    </div>
                    <div id="questionThreeAnswerRightContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">${Answer3_2}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer3_4}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer3_6}</input><br/>
                        </div>
                    </div>
                </div>
                <div id="questionFourContainer" class="questionContainer">
                	<div id="questionFourStem" class="questionStem">
                    	${Prompt4_Text}
                    </div>
                    <div id="questionFourAnswerLeftContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">${Answer4_1}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer4_3}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer4_5}</input><br/>
                        </div>
                    </div>
                    <div id="questionFourAnswerRightContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">${Answer4_2}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer4_4}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer4_6}</input><br/>
                        </div>
                    </div>
                </div>
                <div id="questionFiveContainer" class="questionContainer">
                	<div id="questionFiveStem" class="questionStem">
                    	${Prompt5_Text}
                    </div>
                    <div id="questionFiveAnswerLeftContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">${Answer5_1}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer5_3}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer5_5}</input><br/>
                        </div>
                    </div>
                    <div id="questionFiveAnswerRightContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">${Answer5_2}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer5_4}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer5_6}</input><br/>
                        </div>
                    </div>
                </div>
                <div id="questionSixContainer" class="questionContainer">
                	<div id="questionSixStem" class="questionStem">
                    	${Prompt6_Text}
                    </div>
                    <div id="questionSixAnswerLeftContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">${Answer6_1}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer6_3}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer6_5}</input><br/>
                        </div>
                    </div>
                    <div id="questionSixAnswerRightContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">${Answer6_2}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer6_4}</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">${Answer6_6}</input><br/>
                        </div>
                    </div>
                </div>
                <div id="problemDialog" title="Problem List" class="ui-dialog-titlebar ui-widget-header">
                    <table style="color: #000;">
                        <tr>
                            <td style="padding:5px;">${diag1}</td>
                        </tr>
                        <tr>
                            <td style="padding:5px;">${diag2}</td>
                        </tr>
                        <tr>
                            <td style="padding:5px;">${diag3}</td>
                        </tr>
                        <tr>
                            <td style="padding:5px;">${diag4}</td>
                        </tr>
                        <tr>
                            <td style="padding:5px;">${diag5}</td>
                        </tr>
                        <tr>
                            <td style="padding:5px;">${diag6}</td>
                        </tr>
                        <tr>
                            <td style="padding:5px;">${diag7}</td>
                        </tr>
                        <tr>
                            <td style="padding:5px;">${diag8}</td>
                        </tr>
                        <tr>
                            <td style="padding:5px;">${diag9}</td>
                        </tr>
                        <tr>
                            <td style="padding:5px;">${diag10}</td>
                        </tr>
                    </table>
                </div>
            </form>
    	</div>
    </body>
</html> 