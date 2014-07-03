<%@ include file="/WEB-INF/template/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html  xmlns="http://www.w3.org/1999/xhtml">
<head>
<link
	href="${pageContext.request.contextPath}/moduleResources/chica/chica.css"
	type="text/css" rel="stylesheet" />
<script type="text/javascript">
<!-- borrowed from http://dadabase.de/dev/window_resizer.html -->
function setSize(width,height) {
	if (window.outerWidth) {
		window.outerWidth = width;
		window.outerHeight = height;
	}
	else if (window.resizeTo) {
		window.resizeTo(width,height);
	}
	window.moveTo(50,50);
}
</script>		
</head>
<body  style="scrollbars:no"onload="javascript:setSize(800,475); <c:if test="${!empty checkinPatient}"> javascript:useConfirmationForm();</c:if>" onkeydown="if (event.keyCode==8) {event.keyCode=0; return event.keyCode }">

<form height="100%" name="input" method="post">
<p>Please choose the form you would like to print for: <b>${patientName}</b></p>

<table>
<tr>
<td><select name="options"">
<c:if test="${isASQInterventionLocation}">
<option>ASQ</option>
<option>ASQ Activity Sheet</option>
</c:if>
<c:forEach items="${printableJits}" var="printableJit">
<option value="${printableJit.formId}">${printableJit.displayName}</option>
</c:forEach>
</select></td>
<td><input type="submit" value="OK" tabindex="2"/></td>
<td><input type="button" value="Cancel" onclick='window.close()' tabindex="3"/></td>
</tr>
</table>
<input type="hidden" value="${patientId}" name="patientId" />
<input type="hidden" value="${sessionId}" name="sessionId" />
</form>
 <p><b><c:out value="${resultMessage}"/></b></p>
</body>
</page>
