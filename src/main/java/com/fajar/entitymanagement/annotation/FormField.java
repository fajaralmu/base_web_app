package com.fajar.entitymanagement.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fajar.entitymanagement.dto.FieldType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormField {
 
	
	public FieldType type() default FieldType.FIELD_TYPE_TEXT;
	
	public boolean showDetail() default false;
	
	//multiple image
	public boolean multiple() default false;

	public boolean required() default true;

	public String lableName() default "";

	public String optionItemName() default ""; 

	public String defaultValue() default "";

	public String[] availableValues() default {};
	public String[] detailFields() default {}; 
	public String[] multiply() default {};
	public boolean emptyAble() default true;

}
