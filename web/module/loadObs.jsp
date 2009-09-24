<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />

<form name="input" action="loadObs.form" method="get">
Please enter a start date for the obs load (MM/DD/YYYY): <input type="text" name="minEncounterTime"/>  (optional)<br>
Please enter an end date for the obs load (MM/DD/YYYY): <input type="text" name="maxEncounterTime"/>   (optional)<br>
<input type="hidden" name="loadObs" value="true"/>
<input type="submit" value="Load Obs">
</form>


<%@ include file="/WEB-INF/template/footer.jsp" %>