<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>
    
    <xsl:template match="/">
        <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html></xsl:text>
        
        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
                <title>PWS</title>
                <style>
					
					#formContainer {
						font-size:12px;
					    width:600px;
					    height:806px;
					    margin:0 auto;
					    border: 1px solid black;
					}
					
					#titleContainer {
					    width:590px;
					    height:20px;
					    position: relative;
					    padding:5px;
					    margin-right:10px;
					    border-bottom: 1px solid black;
					}
					
					#title, 
					#mrn,
					#submitFormTop {
					    width: 600px;
					    height: 80%;
					    text-align:center;
					    position: absolute;
					    top: -15;
					    left: 0;
					}
					
					#title {
					    font-size:16px;
					}
					
					#mrn {
					    z-index: 10;
					    text-align:right;
					    margin-left:-5px;
					    font-size:16px;
					}
					
					#submitFormTop {
					    z-index: 20;
					    text-align:left;
					}
					
					#infoLeft {
						width: 368px;
						float: left;
						padding: 2px 2px 2px 5px;
						border-bottom: 1px solid black;
					}
					
					#infoRight {
						width: 221px;
						height: 60px;
						float: right;
						padding: 2px;
						border-bottom: 1px solid black;
					}
					
					#vitals {
						width: 600px;
						height: 212px;
						border-left: none;
					}
					
					#vitalsRowA {
						width: 300px;
						height: 121px;
						float: left;
						border-left: none;
					}
					
					#vitalsRowB {
						width: 300px;
						height: 121px;
						float: left;
						border-left: none;
					}
					
					.vitalsSigns {
					    float: left;
						width: 600px;
						height: 15px;
						text-align: left;
						padding-top: 4px;
						padding-bottom: 2px;
						padding-left: 6px;
					}
					
					.vitalsflagA {
						float: left;
						width: 15px;
						height: 15px;
						text-align: center;
						border-right: 1px solid black;
					}
					
					.vitalsNames {
					    float:left;
					    width:74px;
					    height: 15px;
					    text-align:right;
					}

					.vitalsValues {
					    float:left;
					    width:220px;
					    height: 15px;
					    margin-left:5px;
					    text-align:left;
					}
					
					#vitalsLegend {
					    width:600px;
					    border-left: 1px solid black;
					    border-bottom: 1px solid black;
					    border-top: 1px solid black;
					    float:left;
					    margin-left:-1px;
					    height:35px;
						text-align: center;
					    font-size:0.8em;
					    padding-top:2px;
					}
					
					.vitalsLegendRow {
						width:600px;
					}
					
					#exam {
					    border-bottom: 1px solid black;
					    float:left;
					    width:367px;
					    height:280px;
					    margin-top:-220px;
					    padding:2px 2px 0px 5px;
					}
					
					#buttons {
					    width:226px;
					    height:56px;
					    border-bottom: 1px solid black;
					    float:left;
					    margin-top:6px;
					    margin-left:0px;
					}
					
					#examTitle {
					    float:left;
					    text-align:left;
					    width:175px;
					    height:15px;
					    vertical-align: text-bottom;
					}
					
					.examNames {
					    float:left;
					    width:80px;
					    height:15px;
					    text-align:left;
					    vertical-align: text-bottom;
					}
					
					.examHeader {
					    float:left;
					    width:30px;
					    height:15px;
					    text-align:center;
					    vertical-align: text-bottom;
					}
					
					.examFlag {
					    float:left;
					    width:15px;
					    height:15px;
					    text-align:center;
					    color:red;
					    vertical-align: text-bottom;
					}
					
					#examLegend {
					    float:left;
					    text-align:center;
					    width:175px;
					    height:15px;
					    font-size:0.8em;
					}
					
					#examExtras {
						float: left;
						width: 303px;
						height: 277px; /*259px;*/
						margin-left: 20px;
					}
					
					.examExtraCheckbox {
					    width:189px;
					    height: 19px;
					}
					
					.examExtraData {
					    height: 15px;
					    width:187px;
					    padding-left:2px;
					    margin: 1px 0 1px 0;
					}
					
					.medicalPerformed {
						width: 322px;
						padding-left: 2px;
						margin: 1px 0 1px 0;
					}
					
					.questionContainer {
					    float: left;
						width: 300px;
						height: 120px;
						font-size: 0.75em;
						padding-top: 10px;
						border-bottom: 1px solid black;
						border-left: 1px solid black;
						margin-left: -1px;					   					    
					}
					
					.questionStem {
					    padding:2px 5px 2px 5px;
					    width:290px;
					    height:55px;
					}
					
					.answerContainer {
					    float:left;
					    padding:2px;
					    width:145px;
					    height:55px;
					}
					
					.answerCheckbox {
					    width:150px;
					    height:15px
					}
					
					.highlightYellow {
						background-color: yellow;
					}
					
					.infoLeftItem {
						height: 15px;
						width: 300px;
					}
					
					.infoRightItem {
						height: 15px;
						width: 160px;
						padding: 0px 0px 0px 60px;
					}
					
					#informant {
						width:124px;
						float:left;
						padding:2px 2px 2px 5px;
					}
					
					#textNotesContainer{
						float: left;					
						padding: 0 0 10px 0;					
						border-top: 1px solid black;
						border-bottom: 1px solid black;	
						border-right: 1px solid black;
						border-left: 1px solid black;					
						margin-left:-1px;															
					}
					
					#textNotesContainer div{
						padding: 10px 0 0 5px;											
					}
					
					.notesTextArea {				
					    border: 1px solid black;
					    resize: none;
					    width: 585px;					   					    
					    height: 250px;					    
					    margin-right:10px;
					    border-bottom: 1px solid black;
					}
					
					.textNotesTable {
						border-spacing: 0;
					}
					
					.textNotesTable td{
						padding: 0;
					}
										
					@media print{
						.textNotesTable{page-break-before: always;}
					}
					
                </style>
            </head>
            <body>      	
		        <div id="formContainer">
		            <form id="pwsForm" name="pwsForm" action="pws.form" method="post">            
		                <div id="titleContainer">
		                    <div id="title">
		                        <h3>CHICA Physician Encounter Form</h3>
		                    </div>
		                    <div id="mrn">
		                        <h3><xsl:value-of select="Records/Record/Field[@id = 'MRN']"/></h3>
		                    </div>
		                </div>
		                <div id="infoLeft">
		                	<div class="infoLeftItem">
		                    	<b>Patient:</b> <xsl:value-of select="Records/Record/Field[@id = 'PatientName']"/>
		                	</div>
		                	<div class="infoLeftItem">
		                		<b>DOB:</b> <xsl:value-of select="Records/Record/Field[@id = 'DOB']"/>&#160;&#160;<b>Age:</b> <xsl:value-of select="Records/Record/Field[@id = 'Age']"/>
		                	</div>
		                	<div class="infoLeftItem">
		                    	<b>Doctor:</b> <xsl:value-of select="Records/Record/Field[@id = 'Doctor']"/>
		                	</div>
							<div class="infoLeftItem">
								<b>Informant:</b>
								<xsl:value-of select="Records/Record/Field[@id = 'Informant']"/>
							</div>
		                </div>
		                <div id="infoRight">
		                	<div class="infoRightItem">
		                    	<b>MRN:</b> <xsl:value-of select="Records/Record/Field[@id = 'MRN']"/>
		                	</div>
		                	<div class="infoRightItem">
		                    	<b>Date:</b> <xsl:value-of select="Records/Record/Field[@id = 'VisitDate']"/>
		                	</div>
		                	<div class="infoRightItem">
		                		<b>Time:</b> <xsl:value-of select="Records/Record/Field[@id = 'VisitTime']"/>
		                	</div>
							<div class="infoRightItem">
								<xsl:choose>
									<xsl:when test="normalize-space(Records/Record/Field[@id = 'Language']) = ''">
										&#160;
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="Records/Record/Field[@id = 'Language']"/>
									</xsl:otherwise>
								</xsl:choose>
							</div>
		                </div>
		                <div id="vitals">
							<div class="vitalsSigns">
		                        <b>Vital Signs:</b>
		                    </div>
							<div id="vitalsRowA">
								<div class="vitalsNames">
									<xsl:if test="normalize-space(Records/Record/Field[@id = 'HeightA']) != ''">
										<b style="color:red"><xsl:value-of select="Records/Record/Field[@id = 'HeightA']"/></b>
									</xsl:if>
									Height:<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'Height']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'Height']"/>&#160;<xsl:value-of select="Records/Record/Field[@id = 'HeightSUnits']"/>&#160;(<xsl:value-of select="Records/Record/Field[@id = 'HeightP']"/>%)
										</xsl:otherwise>
									</xsl:choose>
								</div>
								<div class="vitalsNames">
									<xsl:if test="normalize-space(Records/Record/Field[@id = 'WeightA']) != ''">
										<b style="color:red"><xsl:value-of select="Records/Record/Field[@id = 'WeightA']"/></b>
									</xsl:if>
									Weight:<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										 <xsl:when test="normalize-space(Records/Record/Field[@id = 'WeightKG']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'WeightKG']"/>&#160;kg.&#160;(<xsl:value-of select="Records/Record/Field[@id = 'WeightP']"/>%)
										</xsl:otherwise>
									</xsl:choose>
								</div>
								<div class="vitalsNames">
									<xsl:if test="normalize-space(Records/Record/Field[@id = 'BMIA']) != ''">
										<b style="color:red"><xsl:value-of select="Records/Record/Field[@id = 'BMIA']"/></b>
									</xsl:if>
									BMI:<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'BMI']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'BMI']"/>&#160;(<xsl:value-of select="Records/Record/Field[@id = 'BMIP']"/>%)
										</xsl:otherwise>
									</xsl:choose>
								</div>
								<div class="vitalsNames">
									<xsl:if test="normalize-space(Records/Record/Field[@id = 'HCA']) != ''">
										<b style="color:red"><xsl:value-of select="Records/Record/Field[@id = 'HCA']"/></b>
									</xsl:if>
									Head Circ:<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'HC']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'HC']"/> cm. (<xsl:value-of select="Records/Record/Field[@id = 'HCP']"/>%)
										</xsl:otherwise>
									</xsl:choose>
								</div>
								<div class="vitalsNames">
									<xsl:if test="normalize-space(Records/Record/Field[@id = 'TempA']) != ''">
										<b style="color:red"><xsl:value-of select="Records/Record/Field[@id = 'TempA']"/></b>
									</xsl:if>
									Temp:<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'Temperature']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'Temperature']"/>&#160;(<xsl:value-of select="Records/Record/Field[@id = 'Temperature_Method']"/>)
										</xsl:otherwise>
									</xsl:choose>
								</div>
								<div class="vitalsNames">
									<xsl:if test="normalize-space(Records/Record/Field[@id = 'PulseA']) != ''">
										<b style="color:red"><xsl:value-of select="Records/Record/Field[@id = 'PulseA']"/></b>
									</xsl:if>
									Pulse:<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'Pulse']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'Pulse']"/>
										</xsl:otherwise>
									</xsl:choose>
								</div>
								<div class="vitalsNames">
									<xsl:if test="normalize-space(Records/Record/Field[@id = 'RRA']) != ''">
										<b style="color:red"><xsl:value-of select="Records/Record/Field[@id = 'RRA']"/></b>
									</xsl:if>
									RR:<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'RR']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'RR']"/>
										</xsl:otherwise>
									</xsl:choose>
								</div>
								<div class="vitalsNames">
									<xsl:if test="normalize-space(Records/Record/Field[@id = 'BPA']) != ''">
										<b style="color:red"><xsl:value-of select="Records/Record/Field[@id = 'BPA']"/></b>
									</xsl:if>
									BP:<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'BP']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'BP']"/> (<xsl:value-of select="Records/Record/Field[@id = 'BPP']"/>)
										</xsl:otherwise>
									</xsl:choose>
								</div>
							</div>
							<div id="vitalsRowB">
								<div class="vitalsNames">
									<xsl:if test="normalize-space(Records/Record/Field[@id = 'PulseOxA']) != ''">
										<b style="color:red"><xsl:value-of select="Records/Record/Field[@id = 'PulseOxA']"/></b>
									</xsl:if>
									Pulse Ox:<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'PulseOx']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'PulseOx']"/>%
										</xsl:otherwise>
									</xsl:choose>
								</div>
								<div class="vitalsNames">
									<xsl:if test="normalize-space(Records/Record/Field[@id = 'HearA']) != ''">
										<b style="color:red"><xsl:value-of select="Records/Record/Field[@id = 'HearA']"/></b>
									</xsl:if>
									Hear (L):<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'HearL']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'HearL']"/>
										</xsl:otherwise>
									</xsl:choose>
								</div>
								<div class="vitalsNames">
									<xsl:if test="normalize-space(Records/Record/Field[@id = 'HearA']) != ''">
										<b style="color:red"><xsl:value-of select="Records/Record/Field[@id = 'HearA']"/></b>
									</xsl:if>
									Hear (R):<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'HearR']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'HearR']"/>
										</xsl:otherwise>
									</xsl:choose>
								</div>
								<div class="vitalsNames">
									<xsl:if test="normalize-space(Records/Record/Field[@id = 'VisionLA']) != ''">
										<b style="color:red"><xsl:value-of select="Records/Record/Field[@id = 'VisionLA']"/></b>
									</xsl:if>
									Vision (L):<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'VisionL']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'VisionL']"/>&#160;<xsl:value-of select="Records/Record/Field[@id = 'VisionL_Corrected']"/>
										</xsl:otherwise>
									</xsl:choose>
								</div>
								<div class="vitalsNames">
									<xsl:if test="normalize-space(Records/Record/Field[@id = 'VisionRA']) != ''">
										<b style="color:red"><xsl:value-of select="Records/Record/Field[@id = 'VisionRA']"/></b>
									</xsl:if>
									Vision (R):<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'VisionR']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'VisionR']"/>&#160;<xsl:value-of select="Records/Record/Field[@id = 'VisionR_Corrected']"/>
										</xsl:otherwise>
									</xsl:choose>
								</div>
								<div class="vitalsNames">
									Weight:<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'Weight']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'Weight']"/>
										</xsl:otherwise>
									</xsl:choose>
								</div>
								<div class="vitalsNames">
									Prev WT:<br/>
								</div>
								<div class="vitalsValues">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'PrevWeight']) = ''">
											&#160;
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="Records/Record/Field[@id = 'PrevWeight']"/>&#160;(<xsl:value-of select="Records/Record/Field[@id = 'PrevWeightDate']"/>)
										</xsl:otherwise>
									</xsl:choose>
								</div>
							</div>
		                    <div id="vitalsLegend">
		                    	<div class="vitalsLegendRow"><b><font style="color:red;">*</font>=Abnormal, U=Uncorrected,</b></div>
		                    	<div class="vitalsLegendRow"><b>C=Corrected, A=Axillary, R=Rectal, O=Oral,</b></div>
		                    	<div class="vitalsLegendRow"><b>F=Failed, P=Passed</b></div>
		                    </div>
		                </div>
		                <div class="questionContainer">
		                    <xsl:choose>
		                        <xsl:when test="normalize-space(Records/Record/Field[@id = 'Prompt1_Text']) = ''">
		                            &#160;
		                        </xsl:when>
		                        <xsl:otherwise>
		                           <div class="questionStem">
		                               <xsl:value-of select="Records/Record/Field[@id = 'Prompt1_Text']"/>
		                           </div>
		                           <div class="answerContainer">
		                               <div class="answerCheckbox">
		                                   <xsl:choose>
		                                   	<xsl:when test="contains(Records/Record/Field[@id = 'Choice1']/Value, '1')">
		                                   		<input type="checkbox" name="sub_Choice1" value="1" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_1']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice1" value="1" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_1']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                               </div>
		                               <div class="answerCheckbox">
		                                   <xsl:choose>
		                                   	<xsl:when test="contains(Records/Record/Field[@id = 'Choice1']/Value, '3')">
		                                   		<input type="checkbox" name="sub_Choice1" value="3" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_3']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice1" value="3" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_3']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                               </div>
		                               <div class="answerCheckbox">
		                                   <xsl:choose>
		                                   	<xsl:when test="contains(Records/Record/Field[@id = 'Choice1']/Value, '5')">
		                                   		<input type="checkbox" name="sub_Choice1" value="5" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_5']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice1" value="5" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_5']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                               </div>
		                           </div>
		                           <div class="answerContainer">
		                               <div class="answerCheckbox">
		                                   <xsl:choose>
		                                   	<xsl:when test="contains(Records/Record/Field[@id = 'Choice1']/Value, '2')">
		                                   		<input type="checkbox" name="sub_Choice1" value="2" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_2']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice1" value="2" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_2']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                               </div>
		                               <div class="answerCheckbox">
		                                   <xsl:choose>
		                                   	<xsl:when test="contains(Records/Record/Field[@id = 'Choice1']/Value, '4')">
		                                   		<input type="checkbox" name="sub_Choice1" value="4" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_4']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice1" value="4" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_4']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                               </div>
		                               <div class="answerCheckbox">
		                                   <xsl:choose>
		                                   	<xsl:when test="contains(Records/Record/Field[@id = 'Choice1']/Value, '6')">
		                                   		<input type="checkbox" name="sub_Choice1" value="6" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_6']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice1" value="6" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_6']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                               </div>
		                           </div>
		                        </xsl:otherwise>
		                    </xsl:choose>
		                </div>
		                <div class="questionContainer">
		                    <xsl:choose>
		                        <xsl:when test="normalize-space(Records/Record/Field[@id = 'Prompt2_Text']) = ''">
		                            &#160;
		                        </xsl:when>
		                        <xsl:otherwise>
		                            <div class="questionStem">
		                            	<xsl:value-of select="Records/Record/Field[@id = 'Prompt2_Text']"/>
		                            </div>
		                            <div class="answerContainer">
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice2']/Value, '1')">
		                                    		<input type="checkbox" name="sub_Choice2" value="1" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_1']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice2" value="1" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_1']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice2']/Value, '3')">
		                                    		<input type="checkbox" name="sub_Choice2" value="3" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_3']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice2" value="3" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_3']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice2']/Value, '5')">
		                                    		<input type="checkbox" name="sub_Choice2" value="5" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_5']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice2" value="5" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_5']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                            </div>
		                            <div class="answerContainer">
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice2']/Value, '2')">
		                                    		<input type="checkbox" name="sub_Choice2" value="2" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_2']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice2" value="2" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_2']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice2']/Value, '4')">
		                                    		<input type="checkbox" name="sub_Choice2" value="4" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_4']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice2" value="4" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_4']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice2']/Value, '6')">
		                                    		<input type="checkbox" name="sub_Choice2" value="6" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_6']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice2" value="6" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_6']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                            </div>
		                        </xsl:otherwise>
		                    </xsl:choose>
		                </div>
		                <div class="questionContainer">
		                    <xsl:choose>
		                        <xsl:when test="normalize-space(Records/Record/Field[@id = 'Prompt3_Text']) = ''">
		                            &#160;
		                        </xsl:when>
		                        <xsl:otherwise>
		                            <div class="questionStem">
		                            	<xsl:value-of select="Records/Record/Field[@id = 'Prompt3_Text']"/>
		                            </div>
		                            <div class="answerContainer">
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice3']/Value, '1')">
		                                    		<input type="checkbox" name="sub_Choice3" value="1" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_1']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice3" value="1" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_1']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice3']/Value, '3')">
		                                    		<input type="checkbox" name="sub_Choice3" value="3" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_3']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice3" value="3" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_3']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice3']/Value, '5')">
		                                    		<input type="checkbox" name="sub_Choice3" value="5" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_5']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice3" value="5" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_5']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                            </div>
		                            <div class="answerContainer">
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice3']/Value, '2')">
		                                    		<input type="checkbox" name="sub_Choice3" value="2" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_2']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice3" value="2" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_2']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice3']/Value, '4')">
		                                    		<input type="checkbox" name="sub_Choice3" value="4" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_4']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice3" value="4" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_4']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice3']/Value, '6')">
		                                    		<input type="checkbox" name="sub_Choice3" value="6" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_6']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice3" value="6" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_6']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                            </div>
		                        </xsl:otherwise>
		                    </xsl:choose>
		                </div>
		                <div class="questionContainer">
		                    <xsl:choose>
		                        <xsl:when test="normalize-space(Records/Record/Field[@id = 'Prompt4_Text']) = ''">
		                            &#160;
		                        </xsl:when>
		                        <xsl:otherwise>
		                            <div class="questionStem">
		                            	<xsl:value-of select="Records/Record/Field[@id = 'Prompt4_Text']"/>
		                            </div>
		                            <div class="answerContainer">
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice4']/Value, '1')">
		                                    		<input type="checkbox" name="sub_Choice4" value="1" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_1']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice4" value="1" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_1']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice4']/Value, '3')">
		                                    		<input type="checkbox" name="sub_Choice4" value="3" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_3']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice4" value="3" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_3']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice4']/Value, '5')">
		                                    		<input type="checkbox" name="sub_Choice4" value="5" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_5']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice4" value="5" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_5']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                            </div>
		                            <div class="answerContainer">
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice4']/Value, '2')">
		                                    		<input type="checkbox" name="sub_Choice4" value="2" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_2']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice4" value="2" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_2']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice4']/Value, '4')">
		                                    		<input type="checkbox" name="sub_Choice4" value="4" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_4']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice4" value="4" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_4']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice4']/Value, '6')">
		                                    		<input type="checkbox" name="sub_Choice4" value="6" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_6']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice4" value="6" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_6']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                            </div>
		                        </xsl:otherwise>
		                    </xsl:choose>
		                </div>
		                <div class="questionContainer">
		                    <xsl:choose>
		                        <xsl:when test="normalize-space(Records/Record/Field[@id = 'Prompt5_Text']) = ''">
		                            &#160;
		                        </xsl:when>
		                        <xsl:otherwise>
		                            <div class="questionStem">
		                            	<xsl:value-of select="Records/Record/Field[@id = 'Prompt5_Text']"/>
		                            </div>
		                            <div class="answerContainer">
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice5']/Value, '1')">
		                                    		<input type="checkbox" name="sub_Choice5" value="1" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_1']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice5" value="1" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_1']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice5']/Value, '3')">
		                                    		<input type="checkbox" name="sub_Choice5" value="3" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_3']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice5" value="3" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_3']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice5']/Value, '5')">
		                                    		<input type="checkbox" name="sub_Choice5" value="5" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_5']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice5" value="5" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_5']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                            </div>
		                            <div class="answerContainer">
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice5']/Value, '2')">
		                                    		<input type="checkbox" name="sub_Choice5" value="2" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_2']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice5" value="2" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_2']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice5']/Value, '4')">
		                                    		<input type="checkbox" name="sub_Choice5" value="4" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_4']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice5" value="4" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_4']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice5']/Value, '6')">
		                                    		<input type="checkbox" name="sub_Choice5" value="6" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_6']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice5" value="6" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_6']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                            </div>
		                        </xsl:otherwise>
		                    </xsl:choose>
		                </div>
		                <div class="questionContainer">
		                    <xsl:choose>
		                        <xsl:when test="normalize-space(Records/Record/Field[@id = 'Prompt6_Text']) = ''">
		                            &#160;
		                        </xsl:when>
		                        <xsl:otherwise>
		                            <div class="questionStem">
		                            	<xsl:value-of select="Records/Record/Field[@id = 'Prompt6_Text']"/>
		                            </div>
		                            <div class="answerContainer">
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice6']/Value, '1')">
		                                    		<input type="checkbox" name="sub_Choice6" value="1" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_1']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice6" value="1" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_1']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice6']/Value, '3')">
		                                    		<input type="checkbox" name="sub_Choice6" value="3" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_3']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice6" value="3" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_3']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice6']/Value, '5')">
		                                    		<input type="checkbox" name="sub_Choice6" value="5" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_5']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice6" value="5" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_5']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                            </div>
		                            <div class="answerContainer">
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice6']/Value, '2')">
		                                    		<input type="checkbox" name="sub_Choice6" value="2" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_2']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice6" value="2" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_2']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice6']/Value, '4')">
		                                    		<input type="checkbox" name="sub_Choice6" value="4" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_4']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice6" value="4" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_4']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                                <div class="answerCheckbox">
		                                    <xsl:choose>
		                                    	<xsl:when test="contains(Records/Record/Field[@id = 'Choice6']/Value, '6')">
		                                    		<input type="checkbox" name="sub_Choice6" value="6" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_6']"/><br/>
		                                     </xsl:when>
		                                     <xsl:otherwise>
		                                     	<input type="checkbox" name="sub_Choice6" value="6" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_6']"/><br/>
		                                     </xsl:otherwise>
		                                   </xsl:choose>
		                                </div>
		                            </div>
		                        </xsl:otherwise>
		                    </xsl:choose>		                    
		                </div>
		              		                
		            </form>
		        </div>
		        
		         
		    </body>
        </html>
        
    </xsl:template>
</xsl:stylesheet>