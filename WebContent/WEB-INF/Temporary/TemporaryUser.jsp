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
</head>
<body>
<s:url id="logout" action="LogoutAction"></s:url>
<div class="outer-box-container">
	<div class="inner-container">
	<img src="images/dms-banner.jpg" class="banner">
		<div class="TopNavigation">>
			<s:a href="%{logout}">Logout</s:a>
		</div>
			<div class="navigation">
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
			<br>
			<br>
			<br>
			<br>
		</div>
		<div class="operation">
		<h1>Welcome to Account Registration page</h1>
		<s:actionmessage/>
		<s:label>New User Account Creation:</s:label>
		<s:form action="SendRequestAdminAction" method="POST" validate="true">
				<s:actionerror/>
				<s:textfield name="firstname" label="Firstname" size="20" />
				<s:actionerror/>
				<s:textfield name="lastname" label="Lastname" size="20" />
				<s:actionerror/>
				<p>Email address will be used as username for logging into the system</p>
				<s:textfield name="email" label="Email" size="20" />
				<s:actionerror/>
				<s:password name="password" label="Password" size="20" />
				<s:submit key="label.sendreq" align="center" />
		</s:form>
		</div>
	</div>
</div>
</body>
</html>