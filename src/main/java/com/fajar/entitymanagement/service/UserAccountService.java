package com.fajar.entitymanagement.service;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.WebRequest;
import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.User;
import com.fajar.entitymanagement.entity.UserRole;
import com.fajar.entitymanagement.repository.UserRepository;
import com.fajar.entitymanagement.repository.UserRoleRepository;
import com.fajar.entitymanagement.service.sessions.RegisteredRequestService;
import com.fajar.entitymanagement.service.sessions.SessionValidationService;
import com.fajar.entitymanagement.service.sessions.UserSessionService;
import com.fajar.entitymanagement.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserAccountService {

	@Autowired
	private UserSessionService userDataService;
	@Autowired
	private SessionValidationService sessionValidationService;
	@Autowired
	private RegisteredRequestService registeredRequestService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private WebConfigService webConfigService;
	@Autowired
	private ProgressService progressService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	/**
	 * add new user
	 * 
	 * @param request
	 * @return
	 */
	public WebResponse registerUser(WebRequest request, HttpServletRequest httpServletRequest) {

		try {
			UserRole defaultUserRole = webConfigService.defaultRole();
			progressService.sendProgress(30, httpServletRequest);
			
			User user = populateUser(request, defaultUserRole);
			userRepository.save(user);
			progressService.sendProgress(40, httpServletRequest);
			
			log.info("success register");
			return WebResponse.success();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private User populateUser(WebRequest request, UserRole regularRole) {
		User user = new User();
		user.setDisplayName(request.getUser().getDisplayName());
		user.setDeleted(false);
		user.setRole(regularRole);
		user.setPassword(request.getUser().getPassword());
		user.setUsername(request.getUser().getUsername());
		return user;
	}

	/**
	 * login to system
	 * 
	 * @param request
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 * @throws IllegalAccessException
	 */
	public WebResponse login(WebRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws Exception {
		
		User dbUser = userDataService.addUserSession(request, httpRequest, httpResponse); 
		
		if (dbUser == null) {
			return new WebResponse("01", "invalid credential");
		}
		
		registerRequestId(httpRequest, httpResponse); 
		setSessionRequestUri(httpRequest, httpResponse); 
		
		try {
			httpRequest.login(dbUser.getUsername(), dbUser.getPassword());
			httpRequest.authenticate(httpResponse);
		}catch (Exception e) {
			log.error("Error servlet login");
			e.printStackTrace();
		}
		
		log.info("Login Success"); 
		return WebResponse.success();
	}

	private static void setSessionRequestUri(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		 
		String sessionRequestUri = SessionUtil.getSessionRequestUri(httpRequest);

		if (sessionRequestUri != null) {
			log.debug("goto page after login: " + sessionRequestUri);
			httpResponse.setHeader("location", sessionRequestUri); 
		} 
	}

	private void registerRequestId(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		 
		WebResponse requestIdResponse = registeredRequestService.generateRequestId(httpRequest, httpResponse);
		SessionUtil.setSessionRegisteredRequestId(httpRequest, requestIdResponse);

	}

	/**
	 * logout from system
	 * 
	 * @param httpRequest
	 * @return
	 */
	public boolean logout(HttpServletRequest httpRequest) {

		boolean logoutResult = userDataService.removeUserSession(httpRequest);
		try {
			httpRequest.logout();
		} catch (ServletException e) {
			log.error("Error Logging Out");
			e.printStackTrace();
		}
		return logoutResult;
	}

	/**
	 * validate session token & registry token
	 * 
	 * @param httpRequest
	 * @return
	 */
	public boolean validateToken(HttpServletRequest httpRequest) {
		boolean hasSession = sessionValidationService.hasSession(httpRequest);
		if(hasSession) {
			return true;
		}
		String requestToken = SessionUtil.getRequestToken(httpRequest);
		
		return validateToken(requestToken, httpRequest);
		/**
		 * TESTING
		 */
//		boolean pageRequestValidated = sessionValidationService.validatePageRequest(httpRequest);
//		if (pageRequestValidated) {
//			return true;
//		} else {
//			return validateToken(requestToken, httpRequest);
//		}
	}

	/**
	 * compare token
	 * 
	 * @param requestToken
	 * @param httpRequest
	 * @return
	 */
	private boolean validateToken(String requestToken, HttpServletRequest httpRequest) {

		if (requestToken == null) {
			log.error("NULL TOKEN, invalid request");
			return false;

		} else {

			String existingToken = sessionValidationService.getTokenByServletRequest(httpRequest);
			log.info("|| REQUEST_TOKEN: " + requestToken + " vs EXISTING:" + existingToken + "||");

			boolean tokenEquals = requestToken.equals(existingToken);
			return tokenEquals;
		}
	}

	public UserRole getRole(User user) {
		log.info("Role From User: {}", user.getRole());
		Optional<UserRole> dbUserRole = userRoleRepository.findById(user.getRole().getId());
		return dbUserRole.get();
	}

	public WebResponse checkUsername(WebRequest request) {
		String username = request.getUsername();
		User user = userRepository.findByUsername(username);
		String code = user == null ? "00":"01";
		String msg = user == null ? "Username Available": "Username Not Available";
		
		return WebResponse.builder().code(code).message(msg).build();
	}

}
