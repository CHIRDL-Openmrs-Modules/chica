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
                        border: 1px solid red;
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
                </style>
            </head>
            <body>
                <div class="formPage">
                    <div class="nameContainer">
                        <h3><xsl:value-of select="Records/Record/Field[@id = 'PatientName']"/></h3>
                    </div>
                    <div class="titleContainer">
                        <div class="emptyTitle">
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
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">1.</td>
                            <td class="question">Does your child enjoy being swung, bounced on your knee, etc.?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_1']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_1']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">2.</td>
                            <td class="questionBold">Does your child take an interest in other children?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_2']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_2']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">3.</td>
                            <td class="question">Does your child like climbing on things, such as up stairs?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_3']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_3']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">4.</td>
                            <td class="questionBold">Does your child enjoy playing peek-a-bool/hide-and-seek?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_4']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_4']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">5.</td>
                            <td class="question">Does your child ever pretend, for example, to talk on the phone or take care of a doll or pretend other things?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_5']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_5']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">6.</td>
                            <td class="questionBold">Does your child ever use his/her index finger to point, to ask for something?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_6']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_6']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">7.</td>
                            <td class="question">Does your child ever use his/her index finger to point, to indicate interest in something?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_7']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_7']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">8.</td>
                            <td class="questionBold">Can your child play properly with small toys (e.g. cars or blocks) without just mouthing, fiddling, or dropping them?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_8']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_8']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">9.</td>
                            <td class="question">Does your child ever bring objects over to you (parent) to show you something?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_9']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_9']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="emptyQuestionContainer">
                            <td class="emptyQuestionNumber"></td>
                            <td class="emptyQuestion"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td class="emptyAnswerLabel"></td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">10.</td>
                            <td class="questionBold">Does your child ever look you in the eye for more than a second or two?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_10']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_10']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">11.</td>
                            <td class="question">Does your child ever seem oversensitive to noise? (e.g. plugging ears)</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_11']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_11']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">12.</td>
                            <td class="questionBold">Does your child smile in response to your face or your smile?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_12']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_12']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">13.</td>
                            <td class="question">Does your child imitate you? (e.g. you make a face-will your child imitate it?)</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_13']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_13']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="emptyQuestionContainer">
                            <td class="emptyQuestionNumber"></td>
                            <td class="emptyQuestion"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td class="emptyAnswerLabel"></td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">14.</td>
                            <td class="questionBold">Does your child respond to his/her name when you call?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_14']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_14']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">15.</td>
                            <td class="question">If you point at a toy across the room, does your child look at it?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_15']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_15']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">16.</td>
                            <td class="questionBold">Does your child walk?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_16']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_16']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">17.</td>
                            <td class="question">Does your child look at things you are looking at?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_17']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_17']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">18.</td>
                            <td class="questionBold">Does your child make unusual finger movements near his/her face?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_18']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_18']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">19.</td>
                            <td class="question">Does your child try to attract your attention to his/her own activity?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_19']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_19']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="emptyQuestionContainer">
                            <td class="emptyQuestionNumber"></td>
                            <td class="emptyQuestion"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td class="emptyAnswerLabel"></td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">20.</td>
                            <td class="questionBold">Have you ever wondered if your child is deaf?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_20']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_20']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">21.</td>
                            <td class="question">Does your child understand what people say?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_21']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_21']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">22.</td>
                            <td class="questionBold">Does your child sometimes stare at nothing or wander with no purpose?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_22']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_22']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="emptyQuestionContainer">
                            <td class="emptyQuestionNumber"></td>
                            <td class="emptyQuestion"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td class="emptyAnswerLabel"></td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">23.</td>
                            <td class="question">Does your child look at your face to check your reaction when faced with something unfamiliar?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_23']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_23']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                    </table>
                    <div class="copyrightContainer"><p>&#0169; 1999 Diana Robins, Deborah Fein, &#38; Marianne Barton</p></div>
                </div>
                <div class="formPage">
                    <div class="nameContainer">
                        <h3><xsl:value-of select="Records/Record/Field[@id = 'PatientName']"/></h3>
                    </div>
                    <div class="titleContainer">
                        <div class="emptyTitle">
                        </div>
                        <div class="title">
                            <h3>M-CHAT</h3>
                        </div>
                        <div class="mrn">
                            <h3><xsl:value-of select="Records/Record/Field[@id = 'MRN']"/></h3>
                        </div>
                    </div>
                    <div class="extraTitleContainer">Evaluaci&#243;n del desarollo de niños en edad de caminar</div>
                    <div class="instructionContainer">
                        <p>Por favor conteste acerca de como su niño (a) es usualmente. Por favor trata de contestar cada pregunta. Si el comportamiento de su niño no ocurre con frecuencia, conteste como si no lo hiciera.</p>
                    </div>
                    <table class="formTable">
                        <tr class="questionContainer">
                            <td class="questionNumber"></td>
                            <td class="question"></td>
                            <td class="answerLabel">Yes</td>
                            <td class="answerLabel">No</td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">1.</td>
                            <td class="question">&#191;Disfruta su niño (a) cuando lo balancean o hacen saltar sobre su rodilla?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_1_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_1_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">2.</td>
                            <td class="questionBold">&#191;Se interesa su niño (a) en otros niños?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_2_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_2_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">3.</td>
                            <td class="question">&#191;Le gusta a su niño (a) subirse a las cosas, por ejemplo subir las escaleras?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_3_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_3_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">4.</td>
                            <td class="questionBold">&#191;Disfruta su niño (a) jugando "peek-a-boo" o "hide and seek" (a las escondidas)?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_4_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_4_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">5.</td>
                            <td class="question">&#191;Le gusta a su niño (a) jugar a pretendar, como por ejemplo, pretende que habla por tel&#233;fono, que cuida sus muñecas, o pretende otras cosas?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_5_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_5_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">6.</td>
                            <td class="questionBold">&#191;Utiliza su niño (a) su dedo &#237;ndice para señalar algo, o para preguntar alguna cosa?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_6_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_6_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">7.</td>
                            <td class="question">&#191;Usa su niño (a) su dedo &#237;ndice para señalar o indicar inter&#233;s en algo?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_7_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_7_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">8.</td>
                            <td class="questionBold">&#191;Puede su niño (a) jugar bien con jugetes pequeños (como carros o cubos) sin llevárselos a la boca, manipularlos o dejarlos caer)?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_8_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_8_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">9.</td>
                            <td class="question">&#191;Le trae su niño (a) a usted (padre o madre) objetos o cosas, con el prop&#243;sito de mostrarle algo alguna vez?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_9_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_9_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="emptyQuestionContainer">
                            <td class="emptyQuestionNumber"></td>
                            <td class="emptyQuestion"></td>
                            <div class="emptyAnswerLabel"></div>
                            <div class="emptyAnswerLabel"></div>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">10.</td>
                            <td class="questionBold">&#191;Lo mira su niño (a) directamente a los ojos por mas de uno o dos segundos?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_10_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_10_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">11.</td>
                            <td class="question">&#191;Parece su niño (a) ser demasiado sensitivo al ruido? (por ejemplo, se tapa los oidos)?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_11_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_11_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">12.</td>
                            <td class="questionBold">&#191;Sonrie su niño (a) en respuesta a su cara o a su sonrisa?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_12_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_12_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">13.</td>
                            <td class="question">&#191;Lo imita su niño (a)? Por ejemplo, si usted le hace una mueca,  su niño (a) trata de imitarlo?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_13_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_13_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="emptyQuestionContainer">
                            <td class="emptyQuestionNumber"></td>
                            <td class="emptyQuestion"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td class="emptyAnswerLabel"></td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">14.</td>
                            <td class="questionBold">&#191;Responde su niño (a) a su nombre cuando lo(a) llaman?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_14_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_14_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">15.</td>
                            <td class="question">&#191;Si usted señala a un juguete que est&#225;al otro lado de la habitaci&#243;n a su niño (a), lo mira?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_15_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_15_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">16.</td>
                            <td class="questionBold">&#191;Camina su niño (a)?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_16_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_16_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">17.</td>
                            <td class="question">&#191;Presta su niño (a) atenci&#243;n a las cosas que usted est&#225;mirando?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_17_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_17_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">18.</td>
                            <td class="questionBold">&#191;Hace su niño (a) movimientos raros con los dedos cerca de su cara?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_18_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_18_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">19.</td>
                            <td class="question">&#191;Trata su niño (a) de llamar su atenci&#243;n (de sus padres) a las actividades que estada llevando a cabo?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_19_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_19_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="emptyQuestionContainer">
                            <td class="emptyQuestionNumber"></td>
                            <td class="emptyQuestion"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td class="emptyAnswerLabel"></td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">20.</td>
                            <td class="questionBold">&#191;Se ha preguntado alguna vez si su niño (a) es sordo (a)?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_20_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_20_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">21.</td>
                            <td class="question">&#191;Comprende su niño (a) lo que otras dicen?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_21_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_21_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumberBold">22.</td>
                            <td class="questionBold">&#191;Ha notado si su niño (a) se queda con una Mirada fija en nada, o si camina algunas veces sin sentido?</td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_22_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabelBold">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_22_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr class="emptyQuestionContainer">
                            <td class="emptyQuestionNumber"></td>
                            <td class="emptyQuestion"></td>
                            <td class="emptyAnswerLabel"></td>
                            <td class="emptyAnswerLabel"></td>
                        </tr>
                        <tr class="questionContainer">
                            <td class="questionNumber">23.</td>
                            <td class="question">&#191;Su niño le mira a su cara para chequear su reacci&#243;n cuando esta en una situaci&#243;n diferente?</td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_23_sp']/Value = '1'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answerLabel">
                                <xsl:choose>
                                    <xsl:when test="Records/Record/Field[@id = 'Choice_23_sp']/Value = '0'">
                                        <input type="radio" disabled="disabled" checked="checked"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input type="radio" disabled="disabled"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                    </table>
                    <div class="copyrightContainerSpanish"><p>&#0169; 1999 Diana Robins, Deborah Fein, &#38; Marianne Barton</p></div>
                </div>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>