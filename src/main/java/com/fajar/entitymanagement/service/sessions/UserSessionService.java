package com.fajar.entitymanagement.service.sessions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fajar.entitymanagement.dto.WebRequest;
import com.fajar.entitymanagement.entity.User;

public interface UserSessionService {
	
	public User getUserFromSession(HttpServletRequest request);
	public User getLoggedUser(HttpServletRequest httpRequest);
	public User addUserSession(WebRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception;
	public boolean removeUserSession(HttpServletRequest httpRequest);
	

}
