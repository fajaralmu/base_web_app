package com.fajar.entitymanagement.service;

import static com.fajar.entitymanagement.util.SessionUtil.PAGE_REQUEST;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.controller.BaseController;
import com.fajar.entitymanagement.dto.SessionData;
import com.fajar.entitymanagement.dto.UserSessionModel;
import com.fajar.entitymanagement.util.SessionUtil;
import com.fajar.entitymanagement.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RuntimeService {

//	public static final String PAGE_REQUEST = "page_req_id";
//
//	public static final String PAGE_REQUEST_ID = "requestId";
//
//	public static final String JSESSSIONID = "JSESSIONID";

//	@Autowired
//	private Registry registry;
	private final Map<String, Serializable> SESSION_MAP = new LinkedHashMap<>();

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
		set(UserSessionService.SESSION_DATA, new SessionData());
	}

	/**
	 * 
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	public <T extends Serializable> T getModel(String key) {
		try {
			Serializable serializable = SESSION_MAP.get(key);
			T finalObj = (T) serializable;

			log.info("==registry model: " + finalObj);
			return finalObj;

		} catch (Exception ex) {
			log.info("Unexpected error");
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * @param key
	 * @param registryModel
	 * @return
	 */
	public boolean set(String key, Serializable registryModel) {
		try {
			SESSION_MAP.put(key, registryModel);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean remove(String key) {
		try {
			SESSION_MAP.remove(key);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
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
		if (getModel(PAGE_REQUEST) != null) {

			model = (UserSessionModel) getModel(PAGE_REQUEST);
			model.getTokens().put(pageRequestId, value);

		} else {

			model = new UserSessionModel();
			model.setTokens(new HashMap<String, Object>() {
				{
					put(pageRequestId, value);
				}
			});
		}
		if (set(PAGE_REQUEST, model)) {
			return pageRequestId;
		} else {
			return null;
		}

	}

	private String generateIdKey() {

		return StringUtil.generateRandomNumber(15);
	}

	public void updateSessionId(String newSessionId, String requestId) {
		try {
			((UserSessionModel) getModel(PAGE_REQUEST)).getTokens().put(requestId, newSessionId);
			log.info("SessionID UPDATED!!");
			
		} catch (Exception e) {
			log.error("Error update SessionID on runtime");
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
			UserSessionModel model = (UserSessionModel) getModel(PAGE_REQUEST);

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
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
