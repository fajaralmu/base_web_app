package com.fajar.entitymanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.dto.FieldType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table(name = "user")
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3896877759244837620L;
	@Column(unique = true)
	@FormField
	private String username;
	@Column(name = "display_name")
	@FormField
	private String displayName;
	@Column
	@FormField
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

}
