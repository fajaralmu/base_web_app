package com.fajar.entitymanagement.service.sessions;

import javax.management.RuntimeErrorException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.UserSessionModel;
import com.fajar.entitymanagement.entity.RegisteredRequest;
import com.fajar.entitymanagement.entity.User;
import com.fajar.entitymanagement.service.runtime.RuntimeService;
import com.fajar.entitymanagement.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SessionValidationServiceImpl implements SessionValidationService {

	@Autowired
	private RuntimeService runtimeService; 
	@Autowired
	private RegisteredRequestService registeredRequestService;
	@Autowired
	private UserSessionService userSessionService;
	
	@Override
	public boolean hasSession(HttpServletRequest request) {
		
		return hasSession(request, true);
	}

	@Override
	public boolean hasSession(HttpServletRequest request, boolean setRequestURI) {
		
		if (setRequestURI && isSaveRequestUri(request)) { 
			SessionUtil.setSessionRequestUri(request);
		}

		/**
		 * handle standAlone Client
		 */
		String loginKey = SessionUtil.getLoginKey(request);
		if (loginKey != null) {

			String remoteAddress = request.getRemoteAddr();
			int remotePort = request.getRemotePort();
			log.info("remoteAddress:" + remoteAddress + ":" + remotePort);
			
			boolean registered = runtimeService.getUserSessionModel(loginKey) != null;
			return registered;
		}

		/**
		 * end handle standAlone Client
		 */  
		try {
			User sessionUser = SessionUtil.getSessionUser(request);
			UserSessionModel sessionModel = runtimeService.getUserSessionModel(sessionUser.getLoginKey());

			if (sessionUser == null || sessionModel == null || !userEquals(sessionUser, sessionModel.getUser())) {
				log.error("==========USER NOT EQUALS==========");
				throw new Exception();
			}
			log.info("USER HAS SESSION, return true");
			return true;

		} catch (Exception ex) {
			log.info("USER DOES NOT HAVE SESSION, return FALSE"); 
			return false;
		}
	}

	@Override
	public boolean validatePageRequest(HttpServletRequest httpServletRequest) {
		
		final String requestId = SessionUtil.getPageRequestId(httpServletRequest);

		log.debug("requestId Provided: {}", requestId);
		if (null == requestId) {
			return false;
		}
		// check if request id is exist from DB
		RegisteredRequest registeredRequest = registeredRequestService.getRegisteredRequest(requestId);

		if (registeredRequest != null) {
			log.debug("Found Registered Request: " + registeredRequest);
			return true;
		}
		log.debug("REQUEST not registered");

		return runtimeService.validatePageRequest(httpServletRequest);
	}

	@Override
	public String getTokenByServletRequest(HttpServletRequest httpRequest) {

		User user = userSessionService.getLoggedUser(httpRequest);
		log.info("::loggedUser: " + (user == null ? null : user.getUsername()));

		if (user == null) { return null; }
		return getTokenByUser(user);
	}
 
	

	////////////////////// privates ////////////////////////
	
	public String getTokenByUser(User user) {
		UserSessionModel reqModel = runtimeService.getUserSessionModel(user.getLoginKey());
		if (reqModel == null) {
			throw new RuntimeErrorException(null, "Invalid Session");
		}
		String token = reqModel.getUserToken();
		return token;
	}
	
	private boolean userEquals(User user1, User user2) {
		try {
			log.info("httpSession loginKey: {}, sessionModel loginKey: {}", user1.getLoginKey(), user2.getLoginKey());
			return user1.getLoginKey().equals(user2.getLoginKey());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean isSaveRequestUri(HttpServletRequest request) {
		 
		return request.getMethod().toLowerCase().equals("get")
				&& request.getRequestURI().contains("login") == false;
	}

}
