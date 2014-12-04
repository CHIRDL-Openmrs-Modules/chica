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
                    
                    .answerLabelContainer {
                        width:600px;
                        height:20px;
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
                    .emptyAnswerLabel {
                        width:30px;
                        min-height:10px;
                        text-align:center;
                        display: inline-block;
                    }
                    
                    .emptyAnswerLabel {
                        min-height:5px;
                        height:5px;
                    }
                    
                    .questionContainer,
                    .emptyQuestionContainer {
                        width:600px;
                        min-height:22px;
                        display: inline-block;
                    }
                    
                    .emptyQuestionContainer {
                        min-height:5px;
                        height:5px;
                    }
                    
                    .questionNumber,
                    .questionNumberBold,
                    .emptyQuestionNumber {
                        width:28px;
                        height:7px;
                        margin-left:25px;
                        display: inline-block;
                    }
                    .emptyQuestionNumber {
                        height:5px;
                    }
                    
                    .questionNumberBold {
                        font-weight:bold;
                    }
                    
                    .question,
                    .questionBold,
                    .emptyQuestion {
                        width:452px;
                        min-height:7px;
                        display: inline-block;
                    }
                    
                    .emptyQuestion {
                        min-height:5px;
                        height:5px;
                    }
                    
                    .questionBold {
                        font-weight:bold;
                    }
                    
                    .answerContainer,
                    .emptyAnswerContainer {
                        width:70px;
                        min-height:7px;
                        display: inline-block;
                    }
                    
                    .emptyAnswerContainer {
                        min-height:5px;
                        height:5px;
                    }
                    
                    .radioAnswer,
                    .emptyRadioAnswer {
                        min-height:7px;
                        display: inline-block;
                    }
                    
                    .emptyRadioAnswer {
                        min-height:5px;
                        height:5px;
                    }
                    
                    .copyrightContainer,
                    .copyrightContainerSpanish {
                        width:600px;
                        text-align:left;
                        margin-left:25px;
                        margin-top: 50px;
                    }
                    .copyrightContainerSpanish {
                        margin-top: 22px;
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
                    <div class="answerLabelContainer">
                        <div class="answerEmptyContainer">&#160;</div>
                        <div class="answerDataContainer">
                            <div class="answerLabel">Yes</div>
                            <div class="answerLabel">No</div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">1.</div>
                        <div class="question">Does your child enjoy being swung, bounced on your knee, etc.?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">2.</div>
                        <div class="questionBold">Does your child take an interest in other children?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">3.</div>
                        <div class="question">Does your child like climbing on things, such as up stairs?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">4.</div>
                        <div class="questionBold">Does your child enjoy playing peek-a-bool/hide-and-seek?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">5.</div>
                        <div class="question">Does your child ever pretend, for example, to talk on the phone or take care of a doll or pretend other things?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">6.</div>
                        <div class="questionBold">Does your child ever use his/her index finger to point, to ask for something?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">7.</div>
                        <div class="question">Does your child ever use his/her index finger to point, to indicate interest in something?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">8.</div>
                        <div class="questionBold">Can your child play properly with small toys (e.g. cars or blocks) without just mouthing, fiddling, or dropping them?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">9.</div>
                        <div class="question">Does your child ever bring objects over to you (parent) to show you something?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="emptyQuestionContainer">
                        <div class="emptyQuestionNumber">&#160;</div>
                        <div class="emptyQuestion">&#160;</div>
                        <div class="emptyAnswerContainer">
                            <div class="emptyAnswerLabel">&#160;</div>
                            <div class="emptyAnswerLabel">&#160;</div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">10.</div>
                        <div class="questionBold">Does your child ever look you in the eye for more than a second or two?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">11.</div>
                        <div class="question">Does your child ever seem oversensitive to noise? (e.g. plugging ears)</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">12.</div>
                        <div class="questionBold">Does your child smile in response to your face or your smile?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">13.</div>
                        <div class="question">Does your child imitate you? (e.g. you make a face-will your child imitate it?)</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="emptyQuestionContainer">
                        <div class="emptyQuestionNumber">&#160;</div>
                        <div class="emptyQuestion">&#160;</div>
                        <div class="emptyAnswerContainer">
                            <div class="emptyAnswerLabel">&#160;</div>
                            <div class="emptyAnswerLabel">&#160;</div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">14.</div>
                        <div class="questionBold">Does your child respond to his/her name when you call?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">15.</div>
                        <div class="question">If you point at a toy across the room, does your child look at it?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">16.</div>
                        <div class="questionBold">Does your child walk?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">17.</div>
                        <div class="question">Does your child look at things you are looking at?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">18.</div>
                        <div class="questionBold">Does your child make unusual finger movements near his/her face?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">19.</div>
                        <div class="question">Does your child try to attract your attention to his/her own activity?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="emptyQuestionContainer">
                        <div class="emptyQuestionNumber">&#160;</div>
                        <div class="emptyQuestion">&#160;</div>
                        <div class="emptyAnswerContainer">
                            <div class="emptyAnswerLabel">&#160;</div>
                            <div class="emptyAnswerLabel">&#160;</div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">20.</div>
                        <div class="questionBold">Have you ever wondered if your child is deaf?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">21.</div>
                        <div class="question">Does your child understand what people say?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">22.</div>
                        <div class="questionBold">Does your child sometimes stare at nothing or wander with no purpose?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="emptyQuestionContainer">
                        <div class="emptyQuestionNumber">&#160;</div>
                        <div class="emptyQuestion">&#160;</div>
                        <div class="emptyAnswerContainer">
                            <div class="emptyAnswerLabel">&#160;</div>
                            <div class="emptyAnswerLabel">&#160;</div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">23.</div>
                        <div class="question">Does your child look at your face to check your reaction when faced with something unfamiliar?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
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
                    <div class="answerLabelContainer">
                        <div class="answerEmptyContainer">&#160;</div>
                        <div class="answerDataContainer">
                            <div class="answerLabel">Yes</div>
                            <div class="answerLabel">No</div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">1.</div>
                        <div class="question">&#191;Disfruta su niño (a) cuando lo balancean o hacen saltar sobre su rodilla?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">2.</div>
                        <div class="questionBold">&#191;Se interesa su niño (a) en otros niños?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">3.</div>
                        <div class="question">&#191;Le gusta a su niño (a) subirse a las cosas, por ejemplo subir las escaleras?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">4.</div>
                        <div class="questionBold">&#191;Disfruta su niño (a) jugando "peek-a-boo" o "hide and seek" (a las escondidas)?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">5.</div>
                        <div class="question">&#191;Le gusta a su niño (a) jugar a pretendar, como por ejemplo, pretende que habla por tel&#233;fono, que cuida sus muñecas, o pretende otras cosas?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">6.</div>
                        <div class="questionBold">&#191;Utiliza su niño (a) su dedo &#237;ndice para señalar algo, o para preguntar alguna cosa?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">7.</div>
                        <div class="question">&#191;Usa su niño (a) su dedo &#237;ndice para señalar o indicar inter&#233;s en algo?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">8.</div>
                        <div class="questionBold">&#191;Puede su niño (a) jugar bien con jugetes pequeños (como carros o cubos) sin llevárselos a la boca, manipularlos o dejarlos caer)?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">9.</div>
                        <div class="question">&#191;Le trae su niño (a) a usted (padre o madre) objetos o cosas, con el prop&#243;sito de mostrarle algo alguna vez?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="emptyQuestionContainer">
                        <div class="emptyQuestionNumber">&#160;</div>
                        <div class="emptyQuestion">&#160;</div>
                        <div class="emptyAnswerContainer">
                            <div class="emptyAnswerLabel">&#160;</div>
                            <div class="emptyAnswerLabel">&#160;</div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">10.</div>
                        <div class="questionBold">&#191;Lo mira su niño (a) directamente a los ojos por mas de uno o dos segundos?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">11.</div>
                        <div class="question">&#191;Parece su niño (a) ser demasiado sensitivo al ruido? (por ejemplo, se tapa los oidos)?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">12.</div>
                        <div class="questionBold">&#191;Sonrie su niño (a) en respuesta a su cara o a su sonrisa?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">13.</div>
                        <div class="question">&#191;Lo imita su niño (a)? Por ejemplo, si usted le hace una mueca,  su niño (a) trata de imitarlo?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="emptyQuestionContainer">
                        <div class="emptyQuestionNumber">&#160;</div>
                        <div class="emptyQuestion">&#160;</div>
                        <div class="emptyAnswerContainer">
                            <div class="emptyAnswerLabel">&#160;</div>
                            <div class="emptyAnswerLabel">&#160;</div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">14.</div>
                        <div class="questionBold">&#191;Responde su niño (a) a su nombre cuando lo(a) llaman?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">15.</div>
                        <div class="question">&#191;Si usted señala a un juguete que est&#225;al otro lado de la habitaci&#243;n a su niño (a), lo mira?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">16.</div>
                        <div class="questionBold">&#191;Camina su niño (a)?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">17.</div>
                        <div class="question">&#191;Presta su niño (a) atenci&#243;n a las cosas que usted est&#225;mirando?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">18.</div>
                        <div class="questionBold">&#191;Hace su niño (a) movimientos raros con los dedos cerca de su cara?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">19.</div>
                        <div class="question">&#191;Trata su niño (a) de llamar su atenci&#243;n (de sus padres) a las actividades que estada llevando a cabo?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="emptyQuestionContainer">
                        <div class="emptyQuestionNumber">&#160;</div>
                        <div class="emptyQuestion">&#160;</div>
                        <div class="emptyAnswerContainer">
                            <div class="emptyAnswerLabel">&#160;</div>
                            <div class="emptyAnswerLabel">&#160;</div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">20.</div>
                        <div class="questionBold">&#191;Se ha preguntado alguna vez si su niño (a) es sordo (a)?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">21.</div>
                        <div class="question">&#191;Comprende su niño (a) lo que otras dicen?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumberBold">22.</div>
                        <div class="questionBold">&#191;Ha notado si su niño (a) se queda con una Mirada fija en nada, o si camina algunas veces sin sentido?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="emptyQuestionContainer">
                        <div class="emptyQuestionNumber">&#160;</div>
                        <div class="emptyQuestion">&#160;</div>
                        <div class="emptyAnswerContainer">
                            <div class="emptyAnswerLabel">&#160;</div>
                            <div class="emptyAnswerLabel">&#160;</div>
                        </div>
                    </div>
                    <div class="questionContainer">
                        <div class="questionNumber">23.</div>
                        <div class="question">&#191;Su niño le mira a su cara para chequear su reacci&#243;n cuando esta en una situaci&#243;n diferente?</div>
                        <div class="answerContainer">
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                            <div class="answerLabel">
                                <input class="radioAnswer" type="radio"></input>
                            </div>
                        </div>
                    </div>
                    <div class="copyrightContainerSpanish"><p>&#0169; 1999 Diana Robins, Deborah Fein, &#38; Marianne Barton</p></div>
                </div>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>