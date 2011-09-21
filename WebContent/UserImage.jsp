<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
	<head>
		<title>Upload User Image</title>
	</head>
	<body>
		<h1>File upload and Save</h1>
		<s:actionerror />
		<s:form action="userImage" method="post" enctype="multipart/form-data">
			<s:file name="userImage" label="User Image" />
			<s:submit value="Upload" align="center" />
		</s:form>
	</body>
</html>
