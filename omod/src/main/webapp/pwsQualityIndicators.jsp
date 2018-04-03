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
                            <input type="checkbox" name="Special_Need" value="yes" ${Special_Need=='Y' || Special_Need=='yes' ? 'checked' : ''}/>Special Need Child</td>
                    </tr>
                    <tr>
                        <td align="left" style="width: 50;">
                            <input type="checkbox" name="MDTwoIDsChecked" value="Y" ${MDTwoIDsChecked=='Y' ? 'checked' : ''}/>Two ID's Checked</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="patient_info_container">
            <table width="250" border="0">
                <tbody>
                    <tr>
                        <td align="left" style="width: 50;">
                            <input type="checkbox" name="screenedForAbuse" value="screened" ${screenedForAbuse=='screened' ? 'checked' : ''}/>Screened for abuse</td>
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
                            <input type="checkbox" name="discussedHealthyDiet" value="Healthy Diet" ${discussedHealthyDiet=='Healthy Diet' ? 'checked' : ''}/>Discussed healthy diet</td>
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
                            <input type="checkbox" name="discussedPhysicalActivity" value="Physical Activity" ${discussedPhysicalActivity=='Physical Activity' ? 'checked' : ''}/>Discussed physical activity
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
                 <input type="radio" class="uncheckableRadioButton" name="MedicationEducationPerformed" id="medicalEducationPerformedYES" value="yes" ${MedicationEducationPerformed == 'yes' ? 'checked' : ''}/>Y
                 <input type="radio" class="uncheckableRadioButton" name="MedicationEducationPerformed" id="medicalEducationPerformedNO" value="no" ${MedicationEducationPerformed == 'no' ? 'checked' : ''}/>N
                 <input type="radio" class="uncheckableRadioButton" name="MedicationEducationPerformed" id="medicalEducationPerformedNA" value="not applicable" ${MedicationEducationPerformed == 'not applicable' ? 'checked' : ''}/>N/A
            </span>
        </div>
    </section>
</div>