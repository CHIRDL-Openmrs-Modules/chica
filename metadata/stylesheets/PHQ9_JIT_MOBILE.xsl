<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>
    
    <xsl:template match="/">
        <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html></xsl:text>
        
        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
                <title>Untitled Document</title>
                <style>
                    body {
                    font-family:Verdana, Geneva, sans-serif;
                    font-size:11px;
                    }
                    
                    table {
                    border-right:solid 1px black;
                    border-bottom:solid 1px black;
                    border-collapse: separate;
                    border-spacing: 0px 0px;
                    }
                    
                    table th {
                    background-color: #000;
                    color:#FFF;
                    width: 120px;
                    padding:5px;
                    border-left: solid 1px white;
                    border-bottom: solid 1px black;
                    border-right: solid 1px black;
                    }
                    
                    table td {
                    border:solid 1px black;
                    padding:10px;
                    }
                    
                    #container,
                    #title,
                    #patientName,
                    #score,
                    #scoreValue {
                    width:800px;
                    }
                    
                    #container {
                    margin:0 auto;
                    }
                    
                    #patientName {
                    border-bottom:solid 1px; black;
                    margin-bottom:40px;
                    }
                    
                    #score {
                    padding-top:40px;
                    }
                    
                    .scoreValue {
                    font-weight:bold;
                    padding-top:5px;
                    }
                    
                    .emptyHeader {
                    background-color: #FFF;
                    border-right:solid 1px white;
                    }
                    
                    .questionBlock {
                    width:400px;
                    }
                    
                    .answer {
                    text-align: center;
                    font-weight:bold;
                    }
                </style>
            </head>
            <body>
                <div id="container">
                    <div id="title"><h2>PHQ-9 For Teens</h2></div>
                    <div id="patientName">Name: <xsl:value-of select="Records/Record/Field[@id = 'PatientName']"/></div>
                    <table id="phq9Table">
                        <tr>
                            <th class="emptyHeader">X</th>
                            <th>(0)<br/>Not At<br/>All</th>
                            <th>(1)<br/>Several<br/>Days</th>
                            <th>(2)<br/>More Than<br/>Half the Days</th>
                            <th>(3)<br/>Nearly<br/>Every Day</th>
                        </tr>
                        <tr>
                            <td class="questionBlock">1. <xsl:value-of select="Records/Record/Field[@id = 'PHQ9Question_1']"/></td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_1']/Value = '0') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_1_2']/Value = '0')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_1']/Value = '1') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_1_2']/Value = '1')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_1']/Value = '2') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_1_2']/Value = '2')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_1']/Value = '3') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_1_2']/Value = '3')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td> 
                        </tr>
                        <tr>
                            <td class="questionBlock">2. <xsl:value-of select="Records/Record/Field[@id = 'PHQ9Question_2']"/></td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_2']/Value = '0') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_2_2']/Value = '0')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_2']/Value = '1') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_2_2']/Value = '1')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_2']/Value = '2') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_2_2']/Value = '2')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_2']/Value = '3') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_2_2']/Value = '3')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td> 
                        </tr>
                        <tr>
                            <td class="questionBlock">3. <xsl:value-of select="Records/Record/Field[@id = 'PHQ9Question_3']"/></td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_3']/Value = '0') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_3_2']/Value = '0')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_3']/Value = '1') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_3_2']/Value = '1')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_3']/Value = '2') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_3_2']/Value = '2')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_3']/Value = '3') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_3_2']/Value = '3')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td> 
                        </tr>
                        <tr>
                            <td class="questionBlock">4. <xsl:value-of select="Records/Record/Field[@id = 'PHQ9Question_4']"/></td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_4']/Value = '0') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_4_2']/Value = '0')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_4']/Value = '1') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_4_2']/Value = '1')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_4']/Value = '2') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_4_2']/Value = '2')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_4']/Value = '3') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_4_2']/Value = '3')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td> 
                        </tr>
                        <tr>
                            <td class="questionBlock">5. <xsl:value-of select="Records/Record/Field[@id = 'PHQ9Question_5']"/></td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_5']/Value = '0') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_5_2']/Value = '0')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_5']/Value = '1') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_5_2']/Value = '1')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_5']/Value = '2') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_5_2']/Value = '2')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_5']/Value = '3') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_5_2']/Value = '3')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td> 
                        </tr>
                        <tr>
                            <td class="questionBlock">6. <xsl:value-of select="Records/Record/Field[@id = 'PHQ9Question_6']"/></td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_6']/Value = '0') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_6_2']/Value = '0')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_6']/Value = '1') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_6_2']/Value = '1')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_6']/Value = '2') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_6_2']/Value = '2')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_6']/Value = '3') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_6_2']/Value = '3')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td> 
                        </tr>
                        <tr>
                            <td class="questionBlock">7. <xsl:value-of select="Records/Record/Field[@id = 'PHQ9Question_7']"/></td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_7']/Value = '0') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_7_2']/Value = '0')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_7']/Value = '1') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_7_2']/Value = '1')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_7']/Value = '2') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_7_2']/Value = '2')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_7']/Value = '3') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_7_2']/Value = '3')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td> 
                        </tr>
                        <tr>
                            <td class="questionBlock">8. <xsl:value-of select="Records/Record/Field[@id = 'PHQ9Question_8']"/></td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_8']/Value = '0') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_8_2']/Value = '0')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_8']/Value = '1') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_8_2']/Value = '1')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_8']/Value = '2') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_8_2']/Value = '2')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_8']/Value = '3') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_8_2']/Value = '3')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td> 
                        </tr>
                        <tr>
                            <td class="questionBlock">9. <xsl:value-of select="Records/Record/Field[@id = 'PHQ9Question_9']"/></td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_9']/Value = '0') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_9_2']/Value = '0')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_9']/Value = '1') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_9_2']/Value = '1')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_9']/Value = '2') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_9_2']/Value = '2')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td class="answer">
                                <xsl:choose>
                                    <xsl:when test="(Records/Record/Field[@id = 'PHQ9QuestionEntry_9']/Value = '3') or (Records/Record/Field[@id = 'PHQ9QuestionEntry_9_2']/Value = '3')">X</xsl:when>
                                    <xsl:otherwise>&#160;</xsl:otherwise>
                                </xsl:choose>
                            </td> 
                        </tr>
                    </table>
                    <div id="score">
                        <div class="scoreValue">Total Score: <xsl:value-of select="Records/Record/Field[@id = 'PHQ9Score']/Value"/></div>
                        <div class="scoreValue">Depression Level:&#160;
                            <xsl:variable name="interp" select="Records/Record/Field[@id = 'PHQ9Interpretation']/Value"/>
                            <xsl:choose>
                                <xsl:when test="(not($interp)) or ($interp = '')">
                                    N/A
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$interp"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </div>
                    </div>
                </div>
            </body>
        </html>
        
    </xsl:template>
</xsl:stylesheet>