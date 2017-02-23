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
            <span class="psf_results_text">
                <c:choose>
                    <c:when test="${empty psfResults}">
                        Pre-screening questions concerning the patient have not been answered.
                    </c:when>
                    <c:otherwise>
                        <c:out value="${psfResults}" />
                    </c:otherwise>
                </c:choose>
            </span>
        </div>
    </section>
</div>