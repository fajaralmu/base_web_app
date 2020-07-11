
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="header" style="height: auto">
	
	<div class="page-header" style="color:${profile.color}">
		<h1>${profile.name }</h1>
		<p>${profile.shortDescription }</p>
	</div>

	<div>
		<!-- <ul class="nav nav-tabs"> -->
		<ul class="nav  flex-column">

			<!-- Account Menu -->
			<c:if test="${loggedUser == null  }">
				<li class="nav-item "><a
					class="nav-link  ${page == 'login' ? 'active':'' }"
					href="<spring:url value="/account/login"/>">Log In </a></li>
			</c:if>
			<c:if test="${loggedUser != null }">
				<div class="dropdown">
					<button class="btn btn-primary dropdown-toggle" type="button"
						data-toggle="dropdown">
						${loggedUser.displayName }<span class="caret"></span>
					</button>
					<div class="dropdown-menu">
						<a class="dropdown-item"
							href="<spring:url value="/management/profile"/>">Profile</a> <a
							class="dropdown-item" href="<spring:url value="/account/logout"/>" onclick="logout()">Logout</a>
					</div>
				</div>
			</c:if>

			<%--  
			<c:if test="${loggedUser != null }">
				<li class="nav-item"><a
					class="nav-link ${page == 'dashboard' ? 'active':'' }"
					href="<spring:url value="/admin/home"/>">Dashboard</a></li> 
			</c:if> --%>

			<c:forEach var="pageItem" items="${pages}">
				<li class="nav-item page-li"  ><a
					class="nav-link pagelink" id="${pageItem.code }"
					menupage="${pageItem.isMenuPage() }"
					href="<spring:url value="${pageItem.link }"/>">${pageItem.name }</a></li>

			</c:forEach>

		</ul>
	</div>
</div>
<script type="text/javascript">
	document.body.style.backgroundColor = "${profile.color}";

	var pagesLink = document.getElementsByClassName("pagelink");
	var pageMenus = {};
	var ctxPath = "${contextPath}";
	function logout() {
		/* postReq(
				"<spring:url value="/api/account/logout" />",
				{},
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					if (response != null && response.code == "00") {

						window.location.href = "<spring:url value="/account/login" />";
					} else {
						alert("LOGOUT FAILS");
					}
				}); */
	}

	function getCurrentPageCode() {
		try{
			postReq("<spring:url value="/api/public/pagecode" />", {},
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					var pageCode = response.code;
					if(_byId(pageCode))
						_byId(pageCode).setAttribute("class",
								"nav-link pagelink active");
				});
		}catch (e) {
			 console.log("Error occured.. when getCurrentPageCode");
		}
	}

	function initPagesLinkEvent() {
		for (let i = 0; i < pagesLink.length; i++) {
			pageLink = pagesLink[i];

			if (pageLink.getAttribute("menupage") == "true") {
				pageLink.onmouseover = function(e) {
					fetchMenus(e);
				};
			} else {
				pageLink.onmouseover = function(e) {
					hideAllMenuSpoiler();
				}
			}
		}

		_byId("header-wrapper").onmouseleave = function(e) {
			hideAllMenuSpoiler();
		}
	}

	function fetchMenus(e) {
		const pageCode = e.target.id;

		if (pageMenus[pageCode] == null) {
			const url = "<spring:url value="/api/public/menus/" />" + pageCode;
			postReq(url, {}, function(xhr) {
				infoDone();
				var response = (xhr.data);
				var menus = response.entities;
				pageMenus[pageCode] = menus;
				showMenuList(pageCode);
			});
		} else {
			showMenuList(pageCode);
		}
	}

	function showMenuList(pageCode) {
		hideAllMenuSpoiler();

		const menus = pageMenus[pageCode];
		console.log("MENUS:", menus);
		const menuContainer = createGridWrapper(1, "100%");
		const parentElement = _byId(pageCode).parentElement;

		if (parentElement.childElementCount > 1) {
			hideMenuByPageCode(pageCode);
		}

		menuContainer.style.width = "60%";
		menuContainer.style.textAlign = "center";
		menuContainer.setAttribute("id", "menu-spoiler-" + pageCode);
		menuContainer.setAttribute("class", "menu-spoiler");
		menuContainer.innerHTML = "<h5>Available Menu</h5>";

		for (var i = 0; i < menus.length; i++) {
			const menu = menus[i];
			const url = "<spring:url value="/"/>" + menu.url;
			const link = createAnchor(menu.code, menu.name, url);
			menuContainer.appendChild(link);
		}
		parentElement.appendChild(menuContainer);
	}

	function hideMenus(e) {
		hideMenuByPageCode(e.target.id);
	}

	function hideMenuByPageCode(code) {
		const pageLink = _byId(code);
		//console.log("HIDE ", code)
		const parentElement = pageLink.parentElement;
		if (parentElement.childElementCount > 1) {
			parentElement.removeChild(parentElement.lastChild);
		}
	}

	function hideAllMenuSpoiler() {
		for (let i = 0; i < pagesLink.length; i++) {

			hideMenuByPageCode(pagesLink[i].id);
		}
	}

//	initPagesLinkEvent();
	getCurrentPageCode();
</script>