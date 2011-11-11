<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
	background-color: orange;
}
.operation
{
	padding-left:1%;
	width:85%;
	float:left;
	overflow: auto;
	height:350px;
}
.op_header
{	
	font-weight:bolder;
	font-size:1em;
	margin-left:50%;
	padding-bottom:10px;
	
}
</style>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Administrator: Modify User Accounts</title>
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
		<div class="op_header">Modify Accounts</div>
		<div class="operation">
			<s:actionerror />
			<s:actionmessage />
				<table>
					<tr>
						<th>ID</th>
						<th>Firstname</th>
						<th>Lastname</th>
						<th>Email</th>
						<th>Designation</th>
						<th>Department</th>
						<th>Operation</th>
					</tr>
					<s:iterator value="newrequest" status="newrequestStatus">
						<s:form action="AdminAction" method="POST" validate="true">
							<tr>
								<td><s:textfield size="2" name="userid" readonly="true"
										value="%{userid}" /></td>
								<td><s:textfield size="5" name="firstname"
										value="%{firstname}" style="padding:0px;" /></td>
								<td><s:textfield size="10" name="lastname" value="%{lastname}"
										style="padding:0px;" /></td>
								<td><s:textfield size="10" name="email" value="%{email}"
										style="padding:0px;" /></td>
								<td><s:select headerKey="-1" headerValue="Select Designation"
										list="designation_names" name="curr_designation"
										value="user_desig_sel[userid]" /></td>
								<td><s:checkboxlist list="department_names"
										name="curr_department" value="user_depart_sel[userid]"
										theme="vertical-checkbox" /></td>
								<td><s:submit type="button" value="Update"
										method="update_user_info" /> <s:submit type="button" value="Deny"
										method="deny_existing_user" /></td>
							</tr>
						</s:form>
					</s:iterator>
				</table>
	</div>
	</div>
</div>
</body>
</html>