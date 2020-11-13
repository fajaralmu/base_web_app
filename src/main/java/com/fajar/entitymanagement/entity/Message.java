package com.fajar.entitymanagement.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto 
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="message")
public class Message extends BaseEntity{
	 
    /**
	 * 
	 */
	private static final long serialVersionUID = -8717505831657905055L;
	
	public Message(String sender, String content, Date date2, String reqId2) { 
    	this.sender = sender;
    	this.text = content;
    	this.date = date2; 
    	this.requestId =reqId2;
    	 
	}

    @Column
    @FormField
    private int admin;
    @Column
    @FormField
	private String sender;
    @Column(name="user_agent")
    @FormField
    private String userAgent;
    @Column(name="ip_address")
    @FormField
    private String ipAddress;
    @Column
    @FormField
    private String text; 
    @JsonFormat(pattern = "DD-MM-yyyy' 'hh:mm:ss")
    @Column
    @FormField
    private Date date;
    @Column
    @FormField
    private String alias;
    @Column(name="request_id")
    @FormField
    private String requestId;
    
}
