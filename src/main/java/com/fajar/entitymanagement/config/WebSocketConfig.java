package com.fajar.entitymanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	 

	public WebSocketConfig() {
		log.info("====================Web Socket Config=====================");
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		log.info("configureMessageBroker");
		config.enableSimpleBroker("/wsResp");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		log.info(". . . . . . . . . register Stomp Endpoints . . . . . . . . . . ");
		registry.addEndpoint("/realtime-app").setAllowedOrigins("*").withSockJS();
	}
}