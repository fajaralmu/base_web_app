package com.fajar.entitymanagement.service.sessions;

import javax.servlet.http.HttpServletRequest;

public interface SessionValidationService {


	public static final String SESSION_DATA = "session_data"; 
	
	public boolean hasSession(HttpServletRequest request);
	public boolean hasSession(HttpServletRequest request, boolean setRequestURI);
	public boolean validatePageRequest(HttpServletRequest httpRequest);
	public String getTokenByServletRequest(HttpServletRequest httpRequest);  
	
}
