<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />
<c:choose>
<c:when test="${scanned == 'scanned'}">  
<p>PSF ID: ${formInstanceId} was successfully scanned.</p>
<form name="input" action="fillOutPSF.form" method="get">
<input type="submit" value="scan another PSF">
<input type="hidden" value="PSF" name="formName"/>
</form>
<form name="input" action="fillOutPWS.form" method="get">
<input type="submit" value="scan PWS">
<input type="hidden" value="PWS" name="formName"/>
</form>
</c:when>
     <c:when test="${showForm == ''}">  
<p><b>Please select the PSF to scan:</b></p>
<form name="input" action="fillOutPSF.form" method="get">
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
<input type="hidden" value="PSF" name="formName"/>
</tr>
</table>
</form>
</c:when>
<c:otherwise> 
<form name="input" action="fillOutPSF.form" method="get">
<input type="submit" value="Scan">
<table class="rightAlign">
<tr>
<td colspan="3">
<table style="padding:10px" width="100%">
<tr>
<td style="vertical-align:middle;text-align:center;">${formInstanceId}</td>
<td style="vertical-align:middle;text-align:center;"><b style="font-size: 22px">CHICA Pre-Screening Form</b></td>
</tr>
</table>
</td>
<td rowspan="3">
<table style="font-weight:bold;">
<tr>
<td style="vertical-align:bottom">MRN:</td> 
<td style="text-align:left;vertical-align:bottom;"><b style="font-size: 22px">${MRN}</b></td>
</tr>
<tr>
<td>Name:</td> <td style="text-align:left">${PatientName}</td>
</tr>
<tr>
<td>Age:</td> <td style="text-align:left">${Age}&nbsp;&nbsp;&nbsp;DOB: ${DOB}</td>
</tr>
<tr>
<td>Date:</td> <td style="text-align:left">${ScheduledTime}</td>
</tr>
</table>
</td>
</tr>
<tr>
<td>${Height_HL}Height:</td>
<td style="text-align: left"><input type="text" size="3" maxlength="3" name="HeightP"/>.
<input type="text" size="1" maxlength="1" name="HeightS"/>&nbsp;${HeightSUnits}</td>
<td>Uncooperative/Unable to Screen:<br>
<center><input type="checkbox" name="NoVision" value="Y"/>Vision&nbsp;
<input type="checkbox" name="NoHearing" value="Y"/>Hearing&nbsp;
<input type="checkbox" name="NoBP" value="Y"/>BP
</center>
</td>
</tr>
<tr>
<td>${Weight_HL}Weight:</td>
<td style="text-align: left"><input type="text" size="3" maxlength="3" name="WeightP"/>${WeightPUnits}<input type="text" size="2" maxlength="2" name="WeightS"/>&nbsp;${WeightSUnits}</td>
<td>${VisionL_HL}Vision Left: 20/<input type="text" size="3" maxlength="3" name="VisionL"/></td>
</tr>
<tr>
<td>${HC_HL}HC:</td>
<td style="text-align: left"><input type="text" size="3" maxlength="3" name="HCP"/>.<input type="text" size="1" maxlength="1" name="HCS"/>&nbsp;cm.</td>
<td>${VisionR_HL}Vision Right: 20/<input type="text" size="3" maxlength="3" name="VisionR"/></td>
<td>Pulse Ox:<input type="text" size="3" maxlength="3" name="PulseOx"/>%</td>
</tr>
<tr>
<td>${BP_HL}BP:</td>
<td style="text-align: left"><input type="text" size="3" maxlength="3" name="BPS"/>/<input type="text" size="3" maxlength="3" name="BPD"/></td>
<td rowspan="3" align="right">
<table class="rightAlign">
<tr>
<td>${HearL_HL}Left Ear @ 25db:</td>
<td style="text-align: center">P</td>
<td style="text-align: center">F</td>
</tr>
<tr>
<td><input type="checkbox" value="P" name="HearL"/></td>
<td><input type="checkbox" value="F" name="HearL"/></td>
</tr>
</table>
</td>
<td rowspan="3" align="right">
<table class="rightAlign">
<tr>
<td>${HearR_HL}Right Ear @ 25db:</td>
<td style="text-align: center">P</td>
<td style="text-align: center">F</td>
</tr>
<tr>
<td><input type="checkbox" value="P" name="HearR"/></td>
<td><input type="checkbox" value="F" name="HearR"/></td>
</tr>
</table>
</td>
</tr>
<tr>
<td style="vertical-align:top">Temp:</td>
<td style="text-align: left;vertical-align:top;"><input type="text" size="3" maxlength="3" name="TempP"/>.<input type="text" size="1" maxlength="1" name="TempS"/>&nbsp;deg. F</td>
</tr>
<tr>
<td style="vertical-align:top">Pulse:</td>
<td style="text-align: left;vertical-align:top;"><input type="text" size="3" maxlength="3" name="Pulse"/>/min&nbsp;&nbsp;&nbsp;&nbsp;RR:<input type="text" size="3" maxlength="3" name="RR"/>
</tr>
</table>
	<p><b>English Questions</b></p>
<table>
<tr>
	<td align="center"><b>Y</b></td>
	<td align="center"><b>N</b></td>
	<td></td>
	<td align="center"><b>Y</b></td>
	<td align="center"><b>N</b></td>
	<td></td>
</tr>
<tr>
	<td>
	<input type="radio" name="QuestionEntry_1" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_1" value="N"/>
</td>
<td>
	${Question1}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_2" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_2" value="N"/>
</td>
	<td>
	${Question2}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_3" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_3" value="N"/>
</td>
	<td>
	${Question3}
	</td>
		<td>
	<input type="radio" name="QuestionEntry_4" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_4" value="N"/>
</td>
	<td>
	${Question4}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_5" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_5" value="N"/>
</td>
	<td>
	${Question5}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_6" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_6" value="N"/>
	</td>
	<td>

	${Question6}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_7" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_7" value="N"/>
</td>
	<td>
	${Question7}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_8" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_8" value="N"/>
</td>
	<td>
	${Question8}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_9" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_9" value="N"/>
</td>
	<td>
	${Question9}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_10" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_10" value="N"/>
	</td>
	<td>

	${Question10}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_11" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_11" value="N"/>
</td>
	<td>
	${Question11}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_12" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_12" value="N"/>
	</td>
	<td>

	${Question12}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_13" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_13" value="N"/>
	</td>
	<td>

	${Question13}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_14" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_14" value="N"/>
	</td>
	<td>

	${Question14}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_15" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_15" value="N"/>
	</td>
	<td>
	${Question15}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_16" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_16" value="N"/>
</td>
	<td>
	${Question16}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_17" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_17" value="N"/>
</td>
	<td>
	${Question17}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_18" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_18" value="N"/>
</td>
	<td>
	${Question18}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_19" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_19" value="N"/>
</td>
	<td>
	${Question19}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_20" value="Y"/>
	</td>
	<td>
	<input type="radio" name="QuestionEntry_20" value="N"/>
</td>
	<td>
	${Question20}
	</td>
	</tr>
	</table>
	<p><center>${CurrentDate}</center></p>
		<p><b>Spanish Questions</b></p>
<table>
<tr>
	<td align="center"><b>Y</b></td>
	<td align="center"><b>N</b></td>
	<td></td>
	<td align="center"><b>Y</b></td>
	<td align="center"><b>N</b></td>
	<td></td>
</tr>
<tr>
	<td><input type="radio" name="QuestionEntry_1_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_1_2" value="N"/></td>
	<td>

	${Question1_SP}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_2_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_2_2" value="N"/></td>
	<td>

	${Question2_SP}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_3_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_3_2" value="N"/></td>
	<td>

	${Question3_SP}
	</td>
		<td>
	<input type="radio" name="QuestionEntry_4_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_4_2" value="N"/></td>
	<td>

	${Question4_SP}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_5_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_5_2" value="N"/></td>
	<td>

	${Question5_SP}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_6_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_6_2" value="N"/></td>
	<td>

	${Question6_SP}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_7_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_7_2" value="N"/></td>
	<td>

	${Question7_SP}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_8_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_8_2" value="N"/></td>
	<td>

	${Question8_SP}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_9_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_9_2" value="N"/></td>
	<td>

	${Question9_SP}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_10_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_10_2" value="N"/></td>
	<td>

	${Question10_SP}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_11_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_11_2" value="N"/></td>
	<td>

	${Question11_SP}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_12_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_12_2" value="N"/></td>
	<td>

	${Question12_SP}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_13_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_13_2" value="N"/></td>
	<td>

	${Question13_SP}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_14_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_14_2" value="N"/></td>
	<td>

	${Question14_SP}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_15_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_15_2" value="N"/></td>
	<td>

	${Question15_SP}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_16_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_16_2" value="N"/></td>
	<td>

	${Question16_SP}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_17_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_17_2" value="N"/></td>
	<td>

	${Question17_SP}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_18_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_18_2" value="N"/></td>
	<td>

	${Question18_SP}
	</td>
	</tr>
	<tr>
	<td>
	<input type="radio" name="QuestionEntry_19_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_19_2" value="N"/></td>
	<td>

	${Question19_SP}
	</td>
	<td>
	<input type="radio" name="QuestionEntry_20_2" value="Y"/></td>
	<td>
	<input type="radio" name="QuestionEntry_20_2" value="N"/></td>
	<td>

	${Question20_SP}
	</td>
	</tr>
	</table>
	<input type="hidden" name="submitAnswers" value="submitAnswers"/>
	<input type="hidden" value="${formInstanceId}" name="formInstanceId"/>
	<input type="hidden" value="PSF" name="formName"/>
</form>
</c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/template/footer.jsp" %>