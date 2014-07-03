<%@ include file="/WEB-INF/template/include.jsp"%>

<page height="100%">
<link
    href="${pageContext.request.contextPath}/moduleResources/chica/chica.css"
    type="text/css" rel="stylesheet" />
<script type="text/javascript">

function printSelection(node){

  var content=node.innerHTML
  var pwin=window.open('','print_content','width=300,height=300');

  pwin.document.open();
  pwin.document.write('<html><body onload="window.print()">'+content+'</body></html>');
  pwin.document.close();
 
  setTimeout(function(){pwin.close();},1000);

}
</script>

    <table width="100%" class="displayTiffHeader chicaBackground">
        <tr>
            <td width="20%">
             <INPUT TYPE="button" class="exitButton" VALUE="Exit" onClick="history.go(-1);return true;">
            </td>
            <td width="30%" class="displayLeftTiffHeaderSegment">
                <c:choose>
	                <c:when test= "${!empty leftImageFormname}">
	                    <b>${leftImageFormname}:&nbsp;${leftImageForminstance}</b>
	                </c:when>
	                <c:otherwise>
	                    N/A
	                </c:otherwise>
                </c:choose>
            </td>
            <td width="50%" class="displayRighttiffHeaderSegment">
                <c:choose>
	                <c:when test= "${!empty rightImageFormname}">
	                    <b>${rightImageFormname}:&nbsp;${rightImageForminstance}</b>
	                </c:when>
	                <c:otherwise>
	                    N/A
	                </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </table>
    <table width="100%">
        <tr>
            <td width="50%">
                <button type="button" onclick="printSelection(document.getElementById('divLeft'));return false">Print</button>
            </td>
            <td width="50%" style="BORDER-LEFT: black solid 1px;">
                <button type="button" onclick="printSelection(document.getElementById('divRight'));return false">Print</button>
            </td>
        </tr>
    </table>
    <c:choose>
        <c:when test="${error != null}">
            <font color="red"><c:out value="${error}"/></font>
        </c:when>
        <c:otherwise>
            <table height="90%" width="100%" style="border:1px solid black; empty-cells:show; border-collapse:collapse">
                <tr width="100%">
                    <td width="50%">
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
                    <td width="50%">
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
                </tr>
            </table>
        </c:otherwise>
    </c:choose>
</page>
