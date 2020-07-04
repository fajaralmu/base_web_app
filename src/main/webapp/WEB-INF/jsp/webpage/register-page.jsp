
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1256">
<title>Shop::Register</title>
<link rel="icon" href="<c:url value="/res/img/javaEE.ico"></c:url >"
	type="image/x-icon">

<link rel="stylesheet" type="text/css"
	href=<c:url value="/res/css/shop.css?version=1"></c:url> />

<script src="<c:url value="/res/js/ajax.js?v=1"></c:url >"></script>
<script src="<c:url value="/res/js/util.js?v=1"></c:url >"></script>

<link rel="stylesheet" href="<c:url value="/res/css/bootstrap.min.css" />" />
<script src="<c:url value="/res/js/jquery-3.3.1.slim.min.js" />" ></script>
<script src="<c:url value="/res/js/popper.min.js" />" ></script>
<script src="<c:url value="/res/js/bootstrap.min.js"  />"></script>

<script type="text/javascript">
	var ctxPath = "${contextPath}";
	function register() {

		var username = document.getElementById("useraname").value;
		var name = document.getElementById("name").value;
		var password = document.getElementById("password").value;
		var request = new XMLHttpRequest();
		infoLoading();
		var requestObject = {
			'user' : {
				'displayName':name,
				'username' : username,
				'password' : password
			}
		}
		postReq(
				"<spring:url value="/api/account/register" />",
				requestObject,
				function(xhr) {
					var response = xhr.data;
					if (response != null && response.code == "00") {
						alert("register SUCCESS");
						window.location.href = "<spring:url value="/account/login" />";
					} else {
						alert("register FAILS");
					}
				} );
	}
</script>
 
</head>
<body> 
<div id="loading-div"></div>
	<div class="body">
		<p id="info" align="center"></p>
		<div class="wrapper-login-form">

			<div class="login-form">
			 	<span style="font-size: 2em;">Silakan Register</span> <br> 
				<label for="useraname"> Username </label> <br>  <input
					id="useraname" class="form-control" type="text" /> <br />  
				<label for="name"> Display Name </label> <br>  <input
					id="name" class="form-control" type="text" /> <br />  
				<label for="password"> Password </label> <br>   <input
					id="password" type="password" class="login-field" /> <br /> 
				<button class="btn btn-success"    onclick="register(); return false;">Submit</button>
				
			</div>
		</div>
	</div>
</body>
</html>