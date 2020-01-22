<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Home Page</title>
</head>
<body>
	<h2>
		luv2Spring Company Home Page
		</h1>
		<hr>
		<p>welcome to luv2Spring Company Home Page!!</p>
		<hr>
		<!-- Display username and role -->
		<p>
			User:
			<security:authentication property="principal.username" />
			<br>
			<br> Role(s):
			<security:authentication property="principal.authorities" />

		</p>
		<hr>

		<!-- Adding logout button -->
		<form:form action="${pageContext.request.contextPath}/logout"
			method="POST">
			<input type="submit" value="Logout">
		</form:form>
</body>
</html>