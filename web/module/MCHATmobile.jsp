<%@ include file="/WEB-INF/template/include.jsp" %>
<!--<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>-->

<!doctype html>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
		<meta name="viewport" content="width=device-width">
	 	<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.blockUI.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/MCHATMobile.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>

		<!--<link rel="stylesheet" href="resources/jquery.mobile-1.3.2.min.css">
		<link rel="stylesheet" href="resources/chicaMobile.css">
		<script src="resources/jquery-1.9.1.min.js"></script>
		<script src="resources/jquery.mobile-1.3.2.min.js"></script>
		<script src="resources/jquery.blockUI.js"></script>
		<script src="resources/MCHATMobile.js" charset="utf-8"></script>
		<script src="resources/core.js"></script>
		<script src="resources/aes.js"></script>
		<script src="resources/chica.js"></script>-->
		<title>M-CHAT</title>
	</head>
	
	<body style="font-size:20px" onLoad="init('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}', '${formInstance}', '${language}')">
		<form id="MCHATForm" method="post" action="MCHATMobile.form" enctype="multipart/form-data">
			<c:if test="${errorMessage != null}">
				<div id="error_dialog" class="extended-header" data-role="dialog" data-close-btn="none" data-dismissible="false" data-theme="b" data-overlay-theme="c">
					<div data-role="header" data-theme="b">
						<h1>Error</h1>
					</div>
					<div data-role="content">
						<span>${errorMessage></span>
						<div style="margin:0 auto; text-align:center;">
							<a href="#" data-role="button" data-inline="true" data-theme="b" 
								onClick="submitEmptyForm()" style="width:150px;">OK</a>
						</div>
					</div>
				</div>
			</c:if> <!--End Error Message-->
			
			<div data-role="page" id="instruction_page" data-theme="b">
				<div data-role="header">
					<h1>M-CHAT</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
						onClick="setLanguage('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a id="vitalsButton" data-role="button" onClick="parent.navigateToVitals()" data-theme="b" 
						class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>					
				</div>
				<div data-role="content">
					<strong><span id="additionalQuestions">Please fill out the following about how your child usually is. Please try to answer every question. If the behavior is rare (e.g., you've seen it once or twice), please answer as if the child does not do it.</span></strong>
					<div><br /></div>
				</div>
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a id="startButton" href="#" onclick="changePage(1)" data-role="button" data-theme="b" style="width:150px;">Start</a>					
				</div>
			</div> <!--End Instructions-->

			<c:set var="copyright" value='&copy; 1999 Diana Robins, Deborah Fein, & Marianne Barton'/> Copyright			
			<!--<c:set var="copyright" value='&copy; 2009 Diana Robins, Deborah Fein, & Marianne Barton'/>--> <!--Copyright Revised--> 
			
			<div id="question_page_1" data-role="page" data-theme="b" style="question_page">
				<div data-role="header">
					<h1>M-CHAT</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
						onClick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a id="vitalsButton" data-role="button" onClick="parent.navigateToVitals()" data-theme="b" 
						class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>					
				</div>
				<div id="content_1" data-role="content">
					<c:set var="quest1" value='Does your child enjoy being swung, bounced on your knee, etc.?)' />
					<!--<c:set var="quest1" value='If you point at something across the room, does your child look at it? (FOR EXAMPLE, if you point at a toy or an animal, does your child look at the toy or animal?)' />--><!--MCHAT-R-->
					<input id="Question_1" name="Question_1" type="hidden" value="${quest1}" />
					<strong>${quest1}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest1}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_1" id="QuestionEntry_1_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_1_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_1" id="QuestionEntry_1_No" value="0" data-theme="c" />
							<label for="QuestionEntry_1_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest2" value='Does your child take an interest in other children?' />
					<!--<c:set var="quest2" value='Have you ever wondered if your child might be deaf?' />--><!--MCHAT-R-->
					<input id="Question_2" name="Question_2" type="hidden" value="${quest2}" />
					<strong>${quest2}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest2}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_2" id="QuestionEntry_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_2_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_2" id="QuestionEntry_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest3" value='Does your child like climbing on things, such as up stairs?' />
					<!--<c:set var="quest3" value='Does your child play pretend or make-believe? (FOR EXAMPLE, pretend to drink from an empty cup, pretend to talk on a phone, or pretend to feed a doll or stuffed animal?)' />--><!--MCHAT-R-->
					<input id="Question_3" name="Question_3" type="hidden" value="${quest3}" />
					<strong>${quest3}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest3}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_3" id="QuestionEntry_3_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_3_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_3" id="QuestionEntry_3_No" value="0" data-theme="c" />
							<label for="QuestionEntry_3_No">No</label>
						</fieldset>
					</div> 
					<br /> 
					<c:set var="quest4" value='Does your child enjoy playing peek-a-boo/hide-and-seek?' />
					<!--<c:set var="quest4" value='Does your child like climbing on things? (FOR EXAMPLE, furniture, playground equipment, or stairs)' />--><!--MCHAT-R-->
					<input id="Question_4" name="Question_4" type="hidden" value="${quest4}" />
					<strong>${quest4}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest4}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_4" id="QuestionEntry_4_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_4_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_4" id="QuestionEntry_4_No" value="0" data-theme="c" />
							<label for="QuestionEntry_4_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest5" value='Does your child ever pretend, for example, to talk on the phone or take care of a doll or pretend other things?' />
					<!--<c:set var="quest5" value='Does your child make unusual finger movements near his or her eyes? (FOR EXAMPLE, does your child wiggle his or her fingers close to his or her eyes?)' />--><!--MCHAT-R-->
					<input id="Question_5" name="Question_5" type="hidden" value="${quest5}" />
					<strong>${quest5}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest5}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_5" id="QuestionEntry_5_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_5_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_5" id="QuestionEntry_5_No" value="0" data-theme="c" />
							<label for="QuestionEntry_5_No">No</label>
						</fieldset>
					</div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(2)" style="width:150px;">Next</a>
				</div>
			</div> <!--End Questions 1, 2, 3, 4, 5-->
			
			<div id="question_page_2" data-role="page" data-theme="b" style="question_page">
				<div data-role="header">
					<h1>M-CHAT</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage2Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
						onClick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a id="vitalsButton" data-role="button" onClick="parent.navigateToVitals()" data-theme="b" 
						class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>					
				</div>
				<div id="content_2" data-role="content">
					<c:set var="quest6" value='Does your child ever user his/her index finger to point, to ask for something?' />
					<!--<c:set var="quest6" value='Does your child point with one finger to ask for something or to get help? (FOR EXAMPLE, pointing to a snack or toy that is out of reach)' />--><!--MCHAT-R-->
					<input id="Question_6" name="Question_6" type="hidden" value="${quest6}" />
					<strong>${quest6}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest6}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_6" id="QuestionEntry_6_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_6_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_6" id="QuestionEntry_6_No" value="0" data-theme="c" />
							<label for="QuestionEntry_6_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest7" value='Does your child ever user his/her index finger to point, to indicate interest in something?' />
					<!--<c:set var="quest7" value='Does your child point with one finger to show you something interesting? (FOR EXAMPLE, pointing to an airplane in the sky or a big truck in the road)' />--><!--MCHAT-R-->
					<input id="Question_7" name="Question_7" type="hidden" value="${quest7}" />
					<strong>${quest7}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest7}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_7" id="QuestionEntry_7_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_7_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_7" id="QuestionEntry_7_No" value="0" data-theme="c" />
							<label for="QuestionEntry_7_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest8" value='Can your child play properly with small toys (e.g. cars or blocks) without just mouthing, fiddling, or dropping them?' />
					<!--<c:set var="quest8" value='Is your child interested in other children? (FOR EXAMPLE, does your child watch other children, smile at them, or go to them?)' />--><!--MCHAT-R-->
					<input id="Question_8" name="Question_8" type="hidden" value="${quest8}" />
					<strong>${quest8}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest8}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_8" id="QuestionEntry_8_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_8_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_8" id="QuestionEntry_8_No" value="0" data-theme="c" />
							<label for="QuestionEntry_8_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest9" value='Does your child ever bring objects over to you (parent) to show you something?' />
					<!--<c:set var="quest9" value='Does your child show you things by bringing them to you or holding them up for you to see – not to get help, but just to share? (FOR EXAMPLE, showing you a flower, a stuffed animal, or a toy truck)' />--><!--MCHAT-R-->
					<input id="Question_9" name="Question_9" type="hidden" value="${quest9}" />
					<strong>${quest9}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest9}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_9" id="QuestionEntry_9_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_9_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_9" id="QuestionEntry_9_No" value="0" data-theme="c" />
							<label for="QuestionEntry_9_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest10" value='Does your child ever look you in the eye for more than a second or two?' />
					<!--<c:set var="quest10" value='Does your child respond when you call his or her name? (FOR EXAMPLE, does he or she look up, talk or babble, or stop what he or she is doing when you call his or her name?)' />--><!--MCHAT-R-->
					<input id="Question_10" name="Question_10" type="hidden" value="${quest10}" />
					<strong>${quest10}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest10}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_10" id="QuestionEntry_10_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_10_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_10" id="QuestionEntry_10_No" value="0" data-theme="c" />
							<label for="QuestionEntry_10_No">No</label>
						</fieldset>
					</div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(1)" style="width:150px;">Previous</a>
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(3)" style="width:150px;">Next</a>
				</div>
			</div> <!--End Questions 6, 7, 8, 9, 10-->
			
			<div id="question_page_3" data-role="page" data-theme="b" style="question_page">
				<div data-role="header">
					<h1>M-CHAT</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage3Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
						onClick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a id="vitalsButton" data-role="button" onClick="parent.navigateToVitals()" data-theme="b" 
						class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>					
				</div>
				<div id="content_3" data-role="content">
					<c:set var="quest11" value='Does your child ever seem oversensitive to noise? (e.g. plugging ears)' />
					<!--<c:set var="quest11" value='When you smile at your child, does he or she smile back at you?' />--><!--MCHAT-R-->
					<input id="Question_11" name="Question_11" type="hidden" value="${quest11}" />
					<strong>${quest11}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest11}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_11" id="QuestionEntry_11_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_11_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_11" id="QuestionEntry_11_No" value="0" data-theme="c" />
							<label for="QuestionEntry_11_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest12" value='Does your child smile in response to your face or your smile?' />
					<!--<c:set var="quest12" value='Does your child get upset by everyday noises? (FOR EXAMPLE, does your child scream or cry to noise such as a vacuum cleaner or loud music?)' />--><!--MCHAT-R-->
					<input id="Question_12" name="Question_12" type="hidden" value="${quest12}" />
					<strong>${quest12}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest12}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_12" id="QuestionEntry_12_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_12_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_12" id="QuestionEntry_12_No" value="0" data-theme="c" />
							<label for="QuestionEntry_12_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest13" value='Does your child imitate you? (e.g. you make a face-will your chlid imitate it?)' />
					<!--<c:set var="quest13" value='Does your child walk?' />--><!--MCHAT-R-->
					<input id="Question_13" name="Question_13" type="hidden" value="${quest13}" />
					<strong>${quest13}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest13}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_13" id="QuestionEntry_13_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_13_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_13" id="QuestionEntry_13_No" value="0" data-theme="c" />
							<label for="QuestionEntry_13_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest14" value='Does your child respond to his/her name when you call?' />
					<!--<c:set var="quest14" value='Does your child look you in the eye when you are talking to him or her, playing with him or her, or dressing him or her?' />--><!--MCHAT-R-->
					<input id="Question_14" name="Question_14" type="hidden" value="${quest14}" />
					<strong>${quest14}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest14}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_14" id="QuestionEntry_14_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_14_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_14" id="QuestionEntry_14_No" value="0" data-theme="c" />
							<label for="QuestionEntry_14_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest15" value='If you point at a toy across the room, does your child look at it?' />
					<!--<c:set var="quest15" value='Does your child try to copy what you do? (FOR EXAMPLE, wave bye-bye, clap, or make a funny noise when you do)' />--><!--MCHAT-R-->
					<input id="Question_15" name="Question_15" type="hidden" value="${quest15}" />
					<strong>${quest15}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest15}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_15" id="QuestionEntry_15_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_15_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_15" id="QuestionEntry_15_No" value="0" data-theme="c" />
							<label for="QuestionEntry_15_No">No</label>
						</fieldset>
					</div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
				<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(2)" style="width:150px;">Previous</a>
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(4)" style="width:150px;">Next</a>
				</div>
			</div> <!--End Questions 11, 12, 13, 14, 15-->
			
			<div id="question_page_4" data-role="page" data-theme="b" style="question_page">
				<div data-role="header">
					<h1>M-CHAT</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage4Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
						onClick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a id="vitalsButton" data-role="button" onClick="parent.navigateToVitals()" data-theme="b" 
						class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>					
				</div>
				<div id="content_4" data-role="content">
					<c:set var="quest16" value='Does your child walk?' />
					<!--<c:set var="quest16" value='If you turn your head to look at something, does your child look around to see what you are looking at?' />--><!--MCHAT-R-->
					<input id="Question_16" name="Question_16" type="hidden" value="${quest16}" />
					<strong>${quest16}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest16}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_16" id="QuestionEntry_16_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_16_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_16" id="QuestionEntry_16_No" value="0" data-theme="c" />
							<label for="QuestionEntry_16_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest17" value='Does your child look at things you are looking at?' />
					<!--<c:set var="quest17" value='Does your child try to get you to watch him or her? (FOR EXAMPLE, does your child look at you for praise, or say “look” or “watch me”?)' />--><!--MCHAT-R-->
					<input id="Question_17" name="Question_17" type="hidden" value="${quest17}" />
					<strong>${quest17}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest17}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_17" id="QuestionEntry_17_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_17_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_17" id="QuestionEntry_17_No" value="0" data-theme="c" />
							<label for="QuestionEntry_17_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest18" value='Does your child make unusual finger movements near his/her face?' />
					<!--<c:set var="quest18" value='Does your child understand when you tell him or her to do something? (FOR EXAMPLE, if you don’t point, can your child understand “put the book on the chair” or “bring me the blanket”?)' />--><!--MCHAT-R-->
					<input id="Question_18" name="Question_18" type="hidden" value="${quest18}" />
					<strong>${quest18}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest18}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_18" id="QuestionEntry_18_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_18_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_18" id="QuestionEntry_18_No" value="0" data-theme="c" />
							<label for="QuestionEntry_18_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest19" value='Does your child try to attract your attention to his/her own activity?' />
					<!--<c:set var="quest19" value='If something new happens, does your child look at your face to see how you feel about it? (FOR EXAMPLE, if he or she hears a strange or funny noise, or sees a new toy, will he or she look at your face?)' />--><!--MCHAT-R-->
					<input id="Question_19" name="Question_19" type="hidden" value="${quest19}" />
					<strong>${quest19}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest19}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_19" id="QuestionEntry_19_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_19_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_19" id="QuestionEntry_19_No" value="0" data-theme="c" />
							<label for="QuestionEntry_19_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest20" value='Have you ever wondered if your child is deaf?' />
					<!--<c:set var="quest20" value='Does your child like movement activities? (FOR EXAMPLE, being swung or bounced on your knee)' />--><!--MCHAT-R-->
					<input id="Question_20" name="Question_20" type="hidden" value="${quest20}" />
					<strong>${quest20}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest20}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_20" id="QuestionEntry_20_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_20_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_20" id="QuestionEntry_20_No" value="0" data-theme="c" />
							<label for="QuestionEntry_20_No">No</label>
						</fieldset>
					</div> <!--Questions 16, 17, 18, 19, 20-->
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
					
					<!--<div id="not_finished_dialog" class="extended-header" data-role="popup" data-dismissible="false" 
						data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>Not Completed</h1>
						</div>
						<div data-role="content">
							<span>This form is not complete. Please complete before continuing.</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width:150px;">Close</a>							
							</div>
						</div>
					</div>
					<div id="not_finished_final_dialog" class="extended-header" data-role="popup" data-dismissible="false" 
						data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>Not Completed</h1>
						</div>
						<div data-role="content">
							<span>This form is still not complete. Are you sure you want to continue?</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" onClick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width:150px;">Yes</a>
								<a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width:150px;">No</a>							
							</div>
						</div>
					</div>
					<div id="finish_error_dialog" class="extended-header" data-role="popup" data-dismissible="false" 
						data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>Error</h1>
						</div>
						<div data-role="content">
							<span>There was an error submitting the form. Please press 'OK' to try again.</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" onClick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width:150px;">OK</a>
							</div>
						</div>
					</div>--><!--End Incomplete and Error Messages--><!--Uncomment for M-MCHAT-R-->
				</div> 
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(3)" style="width:150px;">Previous</a>
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(5)" style="width:150px;">Next</a>
					<!--<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="attemptFinishForm()" style="width:150px;">Finish</a>--><!--M-CHAT-R-->
				</div>
			</div> <!--End English Questions for M-CHAT-R-->
			
			<!--Begin MCHAT 21-23-->
			<div id="question_page_5" data-role="page" data-theme="b" style="question_page">
				<div data-role="header">
					<h1>M-CHAT</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage5Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
						onClick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a id="vitalsButton" data-role="button" onClick="parent.navigateToVitals()" data-theme="b" 
						class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>					
				</div>
				<div id="content_5" data-role="content">
					<c:set var="quest21" value='Does your child understand what people say?' />
					<input id="Question_21" name="Question_21" type="hidden" value="${quest21}" />
					<strong>${quest21}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest21}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_21" id="QuestionEntry_21_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_21_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_21" id="QuestionEntry_21_No" value="0" data-theme="c" />
							<label for="QuestionEntry_21_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest22" value='Does your child sometimes stare at nothing or wander with no purpose?' />
					<input id="Question_22" name="Question_22" type="hidden" value="${quest22}" />
					<strong>${quest22}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest22}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_22" id="QuestionEntry_22_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_22_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_22" id="QuestionEntry_22_No" value="0" data-theme="c" />
							<label for="QuestionEntry_22_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest23" value='Does your child look at your face to check your reaction when faced with something unfamiliar?' />
					<input id="Question_23" name="Question_23" type="hidden" value="${quest23}" />
					<strong>${quest23}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest23}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_23" id="QuestionEntry_23_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_23_Yes">Yes</label>
							<input type="radio" name="QuestionEntry_23" id="QuestionEntry_23_No" value="0" data-theme="c" />
							<label for="QuestionEntry_23_No">No</label>
						</fieldset>
					</div> <!--Questions 21, 22, 23-->
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
					
					<div id="not_finished_dialog" class="extended-header" data-role="popup" data-dismissible="false" 
						data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>Not Completed</h1>
						</div>
						<div data-role="content">
							<span>This form is not complete. Please complete before continuing.</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width:150px;">Close</a>							
							</div>
						</div>
					</div>
					<div id="not_finished_final_dialog" class="extended-header" data-role="popup" data-dismissible="false" 
						data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>Not Completed</h1>
						</div>
						<div data-role="content">
							<span>This form is still not complete. Are you sure you want to continue?</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" onClick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width:150px;">Yes</a>
								<a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width:150px;">No</a>							
							</div>
						</div>
					</div>
					<div id="finish_error_dialog" class="extended-header" data-role="popup" data-dismissible="false" 
						data-theme="b" data-overlay-theme="a">
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
				</div> <!--End Incomplete and Error Messages-->
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(4)" style="width:150px;">Previous</a>
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="attemptFinishForm()" style="width:150px;">Finish</a>
				</div>
			</div> <!--End English Questions--><!--End MCHAT 21-23-->
			
			<!--<c:set var="copyright_sp" value='&copy; 2009 Diana Robins, Deborah Fein, & Marianne Barton. Traducci&oacute;n y adaptaci&oacute;n en Espa&ntilde;a: Grupo Estudio MCHAT Espa&ntilde;a'/>--><!--Copyright Revised--> 
			
			<div id="question_page_1_sp" data-role="page" data-theme="b" style="question_page">
				<div data-role="header">
					<h1>M-CHAT</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
						onClick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
					<a id="vitalsButton" data-role="button" onClick="parent.navigateToVitals()" data-theme="b" 
						class="ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>					
				</div>
				<div id="content_1_sp" data-role="content">
					<c:set var="quest1_2_sp" value='&iquest;Disfruta su ni&ntilde;o (a) cuando lo balancean o hacen saltar sobre su rodilla?' />
					<!--<c:set var="quest1_2_sp" value='&iquest;Si usted se&ntilde;ala un objeto del otro lado del cuarto, su hijo/a lo mira? (POR EJEMPLO &iquest;Si usted se&ntilde;ala un juguete o un animal, su hijo/a mira al juguete o al animal?)' />--><!--MCHAT-R-->
					<input id="Question_1_2" name="Question_1_2" type="hidden" value="${quest1_2_sp}" />
					<strong>${quest1_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest1_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_1_2" id="QuestionEntry_1_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_1_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_1_2" id="QuestionEntry_1_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_1_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest2_2_sp" value='&iquest;Se interesa su ni&ntilde;o (a) en otros ni&ntilde;os?' />
					<!--<c:set var="quest2_2_sp" value='&iquest;Alguna vez se ha preguntado si su hijo/a es sordo/a?' />--><!--MCHAT-R-->
					<input id="Question_2_2" name="Question_2_2" type="hidden" value="${quest2_2_sp}" />
					<strong>${quest2_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest2_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_2_2" id="QuestionEntry_2_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_2_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_2_2" id="QuestionEntry_2_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_2_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest3_2_sp" value='&iquest;Le gusta a su ni&ntilde;o (a) subirse a las cosas, por ejemplo subir las escaleras?' />
					<!--<c:set var="quest3_2_sp" value='&iquest;Su hijo/a juega juegos de fantas&iacute;a o imaginaci&oacute;n? (POR EJEMPLO finge beber de una taza vac&iacute;a, finge hablar por tel&eacute;fono o finge darle de comer a una mu&ntilde;eca o un peluche)' />--><!--MCHAT-R-->
					<input id="Question_3_2" name="Question_3_2" type="hidden" value="${quest3_2_sp}" />
					<strong>${quest3_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest3_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_3_2" id="QuestionEntry_3_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_3_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_3_2" id="QuestionEntry_3_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_3_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest4_2_sp" value='&iquest;Disfruta su ni&ntilde;o (a) jugando "peek-a-boo" o "hide and seek" (a las escondidas)?' />
					<!--<c:set var="quest4_2_sp" value='&iquest;A su hijo/a le gusta treparse a las cosas? (POR EJEMPLO muebles, escaleras o juegos infantiles)' />--><!--MCHAT-R-->
					<input id="Question_4_2" name="Question_4_2" type="hidden" value="${quest4_2_sp}" />
					<strong>${quest4_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest4_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_4_2" id="QuestionEntry_4_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_4_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_4_2" id="QuestionEntry_4_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_4_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest5_2_sp" value='&iquest;Le gusta a su ni&ntilde;o (a) jugar a pretendar, como por ejemplo, pretende que habla por tel&eacute;fono, que cuida sus mu&ntilde;ecas, o pretende otras cosas?' />
					<!--<c:set var="quest5_2_sp" value='&iquest;Su hijo/a hace movimientos inusuales con los dedos cerca de sus ojos? (POR EJEMPLO &iquest;Mueve sus dedos cerca de sus ojos de manera inusual?)' />--><!--MCHAT-R-->
					<input id="Question_5_2" name="Question_5_2" type="hidden" value="${quest5_2_sp}" />
					<strong>${quest5_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest5_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_5_2" id="QuestionEntry_5_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_5_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_5_2" id="QuestionEntry_5_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_5_2_No">No</label>
						</fieldset>
					</div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright_sp}</span></div>
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(2)" style="width:150px;">Proximo</a>
				</div>
			</div> <!--End Spanish Questions 1, 2, 3, 4, 5-->
			
			<div id="question_page_2_sp" data-role="page" data-theme="b" style="question_page">
				<div data-role="header">
					<h1>M-CHAT</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage2SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
						onClick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
					<a id="vitalsButton" data-role="button" onClick="parent.navigateToVitals()" data-theme="b" 
						class="ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>					
				</div>
				<div id="content_2_sp" data-role="content">
					<c:set var="quest6_2_sp" value='&iquest;Utiliza su ni&ntilde;o (a) su dedo &iacute;ndice para se&ntilde;alar algo, o para preguntar alguna cosa?' />
					<!--<c:set var="quest6_2_sp" value='&iquest;Su hijo/a apunta o se&ntilde;ala con un dedo cuando quiere pedir algo o pedir ayuda? (POR EJEMPLO se&ntilde;ala un juguete o algo para comer que est&aacute; fuera de su alcance)' />--><!--MCHAT-R-->
					<input id="Question_6_2" name="Question_6_2" type="hidden" value="${quest6}" />
					<strong>${quest6_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest6_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_6_2" id="QuestionEntry_6_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_6_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_6" id="QuestionEntry_6_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_6_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest7_2_sp" value='&iquest;Usa su ni&ntilde;o (a) su dedo &iacute;ndice para se&ntilde;alar o indicar inter&eacute;s en algo?' />
					<!--<c:set var="quest7_2_sp" value='&iquest;Su hijo/a apunta o se&ntilde;ala con un dedo cuando quiere mostrarle algo interesante? (POR EJEMPLO se&ntilde;ala un avi&oacute;n en el cielo o un cami&oacute;n grande en el camino)' />--><!--MCHAT-R-->
					<input id="Question_7_2" name="Question_7_2" type="hidden" value="${quest7_2_sp}" />
					<strong>${quest7_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest7_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_7_2" id="QuestionEntry_7_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_7_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_7_2" id="QuestionEntry_7_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_7_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest8_2_sp" value='&iquest;Puede su ni&ntilde;o (a) jugar bien con jugetes peque&ntilde;os (como carros o cubos) sin llev&aacute;rselos a la boca, manipularlos o dejarlos caer)?' />
					<!--<c:set var="quest8_2_sp" value='&iquest;Su hijo/a muestra inter&eacute;s en otros ni&ntilde;os? (POR EJEMPLO &iquest;mira con atenci&oacute;n a otros ni&ntilde;os, les sonr&iacute;e o se les acerca?)' />--><!--MCHAT-R-->
					<input id="Question_8_2" name="Question_8_2" type="hidden" value="${quest8_2_sp}" />
					<strong>${quest8_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest8_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_8_2" id="QuestionEntry_8_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_8_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_8_2" id="QuestionEntry_8_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_8_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest9_2_sp" value='&iquest;Le trae su ni&ntilde;o (a) a usted (padre o madre) objetos o cosas, con el prop&oacute;sito de mostrarle algo alguna vez?' />
					<!--<c:set var="quest9_2_sp" value='&iquest;Su hijo/a le muestra cosas acercándoselas a usted o levantándolas para que usted las vea, no para pedir ayuda sino para compartirlas con usted? (POR EJEMPLO le muestra una flor, un peluche o un cami&oacute;n/carro de juguete)' />--><!--MCHAT-R-->
					<input id="Question_9_2" name="Question_9_2" type="hidden" value="${quest9_2_sp}" />
					<strong>${quest9_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest9_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_9_2" id="QuestionEntry_9_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_9_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_9_2" id="QuestionEntry_9_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_9_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest10_2_sp" value='&iquest;Lo mira su ni&ntilde;o (a) directamente a los ojos por mas de uno o dos segundos?' />
					<!--<c:set var="quest10_2_sp" value='&iquest;Su hijo/a responde cuando usted le llama por su nombre? (POR EJEMPLO &iquest;Cuando usted lo llama por su nombre: lo mira a usted, habla, balbucea, o deja de hacer lo que estaba haciendo?)' />--><!--MCHAT-R-->
					<input id="Question_10_2" name="Question_10_2" type="hidden" value="${quest10_2_sp}" />
					<strong>${quest10_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest10_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_10_2" id="QuestionEntry_10_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_10_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_10_2" id="QuestionEntry_10_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_10_2_No">No</label>
						</fieldset>
					</div>
					<!--<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright_sp}</span></div>-->
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(1)" style="width:150px;">Anterior</a>
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(3)" style="width:150px;">Proximo</a>
				</div>
			</div> <!--End Spanish Questions 6, 7, 8, 9, 10-->
			
			<div id="question_page_3_sp" data-role="page" data-theme="b" style="question_page">
				<div data-role="header">
					<h1>M-CHAT</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage3SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
						onClick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
					<a id="vitalsButton" data-role="button" onClick="parent.navigateToVitals()" data-theme="b" 
						class="ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>					
				</div>
				<div id="content_3_sp" data-role="content">
					<c:set var="quest11_2_sp" value='&iquest;Parece su ni&ntilde;o (a) ser demasiado sensitivo al ruido? (por ejemplo, se tapa los oidos)?' />
					<!--<c:set var="quest11_2_sp" value='&iquest;Cuándo usted le sonr&iacute;e a su hijo/a, &eacute;l o ella le devuelve la sonrisa?' />--><!--MCHAT-R-->
					<input id="Question_11_2" name="Question_11_2" type="hidden" value="${quest11_2_sp}" />
					<strong>${quest11_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest11_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_11_2" id="QuestionEntry_11_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_11_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_11_2" id="QuestionEntry_11_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_11_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest12_2_sp" value='&iquest;Sonrie su ni&ntilde;o (a) en respuesta a su cara o a su sonrisa?' />
					<!--<c:set var="quest12_2_sp" value='&iquest;A su hijo/a le molestan los ruidos cotidianos? (POR EJEMPLO &iquest;Llora o grita cuando escucha la aspiradora o m&uacute;sica muy fuerte?)' />--><!--MCHAT-R-->
					<input id="Question_12_2" name="Question_12_2" type="hidden" value="${quest12_2_sp}" />
					<strong>${quest12_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest12_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_12_2" id="QuestionEntry_12_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_12_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_12_2" id="QuestionEntry_12_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_12_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest13_2_sp" value='&iquest;Lo imita su ni&ntilde;o (a)? Por ejemplo, si usted le hace una mueca, su ni&ntilde;o (a) trata de imitarlo?' />
					<!--<c:set var="quest13_2_sp" value='&iquest;Su hijo/a camina?' />--><!--MCHAT-R-->
					<input id="Question_13_2" name="Question_13_2" type="hidden" value="${quest13_2_sp}" />
					<strong>${quest13_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest13_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_13_2" id="QuestionEntry_13_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_13_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_13_2" id="QuestionEntry_13_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_13_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest14_2_sp" value='&iquest;Responde su ni&ntilde;o (a) a su nombre cuando lo(a) llaman?' />
					<!--<c:set var="quest14_2_sp" value='&iquest;Su hijo/a le mira a los ojos cuando usted le habla, juega con &eacute;l/ella o lo/la viste?' />--><!--MCHAT-R-->
					<input id="Question_14_2" name="Question_14_2" type="hidden" value="${quest14_2_sp}" />
					<strong>${quest14_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest14_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_14_2" id="QuestionEntry_14_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_14_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_14_2" id="QuestionEntry_14_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_14_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest15_2_sp" value='&iquest;Si usted se&ntilde;ala a un juguete que est&aacute; al otro lado de la habitaci&oacute;n a su ni&ntilde;o (a), lo mira?' />
					<!--<c:set var="quest15_2_sp" value='&iquest;Su hijo/a trata de imitar sus movimientos? (POR EJEMPLO decir adi&oacute;s con la mano, aplaudir o alg&uacute;n ruido chistoso que usted haga)' />--><!--MCHAT-R-->
					<input id="Question_15_2" name="Question_15_2" type="hidden" value="${quest15_2_sp}" />
					<strong>${quest15_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest15_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_15_2" id="QuestionEntry_15_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_15_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_15_2" id="QuestionEntry_15_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_15_2_No">No</label>
						</fieldset>
					</div>
					<!--<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright_sp}</span></div>-->
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(2)" style="width:150px;">Anterior</a>
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(4)" style="width:150px;">Proximo</a>
				</div>
			</div> <!--End Spanish Questions 11, 12, 13, 14, 15-->
			
			<div id="question_page_4_sp" data-role="page" data-theme="b" style="question_page">
				<div data-role="header">
					<h1>M-CHAT</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage4SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
						onClick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
					<a id="vitalsButton" data-role="button" onClick="parent.navigateToVitals()" data-theme="b" 
						class="ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>					
				</div>
				<div id="content_4_sp" data-role="content">
					<c:set var="quest16_2_sp" value='&iquest;Camina su ni&ntilde;o (a)?' />
					<!--<c:set var="quest16_2_sp" value='&iquest;Si usted se voltea a ver algo, su hijo/a trata de ver que es lo que usted est&aacute; mirando? ' />--><!--MCHAT-R-->
					<input id="Question_16_2" name="Question_16_2" type="hidden" value="${quest16_2_sp}" />
					<strong>${quest16_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest16_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_16_2" id="QuestionEntry_16_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_16_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_16_2" id="QuestionEntry_16_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_16_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest17_2_sp" value='&iquest;Presta su ni&ntilde;o (a) atenci&oacute;n a las cosas que usted est&aacute; mirando?' />
					<!--<c:set var="quest17_2_sp" value='&iquest;Su hijo/a trata que usted lo mire? (POR EJEMPLO &iquest;Busca que usted lo/la halague, o dice “mirame”?)' />--><!--MCHAT-R-->
					<input id="Question_17_2" name="Question_17_2" type="hidden" value="${quest17_2_sp}" />
					<strong>${quest17_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest17_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_17_2" id="QuestionEntry_17_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_17_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_17_2" id="QuestionEntry_17_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_17_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest18_2_sp" value='&iquest;Hace su ni&ntilde;o (a) movimientos raros con los dedos cerca de su cara?' />
					<!--<c:set var="quest18_2_sp" value='&iquest;Su hijo/a le entiende cuando usted le dice que haga algo? (POR EJEMPLO &iquest;Su hijo/a entiende “pon el libro en la silla” o “tráeme la cobija” sin que usted haga se&ntilde;as?)' />--><!--MCHAT-R-->
					<input id="Question_18_2" name="Question_18_2" type="hidden" value="${quest18_2_sp}" />
					<strong>${quest18_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest18_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_18_2" id="QuestionEntry_18_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_18_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_18_2" id="QuestionEntry_18_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_18_2_No">No</label>
						</fieldset>
					</div>
					<br />					
					<c:set var="quest19_2_sp" value='&iquest;Trata su ni&ntilde;o (a) de llamar su atenci&oacute;n (de sus padres) a las actividades que estada llevando a cabo?' />
					<!--<c:set var="quest19_2_sp" value='&iquest;Si algo nuevo ocurre, su hijo/a lo mira a la cara para ver c&oacute;mo se siente usted al respecto? (POR EJEMPLO &iquest;Si oye un ruido extra&ntilde;o o ve un juguete nuevo, se voltear&iacute;a a ver su cara?)' />--><!--MCHAT-R-->
					<input id="Question_19_2" name="Question_19_2" type="hidden" value="${quest19_2_sp}" />
					<strong>${quest19_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest19_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_19_2" id="QuestionEntry_19_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_19_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_19_2" id="QuestionEntry_19_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_19_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest20_2_sp" value='&iquest;Se ha preguntado alguna vez si su ni&ntilde;o (a) es sordo (a)?' />
					<!--<c:set var="quest20_2_sp" value='&iquest;A su hijo/a le gustan las actividades con movimiento? (POR EJEMPLO Le gusta que lo mezan/columpien, o que lo haga saltar en sus rodillas)' />--><!--MCHAT-R-->
					<input id="Question_20_2" name="Question_20_2" type="hidden" value="${quest20_2_sp}" />
					<strong>${quest20_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest20_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_20_2" id="QuestionEntry_20_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_20_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_20_2" id="QuestionEntry_20_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_20_2_No">No</label>
						</fieldset>
					</div> <!--End Spanish Questions 16, 17, 18, 19, 20-->
					<!--<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright_sp}</span></div>-->
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
					
					<!--<div id="not_finished_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" 
						data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>No Completado</h1>
						</div>
						<div data-role="content">
							<span>Esta forma no es completa. Por favor complete antes de continuar.</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width:150px;">Cerca</a>							
							</div>
						</div>
					</div>
					<div id="not_finished_final_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" 
						data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>No Completado</h1>
						</div>
						<div data-role="content">
							<span>Esta forma a&#250;n no est&#225; completa. &#191;Est&#225; seguro que desea continuar?</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" onClick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width:150px;">S&iacute;</a>
								<a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width:150px;">No</a>							
							</div>
						</div>
					</div>
					<div id="finish_error_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" 
						data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>Error</h1>
						</div>
						<div data-role="content">
							<span>Hubo un error al enviar el formulario. Por favor, pulse 'OK' para intentarlo de nuevo.</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" onClick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width:150px;">OK</a>
							</div>
						</div>
					</div>--><!--End Incomplete and Error Messages--><!--Uncomment for M-MCHAT-R-->
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(3)" style="width:150px;">Anterior</a>
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(5)" style="width:150px;">Proximo</a>
					<!--<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="attemptFinishForm()" style="width:150px;">Acabado</a>--><!--M-CHAT-R-->
				</div>
			</div> <!--End Spanish Questions for M-CHAT-R--> 
			
			<!--Begin MCHAT 21-23-->
			<div id="question_page_5_sp" data-role="page" data-theme="b" style="question_page">
				<div data-role="header">
					<h1>M-CHAT</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage5SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
						onClick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
					<a id="vitalsButton" data-role="button" onClick="parent.navigateToVitals()" data-theme="b" 
						class="ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>					
				</div>
				<div id="content_5_sp" data-role="content">
					<c:set var="quest21_2_sp" value='&iquest;Comprende su ni&ntilde;o (a) lo que otras dicen?' />
					<input id="Question_21_2" name="Question_21_2" type="hidden" value="${quest21_2_sp}" />
					<strong>${quest21_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest21_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_21_2" id="QuestionEntry_21_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_21_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_21_2" id="QuestionEntry_21_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_21_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest22_2_sp" value='&iquest;Ha notado si su ni&ntilde;o (a) se queda con una Mirada fija en nada, o si camina algunas veces sin sentido?' />
					<input id="Question_22_2" name="Question_22_2" type="hidden" value="${quest22_2_sp}" />
					<strong>${quest22_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest22_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_22_2" id="QuestionEntry_22_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_22_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_22_2" id="QuestionEntry_22_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_22_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest23_2_sp" value='&iquest;Su ni&ntilde;o le mira a su cara para chequear su reacci&oacute;n cuando esta en una situaci&oacute;n diferente?' />
					<input id="Question_23_2" name="Question_23_2" type="hidden" value="${quest23_2_sp}" />
					<strong>${quest23_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readTextSpanish("${quest23_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="QuestionEntry_23_2" id="QuestionEntry_23_2_Yes" value="1" data-theme="c" />
							<label for="QuestionEntry_23_2_Yes">S&iacute;</label>
							<input type="radio" name="QuestionEntry_23_2" id="QuestionEntry_23_2_No" value="0" data-theme="c" />
							<label for="QuestionEntry_23_2_No">No</label>
						</fieldset>
					</div><!--End Spanish Questions 21, 22, 23-->
					<!--<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright_sp}</span></div>-->
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
					
					<div id="not_finished_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" 
						data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>No Completado</h1>
						</div>
						<div data-role="content">
							<span>Esta forma no es completa. Por favor complete antes de continuar.</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width:150px;">Cerca</a>							
							</div>
						</div>
					</div>
					<div id="not_finished_final_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" 
						data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>No Completado</h1>
						</div>
						<div data-role="content">
							<span>Esta forma a&#250;n no est&#225; completa. &#191;Est&#225; seguro que desea continuar?</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" onClick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width:150px;">S&iacute;</a>
								<a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width:150px;">No</a>							
							</div>
						</div>
					</div>
					<div id="finish_error_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" 
						data-theme="b" data-overlay-theme="a">
						<div data-role="header" data-theme="b">
							<h1>Error</h1>
						</div>
						<div data-role="content">
							<span>Hubo un error al enviar el formulario. Por favor, pulse 'OK' para intentarlo de nuevo.</span>
							<div style="margin:0 auto; text-align:center;">
								<a href="" onClick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width:150px;">OK</a>
							</div>
						</div>
					</div><!--End Incomplete and Error Messages-->
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="changePage(4)" style="width:150px;">Anterior</a>
					<a href="#" data-role="button" data-inline="true" data-theme="b" onClick="attemptFinishForm()" style="width:150px;">Acabado</a>
				</div>
			</div><!--End Spanish Questions--><!--End MCHAT 21-23-->
			
			<input id="formInstances" name="formInstances" type="hidden" value="${formInstances }"/>
			<input id="patientId" name="patientId" type="hidden" value="${patient.patientId}"/>
			<input id="encounterId" name="encounterId" type="hidden" value="${encounterId}"/>
			<input id="formId" name="formId" type="hidden" value="${formId}"/>
			<input id="formInstanceId" name="formInstanceId" type="hidden" value="${formInstanceId}"/>
			<input id="locationId" name="locationId" type="hidden" value="${locationId}"/>
			<input id="locationTagId" name="locationTagId" type="hidden" value="${locationTagId}"/>
			<input id="sessionId" name="sessionId" type="hidden" value="${sessionId}"/>
			<input id="MCHATScore" name="MCHATScore" type="hidden"/>
			<input id="language" name="language" type="hidden" value="${language}"/> 
		</form>
	</body>
</html>