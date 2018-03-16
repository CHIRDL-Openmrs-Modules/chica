<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/displayScanForm.form" />
<html>
<head>
<link
    href="${pageContext.request.contextPath}/moduleResources/chica/chica.css"
    type="text/css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
<script type="text/javascript">
$(function() {
    $("#exitButton").button();
    $("#printLeftButton").button();
    $("#printRightButton").button();
});

function printSelection(node){

  var content=node.innerHTML
  var pwin=window.open('','print_content','width=300,height=300');

  pwin.document.open();
  pwin.document.write('<html><body onload="window.print()">'+content+'</body></html>');
  pwin.document.close();
 
  setTimeout(function(){pwin.close();},1000);

}
</script>
<style>
html {
    height: 100%;
    width: 100%;
}

body {
    height: 100%;
    width: 100%;
    padding: 0px;
    margin: 0px;
    font-size: 12px;
}
</style>
</head>
<body>
    <div style="height:100%;overflow:scroll;background-color:#F5FBEF;">
    <c:set var="leftHeaderSize" value="30%"/>
    <c:set var="rightHeaderSize" value="50%"/>
    <c:set var="leftPrintSize" value="50%"/>
    <c:set var="rightPrintSize" value="50%"/>
    <c:if test="${(empty leftImageFormname) && (!empty rightImageFormname)}">
        <c:set var="leftHeaderSize" value="0%"/>
        <c:set var="rightHeaderSize" value="100%"/>
        <c:set var="leftPrintSize" value="0%"/>
    <c:set var="rightPrintSize" value="100%"/>
    </c:if>
    <c:if test="${(!empty leftImageFormname) && (empty rightImageFormname)}">
        <c:set var="leftHeaderSize" value="100%"/>
        <c:set var="rightHeaderSize" value="0%"/>
        <c:set var="leftPrintSize" value="100%"/>
    <c:set var="rightPrintSize" value="0%"/>
    </c:if>

    <table width="100%" class="displayTiffHeader chicaBackground">
        <tr>
            <td width="20%">
             <a href="#" id="exitButton" onclick="history.go(-1);return true;" class="icon-button-medium ui-state-default ui-corner-all"><span class="ui-icon ui-icon-arrowreturnthick-1-w"></span>Exit</a>
            </td>
            <c:if test="${leftHeaderSize ne '0%' }">
	            <td width='<c:out value="${leftHeaderSize}"/>' class="displayLeftTiffHeaderSegment">
	                <c:choose>
	                    <c:when test= "${!empty leftImageFormname}">
	                        <b>${leftImageFormname}:&nbsp;${leftImageForminstance}</b>
	                    </c:when>
	                    <c:otherwise>
	                        N/A
	                    </c:otherwise>
	                </c:choose>
	            </td>
            </c:if>
            <c:if test="${rightHeaderSize ne '0%' }">
	            <td width='<c:out value="${rightHeaderSize}"/>' class="displayRighttiffHeaderSegment">
	                <c:choose>
	                    <c:when test= "${!empty rightImageFormname}">
	                        <b>${rightImageFormname}:&nbsp;${rightImageForminstance}</b>
	                    </c:when>
	                    <c:otherwise>
	                        N/A
	                    </c:otherwise>
	                </c:choose>
	            </td>
            </c:if>
        </tr>
    </table>
    <table width="100%">
        <tr>
            <c:if test="${leftPrintSize ne '0%' }">
	            <td width='<c:out value="${leftPrintSize}"/>'>
	                <a href="#" id="printLeftButton" onclick="printSelection(document.getElementById('divLeft'));return false;" class="icon-button-medium ui-state-default ui-corner-all"><span class="ui-icon ui-icon-print"></span>Print</a>
	            </td>
            </c:if>
            <c:if test="${rightPrintSize ne '0%' }">
	            <td width='<c:out value="${rightPrintSize}"/>' style="BORDER-LEFT: black solid 1px;">
	                <a href="#" id="printRightButton" onclick="printSelection(document.getElementById('divRight'));return false;" class="icon-button-medium ui-state-default ui-corner-all"><span class="ui-icon ui-icon-print"></span>Print</a>
	            </td>
            </c:if>
        </tr>
    </table>
    <c:choose>
        <c:when test="${error != null}">
            <font color="red"><c:out value="${error}"/></font>
        </c:when>
        <c:otherwise>
            <table height="90%" width="100%" style="border:1px solid black; empty-cells:show; border-collapse:collapse">
                <tr width="100%">
                    <c:if test="${leftPrintSize ne '0%' }">
	                    <td width='<c:out value="${leftPrintSize}"/>'>
	                        <div id="divLeft" style="height:100%; width:100%; position:relative; overflow:scroll">
	                            <c:choose>
	                                <c:when test="${leftOutput == null}">
	                                    <center><font size="32">Document<br/><br/>Not<br/><br/>Available</font></center>
	                                </c:when>
	                                <c:otherwise>
	                                    ${leftOutput}
	                                </c:otherwise>
	                            </c:choose>
	                        </div>
	                    </td>
                    </c:if>
                    <c:if test="${rightPrintSize ne '0%' }">
	                    <td width='<c:out value="${rightPrintSize}"/>'>
	                        <div id="divRight" style="height:100%; width:100%; position:relative; overflow:scroll">
	                            <c:choose>
	                                <c:when test="${rightOutput == null}">
	                                    <center><font size="32">Document<br/><br/>Not<br/><br/>Available</font></center>
	                                </c:when>
	                                <c:otherwise>
	                                    ${rightOutput}
	                                </c:otherwise>
	                            </c:choose>
	                        </div>
	                    </td>
                    </c:if>
                </tr>
            </table>
        </c:otherwise>
    </c:choose>
    </div>
    </body>
</html>
