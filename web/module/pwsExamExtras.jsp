		    <div id="examExtras">
                        <div class="examExtraCheckbox">
                            <input type="checkbox" name="Special_Need" value="yes" ${Special_Need == 'Y' ? 'checked' : ''}/>Special Need Child<br/> 
							<input id="Special_Need" name="Special_Need" type="hidden" value="no"/>
                        </div>
                        <div>
                            &nbsp;
                        </div>
                        <div class="examExtraCheckbox">
                            <input type="checkbox" name="MDTwoIDsChecked" value="Y"/>Two ID's Checked<br/>
                        </div>
                        <div class="examExtraCheckbox">
                            <input type="checkbox" name="screenedForAbuse" value="screened"/>Screened for abuse<br/>
                        </div>
                        <div class="examExtraCheckbox">
                            <input type="checkbox" name="discussedPhysicalActivity" value="Physical Activity"/>Discussed physical activity<br/>
                        </div>
                        <div class="examExtraCheckbox">
                            <input type="checkbox" name="discussedHealthyDiet" value="Healthy Diet"/>Discussed healthy diet<br/>
                        </div>                       
                        <div>
                            &nbsp;
                        </div>                                             
                        <div class="examExtraData">
                            <c:out value="${TobaccoLabel}"/>&nbsp;<c:out value="${TobaccoAnswer}"/>
                        </div>
                        <div class="examExtraData">
                            <c:out value="${AlcoholLabel}"/>&nbsp;<c:out value="${AlcoholAnswer}"/>
                        </div>
                        <div class="examExtraData">
                            <c:out value="${DrugsLabel}"/>&nbsp;<c:out value="${DrugsAnswer}"/>
                        </div>                                                  
                       <div>
                            &nbsp;
                        </div>                       
						<div class="medicalPerformed">
							<label>Medication Education Performed and/or Counseled on Vaccines</label>
								<input type="radio" class="uncheckableRadioButton" name="MedicationEducationPerformed" id="medicalEducationPerformedYES" value="yes"/>Y
								<input type="radio" class="uncheckableRadioButton" name="MedicationEducationPerformed" id="medicalEducationPerformedNO" value="no"/>N
								<input type="radio" class="uncheckableRadioButton" name="MedicationEducationPerformed" id="medicalEducationPerformedNA" value="not applicable"/>N/A<br>
						</div>
											 
						<div>
                            &nbsp;
                        </div>                       
                  </div>