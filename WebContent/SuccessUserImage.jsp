<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>Success: Upload User Image</title>
	</head>
	<body>
		<h2>File Upload Example</h2>
		User Image:<s:property value="userImage"/>
		Content Type:<s:property value="userImageContentType"/>
		File Name:<s:property value="userImageFileName"/>
		Uploaded Image:<img src="<s:property value="userImageFileName"/>"/>
	</body>
</html>
