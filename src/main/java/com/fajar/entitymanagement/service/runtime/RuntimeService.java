package com.fajar.entitymanagement.service.runtime;

import static com.fajar.entitymanagement.util.SessionUtil.PAGE_REQUEST;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.controller.BaseController;
import com.fajar.entitymanagement.dto.SessionData;
import com.fajar.entitymanagement.dto.UserSessionModel;
import com.fajar.entitymanagement.service.LogProxyFactory;
import com.fajar.entitymanagement.service.sessions.SessionValidationService;
import com.fajar.entitymanagement.util.MapUtil;
import com.fajar.entitymanagement.util.SessionUtil;
import com.fajar.entitymanagement.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RuntimeService {
 
//	private final Map<String, Serializable> SESSION_MAP = new LinkedHashMap<>();
	
	@Autowired
	private TempSessionService tempSessionService;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
		set(SessionValidationService.SESSION_DATA, new SessionData());
	}

	/**
	 * 
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	public <T extends Serializable> T getModel(String key, Class<T> _class) {
		try {
			Serializable serializable = tempSessionService.get(key, _class);
			T finalObj = (T) serializable;

			log.info("==registry model: " + finalObj);
			return finalObj;

		} catch (Exception e ) {
			log.error("runtime data error");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param key
	 * @param registryModel
	 * @return
	 */
	public boolean set(String key, Serializable value) {
		try {
			tempSessionService.put(key, value);
			return true;
		} catch (Exception e) {
			log.error("set runtime data error");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean remove(String key, Class<? extends Serializable> _class) {
		try {
			tempSessionService.remove(key, _class);
			return true;
		} catch (Exception e) {
			log.error("runtime data error");
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * @param value
	 * @return
	 */
	public String addPageRequest(String value) {
		String pageRequestId = generateIdKey();

		UserSessionModel model;
		if (getModel(PAGE_REQUEST, UserSessionModel.class) != null) {

			model = getModel(PAGE_REQUEST, UserSessionModel.class);
			model.getTokens().put(pageRequestId, value);

		} else {

			model = new UserSessionModel();
			model.setTokens(MapUtil.singleMap(pageRequestId, value));
		}
		if (set(PAGE_REQUEST, model)) {
			return pageRequestId;
		} else {
			return null;
		}

	}
 
	public void updateSessionId(String newSessionId, String requestId) {
		try {
			((UserSessionModel) getModel(PAGE_REQUEST, UserSessionModel.class)).getTokens().put(requestId, newSessionId);
			log.info("SessionID UPDATED!!");
			
		} catch (Exception e) {
			log.error("runtime data error");
			e.printStackTrace();
		}
	}

	/**
	 * check page request against cookie jsessionID
	 * 
	 * @param httpServletRequest
	 * @return
	 */
	public boolean validatePageRequest(HttpServletRequest httpServletRequest) {
		log.info("Will validate page request");

		try {
			UserSessionModel model =  getModel(PAGE_REQUEST, UserSessionModel.class);

			if (null == model) {
				log.debug("MODEL IS NULL");
				return false;
			}

			Cookie jsessionCookie = BaseController.getJSessionIDCookie(httpServletRequest);
			String pageRequestId = SessionUtil.getPageRequestId(httpServletRequest);

			boolean exist = model.getTokens().get(pageRequestId) != null;

			if (exist) {
				String sessionId = (String) model.getTokens().get(pageRequestId);

				boolean requestIdMatchCookie = sessionId.equals(jsessionCookie.getValue());

				log.debug("sessionId value: {} vs JSessionId cookie: {}", sessionId, jsessionCookie.getValue());
				log.debug("sessionIdMatchCookie: {}", requestIdMatchCookie);

				return requestIdMatchCookie;
			} else {
				log.debug("x x x x Request ID not found x x x x");
				return false;
			}
		} catch (Exception e ) {
			log.error("runtime data error");
			e.printStackTrace();
			return false;
		}
	}

	public boolean createNewSessionData() { 
		return set(SessionValidationService.SESSION_DATA, new SessionData());
	}
	
	private String generateIdKey() {

		return StringUtil.generateRandomNumber(15);
	}
	
	public UserSessionModel getUserSessionModel(String key) {
		return this.getModel(key, UserSessionModel.class);
	}

}
