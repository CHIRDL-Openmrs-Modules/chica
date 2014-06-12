<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>

    <xsl:template match="/">
        <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html></xsl:text>
        <html>
            <head>
                <title/>
            </head>
            <body>
                <table align="center" cellpadding="1" cellspacing="1"
                    style="BORDER: black solid 1px; page-break-after: always;">
                    <tbody>
                        <tr style="">
                            <td>
                                <table border="0" cellpadding="1" cellspacing="1">
                                    <tbody>
                                        <tr>
                                            <td colspan="1" style="text-align: center;">
                                                <span style="font-size:22px;">
                                                  <strong>CHICA Pre-Screening</strong>
                                                </span>
                                            </td>
                                            <td rowspan="2">
                                                <table align="right" border="0" cellpadding="0"
                                                  cellspacing="0">
                                                  <tbody>
                                                  <tr>
                                                  <td>
                                                  <table align="right" border="0" cellpadding="1"
                                                  cellspacing="1">
                                                  <tbody>
                                                  <tr>
                                                  <td
                                                  style="text-align: right; BORDER-LEFT: black solid 1px;">
                                                  <strong>MRN:</strong>
                                                  </td>
                                                  <td colspan="2" style="white-space: nowrap;">
                                                  <xsl:variable name="mrn"
                                                  select="Records/Record/Field[@id = 'MRN']"/>
                                                  <strong>
                                                  <xsl:value-of select="$mrn/Value"/>
                                                  </strong>
                                                  </td>
                                                  </tr>
                                                  <tr>
                                                  <td
                                                  style="text-align: right; BORDER-LEFT: black solid 1px;">
                                                  <strong>Name:</strong>
                                                  </td>
                                                  <td colspan="2" style="white-space: nowrap;">
                                                  <xsl:variable name="name"
                                                  select="Records/Record/Field[@id = 'PatientName']"/>
                                                  <strong>
                                                  <xsl:value-of select="$name/Value"/>
                                                  </strong>
                                                  </td>
                                                  </tr>
                                                  <tr>
                                                  <td
                                                  style="text-align: right; BORDER-LEFT: black solid 1px;">
                                                  <strong>Age:</strong>
                                                  </td>
                                                  <td style="white-space: nowrap;">
                                                  <xsl:variable name="age"
                                                  select="Records/Record/Field[@id = 'Age']"/>
                                                  <strong>
                                                  <xsl:value-of select="$age/Value"/>
                                                  </strong>
                                                  </td>
                                                  <td style="white-space: nowrap;">
                                                  <strong>DOB: <xsl:variable name="dob"
                                                  select="Records/Record/Field[@id = 'DOB']"/>
                                                  <xsl:value-of select="$dob/Value"/></strong>
                                                  </td>
                                                  </tr>
                                                  <tr>
                                                  <td
                                                  style="text-align: right; BORDER-LEFT: black solid 1px;  BORDER-BOTTOM: black solid 1px;">
                                                  <strong>Date:</strong>
                                                  </td>
                                                  <td colspan="2"
                                                  style="white-space: nowrap;BORDER-BOTTOM: black solid 1px;">
                                                  <xsl:variable name="scheduledTime"
                                                  select="Records/Record/Field[@id = 'ScheduledTime']"/>
                                                  <strong>
                                                  <xsl:value-of select="$scheduledTime/Value"/>
                                                  </strong>
                                                  </td>
                                                  </tr>
                                                      <tr>
                                                          <td colspan="2" style="text-align: right;"> Pulse Ox:</td>
                                                          <td><xsl:variable name="pulseOx"
                                                              select="Records/Record/Field[@id = 'PulseOx']"/>
                                                              <xsl:value-of select="$pulseOx/Value"/>%</td>
                                                      </tr>
                                                      <tr>
                                                          <td style="text-align: right;">
                                                              <xsl:choose>
                                                                  <xsl:when
                                                                      test="Records/Record/Field[@id = 'SickVisit']/Value = 'Y'">
                                                                      <input name="sickVisit" type="checkbox"
                                                                          value="sickVisit" checked="checked"
                                                                          onclick="return false" onkeydown="return false"/>
                                                                  </xsl:when>
                                                                  <xsl:otherwise>
                                                                      <input name="sickVisit" type="checkbox"
                                                                          value="sickVisit" onclick="return false"
                                                                          onkeydown="return false"/>
                                                                  </xsl:otherwise>
                                                              </xsl:choose>
                                                          </td>
                                                          <td colspan="2" style="white-space: nowrap;">Sick Visit</td>
                                                      </tr>
                                                      <tr>
                                                          <td style="text-align: right;">
                                                              <input name="patientLeft" type="checkbox"
                                                                  value="patientLeft" onclick="return false"
                                                                  onkeydown="return false"/>
                                                          </td>
                                                          <td colspan="2" style="white-space: nowrap;">Patient left
                                                              without treatment</td>
                                                      </tr>
                                                      <tr>
                                                          <td style="text-align: right;">
                                                              <input name="patientRefused" type="checkbox"
                                                                  value="patientRefused" onclick="return false"
                                                                  onkeydown="return false"/>
                                                          </td>
                                                          <td colspan="2" style="white-space: nowrap;">Patient refused
                                                              to complete form</td>
                                                      </tr>
                                                      <tr>
                                                          <td style="text-align: right;">
                                                              <input name="twoIdsChecked" type="checkbox"
                                                                  value="twoIdsChecked" onclick="return false"
                                                                  onkeydown="return false"/>
                                                          </td>
                                                          <td colspan="2" style="white-space: nowrap;">Two IDs
                                                              checked</td>
                                                      </tr>
                                                  </tbody>
                                                  </table>
                                                      <br/>
                                                  </td>
                                                  </tr>
                                                  <tr>
                                                  <td> </td>
                                                  </tr>
                                                  <tr>
                                                  <td> </td>
                                                  </tr>
                                                  </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <table border="0" cellpadding="1" cellspacing="1">
                                                  <tbody>
                                                  <tr>
                                                  <td style="text-align: right;"> Height:</td>
                                                  <td align="right">
                                                  <xsl:variable name="height"
                                                  select="Records/Record/Field[@id = 'HeightP']"/>
                                                  <xsl:value-of select="$height/Value"/>
                                                  </td>
                                                  <td>.</td>
                                                  <td style="white-space: nowrap;"><xsl:variable
                                                  name="height"
                                                  select="Records/Record/Field[@id = 'HeightS']"/>
                                                  <xsl:value-of select="$height/Value"
                                                  />&#160;<xsl:variable name="heightUnits"
                                                  select="Records/Record/Field[@id = 'HeightSUnits']"/>
                                                  <xsl:value-of select="$heightUnits/Value"/></td>
                                                  <td>
                                                  <table border="0" cellpadding="0" cellspacing="0">
                                                  <tbody>
                                                  <tr>
                                                  <td colspan="3" style="text-align: center;">
                                                  Uncooperative/Unable to screen:</td>
                                                  </tr>
                                                  <tr>
                                                  <td>
                                                  <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'NoVision']/Value = 'Y'"
                                                  ><input name="Vision" type="checkbox"
                                                  value="Vision" checked="checked"
                                                  onclick="return false" onkeydown="return false"
                                                  /></xsl:when>
                                                  <xsl:otherwise><input name="Vision"
                                                  type="checkbox" value="Vision"
                                                  onclick="return false" onkeydown="return false"
                                                  /></xsl:otherwise>
                                                  </xsl:choose>Vision</td>
                                                  <td>
                                                  <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'NoHearing']/Value = 'Y'"
                                                  ><input name="Hearing" type="checkbox"
                                                  value="Hearing" checked="checked"
                                                  onclick="return false" onkeydown="return false"
                                                  /></xsl:when>
                                                  <xsl:otherwise><input name="Hearing"
                                                  type="checkbox" value="Hearing"
                                                  onclick="return false" onkeydown="return false"
                                                  /></xsl:otherwise>
                                                  </xsl:choose>Hearing</td>
                                                  <td>
                                                  <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'NoBP']/Value = 'Y'"
                                                  ><input name="BP" type="checkbox" value="BP"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/></xsl:when>
                                                  <xsl:otherwise><input name="BP" type="checkbox"
                                                  value="BP" onclick="return false"
                                                  onkeydown="return false"/></xsl:otherwise>
                                                  </xsl:choose>BP</td>
                                                  </tr>
                                                  </tbody>
                                                  </table>
                                                  </td>
                                                  </tr>
                                                  <tr>
                                                  <td style="text-align: right;"> Weight:</td>
                                                  <td align="right">
                                                  <xsl:variable name="weight"
                                                  select="Records/Record/Field[@id = 'WeightP']"/>
                                                  <xsl:value-of select="$weight/Value"/>
                                                  </td>
                                                  <td>
                                                    <xsl:variable name="weightPUnits"
                                                    select="Records/Record/Field[@id = 'WeightPUnits']"/>
                                                    <xsl:value-of select="$weightPUnits/Value"/>
                                                  </td>
                                                  <td style="white-space: nowrap;"><xsl:variable
                                                  name="weight"
                                                  select="Records/Record/Field[@id = 'WeightS']"/>
                                                  <xsl:value-of select="$weight/Value"
                                                  />&#160;<xsl:variable name="weightUnits"
                                                  select="Records/Record/Field[@id = 'WeightSUnits']"/>
                                                  <xsl:value-of select="$weightUnits/Value"/></td>
                                                  <td rowspan="3">
                                                  <table align="center" border="0" cellpadding="1"
                                                  cellspacing="1">
                                                  <tbody>
                                                  <tr>
                                                  <td style="text-align: right;"> Vision Left:</td>
                                                  <td>20/<xsl:variable name="vision"
                                                  select="Records/Record/Field[@id = 'VisionL']"/>
                                                  <xsl:value-of select="$vision/Value"/></td>
                                                  </tr>
                                                  <tr>
                                                  <td style="text-align: right;"> Vision Right:</td>
                                                  <td>20/<xsl:variable name="vision"
                                                  select="Records/Record/Field[@id = 'VisionR']"/>
                                                  <xsl:value-of select="$vision/Value"/></td>
                                                  </tr>
                                                  <tr>
                                                  <td colspan="2" style="text-align: center;">
                                                  Vision Corrected? <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'Vision_Corrected']/Value = 'Y'"
                                                  ><input name="VisionCorrected" type="checkbox"
                                                  value="VisionCorrected" checked="checked"
                                                  onclick="return false" onkeydown="return false"
                                                  /></xsl:when>
                                                  <xsl:otherwise><input name="VisionCorrected"
                                                  type="checkbox" value="VisionCorrected"
                                                  onclick="return false" onkeydown="return false"
                                                  /></xsl:otherwise>
                                                  </xsl:choose></td>
                                                  </tr>
                                                  </tbody>
                                                  </table>
                                                  </td>
                                                  </tr>
                                                  <tr>
                                                  <td style="text-align: right;"> HC:</td>
                                                  <td align="right">
                                                  <xsl:variable name="hc"
                                                  select="Records/Record/Field[@id = 'HCP']"/>
                                                  <xsl:value-of select="$hc/Value"/>
                                                  </td>
                                                  <td> .</td>
                                                  <td style="white-space: nowrap;"><xsl:variable
                                                  name="hcUnits"
                                                  select="Records/Record/Field[@id = 'HCS']"/>
                                                  <xsl:value-of select="$hcUnits/Value"
                                                  />&#160;cm.</td>
                                                  <td> </td>
                                                  </tr>
                                                  <tr>
                                                  <td style="text-align: right;"> BP:</td>
                                                  <td align="right">
                                                  <xsl:variable name="bp"
                                                  select="Records/Record/Field[@id = 'BPS']"/>
                                                  <xsl:value-of select="$bp/Value"/>
                                                  </td>
                                                  <td>/</td>
                                                  <td style="white-space: nowrap;">
                                                  <xsl:variable name="bp"
                                                  select="Records/Record/Field[@id = 'BPD']"/>
                                                  <xsl:value-of select="$bp/Value"/>
                                                  </td>
                                                  <td> </td>
                                                  </tr>
                                                  <tr>
                                                  <td style="text-align: right;"> Temp:</td>
                                                  <td align="right">
                                                  <xsl:variable name="temp"
                                                  select="Records/Record/Field[@id = 'TempP']"/>
                                                  <xsl:value-of select="$temp/Value"/>
                                                  </td>
                                                  <td> .</td>
                                                  <td style="white-space: nowrap;"><xsl:variable
                                                  name="temp"
                                                  select="Records/Record/Field[@id = 'TempS']"/>
                                                  <xsl:value-of select="$temp/Value"/>&#160;deg.
                                                  F</td>
                                                  <td rowspan="3">
                                                  <table align="center" border="0" cellpadding="1"
                                                  cellspacing="1">
                                                  <tbody>
                                                  <tr>
                                                      <td>
                                                          <xsl:choose>
                                                            <xsl:when test="Records/Record/Field[@id = 'Oral']/Value = 'Oral Temp Type'">
                                                                <input name="Oral" type="checkbox" value="Oral" checked="checked"
                                                                    onclick="return false" onkeydown="return false"/>
                                                            </xsl:when>
                                                            <xsl:otherwise>
                                                                <input name="Oral" type="checkbox" value="Oral" onclick="return false" onkeydown="return false"/>
                                                            </xsl:otherwise>
                                                          </xsl:choose>Oral
                                                          <xsl:choose>
                                                              <xsl:when test="Records/Record/Field[@id = 'Rectal']/Value = 'Rectal Temp Type'">
                                                                  <input name="Rectal" type="checkbox" value="Rectal" checked="checked"
                                                                      onclick="return false" onkeydown="return false"/>
                                                              </xsl:when>
                                                              <xsl:otherwise>
                                                                  <input name="Rectal" type="checkbox" value="Rectal" onclick="return false" onkeydown="return false"/>
                                                              </xsl:otherwise>
                                                          </xsl:choose>Rectal
                                                          <xsl:choose>
                                                              <xsl:when test="Records/Record/Field[@id = 'Axillary']/Value = 'Axillary Temp Type'">
                                                                  <input name="Axillary" type="checkbox" value="Axillary" checked="checked"
                                                                      onclick="return false" onkeydown="return false"/>
                                                              </xsl:when>
                                                              <xsl:otherwise>
                                                                  <input name="Axillary" type="checkbox" value="Axillary" onclick="return false" onkeydown="return false"/>
                                                              </xsl:otherwise>
                                                          </xsl:choose>Axillary
                                                      </td>
                                                  <td align="center">P</td>
                                                  <td align="center">F</td>
                                                  </tr>
                                                  <tr>
                                                  <td style="text-align: right;"> Left Ear @
                                                  25db:</td>
                                                  <td>
                                                  <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'HearL']/Value = 'P'">
                                                  <input name="leftEarPass" type="checkbox"
                                                  value="leftEarPass" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="leftEarPass" type="checkbox"
                                                  value="leftEarPass" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                  </xsl:choose>
                                                  </td>
                                                  <td>
                                                  <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'HearL']/Value = 'F'">
                                                  <input name="leftEarFail" type="checkbox"
                                                  value="leftEarFail" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="leftEarFail" type="checkbox"
                                                  value="leftEarFail" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                  </xsl:choose>
                                                  </td>
                                                  </tr>
                                                  <tr>
                                                  <td style="text-align: right;"> Right Ear @
                                                  25db:</td>
                                                  <td>
                                                  <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'HearR']/Value = 'P'">
                                                  <input name="rightEarPass" type="checkbox"
                                                  value="rightEarPass" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="rightEarPass" type="checkbox"
                                                  value="rightEarPass" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                  </xsl:choose>
                                                  </td>
                                                  <td>
                                                  <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'HearR']/Value = 'F'">
                                                  <input name="rightEarFail" type="checkbox"
                                                  value="rightEarFail" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="rightEarFail" type="checkbox"
                                                  value="rightEarFail" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                  </xsl:choose>
                                                  </td>
                                                  </tr>
                                                  </tbody>
                                                  </table>
                                                  </td>
                                                  </tr>
                                                  <tr>
                                                  <td style="text-align: right;"> Pulse:</td>
                                                  <td align="right">
                                                  <xsl:variable name="pulse"
                                                  select="Records/Record/Field[@id = 'Pulse']"/>
                                                  <xsl:value-of select="$pulse/Value"/>
                                                  </td>
                                                  <td>/</td>
                                                  <td style="white-space: nowrap;">min</td>
                                                  <td> </td>
                                                  </tr>
                                                  <tr>
                                                  <td style="text-align: right;"> RR:</td>
                                                  <td align="right">
                                                  <xsl:variable name="rr"
                                                  select="Records/Record/Field[@id = 'RR']"/>
                                                  <xsl:value-of select="$rr/Value"/>
                                                  </td>
                                                  <td> </td>
                                                  <td style="white-space: nowrap;"> </td>
                                                  <td> </td>
                                                  </tr>
                                                  </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td style="BORDER-TOP: black solid 1px;">
                                <span style="font-size:14px;"><strong>Parents:</strong> Thank you
                                    for answering these questions about your child. The answers will
                                    help your doctor provide better quality of care. If your child
                                    is age 11 or older, he/she should answer the questions
                                    privately. Answers are confidential, but you prefer not to
                                    answer that is allowed. You may want to talk about these
                                    questions with your doctor.</span>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;">
                                <strong>
                                    <u>Please fill in the circles completely with a pencil or
                                        pen</u>
                                </strong>
                                <u>
                                    <strong>.</strong>
                                </u>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <table align="center" border="0" cellpadding="1" cellspacing="1">
                                    <tbody>
                                        <tr>
                                            <td style="text-align: center; width: 1%;">
                                                <strong>Y</strong>
                                            </td>
                                            <td style="text-align: center; width: 1%;">
                                                <strong>N</strong>
                                            </td>
                                            <td style="width: 48%"/>
                                            <td style="text-align: center; width: 1%;">
                                                <strong>Y</strong>
                                            </td>
                                            <td style="text-align: center; width: 1%;">
                                                <strong>N</strong>
                                            </td>
                                            <td style="width: 48%"/>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_1']/Value = 'Y'">
                                                  <input name="questionOneYes" type="radio"
                                                  value="questionOneYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionOneYes" type="radio"
                                                  value="questionOneYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_1']/Value = 'N'">
                                                  <input name="questionOneNo" type="radio"
                                                  value="questionOneNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionOneNo" type="radio"
                                                  value="questionOneNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question1']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_2']/Value = 'Y'">
                                                  <input name="questionTwoYes" type="radio"
                                                  value="questionTwoYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTwoYes" type="radio"
                                                  value="questionTwoYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_2']/Value = 'N'">
                                                  <input name="questionTwoNo" type="radio"
                                                  value="questionTwoNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTwoNo" type="radio"
                                                  value="questionTwoNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question2']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_3']/Value = 'Y'">
                                                  <input name="questionThreeYes" type="radio"
                                                  value="questionThreeYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionThreeYes" type="radio"
                                                  value="questionThreeYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_3']/Value = 'N'">
                                                  <input name="questionThreeNo" type="radio"
                                                  value="questionThreeNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionThreeNo" type="radio"
                                                  value="questionThreeNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question3']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_4']/Value = 'Y'">
                                                  <input name="questionFourYes" type="radio"
                                                  value="questionFourYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFourYes" type="radio"
                                                  value="questionFourYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_4']/Value = 'N'">
                                                  <input name="questionFourNo" type="radio"
                                                  value="questionFourNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFourNo" type="radio"
                                                  value="questionFourNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question4']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_5']/Value = 'Y'">
                                                  <input name="questionFiveYes" type="radio"
                                                  value="questionFiveYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFiveYes" type="radio"
                                                  value="questionFiveYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_5']/Value = 'N'">
                                                  <input name="questionFiveNo" type="radio"
                                                  value="questionFiveNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFiveNo" type="radio"
                                                  value="questionFiveNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question5']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_6']/Value = 'Y'">
                                                  <input name="questionSixYes" type="radio"
                                                  value="questionSixYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSixYes" type="radio"
                                                  value="questionSixYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_6']/Value = 'N'">
                                                  <input name="questionSixNo" type="radio"
                                                  value="questionSixNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSixNo" type="radio"
                                                  value="questionSixNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question6']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_7']/Value = 'Y'">
                                                  <input name="questionSevenYes" type="radio"
                                                  value="questionSevenYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSevenYes" type="radio"
                                                  value="questionSevenYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_7']/Value = 'N'">
                                                  <input name="questionSevenNo" type="radio"
                                                  value="questionSevenNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSevenNo" type="radio"
                                                  value="questionSevenNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question7']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_8']/Value = 'Y'">
                                                  <input name="questionEightYes" type="radio"
                                                  value="questionEightYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionEightYes" type="radio"
                                                  value="questionEightYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_8']/Value = 'N'">
                                                  <input name="questionEightNo" type="radio"
                                                  value="questionEightNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionEightNo" type="radio"
                                                  value="questionEightNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question8']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_9']/Value = 'Y'">
                                                  <input name="questionNineYes" type="radio"
                                                  value="questionNineYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionNineYes" type="radio"
                                                  value="questionNineYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_9']/Value = 'N'">
                                                  <input name="questionNineNo" type="radio"
                                                  value="questionNineNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionNineNo" type="radio"
                                                  value="questionNineNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question9']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_10']/Value = 'Y'">
                                                  <input name="questionTenYes" type="radio"
                                                  value="questionTenYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTenYes" type="radio"
                                                  value="questionTenYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_10']/Value = 'N'">
                                                  <input name="questionTenNo" type="radio"
                                                  value="questionTenNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTenNo" type="radio"
                                                  value="questionTenNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question10']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_11']/Value = 'Y'">
                                                  <input name="questionElevenYes" type="radio"
                                                  value="questionElevenYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionElevenYes" type="radio"
                                                  value="questionElevenYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_11']/Value = 'N'">
                                                  <input name="questionElevenNo" type="radio"
                                                  value="questionElevenNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionElevenNo" type="radio"
                                                  value="questionElevenNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question11']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_12']/Value = 'Y'">
                                                  <input name="questionTwelveYes" type="radio"
                                                  value="questionTwelveYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTwelveYes" type="radio"
                                                  value="questionTwelveYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_12']/Value = 'N'">
                                                  <input name="questionTwelveNo" type="radio"
                                                  value="questionTwelveNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTwelveNo" type="radio"
                                                  value="questionTwelveNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question12']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_13']/Value = 'Y'">
                                                  <input name="questionThirteenYes" type="radio"
                                                  value="questionThirteenYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionThirteenYes" type="radio"
                                                  value="questionThirteenYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_13']/Value = 'N'">
                                                  <input name="questionThirteenNo" type="radio"
                                                  value="questionThirteenNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionThirteenNo" type="radio"
                                                  value="questionThirteenNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question13']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_14']/Value = 'Y'">
                                                  <input name="questionFourteenYes" type="radio"
                                                  value="questionFourteenYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFourteenYes" type="radio"
                                                  value="questionFourteenYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_14']/Value = 'N'">
                                                  <input name="questionFourteenNo" type="radio"
                                                  value="questionFourteenNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFourteenNo" type="radio"
                                                  value="questionFourteenNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question14']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_15']/Value = 'Y'">
                                                  <input name="questionFifteenYes" type="radio"
                                                  value="questionFifteenYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFifteenYes" type="radio"
                                                  value="questionFifteenYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_15']/Value = 'N'">
                                                  <input name="questionFifteenNo" type="radio"
                                                  value="questionFifteenNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFifteenNo" type="radio"
                                                  value="questionFifteenNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question15']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_16']/Value = 'Y'">
                                                  <input name="questionSixteenYes" type="radio"
                                                  value="questionSixteenYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSixteenYes" type="radio"
                                                  value="questionSixteenYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_16']/Value = 'N'">
                                                  <input name="questionSixteenNo" type="radio"
                                                  value="questionSixteenNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSixteenNo" type="radio"
                                                  value="questionSixteenNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question16']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_17']/Value = 'Y'">
                                                  <input name="questionSeventeenYes" type="radio"
                                                  value="questionSeventeenYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSeventeenYes" type="radio"
                                                  value="questionSeventeenYes"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_17']/Value = 'N'">
                                                  <input name="questionSeventeenNo" type="radio"
                                                  value="questionSeventeenNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSeventeenNo" type="radio"
                                                  value="questionSeventeenNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question17']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_18']/Value = 'Y'">
                                                  <input name="questionEighteenYes" type="radio"
                                                  value="questionEighteenYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionEighteenYes" type="radio"
                                                  value="questionEighteenYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_18']/Value = 'N'">
                                                  <input name="questionEighteenNo" type="radio"
                                                  value="questionEighteenNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionEighteenNo" type="radio"
                                                  value="questionEighteenNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question18']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_19']/Value = 'Y'">
                                                  <input name="questionNineteenYes" type="radio"
                                                  value="questionNineteenYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionNineteenYes" type="radio"
                                                  value="questionNineteenYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_19']/Value = 'N'">
                                                  <input name="questionNineteenNo" type="radio"
                                                  value="questionNineteenNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionNineteenNo" type="radio"
                                                  value="questionNineteenNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question19']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_20']/Value = 'Y'">
                                                  <input name="questionTwentyYes" type="radio"
                                                  value="questionTwentyYes" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTwentyYes" type="radio"
                                                  value="questionTwentyYes" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_20']/Value = 'N'">
                                                  <input name="questionTwentyNo" type="radio"
                                                  value="questionTwentyNo" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTwentyNo" type="radio"
                                                  value="questionTwentyNo" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question20']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <table align="center" cellpadding="1" cellspacing="1"
                    style="BORDER: black solid 1px;">
                    <tbody>
                        <tr>
                            <td style="BORDER-BOTTOM: solid black 1px">
                                <table align="right" border="0" cellpadding="1" cellspacing="1">
                                    <tbody>
                                        <tr>
                                            <td style="text-align: right;">
                                                <strong>MRN:</strong>
                                            </td>
                                            <td colspan="2">
                                                <xsl:variable name="mrn"
                                                  select="Records/Record/Field[@id = 'MRN']"/>
                                                <strong>
                                                  <xsl:value-of select="$mrn/Value"/>
                                                </strong>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="text-align: right;">
                                                <strong>Name:</strong>
                                            </td>
                                            <td colspan="2">
                                                <xsl:variable name="name"
                                                  select="Records/Record/Field[@id = 'PatientName']"/>
                                                <strong>
                                                  <xsl:value-of select="$name/Value"/>
                                                </strong>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="text-align: right;">
                                                <strong>Age:</strong>
                                            </td>
                                            <td>
                                                <xsl:variable name="age"
                                                  select="Records/Record/Field[@id = 'Age']"/>
                                                <strong>
                                                  <xsl:value-of select="$age/Value"/>
                                                </strong>
                                            </td>
                                            <td>
                                                <strong>DOB:<xsl:variable name="dob"
                                                  select="Records/Record/Field[@id = 'DOB']"/>
                                                  <xsl:value-of select="$dob/Value"/></strong>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <span style="font-size:14px;"><strong>Padres de familia</strong>:
                                    Muchas gracias por tomarse la molestia de contestar las
                                    siguientes preguntas acerca de su nino(a). Las respuestas de
                                    estas preguntas seran: ayudar a su doctor a dar mejor atencion
                                    medica. Si su nino(a) tiene 11 anos o mas, por favor su nino(a)
                                    debe contestar las preguntas el (ella) solo(a). Sus respuestas
                                    seran completamente privadas. No necesita contestar
                                        <strong>ninguna</strong> pregunta que no desee contestar. Si
                                    usted tiene preguntas acerca de este cuestionario, haga el favor
                                    de hablar sobre ellas con su doctor. <strong><u>Por favor llene
                                            los circulos de la forma mas completa que le sea posible
                                            con un lapiz o lapiz tinta.</u></strong></span>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <table align="center" border="0" cellpadding="1" cellspacing="1">
                                    <tbody>
                                        <tr>
                                            <td style="text-align: center; width: 1%;">
                                                <strong>Y</strong>
                                            </td>
                                            <td style="text-align: center; width: 1%;">
                                                <strong>N</strong>
                                            </td>
                                            <td style="width: 48%"/>
                                            <td style="text-align: center; width: 1%;">
                                                <strong>Y</strong>
                                            </td>
                                            <td style="text-align: center; width: 1%;">
                                                <strong>N</strong>
                                            </td>
                                            <td style="width: 48%"/>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_1_2']/Value = 'Y'">
                                                  <input name="questionOneYesSpanish" type="radio"
                                                  value="questionOneYesSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionOneYesSpanish" type="radio"
                                                  value="questionOneYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_1_2']/Value = 'N'">
                                                  <input name="questionOneNoSpanish" type="radio"
                                                  value="questionOneNoSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionOneNoSpanish" type="radio"
                                                  value="questionOneNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question1_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_2_2']/Value = 'Y'">
                                                  <input name="questionTwoYesSpanish" type="radio"
                                                  value="questionTwoYesSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTwoYesSpanish" type="radio"
                                                  value="questionTwoYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_2_2']/Value = 'N'">
                                                  <input name="questionTwoNoSpanish" type="radio"
                                                  value="questionTwoNoSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTwoNoSpanish" type="radio"
                                                  value="questionTwoNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question2_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_3_2']/Value = 'Y'">
                                                  <input name="questionThreeYesSpanish" type="radio"
                                                  value="questionThreeYesSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionThreeYesSpanish" type="radio"
                                                  value="questionThreeYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_3_2']/Value = 'N'">
                                                  <input name="questionThreeNoSpanish" type="radio"
                                                  value="questionThreeNoSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionThreeNoSpanish" type="radio"
                                                  value="questionThreeNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question3_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_4_2']/Value = 'Y'">
                                                  <input name="questionFourYesSpanish" type="radio"
                                                  value="questionFourYesSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFourYesSpanish" type="radio"
                                                  value="questionFourYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_4_2']/Value = 'N'">
                                                  <input name="questionFourNoSpanish" type="radio"
                                                  value="questionFourNoSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFourNoSpanish" type="radio"
                                                  value="questionFourNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question4_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_5_2']/Value = 'Y'">
                                                  <input name="questionFiveYesSpanish" type="radio"
                                                  value="questionFiveYesSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFiveYesSpanish" type="radio"
                                                  value="questionFiveYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_5_2']/Value = 'N'">
                                                  <input name="questionFiveNoSpanish" type="radio"
                                                  value="questionFiveNoSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFiveNoSpanish" type="radio"
                                                  value="questionFiveNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question5_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_6_2']/Value = 'Y'">
                                                  <input name="questionSixYesSpanish" type="radio"
                                                  value="questionSixYesSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSixYesSpanish" type="radio"
                                                  value="questionSixYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_6_2']/Value = 'N'">
                                                  <input name="questionSixNoSpanish" type="radio"
                                                  value="questionSixNoSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSixNoSpanish" type="radio"
                                                  value="questionSixNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question6_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_7_2']/Value = 'Y'">
                                                  <input name="questionSevenYesSpanish" type="radio"
                                                  value="questionSevenYesSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSevenYesSpanish" type="radio"
                                                  value="questionSevenYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_7_2']/Value = 'N'">
                                                  <input name="questionSevenNoSpanish" type="radio"
                                                  value="questionSevenNoSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSevenNoSpanish" type="radio"
                                                  value="questionSevenNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question7_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_8_2']/Value = 'Y'">
                                                  <input name="questionEightYesSpanish" type="radio"
                                                  value="questionEightYesSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionEightYesSpanish" type="radio"
                                                  value="questionEightYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_8_2']/Value = 'N'">
                                                  <input name="questionEightNoSpanish" type="radio"
                                                  value="questionEightNoSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionEightNoSpanish" type="radio"
                                                  value="questionEightNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question8_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_9_2']/Value = 'Y'">
                                                  <input name="questionNineYesSpanish" type="radio"
                                                  value="questionNineYesSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionNineYesSpanish" type="radio"
                                                  value="questionNineYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_9_2']/Value = 'N'">
                                                  <input name="questionNineNoSpanish" type="radio"
                                                  value="questionNineNoSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionNineNoSpanish" type="radio"
                                                  value="questionNineNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question9_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_10_2']/Value = 'Y'">
                                                  <input name="questionTenYesSpanish" type="radio"
                                                  value="questionTenYesSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTenYesSpanish" type="radio"
                                                  value="questionTenYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_10_2']/Value = 'N'">
                                                  <input name="questionTenNoSpanish" type="radio"
                                                  value="questionTenNoSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTenNoSpanish" type="radio"
                                                  value="questionTenNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question10_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_11_2']/Value = 'Y'">
                                                  <input name="questionElevenYesSpanish"
                                                  type="radio" value="questionElevenYesSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionElevenYesSpanish"
                                                  type="radio" value="questionElevenYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_11_2']/Value = 'N'">
                                                  <input name="questionElevenNoSpanish" type="radio"
                                                  value="questionElevenNoSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionElevenNoSpanish" type="radio"
                                                  value="questionElevenNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question11_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_12_2']/Value = 'Y'">
                                                  <input name="questionTwelveYesSpanish"
                                                  type="radio" value="questionTwelveYesSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTwelveYesSpanish"
                                                  type="radio" value="questionTwelveYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_12_2']/Value = 'N'">
                                                  <input name="questionTwelveNoSpanish" type="radio"
                                                  value="questionTwelveNoSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTwelveNoSpanish" type="radio"
                                                  value="questionTwelveNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question12_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_13_2']/Value = 'Y'">
                                                  <input name="questionThirteenYesSpanish"
                                                  type="radio" value="questionThirteenYesSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionThirteenYesSpanish"
                                                  type="radio" value="questionThirteenYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_13_2']/Value = 'N'">
                                                  <input name="questionThirteenNoSpanish"
                                                  type="radio" value="questionThirteenNoSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionThirteenNoSpanish"
                                                  type="radio" value="questionThirteenNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question13_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_14_2']/Value = 'Y'">
                                                  <input name="questionFourteenYesSpanish"
                                                  type="radio" value="questionFourteenYesSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFourteenYesSpanish"
                                                  type="radio" value="questionFourteenYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_14_2']/Value = 'N'">
                                                  <input name="questionFourteenNoSpanish"
                                                  type="radio" value="questionFourteenNoSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFourteenNoSpanish"
                                                  type="radio" value="questionFourteenNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question14_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_15_2']/Value = 'Y'">
                                                  <input name="questionFifteenYesSpanish"
                                                  type="radio" value="questionFifteenYesSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFifteenYesSpanish"
                                                  type="radio" value="questionFifteenYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_15_2']/Value = 'N'">
                                                  <input name="questionFifteenNoSpanish"
                                                  type="radio" value="questionFifteenNoSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionFifteenNoSpanish"
                                                  type="radio" value="questionFifteenNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question15_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_16_2']/Value = 'Y'">
                                                  <input name="questionSixteenYesSpanish"
                                                  type="radio" value="questionSixteenYesSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSixteenYesSpanish"
                                                  type="radio" value="questionSixteenYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_16_2']/Value = 'N'">
                                                  <input name="questionSixteenNoSpanish"
                                                  type="radio" value="questionSixteenNoSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSixteenNoSpanish"
                                                  type="radio" value="questionSixteenNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question16_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_17_2']/Value = 'Y'">
                                                  <input name="questionSeventeenYesSpanish"
                                                  type="radio" value="questionSeventeenYesSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSeventeenYesSpanish"
                                                  type="radio" value="questionSeventeenYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_17_2']/Value = 'N'">
                                                  <input name="questionSeventeenNoSpanish"
                                                  type="radio" value="questionSeventeenNoSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionSeventeenNoSpanish"
                                                  type="radio" value="questionSeventeenNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question17_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_18_2']/Value = 'Y'">
                                                  <input name="questionEighteenYesSpanish"
                                                  type="radio" value="questionEighteenYesSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionEighteenYesSpanish"
                                                  type="radio" value="questionEighteenYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_18_2']/Value = 'N'">
                                                  <input name="questionEighteenNoSpanish"
                                                  type="radio" value="questionEighteenNoSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionEighteenNoSpanish"
                                                  type="radio" value="questionEighteenNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question18_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_19_2']/Value = 'Y'">
                                                  <input name="questionNineteenYesSpanish"
                                                  type="radio" value="questionNineteenYesSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionNineteenYesSpanish"
                                                  type="radio" value="questionNineteenYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_19_2']/Value = 'N'">
                                                  <input name="questionNineteenNoSpanish"
                                                  type="radio" value="questionNineteenNoSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionNineteenNoSpanish"
                                                  type="radio" value="questionNineteenNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question19_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_20_2']/Value = 'Y'">
                                                  <input name="questionTwentyYesSpanish"
                                                  type="radio" value="questionTwentyYesSpanish"
                                                  checked="checked" onclick="return false"
                                                  onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTwentyYesSpanish"
                                                  type="radio" value="questionTwentyYesSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="text-align: center; padding-bottom: 15px;">
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="Records/Record/Field[@id = 'QuestionEntry_20_2']/Value = 'N'">
                                                  <input name="questionTwentyNoSpanish" type="radio"
                                                  value="questionTwentyNoSpanish" checked="checked"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <input name="questionTwentyNoSpanish" type="radio"
                                                  value="questionTwentyNoSpanish"
                                                  onclick="return false" onkeydown="return false"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td valign="top"
                                                style="font-size:15px; padding-bottom: 15px;">
                                                <xsl:variable name="question"
                                                  select="Records/Record/Field[@id = 'Question20_SP']"/>
                                                <xsl:value-of select="$question/Value"/>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <p> </p>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
