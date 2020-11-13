package com.fajar.entitymanagement.entity.setting;

import java.io.Serializable;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.service.entity.BaseEntityUpdateService;
import com.fajar.entitymanagement.service.entity.EntityUpdateInterceptor;
import com.fajar.entitymanagement.util.EntityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityManagementConfig implements Serializable {
	private static final long serialVersionUID = -3980738115751592524L;
	private long id;
	@JsonIgnore
	private Class<? extends BaseEntity> entityClass;
	@JsonIgnore
	private BaseEntityUpdateService entityUpdateService;
	@JsonIgnore
	private String fieldName;
	private boolean disabled;

	public EntityManagementConfig(String fieldName, Class<? extends BaseEntity> entityClass,
			BaseEntityUpdateService service, EntityUpdateInterceptor updateInterceptor) {
		this.entityClass = entityClass;
		this.entityUpdateService = service;
		if (null == fieldName) {
			fieldName = "entity";
		}
		this.fieldName = fieldName;
//		this.updateInterceptor = updateInterceptor;
		init();
	}

	private void init() {
		Dto dtoAnnotation = EntityUtil.getClassAnnotation(entityClass, Dto.class);

		disabled = dtoAnnotation.editable() == false;
	}

	public String getLabel() {
		Dto dtoAnnotation = EntityUtil.getClassAnnotation(entityClass, Dto.class);

		String label = dtoAnnotation.value().equals("") ? entityClass.getSimpleName() : dtoAnnotation.value();
		return label;
	}

	public String getEntityName() {
		return entityClass.getSimpleName().toLowerCase();
	}

}
