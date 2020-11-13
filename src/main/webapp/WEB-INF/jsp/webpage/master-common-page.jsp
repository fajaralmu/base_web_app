<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div class="content" style="background-color: ${page.color}; height: 100%; padding: 10px">
	<div class="common-page-header">
		<h2><i class="fa fa-${page.getIconClass() }" aria-hidden="false"></i> ${page.name}</h2>
		<p>Good ${timeGreeting}, ${loggedUser.displayName}. Have a great day!</p>
		<p>${page.description }</p>
	</div>
	<div class="row" style="grid-row-gap: 10px">
		<%-- <c:forEach var="menu" items="${page.menus }">
			<div class="col-sm-3">
				<div class="card" style="width: 100%;">
					<img class="card-img-top"  width="100" height="200" src="${host}/${contextPath}/${imagePath}/${menu.iconUrl }"
						alt="Card image cap">
					<div class="card-body" style="background-color:${menu.color }; color:${menu.fontColor }">
						<h5 class="card-title">
							 ${menu.name } 
						</h5>
						<a class="badge badge-primary"
							data-toggle="tooltip" data-placement="bottom"
							title="${menu.description }" href="<spring:url value= "${menu.url }" />">Detail</a>
					</div>
				</div>
			</div>
		</c:forEach> --%>
		<c:forEach var="menu" items="${page.menus }">
			<div class="col-2">
				<div class="menu-item shadow-sm p-3 mb-5 rounded" 
					style="width: 100%; height:80%; text-align: center; background-color:${menu.color }">
					<img style="margin-top: 10px" width="50" height="50" src="${host}/${contextPath}/${imagePath}/${menu.iconUrl }">
					<div>
						<h6><a style="color:${menu.fontColor }"
							class="menu-item-link"
							data-toggle="tooltip" data-placement="bottom"
							path-variables="${menu.pathVariablesString() }"
							title="${menu.description }" 
							href-original="<spring:url value= "${menu.url }" />"
							href="<spring:url value= "${menu.url }" />">${menu.name }</a></h6>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
	<p></p>
</div>
<script type="text/javascript">
	byId("content-wrapper").style.padding= 0;
	
	const menuItemLinks = document.getElementsByClassName("menu-item-link");
	
	function setMenuItemHavingPathVariablesOnclick(menuItemLink, pathVariableString){  
			 
		menuItemLink.href = "#";
		menuItemLink.onclick = function(e){
			 
			const pathVariables = pathVariableString.split(",");
			const rawLink = e.target.getAttribute("href-original");
			
			promptDialogMultiple("Provide Path Variables", pathVariables)
			.then(function(result){
				if(result.ok){
					console.debug("result.value:",result.value);
					let finalLink = rawLink;
					for (var key in result.value) {
						const value = result.value[key];
						if(value == null || value == ""){
							alert("Please provide the "+key+"!");
							return;
						}
						finalLink = finalLink.replace("{"+key+"}", value);
						window.location.href = finalLink;
						
					}
				}
			});
		}
	}
	
	function initMenuItemEvents(){
		for (var i = 0; i < menuItemLinks.length; i++) {
			const menuItemLink = menuItemLinks[i];
			const pathVariableString = menuItemLink.getAttribute("path-variables");
			
			if(pathVariableString!=null && pathVariableString!=""){
				setMenuItemHavingPathVariablesOnclick(menuItemLink, pathVariableString); 
			}
		}
	}
	
	initMenuItemEvents();
</script>