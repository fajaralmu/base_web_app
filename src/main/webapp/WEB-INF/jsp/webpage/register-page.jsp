
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<script type="text/javascript">
	var ctxPath = "${contextPath}";
	function register() {

		var username = _byId("useraname").value;
		var name = _byId("name").value;
		var password = _byId("password").value;
		var request = new XMLHttpRequest();
		infoLoading();
		var requestObject = {
			'user' : {
				'displayName' : name,
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
				});
	}
</script>

<div class="content">
	<p id="info" align="center"></p>
	<div class="card" style="max-width: 400px; margin: auto">
		<div class="card-header">Register New User</div>
		<div class="card-body">
			<div class="login-form">
				<label for="useraname"> Username </label>
				<input id="useraname"
					class="form-control" type="text" /> 
				<label for="name">
					Display Name </label> 
				<input id="name" class="form-control" type="text" />
				
				<label for="password"> Password </label> <input
					id="password" type="password" class="login-field" /> 
				<button class="btn btn-success" onclick="register(); return false;">Submit</button>
			</div>
		</div>
	</div>
</div>