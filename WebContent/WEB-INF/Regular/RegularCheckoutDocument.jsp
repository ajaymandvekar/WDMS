<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, cached-pages"); // HTTP 1.1.
response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
response.setDateHeader("Expires", 0); // Proxies.
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
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
	<title>Check-out Document</title>
</head>
<body>
<s:set name="theme" value="'simple'" scope="page" />
	<s:url id="homepage" action="RegularHomePageAction"></s:url>
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
			<s:url id="uploaddocument" action="RegularUploadDocument"></s:url> 
			<s:a href="%{uploaddocument}">Upload</s:a>
			<br>
			<br>
			<s:url id="uploadencrypteddocument" action="RegularUploadEncryptedDocument"></s:url> 
			<s:a href="%{uploadencrypteddocument}">Encrypted Upload</s:a>
			<br>
			<br>
			<s:url id="checkoutdocument" action="RegularCheckoutDocument"></s:url> 
			<s:a href="%{checkoutdocument}">Check-out Document</s:a>
			<br>
			<br>
			<s:url id="checkindocument" action="RegularCheckinDocument"></s:url> 
			<s:a href="%{checkindocument}">Check-in Document</s:a>
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
			<br>
			<br>
			<br>
			<br>
			<br>
			<br>
			<br>
			<br>
			<br>
			<br>
		</div>
		<div class="operation">
			<div class="op_header">Documents</div>
			<s:actionmessage />
			<s:actionerror/>
				
				<table>
					<tr>
						<th>Name</th>
						<th>Author</th>
						<th>Document Type</th>
						<th>Document Size(KB)</th>
						<th>Check-out</th>
						</tr>
					<s:iterator value="documents" status="curr_document">
						<tr>
							<td><s:property value="%{doc_name}" /></td>
							<td><s:property value="%{doc_author}" /></td>
							<td><s:property value="%{doc_type}" /></td>
							<td><s:property value="%{doc_size}" /></td>
							<s:form action="regular_checkoutdocument">
							   <s:hidden name="documentid" value="%{doc_id}" /> 
								<td><s:submit type="button" value="Check-out"/></td>
							</s:form>
						</tr>
					</s:iterator>
				</table>	
			</div>		
	</div>
	</div>
</body>
</html>