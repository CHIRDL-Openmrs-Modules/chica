<%@ include file="/WEB-INF/template/include.jsp" %>

<!doctype html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/MCHATRMobile.form" />
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
		<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" /> 
		<script src="${pageContext.request.contextPath}/moduleResources/chica/browserFixMobile.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.blockUI.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
		<script>var ctx = "${pageContext.request.contextPath}";</script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/MCHATRMobile.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
		<title>M-CHAT-R</title>
	</head>
	<c:set var="search" value="'" />
	<c:set var="replace" value="\\'" />
	<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}" />
	<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}" />
	<c:set var="mchatrTitle" value="M-CHAT-R&trade;:" />
	
	<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">
		<form id="MCHATRForm" method="post" action="MCHATRMobile.form" enctype="multipart/form-data">
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
					<h1>${mchatrTitle}</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguage('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a class="quitButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.quitForm()" data-transition="pop" data-icon="forward">Quit</a>
				</div>
				
				<div data-role="content">
					<strong><span id="additionalQuestions">Please answer these questions about your child. Keep in mind how your child usually behaves. If you have seen your child do the behavior a few times, but he or she does not usually do it, then please answer no. Please select yes or no for every question. Thank you very much.</span></strong>
				    <div><br /></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a id="startButton" href="#" onClick="changePage(1)" data-role="button" data-theme="b" style="width:150px;">Start</a>
				</div>
			</div> <!--End Instructions Page-->
			<c:set var="copyright" value='&copy; 2009 Diana Robins, Deborah Fein, &amp; Marianne Barton' />
			
			<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>${mchatrTitle}</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a class="quitButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.quitForm()" data-transition="pop" data-icon="forward">Quit</a>
				</div>
				
				<div id="content_1" data-role="content">
					<c:set var="quest1" value='If you point at something across the room, does your child look at it? (FOR EXAMPLE, if you point at a toy or an animal, does your child look at the toy or animal?)' />
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
					<c:set var="quest2" value='Have you ever wondered if your child might be deaf?' />
					<input id="Question_2"  name="Question_2" type="hidden" value="${quest2}" />
					<strong>${quest2}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest2}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_2" id="Choice_2_Yes" value="failed" data-theme="c" />
							<label for="Choice_2_Yes">Yes</label>
							<input type="radio" name="Choice_2" id="Choice_2_No" value="passed" data-theme="c" />
							<label for="Choice_2_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest3" value='Does your child play pretend or make-believe? (FOR EXAMPLE, pretend to drink from an empty cup, pretend to talk on a phone, or pretend to feed a doll or stuffed animal?)' />
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
					<c:set var="quest4" value='Does your child like climbing on things? (FOR EXAMPLE, furniture, playground equipment, or stairs)' />
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
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(2)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Next</a>
				</div>
			</div>
			
			<div id="question_page_2" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>${mchatrTitle}</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage2Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a class="quitButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.quitForm()" data-transition="pop" data-icon="forward">Quit</a>
				</div>
				
				<div id="content_2" data-role="content">
				    <c:set var="quest5" value='Does your child make unusual finger movements near his or her eyes? (FOR EXAMPLE, does your child wiggle his or her fingers close to his or her eyes?)' />
                    <input id="Question_5"  name="Question_5" type="hidden" value="${quest5}" />
                    <strong>${quest5}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest5}")'></a>
                    <div data-role="fieldcontain" style="margin-top:0px;">
                        <fieldset data-role="controlgroup" data-type="horizontal">
                            <input type="radio" name="Choice_5" id="Choice_5_Yes" value="failed" data-theme="c" />
                            <label for="Choice_5_Yes">Yes</label>
                            <input type="radio" name="Choice_5" id="Choice_5_No" value="passed" data-theme="c" />
                            <label for="Choice_5_No">No</label>
                        </fieldset>
                    </div>
                    <br />
					<c:set var="quest6" value='Does your child point with one finger to ask for something or to get help? (FOR EXAMPLE, pointing to a snack or toy that is out of reach)' />
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
					<c:set var="quest7" value='Does your child point with one finger to show you something interesting? (FOR EXAMPLE, pointing to an airplane in the sky or a big truck in the road)' />
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
					<c:set var="quest8" value='Is your child interested in other children? (FOR EXAMPLE, does your child watch other children, smile at them, or go to them?)' />
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
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(1)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Previous</a>
					<a href="#" onClick="changePage(3)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Next</a>
				</div>
			</div>
			
			<div id="question_page_3" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>${mchatrTitle}</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage3Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a class="quitButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.quitForm()" data-transition="pop" data-icon="forward">Quit</a>
				</div>
				
				<div id="content_3" data-role="content">
                    <c:set var="quest9" value='Does your child show you things by bringing them to you or holding them up for you to see - not to get help, but just to share? (FOR EXAMPLE, showing you a flower, a stuffed animal, or a toy truck)' />
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
                    <c:set var="quest10" value='Does your child respond when you call his or her name? (FOR EXAMPLE, does he or she look up, talk or babble, or stop what he or she is doing when you call his or her name?)' />
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
                    <br />
					<c:set var="quest11" value='When you smile at your child, does he or she smile back at you?' />
					<input id="Question_11"  name="Question_11" type="hidden" value="${quest11}" />
					<strong>${quest11}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest11}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_11" id="Choice_11_Yes" value="passed" data-theme="c" />
							<label for="Choice_11_Yes">Yes</label>
							<input type="radio" name="Choice_11" id="Choice_11_No" value="failed" data-theme="c" />
							<label for="Choice_11_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest12" value='Does your child get upset by everyday noises? (FOR EXAMPLE, does your child scream or cry to noise such as a vacuum cleaner or loud music?)' />
					<input id="Question_12"  name="Question_12" type="hidden" value="${quest12}" />
					<strong>${quest12}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest12}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_12" id="Choice_12_Yes" value="failed" data-theme="c" />
							<label for="Choice_12_Yes">Yes</label>
							<input type="radio" name="Choice_12" id="Choice_12_No" value="passed" data-theme="c" />
							<label for="Choice_12_No">No</label>
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
					<h1>${mchatrTitle}</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage4Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
					<a class="quitButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.quitForm()" data-transition="pop" data-icon="forward">Quit</a>
				</div>
				
				<div id="content_4" data-role="content">
                    <c:set var="quest13" value='Does your child walk?' />
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
                    <c:set var="quest14" value='Does your child look you in the eye when you are talking to him or her, playing with him or her, or dressing him or her?' />
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
                    <c:set var="quest15" value='Does your child try to copy what you do? (FOR EXAMPLE, wave bye-bye, clap, or make a funny noise when you do)' />
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
                    <br />
					<c:set var="quest16" value='If you turn your head to look at something, does your child look around to see what you are looking at?' />
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
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(3)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Previous</a>
					<a href="#" onClick="changePage(5)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Next</a>
				</div>
			</div>
			<div id="question_page_5" data-role="page" data-theme="b" type="question_page">
                <div data-role="header">
                    <h1>${mchatrTitle}</h1>
                    <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
                    <a id="langPage5Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&ntilde;ol</a>
                    <a class="quitButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.quitForm()" data-transition="pop" data-icon="forward">Quit</a>
                </div>
                
                <div id="content_5" data-role="content">
                    <c:set var="quest17" value='Does your child try to get you to watch him or her? (FOR EXAMPLE, does your child look at you for praise, or say "look" or "watch me"?)' />
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
                    <c:set var="quest18" value='Does your child understand when you tell him or her to do something? (FOR EXAMPLE, if you don&#39;t point, can your child understand "put the book on the chair" or "bring me the blanket"?)' />
                    <input id="Question_18"  name="Question_18" type="hidden" value="${quest18}" />
                    <strong>${quest18}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest18}")'></a>
                    <div data-role="fieldcontain" style="margin-top:0px;">
                        <fieldset data-role="controlgroup" data-type="horizontal">
                            <input type="radio" name="Choice_18" id="Choice_18_Yes" value="passed" data-theme="c" />
                            <label for="Choice_18_Yes">Yes</label>
                            <input type="radio" name="Choice_18" id="Choice_18_No" value="failed" data-theme="c" />
                            <label for="Choice_18_No">No</label>
                        </fieldset>
                    </div>
                    <br />
                    <c:set var="quest19" value='If something new happens, does your child look at your face to see how you feel about it? (FOR EXAMPLE, if he or she hears a strange or funny noise, or sees a new toy, will he or she look at your face?)' />
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
                    <c:set var="quest20" value='Does your child like movement activities? (FOR EXAMPLE, being swung or bounced on your knee)' />
                    <input id="Question_20"  name="Question_20" type="hidden" value="${quest20}" />
                    <strong>${quest20}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest20}")'></a>
                    <div data-role="fieldcontain" style="margin-top:0px;">
                        <fieldset data-role="controlgroup" data-type="horizontal">
                            <input type="radio" name="Choice_20" id="Choice_20_Yes" value="passed" data-theme="c" />
                            <label for="Choice_20_Yes">Yes</label>
                            <input type="radio" name="Choice_20" id="Choice_20_No" value="failed" data-theme="c" />
                            <label for="Choice_20_No">No</label>
                        </fieldset>
                    </div>
                    <div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
                    <%@ include file="mobileFinishDialogs.jsp" %>
                </div>
                
                <div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
                    <a href="#" onClick="changePage(4)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Previous</a>
                    <a href="#" onClick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Continue</a>
                </div>
            </div>
			
			<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>${mchatrTitle}</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
					<a class="quitButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.quitForm()" data-transition="pop" data-icon="forward">Dejar</a>
				</div>
				
				<div id="content_1_sp" data-role="content">
					<c:set var="quest1_2_sp" value='&iquest;Si usted se&ntilde;ala un objeto del otro lado del cuarto, su hijo/a lo mira? (POR EJEMPLO &iquest;Si usted se&ntilde;ala un juguete o un animal, su hijo/a mira al juguete o al animal?)' />
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
					<c:set var="quest2_2_sp" value='&iquest;Alguna vez se ha preguntado si su hijo/a es sordo/a?' />
					<input id="Question_2_2"  name="Question_2_2" type="hidden" value="${quest2_2_sp}" />
					<strong>${quest2_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest2_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_2_sp" id="Choice_2_sp_Yes" value="failed" data-theme="c" />
							<label for="Choice_2_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_2_sp" id="Choice_2_sp_No" value="passed" data-theme="c" />
							<label for="Choice_2_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest3_2_sp" value='&iquest;Su hijo/a juega juegos de fantas&iacute;a o imaginaci&oacute;n? (POR EJEMPLO finge beber de una taza vac&iacute;a, finge hablar por tel&eacute;fono o finge darle de comer a una mu&ntilde;eca o un peluche)' />
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
					<c:set var="quest4_2_sp" value='&iquest;A su hijo/a le gusta treparse a las cosas? (POR EJEMPLO muebles, escaleras o juegos infantiles)' />
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
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(2)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Proximo</a>
				</div>
			</div>
			
			<div id="question_page_2_sp" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>${mchatrTitle}</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage2SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
					<a class="quitButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.quitForm()" data-transition="pop" data-icon="forward">Dejar</a>
				</div>
				
				<div id="content_2_sp" data-role="content">
				    <c:set var="quest5_2_sp" value='&iquest;Su hijo/a hace movimientos inusuales con los dedos cerca de sus ojos? (POR EJEMPLO &iquest;Mueve sus dedos cerca de sus ojos de manera inusual?)' />
                    <input id="Question_5_2"  name="Question_5_2" type="hidden" value="${quest5_2_sp}" />
                    <strong>${quest5_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest5_2_sp}")'></a>
                    <div data-role="fieldcontain" style="margin-top:0px;">
                        <fieldset data-role="controlgroup" data-type="horizontal">
                            <input type="radio" name="Choice_5_sp" id="Choice_5_sp_Yes" value="failed" data-theme="c" />
                            <label for="Choice_5_sp_Yes">S&iacute;</label>
                            <input type="radio" name="Choice_5_sp" id="Choice_5_sp_No" value="passed" data-theme="c" />
                            <label for="Choice_5_sp_No">No</label>
                        </fieldset>
                    </div>
                    <br />
					<c:set var="quest6_2_sp" value='&iquest;Su hijo/a apunta o se&ntilde;ala con un dedo cuando quiere pedir algo o pedir ayuda? (POR EJEMPLO se&ntilde;ala un juguete o algo para comer que est&aacute; fuera de su alcance)' />
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
					<c:set var="quest7_2_sp" value='&iquest;Su hijo/a apunta o se&ntilde;ala con un dedo cuando quiere mostrarle algo interesante? (POR EJEMPLO se&ntilde;ala un avi&oacute;n en el cielo o un cami&oacute;n grande en el camino)' />
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
					<c:set var="quest8_2_sp" value='&iquest;Su hijo/a muestra inter&eacute;s en otros ni&ntilde;os? (POR EJEMPLO &iquest;mira con atenci&oacute;n a otros ni&ntilde;os, les sonr&iacute;e o se les acerca?)' />
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
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(1)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Anterior</a>
					<a href="#" onClick="changePage(3)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Proximo</a>
				</div>
			</div>
			
			<div id="question_page_3_sp" data-role="page" data-theme="b" type="question_page">
				<div data-role="header">
					<h1>${mchatrTitle}</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage3SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
					<a class="quitButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.quitForm()" data-transition="pop" data-icon="forward">Dejar</a>
				</div>
				
				<div id="content_3_sp" data-role="content">
				    <c:set var="quest9_2_sp" value='&iquest;Su hijo/a le muestra cosas acerc&aacute;ndoselas a usted o levant&aacute;ndolas para que usted las vea, no para pedir ayuda sino para compartirlas con usted? (POR EJEMPLO le muestra una flor, un peluche o un cami&oacute;n/carro de juguete)' />
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
                    <c:set var="quest10_2_sp" value='&iquest;Su hijo/a responde cuando usted le llama por su nombre? (POR EJEMPLO &iquest;Cuando usted lo llama por su nombre: lo mira a usted, habla, balbucea, o deja de hacer lo que estaba haciendo?)' />
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
                    <br />
					<c:set var="quest11_2_sp" value='&iquest;Cu&aacute;ndo usted le sonr&iacute;e a su hijo/a, &eacute;l o ella le devuelve la sonrisa?' />
					<input id="Question_11_2"  name="Question_11_2" type="hidden" value="${quest11_2_sp}" />
					<strong>${quest11_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest11_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_11_sp" id="Choice_11_sp_Yes" value="passed" data-theme="c" />
							<label for="Choice_11_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_11_sp" id="Choice_11_sp_No" value="failed" data-theme="c" />
							<label for="Choice_11_sp_No">No</label>
						</fieldset>
					</div>
					<br />
					<c:set var="quest12_2_sp" value='&iquest;A su hijo/a le molestan los ruidos cotidianos? (POR EJEMPLO &iquest;Llora o grita cuando escucha la aspiradora o m&uacute;sica muy fuerte?)' />
					<input id="Question_12_2"  name="Question_12_2" type="hidden" value="${quest12_2_sp}" />
					<strong>${quest12_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest12_2_sp}")'></a>
					<div data-role="fieldcontain" style="margin-top:0px;">
						<fieldset data-role="controlgroup" data-type="horizontal">
							<input type="radio" name="Choice_12_sp" id="Choice_12_sp_Yes" value="failed" data-theme="c" />
							<label for="Choice_12_sp_Yes">S&iacute;</label>
							<input type="radio" name="Choice_12_sp" id="Choice_12_sp_No" value="passed" data-theme="c" />
							<label for="Choice_12_sp_No">No</label>
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
					<h1>${mchatrTitle}</h1>
					<h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
					<a id="langPage4SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
					<a class="quitButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.quitForm()" data-transition="pop" data-icon="forward">Dejar</a>
				</div>
				
				<div id="content_4_sp" data-role="content">
				    <c:set var="quest13_2_sp" value='&iquest;Su hijo/a camina?' />
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
                    <c:set var="quest14_2_sp" value='&iquest;Su hijo/a le mira a los ojos cuando usted le habla, juega con &eacute;l/ella o lo/la viste?' />
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
                    <c:set var="quest15_2_sp" value='&iquest;Su hijo/a trata de imitar sus movimientos? (POR EJEMPLO decir adi&oacute;s con la mano, aplaudir o alg&uacute;n ruido chistoso que usted haga)' />
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
                    <br />
					<c:set var="quest16_2_sp" value='&iquest;Si usted se voltea a ver algo, su hijo/a trata de ver que es lo que usted est&aacute; mirando?' />
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
					<div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
				</div>
				
				<div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
					<a href="#" onClick="changePage(3)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Anterior</a>
					<a href="#" onClick="changePage(5)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Proximo</a>
				</div>
			</div>
			<div id="question_page_5_sp" data-role="page" data-theme="b" type="question_page">
                <div data-role="header">
                    <h1>${mchatrTitle}</h1>
                    <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
                    <a id="langPage5SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onClick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
                    <a class="quitButton" data-role="button" class="ui-btn-right" data-theme="b" onClick="parent.quitForm()" data-transition="pop" data-icon="forward">Dejar</a>
                </div>
                
                <div id="content_5_sp" data-role="content">
                    <c:set var="quest17_2_sp" value='&iquest;Su hijo/a trata que usted lo mire? (POR EJEMPLO &iquest;Busca que usted lo/la halague, o dice "mirame"?)' />
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
                    <c:set var="quest18_2_sp" value='&iquest;Su hijo/a le entiende cuando usted le dice que haga algo? (POR EJEMPLO &iquest;Su hijo/a entiende "pon el libro en la silla" o "tr&aacute;eme la cobija" sin que usted haga se&ntilde;as?)' />
                    <input id="Question_18_2"  name="Question_18_2" type="hidden" value="${quest18_2_sp}" />
                    <strong>${quest18_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest18_2_sp}")'></a>
                    <div data-role="fieldcontain" style="margin-top:0px;">
                        <fieldset data-role="controlgroup" data-type="horizontal">
                            <input type="radio" name="Choice_18_sp" id="Choice_18_sp_Yes" value="passed" data-theme="c" />
                            <label for="Choice_18_sp_Yes">S&iacute;</label>
                            <input type="radio" name="Choice_18_sp" id="Choice_18_sp_No" value="failed" data-theme="c" />
                            <label for="Choice_18_sp_No">No</label>
                        </fieldset>
                    </div>
                    <br />
                    <c:set var="quest19_2_sp" value='&iquest;Si algo nuevo ocurre, su hijo/a lo mira a la cara para ver c&oacute;mo se siente usted al respecto? S&iacute; (POR EJEMPLO &iquest;Si oye un ruido extra&ntilde;o o ve un juguete nuevo, se voltear&iacute;a a ver su cara?)' />
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
                    <c:set var="quest20_2_sp" value='&iquest;A su hijo/a le gustan las actividades con movimiento? (POR EJEMPLO Le gusta que lo mezan/columpien, o que lo haga saltar en sus rodillas)' />
                    <input id="Question_20_2"  name="Question_20_2" type="hidden" value="${quest20_2_sp}" />
                    <strong>${quest20_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onClick='readText("${quest20_2_sp}")'></a>
                    <div data-role="fieldcontain" style="margin-top:0px;">
                        <fieldset data-role="controlgroup" data-type="horizontal">
                            <input type="radio" name="Choice_20_sp" id="Choice_20_sp_Yes" value="passed" data-theme="c" />
                            <label for="Choice_20_sp_Yes">S&iacute;</label>
                            <input type="radio" name="Choice_20_sp" id="Choice_20_sp_No" value="failed" data-theme="c" />
                            <label for="Choice_20_sp_No">No</label>
                        </fieldset>
                    </div>
                    <div style="float:right;"><br /><span style="float:right; font-size:50%;">${copyright}</span></div>
                    <%@ include file="mobileFinishDialogs_SP.jsp" %>
                </div>
                
                <div data-role="footer" style="text-align:center; padding-bottom:20px; padding-top:20px;">
                    <a href="#" onClick="changePage(4)" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Anterior</a>
                    <a href="#" onClick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width:150px;">Continuar</a>
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
			<input id="MchatRTotalItemsFailed" name="MchatRTotalItemsFailed" type="hidden"/>
			<input id="language" name="language" type="hidden" value="${language}"/>
		</form>		
	</body>
</html>
