<%@ page import="org.openmrs.web.WebConstants"%>
<%
    pageContext.setAttribute("redirect", session.getAttribute(WebConstants.OPENMRS_LOGIN_REDIRECT_HTTPSESSION_ATTR));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/loginMobile.js"></script>
</head>
<body style="font-size: 20px">

    <div id="login_mobile" data-url="login_mobile" data-role="page" data-theme="b">
        <div data-role="header">
            <h1>CHICLET Login</h1>
        </div>

        <div data-role="content">
            <form id="loginForm" method="POST" action="loginMobile.form">
                <div data-role="fieldcontain" class="ui-hide-label">
                    <label for="username_field">Username</label> 
                    <input type="text" name="username_field" id="username_field" value="" placeholder="Username"/>
                </div>

                <div data-role="fieldcontain" class="ui-hide-label">
                    <label for="password_field">Password</label> 
                    <input type="password" name="password_field" id="password_field" value="" placeholder="Password"/>
                </div>
                <div id="invalidLogin" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
			        <div data-role="header" data-theme="b">
			            <h1>Login Error</h1>
			        </div>
			        <div data-role="content">
			            <div id="loginResultDiv"></div>
			            <div style="margin: 0 auto;text-align: center;">
			                <a href="#" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">OK</a>
			            </div>
			        </div>
			    </div>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="#" id="login_button" data-role="button" data-theme="b" data-inline="true" style="width: 150px;">Login</a>
                </div>
                <c:choose>
                    <c:when test="${redirect != null}">
                        <input type="hidden" name="redirect" value="${redirect}" />
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="redirect" value="" />
                    </c:otherwise>
                </c:choose>
                <c:if test="${errorMessage != null}">
                    <script type="text/javascript">
                    var resultDiv = document.getElementById('loginResultDiv');
                    resultDiv.innerHTML = '${errorMessage}';
                    </script>
                </c:if>
            </form>
            <div id="loadingDialog" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
		        <div data-role="content">
		            <div style="margin: 0 auto;text-align: center;">
		                Loading...
		            </div>
		        </div>
            </div>
        </div>
    </div>

</body>
</html>
