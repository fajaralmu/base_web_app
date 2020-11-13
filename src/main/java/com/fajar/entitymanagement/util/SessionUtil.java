package com.fajar.entitymanagement.util;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.RegisteredRequest;
import com.fajar.entitymanagement.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionUtil {
	public static final String PAGE_REQUEST = "page_req_id";
	public static final String ATTR_USER = "user";
	public static final String PAGE_REQUEST_ID = "requestId";
	public static final String JSESSSIONID = "JSESSIONID";
	public static final String HEADER_LOGIN_KEY = "loginKey";
	public static final String ACCESS_CONTROL_EXPOSE_HEADER = "Access-Control-Expose-Headers";
	public static final String ATTR_REQUEST_URI = "requestURI";
	public static final String ATTR_REGISTERED_REQUEST_ID = "registered_request_id";
	public static final String PAGE_CODE = "page-code";
	public static final String HEADER_REQUEST_TOKEN = "requestToken";

	public static String getPageRequestId(HttpServletRequest httpServletRequest) {
		String pageRequest = httpServletRequest.getHeader(PAGE_REQUEST_ID);
		log.info("Page request id: " + pageRequest);
		return pageRequest;
	}

	public static String getLoginKey(HttpServletRequest request) {

		return request.getHeader(HEADER_LOGIN_KEY);
	}

	public static void setLoginKeyHeader(HttpServletResponse servletResponse, String loginKey) {

		servletResponse.addHeader(HEADER_LOGIN_KEY, loginKey);
	}

	public static void setAccessControlExposeHeader(HttpServletResponse httpResponse) {

		httpResponse.addHeader(ACCESS_CONTROL_EXPOSE_HEADER, "*");
	}

	public static User getSessionUser(HttpServletRequest request) {
		if (request.getSession(false) == null) {
			return null;
		}
		try {
			Object result = request.getSession(false).getAttribute(ATTR_USER);
			return result == null && !(result instanceof User) ? null : (User) result;
		} catch (Exception e) {
			log.info("invalid session object: {}", e);
			return null;
		}
	}

	public static void setSessionRequestUri(HttpServletRequest request) {

		request.getSession().setAttribute(ATTR_REQUEST_URI, request.getRequestURI());
		log.info("REQUESTED URI: " + request.getRequestURI());
	}

	public static void updateSessionUser(HttpServletRequest httpRequest, User user) {
		User currentUser = getSessionUser(httpRequest);
		if (null == currentUser) {
			log.info("current user session not found");
			return;
		}
		if (user.getLoginKey() == null || user.getLoginKey().isEmpty()) {
			user.setLoginKey(currentUser.getLoginKey());
		}
		user.setPassword(null);
		setSessionUser(httpRequest, user);
	}

	public static void setSessionUser(HttpServletRequest httpRequest, User dbUser) {

		httpRequest.getSession(true).setAttribute(ATTR_USER, dbUser);
	}

	public static void removeSessionUserAndInvalidate(HttpServletRequest request) {

		request.getSession(false).removeAttribute(ATTR_USER);
		request.getSession(false).invalidate();

	}

	public static String getSessionRequestUri(HttpServletRequest httpRequest) {
		try {
			return httpRequest.getSession(false).getAttribute(ATTR_REQUEST_URI).toString();
		} catch (Exception e) {
			return null;
		}
	}

	public static void setSessionRegisteredRequestId(HttpServletRequest httpRequest, WebResponse requestIdResponse) {

		httpRequest.getSession(false).setAttribute(ATTR_REGISTERED_REQUEST_ID, requestIdResponse.getMessage());

	}

	public static RegisteredRequest buildRegisteredRequest(HttpServletRequest servletRequest, String requestId) {
		String ipAddress = servletRequest.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = servletRequest.getRemoteAddr();
		}

		String referrer = servletRequest.getHeader("Referer");
		String userAgent = servletRequest.getHeader("User-Agent");

		RegisteredRequest request = RegisteredRequest.builder().ipAddress(ipAddress).referrer(referrer)
				.userAgent(userAgent).requestId(requestId).created(new Date()).value(null).build();
		return request;
	}

	public static String getSessionPageCode(HttpServletRequest request) {
		try {
			return request.getSession().getAttribute(PAGE_CODE).toString();
		} catch (Exception e) {

			return null;
		}
	}

	public static void setSessionPageCode(HttpServletRequest request, String pageCode) {
		try {
			request.getSession(false).setAttribute(PAGE_CODE, pageCode);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static String getRequestToken(HttpServletRequest httpRequest) {
		try {
			return httpRequest.getHeader(HEADER_REQUEST_TOKEN).toString();
		} catch (Exception e) {
			return null;
		}
	}

	public static String getSessionRegisteredRequest(HttpServletRequest request) {

		try {
			return request.getSession().getAttribute(ATTR_REGISTERED_REQUEST_ID).toString();
		} catch (Exception e) {

		}
		return null;
	}

	public static void setUserInRequest(HttpServletRequest request, User authenticatedUser) {
		if (null == authenticatedUser) {
			return;
		}
		String requestId = getPageRequestId(request);
		authenticatedUser.setRequestId(requestId);
		request.setAttribute(ATTR_USER, authenticatedUser);
	}

	public static User getUserFromRequest(HttpServletRequest request) {
		try {
			return (User) request.getAttribute(ATTR_USER);
		} catch (Exception e) {
			return null;
		}
	}

}
