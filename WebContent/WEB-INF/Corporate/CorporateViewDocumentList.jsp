<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, cached-pages"); // HTTP 1.1.
response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
response.setDateHeader("Expires", 0); // Proxies.
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<style type="text/css">
table,td,th 
{
	border: 1px solid black;
	font-size: 0.9em;
}
table {
	width: 100%;
}
th {
	height:100%;
}
.outer-box-container
{
	background-color : lightyellow;
	width:100%;
	height:100%;
}

.inner-container
{
	width:100%;
	height:100%
}

.banner
{
	width:100%;
	height:20%;
}

.navigation
{
	width:12%;
	height:auto;
	border-right-style: solid;
	border-right-width: 0.1em;
	border-right:ridge;
	border-collapse:collapse;
	float:left;
}
.navigation a{
font-weight: bolder;
font-family:serif; 
font-size:0.8em;
font-stretch: wider;
}

.TopNavigation
{
	width:100%;
	float:right;
	padding-left:80%;
	padding-top:3px;
	padding-bottom: 10px;
}

.TopNavigation a
{
	background-color: lightgreen;
}
.operation
{
	padding-left:1%;
	width:85%;
	float:left;
	overflow: auto;
	height:250px;
	padding-bottom:1px;
}

.op_header
{	
	font-weight:bolder;
	font-size:1em;
	margin-left:50%;
	padding-bottom:10px;
}

.op_header_2
{	
	font-weight:bolder;
	font-size:1em;
	margin-left:48%;
	padding-bottom:10px;
}

</style>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>View Documents List</title>
</head>
<body>
<s:set name="theme" value="'simple'" scope="page" />
	<s:url id="homepage" action="CorporateHomePageAction"></s:url>
	<s:url id="logout" action="LogoutAction"></s:url>
<div class="outer-box-container">
	<div class="inner-container">
	<img src="images/dms-banner.jpg" class="banner">
		<div class="TopNavigation">
			<s:a href="%{homepage}">Home Page</s:a>
			<s:a href="%{logout}">Logout</s:a>
		</div>
	<div class="navigation">
			<br>
			<br>
			<s:url id="getdocuments" action="GetDocumentListAction"></s:url> 
			<s:a href="%{getdocuments}">View/Read/Delete/Share</s:a>
			<br>
			<br>
			<br>
			<s:url id="uploaddocument" action="CorporateUploadDocument"></s:url> 
			<s:a href="%{uploaddocument}">Upload</s:a>
			<br>
			<br>
			<br>
			<s:url id="uploadencrypteddocument" action="CorporateUploadEncryptedDocument"></s:url> 
			<s:a href="%{uploadencrypteddocument}">Encrypted Upload</s:a>
			<br>
			<br>
			<br>
			<s:url id="checkoutdocument" action="CorporateCheckoutDocument"></s:url> 
			<s:a href="%{checkoutdocument}">Check-out Document</s:a>
			<br>
			<br>
			<br>
			<s:url id="checkindocument" action="CorporateCheckinDocument"></s:url> 
			<s:a href="%{checkindocument}">Check-in Document</s:a>
			<br>
			<br>
			<br>
			<s:url id="updatedocument" action="DocumentUpdateAction"></s:url> 
			<s:a href="%{updatedocument}">Update Document</s:a>
				<br>
			<br>
			<br>
				<br>
			<br>
			<br>
		</div>
		<div class="op_header">Documents</div>
		<div class="operation">
			<s:actionmessage />
			<s:actionerror/>
			<hr>
			<table>
				<tr>
					<th>Name</th>
					<th>Author</th>
					<th>Document Type</th>
					<th>Document Size(KB)</th>
					<th>Creation Time</th>
					<th>Last Access Time</th>
					<th>Modification Time</th>
					<th>Read</th>
					<th>Delete</th>
					<th>Share</th>
				</tr>
				<s:iterator value="viewdocumentlist" status="document">
						<tr>
							<td><s:property value="%{doc_name}" /></td>
							<td><s:property value="%{doc_author}" /></td>
							<td><s:property value="%{doc_type}" /></td>
							<td><s:property value="%{doc_size}" /></td>
							<td><s:property value="%{doc_create_time}" /></td>
							<td><s:property value="%{doc_lastaccess_time}" /></td>
							<td><s:property value="%{doc_modify_time}" /></td>
							
							
							<s:if test="%{doc_isencrypted==0}">
								<s:form action="ReadDocument">
									<s:hidden name="documentid" value="%{doc_id}" /> 
									<td><s:submit type="button" value="Read"/></td>
								</s:form>
							</s:if>
							<s:elseif test="%{doc_isencrypted==1}">
								<s:form action="ReadEncryptedDocument">
									<s:hidden name="documentid" value="%{doc_id}" />
									<td><s:textfield size="8" name="EncryptionKey"/><s:submit type="button" value="Read"/></td>
									</s:form>
							</s:elseif>
							
							<s:form action="DeleteDocument">
							   <s:hidden name="documentid" value="%{doc_id}" /> 
								<td><s:submit type="button" value="Delete"/></td>
							</s:form> 
							
							<s:if test="%{can_share==1}">
								<s:form action="CorporateShareDocument">
								   <s:hidden name="documentid" value="%{doc_id}" /> 
									<td><s:submit type="button" value="Share"/></td>
								</s:form>
							</s:if>
							<s:else>
								<td>Can't share</td>
							</s:else>
							
						</tr>
				</s:iterator>
			</table>		
	</div>
	<div class="op_header_2">Shared Documents</div>
	<div class="operation">
	<hr>
				<table>
				<tr>
					<th>Name</th>
					<th>Author</th>
					<th>Document Type</th>
					<th>Document Size(KB)</th>
					<th>Creation Time</th>
					<th>Last Access Time</th>
					<th>Modify Time</th>
					<th>Read</th>
				</tr>
				<s:iterator value="shareddocumentlist" status="document">
						<tr>
							<td><s:property value="%{doc_name}" /></td>
							<td><s:property value="%{doc_author}" /></td>
							<td><s:property value="%{doc_type}" /></td>
							<td><s:property value="%{doc_size}" /></td>
							<td><s:property value="%{doc_create_time}" /></td>
							<td><s:property value="%{doc_lastaccess_time}" /></td>
							<td><s:property value="%{doc_modify_time}" /></td>
							
							<s:if test="%{read_doc == 1}">
								<s:if test="%{doc_isencrypted==0}">
									<s:form action="ReadDocument">
										<s:hidden name="documentid" value="%{doc_id}" /> 
										<td><s:submit type="button" value="Read"/></td>
									</s:form>
								</s:if>
								<s:elseif test="%{doc_isencrypted==1}">
									<s:form action="ReadEncryptedDocument">
										<s:hidden name="documentid" value="%{doc_id}" />
										<td><s:textfield size="8" name="EncryptionKey"/><s:submit type="button" value="Read"/></td>
										</s:form>
								</s:elseif>
							</s:if>
						</tr>
				</s:iterator>
			</table>
		</div>
	</div>
</div>
</body>
</html>