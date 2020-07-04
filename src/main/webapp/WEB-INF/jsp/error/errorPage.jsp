<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="icon" href="<c:url value="/res/img/javaEE.ico"></c:url >"
	type="image/x-icon">

<link rel="stylesheet" type="text/css" href="<c:url value="/res/css/shop.css?version=1"></c:url>" /> 
<link rel="stylesheet" href="<c:url value="/res/css/bootstrap.min.css" />" />
<script src="<c:url value="/res/js/jquery-3.3.1.slim.min.js" />" ></script>
<script src="<c:url value="/res/js/popper.min.js" />" ></script>
<script src="<c:url value="/res/js/bootstrap.min.js"  />"></script>
<script src="<c:url value="/res/js/sockjs-0.3.2.min.js"></c:url >"></script>
<script src="<c:url value="/res/js/stomp.js"></c:url >"></script>
<script src="<c:url value="/res/js/websocket-util.js"></c:url >"></script>  
<script src="<c:url value="/res/js/ajax.js?v=1"></c:url >"></script>
<script src="<c:url value="/res/js/util.js?v=1"></c:url >"></script> 

<title>Error: ${errorCode }</title>
<style>
.container{
	padding: 10px;
	margin: auto;
	width: 80%;
	height: auto;
	background-color: white;
}

body{
	background-color: gray;
}
</style>
</head>
<body>
<div class="container">
	<h2>Sorry, error happened with code [${errorCode }]</h2>
	<h4>${errorMessage}</h4>
	<p>Working on it!</p>
	<div style="text-align: center;"><p>${shopProfile.name } ${year }</p></div>
</div>
	
</body>
</html>