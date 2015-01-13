<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/pws.css" type="text/css" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/pws.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.js"></script>
        <title>CHICA Physician Encounter Form</title>
    </head>

    <body>
    	<div id="formContainer">
            <form id="pwsForm" name="pwsForm" action="pws.form" method="post">
                <div id="titleContainer">
                    <div id="submitFormTop">
                        <a href="#" id="submitButtonTop" class="icon-button ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Submit</a>
                    </div>
                    <div id="title">
                        <h3>CHICA Physician Encounter Form</h3>
                    </div>
                    <div id="mrn">
                        <h3><c:out value="${MRN}"/></h3>
                    </div>
                </div>
                <div id="infoLeft">
                    <b>Patient:</b> <c:out value="${PatientName}"/><br/>
                    <b>DOB:</b> <c:out value="${DOB}"/> <b>Age:</b> <c:out value="${Age}"/><br/>
                    <b>Doctor:</b> <c:out value="${Doctor}"/>
                </div>
                <div id="infoRight">
                    <b>MRN:</b> <c:out value="${MRN}"/><br/>
                    <b>Date:</b> <c:out value="${VisitDate}"/><br/>
                <b>Time:</b> <c:out value="${VisitTime}"/></div>
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
                        <b><c:out value="${HeightA}"/></b><br/>
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
                                <c:out value="${Height}"/>&nbsp;<c:out value="${HeightSUnits}"/>&nbsp;(<c:out value="${HeightP}"/>%)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${WeightA}"/></b><br/>
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
                                <c:out value="${WeightKG}"/>&nbsp;kg.&nbsp;(<c:out value="${WeightP}"/>%)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${BMIA}"/></b><br/>
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
                                <c:out value="${BMI}"/>&nbsp;(<c:out value="${BMIP}"/>%)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${HCA}"/></b><br/>
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
                                <c:out value="${HC}"/> cm. (<c:out value="${HCP}"/>%)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${TempA}"/></b><br/>
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
                                <c:out value="${Temperature}"/>&nbsp;F&nbsp;(<c:out value="${Temperature_Method}"/>)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${PulseA}"/></b><br/>
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
                                <c:out value="${Pulse}"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${RRA}"/></b><br/>
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
                                <c:out value="${RR}"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${BPA}"/></b><br/>
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
                                <c:out value="${BP}"/> (<c:out value="${BPP}"/>)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${PulseOxA}"/></b><br/>
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
                                <c:out value="${PulseOx}"/>%
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${HearA}"/></b><br/>
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
                                <c:out value="${HearL}"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${HearA}"/></b><br/>
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
                                <c:out value="${HearR}"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${VisionLA}"/></b><br/>
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
                                <c:out value="${VisionL}"/>&nbsp;<c:out value="${VisionL_Corrected}"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${VisionRA}"/></b><br/>
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
                                <c:out value="${VisionR}"/>&nbsp;<c:out value="${VisionR_Corrected}"/>
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
                                <c:out value="${Weight}"/>
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
                                <c:out value="${PrevWeight}"/>&nbsp;(<c:out value="${PrevWeightDate}"/>)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div id="vitalsLegend">
                    <b><font style="color:red;">*</font>=Abnormal, U=Uncorrected,<br/>
                    C=Corrected, A=Axillary,
                    R=Rectal, O=Oral,<br/>
                    F=Failed, P=Passed</b></div>
                </div>
                <div id="exam">
                	<div id="physicalExam">
                    	<div id="examTitle">
                        	<b>Physical Exam:</b>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examNames">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	Nl<br/>
                        </div>
                        <div class="examHeader">
                        	Abnl<br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${GeneralExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            General:<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_General" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_General" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${HeadExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Head:<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Head" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Head" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${SkinExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Skin:<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Skin" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Skin" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${EyesVisionExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Eyes:<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Eyes" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Eyes" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${EarsHearingExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Ears:<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Ears" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Ears" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${NoseThroatExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Nose/Throat:<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Nose" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Nose" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${TeethGumsExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Teeth/Gums:<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Teeth" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Teeth" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${NodesExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Nodes:<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Nodes" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Nodes" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${ChestLungsExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Chest/Lungs:<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Chest" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Chest" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${HeartPulsesExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Heart/Pulses:<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Heart" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Heart" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${AbdomenExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Abdomen:<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Abdomen" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Abdomen" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${ExtGenitaliaExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Ext Genitalia:<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_ExtGenitalia" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_ExtGenitalia" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${BackExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Back:<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Back" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Back" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${NeuroExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Neuro:<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Neuro" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Neuro" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${ExtremitiesExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Extremities:<br/>
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
                   	        <c:choose>
                   	            <c:if test="${Special_Need == 'Y'}">
                   	                <input type="checkbox" name="Special_Need" value="Y" checked disabled/>Special Need Child<br/>
                   	            </c:if>
                   	            <c:otherwise>
                        	        <input type="checkbox" name="Special_Need" value="Y"/>Special Need Child<br/>
                        	    </c:otherwise>
                        	</c:choose>
                        </div>
                        <div>
                        	&nbsp;
                        </div>
                        <div class="examExtraCheckbox">
                        	<input type="checkbox"/>Two ID's Checked<br/>
                        </div>
                        <div class="examExtraCheckbox">
                        	<input type="checkbox" name="screenedForAbuse" value="screened"/>Screened for abuse<br/>
                        </div>
                        <div class="examExtraCheckbox">
                        	<input type="checkbox" name="discussedPhysicalActivity" value="Physical Activity"/>Discussed physical activity<br/>
                        </div>
                    	<div class="examExtraCheckbox">
                        	<input type="checkbox" name="discussedHealthyDiet" value="Healthy Diet"/>Discussed healthy diet<br/>
                      	</div>
                        <div>
                        	&nbsp;
                        </div>
                        <div class="examExtraData">
                        	<c:out value="${Language}"/>
                        </div>
                        <div>
                        	&nbsp;
                        </div>
                        <div class="examExtraData">
                        	<c:out value="${TobaccoLabel}"/>&nbsp;<c:out value="${TobaccoAnswer}"/>
                        </div>
                        <div class="examExtraData">
                        	<c:out value="${AlcoholLabel}"/>&nbsp;<c:out value="${AlcoholAnswer}"/>
                        </div>
                        <div class="examExtraData">
                        	<c:out value="${DrugsLabel}"/>&nbsp;<c:out value="${DrugsAnswer}"/>
                        </div>
                        <div>
                        	&nbsp;
                        </div>
                        <c:choose>
                            <c:when test="${Pain == '0'}"> 
                                <div class="examExtraData">
                            </c:when>
                            <c:otherwise>
                                <div class="ui-state-highlight examExtraData">
                            </c:otherwise>
                        </c:choose>
                        	Pain (0-10):<c:out value="${Pain}"/>
                        </div>
                        <c:choose>
                            <c:when test="${Allergy == ' NONE'}"> 
                                <div class="examExtraData">
                            </c:when>
                            <c:otherwise>
                                <div class="ui-state-highlight examExtraData">
                            </c:otherwise>
                        </c:choose>
                        	Allergies:<c:out value="${Allergy}"/>
                        </div>
                        <div class="examExtraData">
                            <c:out value="${MedicationLabel}"/>
                        </div>
                  </div>
                </div>
                <div id="buttons">
                    <div class="buttonsData">
                        <a href="#" id="formPrintButton" class="icon-button largeButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Recommended Handouts</a>
                    </div>
                    <div class="buttonsData">
                        <a href="#" id="forcePrintButton" class="icon-button largeButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Other Handouts</a>
                    </div>
                    <!-- <c:if test="${not empty diag1}">
	                	<div class="buttonsData">
	                        <a href="#" id="problemButton" class="icon-button ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Problem List</a>
	                    </div>
                    </c:if>
                    <c:if test="${not empty Med1_A || not empty Med1_B || not empty Med2_A || not empty Med2_B || 
                                  not empty Med3_A || not empty Med3_B || not empty Med4_A || not empty Med4_B || 
                                  not empty Med5_A || not empty Med5_B || not empty Med6_A || not empty Med6_B}">
	                    <div class="buttonsData">
	                        <input id="medButton" type="button" value="Medications"/>
	                    </div>
                    </c:if> -->
                </div>
                <div class="questionContainer">
                    <c:choose>
	                    <c:when test="${empty Prompt1_Text}">
	                        &nbsp;
	                    </c:when>
	                    <c:otherwise>
	                       <div class="questionStem">
			                   <c:out value="${Prompt1_Text}"/>
			               </div>
			               <div class="answerContainer">
			                   <div class="answerCheckbox">
			                       <c:choose>
			                         <c:when test="${empty Answer1_1}">
			                             <input type="checkbox" name="sub_Choice1" value="1" disabled/><br/>
			                         </c:when>
			                         <c:otherwise>
			                             <input type="checkbox" name="sub_Choice1" value="1"/><c:out value="${Answer1_1}"/><br/>
			                         </c:otherwise>
			                       </c:choose>
			                   </div>
			                   <div class="answerCheckbox">
			                       <c:choose>
                                     <c:when test="${empty Answer1_3}">
                                         <input type="checkbox" name="sub_Choice1" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice1" value="3"/><c:out value="${Answer1_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
			                   </div>
			                   <div class="answerCheckbox">
			                       <c:choose>
                                     <c:when test="${empty Answer1_5}">
                                         <input type="checkbox" name="sub_Choice1" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice1" value="5"/><c:out value="${Answer1_5}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
			                   </div>
			               </div>
			               <div class="answerContainer">
			                   <div class="answerCheckbox">
			                       <c:choose>
                                     <c:when test="${empty Answer1_2}">
                                         <input type="checkbox" name="sub_Choice1" value="2" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice1" value="2"/><c:out value="${Answer1_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
			                   </div>
			                   <div class="answerCheckbox">
			                       <c:choose>
                                     <c:when test="${empty Answer1_4}">
                                         <input type="checkbox" name="sub_Choice1" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice1" value="4"/><c:out value="${Answer1_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
			                   </div>
			                   <div class="answerCheckbox">
			                       <c:choose>
                                     <c:when test="${empty Answer1_6}">
                                         <input type="checkbox" name="sub_Choice1" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice1" value="6"/><c:out value="${Answer1_6}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
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
		                        <c:out value="${Prompt2_Text}"/>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer2_1}">
                                         <input type="checkbox" name="sub_Choice2" value="1" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="1"/><c:out value="${Answer2_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer2_3}">
                                         <input type="checkbox" name="sub_Choice2" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="3"/><c:out value="${Answer2_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer2_5}">
                                         <input type="checkbox" name="sub_Choice2" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="5"/><c:out value="${Answer2_5}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer2_2}">
                                         <input type="checkbox" name="sub_Choice2" value="2" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="2"/><c:out value="${Answer2_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer2_4}">
                                         <input type="checkbox" name="sub_Choice2" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="4"/><c:out value="${Answer2_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer2_6}">
                                         <input type="checkbox" name="sub_Choice2" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="6"/><c:out value="${Answer2_6}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
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
		                        <c:out value="${Prompt3_Text}"/>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer3_1}">
                                         <input type="checkbox" name="sub_Choice3" value="1" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="1"/><c:out value="${Answer3_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer3_3}">
                                         <input type="checkbox" name="sub_Choice3" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="3"/><c:out value="${Answer3_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer3_5}">
                                         <input type="checkbox" name="sub_Choice3" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="5"/><c:out value="${Answer3_5}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer3_2}">
                                         <input type="checkbox" name="sub_Choice3" value="2" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="2"/><c:out value="${Answer3_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer3_4}">
                                         <input type="checkbox" name="sub_Choice3" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="4"/><c:out value="${Answer3_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer3_6}">
                                         <input type="checkbox" name="sub_Choice3" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="6"/><c:out value="${Answer3_6}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
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
		                        <c:out value="${Prompt4_Text}"/>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer4_1}">
                                         <input type="checkbox" name="sub_Choice4" value="1" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="1"/><c:out value="${Answer4_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer4_3}">
                                         <input type="checkbox" name="sub_Choice4" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="3"/><c:out value="${Answer4_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer4_5}">
                                         <input type="checkbox" name="sub_Choice4" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="5"/><c:out value="${Answer4_5}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer4_2}">
                                         <input type="checkbox" name="sub_Choice4" value="2" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="2"/><c:out value="${Answer4_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer4_4}">
                                         <input type="checkbox" name="sub_Choice4" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="4"/><c:out value="${Answer4_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer4_6}">
                                         <input type="checkbox" name="sub_Choice4" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="6"/><c:out value="${Answer4_6}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
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
		                       <c:out value="${Prompt5_Text}"/>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer5_1}">
                                         <input type="checkbox" name="sub_Choice5" value="1" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="1"/><c:out value="${Answer5_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer5_3}">
                                         <input type="checkbox" name="sub_Choice5" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="3"/><c:out value="${Answer5_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer5_5}">
                                         <input type="checkbox" name="sub_Choice5" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="5"/><c:out value="${Answer5_5}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer5_2}">
                                         <input type="checkbox" name="sub_Choice5" value="2" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="2"/><c:out value="${Answer5_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer5_4}">
                                         <input type="checkbox" name="sub_Choice5" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="4"/><c:out value="${Answer5_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer5_6}">
                                         <input type="checkbox" name="sub_Choice5" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="6"/><c:out value="${Answer5_6}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
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
		                        <c:out value="${Prompt6_Text}"/>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer6_1}">
                                         <input type="checkbox" name="sub_Choice6" value="1" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="1"/><c:out value="${Answer6_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer6_3}">
                                         <input type="checkbox" name="sub_Choice6" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="3"/><c:out value="${Answer6_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer6_5}">
                                         <input type="checkbox" name="sub_Choice6" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="5"/><c:out value="${Answer6_5}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                    </div>
		                    <div class="answerContainer">
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer6_2}">
                                         <input type="checkbox" name="sub_Choice6" value="2" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="2"/><c:out value="${Answer6_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer6_4}">
                                         <input type="checkbox" name="sub_Choice6" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="4"/><c:out value="${Answer6_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                        <div class="answerCheckbox">
		                            <c:choose>
                                     <c:when test="${empty Answer6_6}">
                                         <input type="checkbox" name="sub_Choice6" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="6"/><c:out value="${Answer6_6}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
		                        </div>
		                    </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div id="submitContainer">
                    <a href="#" id="submitButtonBottom" class="icon-button ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Submit</a>
                </div>
                <div id="problemDialog" title="Problem List" class="ui-dialog-titlebar ui-widget-header">
                    <table id="problemTable">
                        <tr>
                            <td class="padding5"><c:out value="${diag1}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag2}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag3}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag4}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag5}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag6}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag7}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag8}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag9}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag10}"/></td>
                        </tr>
                    </table>
                </div>
                <div id="medDialog" title="Medication List" class="ui-dialog-titlebar ui-widget-header">
                    <table id="medTable">
                        <c:if test="${not empty Med1_A || not empty Med1_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop"><c:out value="${Med1_A}"/></td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td><c:out value="${Med1_B}"/></td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med2_A || not empty Med2_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop"><c:out value="${Med2_A}}"/></td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td><c:out value="${Med2_B}"/></td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med3_A || not empty Med3_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop"><c:out value="${Med3_A}"/></td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td><c:out value="${Med3_B}"/></td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med4_A || not empty Med4_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop"><c:out value="${Med4_A}"/></td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td><c:out value="${Med4_B}"/></td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med5_A || not empty Med5_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop"><c:out value="${Med5_A}"/></td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td><c:out value="${Med5_B}"/></td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med6_A || not empty Med6_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop"><c:out value="${Med6_A}"/></td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td><c:out value="${Med6_B}"/></td>
	                        </tr>
                        </c:if>
                    </table>
                </div>
                <div id="confirmSubmitDialog" title="Confirm" class="ui-overlay">
                    <div id="confirmText">
                        <span>Click OK to permanently submit the form.</span>
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
                        <div id="formServerErrorText" class="ui-state-error"></div>
                        <br/><br/><a href="#" id="retryButton" class="icon-button ui-state-default ui-corner-all">Retry</a>
                    </div>
                    <div id="noForms">
                        There are no recommended handouts for ${PatientName}.
                    </div>
	                <div id="formAccordion">
	                </div>
                </div>
                <div id="forcePrintDialog" title="Other Handouts" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
                    <div class="pws-force-print-content">
			             <div class="force-print-forms-loading">
			                 <span id="formsLoadingPanel"><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Loading forms...</span>
			             </div>
			             <div class="force-print-forms-server-error">
			                 <div class="force-print-forms-server-error-text ui-state-error"></div>
			                 <br/><br/><a href="#" class="force-print-retry-button force-print-icon-button ui-state-default ui-corner-all">Retry</a>
			                 <a href="#" class="force-print-retry-close-button force-print-icon-button ui-state-default ui-corner-all">Close</a>
			             </div>
			             <div class="force-print-forms-container">
			                 <fieldset class="force-print-fieldset">
			                     <select class="force-print-forms"></select>
			                 </fieldset>
			             </div>
			             <div class="force-print-form-container">
			                <object class="force-print-form-object" data="" onreadystatechange="return forcePrint_formLoaded();" onload="forcePrint_formLoaded();">
			                   <span class="force-print-black-text">It appears your Web browser is not configured to display PDF files. 
			                   <a style="color:blue" href='http://get.adobe.com/reader/'>Click here to download the Adobe PDF Reader.</a>  Please restart your browser once the installation is complete.</span>
			                </object>
			             </div>
			             <div class="force-print-form-loading">
			                <span><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Creating form...</span>
			             </div>
			             <input type="hidden" value="${patientId}" id="patientId" />
			             <input type="hidden" value="${sessionId}" id="sessionId" />
			             <input type="hidden" value="${locationId}" id="locationId" />
			             <input type="hidden" value="${locationTagId}" id="locationTagId" />
                    </div>
                </div>
                <input type=hidden id= "Choice1" name="Choice1"/>
			    <input type=hidden id= "Choice2" name="Choice2"/>
			    <input type=hidden id= "Choice3" name="Choice3"/>
			    <input type=hidden id= "Choice4" name="Choice4"/>
			    <input type=hidden id= "Choice5" name="Choice5"/>
			    <input type=hidden id= "Choice6" name="Choice6"/>
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