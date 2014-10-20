<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="${pageContext.request.contextPath}/moduleResources/chica/pws.css" type="text/css" rel="stylesheet" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui.min.css"/>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui.min.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/pws.js"></script>
        <title>CHICA Physician Encounter Form</title>
    </head>

    <body>
    	<div id="formContainer">
            <form id="pwsForm" name="pwsForm" action="pws.form" method="post">
                <div id="titleContainer">
                    <div id="submitFormTop">
                        <input id="submitButtonTop" type="button" value="Submit"/>
                    </div>
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
                        <b><font style="color:black;">A</font></b>
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
                        <c:choose>
                            <c:when test="${empty Height}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${Height}&nbsp;${HeightSUnits}&nbsp;(${HeightP}%)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b>${WeightA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Weight:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty WeightKG}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${WeightKG}&nbsp;kg.&nbsp;(${WeightP}%)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b>${BMIA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        BMI:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty BMI}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${BMI}&nbsp;(${BMIP}%)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b>${HCA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Head Circ:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty HC}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${HC} cm. (${HCP}%)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b>${TempA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Temp:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty Temperature}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${Temperature} F (${Temperature_Method})
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b>${PulseA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Pulse:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty Pulse}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${Pulse}
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b>${RRA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        RR:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty RR}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${RR}
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b>${BPA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        BP:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty BP}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${BP} (${BPP})
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b>${PulseOxA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Pulse Ox:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty PulseOx}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${PulseOx}%
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b>${HearA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Hear (L):<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty HearL}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${HearL}
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b>${HearA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Hear (R):<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty HearR}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${HearR}
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b>${VisionLA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Vision (L):<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty VisionL}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${VisionL}&nbsp;${VisionL_Corrected}
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b>${VisionRA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Vision (R):<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty VisionR}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${VisionR}&nbsp;${VisionR_Corrected}
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Weight:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty Weight}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${Weight}
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Prev WT:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty PrevWeight}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                ${PrevWeight}&nbsp;(${PrevWeightDate})
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div id="vitalsLegend">
                    <b><font style="color:red;">*</font>=Abnormal, U=Uncorrected,<br/>
                    C=Corrected, A=Axillary,
                    R=Rectal, O=Oral<br/>
                    F=Failed, P=Passed</b></div>
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
                        	<b><font style="color:red;">*</font> = Previously Abnormal</b>
                        </div>
                    </div>
                  <div id="examExtras">
                   	<div class="examExtraCheckbox">
                        	<input type="checkbox" name="Special_Need" value="Y">Special Need Child</input><br/>
                        </div>
                        <div>
                        	&nbsp;
                        </div>
                        <div class="examExtraCheckbox">
                        	<input type="checkbox">Two ID's Checked</input><br/>
                        </div>
                        <div class="examExtraCheckbox">
                        	<input type="checkbox" name="screenedForAbuse" value="screened">Screened for abuse</input><br/>
                        </div>
                        <div class="examExtraCheckbox">
                        	<input type="checkbox" name="discussedPhysicalActivity" value="Physical Activity">Discussed physical activity</input><br/>
                        </div>
                    	<div class="examExtraCheckbox">
                        	<input type="checkbox" name="discussedHealthyDiet" value="Healthy Diet">Discussed healthy diet</input><br/>
                      	</div>
                        <div>
                        	&nbsp;
                        </div>
                        <div class="examExtraData">
                        	${Language}
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
                        <c:choose>
                            <c:when test="${Pain == '0'}"> 
                                <div class="examExtraData">
                            </c:when>
                            <c:otherwise>
                                <div class="examExtraDataHighlight">
                            </c:otherwise>
                        </c:choose>
                        	Pain (0-10):${Pain}
                        </div>
                        <c:choose>
                            <c:when test="${Allergy == ' NONE'}"> 
                                <div class="examExtraData">
                            </c:when>
                            <c:otherwise>
                                <div class="examExtraDataHighlight">
                            </c:otherwise>
                        </c:choose>
                        	Allergies:${Allergy}
                        </div>
                        <div class="examExtraData">
                            ${MedicationLabel}
                        </div>
                  </div>
                </div>
                <div id="buttons">
                    <div class="buttonsData">
                        <input id="formPrintButton" type="button" value="Handouts"/>
                    </div>
                    <c:if test="${not empty diag1}">
	                	<div class="buttonsData">
	                        <input id="problemButton" type="button" value="Problem List"/>
	                    </div>
                    </c:if>
                    <c:if test="${not empty Med1_A || not empty Med1_B || not empty Med2_A || not empty Med2_B || 
                                  not empty Med3_A || not empty Med3_B || not empty Med4_A || not empty Med4_B || 
                                  not empty Med5_A || not empty Med5_B || not empty Med6_A || not empty Med6_B}">
	                    <div class="buttonsData">
	                        <input id="medButton" type="button" value="Medications"/>
	                    </div>
                    </c:if>
                </div>
                <div class="questionContainer">
                    <c:choose>
	                    <c:when test="${empty Prompt1_Text}">
	                        &nbsp;
	                    </c:when>
	                    <c:otherwise>
	                       <div class="questionStem">
			                   ${Prompt1_Text}
			               </div>
			               <div class="answerContainer">
			                   <div class="answerCheckbox">
			                       <input type="checkbox" name="sub_Choice1" value="1">${Answer1_1}</input><br/>
			                   </div>
			                   <div class="answerCheckbox">
			                       <input type="checkbox" name="sub_Choice1" value="3">${Answer1_3}</input><br/>
			                   </div>
			                   <div class="answerCheckbox">
			                       <input type="checkbox" name="sub_Choice1" value="5">${Answer1_5}</input><br/>
			                   </div>
			               </div>
			               <div class="answerContainer">
			                   <div class="answerCheckbox">
			                       <input type="checkbox" name="sub_Choice1" value="2">${Answer1_2}</input><br/>
			                   </div>
			                   <div class="answerCheckbox">
			                       <input type="checkbox" name="sub_Choice1" value="4">${Answer1_4}</input><br/>
			                   </div>
			                   <div class="answerCheckbox">
			                       <input type="checkbox" name="sub_Choice1" value="6">${Answer1_6}</input><br/>
			                   </div>
			               </div>
	                    </c:otherwise>
                    </c:choose>
                </div>
                <div class="questionContainer">
                    <c:choose>
                        <c:when test="${empty Prompt2_Text}">
                            &nbsp;
                        </c:when>
                        <c:otherwise>
                            <div class="questionStem">
		                        ${Prompt2_Text}
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice2" value="1">${Answer2_1}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice2" value="3">${Answer2_3}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice2" value="5">${Answer2_5}</input><br/>
		                        </div>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice2" value="2">${Answer2_2}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice2" value="4">${Answer2_4}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice2" value="6">${Answer2_6}</input><br/>
		                        </div>
		                    </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="questionContainer">
                    <c:choose>
                        <c:when test="${empty Prompt3_Text}">
                            &nbsp;
                        </c:when>
                        <c:otherwise>
                            <div class="questionStem">
		                        ${Prompt3_Text}
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice3" value="1">${Answer3_1}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice3" value="3">${Answer3_3}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice3" value="5">${Answer3_5}</input><br/>
		                        </div>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice3" value="2">${Answer3_2}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice3" value="4">${Answer3_4}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice3" value="6">${Answer3_6}</input><br/>
		                        </div>
		                    </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="questionContainer">
                    <c:choose>
                        <c:when test="${empty Prompt4_Text}">
                            &nbsp;
                        </c:when>
                        <c:otherwise>
                            <div class="questionStem">
		                        ${Prompt4_Text}
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice4" value="1">${Answer4_1}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice4" value="3">${Answer4_3}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice4" value="5">${Answer4_5}</input><br/>
		                        </div>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice4" value="2">${Answer4_2}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice4" value="4">${Answer4_4}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice4" value="6">${Answer4_6}</input><br/>
		                        </div>
		                    </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="questionContainer">
                    <c:choose>
                        <c:when test="${empty Prompt5_Text}">
                            &nbsp;
                        </c:when>
                        <c:otherwise>
                            <div class="questionStem">
		                        ${Prompt5_Text}
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice5" value="1">${Answer5_1}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice5" value="3">${Answer5_3}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice5" value="5">${Answer5_5}</input><br/>
		                        </div>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice5" value="2">${Answer5_2}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice5" value="4">${Answer5_4}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice5" value="6">${Answer5_6}</input><br/>
		                        </div>
		                    </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="questionContainer">
                    <c:choose>
                        <c:when test="${empty Prompt6_Text}">
                            &nbsp;
                        </c:when>
                        <c:otherwise>
                            <div class="questionStem">
		                        ${Prompt6_Text}
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice6" value="1">${Answer6_1}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice6" value="3">${Answer6_3}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice6" value="5">${Answer6_5}</input><br/>
		                        </div>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice6" value="2">${Answer6_2}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice6" value="4">${Answer6_4}</input><br/>
		                        </div>
		                        <div class="answerCheckbox">
		                            <input type="checkbox" name="sub_Choice6" value="6">${Answer6_6}</input><br/>
		                        </div>
		                    </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div id="submitContainer">
                    <input id="submitButtonBottom" type="button" value="Submit"/>
                </div>
                <div id="problemDialog" title="Problem List" class="ui-dialog-titlebar ui-widget-header">
                    <table id="problemTable"">
                        <tr>
                            <td class="padding5">${diag1}</td>
                        </tr>
                        <tr>
                            <td class="padding5">${diag2}</td>
                        </tr>
                        <tr>
                            <td class="padding5">${diag3}</td>
                        </tr>
                        <tr>
                            <td class="padding5">${diag4}</td>
                        </tr>
                        <tr>
                            <td class="padding5">${diag5}</td>
                        </tr>
                        <tr>
                            <td class="padding5">${diag6}</td>
                        </tr>
                        <tr>
                            <td class="padding5">${diag7}</td>
                        </tr>
                        <tr>
                            <td class="padding5">${diag8}</td>
                        </tr>
                        <tr>
                            <td class="padding5">${diag9}</td>
                        </tr>
                        <tr>
                            <td class="padding5">${diag10}</td>
                        </tr>
                    </table>
                </div>
                <div id="medDialog" title="Medication List" class="ui-dialog-titlebar ui-widget-header">
                    <table id="medTable">
                        <c:if test="${not empty Med1_A || not empty Med1_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop">${Med1_A}</td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td>${Med1_B}</td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med2_A || not empty Med2_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop">${Med2_A}</td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td>${Med2_B}</td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med3_A || not empty Med3_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop">${Med3_A}</td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td>${Med3_B}</td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med4_A || not empty Med4_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop">${Med4_A}</td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td>${Med4_B}</td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med5_A || not empty Med5_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop">${Med5_A}</td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td>${Med5_B}</td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med6_A || not empty Med6_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop">${Med6_A}</td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td>${Med6_B}</td>
	                        </tr>
                        </c:if>
                    </table>
                </div>
                <div id="confirmSubmitDialog" title="Confirm" class="ui-dialog-titlebar ui-widget-header">
                    <div id="confirmText">
                        <span>Click OK to permanently submit the form.</span>
                    </div>
                    <div id="confirmButtons">
	                    <input id="okSubmitButton" type="button" value="OK"/>
	                    <input id="cancelSubmitButton" type="button" value="Cancel"/>
                    </div>
                </div>
                <div id="submitWaitDialog" class="noTitle">
                    <div id="submitWaitText">
                        <span>Submitting...</span>
                    </div>
                </div>
                <div id="formAccordionDialog" title="Recommended Handouts" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
                    <div id="formLoading">
                       <span id="formLoadingPanel"><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Loading forms...</span>
                    </div>
                    <div id="formServerError">
                    </div>
	                <div id="formAccordion">
	                </div>
                </div>
                <input type=hidden name="Choice1"/>
			    <input type=hidden name="Choice2"/>
			    <input type=hidden name="Choice3"/>
			    <input type=hidden name="Choice4"/>
			    <input type=hidden name="Choice5"/>
			    <input type=hidden name="Choice6"/>
			    <input id="patientId" name="patientId" type="hidden" value="${patient.patientId}"/>
				<input id="encounterId" name="encounterId" type="hidden" value="${encounterId}"/>
				<input id="sessionId" name="sessionId" type="hidden" value="${sessionId}"/>
				<input id="formId" name="formId" type="hidden" value="${formId}"/>
				<input id="formInstanceId" name="formInstanceId" type="hidden" value="${formInstanceId}"/>
				<input id="locationId" name="locationId" type="hidden" value="${locationId}"/>
				<input id="locationTagId" name="locationTagId" type="hidden" value="${locationTagId}"/>
				<input id="maxElements" name="maxElements" type="hidden" value="5"/>
				<input id="language" name="language" type="hidden" value="${language}"/>
				<input id="formInstance" name="formInstance" type="hidden" value="${formInstance}"/>
            </form>
    	</div>
    </body>
</html> 