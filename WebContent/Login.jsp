<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, cached-pages"); // HTTP 1.1.
response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
response.setDateHeader("Expires", 0); // Proxies.
%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<style type="text/css">
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

.wwformtable
{
	margin-left:40%;
}
.tdLabel
{
	font-color:blue;
}

a
{
	margin-left:40%;
}


input#LoginAction_label_login
{
	width:100%;
	height:50%;
}

.Label_Login
{
	margin-left:40%;
	margin-top:10%;
	font-size:1.7em;
	font:bolder;
}

.error
{
	margin-left:40%;
	color: red;
}
</style>
<title>Document Management System Login</title> 
</head>
<body>
	<div class="outer-box-container">
		<div class="inner-container">
		<img src="images/dms-banner.jpg" class="banner">
		<s:form action="LoginAction" method="POST" validate="true">
			<div class="Label_login">Sign In</div>
			<div class="error"><s:actionerror /></div>
			<s:textfield name="username" key="label.username" size="20" style="padding:2px;" cssClass="textfield">
			</s:textfield>
			<s:password name="password" key="label.password" size="20" style="padding:2px;"/>
			<s:submit key="label.login" align="center" />
		</s:form>
		<s:url id="TemporaryAccount" action="NewUserRegisterAction"></s:url>
		<s:a href="%{TemporaryAccount}">Not yet registered? Register here</s:a>
		</div>
	</div>
</body>
</html>