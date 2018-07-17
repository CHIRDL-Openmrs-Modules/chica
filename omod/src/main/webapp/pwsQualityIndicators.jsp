<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
<div class="psf_container">
    <header id="quality_indicators_header">
        <a href="">
            <h4 class="logo">Quality Indicators</h4>
        </a>
        <a href="#">
            <div class="ui-icon ui-icon-minusthick white"></div>
        </a>
    </header>
    <section class="psf_results" id="quality_indicators">
        <div class="patient_info_container">
            <table width="250" border="0">
                <tbody>
                    <tr>
                        <td align="left" style="width: 50;">
                            <input type="checkbox" name="Special_Need" id="Special_Need" value="yes" ${Special_Need=='Y' || Special_Need=='yes' ? 'checked' : ''}/>
							<label for="Special_Need">Special Need Child</label>
						</td>
                    </tr>
                    <tr>
                        <td align="left" style="width: 50;">
                            <input type="checkbox" name="MDTwoIDsChecked" id="MDTwoIDsChecked" value="Y" ${MDTwoIDsChecked=='Y' ? 'checked' : ''}/>
							<label for="MDTwoIDsChecked">Two ID's Checked</label>
						</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="patient_info_container">
            <table width="250" border="0">
                <tbody>
                    <tr>
                        <td align="left" style="width: 50;">
                            <input type="checkbox" name="screenedForAbuse" id="screenedForAbuse" value="screened" ${screenedForAbuse=='screened' ? 'checked' : ''}/>
							<label for="screenedForAbuse">Screened for abuse</label>
						</td>
                    </tr>
                    <tr>
                        <td align="left" style="width: 50;"><span><c:out value="${AlcoholLabel}"/>&nbsp;<c:out value="${AlcoholAnswer}"/></span>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="patient_info_container">
            <table width="250" border="0">
                <tbody>
                    <tr>
                        <td align="left" style="width: 50;">
                            <input type="checkbox" name="discussedHealthyDiet" id="discussedHealthyDiet" value="Healthy Diet" ${discussedHealthyDiet=='Healthy Diet' ? 'checked' : ''}/>
							<label for="discussedHealthyDiet">Discussed healthy diet</label>
						</td>
                    </tr>
                    <tr>
                        <td align="left" style="width: 50;"><span><c:out value="${TobaccoLabel}"/>&nbsp;<c:out value="${TobaccoAnswer}"/></span>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="patient_info_container">
            <table width="250" border="0">
                <tbody>
                    <tr>
                        <td align="left" style="width: 50;">
                            <input type="checkbox" name="discussedPhysicalActivity" id="discussedPhysicalActivity" value="Physical Activity" ${discussedPhysicalActivity=='Physical Activity' ? 'checked' : ''}/>
							<label for="discussedPhysicalActivity">Discussed physical activity</label>
                            <br/>
                        </td>
                    </tr>
                    <tr>
                        <td align="left" style="width: 50;"><span><c:out value="${DrugsLabel}"/>&nbsp;<c:out value="${DrugsAnswer}"/></span>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="medication_education_container">
            <span>Medication Education Performed and/or Counseled on
                Vaccines
            </span>
            <span> 
                 <input type="radio" class="uncheckableRadioButton" name="MedicationEducationPerformed" id="medicalEducationPerformedYES" value="yes" ${MedicationEducationPerformed == 'yes' ? 'checked' : ''}/>
				 <label for="medicalEducationPerformedYES">Y</label>
                 <input type="radio" class="uncheckableRadioButton" name="MedicationEducationPerformed" id="medicalEducationPerformedNO" value="no" ${MedicationEducationPerformed == 'no' ? 'checked' : ''}/>
				 <label for="medicalEducationPerformedNO">N</label>
                 <input type="radio" class="uncheckableRadioButton" name="MedicationEducationPerformed" id="medicalEducationPerformedNA" value="not applicable" ${MedicationEducationPerformed == 'not applicable' ? 'checked' : ''}/>
				 <label for="medicalEducationPerformedNA">N/A</label>
            </span>
        </div>
    </section>
</div>