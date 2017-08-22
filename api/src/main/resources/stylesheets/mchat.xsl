<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>
    
    <xsl:template match="/">
        <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html></xsl:text>
        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
                <title>M-CHAT</title>
                <style>
                    .formPage {
                        font-size:12px;
                        width:600px;
                        height:788px;
                        margin:0 auto;
                        border: 1px solid black;
                        page-break-after: always;
                    }
                    
                    .instructionContainer {
                        width:590px;
                        min-height:20px;
                        padding-left:5px;
                        padding-right:5px;
                        padding-top:0;
                        padding-bottom:0;
                    }
                    
                    .titleContainer {
                      width:600px;
                      height:30px;
                      margin-top: -20px;
                    }
                    
                    .extraTitleContainer {
                        width:600px;
                        height:15px;
                        text-align:center;
                    }
                    
                    .nameContainer {
                        width:600px;
                        height:40px;
                        text-align:center;
                        margin-top: -10px;
                    }
                    
                    .title, 
                    .mrn,
                    .emptyTitle{
                     width: 197px;
                     height: 30px;
                     float: left;
                     margin-top: -10px;
                    }
                    
                    .emptyTitle {
                    
                    }
                    
                    .title {
                        font-size:16px;
                        text-align:center;
                    }
                    
                    .mrn {
                        text-align:right;
                        font-size:16px;
                    }
                    
                    .answerEmptyContainer {
                        width:511px;
                        height:20px;
                        float:left;
                    }
                    
                    .answerDataContainer {
                        width:85px;
                        height:20px;
                        float:left;
                    }
                    
                    .answerLabel,
                    .emptyAnswerLabel,
                    .answerLabelBold {
                        width:7%;
                        text-align:center;
                        padding-bottom: 0px;
                    }
                    
                    .emptyAnswerLabel {
                        min-height:2px;
                        height:5px;
                    }
                    
                    .questionContainer,
                    .emptyQuestionContainer {
                        width:100%;
                    }
                    
                    .emptyQuestionContainer {
                        height:5px;
                    }
                    
                    .questionNumber,
                    .questionNumberBold,
                    .emptyQuestionNumber {
                        width:4%;
                        padding-bottom: 0px;}
                    }
                    .emptyQuestionNumber {
                        height:5px;
                    }
                    
                    .question,
                    .questionBold,
                    .emptyQuestion {
                        width:80%;
                        padding-bottom: 0px;
                    }
                    
                    .questionBold,
                    .answerLabelBold,
                    .questionNumberBold {
                    background-color:#EBEBFF;
                    }
                    
                    .copyrightContainer,
                    .copyrightContainerSpanish {
                        width:600px;
                        text-align:left;
                        margin-left:25px;
                        margin-top: 83px;
                    }
                    .copyrightContainerSpanish {
                        margin-top: -5px;
                    }
                    .formTable {
                        font-size:13px;
                        width:500px;
                        margin: 0 auto;
                        border-spacing: 0px 1px;
                        margin-top: -10px;
                    }
                    
                    .textBold {
                        font-weight: bold;
                    }
                </style>
            </head>
            <body>
                <div class="formPage">
                    <div class="nameContainer">
                        <h3><xsl:value-of select="Records/Record/Field[@id = 'PatientName']"/></h3>
                    </div>
                    <div class="titleContainer">
                        <div class="emptyTitle">
							&#160;
                        </div>
                        <div class="title">
                            <h3>M-CHAT</h3>
                        </div>
                        <div class="mrn">
                            <h3><xsl:value-of select="Records/Record/Field[@id = 'MRN']"/></h3>
                        </div>
                    </div>
                    <div class="instructionContainer">
                        <p>Please fill out the following about how your child usually is.  Please try to answer every question.  If the behavior is rare (e.g., you've seen it once or twice), please answer as if the child does not do it.</p>
                    </div>
                    <table class="formTable">
                        <tr class="questionContainer">
                            <td class="questionNumber"></td>
                            <td class="question"></td>
                            <td class="answerLabel">Yes</td>
                            <td class="answerLabel">No</td>
                            <td></td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">1.</td>
                            <td class="question">Does your child enjoy being swung, bounced on your knee, etc.?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_1']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_1_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_1']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_1_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_1']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_1_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold textBold">2.</td>
                            <td class="questionBold textBold">Does your child take an interest in other children?</td>
                            <td class="answerLabelBold textBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_2']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_2_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold textBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_2']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_2_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_2']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_2_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">3.</td>
                            <td class="question">Does your child like climbing on things, such as up stairs?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_3']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_3_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_3']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_3_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_3']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_3_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">4.</td>
                            <td class="questionBold">Does your child enjoy playing peek-a-bool/hide-and-seek?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_4']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_4_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_4']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_4_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_4']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_4_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">5.</td>
                            <td class="question">Does your child ever pretend, for example, to talk on the phone or take care of a doll or pretend other things?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_5']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_5_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_5']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_5_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_5']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_5_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">6.</td>
                            <td class="questionBold">Does your child ever use his/her index finger to point, to ask for something?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_6']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_6_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_6']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_6_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_6']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_6_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber textBold">7.</td>
                            <td class="question textBold">Does your child ever use his/her index finger to point, to indicate interest in something?</td>
                            <td class="answerLabel textBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_7']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_7_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel textBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_7']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_7_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_7']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_7_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">8.</td>
                            <td class="questionBold">Can your child play properly with small toys (e.g. cars or blocks) without just mouthing, fiddling, or dropping them?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_8']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_8_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_8']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_8_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_8']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_8_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber textBold">9.</td>
                            <td class="question textBold">Does your child ever bring objects over to you (parent) to show you something?</td>
                            <td class="answerLabel textBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_9']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_9_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel textBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_9']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_9_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_9']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_9_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="emptyQuestionContainer">
                            <td class="emptyQuestionNumber"></td>
                            <td class="emptyQuestion"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td></td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">10.</td>
                            <td class="questionBold">Does your child ever look you in the eye for more than a second or two?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_10']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_10_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_10']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_10_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_10']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_10_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">11.</td>
                            <td class="question">Does your child ever seem oversensitive to noise? (e.g. plugging ears)</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_11']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_11_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_11']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_11_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_11']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_11_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">12.</td>
                            <td class="questionBold">Does your child smile in response to your face or your smile?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_12']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_12_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_12']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_12_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_12']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_12_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber textBold">13.</td>
                            <td class="question textBold">Does your child imitate you? (e.g. you make a face-will your child imitate it?)</td>
                            <td class="answerLabel textBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_13']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_13_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel textBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_13']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_13_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_13']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_13_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="emptyQuestionContainer">
                            <td class="emptyQuestionNumber"></td>
                            <td class="emptyQuestion"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td></td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold textBold">14.</td>
                            <td class="questionBold textBold">Does your child respond to his/her name when you call?</td>
                            <td class="answerLabelBold textBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_14']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_14_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold textBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_14']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_14_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_14']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_14_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber textBold">15.</td>
                            <td class="question textBold">If you point at a toy across the room, does your child look at it?</td>
                            <td class="answerLabel textBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_15']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_15_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel textBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_15']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_15_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_15']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_15_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">16.</td>
                            <td class="questionBold">Does your child walk?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_16']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_16_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_16']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_16_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_16']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_16_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">17.</td>
                            <td class="question">Does your child look at things you are looking at?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_17']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_17_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_17']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_17_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_17']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_17_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">18.</td>
                            <td class="questionBold">Does your child make unusual finger movements near his/her face?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_18']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_18_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_18']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_18_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_18']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_18_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">19.</td>
                            <td class="question">Does your child try to attract your attention to his/her own activity?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_19']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_19_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_19']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_19_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_19']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_19_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="emptyQuestionContainer">
                            <td class="emptyQuestionNumber"></td>
                            <td class="emptyQuestion"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td></td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">20.</td>
                            <td class="questionBold">Have you ever wondered if your child is deaf?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_20']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_20_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_20']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_20_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_20']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_20_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">21.</td>
                            <td class="question">Does your child understand what people say?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_21']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_21_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_21']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_21_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_21']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_21_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">22.</td>
                            <td class="questionBold">Does your child sometimes stare at nothing or wander with no purpose?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_22']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_22_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_22']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_22_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_22']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_22_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="emptyQuestionContainer">
                            <td class="emptyQuestionNumber"></td>
                            <td class="emptyQuestion"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td></td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">23.</td>
                            <td class="question">Does your child look at your face to check your reaction when faced with something unfamiliar?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_23']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_23_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_23']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_23_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_23']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_23_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                    </table>
                    <div class="copyrightContainer"><p>&#0169; 1999 Diana Robins, Deborah Fein, &#38; Marianne Barton</p></div>
                </div>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>