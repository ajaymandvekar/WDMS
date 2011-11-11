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
	background-color: orange;
}
.operation
{
	padding-left:1%;
	width:85%;
	float:left;
}

.op_header
{	
	font-style:oblique;
	font-weight:bolder;
	font-size:1.2em;
	margin-left:40%;
	padding-bottom:10px;
	
}

</style>
<title>Update Document</title>
</head>
<body>
<s:set name="theme" value="'simple'" scope="page" />
	<s:url id="homepage" action="GuestHomePageAction"></s:url>
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
			<s:a href="%{getdocuments}">View/Read</s:a>
			<br>
			<br>
			<s:url id="checkoutdocument" action="GuestCheckoutDocument"></s:url> 
			<s:a href="%{checkoutdocument}">Check-out Document</s:a>
			<br>
			<br>
			<s:url id="checkindocument" action="GuestCheckinDocument"></s:url> 
			<s:a href="%{checkindocument}">Check-in Document</s:a>
			<br>
			<br>
			<s:url id="updatedocument" action="DocumentUpdateAction"></s:url> 
			<s:a href="%{updatedocument}">Update Document</s:a>
			<br>
			<br>
		</div>
		<div class="operation">
			<s:actionmessage />
			<s:actionerror/>
			<table>
				<tr>
					<th>Name</th>
					<th>Author</th>
					<th>Document Type</th>
					<th>Document Size</th>
					<th>Creation Time</th>
					<th>Last Access Time</th>
					<th>Modification Time</th>						
					<th>Update Metadata</th>
					<th>Update Document</th>
				</tr>
				<s:iterator value="documents" status="document">
						<tr>
							<s:form action="UpdateMetaDataDocument">						
								<td><s:textfield name="documentname" value="%{doc_name}" /></td>
								<td><s:property value="%{doc_author}" /></td>
								<td><s:property value="%{doc_type}" /></td>
								<td><s:property value="%{doc_size}" /></td>
								<td><s:property value="%{doc_create_time}" /></td>
								<td><s:property value="%{doc_lastaccess_time}" /></td>
								<td><s:property value="%{doc_modify_time}" /></td>
							    <s:hidden name="documentid" value="%{doc_id}" />
							    <s:hidden name="docname" value="%{doc_name}"/>
								<td><s:submit type="button" value="Update Metadata"/></td>
							</s:form>
							<s:if test="%{doc_isencrypted==1}">
							<s:form action="updatedocument_with_encrypt" method="post" enctype="multipart/form-data">
							   <td>
								<s:hidden name="documentid" value="%{doc_id}" />
								<s:file size="6" name="document" label="Document" />
								<br><br>
								Key : <s:textfield size="6" name="EncryptionKey" label="Key Phrase"></s:textfield>
								<s:submit value="Upload" align="middle" />
							   </td>
							</s:form>
							</s:if>
							<s:if test="%{doc_isencrypted==0}">
							<s:form action="updatedocument" method="post" enctype="multipart/form-data">
							   <td>
								<s:hidden name="documentid" value="%{doc_id}" />
								<s:file name="document" label="Document" />
								<s:submit value="Upload" align="center" />
							   </td>
							</s:form>
							</s:if>
						</tr>
				</s:iterator>
			</table>
		</div>
	</div>
</div>
</body>
</html>