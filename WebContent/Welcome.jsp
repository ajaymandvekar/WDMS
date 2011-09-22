<%@page language="java" contentType="text/html" import="java.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
	<head>
		<title>Welcome,you have logged In!!</title>
	</head>
	<body>
		<h2>Welcome<s:property value="username" /></h2>
		<p>Session Time:<%=new Date(session.getLastAccessedTime())%></p>
		<s:a href="UserImage.jsp">Add Customer</s:a>
		<a href="LogoutAction.action">Logout</a>
	</body>
</html>
