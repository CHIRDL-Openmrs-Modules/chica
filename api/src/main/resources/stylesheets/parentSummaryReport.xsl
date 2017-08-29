<?xml version="1.0" encoding="ISO-8859-1"?>

 <xsl:stylesheet version="2.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 
 <xsl:template match="/"> 
	<html>
	<body>
	<table>
		<xsl:apply-templates select="/Records"/> 
		<xsl:apply-templates select="/Records/Record/Field"/> 
	</table>
	<xsl:variable name="medStatus" select="Records/Record/Field[@id = 'medStatus']"/> 
	<p>Medication status when assessed: <b><xsl:value-of select="$medStatus/Value"/></b></p>
	</body>
	</html>
 </xsl:template>
 
 <xsl:template match="Records">
	<tr>
		<xsl:variable name="patientName" select="Record/Field[@id = 'LNameChild']"/>
		<td align="left" style="padding: 0px 10px 10px 0px"><b>Name:</b></td>
		<td align="left" style="padding: 0px 70px 10px 0px"><b><xsl:value-of select="$patientName/Value"/></b></td>
		<xsl:variable name="mrn" select="Record/Field[@id = 'MRN']"/>
		<td align="left" style="padding: 0px 0px 10px 0px"><b>MRN:</b></td>
		<td align="left" style="padding: 0px 0px 10px 0px"><b><xsl:value-of select="$mrn/Value"/></b></td>
	</tr>
	<tr>
		<xsl:variable name="dob" select="Record/Field[@id = 'dob']"/>
		<td align="left" style="padding: 0px 10px 10px 0px"><b>Date of Birth:</b></td>
		<td align="left" style="padding: 0px 70px 10px 0px"><b><xsl:value-of select="$dob/Value"/></b></td>
		<xsl:variable name="provider" select="Record/Field[@id = 'Provider']"/>
		<td align="left" style="padding: 0px 0px 10px 0px"><b>Provider:</b></td>
		<td align="left" style="padding: 0px 0px 10px 0px"><b><xsl:value-of select="$provider/Value"/></b></td>
	</tr>
	<tr>
		<xsl:variable name="dor" select="Record/Field[@id = 'dor']"/>
		<td align="left" style="padding: 0px 10px 10px 0px"><b>Date of Report:</b></td>
		<td align="left" style="padding: 0px 70px 10px 0px"><b><xsl:value-of select="$dor/Value"/></b></td>
		<xsl:variable name="dov" select="Record/Field[@id = 'dov']"/>
		<td align="left" style="padding: 0px 0px 10px 0px"><b>Date of Visit:</b></td>
		<td align="left" style="padding: 0px 0px 10px 0px"><b><xsl:value-of select="$dov/Value"/></b></td>
	</tr>
	<tr>
		<td colspan="4" align="center" style="padding: 20px 0px 0px 0px"><h2>Report of Vanderbilt Assessment Scales</h2></td>
	</tr>
	<tr>
		<td colspan="4">
			<table border="1" style="border:1px solid black; empty-cells:show; border-collapse:collapse">
				<tr>
					<th align="left" style="padding: 10px 20px 0px 10px"><u>Parent Assessment Scale</u><br/>(Total # of questions scored 2 or 3)</th>
					<th align="left" style="padding: 10px 20px 0px 10px">Patient<br/>Score</th>
					<th align="left" style="padding: 10px 20px 0px 10px">Cut-off<br/>Score</th>
					<th align="left" style="padding: 10px 20px 0px 10px">Interpretation</th>
				</tr>
				<tr>
					<td style="padding: 10px 20px 0px 10px">Predominantly Inattentive subtype:</td>
					<xsl:variable name="ppis" select="Record/Field[@id = 'PPIS']"/>
					<td align="center" style="padding: 10px 20px 0px 10px"><xsl:value-of select="$ppis/Value"/></td>
					<td align="center" style="padding: 10px 20px 0px 10px">6</td>
					<xsl:variable name="ppisi" select="Record/Field[@id = 'PPISI']"/>
					<td align="left" style="padding: 10px 02px 0px 10px"><xsl:value-of select="$ppisi/Value"/></td>
				</tr>
				<tr>
					<td style="padding: 10px 20px 0px 10px">Predominantly Hyperactive/Impulsive subtype:</td>
					<xsl:variable name="pphis" select="Record/Field[@id = 'PPHIS']"/>
					<td align="center" style="padding: 10px 20px 0px 10px"><xsl:value-of select="$pphis/Value"/></td>
					<td align="center" style="padding: 10px 20px 0px 10px">6</td>
					<xsl:variable name="pphisi" select="Record/Field[@id = 'PPHISI']"/>
					<td align="left" style="padding: 10px 20px 0px 10px"><xsl:value-of select="$pphisi/Value"/></td>
				</tr>
				<tr>
					<td style="padding: 10px 20px 0px 10px">Oppositional-Defiant Disorder Screen:</td>
					<xsl:variable name="podds" select="Record/Field[@id = 'PODDS']"/>
					<td align="center" style="padding: 10px 20px 0px 10px"><xsl:value-of select="$podds/Value"/></td>
					<td align="center" style="padding: 10px 20px 0px 10px">4</td>
					<xsl:variable name="poddsi" select="Record/Field[@id = 'PODDSI']"/>
					<td align="left" style="padding: 10px 20px 0px 10px"><xsl:value-of select="$poddsi/Value"/></td>
				</tr>
				<tr>
					<td style="padding: 10px 20px 0px 10px">Conduct Disorder Screen:</td>
					<xsl:variable name="pcds" select="Record/Field[@id = 'PCDS']"/>
					<td align="center" style="padding: 10px 20px 0px 10px"><xsl:value-of select="$pcds/Value"/></td>
					<td align="center" style="padding: 10px 20px 0px 10px">3</td>
					<xsl:variable name="pcdsi" select="Record/Field[@id = 'PCDSI']"/>
					<td align="left" style="padding: 10px 20px 0px 10px"><xsl:value-of select="$pcdsi/Value"/></td>
				</tr>
				<tr>
					<td style="padding: 10px 20px 0px 10px">Anxiety/Depression Screen:</td>
					<xsl:variable name="pads" select="Record/Field[@id = 'PADS']"/>
					<td align="center" style="padding: 10px 20px 0px 10px"><xsl:value-of select="$pads/Value"/></td>
					<td align="center" style="padding: 10px 20px 0px 10px">3</td>
					<xsl:variable name="padsi" select="Record/Field[@id = 'PADSI']"/>
					<td align="left" style="padding: 10px 20px 0px 10px"><xsl:value-of select="$padsi/Value"/></td>
				</tr>
				<tr>
					<td style="padding: 10px 20px 0px 10px">(Total # or questions scored 4 or 5)<br/>Performance questions:</td>
					<xsl:variable name="ppq" select="Record/Field[@id = 'PPQ']"/>
					<td align="center" style="padding: 10px 20px 0px 10px; vertical-align:text-bottom"><xsl:value-of select="$ppq/Value"/></td>
					<td align="center" style="padding: 10px 20px 0px 10px"></td>
					<td align="left" style="padding: 10px 20px 0px 10px"></td>
				</tr>
				<tr>
					<td style="padding: 10px 20px 0px 10px">Total Symptom Score:</td>
					<xsl:variable name="ptss" select="Record/Field[@id = 'PTSS']"/>
					<td align="center" style="padding: 10px 20px 0px 10px"><xsl:value-of select="$ptss/Value"/></td>
					<td align="center" style="padding: 10px 20px 0px 10px"></td>
					<td align="left" style="padding: 10px 20px 0px 10px"></td>
				</tr>
				<tr>
					<td style="padding: 10px 20px 0px 10px">Average Performance Score:</td>
					<xsl:variable name="paps" select="Record/Field[@id = 'PAPS']"/>
					<td align="center" style="padding: 10px 20px 0px 10px"><xsl:value-of select="$paps/Value"/></td>
					<td align="center" style="padding: 10px 20px 0px 10px"></td>
					<td align="left" style="padding: 10px 20px 0px 10px"></td>
				</tr>
				<tr>
					<td style="padding: 10px 20px 0px 10px"></td>
					<td align="center" style="padding: 10px 20px 0px 10px"></td>
					<td align="center" style="padding: 10px 20px 0px 10px"></td>
					<xsl:variable name="gi" select="Record/Field[@id = 'GI']"/>
					<xsl:variable name="odd" select="Record/Field[@id = 'ODD']"/>
					<xsl:variable name="cd" select="Record/Field[@id = 'CD']"/>
					<xsl:variable name="ad" select="Record/Field[@id = 'AD']"/>
					<td align="left" style="padding: 10px 20px 0px 10px"><xsl:value-of select="$gi/Value"/><br/><xsl:value-of select="$odd/Value"/><br/><xsl:value-of select="$cd/Value"/><br/><xsl:value-of select="$ad/Value"/></td>
				</tr>
			</table>
		</td>
	</tr>
 </xsl:template> 
 
 <xsl:template match="Field"> 
 
 </xsl:template>

</xsl:stylesheet> 