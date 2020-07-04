package com.fajar.entitymanagement.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fajar.entitymanagement.dto.FormInputColumn;
import com.fajar.entitymanagement.service.entity.BaseEntityUpdateService;
import com.fajar.entitymanagement.service.entity.CommonUpdateService;

@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.TYPE)  
public @interface Dto {

	FormInputColumn formInputColumn() default FormInputColumn.TWO_COLUMN;
	boolean ignoreBaseField() default true;
	boolean editable() default true;
	String value() default "";
	boolean quistionare() default false;
	Class<? extends BaseEntityUpdateService> updateService() default CommonUpdateService.class;
	 
}
