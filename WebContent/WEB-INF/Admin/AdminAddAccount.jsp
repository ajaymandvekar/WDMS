<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
table,td,th 
{
	border-style: solid	;
	font-size:0.9em;
	font-weight:bold;
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
}

.op_header
{	
	font-weight:bolder;
	font-size:1em;
	margin-left:50%;
	padding-bottom:10px;
	
}
</style>
<title>Admin New Account Creation</title>
</head>
<body>
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
		<div class="op_header">Add Account</div>
		<div class="operation">
		<s:form action="AdminAction" method="POST" validate="true">
		<s:actionmessage />
		<s:textfield style="margin-top:10px;" label="Firstname" name="firstname" />
		<s:textfield style="margin-top:10px;" label="Lastname" name="lastname" />
		<s:textfield style="margin-top:10px;" label="Email" name="email" />
		<s:password style="margin-top:10px;" label="Password" name="password" />
		<s:password style="margin-top:10px;" label="Confirm Password" name="password_confirm" />
		<s:select style="margin-top:10px;" label="Designation" headerKey="-1"
			headerValue="Select Designation" list="designation_names"
			name="curr_designation" />
		<s:checkboxlist label="Department(s)" list="department_names"
			name="curr_department" />
		<s:submit style="margin-top:10px;" type="button" value="Add Account" method="add_new_account" align="middle"/>
		</s:form>
		</div>
	</div>
	</div>
</body>
</html>