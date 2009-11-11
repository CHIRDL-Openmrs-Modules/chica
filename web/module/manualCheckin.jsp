<%@ include file="/WEB-INF/template/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">


<%@ page   import="org.openmrs.web.WebConstants" %>
<%
	pageContext.setAttribute("msg", session.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
	pageContext.setAttribute("msgArgs", session.getAttribute(WebConstants.OPENMRS_MSG_ARGS));
	pageContext.setAttribute("err", session.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	pageContext.setAttribute("errArgs", session.getAttribute(WebConstants.OPENMRS_ERROR_ARGS));
	session.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_MSG_ARGS);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ARGS);
%>

<html  xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<openmrs:htmlInclude file="/openmrs.css" />
		<openmrs:htmlInclude file="/style.css" />
		<openmrs:htmlInclude file="/openmrs.js" />
			<title>Manual Checkin Form</title>
		<script type="text/javascript">
			/* variable used in js to know the context path */
			var openmrsContextPath = '${pageContext.request.contextPath}';
		</script>
		<script type="text/javascript">
<!-- borrowed from http://dadabase.de/dev/window_resizer.html -->



function setSize(width,height) {
	if (window.outerWidth) {
		window.outerWidth = width;
		window.outerHeight = height;
	}
	else if (window.resizeTo) {
		window.resizeTo(width,height);
	}
	window.moveTo(50,50);
}

function useConfirmationForm() {
	setTimeout('self.close();',5000);
	window.resizeTo(525,100); 
	window.moveTo(400,300);
}

function writeConfirmationMessage(success, name,  mrn) {
	var text = "";
	if (success){
		text =  name + " (MRN: " +  mrn + ") was successfully checked in.";
	} else {
		text =  "Unable to check in "+ name + " (MRN: " + mrn + "). Please contact CHICA support.";
	}
	document.write(text.bold());
	
}

function checkform()
{       
        if (document.manualCheckin.ssn1.value != ''||
			document.manualCheckin.ssn2.value != ''||
			document.manualCheckin.ssn3.value != '') {
       var stringSSN = manualCheckin.ssn1.value + manualCheckin.ssn2.value + manualCheckin.ssn3.value
	  
	   var RE_SSN = /^[0-9]{3}[\- ]?[0-9]{2}[\- ]?[0-9]{4}$/;
	   if (RE_SSN.test(stringSSN)) {
          
       } else {
       alert('The patient must have a valid SSN number.');
	   return false;
        }
	}
	  
	if (document.manualCheckin.mrn.value == '')
	{
		alert('The patient must have a medical record number.');
		return false;
	}
	else if (document.manualCheckin.firstName.value == '')
	{
		alert('The patient must have a first name.');
		return false;
	}
	else if (document.manualCheckin.lastName.value == '')
	{
		alert('The patient must have a last name.');
		return false;
	}
	else if (document.manualCheckin.dob1.value == ''||
			document.manualCheckin.dob2.value == ''||
			document.manualCheckin.dob3.value == '')
	{
		alert('The patient must have a birthdate.');
		return false;
	}
	
	var day = document.getElementById("dob2").value;
    var month = document.getElementById("dob1").value;
    var year= document.getElementById("dob3").value;
    var leap = 0;
    var err = 0;
	if (year.length == 2) {
	year = '20' + year.substr(0,2); }
	if (year.length != 4) {
	err = 19;}
	/* year is wrong if year = 0000 */
	if (year == 0) {
	err = 20;
	}
	/* Validation of month*/
	if ((month < 1) || (month > 12)) {
	err = 21;
	}
	/* Validation of day*/
	if (day < 1) {
	err = 22;
	}
	/* Validation leap-year / february / day */
	if ((year % 4 == 0) || (year % 100 == 0) || (year % 400 == 0)) {
	leap = 1;
	}
	if ((month == 2) && (leap == 1) && (day > 29)) {
	err = 23;
	}
	if ((month == 2) && (leap != 1) && (day > 28)) {
	err = 24;
	}
	/* Validation of other months */
	if ((day > 31) && ((month == "01") || (month == "03") || (month == "05") || (month == "07") || (month == "08") || (month == "10") || (month == "12"))) {
	err = 25;
	}	
if ((day > 30) && ((month == "04") || (month == "06") || (month == "09") || (month == "11"))) {
err = 26;
}

if (err != 0)
{
 alert('Please check your DOB entry.');
 return false;
}
	
	var oDDL = document.getElementById("doctorName");
	var curText = oDDL.options[oDDL.selectedIndex].text
	if (curText.indexOf('Other')>-1)
   {
  	var where_to= confirm("Are you sure to select OTHER as the Doctor?");

    if (where_to== true)
    {
 
 
	 }
	else
 	{
      return false;
  	}
   }
   
   	var oDDL = document.getElementById("station");
    var curText = oDDL.options[oDDL.selectedIndex].text
	if (curText.indexOf('Other')>-1)
	
   {
	var where_to= confirm("Are you sure to select OTHER as the Station?");

    if (where_to== true)
    {
 
 
	 }
	else
 	{
      return false;
  	}
   }
    
    if(!document.getElementById("lastName").readOnly)
   {
     var where_to= confirm("Are you sure you want to add " + document.getElementById("firstName").value + " " + document.getElementById("lastName").value +  " as a new patient?");

    if (where_to== false)
    {
      return false;
	 }
	
   }
   
	
    
   for (i = 0; i < document.manualCheckin.length; i++)
    {
      var tempobj = document.manualCheckin.elements[i];
      if (tempobj.type.toLowerCase() == "submit")
      {
        tempobj.disabled = true;
        tempobj.value = "Processing...";
      }
    }
  
	  return true;
 
}



</script>
		
		<!--  Page Title : '${pageTitle}' 
			OpenMRS Title: <spring:message code="openmrs.title"/>
		-->
		<c:choose>
			<c:when test="${!empty pageTitle}">
				<title>${pageTitle}</title>
			</c:when>
			<c:otherwise>
				<title><spring:message code="openmrs.title"/></title>
			</c:otherwise>
		</c:choose>		
		
	</head>

<body  style="scrollbars:no"onload="javascript:setSize(800,475); <c:if test="${!empty checkinPatient}"> javascript:useConfirmationForm();</c:if>" onkeydown="if (event.keyCode==8) {event.keyCode=0; return event.keyCode }">
	<div id="pageBody" class="greaseBoardBackground"  style="width:100%">		
		<div id="contentMinimal">
			<c:if test="${msg != null}">
				<div id="openmrs_msg"><spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" /></div>
			</c:if>
			<c:if test="${err != null}">
				<div id="openmrs_error"><spring:message code="${err}" text="${err}" arguments="${errArgs}"/></div>
			</c:if>
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />

	<c:choose>
			<c:when test="${!empty checkinPatient}">
			<script type="text/javascript">
				writeConfirmationMessage(${checkinSuccess},"${checkinPatient}", "${checkinMRN}");
			</script>
			</c:when>
			
	<c:otherwise>
						
<form height="100%" name="manualCheckin" action="manualCheckin.form" method="get" onSubmit="return checkform()">
<table height="100%" style="border-width:0px;" cellspacing="0">
<tr>
<td style="text-align:left"><c:if test="${!empty newPatient}"><span style="color:red">*</span>&nbsp;</c:if><b>Station</b>
<select name="station" tabindex="1" id="station" >
<c:forEach items="${stations}" var="station">
<option>${station}</option>
</c:forEach>	
</select>&nbsp;&nbsp;
<c:if test="${empty mrn}"><span style="color:red">*</span>&nbsp;</c:if><b>MRN</b></td>
<td><input type="text" name="mrn" value="${mrn}"  readonly class="readonly" />
  <c:if test="${!empty newPatient}"><span style="color:red">*</span>&nbsp;</c:if><b>Doctor</b>
<select name="doctor" tabindex="2" id="doctorName">
<c:forEach items="${doctors}" var="doctorListDoctor">
<option 
<c:if test="${doctorListDoctor.familyName == 'Other'}">selected</c:if> value="${doctorListDoctor.userId}">${doctorListDoctor.familyName}<c:if test="${!empty doctorListDoctor.givenName}">, ${doctorListDoctor.givenName}</c:if><c:if test="${!empty doctorListDoctor.middleName}">${doctorListDoctor.middleName}</c:if>
</option>
</c:forEach>	
</select>
</td><td "text-align:left"></td></tr>
<tr>
<td style="text-align:right"><b>SSN</b></td>
<td style="text-align:left"> <input type="text" name="ssn1" size="3" maxlength="3" value="${ssn1}" tabindex="3" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/>&nbsp;-&nbsp;<input type="text" name="ssn2" size="2" maxlength="2" value="${ssn2}" tabindex="4" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/>&nbsp;-&nbsp;<input type="text" name="ssn3" size="4" maxlength="4" value="${ssn3}" tabindex="5" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/></td>
<td style="text-align:left" rowspan="2"> <input type="submit" value="${checkinButton}"  class="CheckinFormButton" tabindex="22"></td>
</tr>
<tr><td style="text-align:right"><c:if test="${!empty newPatient}"><span style="color:red">*</span>&nbsp;</c:if><b>Last Name</b> </td><td style="text-align:left"><input type="text" name="lastName" tabindex="6" id="lastName" value="${lastName}" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/></td></tr>
<tr><td style="text-align:right"><c:if test="${!empty newPatient}"><span style="color:red">*</span>&nbsp;</c:if><b>First Name</b> </td><td style="text-align:left"><input type="text" name="firstName" tabindex="7" value="${firstName}" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/></td>
<td rowspan="2" ><input type="button" value="Cancel" class="CheckinFormButton" onclick='window.close()' tabindex="23"/>
</tr>
<tr><td style="text-align:right"><b>Middle Name </b></td><td style="text-align:left"><input type="text" name="middleName" value="${middleName}" tabindex="8" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/></td></tr>
<tr><td style="text-align:right"><c:if test="${!empty newPatient}"><span style="color:red">*</span>&nbsp;</c:if><b>DOB</b></td><td style="text-align:left"> <input type="text" name="dob1" size="2" maxlength="2" value="${dob1}" tabindex="9" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/>/<input type="text" name="dob2" size="2" maxlength="2" value="${dob2}" tabindex="10" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/>/<input type="text" name="dob3" size="4" maxlength="4" value="${dob3}" tabindex="11" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/></td></tr>
<tr><td style="text-align:right"><c:if test="${!empty newPatient}"><span style="color:red">*</span>&nbsp;</c:if><b>Sex</b></td><td style="text-align:left">
<SELECT NAME="sex" tabindex="12" <c:if test="${empty newPatient}">READONLY class="readonly"<c:if test="${!empty newPatient}"><span style="color:red">*</span>&nbsp;</c:if>onfocus="this_index1 = this.selectedIndex;" onchange="this.selectedIndex = this_index1;"></c:if>>
<OPTION <c:if test="${sex == 'U'}">selected</c:if> value="U">U - unknown</OPTION>
<OPTION <c:if test="${sex == 'F'}">selected</c:if> value="F">F - female</OPTION>
<OPTION <c:if test="${sex == 'M'}">selected</c:if> value="M">M - male</OPTION>
</SELECT>
</tr>
<tr><td style="text-align:right"><b>Mother's First Name</b></td><td style="text-align:left"> <input type="text" name="mothersFirstName" tabindex="13" value="${mothersFirstName}" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/></td></tr>
<tr><td style="text-align:right"><b>Day Phone</b></td><td style="text-align:left"><input type="text" name="dayPhone" value="${dayPhone}" tabindex="14" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/></td></tr>
<tr><td style="text-align:right"><b>Street Address</b> </td><td style="text-align:left"><input type="text" size="50" name="address1" tabindex="15" value="${address1}" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/></td></tr>
<tr><td style="text-align:right"><b>Street Address2</b></td><td style="text-align:left"> <input type="text" size="50" name="address2" tabindex="16" value="${address2}" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/></td></tr>
<tr><td style="text-align:right"><b>City</b></td><td><input type="text" name="city" value="${city}" tabindex="17" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/></td></tr>
<tr><td style="text-align:right"><b>State</b></td><td>
<SELECT NAME="state" tabindex="18" <c:if test="${empty newPatient}">READONLY class="readonly" onfocus="this_index2 = this.selectedIndex;" onchange="this.selectedIndex = this_index2;"></c:if>>
<OPTION></OPTION>
<OPTION <c:if test="${state == 'AL'}">selected</c:if> value="AL">AL - Alabama</OPTION>
<OPTION <c:if test="${state == 'AK'}">selected</c:if> value="AK">AK - Alaska</OPTION>
<OPTION <c:if test="${state == 'AZ'}">selected</c:if> value="AZ">AZ - Arizona</OPTION>
<OPTION <c:if test="${state == 'AR'}">selected</c:if> value="AR">AR - Arkansas</OPTION>
<OPTION <c:if test="${state == 'CA'}">selected</c:if> value="CA">CA - California</OPTION>
<OPTION <c:if test="${state == 'CO'}">selected</c:if> value="CO">CO - Colorado</OPTION>
<OPTION <c:if test="${state == 'CT'}">selected</c:if> value="CT">CT - Connecticut</OPTION>
<OPTION <c:if test="${state == 'DE'}">selected</c:if> value="DE">DE - Delaware</OPTION>
<OPTION <c:if test="${state == 'DC'}">selected</c:if> value="DC">DC - District of Columbia</OPTION>
<OPTION <c:if test="${state == 'FL'}">selected</c:if> value="FL">FL - Florida</OPTION>
<OPTION <c:if test="${state == 'GA'}">selected</c:if> value="GA">GA - Georgia</OPTION>
<OPTION <c:if test="${state == 'HI'}">selected</c:if> value="HI">HI - Hawaii</OPTION>
<OPTION <c:if test="${state == 'ID'}">selected</c:if> value="ID">ID - Idaho</OPTION>
<OPTION <c:if test="${state == 'IL'}">selected</c:if> value="IL">IL - Illinois</OPTION>
<OPTION <c:if test="${state == 'IN'}">selected</c:if> value="IN">IN - Indiana</OPTION>
<OPTION <c:if test="${state == 'IA'}">selected</c:if> value="IA">IA - Iowa</OPTION>
<OPTION <c:if test="${state == 'KS'}">selected</c:if> value="KS">KS - Kansas</OPTION>
<OPTION <c:if test="${state == 'KY'}">selected</c:if> value="KY">KY - Kentucky</OPTION>
<OPTION <c:if test="${state == 'LA'}">selected</c:if> value="LA">LA - Louisiana</OPTION>
<OPTION <c:if test="${state == 'ME'}">selected</c:if> value="ME">ME - Maine</OPTION>
<OPTION <c:if test="${state == 'MD'}">selected</c:if> value="MD">MD - Maryland</OPTION>
<OPTION <c:if test="${state == 'MA'}">selected</c:if> value="MA">MA - Massachusetts</OPTION>
<OPTION <c:if test="${state == 'MI'}">selected</c:if> value="MI">MI - Michigan</OPTION>
<OPTION <c:if test="${state == 'MN'}">selected</c:if> value="MN">MN - Minnesota</OPTION>
<OPTION <c:if test="${state == 'MS'}">selected</c:if> value="MS">MS - Mississippi</OPTION>
<OPTION <c:if test="${state == 'MO'}">selected</c:if> value="MO">MO - Missouri</OPTION>
<OPTION <c:if test="${state == 'MT'}">selected</c:if> value="MT">MT - Montana</OPTION>
<OPTION <c:if test="${state == 'NE'}">selected</c:if> value="NE">NE - Nebraska</OPTION>
<OPTION <c:if test="${state == 'NV'}">selected</c:if> value="NV">NV - Nevada</OPTION>
<OPTION <c:if test="${state == 'NH'}">selected</c:if> value="NH">NH - New Hampshire</OPTION>
<OPTION <c:if test="${state == 'NJ'}">selected</c:if> value="NJ">NJ - New Jersey</OPTION>
<OPTION <c:if test="${state == 'NM'}">selected</c:if> value="NM">NM - New Mexico</OPTION>
<OPTION <c:if test="${state == 'NY'}">selected</c:if> value="NY">NY - New York</OPTION>
<OPTION <c:if test="${state == 'NC'}">selected</c:if> value="NC">NC - North Carolina</OPTION>
<OPTION <c:if test="${state == 'ND'}">selected</c:if> value="ND">ND - North Dakota</OPTION>
<OPTION <c:if test="${state == 'OH'}">selected</c:if> value="OH">OH - Ohio</OPTION>
<OPTION <c:if test="${state == 'OK'}">selected</c:if> value="OK">OK - Oklahoma</OPTION>
<OPTION <c:if test="${state == 'OR'}">selected</c:if> value="OR">OR - Oregon</OPTION>
<OPTION <c:if test="${state == 'PA'}">selected</c:if> value="PA">PA - </OPTION>
<OPTION <c:if test="${state == 'RI'}">selected</c:if> value="RI">RI - Rhode Island</OPTION>
<OPTION <c:if test="${state == 'SC'}">selected</c:if> value="SC">SC - South Carolina</OPTION>
<OPTION <c:if test="${state == 'SD'}">selected</c:if> value="SD">SD - South Dakota</OPTION>
<OPTION <c:if test="${state == 'TN'}">selected</c:if> value="TN">TN - Tennessee</OPTION>
<OPTION <c:if test="${state == 'TX'}">selected</c:if> value="TX">TX - Texas</OPTION>
<OPTION <c:if test="${state == 'UT'}">selected</c:if> value="UT">UT - Utah</OPTION>
<OPTION <c:if test="${state == 'VT'}">selected</c:if> value="VT">VT - Vermont</OPTION>
<OPTION <c:if test="${state == 'VA'}">selected</c:if> value="VA">VA - Virginia</OPTION>
<OPTION <c:if test="${state == 'WA'}">selected</c:if> value="WA">WA - Washington</OPTION>
<OPTION <c:if test="${state == 'WV'}">selected</c:if> value="WV">WV - West Virginia</OPTION>
<OPTION <c:if test="${state == 'WI'}">selected</c:if> value="WI">WI - Wisconsin</OPTION>
<OPTION <c:if test="${state == 'WY'}">selected</c:if> value="WY">WY - Wyoming</OPTION>
</SELECT></td></tr>
<tr><td style="text-align:right"><b>ZIP </b></td><td><input type="text" size="15" name="zip" value="${zip}" tabindex="19" <c:if test="${empty newPatient}">READONLY class="readonly"</c:if>/></td></tr>
<tr><td style="text-align:right"><b>Race</b></td><td style="text-align:left">
<SELECT NAME="race" tabindex="20" <c:if test="${empty newPatient}">READONLY class="readonly" onfocus="this_index3 = this.selectedIndex;" onchange="this.selectedIndex = this_index3;"></c:if>>
<OPTION></OPTION>
<OPTION <c:if test="${race == 'B' || race == '1'}">selected</c:if> value="B">B - Black</OPTION>
<OPTION <c:if test="${race == 'H' || race == '4' || race == '8'}">selected</c:if> value="H">H - Hispanic / Latino</OPTION>
<OPTION <c:if test="${race == 'W' || race == '6'}">selected</c:if> value="W">W - White</OPTION>
<OPTION <c:if test="${race == 'X' || race == '7'}">selected</c:if> value="X">X - Asian Pacific/ Islander</OPTION>
<OPTION <c:if test="${race == 'I' || race == '2'}">selected</c:if> value="I">I - American Indian / Eskimo</OPTION>
<OPTION <c:if test="${race == 'O' || race == '3'}">selected</c:if> value="O">O - Other</OPTION>
<OPTION <c:if test="${race == 'U' || race == '5'}">selected</c:if> value="U">U - Unknown</OPTION>
</SELECT>
</td></tr>
<tr><td style="text-align:right"><b>Insurance Category</b></td>
<td style="text-align:left">
<select name="insuranceCategory" tabindex="21" >
<option></option>		
<c:forEach items="${insuranceCategories}" var="insurCategory">
<option <c:if test="${insuranceCategory == insurCategory}">selected</c:if> value="${insurCategory}">${insurCategory}</option>
</c:forEach>	
</select>
	</td>
	</tr>
	<tr></tr>
</table>
<input  type="hidden" name="checkin" value="checkin"/>
<input type="hidden" name="checkinForm" value="true"/>
</form>
</c:otherwise>
</c:choose>	
</div>
</div>
</body>
</html>