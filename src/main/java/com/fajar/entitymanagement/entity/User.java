package com.fajar.entitymanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.dto.FieldType;
import com.fajar.entitymanagement.service.entity.UserUpdateService;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(updateService = UserUpdateService.class)
@Entity
@Table(name = "user")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3896877759244837620L;
	@Column(unique = true, nullable = false)
	@FormField(emptyAble = false)
	private String username;
	@Column(name = "display_name", nullable = false)
	@FormField(emptyAble = false)
	private String displayName;
	@Column(nullable = false)
	@FormField(emptyAble = false)
//	@JsonIgnore
	private String password;
	@JoinColumn(name = "role_id")
	@ManyToOne
	@FormField(type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	private UserRole role;

	@javax.persistence.Transient
	@JsonIgnore
	private String loginKey;

	@Transient
	@JsonIgnore
	private String requestId;
	
	@PrePersist
	public void userPrePersist() {
		if(StringUtils.isBlank(password) || StringUtils.isBlank(displayName) || StringUtils.isBlank(username)) {
			throw new RuntimeException("Field not complete!");
		}
		 
	}

}
