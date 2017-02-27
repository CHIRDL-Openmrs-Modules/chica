<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/pwsIUHCerner.css" type="text/css" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/timeout-dialog.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
        <script>var ctx = "${pageContext.request.contextPath}";</script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/pws.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/timeout-dialog.js"></script>
        <title>CHICA Physician Encounter Form</title>
    </head>

    <body>
        <div id="formContainer">
            <form id="pwsForm" name="pwsForm" action="pwsIUHCerner.form" method="post">
                <div id="titleContainer">
                    <div id="submitFormTop">
                        <a href="#" id="submitButtonTop" class="icon-button mediumButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Sign</a>
                        <a href="#" id="saveDraftButtonTop" class="icon-button mediumButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Save Draft</a>
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
                <div id="infoCenter">
                    <b>MRN:</b> <c:out value="${MRN}"/><br/>
                    <b>Date:</b> <c:out value="${VisitDate}"/><br/>
                <b>Time:</b> <c:out value="${VisitTime}"/></div>
                <div id="infoRight">
                    <b>Informant:</b> <c:out value="${Informant}"/><br/>
                    <c:out value="${Language}"/><br/>  
                    <br/>         
                </div>
                
                <div id="vitalsContainer">
                    <div id="vitals">
	                    <div class="flagCell">
	                        <b><font style="color:black;">A</font></b>
	                    </div>
	                    <div class="vitalsNames">
	                        <b>Vital Signs:</b>
	                    </div>                  
	                        <c:choose>
	                            <c:when test="${(empty VitalsProcessed) || (empty psfSubmitted)}">
	                                <c:set var="vitalsPSFProcessedFlag" value="" />
	                                <c:set var="vitalsPSFProcessedClass" value=""/>
	                            </c:when>
	                            <c:otherwise>
	                                <c:choose>
	                                    <c:when test="${VitalsProcessed == 'false'}">
	                                       <c:set var="vitalsPSFProcessedFlag" value="awaiting vitals" />
	                                       <c:set var="vitalsPSFProcessedClass" value="awaitingClass"/>
	                                    </c:when>
	                                    <c:when test="${psfSubmitted == 'false'}">
	                                       <c:set var="vitalsPSFProcessedFlag" value="awaiting pre-screener" />
	                                       <c:set var="vitalsPSFProcessedClass" value="awaitingClass"/>
	                                    </c:when>
	                                     <c:when test="${(VitalsProcessed == 'true') && (psfSubmitted == 'true')}">
	                                        <c:set var="vitalsPSFProcessedFlag" value="" />
	                                        <c:set var="vitalsPSFProcessedClass" value=""/>
	                                    </c:when>
	                                </c:choose>
	                            </c:otherwise>
	                        </c:choose>   
	                    <div class="vitalsValues ${vitalsPSFProcessedClass}">
	                        <c:out value="${vitalsPSFProcessedFlag}" />
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
	                                <c:out value="${Temperature}"/>&nbsp;&nbsp;
	                                <c:if test="${not empty Temperature_Method}">
	                                    (<c:out value="${Temperature_Method}"/>)
	                                </c:if>
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
	                    <div class="flagCell hearing">
	                        <b><c:out value="${HearA}"/></b><br/>
	                    </div>
	                    <div class="vitalsNames hearing">
	                        Hear (L):<br/>
	                    </div>
	                    <div class="vitalsValues hearing">
	                        <c:choose>
	                            <c:when test="${empty HearL}">
	                                &nbsp;
	                            </c:when>
	                            <c:otherwise>
	                                <c:out value="${HearL}"/>
	                            </c:otherwise>
	                        </c:choose>
	                    </div>
	                    <div class="flagCell hearing">
	                        <b><c:out value="${HearA}"/></b><br/>
	                    </div>
	                    <div class="vitalsNames hearing">
	                        Hear (R):<br/>
	                    </div>
	                    <div class="vitalsValues hearing">
	                        <c:choose>
	                            <c:when test="${empty HearR}">
	                                &nbsp;
	                            </c:when>
	                            <c:otherwise>
	                                <c:out value="${HearR}"/>
	                            </c:otherwise>
	                        </c:choose>
	                    </div>
	                    <div class="flagCell vision">
	                        <b><c:out value="${VisionLA}"/></b><br/>
	                    </div>
	                    <div class="vitalsNames vision">
	                        Vision (L):<br/>
	                    </div>
	                    <div class="vitalsValues vision">
	                        <c:choose>
	                            <c:when test="${empty VisionL}">
	                                &nbsp;
	                            </c:when>
	                            <c:otherwise>
	                                <c:out value="${VisionL}"/>&nbsp;<c:out value="${VisionL_Corrected}"/>
	                            </c:otherwise>
	                        </c:choose>
	                    </div>
	                    <div class="flagCell vision">
	                        <b><c:out value="${VisionRA}"/></b><br/>
	                    </div>
	                    <div class="vitalsNames vision">
	                        Vision (R):<br/>
	                    </div>
	                    <div class="vitalsValues vision">
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
	                    <div class="flagCell" style="height: 23px;">
	                        <b></b><br/>
	                    </div>
	                    <div class="vitalsNames" style="height: 23px;">
	                        Prev WT:<br/>
	                    </div>
	                    <div class="vitalsValues" style="height: 23px;">
	                        <c:choose>
	                            <c:when test="${empty PrevWeight}">
	                                &nbsp;
	                            </c:when>
	                            <c:otherwise>
	                                <c:out value="${PrevWeight}"/>&nbsp;(<c:out value="${PrevWeightDate}"/>)
	                            </c:otherwise>
	                        </c:choose>
	                    </div> 
	                    <div id="vitalsLegendIUHCerner">
	                    <b><font style="color:red;">*</font>=Abnormal, U=Uncorrected,<br/>
	                    C=Corrected, A=Axillary,
	                    R=Rectal, O=Oral,<br/>
	                    F=Failed, P=Passed</b></div>             
	                    <div id="vitalsLegendEskenaziEpic">
	                    <b><font style="color:red;">*</font>=Abnormal,<br/>
	                    A=Axillary, R=Rectal, O=Oral</b></div>         
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
                </div>
                <div id="questionAnswerContainer">
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
	                                         <input type="checkbox" name="sub_Choice1" value="1" ${fn:contains(Choice1, '1') ? 'checked' : ''}/><c:out value="${Answer1_1}"/><br/>
	                                     </c:otherwise>
	                                   </c:choose>
	                               </div>
	                               <div class="answerCheckbox">
	                                   <c:choose>
	                                     <c:when test="${empty Answer1_3}">
	                                         <input type="checkbox" name="sub_Choice1" value="3" disabled/><br/>
	                                     </c:when>
	                                     <c:otherwise>
	                                         <input type="checkbox" name="sub_Choice1" value="3" ${fn:contains(Choice1, '3') ? 'checked' : ''}/><c:out value="${Answer1_3}"/><br/>
	                                     </c:otherwise>
	                                   </c:choose>
	                               </div>
	                               <div class="answerCheckbox">
	                                   <c:choose>
	                                     <c:when test="${empty Answer1_5}">
	                                         <input type="checkbox" name="sub_Choice1" value="5" disabled/><br/>
	                                     </c:when>
	                                     <c:otherwise>
	                                         <input type="checkbox" name="sub_Choice1" value="5" ${fn:contains(Choice1, '5') ? 'checked' : ''}/><c:out value="${Answer1_5}"/><br/>
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
	                                         <input type="checkbox" name="sub_Choice1" value="2" ${fn:contains(Choice1, '2') ? 'checked' : ''}/><c:out value="${Answer1_2}"/><br/>
	                                     </c:otherwise>
	                                   </c:choose>
	                               </div>
	                               <div class="answerCheckbox">
	                                   <c:choose>
	                                     <c:when test="${empty Answer1_4}">
	                                         <input type="checkbox" name="sub_Choice1" value="4" disabled/><br/>
	                                     </c:when>
	                                     <c:otherwise>
	                                         <input type="checkbox" name="sub_Choice1" value="4" ${fn:contains(Choice1, '4') ? 'checked' : ''}/><c:out value="${Answer1_4}"/><br/>
	                                     </c:otherwise>
	                                   </c:choose>
	                               </div>
	                               <div class="answerCheckbox">
	                                   <c:choose>
	                                     <c:when test="${empty Answer1_6}">
	                                         <input type="checkbox" name="sub_Choice1" value="6" disabled/><br/>
	                                     </c:when>
	                                     <c:otherwise>
	                                         <input type="checkbox" name="sub_Choice1" value="6" ${fn:contains(Choice1, '6') ? 'checked' : ''}/><c:out value="${Answer1_6}"/><br/>
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
                                         <input type="checkbox" name="sub_Choice2" value="1" ${fn:contains(Choice2, '1') ? 'checked' : ''}/><c:out value="${Answer2_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer2_3}">
                                         <input type="checkbox" name="sub_Choice2" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="3" ${fn:contains(Choice2, '3') ? 'checked' : ''}/><c:out value="${Answer2_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer2_5}">
                                         <input type="checkbox" name="sub_Choice2" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="5" ${fn:contains(Choice2, '5') ? 'checked' : ''}/><c:out value="${Answer2_5}"/><br/>
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
                                         <input type="checkbox" name="sub_Choice2" value="2" ${fn:contains(Choice2, '2') ? 'checked' : ''}/><c:out value="${Answer2_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer2_4}">
                                         <input type="checkbox" name="sub_Choice2" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="4" ${fn:contains(Choice2, '4') ? 'checked' : ''}/><c:out value="${Answer2_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer2_6}">
                                         <input type="checkbox" name="sub_Choice2" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="6" ${fn:contains(Choice2, '6') ? 'checked' : ''}/><c:out value="${Answer2_6}"/><br/>
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
                                         <input type="checkbox" name="sub_Choice3" value="1" ${fn:contains(Choice3, '1') ? 'checked' : ''}/><c:out value="${Answer3_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer3_3}">
                                         <input type="checkbox" name="sub_Choice3" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="3" ${fn:contains(Choice3, '3') ? 'checked' : ''}/><c:out value="${Answer3_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer3_5}">
                                         <input type="checkbox" name="sub_Choice3" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="5" ${fn:contains(Choice3, '5') ? 'checked' : ''}/><c:out value="${Answer3_5}"/><br/>
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
                                         <input type="checkbox" name="sub_Choice3" value="2" ${fn:contains(Choice3, '2') ? 'checked' : ''}/><c:out value="${Answer3_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer3_4}">
                                         <input type="checkbox" name="sub_Choice3" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="4" ${fn:contains(Choice3, '4') ? 'checked' : ''}/><c:out value="${Answer3_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer3_6}">
                                         <input type="checkbox" name="sub_Choice3" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="6" ${fn:contains(Choice3, '6') ? 'checked' : ''}/><c:out value="${Answer3_6}"/><br/>
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
                                         <input type="checkbox" name="sub_Choice4" value="1" ${fn:contains(Choice4, '1') ? 'checked' : ''}/><c:out value="${Answer4_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer4_3}">
                                         <input type="checkbox" name="sub_Choice4" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="3" ${fn:contains(Choice4, '3') ? 'checked' : ''}/><c:out value="${Answer4_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer4_5}">
                                         <input type="checkbox" name="sub_Choice4" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="5" ${fn:contains(Choice4, '5') ? 'checked' : ''}/><c:out value="${Answer4_5}"/><br/>
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
                                         <input type="checkbox" name="sub_Choice4" value="2" ${fn:contains(Choice4, '2') ? 'checked' : ''}/><c:out value="${Answer4_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer4_4}">
                                         <input type="checkbox" name="sub_Choice4" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="4" ${fn:contains(Choice4, '4') ? 'checked' : ''}/><c:out value="${Answer4_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer4_6}">
                                         <input type="checkbox" name="sub_Choice4" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="6" ${fn:contains(Choice4, '6') ? 'checked' : ''}/><c:out value="${Answer4_6}"/><br/>
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
                                         <input type="checkbox" name="sub_Choice5" value="1" ${fn:contains(Choice5, '1') ? 'checked' : ''}/><c:out value="${Answer5_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer5_3}">
                                         <input type="checkbox" name="sub_Choice5" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="3" ${fn:contains(Choice5, '3') ? 'checked' : ''}/><c:out value="${Answer5_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer5_5}">
                                         <input type="checkbox" name="sub_Choice5" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="5" ${fn:contains(Choice5, '5') ? 'checked' : ''}/><c:out value="${Answer5_5}"/><br/>
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
                                         <input type="checkbox" name="sub_Choice5" value="2" ${fn:contains(Choice5, '2') ? 'checked' : ''}/><c:out value="${Answer5_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer5_4}">
                                         <input type="checkbox" name="sub_Choice5" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="4" ${fn:contains(Choice5, '4') ? 'checked' : ''}/><c:out value="${Answer5_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer5_6}">
                                         <input type="checkbox" name="sub_Choice5" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="6" ${fn:contains(Choice5, '6') ? 'checked' : ''}/><c:out value="${Answer5_6}"/><br/>
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
                                         <input type="checkbox" name="sub_Choice6" value="1" ${fn:contains(Choice6, '1') ? 'checked' : ''}/><c:out value="${Answer6_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer6_3}">
                                         <input type="checkbox" name="sub_Choice6" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="3" ${fn:contains(Choice6, '3') ? 'checked' : ''}/><c:out value="${Answer6_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer6_5}">
                                         <input type="checkbox" name="sub_Choice6" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="5" ${fn:contains(Choice6, '5') ? 'checked' : ''}/><c:out value="${Answer6_5}"/><br/>
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
                                         <input type="checkbox" name="sub_Choice6" value="2" ${fn:contains(Choice6, '2') ? 'checked' : ''}/><c:out value="${Answer6_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer6_4}">
                                         <input type="checkbox" name="sub_Choice6" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="4" ${fn:contains(Choice6, '4') ? 'checked' : ''}/><c:out value="${Answer6_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer6_6}">
                                         <input type="checkbox" name="sub_Choice6" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="6" ${fn:contains(Choice6, '6') ? 'checked' : ''}/><c:out value="${Answer6_6}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                </div>
                 
                <div id="submitContainer">
                    <a href="#" id="submitButtonBottom" class="icon-button mediumButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Sign</a>
                    <a href="#" id="saveDraftButtonBottom" class="icon-button mediumButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Save Draft</a>
                </div>
                
                <%@ include file="pwsDialogs.jsp" %>
                
                
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
                <input id="providerId" name="providerId" type="hidden" value="${providerId}"/>
                <input id="patientNameForcePrint" name="patientNameForcePrint" type="hidden" value="${PatientName}"/>
                <input id="sessionTimeout" name="sessionTimeout" type="hidden" value="${pageContext.session.maxInactiveInterval}"/>
                <input id="sessionTimeoutWarning" name="sessionTimeoutWarning" type="hidden" value="${sessionTimeoutWarning}"/>
            </form>
        </div>
    </body>
</html> 
