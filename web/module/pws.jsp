<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
<html style="height:100%;" xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="./resources/pws.css" type="text/css" rel="stylesheet" />
        <title>CHICA Physician Encounter Form</title>
    </head>

    <body>
    	<div id="formContainer">
            <form name="input" action="pws.form" method="post">
                <div id="titleContainer">
                    <div id="title">
                        <h3>CHICA Physician Encounter Form</h3>
                    </div>
                    <div id="mrn">
                        <h3>${MRN}</h3>
                    </div>
                </div>
                <div id="infoLeft">
                    <b>Patient:</b> ${PatientName}<br/>
                    <b>DOB:</b> ${DOB} <b>Age:</b> ${Age}<br/>
                    <b>Doctor:</b> ${Doctor}
                </div>
                <div id="infoRight">
                    <b>MRN:</b> ${MRN}<br/>
                    <b>Date:</b> ${VisitDate}<br/>
                <b>Time:</b> ${VisitTime}</div>
                <div id="vitals">
                    <div class="flagCell">
                        <b>A</b>
                    </div>
                    <div class="vitalsNames">
                        <b>Vital Signs:</b>
                    </div>
                    <div class="vitalsValues">
                        &nbsp;&nbsp;
                    </div>
                    <div class="flagCell">
                        <b>${HeightA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Height:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${Height}&nbsp;${HeightSUnits}&nbsp;(${HeightP}%)
                    </div>
                    <div class="flagCell">
                        <b>${WeightA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Weight:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${WeightKG}&nbsp;kg.&nbsp;(${WeightP}%)
                    </div>
                    <div class="flagCell">
                        <b>${BMIA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        BMI:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${BMI}&nbsp;(${BMIP}%)
                    </div>
                    <div class="flagCell">
                        <b>${HCA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Head Circ:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${HC} cm.(${HCP}%)
                    </div>
                    <div class="flagCell">
                        <b>${TempA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Temp:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${Temperature} F (${Temperature_Method})
                    </div>
                    <div class="flagCell">
                        <b>${PulseA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Pulse:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${Pulse}
                    </div>
                    <div class="flagCell">
                        <b>${RRA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        RR:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${RR}
                    </div>
                    <div class="flagCell">
                        <b>${BPA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        BP:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${BP} (${BPP})
                    </div>
                    <div class="flagCell">
                        <b>${PulseOxA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Pulse Ox:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${PulseOx}%
                    </div>
                    <div class="flagCell">
                        <b>${HearA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Hear (L):<br/>
                    </div>
                    <div class="vitalsValues">
                        ${HearL}
                    </div>
                    <div class="flagCell">
                        <b>${HearA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Hear (R):<br/>
                    </div>
                    <div class="vitalsValues">
                        ${HearR}
                    </div>
                    <div class="flagCell">
                        <b>${VisionLA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Vision (L):<br/>
                    </div>
                    <div class="vitalsValues">
                        ${VisionL}&nbsp;${VisionL_Corrected}
                    </div>
                    <div class="flagCell">
                        <b>${VisionRA}</b><br/>
                    </div>
                    <div class="vitalsNames">
                        Vision (R):<br/>
                    </div>
                    <div class="vitalsValues">
                        ${VisionL}&nbsp;${VisionL_Corrected}
                    </div>
                    <div class="flagCell">
                        <b></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Weight:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${Weight}
                    </div>
                    <div class="flagCell">
                        <b></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Prev WT:<br/>
                    </div>
                    <div class="vitalsValues">
                        ${PrevWeight}&nbsp;(${PrevWeightDate})
                    </div>
                    <div id="vitalsLegend">
                    <b>*=Abnormal, U=Uncooperative</b></div>
                </div>
                <div id="exam">
                	<div id="physicalExam">
                    	<div id="examTitle">
                        	<b>Physical Exam:</b>
                        </div>
                        <div class="examNames">
                            &nbsp;<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	Nl<br/>
                        </div>
                        <div class="examHeader">
                        	Abnl<br/>
                        </div>
                        <div class="examNames">
                            General:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_General" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_General" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Head:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Head" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Head" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Skin:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Skin" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Skin" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Eyes:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Eyes" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Eyes" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Ears:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Ears" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Ears" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Nose/Throat:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Nose" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Nose" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Teeth/Gums:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Teeth" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Teeth" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Nodes:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Nodes" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Nodes" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Chest/Lungs:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Chest" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Chest" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Heart/Pulses:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Heart" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Heart" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Abdomen:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Abdomen" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Abdomen" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Ext Genitalia:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_ExtGenitalia" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_ExtGenitalia" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Back:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Back" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Back" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Neuro:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Neuro" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Neuro" value="A"/><br/>
                        </div>
                        <div class="examNames">
                            Extremities:<br/>
                        </div>
                        <div class="examFlag">
                            &nbsp;<br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Extremities" value="N"/><br/>
                        </div>
                        <div class="examHeader">
                        	<input type="radio" name="Entry_Extremities" value="A"/><br/>
                        </div>
                        <div id="examLegend">
                        	<b>* = Previously Abnormal</b>
                        </div>
                    </div>
                  <div id="examExtras">
                   	<div class="examExtraCheckbox">
                        	<input type="checkbox">Special Need Child</input><br/>
                        </div>
                        <div>
                        	&nbsp;
                        </div>
                        <div class="examExtraCheckbox">
                        	<input type="checkbox">Two ID's Checked</input><br/>
                        </div>
                        <div class="examExtraCheckbox">
                        	<input type="checkbox">Screened for abuse</input><br/>
                        </div>
                        <div class="examExtraCheckbox">
                        	<input type="checkbox">Discussed physical activity</input><br/>
                        </div>
                    	<div class="examExtraCheckbox">
                        	<input type="checkbox">Discussed healthy diet</input><br/>
                      	</div>
                        <div class="examExtraCheckbox">
                        	<input type="checkbox">Updated Med List given to patient</input><br/>
                      	</div>
                        <div>
                        	&nbsp;
                        </div>
                        <div class="examExtraData">
                        	Language: Spanish
                        </div>
                        <div>
                        	&nbsp;
                        </div>
                        <div class="examExtraData">
                        	Tobacco&nbsp;__
                        </div>
                        <div class="examExtraData">
                        	Alcohol&nbsp;__
                        </div>
                        <div class="examExtraData">
                        	Drugs&nbsp;__
                        </div>
                        <div>
                        	&nbsp;
                        </div>
                        <div class="examExtraDataHighlight">
                        	Pain (0-10): _______
                        </div>
                        <div class="examExtraData">
                        	Allergies: _________
                        </div>
                  </div>
                </div>
                <div id="pain">
                	
                </div>
                <div id="questionOneContainer" class="questionContainer">
                	<div id="questionOneStem" class="questionStem">
                    	* ATTENTION * According to information collected today on screening, Jenny seems to be in pain.  Please rate pain on a scale of 1-10 to below and counsel appropriately.
                    </div>
                    <div id="questionOneAnswerLeftContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">Score: 1 - 2</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Score: 5 - 6</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Score: 9 - 10</input><br/>
                        </div>
                    </div>
                    <div id="questionOneAnswerRightContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">Score: 3 - 4</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Score: 7 - 8</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Not in pain</input><br/>
                        </div>
                    </div>
                </div>
                <div id="questionTwoContainer" class="questionContainer">
                	<div id="questionTwoStem" class="questionStem">
                    	Jenny has acknowledged having had sexual intercourse. Jenny has also reported the following risk factors for pregnancy and/or STD's: patient does not always use condoms / patient does not use birth control. Please discuss the following:
                    </div>
                    <div id="questionTwoAnswerLeftContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">Recommend condoms</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Recommend birth control</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">May be pregnant -> test -></input><br/>
                        </div>
                    </div>
                    <div id="questionTwoAnswerRightContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">Gave out condoms</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Prescribed birth control</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Never had intercourse</input><br/>
                        </div>
                    </div>
                </div>
                <div id="questionThreeContainer" class="questionContainer">
                	<div id="questionThreeStem" class="questionStem">
                    	Jenny's type 2 diabetes screening labs cannot be found. Were they done? Please consider the following:
                    </div>
                    <div id="questionThreeAnswerLeftContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">Labs normal -></input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Labs not done -></input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Schedule f/u visit for 3 months</input><br/>
                        </div>
                    </div>
                    <div id="questionThreeAnswerRightContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">Recommend lifestyle changes</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Page and send to test clinic</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox"></input><br/>
                        </div>
                    </div>
                </div>
                <div id="questionFourContainer" class="questionContainer">
                	<div id="questionFourStem" class="questionStem">
                    	According to AAP guidelines, Jenny should have vision screening today, but we have no record. Please screen vision now.
                    </div>
                    <div id="questionFourAnswerLeftContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">Screen done ----></input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox"></input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Unable to screen</input><br/>
                        </div>
                    </div>
                    <div id="questionFourAnswerRightContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">Passed</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Failed</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Not indicated</input><br/>
                        </div>
                    </div>
                </div>
                <div id="questionFiveContainer" class="questionContainer">
                	<div id="questionFiveStem" class="questionStem">
                    	Although Jenny has given no indication that he is at high risk for alcohol abuse, it is worth asking about the use of alcohol.  They are at risk because a family member has substance abuse problem. . Please ask about the following, and check the boxes if appropriate.
                    </div>
                    <div id="questionFiveAnswerLeftContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">Has been drunk in last month</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Been in car with drunk driver</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">No concerns at this time</input><br/>
                        </div>
                    </div>
                    <div id="questionFiveAnswerRightContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">Risk taking while drunk</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Family drinking prob.</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox"></input><br/>
                        </div>
                    </div>
                </div>
                <div id="questionSixContainer" class="questionContainer">
                	<div id="questionSixStem" class="questionStem">
                    	Although Jenny has given no indications of high risk for drug abuse, it is worth asking about the use of drugs. Please ask about the following, and check the boxes if appropriate.
                    </div>
                    <div id="questionSixAnswerLeftContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">Has used drugs in last month</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">Friend(s) abuse(s) drugs</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox">No concerns at this time</input><br/>
                        </div>
                    </div>
                    <div id="questionSixAnswerRightContainer" class="answerContainer">
                    	<div class="answerCheckbox">
                        	<input type="checkbox">Abuses OTC drugs</input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox"></input><br/>
                        </div>
                        <div class="answerCheckbox">
                        	<input type="checkbox"></input><br/>
                        </div>
                    </div>
                </div>
            </form>
    	</div>
    </body>
</html> 