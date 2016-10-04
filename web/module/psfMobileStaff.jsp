<div id="vitals_page" data-role="page" data-theme="b">
    <div id="vitals_header" data-role="header" >
        <a id="backQuestionsButton" data-role="button" data-icon="back" data-theme="b" data-rel="back">Questions</a>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <h1>(***Medical Staff Only***)</h1>
    </div>
    <div id="content_vitals" data-role="content">
        <a id='lnkVitalsPasscode' href="#vitals_passcode_dialog" data-rel="popup" data-transition="pop" data-position-to="window" style='display:none;'></a>
        <a id='lnkPasscodeError' href="#passcodeError" data-rel="popup" data-transition="pop" data-position-to="window" style='display:none;'></a>
        <div id="vitals_passcode_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="c">
            <div data-role="header" data-theme="b">
                <h1>Passcode</h1>
            </div>
            <div data-role="content">
                <span>Please enter the passcode to access the staff page.</span>
                <div style="margin: 0 auto;text-align: center;">
                    <input type="number" masktype="password" id="vitals_passcode" name="vitals_passcode" placeholder="Passcode"/>
                    <a id="backQuestionsButton" data-role="button" data-icon="back" data-theme="b" onclick="history.go(-2)" data-inline="true" style="width: 200px;">Questions</a>
                    <a href="#" id="goButton" onclick="checkPasscode()" data-role="button" data-inline="true" data-theme="b" style="width: 200px;">Go</a>
                </div>
            </div>
        </div>
        <div id="passcodeError" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="c">
            <div data-role="header" data-theme="b">
                <div>
                    <h3 style="text-align: center;">Passcode Error</h3>
                </div>
            </div>
            <div data-role="content">
                <div id="passcodeErrorResultDiv"></div>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="#vitals_passcode_dialog" data-rel="popup" data-position-to="window" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
        <div>
            <input type="checkbox" id="SickVisit" name="SickVisit" value="Y" ${SickVisit == "Y" ? 'checked' : '' }/><label for="SickVisit">Sick Visit</label>
            <input type="checkbox" id="MATwoIDsChecked" name="MATwoIDsChecked" value="Y" ${MATwoIDsChecked == "Y" ? 'checked' : '' }/><label for="MATwoIDsChecked">Two IDs checked</label>
            <input type="checkbox" id="Handout_Reviewed_by_MA" name="Handout_Reviewed_by_MA" value="diet and exercise" ${Handout_Reviewed_by_MA == "diet and exercise" ? 'checked' : '' }/><label for="Handout_Reviewed_by_MA">Diet and Exercise Handout Given</label>
			<br>
			<c:set var="age" value="${Age}"/>
			<c:set var="ageYr" value="${fn:substringBefore(age,' ')}" />
			<c:set var="ageMD" value="${fn:substringAfter(age,' ')}" />
			<c:if test ="${ageYr ge 3 && ageMD != 'mo' && ageMD != 'do'}">
				<div>
					<strong><label for="visionScreening">${patient.givenName}&nbsp;${patient.familyName} is due for vision screening. Please, screen and enter results in the EHR.</label></strong><br>
					<div class="ui-grid-solo">
						<input type="checkbox" id="passed" name="passed" value="Y" ${passed == "Y" ? 'checked' : '' }/><label for="passed">Passed (>20/30 both eyes)</label>
					</div>
					<div class="ui-grid-a">
						<div class="ui-block-a" >
							<input type="checkbox" id="failed" name="failed" value="Y" ${failed == "Y" ? 'checked' : '' }/><label for="failed">Failed (20/40 or worse either eye) ---></label>
						</div>
						<div class="ui-block-b" >
							<input type="checkbox" id="referral" name="referral" value="Y" ${referral == "Y" ? 'checked' : '' }/><label for="referral">Refer to ophthalmology</label>
						</div>
					</div>
					<div class="ui-grid-a">
						<div class="ui-block-a" >
							<input type="checkbox" id="not_cooperative" name="not_cooperative" value="Y" ${not_cooperative == "Y" ? 'checked' : '' }/><label for="not_cooperative">Patient unable to cooperate</label>
						</div>
						<div class="ui-block-b">
							<input type="checkbox" id="not_done" name="not_done" value="Y" ${not_done == "Y" ? 'checked' : '' }/><label for="not_done">Screening not done</label>
						</div>
					</div>
				</div>
			</c:if>
        </div>
                
        <div id="invalidLogin" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Invalid Login</h1>
            </div>
            <div data-role="content">
                <div id="loginResultDiv"></div>
                <div style="margin: 0 auto;text-align: center;">
                    <a data-inline="true" onclick="showLoginDialog()" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
        <div id="confirm_submit_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Confirm</h1>
            </div>
            <div data-role="content">
                <div>If all patient questions and staff information are complete, please click 'Submit' to permanently submit the form.</div>
                <div style="margin: 0 auto;text-align: center;">
                    <a id="confirm_submit_submit_button" data-role="button" data-theme="b" data-inline="true" onclick="completeForm()" style="width: 150px;">Submit</a>
                    <a id="confirm_submit_cancel_button" data-rel="back" data-role="button" data-theme="b" data-inline="true" style="width: 150px;">Cancel</a>
                </div>
            </div>
        </div>
        <a id='lnkLoadingDialog' href="#loadingDialog" data-rel="popup" data-transition="pop" data-position-to="window" style='display:none;'></a>
        <div id="loadingDialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
            <div data-role="content">
                <div style="margin: 0 auto;text-align: center;">
                    Saving...
                </div>
            </div>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a data-theme="b" data-role="button" onclick="finishVitals()" rel="external" data-ajax="false" style="width: 150px;">Submit</a>
    </div>
</div>