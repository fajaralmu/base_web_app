package com.fajar.entitymanagement.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fajar.entitymanagement.entity.RegisteredRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1210492423406561769L;
	private Map<String, RegisteredRequest> registeredApps;
	public static final String ATTR_REQUEST_URI = "requestURI";
	
	public void addNewApp(RegisteredRequest registeredRequest) {
		if(registeredApps == null) {
			registeredApps = new HashMap<>();
		}
		registeredApps.put(registeredRequest.getRequestId(),registeredRequest);
	}
	
	public void remove(String reqId) {
		if(registeredApps == null) {
			registeredApps = new HashMap<>();
		}
		registeredApps.remove(reqId);
	}
	
	public void clear() {
		if(registeredApps == null) {
			registeredApps = new HashMap<>();
		}
		registeredApps.clear();
	}
	
	public RegisteredRequest getRequest(String reqId) {
		if(registeredApps == null) {
			registeredApps = new HashMap<>();
		}
		return registeredApps.get(reqId);
	}

	public void setActiveSession(String requestId, boolean active) {
		try { 
			getRequest(requestId).setActive(active);
		}catch (Exception e) {
			// TODO: handle exception
		}
	} 

}
