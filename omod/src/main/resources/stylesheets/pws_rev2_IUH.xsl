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

					.main_container {
						width: 700px;
						margin:0 auto;
						overflow: auto;
						background-color: #CACACA;
						font-size: 12px;
						font-family: Helvetica,Arial,sans-serif;
						border-radius: 10px 10px 0px 0px;
						margin-top: 15px;
						margin-bottom: 15px;
						padding-bottom: 1px;
						box-shadow: 10px 10px 5px #2E2E2E;
						-webkit-box-shadow: 10px 10px 5px #2E2E2E;
						-moz-box-shadow: 10px 10px 5px #2E2E2E;
					}
					.main_header {
						width: 100%;
						height: 30px;
						background-color: #555555;
						border-bottom: 1px solid black;
						display: block;
						border-radius: 10px 10px 0px 0px;
					}
					.main_header_title {
						width: 53%;
						padding-top: 5px;
						font-size: 20px;
						float: right;
						height: 17px;
						text-align: left;
						color: #FFFFFF;
						padding-right: 10px;
					}
					.mrn_container {
						width: 42%;
						padding-left: 10px;
						font-size: 20px;
						float: left;
						text-align: left;
						margin-top: 5px;
						color: #FFFFFF;
					}
					.patient_information_container {
						width: 696px;
						background-color: #CACACA;
						float: left;
						padding-top: 2px;
						padding: 2px; 
						height: 165px;
					}
					.header_bar {
						height: 16px;
						background-color: #555555;
						border-bottom: 1px solid black;
						display: block;
						font-size: 14px;
						padding-left: 12px;
						color: #FFFFFF;
						border-radius: 10px 10px 0px 0px;
					}
					.fixed_height_section {
						background-color: #F5FBEF;
						padding-top: 10px;
						padding-bottom: 5px;
						overflow: auto;
						width: 100%;
						    height: 86px;
						border-radius: 0px 0px 10px 10px;
					}
					.patient_name_header {
						color: black;
						text-align: left;
						margin-top: 0px;
						margin-right: 0px;
						margin-bottom: 0px;
						margin-left: 0px;
						padding-left: 5px;
						font-weight: bold;
						font-size: 15px;
					}
					.vitals_container {
						color: black;
						float: left;
						margin: auto;
						font-size: 12px;
						width: 33%;
					}
					.patient_info_container {
						color: black;
						float: left;
						margin: auto;
						font-size: 12px;
						width: 50%;
						height: 68px;
					}
					.psf_container {
						margin-left: auto;
						margin-right: auto;
						overflow: auto;
						background-color: #CACACA;
						float: left;
						padding: 2px;
						width: 696px;
					}
					.psf_results {
						background-color: #F5FBEF;
						padding-bottom: 5px;
						overflow: auto;
						width: 100%;
						min-height: 0px;
						border-radius: 0px 0px 10px 10px;
					}
					.vitals_legend {
						margin: auto;
						color: black;
						width: 100%;
						float: left;
						text-align: center;
						padding-top: 3px;
					}
					.medication_education_container {
						margin: auto;
						color: black;
						width: 100%;
						float: left;
						text-align: center;
					}
					.psf_results_container {
						padding-left: 5px;
						padding-right: 5px;
						max-height: 150px;
					}
					.psf_results_container pre {
						font-family: Helvetica,Arial,sans-serif;
						font-size: 12px;
					}
					.pws_prompts {
						background-color: #F5FBEF;
						width: 100%;
						min-height: 0px;
						font-size: 12px;
						border-radius: 0px 0px 10px 10px;
					}
					.pws_prompts_table {
						border-spacing: 0px;
						border-top: 1px solid #000000;
						padding: 0 21px 0 0;
					}
					.pws_prompt_text {
						padding-left: 5px;
						padding-right: 5px;
						padding-bottom: 2px;
						padding-top: 2px;
					}
					.left_border {
						border-left: 1px solid black;
					}
					.pws_leaves_table, .pws_leaves_table_padded {
						border-spacing: 0px;
					}
					.pws_leaves_table_padded {
						padding-bottom: 10px;
					}
					.pws_leaf_cell {
						padding: 0px;
						border-spacing: 0px;
					}
                </style>
            </head>
            <body>      	
		        <div class="main_container">
		            <form id="pwsForm" name="pwsForm" action="pws.form" method="post">            
		                <div id="" class="main_header">
							<div class="mrn_container">
		                        <b><xsl:value-of select="Records/Record/Field[@id = 'MRN']"/></b>
		                    </div>
		                    <div class="main_header_title">
		                        <b>CHICA</b>
		                    </div>
		                </div>
						<div class="patient_information_container" id="patient_container" style="height: 117px;">
							<div class="header_bar">
		                        <b>Patient</b>
		                    </div>
							<div class="fixed_height_section" id="patient_name">
								<h3 class="patient_name_header"><xsl:value-of select="Records/Record/Field[@id = 'PatientName']"/></h3>
								<div class="patient_info_container">
									<table width="300" border="0">
										<tbody>
											<tr>
												<td align="right">MRN:</td>
												<td align="left"><xsl:value-of select="Records/Record/Field[@id = 'MRN']"/></td>
											</tr>
											<tr>
												<td align="right">DOB:</td>
												<td align="left"><xsl:value-of select="Records/Record/Field[@id = 'DOB']"/></td>
											</tr>
											<tr>
												<td align="right">Age:</td>
												<td align="left"><xsl:value-of select="Records/Record/Field[@id = 'Age']"/></td>
											</tr>
											<tr>
												<td align="right">Provider:</td>
												<td align="left"><xsl:value-of select="Records/Record/Field[@id = 'Doctor']"/></td>
											</tr>
										</tbody>
									</table>
								</div>
								<div class="patient_info_container">
									<table width="300" border="0">
										<tbody>
											<tr>
												<td align="right">Date:</td>
												<td align="left"><xsl:value-of select="Records/Record/Field[@id = 'VisitDate']"/></td>
											</tr>
											<tr>
												<td align="right">Time:</td>
												<td align="left"><xsl:value-of select="Records/Record/Field[@id = 'VisitTime']"/></td>
											</tr>
											<tr>
												<td align="right">Informant:</td>
												<td align="left"><xsl:value-of select="Records/Record/Field[@id = 'Informant']"/></td>
											</tr>
											<tr>
												<td align="right">Language:</td>
												<td align="left"><xsl:value-of select="Records/Record/Field[@id = 'Language']"/></td>
											</tr>
										</tbody>
									</table>
								</div>
							</div>
						</div>
						<div class="psf_container">
							<div id="vitals_header" class="header_bar">
								<b>Vitals</b>
							</div>
							<div class="psf_results" id="vitals">
								<div class="vitals_container">
									<table width="225" border="0">
										<tbody>
											<tr>
												<td align="right" width="75">
													<font color="red"> 
														<xsl:choose>
														<xsl:when test="normalize-space(Records/Record/Field[@id = 'HeightA']) != ''">
															<b><xsl:value-of select="Records/Record/Field[@id = 'HeightA']"/></b>
														</xsl:when>
														<xsl:otherwise>
															&#160;
														</xsl:otherwise>
														</xsl:choose>
													</font>Height:
												</td>
												<td align="left" width="155">
													<xsl:choose>
													<xsl:when test="normalize-space(Records/Record/Field[@id = 'Height']) = ''">
														&#160;
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="Records/Record/Field[@id = 'Height']"/>&#160;<xsl:value-of select="Records/Record/Field[@id = 'HeightSUnits']"/>&#160;(<xsl:value-of select="Records/Record/Field[@id = 'HeightP']"/>%)
													</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
											<tr>
												<td align="right" width="75">
													<font color="red">
														<xsl:choose>
															<xsl:when test="normalize-space(Records/Record/Field[@id = 'WeightA']) != ''">
																<b><xsl:value-of select="Records/Record/Field[@id = 'WeightA']"/></b>
															</xsl:when>
															<xsl:otherwise>
																&#160;
															</xsl:otherwise>
														</xsl:choose>
													</font>Weight:
												</td>
												<td align="left" width="155">
													<xsl:choose>
														 <xsl:when test="normalize-space(Records/Record/Field[@id = 'WeightKG']) = ''">
															&#160;
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="Records/Record/Field[@id = 'WeightKG']"/>&#160;kg.&#160;(<xsl:value-of select="Records/Record/Field[@id = 'WeightP']"/>%)
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
											<tr>
												<td align="right" width="75">Weight:</td>
												<td align="left" width="155">
													<xsl:choose>
														<xsl:when test="normalize-space(Records/Record/Field[@id = 'Weight']) = ''">
															&#160;
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="Records/Record/Field[@id = 'Weight']"/>
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
											<tr>
												<td align="right" width="75">Prev Weight:</td>
												<td align="left" width="155">
													<xsl:choose>
														<xsl:when test="normalize-space(Records/Record/Field[@id = 'PrevWeight']) = ''">
															&#160;
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="Records/Record/Field[@id = 'PrevWeight']"/>&#160;(<xsl:value-of select="Records/Record/Field[@id = 'PrevWeightDate']"/>)
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
											<tr>
												<td align="right" width="75">
													<font color="red">
														<xsl:choose>
															<xsl:when test="normalize-space(Records/Record/Field[@id = 'HearA']) != ''">
																<b><xsl:value-of select="Records/Record/Field[@id = 'HearA']"/></b>
															</xsl:when>
															<xsl:otherwise>
																&#160;
															</xsl:otherwise>
														</xsl:choose>
													</font>Hear (L):
												</td>
												<td align="left" width="155">
													<xsl:choose>
														<xsl:when test="normalize-space(Records/Record/Field[@id = 'HearL']) = ''">
															&#160;
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="Records/Record/Field[@id = 'HearL']"/>
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
										</tbody>
									</table>
								</div>
								<div class="vitals_container">
									<table width="225" border="0">
										<tbody>
											<tr>
												<td align="right" width="75">
													<font color="red">
														<xsl:choose>
															<xsl:when test="normalize-space(Records/Record/Field[@id = 'BMIA']) != ''">
																<b><xsl:value-of select="Records/Record/Field[@id = 'BMIA']"/></b>
															</xsl:when>
															<xsl:otherwise>
																&#160;
															</xsl:otherwise>
														</xsl:choose>
													</font>BMI:
												</td>
												<td align="left" width="150">
													<xsl:choose>
														<xsl:when test="normalize-space(Records/Record/Field[@id = 'BMI']) = ''">
															&#160;
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="Records/Record/Field[@id = 'BMI']"/>&#160;(<xsl:value-of select="Records/Record/Field[@id = 'BMIP']"/>%)
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
											<tr>
												<td align="right" width="75">
													<font color="red">
														<xsl:choose>
															<xsl:when test="normalize-space(Records/Record/Field[@id = 'HCA']) != ''">
																<b><xsl:value-of select="Records/Record/Field[@id = 'HCA']"/></b>
															</xsl:when>
															<xsl:otherwise>
																&#160;
															</xsl:otherwise>
														</xsl:choose>
													</font>Head Circ:
												</td>
												<td align="left" width="150">
													<xsl:choose>
														<xsl:when test="normalize-space(Records/Record/Field[@id = 'HC']) = ''">
															&#160;
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="Records/Record/Field[@id = 'HC']"/> cm. (<xsl:value-of select="Records/Record/Field[@id = 'HCP']"/>%)
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
											<tr>
												<td align="right" width="75">
													<font color="red">
														<xsl:choose>
															<xsl:when test="normalize-space(Records/Record/Field[@id = 'TempA']) != ''">
																<b><xsl:value-of select="Records/Record/Field[@id = 'TempA']"/></b>
															</xsl:when>
															<xsl:otherwise>
																&#160;
															</xsl:otherwise>
														</xsl:choose>
													</font>Temp:
												</td>
												<td align="left" width="150">
													<xsl:choose>
														<xsl:when test="normalize-space(Records/Record/Field[@id = 'Temperature']) = ''">
															&#160;
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="Records/Record/Field[@id = 'Temperature']"/>&#160;(<xsl:value-of select="Records/Record/Field[@id = 'Temperature_Method']"/>)
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
											<tr>
												<td align="right" width="75">
													<font color="red">
														<xsl:choose>
															<xsl:when test="normalize-space(Records/Record/Field[@id = 'PulseA']) != ''">
																<b><xsl:value-of select="Records/Record/Field[@id = 'PulseA']"/></b>
															</xsl:when>
															<xsl:otherwise>
																&#160;
															</xsl:otherwise>
														</xsl:choose>
													</font>Pulse:
												</td>
												<td align="left" width="150">
													<xsl:choose>
														<xsl:when test="normalize-space(Records/Record/Field[@id = 'Pulse']) = ''">
															&#160;
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="Records/Record/Field[@id = 'Pulse']"/>
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
											<tr>
												<td align="right" width="75">
													<font color="red">
														<xsl:choose>
															<xsl:when test="normalize-space(Records/Record/Field[@id = 'HearA']) != ''">
																<b><xsl:value-of select="Records/Record/Field[@id = 'HearA']"/></b>
															</xsl:when>
															<xsl:otherwise>
																&#160;
															</xsl:otherwise>
														</xsl:choose>
													</font>Hear (R):
												</td>
												<td align="left" width="150">
													<xsl:choose>
														<xsl:when test="normalize-space(Records/Record/Field[@id = 'HearR']) = ''">
															&#160;
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="Records/Record/Field[@id = 'HearR']"/>
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
										</tbody>
									</table>
								</div>
								<div class="vitals_container">
									<table width="225" border="0">
										<tbody>
											<tr>
												<td align="right" width="75">
													<font color="red">
														<xsl:choose>
															<xsl:when test="normalize-space(Records/Record/Field[@id = 'RRA']) != ''">
																<b><xsl:value-of select="Records/Record/Field[@id = 'RRA']"/></b>
															</xsl:when>
															<xsl:otherwise>
																&#160;
															</xsl:otherwise>
														</xsl:choose>
													</font>RR:
												</td>
												<td align="left" width="150">
													<xsl:choose>
														<xsl:when test="normalize-space(Records/Record/Field[@id = 'RR']) = ''">
															&#160;
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="Records/Record/Field[@id = 'RR']"/>
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
											<tr>
												<td align="right" width="75">
													<font color="red">
														<xsl:choose>
															<xsl:when test="normalize-space(Records/Record/Field[@id = 'BPA']) != ''">
																<b><xsl:value-of select="Records/Record/Field[@id = 'BPA']"/></b>
															</xsl:when>
															<xsl:otherwise>
																&#160;
															</xsl:otherwise>
														</xsl:choose>
													</font>BP:
												</td>
												<td align="left" width="150">
													<xsl:choose>
														<xsl:when test="normalize-space(Records/Record/Field[@id = 'BP']) = ''">
															&#160;
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="Records/Record/Field[@id = 'BP']"/> (<xsl:value-of select="Records/Record/Field[@id = 'BPP']"/>)
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
											<tr>
												<td align="right" width="75">
													<font color="red">
														<xsl:choose>
															<xsl:when test="normalize-space(Records/Record/Field[@id = 'PulseOxA']) != ''">
																<b><xsl:value-of select="Records/Record/Field[@id = 'PulseOxA']"/></b>
															</xsl:when>
															<xsl:otherwise>
																&#160;
															</xsl:otherwise>
														</xsl:choose>
													</font>Pulse Ox:
												</td>
												<td align="left" width="150">
													<xsl:choose>
														<xsl:when test="normalize-space(Records/Record/Field[@id = 'PulseOx']) = ''">
															&#160;
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="Records/Record/Field[@id = 'PulseOx']"/>%
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
											<tr>
												<td align="right" width="75">
													<font color="red">
														<xsl:choose>
															<xsl:when test="normalize-space(Records/Record/Field[@id = 'VisionLA']) != ''">
																<b><xsl:value-of select="Records/Record/Field[@id = 'VisionLA']"/></b>
															</xsl:when>
															<xsl:otherwise>
																&#160;
															</xsl:otherwise>
														</xsl:choose>
													</font>Vision (L):
												</td>
												<td align="left" width="150">
													<xsl:choose>
														<xsl:when test="normalize-space(Records/Record/Field[@id = 'VisionL']) = ''">
															&#160;
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="Records/Record/Field[@id = 'VisionL']"/>&#160;<xsl:value-of select="Records/Record/Field[@id = 'VisionL_Corrected']"/>
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
											<tr>
												<td align="right" width="75">
													<font color="red">
														<xsl:choose>
															<xsl:when test="normalize-space(Records/Record/Field[@id = 'VisionRA']) != ''">
																<b><xsl:value-of select="Records/Record/Field[@id = 'VisionRA']"/></b>
															</xsl:when>
															<xsl:otherwise>
																&#160;
															</xsl:otherwise>
														</xsl:choose>
													</font>Vision (R):
												</td>
												<td align="left" width="150">
													<xsl:choose>
														<xsl:when test="normalize-space(Records/Record/Field[@id = 'VisionR']) = ''">
															&#160;
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="Records/Record/Field[@id = 'VisionR']"/>&#160;<xsl:value-of select="Records/Record/Field[@id = 'VisionR_Corrected']"/>
														</xsl:otherwise>
													</xsl:choose>
												</td>
											</tr>
										</tbody>
									</table>
								</div>
								<div class="vitals_legend">
									<span id="vitalsLegendIUHCerner" class="vitals_lengend_span"><font color="red">*</font>=Abnormal, U=Uncorrected, C=Corrected, A=Axillary, R=Rectal, O=Oral, F=Failed, P=Passed</span>
								</div>
							</div>
						</div>
						<div class="psf_container">
							<div id="psf_results_header" class="header_bar">
								<b>Pre-screening Results</b>
							</div>
							<div class="psf_results" id="psf_results">
								<div class="psf_results_container">
									<xsl:choose>
										<xsl:when test="normalize-space(Records/Record/Field[@id = 'psfResults']) = ''">
											<pre>Pre-screening questions have not been answered.</pre> 
										</xsl:when>
										<xsl:otherwise>
											<pre><xsl:value-of select="Records/Record/Field[@id = 'psfResults']"/></pre>
										</xsl:otherwise>
									</xsl:choose>
								</div>
							</div>
						</div>
						<div class="psf_container">
							<div id="pws_prompts_header" class="header_bar">
								<b>Physician Prompts</b>
							</div>
							<div class="pws_prompts" id="pws_prompts">
								<table width="696" border="0" class="pws_prompts_table">
									<tbody>
										<tr>
											<xsl:choose>
												<xsl:when test="normalize-space(Records/Record/Field[@id = 'Prompt1_Text']) = ''">
													<td width="320" valign="top" class="pws_prompt_text">&#160;</td>
												</xsl:when>
												<xsl:otherwise>
													<td width="320" valign="top" class="pws_prompt_text">
														<xsl:value-of select="Records/Record/Field[@id = 'Prompt1_Text']"/>
													</td>
												</xsl:otherwise>
											</xsl:choose>
											<xsl:choose>
												<xsl:when test="normalize-space(Records/Record/Field[@id = 'Prompt2_Text']) = ''">
													<td width="320" valign="top" class="pws_prompt_text left_border">&#160;</td>
												</xsl:when>
												<xsl:otherwise>
													<td width="320" valign="top" class="pws_prompt_text left_border">
														<xsl:value-of select="Records/Record/Field[@id = 'Prompt2_Text']"/>
													</td>
												</xsl:otherwise>
											</xsl:choose>
										</tr>
										<tr>
											<td valign="top">
												<table width="320" class="pws_leaves_table">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice1']/Value, '1')">
																		<input type="checkbox" name="sub_Choice1" value="1" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_1']"/>

																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice1" value="1" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_1']"/>

																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice1']/Value, '2')">
																		<input type="checkbox" name="sub_Choice1" value="2" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_2']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice1" value="2" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_2']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
											<td valign="top" class="left_border">
												<table width="320" class="pws_leaves_table">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice2']/Value, '1')">
																		<input type="checkbox" name="sub_Choice2" value="1" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_1']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice2" value="1" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_1']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice2']/Value, '2')">
																		<input type="checkbox" name="sub_Choice2" value="2" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_2']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice2" value="2" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_2']"/>
																	</xsl:otherwise>
															   </xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
										<tr>
											<td valign="top">
												<table width="320" class="pws_leaves_table">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice1']/Value, '3')">
																		<input type="checkbox" name="sub_Choice1" value="3" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_3']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice1" value="3" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_3']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice1']/Value, '4')">
																		<input type="checkbox" name="sub_Choice1" value="4" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_4']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice1" value="4" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_4']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
											<td valign="top" class="left_border">
												<table width="320" class="pws_leaves_table">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice2']/Value, '3')">
																		<input type="checkbox" name="sub_Choice2" value="3" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_3']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice2" value="3" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_3']"/>
																	</xsl:otherwise>
															   </xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice2']/Value, '4')">
																		<input type="checkbox" name="sub_Choice2" value="4" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_4']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice2" value="4" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_4']"/>
																	</xsl:otherwise>
															   </xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
										<tr>
											<td valign="top">
												<table width="320" class="pws_leaves_table_padded">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice1']/Value, '5')">
																		<input type="checkbox" name="sub_Choice1" value="5" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_5']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice1" value="5" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_5']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice1']/Value, '6')">
																		<input type="checkbox" name="sub_Choice1" value="6" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_6']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice1" value="6" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer1_6']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
											<td valign="top" class="left_border">
												<table width="320" class="pws_leaves_table_padded">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice2']/Value, '5')">
																		<input type="checkbox" name="sub_Choice2" value="5" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_5']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice2" value="5" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_5']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice2']/Value, '6')">
																		<input type="checkbox" name="sub_Choice2" value="6" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_6']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice2" value="6" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer2_6']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
									</tbody>
								</table>
								<table width="696" border="0" class="pws_prompts_table">
									<tbody>
										<tr>
											<xsl:choose>
												<xsl:when test="normalize-space(Records/Record/Field[@id = 'Prompt3_Text']) = ''">
													<td width="320" valign="top" class="pws_prompt_text">&#160;</td>
												</xsl:when>
												<xsl:otherwise>
													<td width="320" valign="top" class="pws_prompt_text">
														<xsl:value-of select="Records/Record/Field[@id = 'Prompt3_Text']"/>
													</td>
												</xsl:otherwise>
											</xsl:choose>
											<xsl:choose>
												<xsl:when test="normalize-space(Records/Record/Field[@id = 'Prompt4_Text']) = ''">
													<td width="320" valign="top" class="pws_prompt_text left_border">&#160;</td>
												</xsl:when>
												<xsl:otherwise>
													<td width="320" valign="top" class="pws_prompt_text left_border">
														<xsl:value-of select="Records/Record/Field[@id = 'Prompt4_Text']"/>
													</td>
												</xsl:otherwise>
											</xsl:choose>
										</tr>
										<tr>
											<td valign="top">
												<table width="320" class="pws_leaves_table">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice3']/Value, '1')">
																		<input type="checkbox" name="sub_Choice3" value="1" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_1']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice3" value="1" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_1']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice3']/Value, '2')">
																		<input type="checkbox" name="sub_Choice3" value="2" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_2']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice3" value="2" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_2']"/>
																	</xsl:otherwise>
															   </xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
											<td valign="top" class="left_border">
												<table width="320" class="pws_leaves_table">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice4']/Value, '1')">
																		<input type="checkbox" name="sub_Choice4" value="1" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_1']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice4" value="1" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_1']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice4']/Value, '2')">
																		<input type="checkbox" name="sub_Choice4" value="2" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_2']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice4" value="2" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_2']"/>
																	</xsl:otherwise>
															   </xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
										<tr>
											<td valign="top">
												<table width="320" class="pws_leaves_table">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice3']/Value, '3')">
																		<input type="checkbox" name="sub_Choice3" value="3" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_3']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice3" value="3" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_3']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice3']/Value, '4')">
																		<input type="checkbox" name="sub_Choice3" value="4" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_4']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice3" value="4" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_4']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
											<td valign="top" class="left_border">
												<table width="320" class="pws_leaves_table">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice4']/Value, '3')">
																		<input type="checkbox" name="sub_Choice4" value="3" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_3']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice4" value="3" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_3']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice4']/Value, '4')">
																		<input type="checkbox" name="sub_Choice4" value="4" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_4']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice4" value="4" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_4']"/>
																	</xsl:otherwise>
															   </xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
										<tr>
											<td valign="top">
												<table width="320" class="pws_leaves_table_padded">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice3']/Value, '5')">
																		<input type="checkbox" name="sub_Choice3" value="5" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_5']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice3" value="5" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_5']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice3']/Value, '6')">
																		<input type="checkbox" name="sub_Choice3" value="6" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_6']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice3" value="6" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer3_6']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
											<td valign="top" class="left_border">
												<table width="320" class="pws_leaves_table_padded">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice4']/Value, '5')">
																		<input type="checkbox" name="sub_Choice4" value="5" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_5']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice4" value="5" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_5']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice4']/Value, '6')">
																		<input type="checkbox" name="sub_Choice4" value="6" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_6']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice4" value="6" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer4_6']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
									</tbody>
								</table>
								<table width="696" border="0" class="pws_prompts_table">
									<tbody>
										<tr>
											<xsl:choose>
												<xsl:when test="normalize-space(Records/Record/Field[@id = 'Prompt5_Text']) = ''">
													<td width="320" valign="top" class="pws_prompt_text">&#160;</td>
												</xsl:when>
												<xsl:otherwise>
													<td width="320" valign="top" class="pws_prompt_text">
														<xsl:value-of select="Records/Record/Field[@id = 'Prompt5_Text']"/>
													</td>
												</xsl:otherwise>
											</xsl:choose>
											<xsl:choose>
												<xsl:when test="normalize-space(Records/Record/Field[@id = 'Prompt6_Text']) = ''">
													<td width="320" valign="top" class="pws_prompt_text left_border">&#160;</td>
												</xsl:when>
												<xsl:otherwise>
													<td width="320" valign="top" class="pws_prompt_text left_border">
														<xsl:value-of select="Records/Record/Field[@id = 'Prompt6_Text']"/>
													</td>
												</xsl:otherwise>
											</xsl:choose>
										</tr>
										<tr>
											<td valign="top">
												<table width="320" class="pws_leaves_table">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice5']/Value, '1')">
																		<input type="checkbox" name="sub_Choice5" value="1" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_1']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice5" value="1" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_1']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice5']/Value, '2')">
																		<input type="checkbox" name="sub_Choice5" value="2" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_2']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice5" value="2" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_2']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
											<td valign="top" class="left_border">
												<table width="320" class="pws_leaves_table">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice6']/Value, '1')">
																		<input type="checkbox" name="sub_Choice6" value="1" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_1']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice6" value="1" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_1']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice6']/Value, '2')">
																		<input type="checkbox" name="sub_Choice6" value="2" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_2']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice6" value="2" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_2']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
										<tr>
											<td valign="top">
												<table width="320" class="pws_leaves_table">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice5']/Value, '3')">
																		<input type="checkbox" name="sub_Choice5" value="3" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_3']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice5" value="3" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_3']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice5']/Value, '4')">
																		<input type="checkbox" name="sub_Choice5" value="4" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_4']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice5" value="4" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_4']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
											<td valign="top" class="left_border">
												<table width="320" class="pws_leaves_table">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice6']/Value, '3')">
																		<input type="checkbox" name="sub_Choice6" value="3" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_3']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice6" value="3" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_3']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice6']/Value, '4')">
																		<input type="checkbox" name="sub_Choice6" value="4" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_4']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice6" value="4" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_4']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
										<tr>
											<td valign="top">
												<table width="320" class="pws_leaves_table_padded">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice5']/Value, '5')">
																		<input type="checkbox" name="sub_Choice5" value="5" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_5']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice5" value="5" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_5']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice5']/Value, '6')">
																		<input type="checkbox" name="sub_Choice5" value="6" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_6']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice5" value="6" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer5_6']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
											<td valign="top" class="left_border">
												<table width="320" class="pws_leaves_table_padded">
													<tbody>
														<tr>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice6']/Value, '5')">
																		<input type="checkbox" name="sub_Choice6" value="5" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_5']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice6" value="5" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_5']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
															<td width="160" valign="top" class="pws_leaf_cell">
																<xsl:choose>
																	<xsl:when test="contains(Records/Record/Field[@id = 'Choice6']/Value, '6')">
																		<input type="checkbox" name="sub_Choice6" value="6" disabled="disabled" checked="checked"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_6']"/>
																	</xsl:when>
																	<xsl:otherwise>
																		<input type="checkbox" name="sub_Choice6" value="6" disabled="disabled"/><xsl:value-of select="Records/Record/Field[@id = 'Answer6_6']"/>
																	</xsl:otherwise>
																</xsl:choose>
															</td>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>
  		            </form>
		        </div>
		    </body>
        </html>
    </xsl:template>
</xsl:stylesheet>