<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients" otherwise="/login.htm" redirect="/module/chica/displayTiff.form" />
<html>
<head>
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/moduleResources/chica/displayTiff.css" type="text/css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/displayTiff.js"></script>
<title>Encounter Forms</title>
</head>
<body>
<div class="main_container">
    <div class="form_info_container chicaBackground">
        <div class="row_container">
            <div class="cell_container">
                <div class="table_container">
                   <div class="row_container">
                        <div class="cell_container cell_padding">
                            <a href="#" id="exitButton" onclick="history.go(-1);return false;" class="icon-button-medium ui-state-default ui-corner-all"><span class="ui-icon ui-icon-arrowreturnthick-1-w"></span>Exit</a>
                        </div>
                        <div class="cell_container displayLeftTiffHeaderSegment">
                            <c:if test="${!empty leftImageFormname}">
                                <b>${leftImageFormname}:&nbsp;${leftImageForminstance}</b>
                            </c:if> 
                            <c:if test="${empty leftImageFormname}">
                                N/A
                            </c:if>
                       </div>
                   </div>
                </div>
            </div>
            <div class="cell_container">
                <div class="table_container chicaBackground">
                    <div class="row_container">
                        <div class="cell_container displayRighttiffHeaderSegment">
                            <c:if test="${!empty rightImageFormname}">
                                <b>${rightImageFormname}:&nbsp;${rightImageForminstance}</b>
                            </c:if> 
                            <c:if test="${empty rightImageFormname}">
                                N/A
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="forms_container">
        <div class="forms_row_container">
            <div class="forms_row_cell_container">
                <c:choose>
                    <c:when test="${empty leftHtmlOutput}">
	                    <iframe id="left_pdf_display" class="form_pdf_object" src="${pageContext.request.contextPath}${leftImagefilename}">
	                       <span class="pdf_error">It appears your Web browser is not configured to display PDF files. 
	                       <a class="link" href='http://get.adobe.com/reader/'>Click here to download the Adobe PDF Reader.</a>  Please restart your browser once the installation is complete.</span>
	                    </iframe>
                    </c:when>
                    <c:otherwise>
                        <div class="print_table_container">
                            <div id="divLeft" class="row_container">
                                <div class="cell_container">
                                    <div class="row_container">
		                                <div class="cell_container cell_padding">
		                                    <a href="#" id="printLeftButton" onclick="printSelection(document.getElementById('divLeft'));return false;" class="icon-button-medium ui-state-default ui-corner-all"><span class="ui-icon ui-icon-print"></span>Print</a>
		                                </div>
		                            </div>
                                    <iframe id="left_html_display" class="form_html_object" src="${pageContext.request.contextPath}${leftHtmlOutput}">
			                           <span class="pdf_error">It appears your Web browser is not configured to display PDF files. 
			                           <a class="link" href='http://get.adobe.com/reader/'>Click here to download the Adobe PDF Reader.</a>  Please restart your browser once the installation is complete.</span>
			                        </iframe>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="forms_row_cell_container">
                <c:choose>
                    <c:when test="${empty rightHtmlOutput}">
	                    <iframe id="right_pdf_display" class="form_pdf_object" src="${pageContext.request.contextPath}${rightImagefilename}">
	                       <span class="pdf_error">It appears your Web browser is not configured to display PDF files. 
	                       <a class="link" href='http://get.adobe.com/reader/'>Click here to download the Adobe PDF Reader.</a>  Please restart your browser once the installation is complete.</span>
	                    </iframe>
                    </c:when>
                    <c:otherwise>
                       <div class="print_table_container">
                           <div id="divRight" class="row_container">
                               <div class="cell_container">
                                <div class="row_container">
	                               <div class="cell_container cell_padding">
	                                   <a href="#" id="printRightButton" onclick="printSelection(document.getElementById('divRight'));return false;" class="icon-button-medium ui-state-default ui-corner-all"><span class="ui-icon ui-icon-print"></span>Print</a>
	                               </div>
	                            </div>
                                <iframe id="right_html_display" class="form_html_object" src="${pageContext.request.contextPath}${rightHtmlOutput}">
                                   <span class="pdf_error">It appears your Web browser is not configured to display PDF files. 
                                   <a class="link" href='http://get.adobe.com/reader/'>Click here to download the Adobe PDF Reader.</a>  Please restart your browser once the installation is complete.</span>
                                </iframe>
                               </div>
                           </div>
                       </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    <input id="leftImageLocation" name="leftImageLocation" type="hidden" value="${leftImagefilename}"/>
    <input id="rightImageLocation" name="rightImageLocation" type="hidden" value="${rightImagefilename}"/>
    <input id="leftHtmlLength" name="leftHtml" type="hidden" value="${fn:length(leftHtmlOutput)}"/>
    <input id="rightHtmlLength" name="rightHtml" type="hidden" value="${fn:length(rightHtmlOutput)}"/>
</div>
</body>
</html>








