<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:require
    allPrivileges="Manage CHICA"
    otherwise="/login.htm" redirect="/module/chica/chicaNoteTester.form" />
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />

<form name="input" action="chicaNoteTester.form" method="get">
<p>
Please enter the patient's mrn:
</p>
<input type="text" name="mrn" value="${lastMRN}"/>
<input type="submit" value="Get Note">
</form>
<p>
Note for <b>${lastMRN}</b> was:
</p><br/><br/>
<c:if test="${!empty note}">

<pre>
${note}
</pre>

</c:if>


<%@ include file="/WEB-INF/template/footer.jsp" %>