<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>
    
    <xsl:template match="/">
        <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html></xsl:text>
        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
                <title>M-CHAT-R&#153;:</title>
                <style>
                    .formPage {
                        font-family:Arial; 
                        font-size:12px;
                        width:600px;
                        height:880px;
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
                    
                    .questionContainer {
                        width:100%;
                    }
                    
                    .questionNumber,
                    .questionNumberBold {
                        width:4%;
                        padding-bottom: 0px;}
                    }
                    
                    .question,
                    .questionBold {
                        width:80%;
                        padding-bottom: 0px;
                    }
                    
                    .questionBold,
                    .answerLabelBold,
                    .questionNumberBold,
                    .flagBold {
                        background-color:#EBEBFF;
                    }
                    
                    .copyrightContainer,
                    .copyrightContainerSpanish {
                        width:600px;
                        text-align:left;
                        margin-left:25px;
                        margin-top: 20px;
                    }
                    .copyrightContainerSpanish {
                        margin-top: -5px;
                    }
                    .formTable {
                        font-size:13px;
                        width:560px;
                        margin: 0 auto;
                        border-spacing: 0px 1px;
                        margin-top: -10px;
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
                            <h3>M-CHAT-R&#153;:</h3>
                        </div>
                        <div class="mrn">
                            <h3><xsl:value-of select="Records/Record/Field[@id = 'MRN']"/></h3>
                        </div>
                    </div>
                    <div class="instructionContainer">
                        <p>Please answer these questions about your child. Keep in mind how your child usually behaves. If you have seen your child do the behavior a few times, but he or she does not usually do it, then please answer no. Please circle yes or no for every question. Thank you very much.</p>
                    </div>
                    <table class="formTable">
                        <tr class="questionContainer">
                            <td class="questionNumber"></td>
                            <td class="question"></td>
                            <td class="answerLabel" valign="top">Yes</td>
                            <td class="answerLabel" valign="top">No</td>
                            <td></td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber" valign="top">1.</td>
                            <td class="question">If you point at something across the room, does your child look at it? (FOR EXAMPLE, if you point at a toy or an animal, does your child look at the toy or animal?)</td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_1']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_1_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_1']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_1_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_1']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_1_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold" valign="top">2.</td>
                            <td class="questionBold">Have you ever wondered if your child might be deaf?</td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_2']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_2_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_2']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_2_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="flagBold" valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_2']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_2_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber" valign="top">3.</td>
                            <td class="question">Does your child play pretend or make-believe? (FOR EXAMPLE, pretend to drink from an empty cup, pretend to talk on a phone, or pretend to feed a doll or stuffed animal?)</td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_3']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_3_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_3']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_3_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_3']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_3_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold" valign="top">4.</td>
                            <td class="questionBold">Does your child like climbing on things? (FOR EXAMPLE, furniture, playground equipment, or stairs)</td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_4']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_4_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_4']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_4_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="flagBold" valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_4']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_4_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber" valign="top">5.</td>
                            <td class="question">Does your child make unusual finger movements near his or her eyes? (FOR EXAMPLE, does your child wiggle his or her fingers close to his or her eyes?)</td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_5']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_5_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_5']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_5_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_5']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_5_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold" valign="top">6.</td>
                            <td class="questionBold">Does your child point with one finger to ask for something or to get help? (FOR EXAMPLE, pointing to a snack or toy that is out of reach)</td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_6']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_6_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_6']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_6_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="flagBold" valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_6']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_6_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber" valign="top">7.</td>
                            <td class="question">Does your child point with one finger to show you something interesting? (FOR EXAMPLE, pointing to an airplane in the sky or a big truck in the road)</td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_7']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_7_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_7']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_7_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_7']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_7_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold" valign="top">8.</td>
                            <td class="questionBold">Is your child interested in other children? (FOR EXAMPLE, does your child watch other children, smile at them, or go to them?)</td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_8']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_8_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_8']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_8_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="flagBold" valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_8']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_8_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber" valign="top">9.</td>
                            <td class="question">Does your child show you things by bringing them to you or holding them up for you to see - not to get help, but just to share? (FOR EXAMPLE, showing you a flower, a stuffed animal, or a toy truck)</td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_9']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_9_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_9']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_9_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_9']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_9_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold" valign="top">10.</td>
                            <td class="questionBold">Does your child respond when you call his or her name? (FOR EXAMPLE, does he or she look up, talk or babble, or stop what he or she is doing when you call his or her name?)</td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_10']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_10_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_10']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_10_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="flagBold" valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_10']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_10_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber" valign="top">11.</td>
                            <td class="question">When you smile at your child, does he or she smile back at you?</td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_11']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_11_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_11']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_11_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_11']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_11_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold" valign="top">12.</td>
                            <td class="questionBold">Does your child get upset by everyday noises? (FOR EXAMPLE, does your child scream or cry to noise such as a vacuum cleaner or loud music?)</td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_12']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_12_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_12']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_12_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="flagBold" valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_12']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_12_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber" valign="top">13.</td>
                            <td class="question">Does your child walk?</td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_13']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_13_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_13']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_13_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_13']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_13_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold" valign="top">14.</td>
                            <td class="questionBold">Does your child look you in the eye when you are talking to him or her, playing with him or her, or dressing him or her?</td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_14']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_14_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_14']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_14_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="flagBold" valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_14']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_14_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber" valign="top">15.</td>
                            <td class="question">Does your child try to copy what you do? (FOR EXAMPLE, wave bye-bye, clap, or make a funny noise when you do)</td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_15']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_15_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_15']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_15_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_15']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_15_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold" valign="top">16.</td>
                            <td class="questionBold">If you turn your head to look at something, does your child look around to see what you are looking at?</td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_16']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_16_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_16']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_16_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="flagBold" valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_16']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_16_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber" valign="top">17.</td>
                            <td class="question">Does your child try to get you to watch him or her? (FOR EXAMPLE, does your child look at you for praise, or say "look" or "watch me"?)</td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_17']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_17_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_17']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_17_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_17']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_17_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold" valign="top">18.</td>
                            <td class="questionBold">Does your child understand when you tell him or her to do something? (FOR EXAMPLE, if you don&#39;t point, can your child understand "put the book on the chair" or "bring me the blanket"?)</td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_18']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_18_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_18']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_18_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="flagBold" valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_18']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_18_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber" valign="top">19.</td>
                            <td class="question">If something new happens, does your child look at your face to see how you feel about it? (FOR EXAMPLE, if he or she hears a strange or funny noise, or sees a new toy, will he or she look at your face?)</td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_19']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_19_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_19']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_19_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_19']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_19_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold" valign="top">20.</td>
                            <td class="questionBold">Does your child like movement activities? (FOR EXAMPLE, being swung or bounced on your knee)</td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_20']/Value = 'passed') or (Records/Record/Field[@id = 'Choice_20_sp']/Value = 'passed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold" valign="top">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'Choice_20']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_20_sp']/Value = 'failed')">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="flagBold" valign="top">
                                <xsl:if test="(Records/Record/Field[@id = 'Choice_20']/Value = 'failed') or (Records/Record/Field[@id = 'Choice_20_sp']/Value = 'failed')">
                                    <xsl:text>*</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                    </table>
                    <div class="copyrightContainer"><p>&#169; 2009 Diana Robins, Deborah Fein, &amp; Marianne Barton</p></div>
                </div>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>