package com.fajar.entitymanagement.service.sessions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fajar.entitymanagement.dto.WebRequest;
import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.RegisteredRequest;

public interface RegisteredRequestService {
	
public RegisteredRequest getRegisteredRequest(String requestId);
	
	public WebResponse getAvailableSessions();
	public WebResponse deleteSession(WebRequest request);
	public WebResponse clearSessions();
	public WebResponse generateRequestId(HttpServletRequest httpRequest, HttpServletResponse httpResponse);

}
