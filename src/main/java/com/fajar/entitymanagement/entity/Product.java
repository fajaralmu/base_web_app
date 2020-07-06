package com.fajar.entitymanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.dto.FieldType;
import com.fajar.entitymanagement.dto.FormInputColumn;
import com.fajar.entitymanagement.service.entity.ProductUpdateService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Dto(formInputColumn = FormInputColumn.ONE_COLUMN, updateService =  ProductUpdateService.class)
@Entity
@Table(name = "product")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity {

	/**
	* 
	*/
	private static final long serialVersionUID = 3494963248002164943L;
	@Column(unique = true)
	@FormField(lableName = "Product Code")
	private String code;
	@Column(unique = true)
	@FormField
	private String name;
	@Column
	@FormField
	private String description;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_CURRENCY)
	@Type(type = "org.hibernate.type.LongType")
	private long price;
	@Column
	@FormField
	private String type;
	
	@Column(name = "image_url", unique = true)
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, multiple = true, defaultValue = "Default.BMP")
	private String imageUrl; // type:BLOB
	
	@JoinColumn(name = "unit_id", nullable = false)
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private ProductUnit unit;
	
	@JoinColumn(name = "category_id", nullable = false)
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private ProductCategory category;
 

}
