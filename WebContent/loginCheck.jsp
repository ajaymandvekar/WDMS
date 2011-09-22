<%@taglib prefix="s"uri="/struts-tags"%> 
<%@page language="java" contentType="text/html" import ="java.util.*"%>
<html> 
	<head> 
		<title>Check validate!</title> 
	</head> 
	<body> 
		<s:if test="#session.login != 'admin'"> <jsp:forward page="Login.jsp"/></s:if> 
	</body>
</html>