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
@Table (name="product_category")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder	
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategory extends BaseEntity {/**
	 * 
	 */
	private static final long serialVersionUID = -1168912843978053906L; 
	@FormField 
	@Column(unique = true)
	private String name;
	@FormField ( type= FieldType.FIELD_TYPE_TEXTAREA) 
	@Column
	private String description;
}
