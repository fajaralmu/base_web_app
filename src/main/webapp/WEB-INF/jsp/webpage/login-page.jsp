
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<script type="text/javascript">
	var ctxPath = "${contextPath}";
	function login() {

		var username = byId("user-name").value;
		var password = byId("password").value;
		var request = new XMLHttpRequest();
		infoLoading();
		var requestObject = {
			'user' : {
				'username' : username,
				'password' : password
			}
		}
		postReq(
				"<spring:url value="/api/account/login" />",
				requestObject,
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					if (response != null && response.code == "00") {
						alert("LOGIN SUCCESS");
						const redirectLocation = xhr.getResponseHeader("location");
						
						if (redirectLocation!= null) {
							window.location.href = redirectLocation;
						} else
							window.location.href = "<spring:url value="/admin/home" />";
					} else {
						alert("LOGIN FAILED");
					}
				});
	}
 
</script>
<div class="content">
	<p id="info" align="center"></p>
	<div class="card" style="max-width: 400px; margin: auto">
		<div class="card-header">Please Login</div>
		<div class="card-body">
			<div class="login-form">
				<div class="input-group mb-3">
					<div class="input-group-prepend">
						<span class="input-group-text"><i class="fa fa-user-circle"></i></span>
					</div>
					<input placeholder="username" id="user-name" class="form-control" type="text" />
				</div>
				<div class="input-group mb-3">
					<div class="input-group-prepend">
						<span class="input-group-text"> <i class="fa fa-lock"></i></span>
					</div>
					<input placeholder="password" id="password" type="password" class="form-control" />
				</div> 
			</div>
		</div>
		<div class="card-footer">
			<button onclick="login()" id="btn-login" class="btn btn-primary"  >Login</button>
			<a role="button" class="btn btn-success"
				href='<spring:url value="/account/register"></spring:url>'>Register</a>
		</div>
	</div>
</div>
