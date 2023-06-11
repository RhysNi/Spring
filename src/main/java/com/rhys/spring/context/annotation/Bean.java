package com.rhys.spring.context.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/4/19 3:10 AM
 */
@Target({METHOD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
public @interface Bean {
    @AliasFor("value")
    String value() default "";
    @AliasFor("name")
    String name() default "";
    String initMethod() default "";
    String destroyMethod() default "";
}