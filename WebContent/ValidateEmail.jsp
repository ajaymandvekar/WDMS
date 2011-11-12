<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, cached-pages"); // HTTP 1.1.
response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
response.setDateHeader("Expires", 0); // Proxies.
%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
font-size:large;
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
	font-style:oblique;
	font-weight:bolder;
	font-size:2em;
	margin-left:35%;
	padding-bottom:10px;
	
}
</style>
<title>Account validation Process</title>
</head>
<body>
<s:action name="ValidateEmailAccountAction" executeResult="true" namespace="/"></s:action>
</body>
</html>