package com.fajar.entitymanagement.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Dto
@Entity
@Table (name="registered_request")
@Data
@Builder	
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredRequest extends BaseEntity {/**
	 * 
	 */
	private static final long serialVersionUID = -2584171097698972770L; 
	@Column(name="request_id")
	@FormField(required = true)
	private String requestId;
	@Column(name="value")
	@FormField(required = true)
	private String value;
	@Transient
	@JsonFormat(pattern = "dd-MM-yyyy' 'hh:mm:ss")
	private Date created;
	@Column(name="referrer")
	private String referrer;
	@Column(name="user_agent")
	private String userAgent;
	@Column(name="ip_address")
	private String ipAddress;
	@Transient
	private  List<? extends BaseEntity> messages;
	@Transient
	private boolean active;
	
}
