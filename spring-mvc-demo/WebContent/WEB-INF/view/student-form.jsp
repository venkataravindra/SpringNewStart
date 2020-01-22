<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Student Registration Form</title>
</head>
		<body>
			<form:form action="processForm" modelAttribute="student">
		     
		     FirstName <form:input path="firstName" />
		
				<br>
				<br>
		     
		     LastName <form:input path="lastName" />
				<br>
				<br>
				Gender:
				 Male <form:radiobutton path="gender" value="male"/>
				 Fe-Male <form:radiobutton path="gender" value="fe-male"/>
				 <br>
				 <br>
			 country <form:select path="country" >
			 				<form:options items="${student.countryOptions}"/>
							<%-- <form:option value="Brazil" label="Brazil"/>
							<form:option value="France" label="France"/>
							<form:option value="Germany" label="Germany"/>
							<form:option value="India" label="India"/> --%>
				     </form:select>	
				<br>
				<br>
				Subjects:
				Telugu <form:checkbox path="subjects" value="Telugu"/>
				Hindi <form:checkbox path="subjects" value="Hindi"/>
				English <form:checkbox path="subjects" value="English"/>
				Maths <form:checkbox path="subjects" value="Maths"/>
				Science<form:checkbox path="subjects" value="Science"/>
				Social <form:checkbox path="subjects" value="Social"/>
				<br><br>
				<input type="submit" value="submit" />
		
			</form:form>
</body>
</html>