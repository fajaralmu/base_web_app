package com.fajar.entitymanagement.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.management.RuntimeErrorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.SessionData;
import com.fajar.entitymanagement.dto.UserSessionModel;
import com.fajar.entitymanagement.dto.WebRequest;
import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.RegisteredRequest;
import com.fajar.entitymanagement.entity.User;
import com.fajar.entitymanagement.repository.RegisteredRequestRepository;
import com.fajar.entitymanagement.repository.UserRepository;
import com.fajar.entitymanagement.util.CollectionUtil;
import com.fajar.entitymanagement.util.EntityUtil;
import com.fajar.entitymanagement.util.SessionUtil;
import com.fajar.entitymanagement.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserSessionService {

	public static final String SESSION_DATA = "session_data";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RegisteredRequestRepository registeredRequestRepository;

	@Autowired
	private RealtimeService2 realtimeService;

	@Autowired
	private RuntimeService registryService;

//	@Autowired
//	private MessagingService messagingService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	/**
	 * get user from httpSession
	 * 
	 * @param request
	 * @return
	 */
	public User getUserFromSession(HttpServletRequest request) {
		try {
			return SessionUtil.getSessionUser(request);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * get user from runtime
	 * 
	 * @param request
	 * @return
	 */
	public User getUserFromRegistry(HttpServletRequest request) {
		String loginKey = SessionUtil.getLoginKey(request);
		UserSessionModel registryModel = registryService.getModel(loginKey);

		if (registryModel == null) {
			return null;
		}

		return registryModel.getUser();
	}

	public User getUserFromRegistry(String loginKey) {
		UserSessionModel registryModel = registryService.getModel(loginKey);

		if (registryModel == null) {
			return null;
		}
		User user = registryModel.getUser();

		return user;
	}

	public boolean hasSession(HttpServletRequest request) {
		return hasSession(request, true);
	}

	public boolean hasSession(HttpServletRequest request, boolean setRequestURI) {
		if (setRequestURI && request.getMethod().toLowerCase().equals("get")
				&& request.getRequestURI().contains("login") == false) {

			SessionUtil.setSessionRequestUri(request);

		}

		/**
		 * handle Client
		 */
		String loginKey = SessionUtil.getLoginKey(request);
		if (loginKey != null) {

			String remoteAddress = request.getRemoteAddr();
			int remotePort = request.getRemotePort();
			log.info("remoteAddress:" + remoteAddress + ":" + remotePort);
			boolean registered = getUserFromRegistry(request) != null;
			return registered;
		}

		/**
		 * end handle Client
		 */
		User sessionUser = SessionUtil.getSessionUser(request);

		try {
			UserSessionModel registryModel = registryService.getModel(sessionUser.getLoginKey().toString());

			if (sessionUser == null || registryModel == null || !sessionUser.equals(registryModel.getUser())) {
				log.error("==========USER NOT EQUALS==========");
				throw new Exception();
			}
			log.info("USER HAS SESSION, return true");
			return true;

		} catch (Exception ex) {
			log.info("USER DOES NOT HAVE SESSION, return FALSE");
//			ex.printStackTrace();
			return false;
		}
	}

	public User addUserSession(final User dbUser, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws IllegalAccessException {
		UserSessionModel registryModel = null;

		try {
			registryModel = new UserSessionModel(dbUser, generateUserToken());

			String loginKey = generateLoginKey();
			dbUser.setLoginKey(loginKey);
			dbUser.setPassword(null);

			boolean registryIsSet = registryService.set(loginKey, registryModel);

			if (!registryIsSet) {
				throw new RuntimeException("Error saving session");
			}

			SessionUtil.setLoginKeyHeader(httpResponse, loginKey);
			SessionUtil.setAccessControlExposeHeader(httpResponse);
			SessionUtil.setSessionUser(httpRequest, dbUser);

			log.info(" > > > SUCCESS LOGIN :");

			return dbUser;
		} catch (Exception e) {

			e.printStackTrace();
			log.info(" < < < FAILED LOGIN");
			throw new IllegalAccessException("Login Failed");
		}
	}

	private String generateLoginKey() {
		return UUID.randomUUID().toString();
	}

	private String generateUserToken() {
		return UUID.randomUUID().toString();
	}

	public boolean logout(HttpServletRequest request) {

		try {

			User user = getLoggedUser(request);
			registryService.remove(user.getLoginKey().toString());
			invalidateSessionUser(request);

			log.info(" > > > > > SUCCESS LOGOUT");
			return true;
		} catch (Exception e) {

			e.printStackTrace();
			log.info(" < < < < < FAILED LOGOUT");
			return false;
		}
	}

	/**
	 * get logged user
	 * 
	 * @param request
	 * @return
	 */
	public User getLoggedUser(HttpServletRequest request) {
		User user = getUserFromSession(request);

		try {
			if (user == null && SessionUtil.getLoginKey(request) != null) {
				user = getUserFromRegistry(request);
			}
			return user;
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	private void invalidateSessionUser(HttpServletRequest request) {

		SessionUtil.removeSessionUserAndInvalidate(request);

	}

	/**
	 * get token
	 * 
	 * @param httpRequest
	 * @return
	 */
	public String getToken(HttpServletRequest httpRequest) {
		User user = getLoggedUser(httpRequest);
		log.info("==loggedUser: " + (user == null ? null : user.getUsername()));

		if (user == null)
			return null;
		return getToken(user);
	}

	/**
	 * get token
	 * 
	 * @param user
	 * @return
	 */
	public String getToken(User user) {
		UserSessionModel reqModel = registryService.getModel(user.getLoginKey());
		if (reqModel == null) {
			throw new RuntimeErrorException(null, "Invalid Session");
		}
		String token = reqModel.getUserToken();
		return token;
	}

	public boolean validatePageRequest(HttpServletRequest httpServletRequest) {
		final String requestId = SessionUtil.getPageRequestId(httpServletRequest);

		if (null == requestId) {
			return false;
		}
		// check from DB
		RegisteredRequest registeredRequest = getRegisteredRequest(requestId);

		if (registeredRequest != null) {
			log.info("Found Registered Request: " + registeredRequest);
			return true;
		}
		log.info("Reuqest not registered");

		return registryService.validatePageRequest(httpServletRequest);
	}

	public RegisteredRequest getRegisteredRequest(String requestId) {

		SessionData sessionData = null;
		RegisteredRequest registeredRequest = registeredRequestRepository.findTop1ByRequestId(requestId);

		log.info("registeredRequest from DB with req Id ({}): {}", requestId, registeredRequest);

		if (null == registeredRequest) {
			sessionData = registryService.getModel(SESSION_DATA);
		}
		if (null != sessionData) {
			registeredRequest = sessionData.getRequest(requestId);
		}

		return registeredRequest;
	}

	private static void removeAttribute(Object object, String... fields) {
		for (String fieldName : fields) {
			Field field = EntityUtil.getDeclaredField(object.getClass(), fieldName);

			try {
				field.set(object, null);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	public WebResponse getProfile(HttpServletRequest httpRequest) {

		User user = getUserFromRegistry(httpRequest);
		if (user != null) {
			removeAttribute(user, "role", "password");
		}
		return WebResponse.builder().code("00").entity(user).build();
	}

	/**
	 * ===================SESSION MANAGEMENT========================
	 * 
	 */

	public WebResponse requestId(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		String requestId;

		if (validatePageRequest(servletRequest)) {
			requestId = SessionUtil.getPageRequestId(servletRequest);// servletRequest.getHeader(RuntimeService.PAGE_REQUEST_ID);

			if (hasSession(servletRequest)) {
				String loginKey = SessionUtil.getLoginKey(servletRequest);
				SessionUtil.setLoginKeyHeader(servletResponse, loginKey);
			}

		} else {

			requestId = generateRequestId();
		}

		SessionData sessionData = generateSessionData(servletRequest, servletResponse, requestId);

		if (!registryService.set(SESSION_DATA, sessionData))
			throw new RuntimeErrorException(null, "Error generating request id");

		log.info("NEW Session Data Created: {}", (SessionData) registryService.getModel(SESSION_DATA));
		realtimeService.sendUpdateSession(getAvailableSessions());

		return WebResponse.builder().code("00").message(requestId).build();
	}

	private String generateRequestId() {

		return StringUtil.generateRandomNumber(17);
	}

	private SessionData generateSessionData(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
			String requestId) {

		SessionData sessionData = registryService.getModel(SESSION_DATA);

		if (null == sessionData) {
			if (!registryService.set(SESSION_DATA, new SessionData()))
				throw new RuntimeErrorException(null, "Error getting session data");

			sessionData = registryService.getModel(SESSION_DATA);
		}

		RegisteredRequest requestv2 = SessionUtil.buildRegisteredRequest(servletRequest, requestId);
		sessionData.addNewApp(requestv2);
		return sessionData;
	}

	/**
	 * key for client app
	 * 
	 * @return
	 */
	public WebResponse getAvailableSessions() {

		List<BaseEntity> appSessions = CollectionUtil.convertList(getAvailableSessionList());

//		for (BaseEntity appSession : appSessions) {
//			List<BaseEntity> messages = messagingService.getMessages(((RegisteredRequest) appSession).getRequestId());
//			((RegisteredRequest) appSession).setMessages(messages);
//		}
		return WebResponse.builder().code("00").entities(appSessions).build();
	}

	private List<RegisteredRequest> getAvailableSessionList() {
		SessionData sessionData = registryService.getModel(SESSION_DATA);

		if (null == sessionData) {
			log.info("Session Data IS NULL");
			boolean successSettingRegistry = registryService.set(SESSION_DATA, new SessionData());

			if (!successSettingRegistry)
				throw new RuntimeErrorException(null, "Error updating session data");

			sessionData = registryService.getModel(SESSION_DATA);
		} else {
			log.info("sessionData found: {}", sessionData);
		}

		List<RegisteredRequest> appSessions = CollectionUtil.mapToList(sessionData.getRegisteredApps());

		return appSessions;
	}

	public void setActiveSession(String requestId, boolean active) {
		SessionData sessionData = registryService.getModel(SESSION_DATA);
		if (null == sessionData) {
			return;
		}
		((SessionData) registryService.getModel(SESSION_DATA)).setActiveSession(requestId, active);
	}

	public RegisteredRequest getAvailableSession(String requestId) {

		List<RegisteredRequest> sessionList = CollectionUtil.convertList(getAvailableSessionList());
		for (RegisteredRequest baseEntity : sessionList) {
			if (baseEntity.getRequestId().equals(requestId)) {
				return baseEntity;
			}
		}
		return null;
	}

	public WebResponse deleteSession(WebRequest request) {
		SessionData sessionData = registryService.getModel(SESSION_DATA);
		String requestId = request.getRegisteredRequest().getRequestId();
		
		sessionData.remove(requestId );

		if (!registryService.set(SESSION_DATA, sessionData))
			throw new RuntimeErrorException(null, "Error updating session data");

		return WebResponse.builder().code("00").sessionData(sessionData).build();
	}

	public WebResponse clearSessions() {
		SessionData sessionData = registryService.getModel(SESSION_DATA);
		sessionData.clear();

		if (!registryService.set(SESSION_DATA, sessionData))
			throw new RuntimeErrorException(null, "Error updating session data");

		sessionData = registryService.getModel(SESSION_DATA);

		realtimeService.sendUpdateSession(getAvailableSessions());
		return WebResponse.builder().code("00").sessionData(sessionData).build();
	}

	public String getPageCode(HttpServletRequest request) {
		log.info("getPageCode");
		try {
			String pageCode = SessionUtil.getSessionPageCode(request);
			log.info("pageCode: {}", pageCode);
			return pageCode;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setActivePage(HttpServletRequest request, String pageCode) {
		if(null == pageCode) {
			log.info("will not setActivePage, pageCode IS NULL");
			return;
		}
		log.info("setActivePage: {}", pageCode);
		try {
			SessionUtil.setSessionPageCode(request, pageCode);
			log.info("pageCode: {}", request.getSession().getAttribute("page-code"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * get user by userName and password
	 * 
	 * @param request
	 * @return
	 */
	public User getUserByUsernameAndPassword(WebRequest request) {
		User requestUser = request.getUser();
		User dbUser = userRepository.findByUsername(requestUser.getUsername());

		if (dbUser != null) {
			log.info("username: {} exist", dbUser.getUsername());
		} else {
			log.error("username: {} does not exist", requestUser.getUsername());
			return null;
		}

		boolean passwordMatched = comparePassword(dbUser, requestUser.getPassword());
		log.info("Logged User Role: {}", dbUser.getRole());
		return passwordMatched ? dbUser : null;
	}

	private boolean comparePassword(User dbUser, String password) {
		if (null == password || dbUser == null) {
			return false;
		}

		boolean match = password.equals(dbUser.getPassword());
		log.info("Password match: {}", match);

		return match;
	}

}
