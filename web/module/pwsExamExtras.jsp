		    <div id="examExtras">
                        <div class="examExtraCheckbox">
                            <c:choose>
                                <c:if test="${Special_Need == 'Y'}">
                                    <input type="checkbox" name="Special_Need" value="Y" checked disabled/>Special Need Child<br/>
                                </c:if>
                                <c:otherwise>
                                    <input type="checkbox" name="Special_Need" value="Y"/>Special Need Child<br/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div>
                            &nbsp;
                        </div>
                        <div class="examExtraCheckbox">
                            <input type="checkbox"/>Two ID's Checked<br/>
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
						<div id="informant" >
                            <b>Informant:</b> <c:out value="${Informant}"/>
                        </div>
                        <div>
                            &nbsp;
                        </div>
                        <div class="examExtraData">
                            <c:out value="${Language}"/>
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
                        <c:choose>
                            <c:when test="${Pain == '0'}"> 
                                <div class="examExtraData">
                            </c:when>
                            <c:otherwise>
                                <div class="ui-state-highlight examExtraData">
                            </c:otherwise>
                        </c:choose>
                            Pain (0-10):<c:out value="${Pain}"/>
                        </div>
                        <c:choose>
                            <c:when test="${Allergy == ' NONE'}"> 
                                <div class="examExtraData">
                            </c:when>
                            <c:otherwise>
                                <div class="ui-state-highlight examExtraData">
                            </c:otherwise>
                        </c:choose>
                            Allergies:<c:out value="${Allergy}"/>
                        </div>
                        <div class="examExtraData">
                            <c:out value="${MedicationLabel}"/>
                        </div>
                  </div>