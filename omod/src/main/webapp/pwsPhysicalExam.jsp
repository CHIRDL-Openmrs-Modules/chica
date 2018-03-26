<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
                <div id="exam">
                    <div id="physicalExam">
                        <div id="examTitle">
                            <b>Physical Exam:</b>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examNames">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                            Nl<br/>
                        </div>
                        <div class="examHeader">
                            Abnl<br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${GeneralExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            General:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_General" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_General" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${HeadExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Head:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Head" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Head" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${SkinExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Skin:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Skin" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Skin" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${EyesVisionExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Eyes:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Eyes" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Eyes" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${EarsHearingExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Ears:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Ears" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Ears" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${NoseThroatExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Nose/Throat:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Nose" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Nose" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${TeethGumsExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Teeth/Gums:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Teeth" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Teeth" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${NodesExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Nodes:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Nodes" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Nodes" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${ChestLungsExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Chest/Lungs:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Chest" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Chest" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${HeartPulsesExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Heart/Pulses:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Heart" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Heart" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${AbdomenExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Abdomen:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Abdomen" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Abdomen" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${ExtGenitaliaExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Ext Genitalia:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_ExtGenitalia" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_ExtGenitalia" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${BackExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Back:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Back" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Back" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${NeuroExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Neuro:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Neuro" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Neuro" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div class="examFlag">
                            <c:out value="${ExtremitiesExamA}"/><br/>
                        </div>
                        <div class="examNames">
                            Extremities:<br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Extremities" class="uncheckableRadioButton" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                            <input type="radio" name="Entry_Extremities" class="uncheckableRadioButton" value="A"/><br/>
                        </div>
                        <div id="examLegend">
                            <b><font style="color:red;">*</font> = Previously Abnormal</b>
                        </div>
                    </div>
                  
                </div>