<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="content">
	<div id="welcome-header" class="page-header" style="padding:5px;
		margin-top:10px;margin-bottom:10px; 
	 	color:white;text-align: center; 
	  	background-position: center;
		  background-repeat: no-repeat;
		  background-size: cover;
	 	background-image: url('${host}/${contextPath}/${imagePath}/${shopProfile.backgroundUrl }'); 
	 	color:${shopProfile.fontColor }; 
	 	width:100%; font-size:3 em">
		<h3>Assalamu'alaikum, Good ${timeGreeting} Welcome to</h3>
		<h1> ${shopProfile.name }</h1>
		<p></p>
		<p>${shopProfile.welcomingMessage }</p>
		<p></p>
		<p></p>
		<c:forEach var="imageUrl" items="${imageUrlList }">
			<img src="${imageUrl }" width="40" height="40" />
		</c:forEach>
	</div>
	 

	<div class="row">
		<c:forEach var="menu" items="${menus }">
			<div class="col-sm-3">
				<div class="card" style="width: 100%;">
					<img class="card-img-top" width="100" height="150"
						src="${host}/${contextPath}/${imagePath}/${menu.iconUrl }"
						alt="Card image cap">
					<div class="card-body" style="background-color:${menu.color }; color:${menu.fontColor }">
						<h5 class="card-title">${menu.name }</h5>
						<a role="button" class="badge badge-primary" data-toggle="tooltip"
							data-placement="bottom" title="${menu.description }"
							href="${menu.url }">Detail</a>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
	<p></p>
</div>
