<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<%@ include file="bootstrapTemplate.jsp" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <title>Appointment Service</title>
</head>

<body>
    <h1>Hi ${name} Welcome To Appointment Service </h1>
    <button>
        <a href="/logout">Logout</a>
    </button>
    <div class="container mt-2">
        <div class="center">

            <c:forEach items="${specialities}" var="speciality">
                <div class="card mb-3">
                    <div class="row no-gutters">
                        <div class="col-md-8">
                            <div class="card-body">
                                <h5 class="card-title">${speciality.title}</h5>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</body>

</html>