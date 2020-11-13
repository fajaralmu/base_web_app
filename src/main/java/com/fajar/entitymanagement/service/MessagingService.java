package com.fajar.entitymanagement.service;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.WebRequest;
import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.Message;
import com.fajar.entitymanagement.entity.RegisteredRequest;
import com.fajar.entitymanagement.repository.EntityRepository;
import com.fajar.entitymanagement.repository.MessageRepository;
import com.fajar.entitymanagement.service.sessions.RegisteredRequestService;

@Service
public class MessagingService {

	@Autowired
	private RealtimeService2 realtimeService;

//	@Autowired
//	private MessageRepository messageRepository;

	@Autowired
	private RegisteredRequestService registeredRequestService;

	@Autowired
	private EntityRepository entityRepository;
 
	@Autowired
	private MessageRepository messageRepository;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public List<Message> getMessages(String requestId) {

		return messageRepository.findByRequestId(requestId);
	}

	public WebResponse getMessages(HttpServletRequest httpRequest) {

		String requestId = httpRequest.getHeader("requestId");
		WebResponse response = WebResponse.builder().code(requestId).build();
		response.setEntities(getMessages(requestId));
		
		realtimeService.sendMessageChatToClient(response, requestId);
		return response;
	}

	/**
	 * send message to admin
	 * @param request
	 * @param httpRequest
	 * @return
	 */
	public WebResponse sendMessageToAdmin(WebRequest request, HttpServletRequest httpRequest) {
		String content = request.getValue();
		String requestId = httpRequest.getHeader("requestId");

		RegisteredRequest registeredRequest = registeredRequestService.getRegisteredRequest(requestId);

		Message message = new Message(requestId, content, new Date(), requestId);
		message.setAlias(request.getUsername() == null ? "" : request.getUsername());
		message.setUserAgent(registeredRequest.getUserAgent());
		message.setIpAddress(registeredRequest.getIpAddress());
		putMessage(requestId, message);

		WebResponse response = WebResponse.builder().code(requestId).build();
		response.setEntities(getMessages(requestId));
		realtimeService.sendChatMessageToAdmin(response);
		return response;
	}

	/**
	 * reply messages to client
	 * @param request
	 * @param httpRequest
	 * @return
	 */
	public WebResponse replyMessageToClient(WebRequest request, HttpServletRequest httpRequest) {
		String content = request.getValue();

		RegisteredRequest registeredRequest = registeredRequestService.getRegisteredRequest(request.getDestination());

		Message message = new Message("ADMIN", content, new Date() , request.getDestination());
		message.setAdmin(1);
		putMessage(request.getDestination(), message);
		message.setUserAgent(registeredRequest.getUserAgent());
		message.setIpAddress(registeredRequest.getIpAddress());

		WebResponse response = WebResponse.builder().code(request.getDestination()).build();
		response.setEntities(getMessages(request.getDestination()));
		
		realtimeService.sendMessageChatToClient(response, request.getDestination());
		realtimeService.sendChatMessageToAdmin(response);
		WebResponse responseAPI = new WebResponse();
		BeanUtils.copyProperties(response, responseAPI);
		responseAPI.setCode("00");
		return responseAPI;
	}

	private void putMessage(String requestId, Message message) {
		entityRepository.save(message);
	}

}
