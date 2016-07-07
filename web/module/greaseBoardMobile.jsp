<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/greaseBoardMobile.form" />
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/browserFixMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.timer.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/greaseBoardMobile.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.throttle-debounce.js"></script>
</head>




<style>

#custom-header .ui-block-a{
	padding-left: 10px;
	text-align:	left;
	width: 25%;
}

#custom-header .ui-block-b{
   text-align: center;  
   width: 50%;
}

#custom-header .ui-block-c{
   text-align: right;
   width: 25%;
}

.refreshButtonDiv{
	width: 125px !important;
	display: inline-block;
	text-align: right;
}

.showAllDiv{
	display: inline-block;
	padding-top: 5px;
}

/*
.divBlockC{
	display: inline-block;
	text-align: right;
}
*/

/*

.toggleShowAll{
	display: inline-block;
	height: 50px;
}
*/

/*
.toggleLabel{
	vertical-align: middle;
}
*/

/*
.toggleShowAllLabel{
	vertical-align: middle;
}
*/

/*
.toggleShowAllInput{
	vertical-align: middle;
}
*/

/*
#patientList{
	margin: 0;
}

#patientList {
   -webkit-border-radius:.0 !important;
   border-radius: 0 !important;
}
*/


</style>





<body>

<div data-role="page" id="patient_list_page" data-theme="b" type="patient_page">
    <div data-role="header" id="custom-header" class="single-line-header" data-theme="a" data-position="fixed">
    
    
    <div class="ui-grid-b">
			<div class="ui-block-a" style="width:25%;">
				<div class="showAllDiv">
						
						<input type="checkbox" data-theme="b" name="showAllCheckbox" id="showAllCheckbox" class="custom" data-mini="true"/>
						<label for="showAllCheckbox">Show all patients</label>
						
						<!--
						<div class="toggleShowAll toggleShowAllLabel"><label>Show all patients:</label></div>
						<div class="toggleShowAll toggleShowAllInput">
							<select name="showAllPatients" id="showAllPatients" data-role="slider">
					            <option value="false">Off</option>
					            <option value="true">On</option>
					        </select>
				        </div>
    					-->
				</div>
				
    		</div>

	
		
			<div class="ui-block-b" style="width:50%;">
				<h3>Patients (${currentUser})</h3>
			</div>	
			
			<div class="ui-block-c" style="width:25%;">
				<div class="refreshButtonDiv">
					<button type="button" id="refreshButton" data-icon="refresh" data-theme="b" onclick="populateList()">Refresh</button>
				</div>
			</div>  
			
			<div class="ui-block-a" style="width:25%;">
			</div>
			
			<div class="ui-block-b" style="width:50%;">
				<div id="searchAllPatientsDIV" data-role="fieldcontain" class="ui-hide-label">
            		<label for="searchAllPatients">Search</label>
            		<input type="search" data-theme="b" name="searchAllPatients" id="searchAllPatients" value="" placeholder="Search by patient name or MRN..." data-corners="false"/>
        		</div>
        	</div>	
        	
			<div class="ui-block-c" style="width:25%;">
			</div>
			 
		</div>
    
    
    	
		
    		
    		
    	
    	 
       
        
        
        
    </div>
    
    
    
    
    		
    			
    
    
    
    <div id="listError" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
        <div data-role="header" data-theme="b">
            <h1>Patient List Error</h1>
        </div>
        <div data-role="content">
            <div id="listErrorResultDiv"></div>
            <div style="margin: 0 auto;text-align: center;">
                <a href="#" data-inline="true" onClick="startTimer()" data-role="button" data-theme="b" style="width: 150px;">OK</a>
            </div>
        </div>
    </div>
    <div id="listLogIn" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
        <div data-role="header" data-theme="b">
            <h1>Login</h1>
        </div>
        <div data-role="content">
            <div id="listLoginResultDiv"></div>
            <div style="margin: 0 auto;text-align: center;">
                <a href="${pageContext.request.contextPath}/module/chica/greaseBoardMobile.form" data-inline="true" rel="external" data-ajax="false" data-role="button" data-theme="b" style="width: 150px;">OK</a>
            </div>
        </div>
    </div>
     <div id="patientContent" data-role="content" role="main">
        <div id="sorter">
            <ul data-role="listview" id="patientList">
            	
            </ul>
        </div>
    </div>
    
    <!--
    <div data-role="content">
	    <ul data-role="listview" id="testList" data-filter="true">
	      <li><a href="index.html">Acura</a></li>
	      <li><a href="index.html">Audi</a></li>
	      <li><a href="index.html">BMW</a></li>
	      <li><a href="index.html">Cadillac</a></li>
	      <li><a href="index.html">Chrysler</a></li>
	    </ul>
  	</div>
  	-->
  
    <div id="loadingDialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
        <div data-role="content">
            <div style="margin: 0 auto;text-align: center;">
                Loading...
            </div>
        </div>
    </div>
    <div data-role="footer" data-theme="a" data-position="fixed" style="text-align: left;">
        <h4>
        <div class="ui-grid-b">
            <div class="ui-block-a" style="text-align: left;"><span style="color:red">*</span> = Pre-screener reprinted</div>               
        </div>
        </h4>
    </div>
</div>

<!-- Start of second page: #two -->
<div data-role="page" id="passcode_page" data-theme="b">

    <div data-role="header" data-theme="a" data-position="fixed">
        <h1>Passcode</h1>
    </div>

    <div data-role="content" >
        <div data-role="fieldcontain" class="ui-hide-label">
            <label for="passcode">Passcode:</label>
            <input type="number" masktype="password" name="passcode" id="passcode" value="" placeholder="Passcode"/>
        </div>
        <div>
            <input id="hiddenField" type="hidden"/>
        </div>
        <div style="margin: 0 auto;text-align: center;">
            <a href="#" id="goButton" onClick="checkPasscode()" data-role="button" data-theme="b" data-inline="true" style="width: 150px;">Go</a>
        </div> 
        <div id="invalidPasscode" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
            <div data-role="header" data-theme="b">
                <div>
                    <h3 style="text-align: center;">Invalid Passcode</h3>
                </div>
            </div>
            <div data-role="content">
                <div id="passcodeResultDiv"></div>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="#" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
        <div id="logInPasscode" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
            <div data-role="header" data-theme="b">
                <h1>Login</h1>
            </div>
            <div data-role="content">
                <div id="passcodeLoginDiv"></div>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="${pageContext.request.contextPath}/module/chica/greaseBoardMobile.form" data-position-to="window" rel="external" data-ajax="false" data-role="button" data-theme="b" class="ui-btn-right">OK</a>
                </div>
            </div>
        </div>
        <div id="passcodeError" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
            <div data-role="header" data-theme="b">
                <div>
                    <h3 style="text-align: center;">Passcode Error</h3>
                </div>
            </div>
            <div data-role="content">
                <div id="passcodeErrorResultDiv"></div>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="#" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
        
        <div id="error_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
                <div data-role="header" data-theme="b">
                    <h1>Error</h1>
                </div>
                <div data-role="content">
                    <span id="span_errorMessage">${errorMessage}</span>
                    <div style="margin: 0 auto;text-align: center;">
                        <a href="#" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                    </div>
                </div>
            </div>
    </div><!-- /content -->
</div><!-- /page two -->

</body>
</html>
