<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>Welcome</title>
	</head>
	<body>
		<h2>Welcome<s:property value="username" /></h2>
		<s:a href="UserImage.jsp">Add Customer</s:a>
	</body>
</html>
