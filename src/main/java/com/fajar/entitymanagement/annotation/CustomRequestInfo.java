package com.fajar.entitymanagement.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CustomRequestInfo {
	
	public String[] stylePaths() default {};
	public String[] scriptPaths() default {};
	public String pageUrl() default "";
	public String title() default "";
	public boolean withRealtimeProgress() default false;
	

}
