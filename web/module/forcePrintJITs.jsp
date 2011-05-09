<%@ include file="/WEB-INF/template/include.jsp"%>

<page height="100%">
<link
	href="${pageContext.request.contextPath}/moduleResources/chica/chica.css"
	type="text/css" rel="stylesheet" />


<form height="100%" name="input" method="post">
<p>Please choose the form you would like to print for: <b>${patientName}</b></p>

<table>
<tr>
<td><select name="options"">
<c:if test="${isASQInterventionLocation}">
<option>ASQ</option>
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
</page>
