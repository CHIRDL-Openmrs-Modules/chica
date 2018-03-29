<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
<div id="painAndAllergiesDiv">
	<c:choose>
    	<c:when test="${Pain == '0'}"> 
        	<div class="">
        </c:when>
        <c:otherwise>
        	<div class="ui-state-highlight">
        </c:otherwise>
        </c:choose>
			Pain (0-10):<c:out value="${Pain}"/>
        	</div>
        <c:choose>
        	<c:when test="${Allergy == ' NONE'}"> 
            	<div class="examExtraData">
           	</c:when>
        	<c:otherwise>
            	<div class="ui-state-highlight">
            </c:otherwise>
        </c:choose>
        	Allergies:<c:out value="${Allergy}"/>
        	</div>
</div>