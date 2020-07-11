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
<title>${title}</title>
<link rel="icon" href="<c:url value="/res/img/javaEE.ico"></c:url >"
	type="image/x-icon">

<link rel="stylesheet" type="text/css"
	href="<c:url value="/res/css/shop.css?version=1"></c:url>" />
<link rel="stylesheet"
	href="<c:url value="/res/css/bootstrap.min.css" />" />
<script src="<c:url value="/res/js/jquery-3.3.1.slim.min.js" />"></script>
<script src="<c:url value="/res/js/popper.min.js" />"></script>
<script src="<c:url value="/res/js/bootstrap.min.js"  />"></script>
<script src="<c:url value="/res/js/sockjs-0.3.2.min.js"></c:url >"></script>
<script src="<c:url value="/res/js/stomp.js"></c:url >"></script>
<script src="<c:url value="/res/js/websocket-util.js"></c:url >"></script>
<script src="<c:url value="/res/js/ajax.js?v=1"></c:url >"></script>
<script src="<c:url value="/res/js/util.js?v=1"></c:url >"></script>
<script src="<c:url value="/res/js/strings.js?v=1"></c:url >"></script>

<c:forEach var="stylePath" items="${additionalStylePaths }">
	<link rel="stylesheet"
		href="<c:url value="/res/css/pages/${ stylePath.value}.css?version=1"></c:url >" />
</c:forEach>
<c:forEach var="scriptPath" items="${additionalScriptPaths }">
	<script
		src="<c:url value="/res/js/pages/${scriptPath.value }.js?v=1"></c:url >"></script>
</c:forEach>


<style>
.page-li {
	position: relative;
}

.container {
	display: grid;
	grid-template-columns: 20% 80%
}
/**
		active menu when using vertical aligment
	**/
.active {
	font-weight: bold;
}

.centered-align {
	text-align: center;
	width: 100%;
}

.menu-spoiler {
	text-align: left;
	font-size: 0.7em;
	background-color: gray;
	z-index: 1;
	position: absolute;
}

.menu-spoiler>a {
	color: white;
}

#header-wrapper {
	height: 100%;
}
</style>
</head>
<body>
	<div id="progress-bar-wrapper" onclick="hide('progress-bar-wrapper');"
		class="box-shadow"
		style="display: none; height: 50px; padding: 10px; background-color: white; margin: auto; position: fixed; width: 100%">
		<div class="progress">
			<div id="progress-bar"
				class="progress-bar progress-bar-striped bg-info" role="progressbar"
				aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
		</div>
	</div>
	<input id="token-value" value="${pageToken }" type="hidden" />
	<input id="request-id" value="${requestId }" type="hidden" />
	<input id="registered-request-id" value="${registeredRequestId }"
		type="hidden" />
	<div id="loading-div"></div>
	<div class="container">
		<div>
			<jsp:include page="include/head.jsp"></jsp:include>
		</div>
		<div>
			<jsp:include page="${pageUrl == null? 'error/notfound': pageUrl}.jsp"></jsp:include>
		</div>
		<div></div>
		<div>
			<jsp:include page="include/foot.jsp"></jsp:include>
		</div>

	</div>
	<script type="text/javascript">
		const websocketUrl = '${contextPath}/realtime-app';
		function initProgressWebsocket() {
			hide('progress-bar-wrapper');

			addWebsocketRequest('/wsResp/progress/${requestId}', function(
					response) {

				show('progress-bar-wrapper');
				_byId('progress-bar').style.width = response.percentage + "%";
				_byId('progress-bar').setAttribute("aria-valuenow",
						Math.floor(response.percentage));

				if (response.percentage >= 100) {
					hide('progress-bar-wrapper');
				}
			});
		}

		document.body.onload = function() {
			initProgressWebsocket();
			connectToWebsocket();

		}
	</script>
</body>
</html>