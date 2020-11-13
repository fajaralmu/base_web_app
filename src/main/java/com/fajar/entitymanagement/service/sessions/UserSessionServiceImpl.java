package com.fajar.entitymanagement.service.sessions;

import static com.fajar.entitymanagement.controller.BaseController.getJSessionIDCookie;
import static com.fajar.entitymanagement.util.SessionUtil.getLoginKey;
import static com.fajar.entitymanagement.util.SessionUtil.getPageRequestId;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.UserSessionModel;
import com.fajar.entitymanagement.dto.WebRequest;
import com.fajar.entitymanagement.entity.User;
import com.fajar.entitymanagement.repository.UserRepository;
import com.fajar.entitymanagement.service.runtime.RuntimeService;
import com.fajar.entitymanagement.util.SessionUtil;
import com.fajar.entitymanagement.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserSessionServiceImpl implements UserSessionService {
	
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private UserRepository userRepository;

	@Override
	public User getUserFromSession(HttpServletRequest request) {
		
		try {
			return SessionUtil.getSessionUser(request);
		} catch (Exception ex) { return null; }
	}

	@Override
	public User getLoggedUser(HttpServletRequest request) {
		try {
			User user = getUserFromSession(request);
			if (user == null && getLoginKey(request) != null) {
				user = getUserFromRuntime(request);
			}
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public User addUserSession(WebRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception{
		
		try {
			User dbUser = this.getUserByUsernameAndPassword(request);

			String loginKey = generateLoginKey();
			dbUser.setLoginKeyAndPasswordNull(loginKey); 

			boolean sessionIsSet = setNewUserSessionModel(dbUser);  

			if (!sessionIsSet) {
				throw new RuntimeException("Error saving session");
			}

			SessionUtil.setLoginKeyHeader(httpResponse, loginKey);
			SessionUtil.setAccessControlExposeHeader(httpResponse);
			SessionUtil.setSessionUser(httpRequest, dbUser);

			log.info("success login");

			return dbUser;
		} catch (Exception e) {

			e.printStackTrace();
			log.error("failed login");
			throw new IllegalAccessException("Login Failed");
		}
	}

	@Override
	public boolean removeUserSession(HttpServletRequest httpRequest) {
		
		try {
			invalidateSessionUser(httpRequest);
			log.info("success logout");
			return true;
			
		} catch (Exception e) {
			
			e.printStackTrace();
			log.info("failed logout");
			return false;
		}
	}
	
	/////////////////// privates ///////////////////////////
	
	private User getUserFromRuntime(HttpServletRequest request) {
		String loginKey = getLoginKey(request);
		UserSessionModel sessionModel = runtimeService.getUserSessionModel(loginKey);

		if (sessionModel == null) { return null; } 
		return sessionModel.getUser();
	}
	
	private User getUserByUsernameAndPassword(WebRequest request) {
		User userFromRequest = request.getUser();
		User userFromDatabase = userRepository.findByUsername(userFromRequest.getUsername());

		if (userFromDatabase != null) {
			log.info("username: {} exist", userFromDatabase.getUsername());
		} else {
			log.error("username: {} does not exist", userFromRequest.getUsername());
			return null;
		}

		boolean passwordMatched = comparePassword(userFromDatabase, userFromRequest.getPassword());
		log.info("Logged User Role: {}", userFromDatabase.getRole());
		
		return passwordMatched ? userFromDatabase : null;
	}
	
	private boolean comparePassword(User dbUser, String password) {
		if (null == password || dbUser == null) {
			return false;
		}

		boolean match = password.equals(dbUser.getPassword());
		log.info("Password match: {}", match);

		return match;
	}
	
	private boolean setNewUserSessionModel(User user) {
		UserSessionModel sessionModel = new UserSessionModel(user, generateUserToken()); 
		return runtimeService.set(user.getLoginKey(), sessionModel); 
	}
	
	private void invalidateSessionUser(HttpServletRequest request) {
		User user = getLoggedUser(request);
		removeUserFromRuntime(user);
		SessionUtil.removeSessionUserAndInvalidate(request);
		try {
			runtimeService.updateSessionId(getJSessionIDCookie(request).getValue(), getPageRequestId(request));
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void removeUserFromRuntime(User user) {
		runtimeService.remove(user.getLoginKey(), UserSessionModel.class);
	}
	
	private String generateLoginKey() {
		return UUID.randomUUID().toString() + "-" + StringUtil.generateRandomNumber(10);
	}

	private String generateUserToken() {
		return StringUtil.generateRandomNumber(10) + "-" + UUID.randomUUID().toString();
	}
	
	

}
