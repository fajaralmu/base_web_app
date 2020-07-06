package com.fajar.entitymanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.dto.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table(name = "customer")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends BaseEntity {

	/**
	* 
	*/
	private static final long serialVersionUID = -8365369638070739369L;
	@Column(unique = true)
	@FormField
	private String username;
	@Column(name = "display_name")
	@FormField
	private String name;
	@Column
	@FormField(required = false, type = FieldType.FIELD_TYPE_TEXTAREA)
	private String address;
	@Column
	@FormField
	private String phone;
	@Column
	private String type;

	@Column
	@FormField
	private String email;

}
