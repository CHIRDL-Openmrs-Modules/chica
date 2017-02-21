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
            <span class="patient_name">
                    <c:out value="${psfResults}" />
                </span>
        </div>
    </section>
</div>