<%
response.setHeader("Pragma","no-cache");
response.setHeader("Cache-control","no-cache");
response.setHeader("Cache-control","no-store");
response.setHeader("Cache-control","no-chache");
response.setHeader("Cache-control","cached-pages");
response.setHeader("Expires","0");
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
	height:100%
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
</style>
<title>Administrator Page</title>
</head>
<body>
<s:set name="theme" value="'simple'" scope="page" />
	<s:url id="homepage" action="AdminHomePageAction"></s:url>
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
			<s:url id="viewrequest" action="ViewRequests"></s:url> 
			<s:a href="%{viewrequest}">View Account Requests</s:a>
			<br>
			<br>
			<s:url id="adduser" action="AdminAddUser"></s:url> 
			<s:a href="%{adduser}">Add Account</s:a>
			<br>
			<br>
			<s:url id="deleteuser" action="AdminDeleteAccounts"></s:url>
			<s:a href="%{deleteuser}">Delete Accounts</s:a>
			<br>
			<br>
			<s:url id="updateuser" action="ViewUserAccounts"></s:url> 
			<s:a href="%{updateuser}">Modify Accounts</s:a>
			<br>
			<br>
			<s:url id="viewsystemlog" action="ViewSystemLog" ></s:url>
			<s:a href="%{viewsystemlog}">View System Log File</s:a>
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
		<h1>Welcome "<s:property value="#session.name" />"</h1>
		<h3>Please select the side navigation bar links to perform operations</h3>
		</div>
	</div>
	</div>
</body>
</html>