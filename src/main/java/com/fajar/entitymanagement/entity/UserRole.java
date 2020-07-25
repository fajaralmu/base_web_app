package com.fajar.entitymanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table(name = "user_role")
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class UserRole extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -725487831020131248L;
	@Column(unique = true)
	@FormField
	private String name;
	@Column
	@FormField
	private String access;
	@Column(unique = true)
	@FormField
	private String code;

}
