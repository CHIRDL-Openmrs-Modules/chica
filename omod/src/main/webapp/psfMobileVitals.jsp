<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/psfMobileVitals.form" />
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
                <span>Please enter the passcode to access the vitals page.</span>
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
        <div class="ui-grid-a" style="padding-bottom: 0px;">
            <div class="ui-block-a" style="width: 42%">
              <div class="ui-grid-c">
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <c:choose>
                          <c:when test="${Height_HL != null }">
                            <strong><span style="line-height: 50px;"><c:out value="${Height_HL}"/>&nbsp;Height:</span></strong>
                          </c:when>
                          <c:otherwise>
                            <strong><span style="line-height: 50px;">Height:</span></strong>
                          </c:otherwise>
                      </c:choose>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;">
                      <c:choose>
                        <c:when test="${HeightP == '' && HeightS == ''}">
                            <input type="number" id="height" name="height" step="any" value=""/>
                        </c:when>
                        <c:otherwise>
                            <input type="number" id="height" name="height" step="any" value="${HeightP == '' ? 0 : HeightP}.${HeightS == '' ? 0 : HeightS}"/>
                        </c:otherwise>
                      </c:choose>
                      </span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">${HeightSUnits}</span></strong>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <c:choose>
                          <c:when test="${Weight_HL != null }">
                            <strong><span style="line-height: 50px;"><c:out value="${Weight_HL}"/>&nbsp;Weight:</span></strong>
                          </c:when>
                          <c:otherwise>
                            <strong><span style="line-height: 50px;">Weight:</span></strong>
                          </c:otherwise>
                      </c:choose>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;">
                      <c:choose>
                        <c:when test="${WeightP == '' && WeightS == ''}">
                            <input type="number" id="weight" name="weight" step="any" value=""/>
                        </c:when>
                        <c:otherwise>
                            <input type="number" id="weight" name="weight" step="any" value="${WeightP == '' ? 0 : WeightP}.${WeightS == '' ? 0 : WeightS}"/>
                        </c:otherwise>
                      </c:choose>
                      </span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <c:choose>
                          <c:when test="${WeightSUnits == 'oz.' }">
                            <strong><span style="line-height: 50px;">lb.oz</span></strong>
                          </c:when>
                          <c:otherwise>
                            <strong><span style="line-height: 50px;">${WeightSUnits}</span></strong>
                          </c:otherwise>
                      </c:choose>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <c:choose>
                          <c:when test="${HC_HL != null }">
                            <strong><span style="line-height: 50px;"><c:out value="${HC_HL}"/>&nbsp;HC:</span></strong>
                          </c:when>
                          <c:otherwise>
                            <strong><span style="line-height: 50px;">HC:</span></strong>
                          </c:otherwise>
                      </c:choose>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;">
                      <c:choose>
                        <c:when test="${HCP == '' && HCS == ''}">
                            <input type="number" id="hc" name="hc" step="any" value=""/>
                        </c:when>
                        <c:otherwise>
                            <input type="number" id="hc" name="hc" step="any" value="${HCP == '' ? 0 : HCP}.${HCS == '' ? 0 : HCS}"/>
                        </c:otherwise>
                      </c:choose>
                      </span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">cm.</span></strong>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <c:choose>
                          <c:when test="${BP_HL != null }">
                            <strong><span style="line-height: 50px;"><c:out value="${BP_HL}"/>&nbsp;BP:</span></strong>
                          </c:when>
                          <c:otherwise>
                            <strong><span style="line-height: 50px;">BP:</span></strong>
                          </c:otherwise>
                      </c:choose>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="BPS" name="BPS" value="${BPS}"/></span>
                  </div>
                  <div class="ui-block-c" style="height: 50px;padding-left:10px;width: 15px;">
                      <span style="line-height: 50px;"><c:out value="/"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;padding-left:10px;">
                      <span style="line-height: 50px;"><input type="number" id="BPD" name="BPD" value="${BPD}"/></span>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">Temp:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;">
                      <c:choose>
                        <c:when test="${TempP == '' && TempS == ''}">
                            <input type="number" id="temp" name="temp" step="any" value=""/>
                        </c:when>
                        <c:otherwise>
                            <input type="number" id="temp" name="temp" step="any" value="${TempP == '' ? 0 : TempP}.${TempS == '' ? 0 : TempS}"/>
                        </c:otherwise>
                      </c:choose>
                      </span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">deg. F</span></strong>
                  </div>
                  <div class="ui-block-a" style="text-align: center;margin-bottom: 10px;margin-top: 10px;width: 100%;">
                      <fieldset data-role="controlgroup" data-type="horizontal" style="margin: auto;">
                            <input type="radio" name="TemperatureType" id="Temperature_Method_Oral" value="Oral Temp Type" data-theme="c" ${TemperatureType == 'Oral Temp Type' ? 'checked' : ''}/>
                            <label for="Temperature_Method_Oral">Oral</label>
                            <input type="radio" name="TemperatureType" id="Temperature_Method_Rectal" value="Rectal Temp Type" data-theme="c" ${TemperatureType == 'Rectal Temp Type' ? 'checked' : ''}/>
                            <label for="Temperature_Method_Rectal">Rectal</label>
                            <input type="radio" name="TemperatureType" id="Temperature_Method_Axillary" value="Axillary Temp Type" data-theme="c" ${TemperatureType == 'Axillary Temp Type' ? 'checked' : ''}/>
                            <label for="Temperature_Method_Axillary">Axillary</label>
                        </fieldset>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">Pulse:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="Pulse" name="Pulse" value="${Pulse}"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">/min.</span></strong>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">RR:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="RR" name="RR" value="${RR}"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;">
                      <span style="line-height: 50px;"></span>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">Pulse Ox:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;margin-bottom: 8px;">
                      <span style="line-height: 50px;"><input type="number" id="PulseOx" name="PulseOx" value="${PulseOx}"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">%</span></strong>
                  </div>
              </div>
            </div>
            <div class="ui-block-b" style="width: 58%">
               <div class="ui-grid-b">
                  <div class="ui-block-b" style="height: 25px;text-align: center;width: 100%;">
                      <strong><span>Uncooperative/Unable to Screen:</span></strong>
                  </div>
                  <div class="ui-block-a" style="height: 50px;margin-bottom: 20px;">
                      <span><input type="checkbox" id="NoVision" name="NoVision" value="Y" style="vertical-align: top; margin: 0px;" ${NoVision == "Y" ? 'checked' : ''}/><label for="NoVision">Vision</label></span>
                  </div>
                  <div class="ui-block-b" style="height: 50px;margin-bottom: 20px;">
                      <span><input type="checkbox" id="NoHearing" name="NoHearing" value="Y" style="vertical-align: top; margin: 0px;" ${NoHearing == "Y" ? 'checked' : ''}/><label for="NoHearing">Hearing</label></span>
                  </div>
                  <div class="ui-block-c" style="height: 50px;margin-bottom: 20px;">
                      <span><input type="checkbox" id="NoBP" name="NoBP" value="Y" style="vertical-align: top; margin: 0px;" ${NoBP == "Y" ? 'checked' : ''}/><label for="NoBP">BP</label></span>
                  </div>
                  <div class="ui-block-a" style="text-align: center;width: 5%"></div>
                  <div class="ui-block-b" style="height: 50px;text-align: center;width: 85%;">
                      <div class="ui-grid-a" style="text-align: center;">
                        <div class="ui-block-a" style="height: 50px;text-align: right;width: 50%;">
                            <c:choose>
                              <c:when test="${VisionL_HL != null }">
                                <strong><span style="line-height: 50px;"><c:out value="${VisionL_HL}"/>&nbsp;Vision Left: 20/</span></strong>
                              </c:when>
                              <c:otherwise>
                                <strong><span style="line-height: 50px;">Vision Left: 20/</span></strong>
                              </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="ui-block-b" style="height: 50px;width: 50%;padding-left: 10px;margin-bottom: 10px;">
                            <span><input type="number" id="VisionL" name="VisionL" value="${VisionL}"/></span>
                        </div>
                      </div>
                  </div>
                  <div class="ui-block-c" style="text-align: center;height: 50px;width: 5%"></div>
                  <div class="ui-block-a" style="text-align: center;width: 5%"></div>
                  <div class="ui-block-b" style="height: 50px;text-align: center;width: 85%;">
                      <div class="ui-grid-a" style="text-align: center;">
                        <div class="ui-block-a" style="height: 50px;text-align: right;width: 50%;">
                            <c:choose>
                              <c:when test="${VisionR_HL != null }">
                                <strong><span style="line-height: 50px;"><c:out value="${VisionR_HL}"/>&nbsp;Vision Right: 20/</span></strong>
                              </c:when>
                              <c:otherwise>
                                <strong><span style="line-height: 50px;">Vision Right: 20/</span></strong>
                              </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="ui-block-b" style="height: 50px;width: 50%;padding-left: 10px;margin-bottom: 10px;">
                            <span><input type="number" id="VisionR" name="VisionR" value="${VisionR}"/></span>
                        </div>
                      </div>
                  </div>
                  <div class="ui-block-c" style="text-align: center;height: 50px;width: 10%"></div>
                  <div class="ui-block-a" style="text-align: center;height: 50px;margin-bottom: 20px;width: 20%"></div>
                  <div class="ui-block-b" style="text-align: center;height: 50px;margin-bottom: 20px;width: 60%">
                      <span><input type="checkbox" id="Vision_Corrected" name="Vision_Corrected" value="Y" style="vertical-align: top; margin: 0px;" ${Vision_Corrected == "Y" ? 'checked' : ''}/><label for="Vision_Corrected">Vision Corrected?</label></span>
                  </div>
                  <div class="ui-block-c" style="text-align: center;height: 50px;margin-bottom: 20px;width: 20%"></div>
                  <div class="ui-block-a" style="height: 60px;width: 100%;padding-top:10px;">
                      <div class="ui-grid-a">
                          <div class="ui-block-a" style="text-align: right;width: 50%;padding-right:10px;">
                           <c:choose>
                              <c:when test="${HearL_HL != null }">
                                <strong><span style="line-height: 50px;"><c:out value="${HearL_HL}"/>&nbsp;Left Ear @ 25db:</span></strong>
                              </c:when>
                              <c:otherwise>
                                <strong><span style="line-height: 50px;">Left Ear @ 25db:</span></strong>
                              </c:otherwise>
                           </c:choose>
                          </div>
                          <div class="ui-block-b" style="width: 50%;height: 50px;display: table">
                              <div data-role="fieldcontain" style="display: table-cell;">
                                <fieldset data-role="controlgroup" data-type="horizontal">
                                    <input type="radio" name="HearL" id="HearL_pass" value="P" data-theme="c" ${HearL == "P" ? 'checked' : '' }/>
                                    <label for="HearL_pass">P</label>
                                    <input type="radio" name="HearL" id="HearL_fail" value="F" data-theme="c" ${HearL == "F" ? 'checked' : '' }/>
                                    <label for="HearL_fail">F</label>
                                </fieldset>
                              </div>
                          </div>
                      </div>
                  </div>
                  <div class="ui-block-a" style="height: 60px;width: 100%;padding-top:10px;">
                      <div class="ui-grid-a">
                          <div class="ui-block-a" style="text-align: right;width: 50%;padding-right:10px;">
                           <c:choose>
                              <c:when test="${HearR_HL != null }">
                                <strong><span style="line-height: 50px;"><c:out value="${HearR_HL}"/>&nbsp;Right Ear @ 25db:</span></strong>
                              </c:when>
                              <c:otherwise>
                                <strong><span style="line-height: 50px;">Right Ear @ 25db:</span></strong>
                              </c:otherwise>
                           </c:choose>
                          </div>
                          <div class="ui-block-b" style="width: 50%;height: 50px;display: table">
                              <div data-role="fieldcontain" style="display: table-cell;">
                                <fieldset data-role="controlgroup" data-type="horizontal">
                                    <input type="radio" name="HearR" id="HearR_pass" value="P" data-theme="c" ${HearR == "P" ? 'checked' : '' }/>
                                    <label for="HearR_pass">P</label>
                                    <input type="radio" name="HearR" id="HearR_fail" value="F" data-theme="c" ${HearR == "F" ? 'checked' : '' }/>
                                    <label for="HearR_fail">F</label>
                                </fieldset>
                              </div>
                          </div>
                      </div>
                  </div>
                  <div class="ui-block-a" style="text-align: center;height: 25px;margin-bottom: 0px;width: 14%"></div>
                  <div class="ui-block-b" style="text-align: center;height: 25px;margin-bottom: 0px;width: 86%"></div>
                  <div class="ui-block-a" style="text-align: center;height: 60px;margin-bottom: 0px;width: 14%"></div>
                  <div class="ui-block-b" style="text-align: center;height: 60px;margin-bottom: 0px;width: 86%;">
                      <span><input type="checkbox" id="Handout_Reviewed_by_MA" name="Handout_Reviewed_by_MA" value="diet and exercise" style="vertical-align: top; margin: 0px;" ${Handout_Reviewed_by_MA == "diet and exercise" ? 'checked' : '' }/><label for="Handout_Reviewed_by_MA">Diet and Exercise Handout Given</label></span>
                  </div>
               </div>
            </div>
        </div>
        <div class="ui-grid-a">
            <div class="ui-block-a">
                <input type="checkbox" id="SickVisit" name="SickVisit" value="Y" ${SickVisit == "Y" ? 'checked' : '' }/><label for="SickVisit">Sick Visit</label>
            </div>
            <div class="ui-block-b">
                <input type="checkbox" id="MATwoIDsChecked" name="MATwoIDsChecked" value="Y" ${MATwoIDsChecked == "Y" ? 'checked' : '' }/><label for="MATwoIDsChecked">Two IDs checked</label>
            </div>
        </div>
        <div id="validation_error_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <div>
                    <h3 style="text-align: center;">Validation Error</h3>
                </div>
            </div>
            <div data-role="content">
                <span id="validationError"></span>
                <div style="margin: 0 auto;text-align: center;">
                    <a id="validationOkButton" href="#" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
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
                <div>If all patient questions and vitals information are complete, please click 'Submit' to permanently submit the form.</div>
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
<input id="HeightP" name="HeightP" type="hidden"/>
<input id="HeightS" name="HeightS" type="hidden"/>
<input id="WeightP" name="WeightP" type="hidden"/>
<input id="WeightS" name="WeightS" type="hidden"/>
<input id="TempP" name="TempP" type="hidden"/>
<input id="TempS" name="TempS" type="hidden"/>
<input id="HCP" name="HCP" type="hidden"/>
<input id="HCS" name="HCS" type="hidden"/>

<!-- DWE CHICA-430 Add hidden input to allow these parameters to be included when the checkboxes are unchecked -->
<input id="Vision_Corrected" name="Vision_Corrected" type="hidden" value="N"/>
<input id="NoBP" name="NoBP" type="hidden" value="N"/>
<input id="NoHearing" name="NoHearing" type="hidden" value="N"/>
<input id="NoVision" name="NoVision" type="hidden" value="N"/>