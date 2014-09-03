<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />

<form name="input" action="parseDictionary.form" method="get">
<input type="hidden" name="parse" value="true"/>
<input type="submit" value="Parse">
</form>


<%@ include file="/WEB-INF/template/footer.jsp" %>