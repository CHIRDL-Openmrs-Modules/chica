<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />
<c:choose>
<c:when test="${scanned == 'scanned'}">  
<p>PWS ID: ${formInstanceId} was successfully scanned.</p>
<form name="input" action="fillOutPWS.form" method="get">
<input type="submit" value="scan another PWS">
<input type="hidden" value="PWS" name="formName"/>
</form>
<form name="input" action="fillOutPSF.form" method="get">
<input type="submit" value="scan PSF">
<input type="hidden" value="PSF" name="formName"/>
</form>
</c:when>
     <c:when test="${showForm == ''}">  
<p><b>Please select the PWS to scan:</b></p>
<form name="input" action="fillOutPWS.form" method="get">
<table>
<tr style="padding: 5px">
<td>
<select name="formInstanceId">
<c:forEach items="${forms}" var="form">
<option value="${form}">${form}</option>
</c:forEach>
</select>
</td>
<td><input type="submit" value="OK"></td>
<input type="hidden" value="showForm" name="showForm">
<input type="hidden" value="PWS" name="formName"/>
</tr>
</table>
</form>
</c:when>
<c:otherwise> 
<form name="input" action="fillOutPWS.form" method="get">
<input type="submit" value="Scan">
<center><h2>CHICA Physician Encounter Form</h2></center>
<table width="100%">
<tr>
<td>
<table class="rightAlign">
<tr><td><b>Patient:</b></td><td style="text-align:left">${PatientName}</td></tr>
<tr><td><b>DOB:</b></td><td style="text-align:left">${DOB}&nbsp;&nbsp;&nbsp;<b>Age:</b> ${Age}</td></tr>
<tr><td><b>Doctor:</b></td><td style="text-align:left">${Doctor}</td></tr>
</table>
</td>
<td>
<table class="rightAlign">
<tr><td><b>MRN:</b></td><td style="text-align:left">${MRN}</td></tr>
<tr><td><b>Apt. Date:</b></td><td style="text-align:left">${VisitDate}</td></tr>
<tr><td><b>Apt. Time:</b></td><td style="text-align:left">${VisitTime}</td></tr>
</table>
</td>
<td rowspan="2">
<b>Vital Signs:</b><br>
<table class="rightAlign">
<tr><td>${HeightA}Height:</td><td style="text-align:left"> ${Height}&nbsp;&nbsp;${HeightSUnits}&nbsp;(${HeightP}%)</td></tr>
<tr><td>${WeightA}Weight:</td><td style="text-align:left"> ${Weight}&nbsp;&nbsp;lb. (${WeightP}%)</td></tr>
<tr><td></td><td style="text-align:left">${WeightKG} kg.</td></tr>
<tr><td>${BMIA}BMI:</td><td style="text-align:left"> ${BMI} (${BMIP}%)</td></tr>
<tr><td>${HCA}Head Circ:</td><td style="text-align:left"> ${HC} cm.(${HCP}%)</td></tr>
<tr><td>${TempA}Temp:</td><td style="text-align:left"> ${Temperature} F</td></tr>
<tr><td>${PulseA}Pulse:</td><td style="text-align:left"> ${Pulse}</td></tr>
<tr><td>${RRA}RR:</td><td style="text-align:left">${RR}</td></tr>
<tr><td>${BPA}BP:</td><td style="text-align:left"> ${BP}</td></tr>
<tr><td>${PulseOxA}Pulse Ox:</td><td style="text-align:left"> ${PulseOx}%</td></tr>
<tr><td>${HearA}Hear (L):</td><td style="text-align:left"> ${HearL}&nbsp;&nbsp;Hear (R): ${HearR}</td></tr>
<tr><td>${VisionLA}Vision (L):</td><td style="text-align:left"> ${VisionL}</td></tr>
<tr><td>${VisionRA}Vision (R):</td><td style="text-align:left">${VisionR}</td></tr>
</table><br><br>
Allergies:${Allergy}<br>
Pain (0-10):${Pain}<br>
${AlcoholLabel} ${AlcoholAnswer}<br>
${DrugsLabel} ${DrugsAnswer}<br>
${TobaccoLabel} ${TobaccoAnswer}
</td>
</tr>
<tr>
<td colspan="2">
<b>Physical Exam:</b><br>
<table>
<tr><td></td><td style="text-align:center">Nl</td>
<td style="text-align:center">Abnl</td></tr>
<tr>
<td>${GeneralExamA}General:</td>
<td style="text-align:center"><input type="radio" name="Entry_General" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_General" value="A"/></td>
</tr>
<tr>
<td>${HeadExamA}Head:</td>
<td style="text-align:center"><input type="radio" name="Entry_Head" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_Head" value="A"/></td>
</tr>
<tr>
<td>${SkinExamA}Skin:</td>
<td style="text-align:center"><input type="radio" name="Entry_Skin" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_Skin" value="A"/></td>
</tr>
<tr>
<td>${EyesVisionExamA}Eyes:</td>
<td style="text-align:center"><input type="radio" name="Entry_Eyes" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_Eyes" value="A"/></td>
</tr>
<tr>
<td>${EarsHearingExamA}Ears:</td>
<td style="text-align:center"><input type="radio" name="Entry_Ears" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_Ears" value="A"/></td>
</tr>
<tr>
<td>${NoseThroatExamA}Nose/Throat:</td>
<td style="text-align:center"><input type="radio" name="Entry_Nose" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_Nose" value="A"/></td>
</tr>
<tr>
<td>${TeethGumsExamA}Teeth/Gums:</td>
<td style="text-align:center"><input type="radio" name="Entry_Teeth" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_Teeth" value="A"/></td>
</tr>
<tr>
<td>${NodesExamA}Nodes:</td>
<td style="text-align:center"><input type="radio" name="Entry_Nodes" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_Nodes" value="A"/></td>
</tr>
<tr>
<td>${ChestLungsExamA}Chest/Lungs:</td>
<td style="text-align:center"><input type="radio" name="Entry_Chest" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_Chest" value="A"/></td>
</tr>
<tr>
<td>${HeartPulsesExamA}Heart/Pulses:</td>
<td style="text-align:center"><input type="radio" name="Entry_Heart" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_Heart" value="A"/></td>
</tr>
<tr>
<td>${AbdomenExamA}Abdomen:</td>
<td style="text-align:center"><input type="radio" name="Entry_Abdomen" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_Abdomen" value="A"/></td>
</tr>
<tr>
<td>${ExtGenitaliaExamA}Ext. Genitalia:</td>
<td style="text-align:center"><input type="radio" name="Entry_ExtGenitalia" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_ExtGenitalia" value="A"/></td>
</tr>
<tr>
<td>${BackExamA}Back:</td>
<td style="text-align:center"><input type="radio" name="Entry_Back" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_Back" value="A"/></td>
</tr>
<tr>
<td>${NeuroExamA}Neuro:</td>
<td style="text-align:center"><input type="radio" name="Entry_Neuro" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_Neuro" value="A"/></td>
</tr>
<tr>
<td>${ExtremitiesExamA}Extremities:</td>
<td style="text-align:center"><input type="radio" name="Entry_Extremities" value="N"/></td>
<td style="text-align:center"><input type="radio" name="Entry_Extremities" value="A"/></td>
</tr>
</table>
</td>
</tr>
</table>
<br><br>
<table>
<tr style="padding: 10px">
	<td>${Prompt1_Text}<br/><br/>
	<table>
	<tr><td><input type="checkbox" name="Choice1" value="1"/>${Answer1_1}</td>
	<td><input type="checkbox" name="Choice1" value="2"/>${Answer1_2}</td></tr>
	<tr><td><input type="checkbox" name="Choice1" value="3"/>${Answer1_3}</td>
	<td><input type="checkbox" name="Choice1" value="4"/>${Answer1_4}</td></tr>
	<tr><td><input type="checkbox" name="Choice1" value="5"/>${Answer1_5}</td>
	<td><input type="checkbox" name="Choice1" value="6"/>${Answer1_6}</td></tr>
	</table>
	</td>
	<td>${Prompt2_Text}<br/><br/>
	<table>
	<tr><td><input type="checkbox" name="Choice2" value="1"/>${Answer2_1}</td>
	<td><input type="checkbox" name="Choice2" value="2"/>${Answer2_2}</td></tr>
	<tr><td><input type="checkbox" name="Choice2" value="3"/>${Answer2_3}</td>
	<td><input type="checkbox" name="Choice2" value="4"/>${Answer2_4}</td></tr>
	<tr><td><input type="checkbox" name="Choice2" value="5"/>${Answer2_5}</td>
	<td><input type="checkbox" name="Choice2" value="6"/>${Answer2_6}</td></tr>
	</table>
	</td>
</tr>
<tr style="padding: 10px">
	<td>${Prompt3_Text}<br/><br/>
	<table>
	<tr><td><input type="checkbox" name="Choice3" value="1"/>${Answer3_1}</td>
	<td><input type="checkbox" name="Choice3" value="2"/>${Answer3_2}</td></tr>
	<tr><td><input type="checkbox" name="Choice3" value="3"/>${Answer3_3}</td>
	<td><input type="checkbox" name="Choice3" value="4"/>${Answer3_4}</td></tr>
	<tr><td><input type="checkbox" name="Choice3" value="5"/>${Answer3_5}</td>
	<td><input type="checkbox" name="Choice3" value="6"/>${Answer3_6}</td></tr>
	</table>
	</td>
	<td>${Prompt4_Text}<br/><br/>
	<table>
	<tr><td><input type="checkbox" name="Choice4" value="1"/>${Answer4_1}</td>
	<td><input type="checkbox" name="Choice4" value="2"/>${Answer4_2}</td></tr>
	<tr><td><input type="checkbox" name="Choice4" value="3"/>${Answer4_3}</td>
	<td><input type="checkbox" name="Choice4" value="4"/>${Answer4_4}</td></tr>
	<tr><td><input type="checkbox" name="Choice4" value="5"/>${Answer4_5}</td>
	<td><input type="checkbox" name="Choice4" value="6"/>${Answer4_6}</td></tr>
	</table>
	</td>
</tr>
<tr style="padding: 10px">
	<td>${Prompt5_Text}<br/><br/>
	<table>
	<tr><td><input type="checkbox" name="Choice5" value="1"/>${Answer5_1}</td>
	<td><input type="checkbox" name="Choice5" value="2"/>${Answer5_2}</td></tr>
	<tr><td><input type="checkbox" name="Choice5" value="3"/>${Answer5_3}</td>
	<td><input type="checkbox" name="Choice5" value="4"/>${Answer5_4}</td></tr>
	<tr><td><input type="checkbox" name="Choice5" value="5"/>${Answer5_5}</td>
	<td><input type="checkbox" name="Choice5" value="6"/>${Answer5_6}</td></tr>
	</table>
	</td>
	<td>${Prompt6_Text}<br/><br/>
	<table>
	<tr><td><input type="checkbox" name="Choice6" value="1"/>${Answer6_1}</td>
	<td><input type="checkbox" name="Choice6" value="2"/>${Answer6_2}</td></tr>
	<tr><td><input type="checkbox" name="Choice6" value="3"/>${Answer6_3}</td>
	<td><input type="checkbox" name="Choice6" value="4"/>${Answer6_4}</td></tr>
	<tr><td><input type="checkbox" name="Choice6" value="5"/>${Answer6_5}</td>
	<td><input type="checkbox" name="Choice6" value="6"/>${Answer6_6}</td></tr>
	</table>
	</td>
</tr>

	</table><br>
	<center><b style="font-size:16px">${BottomName}</b></center>
	<center><b>${CurrentTime}</b></center>
	<input type="hidden" name="submitAnswers" value="submitAnswers"/>
	<input type="hidden" value="${formInstanceId}" name="formInstanceId"/>
	<input type="hidden" value="PWS" name="formName"/>
</form>
</c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/template/footer.jsp" %>