<%@ include file="/WEB-INF/template/include.jsp" %>

<!doctype html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/LoginMobile.form" redirect="/module/chica/psfMobile.form" />
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/browserFixMobile.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.blockUI.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/MCHATMobile.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
		<title>M-CHAT</title>
	</head>
	<c:set var="search" value="'" />
	<c:set var="replace" value="\\'" />
	<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}" />
	<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}" />
	
	<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">
		<form id="MCHATForm" method="post" action="MCHATMobile.form" enctype="multipart/form-data">
			<c:if test="${errorMessage != null}">
				<div id="error_dialog" class="extended-header" data-role="dialog" data-close-btn="none" data-dismissible="false" data-theme="b" data-overlay-theme="c">
					<div data-role="header" data-theme="b">
						<h1>Error</h1>
					</div>
					<div data-role="content">
						<span>${errorMessage}</span>
						<div style="margin:0 auto; text-align:center;">
							<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="submitEmptyForm()" style="width:150px;">OK</a>
						</div>
					</div>
				</div> 
			</c:if><!--End Error Message-->
			
			<div id="instruction_page" data-role="page" data-theme="b">
				<div data-role="header">
					<h1>M-CHAT:</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguage('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a class="vitalsButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.navigateToVitals()" data-transition="pop" data-icon="forward">Staff</a>
				</div>
				
				<div data-role="content">
					<strong><span id="additionalQuestions">Please fill out the following about how your child usually is. Please try to answer every question. If the behavior is rare (e.g., you've seen it once or twice), please answer as if the child does not do it.</span></strong>
					<div><br /></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a id="startButton" href="#" onClick="changePage(1)" data-role="button" data-theme="b" style="width:150px;">Start</a>
				</div>
			</div> <!--End Instructions Page-->
			<c:set var="copyright" value='&copy; 1999 Diana Robins, Deborah Fein, &amp; Marianne Barton' />
			
			<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>M-CHAT:</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a class="vitalsButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.navigateToVitals()" data-transition="pop" data-icon="forward">Staff</a>
				</div>
				
				<div id="content_1" data-role="content">
					<c:set var="quest1" value='Does your child enjoy being swung, bounced on your knee, etc.?' />
					<input id="Question_1"  name="Question_1" type="hidden" value="${quest1}" />
					<strong>${quest1}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest1}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_1" id="Choice_1_Yes" value="passed" data-theme="c" />
							<label for="Choice_1_Yes">Yes</label>
							<input type="radio" name="Choice_1" id="Choice_1_No" value="failed" data-theme="c" />
							<label for="Choice_1_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest2" value='Does your child take an interest in other children?' />
					<input id="Question_2"  name="Question_2" type="hidden" value="${quest2}" />
					<strong>${quest2}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest2}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_2" id="Choice_2_Yes" value="passed" data-theme="c" />
							<label for="Choice_2_Yes">Yes</label>
							<input type="radio" name="Choice_2" id="Choice_2_No" value="failed" data-theme="c" />
							<label for="Choice_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest3" value='Does your child like climbing on things, such as up stairs?' />
					<input id="Question_3"  name="Question_3" type="hidden" value="${quest3}" />
					<strong>${quest3}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest3}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_3" id="Choice_3_Yes" value="passed" data-theme="c" />
							<label for="Choice_3_Yes">Yes</label>
							<input type="radio" name="Choice_3" id="Choice_3_No" value="failed" data-theme="c" />
							<label for="Choice_3_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest4" value='Does your child enjoy playing peek-a-boo/hide-and-seek?' />
					<input id="Question_4"  name="Question_4" type="hidden" value="${quest4}" />
					<strong>${quest4}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest4}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_4" id="Choice_4_Yes" value="passed" data-theme="c" />
							<label for="Choice_4_Yes">Yes</label>
							<input type="radio" name="Choice_4" id="Choice_4_No" value="failed" data-theme="c" />
							<label for="Choice_4_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest5" value='Does your child ever pretend, for example, to talk on the phone or take care of a doll or pretend other things?' />
					<input id="Question_5"  name="Question_5" type="hidden" value="${quest5}" />
					<strong>${quest5}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest5}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_5" id="Choice_5_Yes" value="passed" data-theme="c" />
							<label for="Choice_5_Yes">Yes</label>
							<input type="radio" name="Choice_5" id="Choice_5_No" value="failed" data-theme="c" />
							<label for="Choice_5_No">No</label>
						</fieldset>
					</div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(2)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Next</a>
				</div>
			</div>
			
			<div id="question_page_2" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>M-CHAT:</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage2Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a class="vitalsButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.navigateToVitals()" data-transition="pop" data-icon="forward">Staff</a>
				</div>
				
				<div id="content_2" data-role="content">
					<c:set var="quest6" value='Does your child ever use his/her index finger to point, to ask for something?' />
					<input id="Question_6"  name="Question_6" type="hidden" value="${quest6}" />
					<strong>${quest6}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest6}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_6" id="Choice_6_Yes" value="passed" data-theme="c" />
							<label for="Choice_6_Yes">Yes</label>
							<input type="radio" name="Choice_6" id="Choice_6_No" value="failed" data-theme="c" />
							<label for="Choice_6_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest7" value='Does your child ever use his/her index finger to point, to indicate interest in something?' />
					<input id="Question_7"  name="Question_7" type="hidden" value="${quest7}" />
					<strong>${quest7}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest7}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_7" id="Choice_7_Yes" value="passed" data-theme="c" />
							<label for="Choice_7_Yes">Yes</label>
							<input type="radio" name="Choice_7" id="Choice_7_No" value="failed" data-theme="c" />
							<label for="Choice_7_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest8" value='Can your child play properly with small toys (e.g. cars or blocks) without just mouthing, fiddling, or dropping them?' />
					<input id="Question_8"  name="Question_8" type="hidden" value="${quest8}" />
					<strong>${quest8}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest8}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_8" id="Choice_8_Yes" value="passed" data-theme="c" />
							<label for="Choice_8_Yes">Yes</label>
							<input type="radio" name="Choice_8" id="Choice_8_No" value="failed" data-theme="c" />
							<label for="Choice_8_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest9" value='Does your child ever bring objects over to you (parent) to show you something?' />
					<input id="Question_9"  name="Question_9" type="hidden" value="${quest9}" />
					<strong>${quest9}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest9}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_9" id="Choice_9_Yes" value="passed" data-theme="c" />
							<label for="Choice_9_Yes">Yes</label>
							<input type="radio" name="Choice_9" id="Choice_9_No" value="failed" data-theme="c" />
							<label for="Choice_9_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest10" value='Does your child look you in the eye for more than a second or two?' />
					<input id="Question_10"  name="Question_10" type="hidden" value="${quest10}" />
					<strong>${quest10}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest10}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_10" id="Choice_10_Yes" value="passed" data-theme="c" />
							<label for="Choice_10_Yes">Yes</label>
							<input type="radio" name="Choice_10" id="Choice_10_No" value="failed" data-theme="c" />
							<label for="Choice_10_No">No</label>
						</fieldset>
					</div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(1)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Previous</a>
					<a href="#" onClick="changePage(3)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Next</a>
				</div>
			</div>
			
			<div id="question_page_3" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>M-CHAT:</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage3Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a class="vitalsButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.navigateToVitals()" data-transition="pop" data-icon="forward">Staff</a>
				</div>
				
				<div id="content_3" data-role="content">
					<c:set var="quest11" value='Does your child ever seem oversensitive to noise? (e.g., plugging ears)' />
					<input id="Question_11"  name="Question_11" type="hidden" value="${quest11}" />
					<strong>${quest11}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest11}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_11" id="Choice_11_Yes" value="failed" data-theme="c" />
							<label for="Choice_11_Yes">Yes</label>
							<input type="radio" name="Choice_11" id="Choice_11_No" value="passed" data-theme="c" />
							<label for="Choice_11_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest12" value='Does your child smile in response to your face or your smile?' />
					<input id="Question_12"  name="Question_12" type="hidden" value="${quest12}" />
					<strong>${quest12}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest12}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_12" id="Choice_12_Yes" value="passed" data-theme="c" />
							<label for="Choice_12_Yes">Yes</label>
							<input type="radio" name="Choice_12" id="Choice_12_No" value="failed" data-theme="c" />
							<label for="Choice_12_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest13" value='Does your child imitate you? (e.g., you make a face-will your child imitate it?)' />
					<input id="Question_13"  name="Question_13" type="hidden" value="${quest13}" />
					<strong>${quest13}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest13}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_13" id="Choice_13_Yes" value="passed" data-theme="c" />
							<label for="Choice_13_Yes">Yes</label>
							<input type="radio" name="Choice_13" id="Choice_13_No" value="failed" data-theme="c" />
							<label for="Choice_13_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest14" value='Does your child respond to his/her name when you call?' />
					<input id="Question_14"  name="Question_14" type="hidden" value="${quest14}" />
					<strong>${quest14}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest14}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_14" id="Choice_14_Yes" value="passed" data-theme="c" />
							<label for="Choice_14_Yes">Yes</label>
							<input type="radio" name="Choice_14" id="Choice_14_No" value="failed" data-theme="c" />
							<label for="Choice_14_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest15" value='If you point at a toy across the room, does your child look at it?' />
					<input id="Question_15"  name="Question_15" type="hidden" value="${quest15}" />
					<strong>${quest15}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest15}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_15" id="Choice_15_Yes" value="passed" data-theme="c" />
							<label for="Choice_15_Yes">Yes</label>
							<input type="radio" name="Choice_15" id="Choice_15_No" value="failed" data-theme="c" />
							<label for="Choice_15_No">No</label>
						</fieldset>
					</div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(2)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Previous</a>
					<a href="#" onClick="changePage(4)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Next</a>
				</div>
			</div>
			
			<div id="question_page_4" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>M-CHAT:</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage4Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a class="vitalsButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.navigateToVitals()" data-transition="pop" data-icon="forward">Staff</a>
				</div>
				
				<div id="content_4" data-role="content">
					<c:set var="quest16" value='Does your child walk?' />
					<input id="Question_16"  name="Question_16" type="hidden" value="${quest16}" />
					<strong>${quest16}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest16}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_16" id="Choice_16_Yes" value="passed" data-theme="c" />
							<label for="Choice_16_Yes">Yes</label>
							<input type="radio" name="Choice_16" id="Choice_16_No" value="failed" data-theme="c" />
							<label for="Choice_16_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest17" value='Does your child look at things you are looking at?' />
					<input id="Question_17"  name="Question_17" type="hidden" value="${quest17}" />
					<strong>${quest17}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest17}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_17" id="Choice_17_Yes" value="passed" data-theme="c" />
							<label for="Choice_17_Yes">Yes</label>
							<input type="radio" name="Choice_17" id="Choice_17_No" value="failed" data-theme="c" />
							<label for="Choice_17_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest18" value='Does your child make unusual finger movements near his/her face?' />
					<input id="Question_18"  name="Question_18" type="hidden" value="${quest18}" />
					<strong>${quest18}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest18}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_18" id="Choice_18_Yes" value="failed" data-theme="c" />
							<label for="Choice_18_Yes">Yes</label>
							<input type="radio" name="Choice_18" id="Choice_18_No" value="passed" data-theme="c" />
							<label for="Choice_18_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest19" value='Does your child try to attract your attention to his/her own activity?' />
					<input id="Question_19"  name="Question_19" type="hidden" value="${quest19}" />
					<strong>${quest19}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest19}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_19" id="Choice_19_Yes" value="passed" data-theme="c" />
							<label for="Choice_19_Yes">Yes</label>
							<input type="radio" name="Choice_19" id="Choice_19_No" value="failed" data-theme="c" />
							<label for="Choice_19_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest20" value='Have you ever wondered if your child is deaf?' />
					<input id="Question_20"  name="Question_20" type="hidden" value="${quest20}" />
					<strong>${quest20}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest20}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_20" id="Choice_20_Yes" value="failed" data-theme="c" />
							<label for="Choice_20_Yes">Yes</label>
							<input type="radio" name="Choice_20" id="Choice_20_No" value="passed" data-theme="c" />
							<label for="Choice_20_No">No</label>
						</fieldset>
					</div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(3)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Previous</a>
					<a href="#" onClick="changePage(5)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Next</a>
				</div>
			</div>
			
			<div id="question_page_5" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>M-CHAT:</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage5Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a class="vitalsButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.navigateToVitals()" data-transition="pop" data-icon="forward">Staff</a>
				</div>
				
				<div id="content_5" data-role="content">
					<c:set var="quest21" value='Does your child understand what people say?' />
					<input id="Question_21"  name="Question_21" type="hidden" value="${quest21}" />
					<strong>${quest21}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest21}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_21" id="Choice_21_Yes" value="passed" data-theme="c" />
							<label for="Choice_21_Yes">Yes</label>
							<input type="radio" name="Choice_21" id="Choice_21_No" value="failed" data-theme="c" />
							<label for="Choice_21_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest22" value='Does your child sometimes stare at nothing or wander with no purpose?' />
					<input id="Question_22"  name="Question_22" type="hidden" value="${quest22}" />
					<strong>${quest22}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest22}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_22" id="Choice_22_Yes" value="failed" data-theme="c" />
							<label for="Choice_22_Yes">Yes</label>
							<input type="radio" name="Choice_22" id="Choice_22_No" value="passed" data-theme="c" />
							<label for="Choice_22_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest23" value='Does your child look at your face to check your reaction when faced with something unfamiliar?' />
					<input id="Question_23"  name="Question_23" type="hidden" value="${quest23}" />
					<strong>${quest23}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest23}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_23" id="Choice_23_Yes" value="passed" data-theme="c" />
							<label for="Choice_23_Yes">Yes</label>
							<input type="radio" name="Choice_23" id="Choice_23_No" value="failed" data-theme="c" />
							<label for="Choice_23_No">No</label>
						</fieldset>
					</div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
					<div id="not_finished_final_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>Not Completed</h1>
						</div>
						<div data-role="content">
							<span>This form is not complete. Are you sure you want to continue?</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" onClick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width:150px;">Yes</a>
								<a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width:150px;">No</a>
							</div>
						</div>
					</div>
					<div id="finish_error_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>Error</h1>
						</div>
						<div data-role="content">
							<span>There was an error submitting the form. Please press 'OK' to try again.</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" onClick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width:150px;">OK</a>
							</div>
						</div>
					</div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(4)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Previous</a>
					<a href="#" onClick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Finish</a>
				</div>
			</div>
			
			<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>M-CHAT:</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
					<a class="vitalsButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.navigateToVitals()" data-transition="pop" data-icon="forward">Personal</a>
				</div>
				
				<div id="content_1_sp" data-role="content">
					<c:set var="quest1_2_sp" value='&iquest;Disfruta su ni&ntilde;o (a) cuando lo balancean o hacen saltar sobre su rodilla?' />
					<input id="Question_1_2"  name="Question_1_2" type="hidden" value="${quest1_2_sp}" />
					<strong>${quest1_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest1_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_1_sp" id="Choice_1_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_1_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_1_sp" id="Choice_1_sp_No" value="failed" data-theme="c" />
							<label for="Choice_1_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest2_2_sp" value='&iquest;Se interesa su ni&ntilde;o (a) en otros ni&ntilde;os?' />
					<input id="Question_2_2"  name="Question_2_2" type="hidden" value="${quest2_2_sp}" />
					<strong>${quest2_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest2_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_2_sp" id="Choice_2_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_2_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_2_sp" id="Choice_2_sp_No" value="failed" data-theme="c" />
							<label for="Choice_2_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest3_2_sp" value='&iquest;Le gusta a su ni&ntilde;o (a) subirse a las cosas, por ejemplo subir las escaleras?' />
					<input id="Question_3_2"  name="Question_3_2" type="hidden" value="${quest3_2_sp}" />
					<strong>${quest3_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest3_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_3_sp" id="Choice_3_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_3_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_3_sp" id="Choice_3_sp_No" value="failed" data-theme="c" />
							<label for="Choice_3_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest4_2_sp" value='&iquest;Disfruta su ni&ntilde;o (a) jugando "peek-a-boo" o "hide and seek" (a las escondidas)?' />
					<input id="Question_4_2"  name="Question_4_2" type="hidden" value="${quest4_2_sp}" />
					<strong>${quest4_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest4_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_4_sp" id="Choice_4_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_4_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_4_sp" id="Choice_4_sp_No" value="failed" data-theme="c" />
							<label for="Choice_4_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest5_2_sp" value='&iquest;Le gusta a su ni&ntilde;o (a) jugar a pretendar, como por ejemplo, pretende que habla por tel&eacute;fono, que cuida sus mu&ntilde;ecas, o pretende otras cosas?' />
					<input id="Question_5_2"  name="Question_5_2" type="hidden" value="${quest5_2_sp}" />
					<strong>${quest5_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest5_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_5_sp" id="Choice_5_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_5_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_5_sp" id="Choice_5_sp_No" value="failed" data-theme="c" />
							<label for="Choice_5_sp_No">No</label>
						</fieldset>
					</div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(2)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Proximo</a>
				</div>
			</div>
			
			<div id="question_page_2_sp" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>M-CHAT:</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage2SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
					<a class="vitalsButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.navigateToVitals()" data-transition="pop" data-icon="forward">Personal</a>
				</div>
				
				<div id="content_2_sp" data-role="content">
					<c:set var="quest6_2_sp" value='&iquest;Utiliza su ni&ntilde;o (a) su dedo &iacute;ndice para se&ntilde;alar algo, o para preguntar alguna cosa?' />
					<input id="Question_6_2"  name="Question_6_2" type="hidden" value="${quest6_2_sp}" />
					<strong>${quest6_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest6_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_6_sp" id="Choice_6_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_6_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_6_sp" id="Choice_6_sp_No" value="failed" data-theme="c" />
							<label for="Choice_6_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest7_2_sp" value='&iquest;Usa su ni&ntilde;o (a) su dedo &iacute;ndice para se&ntilde;alar o indicar inter&eacute;s en algo?' />
					<input id="Question_7_2"  name="Question_7_2" type="hidden" value="${quest7_2_sp}" />
					<strong>${quest7_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest7_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_7_sp" id="Choice_7_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_7_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_7_sp" id="Choice_7_sp_No" value="failed" data-theme="c" />
							<label for="Choice_7_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest8_2_sp" value='&iquest;Puede su ni&ntilde;o (a) jugar bien con jugetes peque&ntilde;os (como carros o cubos) sin llevárselos a la boca, manipularlos o dejarlos caer)?' />
					<input id="Question_8_2"  name="Question_8_2" type="hidden" value="${quest8_2_sp}" />
					<strong>${quest8_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest8_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_8_sp" id="Choice_8_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_8_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_8_sp" id="Choice_8_sp_No" value="failed" data-theme="c" />
							<label for="Choice_8_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest9_2_sp" value='&iquest;Le trae su ni&ntilde;o (a) a usted (padre o madre) objetos o cosas, con el prop&oacute;sito de mostrarle algo alguna vez?' />
					<input id="Question_9_2"  name="Question_9_2" type="hidden" value="${quest9_2_sp}" />
					<strong>${quest9_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest9_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_9_sp" id="Choice_9_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_9_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_9_sp" id="Choice_9_sp_No" value="failed" data-theme="c" />
							<label for="Choice_9_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest10_2_sp" value='&iquest;Lo mira su ni&ntilde;o (a) directamente a los ojos por mas de uno o dos segundos?' />
					<input id="Question_10_2"  name="Question_10_2" type="hidden" value="${quest10_2_sp}" />
					<strong>${quest10_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest10_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_10_sp" id="Choice_10_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_10_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_10_sp" id="Choice_10_sp_No" value="failed" data-theme="c" />
							<label for="Choice_10_sp_No">No</label>
						</fieldset>
					</div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(1)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Anterior</a>
					<a href="#" onClick="changePage(3)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Proximo</a>
				</div>
			</div>
			
			<div id="question_page_3_sp" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>M-CHAT:</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage3SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
					<a class="vitalsButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.navigateToVitals()" data-transition="pop" data-icon="forward">Personal</a>
				</div>
				
				<div id="content_3_sp" data-role="content">
					<c:set var="quest11_2_sp" value='&iquest;Parece su ni&ntilde;o (a) ser demasiado sensitivo al ruido? (por ejemplo, se tapa los oidos)?' />
					<input id="Question_11_2"  name="Question_11_2" type="hidden" value="${quest11_2_sp}" />
					<strong>${quest11_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest11_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_11_sp" id="Choice_11_sp_Yes" value="failed" data-theme="c" />
							<label for="Choice_11_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_11_sp" id="Choice_11_sp_No" value="passed" data-theme="c" />
							<label for="Choice_11_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest12_2_sp" value='&iquest;Sonrie su ni&ntilde;o (a) en respuesta a su cara o a su sonrisa?' />
					<input id="Question_12_2"  name="Question_12_2" type="hidden" value="${quest12_2_sp}" />
					<strong>${quest12_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest12_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_12_sp" id="Choice_12_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_12_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_12_sp" id="Choice_12_sp_No" value="failed" data-theme="c" />
							<label for="Choice_12_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest13_2_sp" value='&iquest;Lo imita su ni&ntilde;o (a)? Por ejemplo, si usted le hace una mueca,  su ni&ntilde;o (a) trata de imitarlo?' />
					<input id="Question_13_2"  name="Question_13_2" type="hidden" value="${quest13_2_sp}" />
					<strong>${quest13_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest13_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_13_sp" id="Choice_13_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_13_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_13_sp" id="Choice_13_sp_No" value="failed" data-theme="c" />
							<label for="Choice_13_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest14_2_sp" value='&iquest;Responde su ni&ntilde;o (a) a su nombre cuando lo(a) llaman?' />
					<input id="Question_14_2"  name="Question_14_2" type="hidden" value="${quest14_2_sp}" />
					<strong>${quest14_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest14_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_14_sp" id="Choice_14_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_14_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_14_sp" id="Choice_14_sp_No" value="failed" data-theme="c" />
							<label for="Choice_14_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest15_2_sp" value='&iquest;Si usted se&ntilde;ala a un juguete que est&aacute;al otro lado de la habitaci&oacute;n a su ni&ntilde;o (a), lo mira?' />
					<input id="Question_15_2"  name="Question_15_2" type="hidden" value="${quest15_2_sp}" />
					<strong>${quest15_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest15_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_15_sp" id="Choice_15_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_15_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_15_sp" id="Choice_15_sp_No" value="failed" data-theme="c" />
							<label for="Choice_15_sp_No">No</label>
						</fieldset>
					</div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(2)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Anterior</a>
					<a href="#" onClick="changePage(4)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Proximo</a>
				</div>
			</div>
			
			<div id="question_page_4_sp" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>M-CHAT:</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage4SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
					<a class="vitalsButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.navigateToVitals()" data-transition="pop" data-icon="forward">Personal</a>
				</div>
				
				<div id="content_4_sp" data-role="content">
					<c:set var="quest16_2_sp" value='&iquestCamina su ni&ntilde;o (a)?' />
					<input id="Question_16_2"  name="Question_16_2" type="hidden" value="${quest16_2_sp}" />
					<strong>${quest16_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest16_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_16_sp" id="Choice_16_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_16_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_16_sp" id="Choice_16_sp_No" value="failed" data-theme="c" />
							<label for="Choice_16_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest17_2_sp" value='&iquest;Presta su ni&ntilde;o (a) atenci&oacute;n a las cosas que usted est&aacute; mirando?' />
					<input id="Question_17_2"  name="Question_17_2" type="hidden" value="${quest17_2_sp}" />
					<strong>${quest17_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest17_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_17_sp" id="Choice_17_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_17_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_17_sp" id="Choice_17_sp_No" value="failed" data-theme="c" />
							<label for="Choice_17_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest18_2_sp" value='&iquest;Hace su ni&ntilde;o (a) movimientos raros con los dedos cerca de su cara?' />
					<input id="Question_18_2"  name="Question_18_2" type="hidden" value="${quest18_2_sp}" />
					<strong>${quest18_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest18_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_18_sp" id="Choice_18_sp_Yes" value="failed" data-theme="c" />
							<label for="Choice_18_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_18_sp" id="Choice_18_sp_No" value="passed" data-theme="c" />
							<label for="Choice_18_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest19_2_sp" value='&iquest;Trata su ni&ntilde;o (a) de llamar su atenci&oacute;n (de sus padres) a las actividades que estada llevando a cabo?' />
					<input id="Question_19_2"  name="Question_19_2" type="hidden" value="${quest19_2_sp}" />
					<strong>${quest19_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest19_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_19_sp" id="Choice_19_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_19_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_19_sp" id="Choice_19_sp_No" value="failed" data-theme="c" />
							<label for="Choice_19_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest20_2_sp" value='&iquest;Se ha preguntado alguna vez si su ni&ntilde;o (a) es sordo (a)?' />
					<input id="Question_20_2"  name="Question_20_2" type="hidden" value="${quest20_2_sp}" />
					<strong>${quest20_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest20_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_20_sp" id="Choice_20_sp_Yes" value="failed" data-theme="c" />
							<label for="Choice_20_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_20_sp" id="Choice_20_sp_No" value="passed" data-theme="c" />
							<label for="Choice_20_sp_No">No</label>
						</fieldset>
					</div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(3)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Anterior</a>
					<a href="#" onClick="changePage(5)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Proximo</a>
				</div>
			</div>
			
			<div id="question_page_5_sp" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>M-CHAT:</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage5SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
					<a class="vitalsButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.navigateToVitals()" data-transition="pop" data-icon="forward">Personal</a>
				</div>
				
				<div id="content_5_sp" data-role="content">
					<c:set var="quest21_2_sp" value='&iquest;Comprende su ni&ntilde;o (a) lo que otras dicen?' />
					<input id="Question_21_2"  name="Question_21_2" type="hidden" value="${quest21_2_sp}" />
					<strong>${quest21_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest21_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_21_sp" id="Choice_21_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_21_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_21_sp" id="Choice_21_sp_No" value="failed" data-theme="c" />
							<label for="Choice_21_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest22_2_sp" value='&iquest;Ha notado si su ni&ntilde;o (a) se queda con una mirando fija en nada, o si camina algunas veces sin sentido?' />
					<input id="Question_22_2"  name="Question_22_2" type="hidden" value="${quest22_2_sp}" />
					<strong>${quest22_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest22_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_22_sp" id="Choice_22_sp_Yes" value="failed" data-theme="c" />
							<label for="Choice_22_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_22_sp" id="Choice_22_sp_No" value="passed" data-theme="c" />
							<label for="Choice_22_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest23_2_sp" value='&iquest;Su ni&ntilde;o le mira a su cara para chequear su reacci&oacute;n cuando esta en una situaci&oacute;n diferente?' />
					<input id="Question_23_2"  name="Question_23_2" type="hidden" value="${quest23_2_sp}" />
					<strong>${quest23_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest23_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_23_sp" id="Choice_23_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_23_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_23_sp" id="Choice_23_sp_No" value="failed" data-theme="c" />
							<label for="Choice_23_sp_No">No</label>
						</fieldset>
					</div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
					<div id="not_finished_final_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>Not Completado</h1>
						</div>
						<div data-role="content">
							<span>Esta forma no es completa. Est&aacute;s seguro de que quieres continuar?</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" onClick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width:150px;">S&iacute;</a>
								<a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width:150px;">No</a>
							</div>
						</div>
					</div>
					<div id="finish_error_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>Error</h1>
						</div>
						<div data-role="content">
							<span>Hubo un error al enviar el formulario. Por favor, pulse 'OK' para intentarlo de nuevo.</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" onClick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width:150px;">OK</a>
							</div>
						</div>
					</div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(4)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Anterior</a>
					<a href="#" onClick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Acabado</a>
				</div>
			</div>
			
			<input id="formInstances" name="formInstances" type="hidden" value="${formInstances }"/>
			<input id="patientId" name="patientId" type="hidden" value="${patient.patientId}"/>
			<input id="encounterId" name="encounterId" type="hidden" value="${encounterId}"/>
			<input id="formId" name="formId" type="hidden" value="${formId}"/>
			<input id="formInstanceId" name="formInstanceId" type="hidden" value="${formInstanceId}"/>
			<input id="locationId" name="locationId" type="hidden" value="${locationId}"/>
			<input id="locationTagId" name="locationTagId" type="hidden" value="${locationTagId}"/>
			<input id="sessionId" name="sessionId" type="hidden" value="${sessionId}"/>
			<!--<input id="MCHATScore" name="MCHATScore" type="hidden"/>-->
			<input id="MchatTotalItemsFailed" name="MchatTotalItemsFailed" type="hidden"/>
			<input id="MchatCriticalItemsFailed" name="MchatCriticalItemsFailed" type="hidden"/>
			<input id="language" name="language" type="hidden" value="${language}"/>
		</form>		
	</body>
</html>
