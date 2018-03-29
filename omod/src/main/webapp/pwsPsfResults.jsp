<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
<div class="psf_container">
    <c:set var="psfProcessedFlag" value="" />
    <c:if test="${psfSubmitted == 'false'}">
        <c:set var="psfProcessedFlag" value=" (Awaiting)" />
    </c:if>
    <header id="psf_results_header">
        <a href="">
            <h4 class="logo">Pre-screening Results<c:out value="${psfProcessedFlag}" /></h4>
        </a>
        <a href="#">
            <div class="ui-icon ui-icon-minusthick white"></div>
        </a>
    </header>
    <section class="psf_results" id="psf_results">
        <div class="psf_results_container">
	        <c:choose>
	            <c:when test="${empty psfResults}">
	                <pre>Pre-screening questions have not been answered.</pre>
	            </c:when>
	            <c:otherwise>
	                <pre><c:out value="${psfResults}" /></pre>
	            </c:otherwise>
	        </c:choose>
        </div>
    </section>
</div>