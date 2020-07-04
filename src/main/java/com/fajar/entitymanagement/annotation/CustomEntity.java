package com.fajar.entitymanagement.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Fajar AM
 * 03/10/2019 02.18 PM
 * <p>
 * this annotation is used to map JPA result list into object
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value =
        ElementType.TYPE)
public @interface CustomEntity {

    public String[] propOrder();

}
