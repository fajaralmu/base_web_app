package com.fajar.entitymanagement.entity;

import java.beans.Transient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import org.hibernate.annotations.Type;

import com.fajar.entitymanagement.annotation.BaseField;
import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.dto.FieldType;
import com.fajar.entitymanagement.service.entity.EntityUpdateInterceptor;
import com.fajar.entitymanagement.util.CollectionUtil;
import com.fajar.entitymanagement.util.EntityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.extern.slf4j.Slf4j;

@Dto
@Slf4j
@MappedSuperclass
public class BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5713292970611528372L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@FormField
	@Type(type = "org.hibernate.type.LongType")
	@Column
	@BaseField
	private Long id;

	@Column(name = "created_date")
	@JsonIgnore
//	@FormField
	private Date createdDate;
	@Column(name = "modified_date")
	@JsonIgnore
	private Date modifiedDate;
	@Column(name = "deleted")
	@JsonIgnore
	private boolean deleted;

	@Column(name = "general_color")
	@FormField(lableName = "Background Color", type = FieldType.FIELD_TYPE_COLOR, defaultValue = "#ffffff")
	private String color;
	@BaseField
	@Column(name = "font_color")
	@FormField(type = FieldType.FIELD_TYPE_COLOR, defaultValue = "#000000")
	private String fontColor;

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@PrePersist
	private void prePersist() throws Exception {
		try {
			validateNonEmptyAbleFields();
		} catch (Exception e) {
			log.error("validateNonEmptyAbleFields ERROR");
			e.printStackTrace();
			throw e;
		}

		if (this.createdDate == null) {
			this.createdDate = new Date();
		}
		this.modifiedDate = new Date();
		if (this.color == null) {
			color = "#ffffff";
		}
		if (this.fontColor == null) {
			fontColor = "#ffffff";
		}
	}

	private void validateNonEmptyAbleFields() throws Exception {
		List<Field> notEmptyAbleFields = EntityUtil.getNotEmptyAbleField(this.getClass());
		
		Object[] array = notEmptyAbleFields.toArray();
		Object[] fieldNames = CollectionUtil.objectElementsToArray("name", array);
		String[] fieldNamesStr = CollectionUtil.toArrayOfString(fieldNames);
		String fieldNamesStrPlain = String.join(",", fieldNamesStr);
		
		for (int i = 0; i < notEmptyAbleFields.size(); i++) {

			Field field = notEmptyAbleFields.get(i);
			Object value = field.get(this);
			if (value == null || (value.toString().trim().isEmpty())) {
				log.error("FIELD(s): {} MUST NOT BE EMPTY", fieldNamesStrPlain);
				throw new RuntimeException("Field is Empty!");
			}

		}
	}

	@JsonIgnore
	@Transient
	public EntityUpdateInterceptor getUpdateInterceptor() {
		return new EntityUpdateInterceptor<BaseEntity>() {
			private static final long serialVersionUID = 2878932467536346251L;

			@Override
			public BaseEntity preUpdate(BaseEntity baseEntity) {
				return baseEntity;
			}
		};
	}
}
