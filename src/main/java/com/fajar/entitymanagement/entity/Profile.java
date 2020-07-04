package com.fajar.entitymanagement.entity;

import static com.fajar.entitymanagement.dto.FieldType.FIELD_TYPE_TEXTAREA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.dto.FieldType;
import com.fajar.entitymanagement.service.entity.ShopProfileUpdateService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(ignoreBaseField = false, updateService = ShopProfileUpdateService.class)
@Entity
@Table(name = "app_profile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Profile extends BaseEntity {

	/**
	* 
	*/
	private static final long serialVersionUID = 4095664637854922384L;
	@Column 
	@FormField
	private String name;
	@Column(name = "app_code", unique = true)
	@FormField(type = FieldType.FIELD_TYPE_HIDDEN)
	private String appCode;
	@Column(name = "short_description")
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String shortDescription;
	@Column
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String about;
	@Column(name = "welcoming_message")
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String welcomingMessage;
	@Column
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String address;

	@Column
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String contact;
	@Column
	@FormField
	private String website;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "icon_url")
	private String iconUrl;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultBackground.BMP")
	@Column(name = "background_url")
	private String backgroundUrl;

}
